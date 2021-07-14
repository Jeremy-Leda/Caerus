package view.panel;

import io.vavr.Function2;
import model.analyze.constants.ActionEditTableEnum;
import org.apache.commons.lang3.StringUtils;
import view.beans.EditTableElement;
import view.beans.EditTableElementBuilder;
import view.interfaces.*;
import view.panel.model.EditTableModel;
import view.panel.model.SpecificEditTableModel;
import view.utils.ColumnsAutoSize;
import view.utils.RowNumberTable;
import view.windows.UserQuestion;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 *
 * Classe pour l'affichage d'une table avec édition et recherche par filtres
 *
 */
public class TableWithFilterAndEditPanel<T> implements ITableWithFilterAndEditPanel<T> {
    private final JPanel panel;
    private final EditTableModel editTableModel;
    private final JTable table;
    private final JScrollPane scrollPane;
    private final ITableFilterPanel tableFilterPanel;
    private Optional<Consumer<T>> loadSelectedRowConsumerOptional = Optional.empty();
    private Consumer<Void> consumerForRefrehStateOfAllAddButton;

    private IActionPanel actionPanel;
    private ISpecificEditTableModel<T, ITableFilterObject> specificEditTableModel;

    public TableWithFilterAndEditPanel(String titlePanel, String header, Consumer<?> saveInMemory,
                                       Comparator comparator, Comparator viewComparator, Function<ITableFilterObject, Boolean> checkFilterIsPresentFunction, Function2<T, ITableFilterObject, Boolean> applyFilterFunction,
                                       ITableFilterPanel tableFilterPanel) {
        this(titlePanel, header, saveInMemory, comparator, viewComparator, checkFilterIsPresentFunction, applyFilterFunction, tableFilterPanel, false, Optional.empty());
    }

    public TableWithFilterAndEditPanel(String titlePanel, String header, Consumer<?> saveInMemory,
                                       Comparator comparator, Comparator viewComparator, Function<ITableFilterObject, Boolean> checkFilterIsPresentFunction, Function2<T, ITableFilterObject, Boolean> applyFilterFunction,
                                       ITableFilterPanel tableFilterPanel,
                                       Boolean isReadOnly,
                                       Optional<Map<Integer, Class<?>>> optionalColumnClassMap) {
        this.panel = new JPanel();
        if (StringUtils.isNotBlank(titlePanel)) {
            this.panel.setBorder(
                    BorderFactory.createTitledBorder(titlePanel));
        }
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        this.specificEditTableModel = new SpecificEditTableModel(e -> refreshDataAndColumnSize(), saveInMemory, getConsumerForAutoSizeColumn(), comparator, viewComparator, checkFilterIsPresentFunction, applyFilterFunction);
        //this.tableFilterPanel = new TextBoxPanel(1, false);
        this.tableFilterPanel = tableFilterPanel;
        this.tableFilterPanel.addConsumerOnChange(getConsumerFilter());
        this.editTableModel = new EditTableModel(header, this.specificEditTableModel, isReadOnly, optionalColumnClassMap);
        this.table = new JTable(this.editTableModel);

        this.scrollPane = new JScrollPane(this.table);
        JTable rowTable = new RowNumberTable(this.table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rowTable.getTableHeader());
//        this.tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_LABEL)));
//        this.tableFilterPanel.addConsumerOnChange(0, getConsumerFilter());
        this.panel.add(this.tableFilterPanel.getJPanel());
        this.panel.add(this.scrollPane);
        table.getSelectionModel().setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        if (!isReadOnly) {
            configureTable();
            createAndAddButtonEditAndRemove();
        }
    }

    /**
     * Permet de configurer la table
      */
    private void configureTable() {
        InputMap im = table.getInputMap(JTable.WHEN_FOCUSED);
        ActionMap am = table.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
        am.put("DeleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable tableSource = (JTable)e.getSource();
                removeSelectedRow(tableSource);
            }
        });
        this.table.getSelectionModel().addListSelectionListener(getDefaultListSelectionListener());
    }

    /**
     * Permet de créer et d'ajouter les boutons pour l'édition de la liste
     */
    private void createAndAddButtonEditAndRemove() {
        this.actionPanel = new ActionPanel(2);
        this.actionPanel.setEnabled(0, Boolean.FALSE);
        this.actionPanel.setEnabled(1, Boolean.FALSE);
        this.actionPanel.addAction(1, e -> removeSelectedRow(this.table));
        this.panel.add(this.actionPanel.getJPanel());
    }

    @Override
    public JComponent getJPanel() {
        return this.panel;
    }

    @Override
    public void refresh() {
        this.editTableModel.fireTableStructureChanged();
        refreshDataAndColumnSize();
    }

    @Override
    public void fillTable(final Collection<T> collection) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            this.specificEditTableModel.createSpecificRowList(collection);
        });
        executorService.shutdown();
    }

    @Override
    public void setInterfaceForAddButton(String informationMessage, String label) {
        this.actionPanel.addAction(0, e-> addRow(informationMessage, label));
    }

    @Override
    public void setLabelForAddAndRemoveButton(String addLabel, String removeLabel) {
        this.actionPanel.setStaticLabel(StringUtils.EMPTY,
                Map.of(0, addLabel, 1, removeLabel));
    }

    @Override
    public Set<T> getValues() {
        return this.specificEditTableModel.getModelValues();
    }

    @Override
    public void setConsumerForRowChanged(Consumer<T> consumer) {
        this.loadSelectedRowConsumerOptional = Optional.of(consumer);
    }

    @Override
    public void setConsumerForRefreshStateOfAllAddButton(Consumer<Void> consumer) {
        this.consumerForRefrehStateOfAllAddButton = consumer;
    }

    @Override
    public Optional<EditTableElement> getEditTableElement() {
        return this.specificEditTableModel.getEditTableElement();
    }

    @Override
    public T getSelectedValue() {
        if (haveSelectedValue()) {
            return (T) this.table.getModel().getValueAt(this.table.getSelectedRow(), 0);
        }
        return null;
    }

    /**
     * Permet de savoir si une valeur est sélectionné
     * @return Vrai si la valeur est sélectionné
     */
    @Override
    public Boolean haveSelectedValue() {
        return this.table.getSelectedRow() > -1;
    }

    @Override
    public void setEnabledAddButton(Boolean enabledAddButton) {
        this.actionPanel.setEnabled(0, enabledAddButton);
    }

    /**
     * Permet de se procurer le consumer pour le filtre
     * @return le consumer pour le filtre
     */
    private Consumer<?> getConsumerFilter() {
        return e -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                this.specificEditTableModel.filter(Optional.ofNullable(this.tableFilterPanel.getFilter()));
            });
            executorService.shutdown();
        };
    }

    /**
     * Permet de supprimer une ligne de la table
     * @param tableSource Table dont on doit supprimer la ligne sélectionnée
     */
    private void removeSelectedRow(JTable tableSource) {
        if (tableSource.getSelectedRow() > -1) {
            Object value = tableSource.getModel().getValueAt(tableSource.getSelectedRow(), 0);
            tableSource.getSelectionModel().clearSelection();
            specificEditTableModel.remove((T) value);
        }
    }

    /**
     * Permet d'ajouter une ligne
     * @param informationMessage Information pour l'utilisateur
     * @param label Label devant la zone de saisie
     */
    private void addRow(String informationMessage, String label) {
        UserQuestion userQuestion = new UserQuestion(informationMessage, label);
        if (StringUtils.isNotBlank(userQuestion.getAnswer())) {
            specificEditTableModel.add((T) userQuestion.getAnswer());
            //this.editTableModel.fireTableDataChanged();
            //ColumnsAutoSize.sizeColumnsToFit(this.table);
        }
    }

    /**
     * Permet de charger le {@link ListSelectionListener} par défaut
     * @return le {@link ListSelectionListener} par défaut
     */
    private ListSelectionListener getDefaultListSelectionListener() {
        return e -> {
            this.actionPanel.setEnabled(1, Boolean.FALSE);
            if (haveSelectedValue()) {
                Object valeur = table.getModel().getValueAt(table.getSelectedRow(), 1);
                this.specificEditTableModel.setEditTableElement(Optional.of(new EditTableElementBuilder()
                    .actionEditTableEnum(ActionEditTableEnum.UPDATE)
                    .oldValue(valeur)
                    .value(valeur)
                    .build()));
                if (loadSelectedRowConsumerOptional.isPresent()) {
                    loadSelectedRowConsumerOptional.get().accept((T) valeur);
                }
                this.actionPanel.setEnabled(1, Boolean.TRUE);
            }
            if (Optional.ofNullable(this.consumerForRefrehStateOfAllAddButton).isPresent()) {
                this.consumerForRefrehStateOfAllAddButton.accept(null);
            }
        };
    }

    /**
     * Permet de rafraichir les datas et la taille des colonnes
     */
    private void refreshDataAndColumnSize() {
        this.editTableModel.fireTableDataChanged();
        autoSizeColumn();
    }

    /**
     * Permet de mettre à jour la taille des colonnes
     */
    private void autoSizeColumn() {
        ColumnsAutoSize.sizeColumnsToFit(this.table);
    }

    /**
     * Permet de se procurer le consumer pour redimensionner une colonne suite à la modification d'une ligne
     * @return le consumer
     */
    private Consumer<Integer> getConsumerForAutoSizeColumn() {
        return id -> ColumnsAutoSize.sizeColumnsToFitForUpdate(this.table, id, 0);
    }

}
