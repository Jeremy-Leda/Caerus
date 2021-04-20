package view.panel.model;

import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import view.beans.SpecificRow;
import view.interfaces.ISpecificEditTableModel;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;

/**
 *
 * Model pour l'édition des listes de configuration
 *
 */
public class EditTableModel extends AbstractTableModel {

    private final ISpecificEditTableModel specificEditTableModel;
    private final LinkedList<String> headersList;
    private final String HTML_BOLD_HEADER = "<html><b>%s</b></html>";

    /**
     * Constructeur
     * @param header En tête pour le tableau
     * @param specificEditTableModel la gestion des données pour le tableau
     */
    public EditTableModel(String header, ISpecificEditTableModel specificEditTableModel) {
        this.headersList = new LinkedList<>();
        this.headersList.add(" ");
        this.headersList.add(String.format(HTML_BOLD_HEADER,header));
        this.specificEditTableModel = specificEditTableModel;
    }

    @Override
    public int getRowCount() {
        return specificEditTableModel.getRows().size();
    }

    @Override
    public int getColumnCount() {
        return this.headersList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //return this.specificEditTableModel.getSpecificRow(rowIndex).getSpecificList().get(columnIndex);
//        Option<Tuple2<SpecificRow, Integer>> tuple2s = Stream.ofAll(this.specificEditTableModel.getRows()).zipWithIndex().find(tuple -> tuple._2().equals(rowIndex));
//        if (tuple2s.isDefined()) {
//            return tuple2s.get()._1().getSpecificList().get(columnIndex);
//        }
//        return new SpecificRow();
        return this.specificEditTableModel.getRows().get(rowIndex).getSpecificList().get(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        SpecificRow specificRow = this.specificEditTableModel.getSpecificRow(rowIndex);
//        this.specificEditTableModel.update(Integer.valueOf(specificRow.getSpecificList().get(0)), (String)aValue);
//
////        Option<Tuple2<SpecificRow, Integer>> tuple2s = Stream.ofAll(this.specificEditTableModel.getRows()).zipWithIndex().find(tuple -> tuple._2().equals(rowIndex));
////        if (tuple2s.isDefined()) {
////
////            this.specificEditTableModel.update(Integer.valueOf(tuple2s.get()._1().getSpecificList().get(0)), (String)aValue);
////        }
        this.specificEditTableModel.update(Integer.valueOf(this.specificEditTableModel.getRows().get(rowIndex).getSpecificList().get(0)), (String)aValue);
    }

    @Override
    public String getColumnName(int column) {
        return this.headersList.get(column);
    }

}
