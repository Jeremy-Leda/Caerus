package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisResultDisplay;
import view.interfaces.ILabelsPanel;
import view.panel.LabelsPanel;
import view.panel.analysis.TableAnalysisPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.LinkedList;

/**
 *
 * Fenêtre pour l'affichage des résultats pour l'analyse des tokens
 *
 */
public class AnalysisTokenResultWindow extends ModalJFrameAbstract {

    private final JPanel content = new JPanel();
    private final AnalysisResultDisplay analysisResultDisplay;
    private final TableAnalysisPanel tableAnalysisPanel;
    private final ILabelsPanel labelsPanel;

    public AnalysisTokenResultWindow(IConfigurationControler controler, AnalysisResultDisplay analysisResultDisplay) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_PANEL_TITLE), controler);
        this.analysisResultDisplay = analysisResultDisplay;
        LinkedList<String> headerLinkedList = new LinkedList<>();
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL));
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL));
        this.tableAnalysisPanel = new TableAnalysisPanel(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, analysisResultDisplay.toAnalysisTokenRowList());
        this.labelsPanel = new LabelsPanel(getMessage(Constants.WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        this.labelsPanel.setLabel(0, getMessage(Constants.WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
        this.labelsPanel.setLabel(1, getMessage(Constants.WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
        content.add(this.labelsPanel.getJPanel());
        content.add(this.tableAnalysisPanel.getJPanel());
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Analysis token result window";
    }
}
