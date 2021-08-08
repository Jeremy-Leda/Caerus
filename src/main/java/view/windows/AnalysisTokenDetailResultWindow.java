package view.windows;

import controler.IConfigurationControler;
import io.vavr.collection.Stream;
import model.analyze.LexicometricAnalysis;
import model.analyze.cmd.AnalysisDetailResultDisplayCmd;
import model.analyze.cmd.AnalysisDetailResultDisplayCmdBuilder;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisDetailResultDisplay;
import view.analysis.beans.AnalysisGroupDisplay;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.LexicometricAnalyzeCmd;
import view.beans.LexicometricEditEnum;
import view.components.DragAndDropCloseableTabbedPane;
import view.interfaces.*;
import view.panel.ActionPanel;
import view.panel.LabelsPanel;
import view.panel.RadioButtonPanel;
import view.panel.TextHighlightPanel;
import view.panel.analysis.TableAnalysisPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre pour l'affichage des détails pour les tokens
 *
 */
public class AnalysisTokenDetailResultWindow extends ModalJFrameAbstract {

    private JPanel content = new JPanel();
    private IRadioButtonPanel radioButtonPanel;
    private IActionPanel actionPanel = new ActionPanel(3);
    private final Map<String, String> mapField;
    private Optional<ReadCorpus> optionalReadCorpusWindow = Optional.empty();
    private Optional<ReadText> optionalReadTextWindow = Optional.empty();
    private Optional<AnalysisProperNounAddWindow> optionalAnalysisProperNounAddWindow = Optional.empty();
    private final Consumer<?> consumerRelaunchBase;
    private final Map<JComponent, AnalysisDetailResultDisplay> componentAnalysisDetailResultDisplayMap = new HashMap<>();
    private final Map<AnalysisDetailResultDisplay, ITextHighlightPanel> analysisDetailResultDisplayITextHighlightPanelHashMap = new HashMap<>();
    private final LinkedList<String> headerLinkedList = new LinkedList<>();
    private final DragAndDropCloseableTabbedPane tabbedPane = new DragAndDropCloseableTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
    private final Map<LexicometricConfigurationEnum, String> lexicometricConfigurationEnumStringMap;


    /**
     * Constructeur
     * @param configurationControler controller
     */
    public AnalysisTokenDetailResultWindow(IConfigurationControler configurationControler,
                                           LexicometricAnalyzeCmd cmd,
                                           LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                                           Consumer<?> relaunchAnalyzeConsumer,
                                           Set<String> selectedWords,
                                           Set<String> keySet) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PANEL_TITLE), configurationControler, false);
        this.consumerRelaunchBase = relaunchAnalyzeConsumer;
        this.mapField = getControler().getFieldConfigurationNameLabelWithoutMetaMap();
        this.lexicometricConfigurationEnumStringMap = cmd.toPreTreatmentServerMap();
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
        reload(keyFilteredTextList, lexicometricAnalyzeTypeEnum, cmd.getFieldToAnalyzeSet());
        this.tabbedPane.addChangeListener(s -> {
            optionalReadCorpusWindow.ifPresent(readCorpus -> readCorpus.setKeyText(getSelectedKeyText()));
            optionalReadTextWindow.ifPresent(readText -> readText.setKeyText(getSelectedKeyText()));
            optionalAnalysisProperNounAddWindow.ifPresent(analysisProperNounAddWindow -> analysisProperNounAddWindow.setProperNounSetOfText(getPotentialProperNounCollection()));
            reloadTextHighlightPanel();
            repack();
        });
        this.tabbedPane.setConsumerToRemove(c -> componentAnalysisDetailResultDisplayMap.remove(c));
        this.addActionOnClose(e -> closeAutomaticallyAllWindows());
        createWindow();
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
                2, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PROPER_NOUN_BUTTON_LABEL));
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL), labelMap);
        this.actionPanel.addAction(0, e -> {
            actionPanel.setEnabled(0, false);
            optionalReadCorpusWindow = Optional.of(new ReadCorpus(getMessage(WINDOW_READ_CORPUS_TITLE), getControler(), false, getSelectedKeyText()));
            optionalReadCorpusWindow.get().addActionOnClose(s -> actionPanel.setEnabled(0, true));
        });
        this.actionPanel.addAction(1, e -> {
            actionPanel.setEnabled(1, false);
            optionalReadTextWindow = Optional.of(new ReadText(getMessage(WINDOW_READ_TEXT_TITLE), getControler(), false, getSelectedKeyText()));
            optionalReadTextWindow.get().addActionOnClose(s -> actionPanel.setEnabled(1, true));
        });
        this.actionPanel.addAction(2, e -> {
            actionPanel.setEnabled(2, false);
            optionalAnalysisProperNounAddWindow = Optional.of(new AnalysisProperNounAddWindow(getControler(), getPotentialProperNounCollection(),
                    getRelaunchAnalyzeConsumer(consumerRelaunchBase, keyFilteredTextList, lexicometricAnalyzeTypeEnum, cmd.getFieldToAnalyzeSet()),
                    cmd.getPreTreatmentListLexicometricMap().get(LexicometricEditEnum.PROPER_NOUN)));
            optionalAnalysisProperNounAddWindow.get().addActionOnClose(s -> actionPanel.setEnabled(2, true));
        });
    }
//
//    /**
//     * Permet d'initialiser le panel de navigation
//     */
//    private void initNavigationPanel() {
//        this.navigationPanel.addAction(0, x -> {
//            current--;
//            loadText();
//        });
//        this.navigationPanel.addAction(1, x -> {
//            current++;
//            loadText();
//        });
//    }

    @Override
    public void initComponents() {
//        initRadioButton(cmd);
//        initActionPanel();
//        initNavigationPanel();
//        this.tableAnalysisPanel.addConsumerOnSelectedChangeForWord(getConsumerForHighlight());
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(radioButtonPanel.getJPanel());
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

//    /**
//     * Permet de rafraichir la navigation
//     */
//    private void refreshNavigationDisplay() {
//        Map<Integer, String> labelMap = Map.of(0, getMessage(WINDOW_WIZARD_NAVIGATION_PREVIOUS_BUTTON_LABEL),
//                1, getMessage(WINDOW_WIZARD_NAVIGATION_NEXT_BUTTON_LABEL));
//        this.navigationPanel.setStaticLabel(String.format(getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_NAVIGATION_LABEL),
//                this.current + 1, this.keyFilteredTextList.size()), labelMap);
//        this.navigationPanel.setEnabled(0, current > 0);
//        this.navigationPanel.setEnabled(1, current + 1 < this.keyFilteredTextList.size());
//    }

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
        }, LexicometricAnalysis.getInstance(), false, false);
        return analysisDetailResultDisplaySet;
    }

//    /**
//     * Permet de charger le texte
//     */
//    private void loadText() {
//        tableAnalysisPanel.clearSelection();
//        String key = this.keyFilteredTextList.get(current);
//        optionalReadCorpusWindow.ifPresent(readCorpus -> readCorpus.setKeyText(key));
//        optionalReadTextWindow.ifPresent(readText -> readText.setKeyText(key));
//        AnalysisResultDisplay analysisResultDisplay = lexicometricAnalyzeTypeEnum.getAnalysisResultDisplayFunction().apply(List.of(key));
//        this.labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
//        this.labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
//        this.tableAnalysisPanel.updateAnalysisResult(analysisResultDisplay.toAnalysisTokenRowList());
//        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
//        this.textHighlightPanel.setText(StringUtils.trim(getControler().getValueFromKeyTextAndFieldWithAnalyzeTreatment(key, field, cmd.toPreTreatmentServerMap())));
//        refreshNavigationDisplay();
//    }

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
    private Consumer<Void> closeAutomaticallyAllWindows() {
        return (v) -> {
            optionalReadCorpusWindow.ifPresent(ModalJFrameAbstract::closeFrame);
            optionalReadTextWindow.ifPresent(ModalJFrameAbstract::closeFrame);
            optionalAnalysisProperNounAddWindow.ifPresent(ModalJFrameAbstract::closeFrame);
        };
    }

    /**
     * Permet de se procurer le consumer qui relance l'analyse
     * @param consumerBase consumer de base
     * @param keyFilteredTextList liste des clés
     * @param lexicometricAnalyzeTypeEnum le type d'analyse
     * @param fieldToAnalyzeSet le set des champs d'analyse
     * @return le consumer qui relance l'analyse
     */
    private Consumer<?> getRelaunchAnalyzeConsumer(Consumer<?> consumerBase,
                                                   List<String> keyFilteredTextList,
                                                   LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum,
                                                   Set<String> fieldToAnalyzeSet) {
        return x -> {
            consumerBase.accept(null);
            reload(keyFilteredTextList, lexicometricAnalyzeTypeEnum, fieldToAnalyzeSet);
        };
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
        tabbedPane.removeAll();
        Set<AnalysisDetailResultDisplay> detailResultSet = getDetailResultSet(keyFilteredTextList, lexicometricAnalyzeTypeEnum, fieldToAnalyzeSet);
        detailResultSet.forEach(this::addAnalysisDetailResultDisplay);
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
}
