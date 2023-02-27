package view.panel.model;

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
 * Model pour l'édition des listes de configuration
 *
 */
public class EditTableModel extends AbstractTableModel {

    private final ISpecificEditTableModel specificEditTableModel;
    private final LinkedList<String> headersList;
    private final String HTML_BOLD_HEADER = "<html><b>%s</b></html>";
    private final Boolean isReadOnly;
    private final Map<Integer, Class<?>> columnClassMap;

    /**
     * Constructeur
     * @param header En tête pour le tableau
     * @param specificEditTableModel la gestion des données pour le tableau
     */
    public EditTableModel(String header, ISpecificEditTableModel specificEditTableModel, Boolean isReadOnly,
                          Optional<Map<Integer, Class<?>>> columnClassMap) {
        this.headersList = new LinkedList<>();
        this.headersList.add(String.format(HTML_BOLD_HEADER,header));
        this.specificEditTableModel = specificEditTableModel;
        this.isReadOnly = isReadOnly;
        this.columnClassMap = columnClassMap.orElse(getStringColumnClassMap(this.headersList.size()));
        this.specificEditTableModel.setConsumerForFireTableInserted(getFireTableInsertConsumer());
        this.specificEditTableModel.setConsumerForFireTableUpdated(getFireTableUpdateConsumer());
        this.specificEditTableModel.setConsumerForFireTableDeleted(getFireTableDeleteConsumer());
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
        return this.specificEditTableModel.getRow(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !isReadOnly;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        this.specificEditTableModel.update(this.specificEditTableModel.getRow(rowIndex), aValue);
    }

    @Override
    public String getColumnName(int column) {
        return this.headersList.get(column);
    }

    /**
     * Permet de se procurer le consumer pour l'insertion d'une ligne
     * Met à jour l'affichage en conséquent
     * @return le consumer
     */
    private BiConsumer<Integer, Integer> getFireTableInsertConsumer() {
        return (startRow, endRow) -> this.fireTableRowsInserted(startRow, endRow);
    }

    /**
     * Permet de se procurer le consumer pour la mise à jour d'une ligne
     * Met à jour l'affichage en conséquent
     * @return le consumer
     */
    private BiConsumer<Integer, Integer>getFireTableUpdateConsumer() {
        return (startRow, endRow) -> this.fireTableRowsUpdated(startRow, endRow);
    }

    /**
     * Permet de se procurer le consumer pour la suppression d'une ligne
     * Met à jour l'affichage en conséquent
     * @return le consumer
     */
    private BiConsumer<Integer, Integer> getFireTableDeleteConsumer() {
        return (startRow, endRow) -> this.fireTableRowsDeleted(startRow, endRow);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.columnClassMap.get(columnIndex);
    }

    private Map<Integer, Class<?>> getStringColumnClassMap(Integer nb) {
        Map<Integer, Class<?>> map = new HashMap<>();
        IntStream.range(0, nb).forEach(s -> map.put(s, String.class));
        return map;
    }
}