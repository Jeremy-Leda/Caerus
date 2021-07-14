package view.panel.model;

import org.apache.commons.lang3.StringUtils;
import view.beans.SelectedObjectTable;
import view.interfaces.ISelectedEditTableModel;
import view.interfaces.ISpecificEditTableModel;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 *
 * Model pour pour afficher une liste avec une case à cocher
 *
 */
public class SelectedTableModel<T> extends AbstractTableModel {

    private final ISelectedEditTableModel<T> selectedEditTableModel;
    private final LinkedList<String> headersList;
    private final String HTML_BOLD_HEADER = "<html><b>%s</b></html>";
    private final Class<T> dataClass;

    /**
     * Constructeur
     * @param header En tête pour le tableau
     * @param dataClass la classe des objet de données
     */
    public SelectedTableModel(String header, Class<T> dataClass) {
        this.headersList = new LinkedList<>();
        this.headersList.add(StringUtils.SPACE);
        this.headersList.add(String.format(HTML_BOLD_HEADER,header));
        this.selectedEditTableModel = new SelectedEditTableModel<>();
        this.dataClass = dataClass;
    }

    @Override
    public int getRowCount() {
        return selectedEditTableModel.getRows().size();
    }

    @Override
    public int getColumnCount() {
        return this.headersList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SelectedObjectTable<T> row = this.selectedEditTableModel.getRow(rowIndex);
        if (columnIndex == 0) {
            return row.isChecked();
        }
        return row.getData();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public String getColumnName(int column) {
        return this.headersList.get(column);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SelectedObjectTable<T> row = this.selectedEditTableModel.getRow(rowIndex);
        if (columnIndex == 0) {
            row.setChecked((Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        }
        return dataClass;
    }

    /**
     * Permet de se procurer le model
     * @return le model
     */
    public ISelectedEditTableModel<T> getSelectedEditTableModel() {
        return selectedEditTableModel;
    }
}
