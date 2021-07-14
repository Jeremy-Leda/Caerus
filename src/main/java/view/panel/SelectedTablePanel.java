package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.beans.SelectedObjectTable;
import view.interfaces.ISelectedTablePanel;
import view.interfaces.ITableFilterPanel;
import view.panel.model.SelectedTableModel;
import view.utils.RowNumberTable;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class SelectedTablePanel<T> implements ISelectedTablePanel<T> {

    private final JPanel panel = new JPanel();
    private final JTable table;
    private JScrollPane scrollPane;
    private final SelectedTableModel<T> selectedTableModel;
    private final TableFilterTextPanel<T> tableFilterPanel;
    private final BiFunction<Collection<SelectedObjectTable<T>>, T, Collection<SelectedObjectTable<T>>> filterFunction;

    public SelectedTablePanel(String title, String header, Class<T> dataClass,
                              BiFunction<Collection<SelectedObjectTable<T>>, T, Collection<SelectedObjectTable<T>>> filterFunction,
                              String filterLabel) {
        this.filterFunction = filterFunction;
        this.selectedTableModel = new SelectedTableModel(header, dataClass);
        this.table = new JTable(selectedTableModel);
        this.tableFilterPanel = new TableFilterTextPanel();
        this.tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, filterLabel));
        this.tableFilterPanel.addConsumerOnChange(s -> filter(Optional.ofNullable(this.tableFilterPanel.getFilter().getValue())));
        table.getColumnModel().getColumn(0).setMinWidth(20);
        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.getColumnModel().getColumn(0).setWidth(20);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        configureTable();
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        panel.add(this.tableFilterPanel.getJPanel());
        panel.add(this.scrollPane);
    }

    /**
     * Permet de configurer la table
     */
    private void configureTable() {
        this.scrollPane = new JScrollPane(this.table);
        JTable rowTable = new RowNumberTable(this.table);
        scrollPane.setRowHeaderView(rowTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
                rowTable.getTableHeader());
        table.setRowHeight(30);
    }

    @Override
    public JComponent getJPanel() {
        return panel;
    }

    @Override
    public void refreshData(Collection<SelectedObjectTable<T>> data) {
        this.selectedTableModel.getSelectedEditTableModel().cleanAndFill(data);
        this.selectedTableModel.fireTableDataChanged();
    }

    @Override
    public Collection<SelectedObjectTable<T>> getRowsByState(boolean isSelected) {
        return this.selectedTableModel.getSelectedEditTableModel().getRowsByState(isSelected);
    }

    @Override
    public void filter(Optional<T> filterData) {
        this.selectedTableModel.getSelectedEditTableModel().filterData(this.filterFunction, filterData);
        this.selectedTableModel.fireTableDataChanged();
    }
}
