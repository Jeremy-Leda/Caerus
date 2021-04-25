package view.panel.model;

import io.vavr.control.Try;
import model.analyze.constants.ActionEditTableEnum;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import view.beans.EditTableElement;
import view.beans.EditTableElementBuilder;
import view.interfaces.ISpecificEditTableModel;
import view.services.ExecutionService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * Classe pour la gestion du modèle des données d'une table éditable
 *
 */
public class SpecificEditTableModel implements ISpecificEditTableModel<String, String> {

    // La liste d'origine
    private Collection<String> originList;

    // La liste trié et sans doublon
    private final SortedSet<String> sortedSet = new TreeSet<>(Comparator.comparing(StringUtils::stripAccents));

    // La liste pour afficher
    private final List<String> filteredList = new LinkedList<>();

    //private LinkedList<SpecificRow> specificRowList;
    private String filterValue;

    private final Consumer<?> refreshConsumer;
    private final Consumer<?> saveInMemory;
    private final Consumer<Integer> consumerForAutoSize;
    private Optional<EditTableElement> editTableElementOptional = Optional.empty();
    private BiConsumer<Integer, Integer> fireTableInserted;
    private BiConsumer<Integer, Integer> fireTableUpdated;
    private BiConsumer<Integer, Integer> fireTableDeleted;
    private final ExecutionService executionService = new ExecutionService();

    public SpecificEditTableModel(Consumer<?> refreshConsumer, Consumer<?> saveInMemory, Consumer<Integer> consumerForAutoSize) {
        this.refreshConsumer = refreshConsumer;
        this.saveInMemory = saveInMemory;
        this.consumerForAutoSize = consumerForAutoSize;
    }

    @Override
    public Collection<String> getRows() {
        return this.filteredList;
    }

    @Override
    public String getRow(Integer rowIndex) {
        return this.filteredList.get(rowIndex);
    }

    @Override
    public Set<String> getModelValues() {
        return originList.stream().collect(Collectors.toSet());
    }

    @Override
    public void update(String oldValue, String newValue) {
        executionService.executeOnServer(() -> {
            checkedValue(newValue);
            this.originList.remove(oldValue);
            this.originList.add(newValue);
            int indexValue = this.filteredList.indexOf(oldValue);
            this.filteredList.remove(oldValue);
            this.filteredList.add(indexValue, newValue);
            this.sortedSet.remove(oldValue);
            this.sortedSet.add(newValue);
            this.fireTableUpdated.accept(indexValue, indexValue);
            autoSizeRow(indexValue);
            this.editTableElementOptional = Optional.of(createEditTableElementForUpdate(oldValue, newValue));
            saveMemoryInBackground();
        });
    }

    @Override
    public void remove(String value) {
        this.originList.remove(value);
        int indexValue = this.filteredList.indexOf(value);
        this.filteredList.remove(value);
        this.sortedSet.remove(value);
        this.fireTableDeleted.accept(indexValue, indexValue);
        this.editTableElementOptional = Optional.of(createEditTableElementForRemove(value));
        saveMemoryInBackground();
    }

    @Override
    public void add(String value) {
        executionService.executeOnServer(() -> {
            checkedValue(value);
            this.originList.add(value);
            this.sortedSet.add(value);
            Integer index = new LinkedList<>(sortedSet).indexOf(value);
            this.filteredList.add(index, value);
            this.fireTableInserted.accept(index, index);
            autoSizeRow(index);
            this.editTableElementOptional = Optional.of(createEditTableElementForAdd(value));
            saveMemoryInBackground();
        });
    }

    @Override
    public void createSpecificRowList(Collection<String> collection) {
        this.originList = new HashSet<>(collection);
        filter(this.filterValue);
    }

    @Override
    public void filter(String value) {
        this.filterValue = value;
        filterInBackground();
        refreshConsumer.accept(null);
    }

    /**
     * Permet de filtrer les valeurs en arriére plan
     */
    private synchronized void filterInBackground() {
        boolean haveFilter = StringUtils.isNotBlank(this.filterValue);
        this.filteredList.clear();
        this.sortedSet.clear();

        if (haveFilter) {
            List<List<String>> partition = ListUtils.partition(new ArrayList<>(this.originList), 5000);

            ExecutorService executorService = Executors.newFixedThreadPool(partition.size());

            Queue<Future<List<String>>> taskQueue = new LinkedList<>();
            for (int i = 0; i < partition.size(); i++) {
                taskQueue.add(executorService.submit(getCallable(partition.get(i), this.filterValue)));
            }

            while (!taskQueue.isEmpty()) {
                Future<List<String>> loadTask = taskQueue.remove();
                Try.run(() -> this.sortedSet.addAll(loadTask.get()));
            }

            executorService.shutdown();
        } else {
            this.sortedSet.addAll(this.originList);
        }
        this.filteredList.addAll(this.sortedSet);
    }

    /**
     * Permet de filtrer une liste de ligne par rapport au filtre (contains)
     * @param rows liste des lignes à filtrer
     * @param filter filtre à appliquer
     * @return la liste des lignes filtrés
     */
    private Callable<List<String>> getCallable(List<String> rows, String filter) {
        return () -> rows.stream().filter(row -> row.toLowerCase(Locale.ROOT).contains(filter)).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Permet de se procurer l'élément d'édition pour l'ajout dans la liste
     * @param value valeur à ajouter
     * @return L'élément d'édition pour l'ajout dans la liste
     */
    private EditTableElement createEditTableElementForAdd(String value) {
        return new EditTableElementBuilder()
                .actionEditTableEnum(ActionEditTableEnum.ADD)
                .value(value)
                .build();
    }

    /**
     * Permet de se procurer l'élément d'édition pour la suppression dans la liste
     * @param value valeur à supprimer
     * @return L'élément d'édition pour la suppression dans la liste
     */
    private EditTableElement createEditTableElementForRemove(String value) {
        return new EditTableElementBuilder()
                .actionEditTableEnum(ActionEditTableEnum.REMOVE)
                .value(value)
                .build();
    }

    /**
     * Permet de se procurer l'élément d'édition pour la mise à jour dans la liste
     * @param oldValue Ancienne valeur
     * @param value Nouvelle valeur
     * @return L'élément d'édition pour la mise à jour dans la liste
     */
    private EditTableElement createEditTableElementForUpdate(String oldValue, String value) {
        return new EditTableElementBuilder()
                .actionEditTableEnum(ActionEditTableEnum.UPDATE)
                .oldValue(oldValue)
                .value(value)
                .build();
    }

    /**
     * Permet de se procurer le {@link EditTableElement} qui a été construit pour la mise à jour de la table
     * @return le {@link EditTableElement}
     */
    @Override
    public Optional<EditTableElement> getEditTableElement() {
        return editTableElementOptional;
    }

    @Override
    public void setEditTableElement(Optional<EditTableElement> editTableElement) {
        this.editTableElementOptional = editTableElement;
    }

    @Override
    public void setConsumerForFireTableInserted(BiConsumer<Integer, Integer> consumer) {
        this.fireTableInserted = consumer;
    }

    @Override
    public void setConsumerForFireTableUpdated(BiConsumer<Integer, Integer> consumer) {
        this.fireTableUpdated = consumer;
    }

    @Override
    public void setConsumerForFireTableDeleted(BiConsumer<Integer, Integer> consumer) {
        this.fireTableDeleted = consumer;
    }

    /**
     * Permet de sauvegarder en arrière plan
     */
    private void saveMemoryInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            this.saveInMemory.accept(null);
            this.editTableElementOptional = Optional.empty();
        });
        executorService.shutdown();
    }

    /**
     * Permet de mettre à jour la taille par rapport à une ligne
     * @param index index de la ligne à utiliser
     */
    private void autoSizeRow(Integer index) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            consumerForAutoSize.accept(index);
        });
        executorService.shutdown();
    }

    /**
     * Permet de vérifier que la valeur n'est pas déjà présente dans la liste.
     * Si c'est le cas une exception est levé
     * @param value valeur à vérifier
     */
    private void checkedValue(String value) {
        if (this.originList.contains(value)) {
            InformationException informationException = new InformationExceptionBuilder()
                    .errorCode(ErrorCode.VALUE_EXIST)
                    .parameters(Set.of(value))
                    .objectInError(value)
                    .build();
            throw new ServerException().addInformationException(informationException);
        }
    }

}
