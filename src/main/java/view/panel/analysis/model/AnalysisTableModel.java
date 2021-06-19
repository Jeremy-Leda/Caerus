package view.panel.analysis.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Classe pour le model d'affichage des résultats de l'analyse
 */
public class AnalysisTableModel extends AbstractTableModel {

    private final List<String> headersLinkedList;
    private final List<AnalysisRow> analysisRowLinkedList;

    /**
     * Constructeur
     * @param headersLinkedList liste des en-têtes
     * @param analysisRowLinkedList liste des lignes d'analyses
     */
    public AnalysisTableModel(List<String> headersLinkedList, List<AnalysisRow> analysisRowLinkedList) {
        this.headersLinkedList = headersLinkedList;
        this.analysisRowLinkedList = analysisRowLinkedList;
    }

    @Override
    public int getRowCount() {
        return this.analysisRowLinkedList.size();
    }

    @Override
    public int getColumnCount() {
        return this.headersLinkedList.size();
    }

    @Override
    public String getColumnName(int column) {
        return this.headersLinkedList.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.analysisRowLinkedList.get(rowIndex).getAnalysisList().get(columnIndex);
    }
}
