package view.panel.analysis;

import view.interfaces.ITableAnalysisPanel;
import view.panel.analysis.model.AnalysisRow;
import view.panel.analysis.model.AnalysisTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Permet de construire un panel pour l'affichage du tableau des résultats d'analyse
 */
public class TableAnalysisPanel implements ITableAnalysisPanel {

    private final JPanel tablePanel = new JPanel();
    private final JTable table = new JTable();
    private final AnalysisTableModel analysisTableModel;
    private Optional<Consumer<List<Object>>> consumerOnSelectedChange = Optional.empty();
    private final TableRowSorter tableRowSorter;

    /**
     * Constructeur
     * @param titlePanel Titre du panel
     * @param headerLinkedList liste des en-têtes
     * @param analysisRowCollection la liste des lignes d'analyses
     */
    public TableAnalysisPanel(String titlePanel, List<String> headerLinkedList, List<Class<?>> classList, List<AnalysisRow> analysisRowCollection) {
        this.analysisTableModel = new AnalysisTableModel(headerLinkedList, classList, analysisRowCollection);
        tablePanel.setBorder(
                BorderFactory.createTitledBorder(titlePanel));
        table.setModel(analysisTableModel);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(getConsumerListener());
        tableRowSorter = new TableRowSorter<>(analysisTableModel);
        table.setRowSorter(tableRowSorter);
        tablePanel.add(new JScrollPane(table));
    }


    @Override
    public JComponent getJPanel() {
        return tablePanel;
    }

    @Override
    public void clearSelection() {
        table.getSelectionModel().clearSelection();
    }

    @Override
    public void updateAnalysisResult(List<AnalysisRow> analysisRowLinkedList) {
        this.analysisTableModel.updateAnalysisResult(analysisRowLinkedList);
        this.analysisTableModel.fireTableDataChanged();
    }

    @Override
    public void addConsumerOnSelectedChangeForWord(Consumer<List<Object>> consumer) {
        this.consumerOnSelectedChange = Optional.ofNullable(consumer);
    }

    @Override
    public Set<String> getSelectedWords() {
        return Arrays.stream(table.getSelectionModel().getSelectedIndices())
                .map(x -> table.convertRowIndexToModel(x))
                .mapToObj(x -> this.analysisTableModel.getRow(x).get(0).toString())
                .collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer le consumer pour le changement de valeur
     * @return le consumer
     */
    private ListSelectionListener getConsumerListener() {
        return e -> {
            int row = table.getSelectedRow();
            if (row > -1) {
                int realRow = tableRowSorter.convertRowIndexToModel(row);
                consumerOnSelectedChange.ifPresent(s -> s.accept(analysisTableModel.getRow(realRow)));
            }
        };
    }
}
