package view.windows;

import controler.IConfigurationControler;
import io.vavr.collection.Stream;
import model.analyze.LexicometricAnalysis;
import model.analyze.cmd.AnalysisDetailResultDisplayCmd;
import model.analyze.cmd.AnalysisDetailResultDisplayCmdBuilder;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.interfaces.IProgressModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisDetailResultDisplay;
import view.analysis.beans.interfaces.IExcelSheet;
import view.beans.LexicometricAnalyzeCmd;
import view.beans.LexicometricEditEnum;
import view.components.DragAndDropCloseableTabbedPane;
import view.interfaces.*;
import view.panel.*;
import view.panel.analysis.TableAnalysisPanel;
import view.panel.model.ProgressBarModel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre pour l'affichage des détails pour les tokens
 *
 */
public class AnalysisTokenDetailResultWindow extends ModalJFrameAbstract implements IProgressModel {

    private JPanel content = new JPanel();
    private IRadioButtonPanel radioButtonPanel;
    private IActionPanel actionPanel = new ActionPanel(4);
    private final Map<String, String> mapField;
    private Optional<ReadCorpus> optionalReadCorpusWindow = Optional.empty();
    private Optional<ReadText> optionalReadTextWindow = Optional.empty();
    private Optional<AnalysisProperNounAddWindow> optionalAnalysisProperNounAddWindow = Optional.empty();
    private final Runnable runnableRelaunchBase;
    private final Map<JComponent, AnalysisDetailResultDisplay> componentAnalysisDetailResultDisplayMap = new HashMap<>();
    private final Map<AnalysisDetailResultDisplay, ITextHighlightPanel> analysisDetailResultDisplayITextHighlightPanelHashMap = new HashMap<>();
    private final LinkedList<String> headerLinkedList = new LinkedList<>();
    private final DragAndDropCloseableTabbedPane tabbedPane = new DragAndDropCloseableTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
    private final Map<LexicometricConfigurationEnum, String> lexicometricConfigurationEnumStringMap;
    private final ExecutorService loadExecutorService = Executors.newSingleThreadExecutor();
    private ExecutorService synchronousExecutorService = Executors.newSingleThreadExecutor();
    private final IProgressBarPanel progressBarPanel = new ProgressBarPanel(new ProgressBarModel());
    private static Logger logger = LoggerFactory.getLogger(AnalysisTokenDetailResultWindow.class);
    private final String analysisLabel;
    private Integer currentLoadingTab = 0;

    private Integer oldSelectedIndex = -1;
    private Integer currentSelectedIndex = -1;

    /**
     * Constructeur
     * @param configurationControler controller
     */
    public AnalysisTokenDetailResultWindow(IConfigurationControler configurationControler,
                                           LexicometricAnalyzeCmd cmd,
                                           LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                                           Runnable relaunchAnalyzeConsumer,
                                           Set<String> selectedWords,
                                           Set<String> keySet) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PANEL_TITLE), configurationControler, false);
        this.runnableRelaunchBase = relaunchAnalyzeConsumer;
        this.mapField = getControler().getFieldConfigurationNameLabelWithoutMetaMap();
        this.lexicometricConfigurationEnumStringMap = cmd.toPreTreatmentServerMap();
        this.analysisLabel = cmd.getLabel();
        List<String> keyFilteredTextList = new LinkedList<>();
        if (selectedWords.isEmpty()) {
            keyFilteredTextList.addAll(keySet);
        } else {
            keyFilteredTextList.addAll(getControler().getKeyTextSetWithSelectedWordsFromAnalyze(keySet, selectedWords));
        }
        initActionPanel(cmd, keyFilteredTextList, lexicometricAnalyzeTypeEnum);
        this.radioButtonPanel = getRadioButtonPanel(cmd);
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL));
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL));
        tabbedPane.addMouseWheelListener(e -> {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            int units = e.getWheelRotation();
            int oldIndex = pane.getSelectedIndex();
            int newIndex = oldIndex + units;
            if (newIndex < 0)
                pane.setSelectedIndex(0);
            else if (newIndex >= pane.getTabCount())
                pane.setSelectedIndex(pane.getTabCount() - 1);
            else
                pane.setSelectedIndex(newIndex);
        });
        tabbedPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                logger.info("launch synchronous");
                synchronousExecutorService = Executors.newSingleThreadExecutor();
                synchronousExecutorService.execute(() -> { while(tabbedPane.isVisible()) checkForSynchronize(); });
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                logger.info("stop synchronous");
                synchronousExecutorService.shutdownNow();
            }
        });
        reload(keyFilteredTextList, lexicometricAnalyzeTypeEnum, cmd.getFieldToAnalyzeSet());
        this.tabbedPane.setConsumerToRemove(c -> componentAnalysisDetailResultDisplayMap.remove(c));
        this.addActionOnClose(closeAutomaticallyAllWindows());
        this.addActionOnClose(() -> loadExecutorService.shutdownNow());
        this.addActionOnClose(() -> {
            tabbedPane.setVisible(false);
            synchronousExecutorService.shutdownNow();
        });
        createWindow();
    }

    /**
     * Permet de vérifier si il est nécessaire de synchroniser
     */
    private void checkForSynchronize() {
        try {
            Thread.sleep(50);
        } catch (Exception ex) {}
        if (this.tabbedPane.getSelectedIndex() == -1) {
            return;
        }
        if (this.oldSelectedIndex != this.tabbedPane.getSelectedIndex()) {
            this.oldSelectedIndex = this.tabbedPane.getSelectedIndex();
            return;
        }
        if (this.currentSelectedIndex != this.tabbedPane.getSelectedIndex()) {
            this.currentSelectedIndex = this.tabbedPane.getSelectedIndex();
            synchronize();
        }
    }

    /**
     * Permet de synchronizer les autres interfaces avec la sélection
     */
    private void synchronize() {
        optionalReadCorpusWindow.ifPresent(readCorpus -> readCorpus.setKeyText(getSelectedKeyText()));
        optionalReadTextWindow.ifPresent(readText -> readText.setKeyText(getSelectedKeyText()));
        optionalAnalysisProperNounAddWindow.ifPresent(analysisProperNounAddWindow -> analysisProperNounAddWindow.setProperNounSetOfText(getPotentialProperNounCollection()));
        reloadTextHighlightPanel();
    }

    /**
     * Permet d'initialiser le panel des actions
     *
     */
    private void initActionPanel(LexicometricAnalyzeCmd cmd,
                                 List<String> keyFilteredTextList,
                                 LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum) {
        Map<Integer, String> labelMap = Map.of(0, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_ACTION_VIEW_META_BUTTON_LABEL),
                1, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_ACTION_VIEW_DATA_BUTTON_LABEL),
                2, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PROPER_NOUN_BUTTON_LABEL),
                3, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ACTION_EXPORT_EXCEL_BUTTON_LABEL));
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL), labelMap);
        this.actionPanel.addAction(0, e -> {
            actionPanel.setEnabled(0, false);
            optionalReadCorpusWindow = Optional.of(new ReadCorpus(getMessage(WINDOW_READ_CORPUS_TITLE), getControler(), false, getSelectedKeyText()));
            optionalReadCorpusWindow.get().addActionOnClose(() -> actionPanel.setEnabled(0, true));
        });
        this.actionPanel.addAction(1, e -> {
            actionPanel.setEnabled(1, false);
            optionalReadTextWindow = Optional.of(new ReadText(getMessage(WINDOW_READ_TEXT_TITLE), getControler(), false, getSelectedKeyText()));
            optionalReadTextWindow.get().addActionOnClose(() -> actionPanel.setEnabled(1, true));
        });
        this.actionPanel.addAction(2, e -> {
            actionPanel.setEnabled(2, false);
            optionalAnalysisProperNounAddWindow = Optional.of(new AnalysisProperNounAddWindow(getControler(), getPotentialProperNounCollection(),
                    getRelaunchAnalyzeConsumer(runnableRelaunchBase, keyFilteredTextList, lexicometricAnalyzeTypeEnum, cmd.getFieldToAnalyzeSet()),
                    cmd.getPreTreatmentListLexicometricMap().get(LexicometricEditEnum.PROPER_NOUN)));
            optionalAnalysisProperNounAddWindow.get().addActionOnClose(() -> actionPanel.setEnabled(2, true));
        });
        this.actionPanel.addAction(3, e -> {
            actionPanel.setEnabled(3, false);
            List<IExcelSheet> sheetsList = new LinkedList<>();
            sheetsList.addAll(componentAnalysisDetailResultDisplayMap.values().stream().sorted(Comparator.comparing(AnalysisDetailResultDisplay::getFileNumber).thenComparing(AnalysisDetailResultDisplay::getMaterialNumber)).collect(Collectors.toList()));
            new ExportExcelWindow(getControler(), sheetsList);
        });
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(radioButtonPanel.getJPanel());
        content.add(progressBarPanel.getJPanel());
        content.add(tabbedPane);
        content.add(this.actionPanel.getJPanel());
    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Detail analysis result window";
    }

    /**
     * Permet de se procurer un set de détail des résultats
     * @param keyFilteredTextList liste des clés
     * @return un set de détail des résultats
     */
    private Set<AnalysisDetailResultDisplay> getDetailResultSet(List<String> keyFilteredTextList,
                                                                LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                                                                Set<String> fieldToAnalyzeSet) {
        Set<AnalysisDetailResultDisplay> analysisDetailResultDisplaySet = new TreeSet<>(Comparator.comparing(AnalysisDetailResultDisplay::getFileNumber).thenComparing(AnalysisDetailResultDisplay::getMaterialNumber));
        executeOnServerWithProgressView(() -> {
            AnalysisDetailResultDisplayCmd cmd = new AnalysisDetailResultDisplayCmdBuilder()
                    .keyTextFilteredList(keyFilteredTextList)
                    .keyFieldSet(fieldToAnalyzeSet)
                    .preTreatmentListLexicometricMap(this.lexicometricConfigurationEnumStringMap)
                    .build();
            analysisDetailResultDisplaySet.addAll(lexicometricAnalyzeTypeEnum.getAnalysisDetailResultDisplayCmdSetFunction().apply(cmd));
        }, LexicometricAnalysis.getInstance(),
                String.format(getMessage(WINDOW_LOADING_RESULTS_DETAIL_ANALYSIS_LABEL), analysisLabel),
                false,
                false);
        return analysisDetailResultDisplaySet;
    }

    /**
     * Permet de se procurer la liste des noms propres potentiel
     * @return la liste des noms propres potentiels
     */
    private Collection<String> getPotentialProperNounCollection() {
        String key = getSelectedKeyText();
        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
        return getControler().getPotentialProperNounCollection(Set.of(key), Set.of(field), this.lexicometricConfigurationEnumStringMap);
    }

    /**
     * Permet de se procurer la clé du texte sélectionné
     * @return la clé du texte sélectionné
     */
    private String getSelectedKeyText() {
        return this.componentAnalysisDetailResultDisplayMap.get(this.tabbedPane.getSelectedComponent()).getAnalysisResultDisplay().getKey();
    }

    /**
     * Permet de se procurer le consumer pour surligner
     * @param textHighlightPanel Le composant dans lequel on souhaite surligner
     * @return le consumer
     */
    private Consumer<List<Object>> getConsumerForHighlight(ITextHighlightPanel textHighlightPanel) {
        return result -> {
            String word = String.valueOf(result.get(0));
            textHighlightPanel.highlightWord(word, Color.CYAN);
        };
    }

    /**
     * Consumer pour rattacher la fermeture de la fenêtre fille si présente
     *
     * @return
     */
    private Runnable closeAutomaticallyAllWindows() {
        return () -> {
            optionalReadCorpusWindow.ifPresent(ModalJFrameAbstract::closeFrame);
            optionalReadTextWindow.ifPresent(ModalJFrameAbstract::closeFrame);
            optionalAnalysisProperNounAddWindow.ifPresent(ModalJFrameAbstract::closeFrame);
        };
    }

    /**
     * Permet de se procurer le consumer qui relance l'analyse
     * @param runnableBase consumer de base
     * @param keyFilteredTextList liste des clés
     * @param lexicometricAnalyzeTypeEnum le type d'analyse
     * @param fieldToAnalyzeSet le set des champs d'analyse
     * @return le consumer qui relance l'analyse
     */
    private Runnable getRelaunchAnalyzeConsumer(Runnable runnableBase,
                                                   List<String> keyFilteredTextList,
                                                   LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                                                   Set<String> fieldToAnalyzeSet) {
        return () -> {
            runnableBase.run();
            reload(keyFilteredTextList, lexicometricAnalyzeTypeEnum, fieldToAnalyzeSet);
        };
    }

    private void displayLoading() {
        this.tabbedPane.setVisible(false);
        currentLoadingTab = 0;
        progressBarPanel.launchTreatment(this);
        progressBarPanel.getJPanel().setVisible(true);
        this.actionPanel.setEnabled(0, false);
        this.actionPanel.setEnabled(1, false);
        this.actionPanel.setEnabled(2, false);
        this.actionPanel.setEnabled(3, false);
        repack();
    }

    private void displayTab() {
        this.tabbedPane.setVisible(true);
        progressBarPanel.getJPanel().setVisible(false);
        progressBarPanel.stop();
        this.actionPanel.setEnabled(0, true);
        this.actionPanel.setEnabled(1, true);
        this.actionPanel.setEnabled(2, true);
        this.actionPanel.setEnabled(3, true);
        repack();
    }

    /**
     * Permet de recharger les textes
     * @param keyFilteredTextList liste des clés
     * @param lexicometricAnalyzeTypeEnum le type d'analyse
     * @param fieldToAnalyzeSet le set des champs d'analyse
     */
    private void reload(List<String> keyFilteredTextList,
                        LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                        Set<String> fieldToAnalyzeSet) {
        if (LexicometricAnalysis.getInstance().treatmentIsCancelled()) {
            return;
        }
        displayLoading();
        Set<AnalysisDetailResultDisplay> detailResultSet = getDetailResultSet(keyFilteredTextList, lexicometricAnalyzeTypeEnum, fieldToAnalyzeSet);
        AtomicInteger temp = new AtomicInteger(0);
        loadExecutorService.execute(() -> {
            tabbedPane.removeAll();
            detailResultSet.forEach(a -> {
                this.addAnalysisDetailResultDisplay(a);
                currentLoadingTab = temp.getAndIncrement() * 100 / detailResultSet.size();
            });
            if (LexicometricAnalysis.getInstance().treatmentIsCancelled()) {
                tabbedPane.removeAll();
            }
            displayTab();
        });
    }

    /**
     * Permet d'ajouter un résultat en onglet
     * @param analysisDetailResultDisplay résultat à ajouter
     */
    public void addAnalysisDetailResultDisplay(AnalysisDetailResultDisplay analysisDetailResultDisplay) {
        JPanel content = new JPanel();
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        ITextHighlightPanel textHighlightPanel = getTextHighlightPanel(analysisDetailResultDisplay);
        ILabelsPanel labelsPanel = getLabelsPanel(analysisDetailResultDisplay);
        ITableAnalysisPanel tableAnalysisPanel = getTableAnalysisPanel(analysisDetailResultDisplay, textHighlightPanel);
        content.add(textHighlightPanel.getJPanel());
        content.add(labelsPanel.getJPanel());
        content.add(tableAnalysisPanel.getJPanel());
        this.componentAnalysisDetailResultDisplayMap.put(content, analysisDetailResultDisplay);
        this.analysisDetailResultDisplayITextHighlightPanelHashMap.put(analysisDetailResultDisplay, textHighlightPanel);
        this.tabbedPane.addCloseableTab(analysisDetailResultDisplay.getIdentification(), content);
    }

    /**
     * Permet de se procurer le panel avec les boutons radios
     * @param cmd commande
     * @return le panel avec les boutons radios
     */
    private IRadioButtonPanel getRadioButtonPanel(LexicometricAnalyzeCmd cmd) {
        IRadioButtonPanel radioButtonPanel = new RadioButtonPanel(cmd.getFieldToAnalyzeSet().size());
        Map<Integer, String> radioButtonMap = Stream.ofAll(cmd.getFieldToAnalyzeSet()).zipWithIndex()
                .collect(Collectors.toMap(t -> t._2, t ->  mapField.get(t._1)));
        radioButtonPanel.setStaticLabel(
                getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_CHOOSE_FIELD_PANEL_TITLE), radioButtonMap);
        radioButtonPanel.setDefaultSelectedRadioButton(0);
        radioButtonPanel.setActionListener(x -> reloadTextHighlightPanel());
        return radioButtonPanel;
    }

    /**
     * Permet de recharger le texte dans le composant pour surligner
     */
    private void reloadTextHighlightPanel() {
        AnalysisDetailResultDisplay analysisDetailResultDisplay = componentAnalysisDetailResultDisplayMap.get(tabbedPane.getSelectedComponent());
        ITextHighlightPanel iTextHighlightPanel = analysisDetailResultDisplayITextHighlightPanelHashMap.get(analysisDetailResultDisplay);
        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
        iTextHighlightPanel.setText(analysisDetailResultDisplay.getFieldValueMap().get(field));
    }

    /**
     * Permet de se procurer le panel pour les textes à surligner
     * @param analysisDetailResultDisplay le détail des résultats
     * @return le panel pour les textes à surligner
     */
    private ITextHighlightPanel getTextHighlightPanel(AnalysisDetailResultDisplay analysisDetailResultDisplay) {
        ITextHighlightPanel textHighlightPanel = new TextHighlightPanel(getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_DISPLAY_FIELD_PANEL_TITLE));
        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
        textHighlightPanel.setText(analysisDetailResultDisplay.getFieldValueMap().get(field));
        return textHighlightPanel;
    }

    /**
     * Permet de se procurer les labels pour le récap
     * @param analysisDetailResultDisplay le détail des résultats
     * @return les labels pour le récap
     */
    private ILabelsPanel getLabelsPanel(AnalysisDetailResultDisplay analysisDetailResultDisplay) {
        ILabelsPanel labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisDetailResultDisplay.getAnalysisResultDisplay().getNbToken()));
        labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisDetailResultDisplay.getAnalysisResultDisplay().getNbOccurrency()));
        return labelsPanel;
    }

    /**
     * Permet de se procurer la table d'analyse
     * @param analysisDetailResultDisplay le détail des résultats
     * @param textHighlightPanel le panel pour les textes à surligner
     * @return la table d'analyse
     */
    private ITableAnalysisPanel getTableAnalysisPanel(AnalysisDetailResultDisplay analysisDetailResultDisplay, ITextHighlightPanel textHighlightPanel) {
        TableAnalysisPanel tableAnalysisPanel = new TableAnalysisPanel(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, List.of(String.class, Long.class), new LinkedList<>());
        tableAnalysisPanel.addConsumerOnSelectedChangeForWord(getConsumerForHighlight(textHighlightPanel));
        tableAnalysisPanel.updateAnalysisResult(analysisDetailResultDisplay.getAnalysisResultDisplay().toAnalysisTokenRowList());
        return tableAnalysisPanel;
    }

    @Override
    public Integer getProgress() {
        return this.currentLoadingTab;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean treatmentIsCancelled() {
        return false;
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void resetProgress() {

    }
}