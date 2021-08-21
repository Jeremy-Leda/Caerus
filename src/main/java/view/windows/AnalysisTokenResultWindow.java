package view.windows;

import controler.IConfigurationControler;
import io.vavr.collection.Stream;
import model.analyze.LexicometricAnalysis;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisGroupDisplay;
import view.analysis.beans.AnalysisResultDisplay;
import view.analysis.beans.interfaces.IExcelSheet;
import view.beans.LexicometricAnalyzeCmd;
import view.beans.LexicometricAnalyzeTypeViewEnum;
import view.beans.LexicometricEditEnum;
import view.components.DragAndDropCloseableTabbedPane;
import view.interfaces.IActionPanel;
import view.interfaces.IAddAnalysisGroupDisplay;
import view.interfaces.ILabelsPanel;
import view.interfaces.ITableAnalysisPanel;
import view.panel.ActionPanel;
import view.panel.LabelsPanel;
import view.panel.analysis.TableAnalysisPanel;
import view.utils.ConfigurationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre pour l'affichage des résultats pour l'analyse des tokens
 *
 */
public class AnalysisTokenResultWindow extends ModalJFrameAbstract implements IAddAnalysisGroupDisplay {

    private final JPanel content = new JPanel();
    private AnalysisResultDisplay analysisResultDisplay;
    private final ITableAnalysisPanel tableAnalysisPanel;
    private final ILabelsPanel labelsPanel;
    private final IActionPanel actionPanel = new ActionPanel(5);
    private final LexicometricAnalyzeCmd cmd;
    private final LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum;
    private final DragAndDropCloseableTabbedPane tabbedPane = new DragAndDropCloseableTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
    private final Map<JComponent, AnalysisGroupDisplay> componentAnalysisGroupDisplayMap = new HashMap<>();
    private final Map<JComponent, ITableAnalysisPanel> componentTableAnalysisPanelMap = new HashMap<>();
    private final LinkedList<String> headerLinkedList;

    public AnalysisTokenResultWindow(IConfigurationControler controler,
                                     AnalysisResultDisplay analysisResultDisplay,
                                     LexicometricAnalyzeCmd cmd,
                                     LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_ANALYSIS_PANEL_TITLE), controler, false);
        this.analysisResultDisplay = analysisResultDisplay;
        this.cmd = cmd;
        this.lexicometricAnalyzeTypeEnum = lexicometricAnalyzeTypeEnum;
        this.headerLinkedList = new LinkedList<>();
        headerLinkedList.add(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL));
        headerLinkedList.add(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL));
        this.tableAnalysisPanel = new TableAnalysisPanel(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, List.of(String.class, Long.class), analysisResultDisplay.toAnalysisTokenRowList());
        this.labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        this.tabbedPane.addChangeListener(s -> repack());
        this.tabbedPane.setConsumerToRemove(c -> {
            componentAnalysisGroupDisplayMap.remove(c);
            componentTableAnalysisPanelMap.remove(c);
        });
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        initActionPanel();
        this.labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
        this.labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
        this.tabbedPane.addTab(getMessage(WINDOW_RESULT_TOKEN_GLOBAL_LABEL), this.tableAnalysisPanel.getJPanel());

        content.add(this.labelsPanel.getJPanel());
        content.add(this.tabbedPane);
        content.add(this.actionPanel.getJPanel());
    }

    /**
     * Permet d'initialiser les boutons
     */
    private void initActionPanel() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_RESULT_TOKEN_ACTION_PANEL_TITLE),
                Map.of(0, getMessage(WINDOW_RESULT_TOKEN_ACTION_SHOW_DETAIL_BUTTON_LABEL),
                        1, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PROPER_NOUN_BUTTON_LABEL),
                            2, getMessage(WINDOW_RESULT_TOKEN_ACTION_SHOW_DETAIL_FOR_WORD_BUTTON_LABEL),
                        3, getMessage(WINDOW_RESULT_TOKEN_ACTION_SHOW_GROUP_RESULT_BUTTON_LABEL),
                        4, getMessage(WINDOW_RESULT_TOKEN_ACTION_EXPORT_EXCEL_BUTTON_LABEL)));
        this.actionPanel.addAction(0, e -> openDetailResult(new HashSet<>(), getKeySetForDisplayDetail()));
        this.actionPanel.addAction(1, e -> {
            actionPanel.setEnabled(1, false);
            AnalysisProperNounAddWindow analysisProperNounAddWindow =
                    new AnalysisProperNounAddWindow(getControler(), getPotentialProperNounCollection(), getRelaunchAnalyzeConsumer(),
                            cmd.getPreTreatmentListLexicometricMap().get(LexicometricEditEnum.PROPER_NOUN));
            analysisProperNounAddWindow.addActionOnClose(s -> actionPanel.setEnabled(1, true));
        });
        this.actionPanel.addAction(2, e -> openDetailResult(getSelectedWordsForDisplayDetail(), getKeySetForDisplayDetail()));
        this.actionPanel.addAction(3, e -> new AnalysisGroupResultWindow(getControler(), this, cmd));
        this.actionPanel.addAction(4, e -> {
            List<IExcelSheet> sheetsList = new LinkedList<>();
            sheetsList.add(analysisResultDisplay);
            sheetsList.addAll(componentAnalysisGroupDisplayMap.values());
            new ExportExcelWindow(getControler(), sheetsList);
        });
        
    }

    /**
     * Permet d'ouvrir la fenêtre de détail des résultats
     * @param selectedWordSet liste des mots sélectionné
     */
    private void openDetailResult(Set<String> selectedWordSet, Set<String> keySet) {
        actionPanel.setEnabled(0, false);
        actionPanel.setEnabled(2, false);
        AnalysisTokenDetailResultWindow analysisTokenDetailResultWindow =
                new AnalysisTokenDetailResultWindow(getControler(), cmd, lexicometricAnalyzeTypeEnum,
                        getRelaunchAnalyzeConsumer(), selectedWordSet, keySet);
        analysisTokenDetailResultWindow.addActionOnClose(x -> {
            actionPanel.setEnabled(0, true);
            actionPanel.setEnabled(2, true);
        });
    }

    /**
     * Permet de se procurer la liste des clés pour l'affichage des détail
     * @return la liste des clés pour l'affichage des détail
     */
    private Set<String> getKeySetForDisplayDetail() {
        Component selectedComponent = this.tabbedPane.getSelectedComponent();
        if (this.componentAnalysisGroupDisplayMap.containsKey(selectedComponent)) {
            AnalysisGroupDisplay analysisGroupDisplay = this.componentAnalysisGroupDisplayMap.get(selectedComponent);
            return analysisGroupDisplay.getKeySet();
        }
        return this.cmd.getKeyTextFilteredList().stream().collect(Collectors.toSet());
    }

    /**
     * Permet de se procurer la liste des mots pour l'affichage des détail
     * @return la liste des mots pour l'affichage des détail
     */
    private Set<String> getSelectedWordsForDisplayDetail() {
        Component selectedComponent = this.tabbedPane.getSelectedComponent();
        if (this.componentTableAnalysisPanelMap.containsKey(selectedComponent)) {
            return this.componentTableAnalysisPanelMap.get(selectedComponent).getSelectedWords();
        }
        return this.tableAnalysisPanel.getSelectedWords();
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
        return getControler().getPotentialProperNounCollection(getKeySetForDisplayDetail().stream().collect(Collectors.toSet()),
                this.cmd.getFieldToAnalyzeSet(), cmd.toPreTreatmentServerMap());
    }

    @Override
    public void addAnalysisGroupDisplay(AnalysisGroupDisplay analysisGroupDisplay) {
        JPanel content = new JPanel();
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(createLabelsPanel(analysisGroupDisplay).getJPanel());
        TableAnalysisPanel tableAnalysisPanel = new TableAnalysisPanel(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, List.of(String.class, Long.class), analysisGroupDisplay.getAnalysisResultDisplay().toAnalysisTokenRowList());
        content.add(tableAnalysisPanel.getJPanel());
        this.componentAnalysisGroupDisplayMap.put(content, analysisGroupDisplay);
        this.componentTableAnalysisPanelMap.put(content, tableAnalysisPanel);
        this.tabbedPane.addCloseableTab(analysisGroupDisplay.getTitle(), content);
    }

    private ILabelsPanel createLabelsPanel(AnalysisGroupDisplay analysisGroupDisplay) {
        ILabelsPanel labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_GROUP_RESULT_INFORMATION_PANEL_TITLE), analysisGroupDisplay.getCartesianGroupSet().size() + 2);
        Stream.ofAll(analysisGroupDisplay.getCartesianGroupSet()).zipWithIndex().forEach(t -> labelsPanel.setLabel(t._2, t._1.getLabel(), t._1.getValue()));
        labelsPanel.setLabel(analysisGroupDisplay.getCartesianGroupSet().size(),
                getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL),
                String.valueOf(analysisGroupDisplay.getAnalysisResultDisplay().getNbToken()));
        labelsPanel.setLabel(analysisGroupDisplay.getCartesianGroupSet().size() + 1,
                getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL),
                String.valueOf(analysisGroupDisplay.getAnalysisResultDisplay().getNbOccurrency()));
        return  labelsPanel;
    }
}
