package view.panel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.panel.model.SpecificTableModel;
import view.utils.ColumnsAutoSize;

/**
 * 
 * Panel pour l'affichage de la liste des sp�cifiques dans un tableau
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
	}

	/**
	 * Permet de charger l'affichage de la fen�tre
	 */
	private void loadDisplayDetailsListPanel() {
		addSelectionModelHandler();
	}

	private void addSelectionModelHandler() {
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				specificTextModel.setCurrentSelectedIndexInList(lsm.getAnchorSelectionIndex());
				
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