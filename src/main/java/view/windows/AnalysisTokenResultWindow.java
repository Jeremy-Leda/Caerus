package view.windows;

import controler.IConfigurationControler;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.LexicometricAnalyzeCmd;
import view.beans.LexicometricAnalyzeTypeViewEnum;
import view.beans.LexicometricEditEnum;
import view.interfaces.IActionPanel;
import view.interfaces.ILabelsPanel;
import view.interfaces.ITableAnalysisPanel;
import view.panel.ActionPanel;
import view.panel.LabelsPanel;
import view.panel.analysis.TableAnalysisPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre pour l'affichage des résultats pour l'analyse des tokens
 *
 */
public class AnalysisTokenResultWindow extends ModalJFrameAbstract {

    private final JPanel content = new JPanel();
    private AnalysisResultDisplay analysisResultDisplay;
    private final ITableAnalysisPanel tableAnalysisPanel;
    private final ILabelsPanel labelsPanel;
    private final IActionPanel actionPanel = new ActionPanel(3);
    private final LexicometricAnalyzeCmd cmd;
    private final LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum;

    public AnalysisTokenResultWindow(IConfigurationControler controler,
                                     AnalysisResultDisplay analysisResultDisplay,
                                     LexicometricAnalyzeCmd cmd,
                                     LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_ANALYSIS_PANEL_TITLE), controler, false);
        this.analysisResultDisplay = analysisResultDisplay;
        this.cmd = cmd;
        this.lexicometricAnalyzeTypeEnum = lexicometricAnalyzeTypeEnum;
        LinkedList<String> headerLinkedList = new LinkedList<>();
        headerLinkedList.add(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL));
        headerLinkedList.add(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL));
        this.tableAnalysisPanel = new TableAnalysisPanel(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, List.of(String.class, Long.class), analysisResultDisplay.toAnalysisTokenRowList());
        this.labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        initActionPanel();
        this.labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
        this.labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
        content.add(this.labelsPanel.getJPanel());
        content.add(this.tableAnalysisPanel.getJPanel());
        content.add(this.actionPanel.getJPanel());
    }

    /**
     * Permet d'initialiser les boutons
     */
    private void initActionPanel() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_RESULT_TOKEN_ACTION_PANEL_TITLE),
                Map.of(0, getMessage(WINDOW_RESULT_TOKEN_ACTION_SHOW_DETAIL_BUTTON_LABEL),
                        1, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PROPER_NOUN_BUTTON_LABEL),
                            2, getMessage(WINDOW_RESULT_TOKEN_ACTION_SHOW_DETAIL_FOR_WORD_BUTTON_LABEL)));
        this.actionPanel.addAction(0, e -> openDetailResult(new HashSet<>()));
        this.actionPanel.addAction(1, e -> {
            actionPanel.setEnabled(1, false);
            AnalysisProperNounAddWindow analysisProperNounAddWindow =
                    new AnalysisProperNounAddWindow(getControler(), getPotentialProperNounCollection(), getRelaunchAnalyzeConsumer(),
                            cmd.getPreTreatmentListLexicometricMap().get(LexicometricEditEnum.PROPER_NOUN));
            analysisProperNounAddWindow.addActionOnClose(s -> actionPanel.setEnabled(1, true));
        });
        this.actionPanel.addAction(2, e -> openDetailResult(this.tableAnalysisPanel.getSelectedWords()));
        
    }

    /**
     * Permet d'ouvrir la fenêtre de détail des résultats
     * @param selectedWordSet liste des mots sélectionné
     */
    private void openDetailResult(Set<String> selectedWordSet) {
        actionPanel.setEnabled(0, false);
        actionPanel.setEnabled(2, false);
        AnalysisTokenDetailResultWindow analysisTokenDetailResultWindow =
                new AnalysisTokenDetailResultWindow(getControler(), cmd, lexicometricAnalyzeTypeEnum,
                        getRelaunchAnalyzeConsumer(), selectedWordSet);
        analysisTokenDetailResultWindow.addActionOnClose(x -> {
            actionPanel.setEnabled(0, true);
            actionPanel.setEnabled(2, true);
        });
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Analysis token result window";
    }

    /**
     * Consumer permettant de relancer l'analyse
     * @return le consumer
     */
    private Consumer<?> getRelaunchAnalyzeConsumer() {
        return x -> {
            LexicometricAnalyzeTypeViewEnum lexicometricAnalyzeTypeViewEnum = LexicometricAnalyzeTypeViewEnum.valueOf(lexicometricAnalyzeTypeEnum.name());
            lexicometricAnalyzeTypeViewEnum.getBiConsumerAnalysis().accept(getControler(), cmd);
            AnalysisResultDisplay analysisResultDisplay = lexicometricAnalyzeTypeEnum.getAnalysisResultDisplayFunction().apply(cmd.getKeyTextFilteredList());
            this.tableAnalysisPanel.updateAnalysisResult(analysisResultDisplay.toAnalysisTokenRowList());
            this.labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
            this.labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
        };
    }

    /**
     * Permet de se procurer la liste des noms propres potentiel
     * @return la liste des noms propres potentiels
     */
    private Collection<String> getPotentialProperNounCollection() {
        return getControler().getPotentialProperNounCollection(this.cmd.getKeyTextFilteredList().stream().collect(Collectors.toSet()),
                this.cmd.getFieldToAnalyzeSet(), cmd.toPreTreatmentServerMap());
    }
}
