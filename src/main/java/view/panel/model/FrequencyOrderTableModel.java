package view.panel.model;

import view.beans.FrequencyOrder;
import view.beans.FrequencyOrderEnum;
import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.IRefreshPanel;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

public class FrequencyOrderTableModel extends AbstractTableModel implements IRefreshPanel {

    private final List<String> headersList;
    private final List<FrequencyOrder> rowsList;
    private final Boolean isReadOnly;

    private final IFrequencyOrderTextModel frequencyOrderTextModel;

    public FrequencyOrderTableModel(IFrequencyOrderTextModel frequencyOrderTextModel, Boolean isReadOnly) {
        this.headersList = FrequencyOrderEnum.getHeaderList();
        this.rowsList = new LinkedList<>();
        this.isReadOnly = isReadOnly;
        this.frequencyOrderTextModel = frequencyOrderTextModel;
    }

    private void loadTable() {
        this.rowsList.clear();
        this.rowsList.addAll(this.frequencyOrderTextModel.getFrequencyOrderList());
    }

    @Override
    public int getRowCount() {
        return this.rowsList.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return this.headersList.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FrequencyOrder frequencyOrder = rowsList.get(rowIndex);
        return FrequencyOrderEnum.getFrequencyOrderEnumByOrder(columnIndex).getExtractBeanFunction().apply(frequencyOrder);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !isReadOnly;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        this.frequencyOrderTextModel.updateFromCell(rowIndex, columnIndex, aValue);
    }

    @Override
    public void refresh() {
        loadTable();
    }
}
