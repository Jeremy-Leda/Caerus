package view.panel.model;

import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import model.ConfigurationModel;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import model.analyze.constants.ActionEditTableEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.beans.EditTableElement;
import view.beans.EditTableElementBuilder;
import view.beans.SpecificRow;
import view.interfaces.ISpecificEditTableModel;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * Classe pour la gestion du modèle des données d'une table éditable
 *
 */
public class SpecificEditTableModel implements ISpecificEditTableModel {

    private Collection<String> originList;
    private LinkedList<SpecificRow> specificRowList;
    private String filterValue;
    private final LinkedList<SpecificRow> filteredList = new LinkedList<>();
    private final Consumer<?> refreshConsumer;
    private final Consumer<?> saveInMemory;
    private Optional<EditTableElement> editTableElementOptional = Optional.empty();
    private Logger logger = LoggerFactory.getLogger(SpecificEditTableModel.class);
    private Stream<Tuple2<SpecificRow, Integer>> cacheStream;

    public SpecificEditTableModel(Consumer<?> refreshConsumer, Consumer<?> saveInMemory) {
        this.refreshConsumer = refreshConsumer;
        this.saveInMemory = saveInMemory;
    }

    @Override
    public LinkedList<SpecificRow> getRows() {
        return this.filteredList;
    }

    @Override
    public SpecificRow getSpecificRow(Integer index) {
        return cacheStream.find(sr -> sr._2().equals(index)).get()._1();
    }

    @Override
    public Set<String> getModelValues() {
        return originList.stream().collect(Collectors.toSet());
    }

    @Override
    public void update(Integer id, String newValue) {
        if (Optional.ofNullable(id).isPresent()) {
            Optional<SpecificRow> optionalSpecificRow = this.specificRowList.stream().filter(specificRow -> id.equals(Integer.valueOf(specificRow.getSpecificList().get(0)))).findFirst();
            if (optionalSpecificRow.isPresent()) {
                String oldValue = optionalSpecificRow.get().getSpecificList().get(1);
                optionalSpecificRow.get().getSpecificList().set(1, newValue);
                this.editTableElementOptional = Optional.of(createEditTableElementForUpdate(oldValue, newValue));
                saveMemoryInBackground();
            }
            optionalSpecificRow.ifPresent(specificRow -> specificRow.getSpecificList().set(1, newValue));
            this.originList = this.specificRowList.parallelStream().map(this::mapSpecificRowToString).collect(Collectors.toSet());
            //filter(this.filterValue);
        }
    }

    @Override
    public void remove(Integer id) {
        if (Optional.ofNullable(id).isPresent()) {
            Optional<SpecificRow> specificRowOptional = this.specificRowList.stream().filter(specificRow -> id.equals(Integer.valueOf(specificRow.getSpecificList().get(0)))).findFirst();
            if (specificRowOptional.isPresent()) {
                this.editTableElementOptional = Optional.of(createEditTableElementForRemove(specificRowOptional.get().getSpecificList().get(1)));
                this.specificRowList.remove(specificRowOptional.get());
                this.filteredList.remove(specificRowOptional.get());
                this.originList = this.specificRowList.parallelStream().map(this::mapSpecificRowToString).collect(Collectors.toSet());
                //createSpecificRowList(this.originList);
                saveMemoryInBackground();
            }
        }
    }

    @Override
    public void add(String value) {
        SortedSet<String> stringSortedSet = this.specificRowList.stream().map(this::mapSpecificRowToString).collect(Collectors.toCollection(TreeSet::new));
        stringSortedSet.add(value);
        this.originList = stringSortedSet;
        this.specificRowList = convertSortedSetToSpecificRowList(stringSortedSet);
        filter(this.filterValue);
        this.editTableElementOptional = Optional.of(createEditTableElementForAdd(value));

//        Option<Tuple2<String, Integer>> addedElementOptional = Stream.ofAll(stringSortedSet).zipWithIndex(). find(tuple -> tuple._1().equals(value));
//        this.editTableElementOptional = Optional.of(createEditTableElementForAdd(value));
//        if (addedElementOptional.isDefined()) {
//            Tuple2<String, Integer> addedRow = addedElementOptional.get();
//            SpecificRow specificRowToAdd = mapStringToSpecificRow(addedRow._2() + 1, addedRow._1());
//            this.filteredList.add(addedRow._2(), specificRowToAdd);
//            logger.info("CALL REFRESH");
//            refreshConsumer.accept(null);
//            logger.info("FIN CALL REFRESH");
//            this.specificRowList.add(addedRow._2(), specificRowToAdd);
//        } else {
//            createSpecificRowList(this.originList);
//        }
//        this.originList = stringSortedSet;
        saveMemoryInBackground();
    }

    @Override
    public void createSpecificRowList(Collection<String> collection) {
        this.originList = collection;
        this.specificRowList = convertValueCollectionToSpecificRowList(collection);
        filter(this.filterValue);
    }

    @Override
    public void filter(String value) {
        this.filterValue = value;
        filterInBackground();
        refreshConsumer.accept(null);
    }

    /**
     * Permet de mapper les valeurs avec leur numéro de ligne dans une {@link SpecificRow}
     * @param index index pour le mapping
     * @param value valeur à mapper
     * @return la ligne spécifique
     */
    private SpecificRow mapStringToSpecificRow(int index, String value) {
        SpecificRow specificRow = new SpecificRow();
        specificRow.addSpecific(String.valueOf(index));
        specificRow.addSpecific(value);
        return specificRow;
    }

    /**
     * Permet de mapper une {@link SpecificRow} en valeur
     * @param specificRow {@link SpecificRow} à mapper
     * @return la valeur
     */
    private String mapSpecificRowToString(SpecificRow specificRow) {
        return specificRow.getSpecificList().get(1);
    }

    /**
     * Permet de convertir la collection de valeur en liste de {@link SpecificRow}
     * @param collection collection de valeurs
     * @return la liste de {@link SpecificRow}
     */
    private LinkedList<SpecificRow> convertValueCollectionToSpecificRowList(Collection<String> collection) {
        SortedSet<String> sortedRowSet = new TreeSet<>(Comparator.comparing(StringUtils::stripAccents));
        sortedRowSet.addAll(collection);
        return convertSortedSetToSpecificRowList(sortedRowSet);


        //List<String> orderRow = collection.stream().sorted(Comparator.comparing(StringUtils::stripAccents)).collect(Collectors.toList());

//
//        return IntStream.range(0, orderRow.size())
//                .boxed()
//                .map(i -> mapStringToSpecificRow(i+1, orderRow.get(i)))
//                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Permet de convertir un {@link SortedSet} de valeur en liste de {@link SpecificRow}
     * @param sortedRowSet les valeurs à convertir
     * @return la liste de {@link SpecificRow}
     */
    private LinkedList<SpecificRow> convertSortedSetToSpecificRowList(SortedSet<String> sortedRowSet) {
        return Stream.ofAll(sortedRowSet)
                .zipWithIndex()
                .map(tuple -> mapStringToSpecificRow(tuple._2()+1, tuple._1()))
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Permet de filtrer les valeurs en arriére plan
     */
    private void filterInBackground() {
        boolean haveFilter = StringUtils.isNotBlank(this.filterValue);
        LinkedList<SpecificRow> resultList = new LinkedList<>();
        if (haveFilter) {
            List<List<SpecificRow>> partition = ListUtils.partition(this.specificRowList, 5000);

            ExecutorService executorService = Executors.newFixedThreadPool(partition.size());

            Queue<Future<List<SpecificRow>>> taskQueue = new LinkedList<>();
            for (int i = 0; i < partition.size(); i++) {
                taskQueue.add(executorService.submit(getCallable(partition.get(i), this.filterValue)));
            }

            while (!taskQueue.isEmpty()) {
                Future<List<SpecificRow>> loadTask = taskQueue.remove();
                Try.run(() -> resultList.addAll(loadTask.get()));
            }
            executorService.shutdown();
        } else {
            resultList.addAll(this.specificRowList);
        }
        this.filteredList.clear();
        this.filteredList.addAll(resultList);
        refreshStream();
    }

    /**
     * Permet de filtrer une liste de ligne par rapport au filtre (contains)
     * @param rows liste des lignes à filtrer
     * @param filter filtre à appliquer
     * @return la liste des lignes filtrés
     */
    private Callable<List<SpecificRow>> getCallable(List<SpecificRow> rows, String filter) {
        return () -> rows.stream().filter(specificRow -> specificRow.getSpecificList().get(1).toLowerCase(Locale.ROOT).contains(filter)).collect(Collectors.toCollection(LinkedList::new));
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

    private void saveMemoryInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            refreshStream();
            this.saveInMemory.accept(null);
            this.editTableElementOptional = Optional.empty();
        });
        executorService.shutdown();
    }

    private void refreshStream() {
        this.cacheStream = Stream.ofAll(this.filteredList).zipWithIndex();
    }
}
