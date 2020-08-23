package view.panel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.panel.model.SpecificTableModel;
import view.utils.ColumnsAutoSize;

/**
 * 
 * Panel pour l'affichage de la liste des spécifiques dans un tableau
 * 
 * @author jerem
 *
 */
public class SpecificListTablePanel implements ISpecificTextRefreshPanel {

	private final ISpecificTextModel specificTextModel;
	private final JPanel detailsListPanel;
	private final JScrollPane scrollPanel;
	private final SpecificTableModel tableModel;
	private final JTable table;

	public SpecificListTablePanel(ISpecificTextModel specificTextModel) {
		this.specificTextModel = specificTextModel;
		this.detailsListPanel = new JPanel();
		this.tableModel = new SpecificTableModel(this.specificTextModel, this.detailsListPanel.getGraphics());
		this.table = new JTable(this.tableModel);
		this.specificTextModel.setClearSelectionConsumer(v -> this.table.clearSelection());
		this.scrollPanel = new JScrollPane(table);
		loadDisplayDetailsListPanel();

		InputMap im = table.getInputMap(JTable.WHEN_FOCUSED);
		ActionMap am = table.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
		am.put("DeleteRow", new AbstractAction() {
			private static final long serialVersionUID = -4413772916234405739L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (specificTextModel.haveCurrentSelectedIndexInList()) {
					specificTextModel.removeSpecificField();
				}

			}
		});
	}

	/**
	 * Permet de charger l'affichage de la fenêtre
	 */
	private void loadDisplayDetailsListPanel() {
		addSelectionModelHandler();
	}

	private void addSelectionModelHandler() {
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				specificTextModel.clearAllSelectedIndexList();
				specificTextModel.setCurrentSelectedIndexInList(lsm.getAnchorSelectionIndex());
				// On ajoute la liste des index sélectionné
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {
						specificTextModel.addIndexToSelectedIndexList(i);
					}
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
		this.tableModel.refresh();
		this.tableModel.fireTableStructureChanged();
		this.tableModel.fireTableDataChanged();
		ColumnsAutoSize.sizeColumnsToFit(table);
	}

	@Override
	public void refreshAfterSelectedIndex() {
	}

}
