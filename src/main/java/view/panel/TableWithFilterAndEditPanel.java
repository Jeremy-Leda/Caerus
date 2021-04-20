package view.panel;

import org.apache.commons.lang3.StringUtils;
import model.analyze.constants.ActionEditTableEnum;
import view.beans.EditTableElement;
import view.beans.EditTableElementBuilder;
import view.interfaces.*;
import view.panel.model.EditTableModel;
import view.panel.model.SpecificEditTableModel;
import view.utils.ColumnsAutoSize;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.UserQuestion;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 *
 * Classe pour l'affichage d'une table avec édition et recherche par filtres
 *
 */
public class TableWithFilterAndEditPanel implements ITableWithFilterAndEditPanel {
    private final JPanel panel;
    private final EditTableModel editTableModel;
    private final JTable table;
    private final JScrollPane scrollPane;
    private final ITextBoxPanel textBoxPanel;
    private Optional<Consumer<String>> loadSelectedRowConsumerOptional = Optional.empty();

    private IActionPanel actionPanel;
    private ISpecificEditTableModel specificEditTableModel;

    public TableWithFilterAndEditPanel(String titlePanel, String header, Consumer<?> saveInMemory) {
        this.panel = new JPanel();
        if (StringUtils.isNotBlank(titlePanel)) {
            this.panel.setBorder(
                    BorderFactory.createTitledBorder(titlePanel));
        }
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        this.specificEditTableModel = new SpecificEditTableModel(e -> refresh(), saveInMemory);
        this.textBoxPanel = new TextBoxPanel(1, false);
        this.editTableModel = new EditTableModel(header, this.specificEditTableModel);
        this.table = new JTable(this.editTableModel);
        configureTable();
        this.scrollPane = new JScrollPane(this.table);
        this.textBoxPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_LABEL)));
        this.textBoxPanel.addConsumerOnChange(0, getConsumerFilter());
        this.panel.add(this.textBoxPanel.getJPanel());
        this.panel.add(this.scrollPane);
        createAndAddButtonEditAndRemove();
    }

    /**
     * Permet de configurer la table
      */
    private void configureTable() {
        table.getSelectionModel().setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
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
        this.actionPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_ADD_BUTTON_LABEL),
                1, ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_REMOVE_BUTTON_LABEL)));
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
    public void fillTable(Collection<String> collection) {
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
    public Set<String> getValues() {
        return this.specificEditTableModel.getModelValues();
    }

    @Override
    public void setConsumerForRowChanged(Consumer<String> consumer) {
        this.loadSelectedRowConsumerOptional = Optional.of(consumer);
    }

    @Override
    public Optional<EditTableElement> getEditTableElement() {
        return this.specificEditTableModel.getEditTableElement();
    }

    /**
     * Permet de se procurer le consumer pour le filtre
     * @return le consumer pour le filtre
     */
    private Consumer<?> getConsumerFilter() {
        return e -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                this.specificEditTableModel.filter(this.textBoxPanel.getValueOfTextBox(0));
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
            String id = (String) tableSource.getModel().getValueAt(tableSource.getSelectedRow(), 0);
            tableSource.getSelectionModel().clearSelection();
            specificEditTableModel.remove(Integer.valueOf(id));
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
            specificEditTableModel.add(userQuestion.getAnswer());
            this.editTableModel.fireTableDataChanged();
            ColumnsAutoSize.sizeColumnsToFit(this.table);
        }
    }

    /**
     * Permet de charger le {@link ListSelectionListener} par défaut
     * @return le {@link ListSelectionListener} par défaut
     */
    private ListSelectionListener getDefaultListSelectionListener() {
        return e -> {
            if (table.getSelectedRow() > -1) {
                String valeur = (String) table.getModel().getValueAt(table.getSelectedRow(), 1);
                this.specificEditTableModel.setEditTableElement(Optional.of(new EditTableElementBuilder()
                    .actionEditTableEnum(ActionEditTableEnum.UPDATE)
                    .oldValue(valeur)
                    .value(valeur)
                    .build()));
                if (loadSelectedRowConsumerOptional.isPresent()) {
                    loadSelectedRowConsumerOptional.get().accept(valeur);
                }
            }
        };
    }

    /**
     * Permet de rafraichir les datas et la taille des colonnes
     */
    private void refreshDataAndColumnSize() {
        this.editTableModel.fireTableDataChanged();
        ColumnsAutoSize.sizeColumnsToFit(this.table);
    }

}
