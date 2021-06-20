package view.panel.analysis.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Classe pour le model d'affichage des résultats de l'analyse
 */
public class AnalysisTableModel extends AbstractTableModel {

    private final List<String> headersLinkedList;
    private final List<AnalysisRow> analysisRowLinkedList;
    private final List<Class<?>> classeList;

    /**
     * Constructeur
     * @param headersLinkedList liste des en-têtes
     * @param analysisRowLinkedList liste des lignes d'analyses
     */
    public AnalysisTableModel(List<String> headersLinkedList, List<Class<?>> classeList, List<AnalysisRow> analysisRowLinkedList) {
        this.headersLinkedList = headersLinkedList;
        this.analysisRowLinkedList = analysisRowLinkedList;
        this.classeList = classeList;
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.classeList.get(columnIndex);
    }

    /**
     * Permet de se procurer la ligne sélectionné
     * @param num numéro de la ligne
     * @return la ligne
     */
    public List<Object> getRow(Integer num) {
        return this.analysisRowLinkedList.get(num).getAnalysisList();
    }

    /**
     * Permet de mettre à jour les résultat de l'analyse
     * @param analysisRowLinkedList résultat de l'analyse
     */
    public void updateAnalysisResult(List<AnalysisRow> analysisRowLinkedList) {
        this.analysisRowLinkedList.clear();
        this.analysisRowLinkedList.addAll(analysisRowLinkedList);
    }
}
