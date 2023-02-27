package view.panel;

import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.panel.model.FrequencyOrderTableModel;
import view.panel.model.FrequencyOrderTextModel;
import view.utils.ColumnsAutoSize;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FrequencyOrderTablePanel implements ISpecificTextRefreshPanel {

    private final IFrequencyOrderTextModel frequencyOrderTextModel;
    private final FrequencyOrderTableModel frequencyOrderTableModel;
    private final JScrollPane scrollPanel;
    private final JTable table;

    public FrequencyOrderTablePanel(IFrequencyOrderTextModel frequencyOrderTextModel, Boolean readOnly) {
        this.frequencyOrderTextModel = frequencyOrderTextModel;
        this.frequencyOrderTableModel = new FrequencyOrderTableModel(frequencyOrderTextModel, readOnly);
        this.table = new JTable(this.frequencyOrderTableModel);
        this.frequencyOrderTextModel.setClearSelectionConsumer(v -> this.table.clearSelection());
        this.scrollPanel = new JScrollPane(table);
        loadDisplayDetailsListPanel();

        if (!readOnly) {
            InputMap im = table.getInputMap(JTable.WHEN_FOCUSED);
            ActionMap am = table.getActionMap();

            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
            am.put("DeleteRow", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (frequencyOrderTextModel.haveCurrentSelectedIndexInList()) {
                        frequencyOrderTextModel.removeSpecificField();
                    }
                }
            });
        }
    }

    /**
     * Permet de charger l'affichage de la fenêtre
     */
    private void loadDisplayDetailsListPanel() {
        addSelectionModelHandler();
    }

    private void addSelectionModelHandler() {
        this.table.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            frequencyOrderTextModel.clearAllSelectedIndexList();
            frequencyOrderTextModel.setCurrentSelectedIndexInList(lsm.getAnchorSelectionIndex());
            // On ajoute la liste des index sélectionné
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    frequencyOrderTextModel.addIndexToSelectedIndexList(i);
                }
            }
        });
    }

    @Override
    public JComponent getJPanel() {
        return scrollPanel;
    }

    @Override
    public void refresh() {
        this.frequencyOrderTableModel.refresh();
        this.frequencyOrderTableModel.fireTableStructureChanged();
        this.frequencyOrderTableModel.fireTableDataChanged();
        ColumnsAutoSize.sizeColumnsToFit(table);
    }

    @Override
    public void refreshAfterSelectedIndex() {

    }
}
