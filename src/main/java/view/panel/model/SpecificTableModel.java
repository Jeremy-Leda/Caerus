package view.panel.model;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import view.beans.SpecificRow;
import view.interfaces.IRefreshPanel;
import view.interfaces.ISpecificTextModel;

/**
 * Classe model pour la table
 * @author jerem
 *
 */
public class SpecificTableModel extends AbstractTableModel implements IRefreshPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 505483651198604683L;
	private final ISpecificTextModel specificTextModel;
	private final List<String> headersList;
	private final List<SpecificRow> rowsList;

	public SpecificTableModel(ISpecificTextModel specificTextModel, Graphics graphic) {
		this.specificTextModel = specificTextModel;
		this.headersList = new LinkedList<String>();
		this.rowsList = new LinkedList<SpecificRow>();
	}

	private void loadTable() {
		this.headersList.clear();
		this.rowsList.clear();
		this.headersList.addAll(this.specificTextModel.getHeaderList());
		this.rowsList.addAll(this.specificTextModel.getSpecificRowList());
	}

	@Override
	public int getColumnCount() {
		return this.headersList.size();
	}

	@Override
	public int getRowCount() {
		return this.rowsList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.rowsList.get(rowIndex).getSpecificList().get(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != 0;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.specificTextModel.updateFromCell(rowIndex, columnIndex, aValue.toString());
	}
	
	
	@Override
	public String getColumnName(int column) {
		return this.headersList.get(column);
	}

	@Override
	public void refresh() {
		loadTable();
	}

}
