package view.panel.analysis;

import view.interfaces.IAccessPanel;
import view.panel.analysis.model.AnalysisRow;
import view.panel.analysis.model.AnalysisTableModel;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Permet de construire un panel pour l'affichage du tableau des résultats d'analyse
 */
public class TableAnalysisPanel implements IAccessPanel {

    private final JPanel tablePanel = new JPanel();
    private final JTable table = new JTable();
    private final AnalysisTableModel analysisTableModel;

    /**
     * Constructeur
     * @param titlePanel Titre du panel
     * @param headerLinkedList liste des en-têtes
     * @param analysisRowCollection la liste des lignes d'analyses
     */
    public TableAnalysisPanel(String titlePanel, List<String> headerLinkedList, List<AnalysisRow> analysisRowCollection) {
        this.analysisTableModel = new AnalysisTableModel(headerLinkedList, analysisRowCollection);
        tablePanel.setBorder(
                BorderFactory.createTitledBorder(titlePanel));
        table.setModel(analysisTableModel);
        tablePanel.add(new JScrollPane(table));
    }


    @Override
    public JComponent getJPanel() {
        return tablePanel;
    }
}
