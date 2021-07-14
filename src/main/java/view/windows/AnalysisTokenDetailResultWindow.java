package view.windows;

import controler.IConfigurationControler;
import io.vavr.collection.Stream;
import model.analyze.lexicometric.beans.LexicometricAnalyzeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.LexicometricAnalyzeCmd;
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
    private ITextHighlightPanel textHighlightPanel = new TextHighlightPanel(getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_DISPLAY_FIELD_PANEL_TITLE));
    private IActionPanel actionPanel = new ActionPanel(3);
    private IActionPanel navigationPanel = new ActionPanel(2);
    private final ITableAnalysisPanel tableAnalysisPanel;
    private final ILabelsPanel labelsPanel;
    private int current = 0;
    private final LexicometricAnalyzeCmd cmd;
    private final LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum;
    private final Map<String, String> mapField;
    private Optional<ReadCorpus> optionalReadCorpusWindow = Optional.empty();
    private Optional<ReadText> optionalReadTextWindow = Optional.empty();
    private Optional<AnalysisProperNounAddWindow> optionalAnalysisProperNounAddWindow = Optional.empty();
    private final Consumer<?> consumerRelaunchBase;

    /**
     * Constructeur
     * @param configurationControler controller
     */
    public AnalysisTokenDetailResultWindow(IConfigurationControler configurationControler, LexicometricAnalyzeCmd cmd, LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum, Consumer<?> relaunchAnalyzeConsumer) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PANEL_TITLE), configurationControler, false);
        this.consumerRelaunchBase = relaunchAnalyzeConsumer;
        radioButtonPanel = new RadioButtonPanel(cmd.getFieldToAnalyzeSet().size());
        LinkedList<String> headerLinkedList = new LinkedList<>();
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL));
        headerLinkedList.add(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL));
        this.tableAnalysisPanel = new TableAnalysisPanel(getMessage(Constants.WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, List.of(String.class, Long.class), new LinkedList<>());
        this.cmd = cmd;
        this.lexicometricAnalyzeTypeEnum = lexicometricAnalyzeTypeEnum;
        this.mapField = getControler().getFieldConfigurationNameLabelWithoutMetaMap();
        this.labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        this.addActionOnClose(e -> closeAutomaticallyAllWindows());
        createWindow();
    }

    /**
     * Permet d'initialiser les boutons radio
     * @param cmd commande
     */
    private void initRadioButton(LexicometricAnalyzeCmd cmd) {
        Map<Integer, String> radioButtonMap = Stream.ofAll(cmd.getFieldToAnalyzeSet()).zipWithIndex()
                .collect(Collectors.toMap(t -> t._2, t ->  mapField.get(t._1)));
        this.radioButtonPanel.setStaticLabel(
                getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_CHOOSE_FIELD_PANEL_TITLE), radioButtonMap);
        this.radioButtonPanel.setDefaultSelectedRadioButton(0);
    }

    /**
     * Permet d'initialiser le panel des actions
     *
     */
    private void initActionPanel() {
        Map<Integer, String> labelMap = Map.of(0, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_ACTION_VIEW_META_BUTTON_LABEL),
                1, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_ACTION_VIEW_DATA_BUTTON_LABEL),
                2, getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_PROPER_NOUN_BUTTON_LABEL));
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL), labelMap);
        this.actionPanel.addAction(0, e -> {
            actionPanel.setEnabled(0, false);
            optionalReadCorpusWindow = Optional.of(new ReadCorpus(getMessage(WINDOW_READ_CORPUS_TITLE), getControler(), false, this.cmd.getKeyTextFilteredList().get(current)));
            optionalReadCorpusWindow.get().addActionOnClose(s -> actionPanel.setEnabled(0, true));
        });
        this.actionPanel.addAction(1, e -> {
            actionPanel.setEnabled(1, false);
            optionalReadTextWindow = Optional.of(new ReadText(getMessage(WINDOW_READ_TEXT_TITLE), getControler(), false, this.cmd.getKeyTextFilteredList().get(current)));
            optionalReadTextWindow.get().addActionOnClose(s -> actionPanel.setEnabled(1, true));
        });
        this.actionPanel.addAction(2, e -> {
            actionPanel.setEnabled(2, false);
            optionalAnalysisProperNounAddWindow = Optional.of(new AnalysisProperNounAddWindow(getControler(), getPotentialProperNounCollection(), getRelaunchAnalyzeConsumer(consumerRelaunchBase)));
            optionalAnalysisProperNounAddWindow.get().addActionOnClose(s -> actionPanel.setEnabled(2, true));
        });
    }

    /**
     * Permet d'initialiser le panel de navigation
     */
    private void initNavigationPanel() {
        this.navigationPanel.addAction(0, x -> {
            current--;
            loadText();
        });
        this.navigationPanel.addAction(1, x -> {
            current++;
            loadText();
        });
    }

    @Override
    public void initComponents() {
        initRadioButton(cmd);
        initActionPanel();
        initNavigationPanel();
        this.tableAnalysisPanel.addConsumerOnSelectedChangeForWord(getConsumerForHighlight());
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.radioButtonPanel.getJPanel());
        content.add(this.textHighlightPanel.getJPanel());
        content.add(this.labelsPanel.getJPanel());
        content.add(this.tableAnalysisPanel.getJPanel());
        content.add(this.actionPanel.getJPanel());
        content.add(this.navigationPanel.getJPanel());
        loadText();
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
     * Permet de rafraichir la navigation
     */
    private void refreshNavigationDisplay() {
        Map<Integer, String> labelMap = Map.of(0, getMessage(WINDOW_WIZARD_NAVIGATION_PREVIOUS_BUTTON_LABEL),
                1, getMessage(WINDOW_WIZARD_NAVIGATION_NEXT_BUTTON_LABEL));
        this.navigationPanel.setStaticLabel(String.format(getMessage(WINDOW_RESULT_DETAIL_TOKEN_ANALYSIS_NAVIGATION_LABEL),
                this.current + 1, this.cmd.getKeyTextFilteredList().size()), labelMap);
        this.navigationPanel.setEnabled(0, current > 0);
        this.navigationPanel.setEnabled(1, current + 1 < this.cmd.getKeyTextFilteredList().size());
    }

    /**
     * Permet de charger le texte
     */
    private void loadText() {
        tableAnalysisPanel.clearSelection();
        String key = this.cmd.getKeyTextFilteredList().get(current);
        optionalReadCorpusWindow.ifPresent(readCorpus -> readCorpus.setKeyText(key));
        optionalReadTextWindow.ifPresent(readText -> readText.setKeyText(key));
        AnalysisResultDisplay analysisResultDisplay = lexicometricAnalyzeTypeEnum.getAnalysisResultDisplayFunction().apply(List.of(key));
        this.labelsPanel.setLabel(0, getMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL), String.valueOf(analysisResultDisplay.getNbToken()));
        this.labelsPanel.setLabel(1, getMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL), String.valueOf(analysisResultDisplay.getNbOccurrency()));
        this.tableAnalysisPanel.updateAnalysisResult(analysisResultDisplay.toAnalysisTokenRowList());
        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
        this.textHighlightPanel.setText(StringUtils.trim(getControler().getValueFromKeyTextAndFieldWithAnalyzeTreatment(key, field, cmd.toPreTreatmentServerMap())));
        refreshNavigationDisplay();
    }

    /**
     * Permet de se procurer la liste des noms propres potentiel
     * @return la liste des noms propres potentiels
     */
    private Collection<String> getPotentialProperNounCollection() {
        String key = this.cmd.getKeyTextFilteredList().get(current);
        String field = mapField.entrySet().stream().filter(s -> s.getValue().equals(this.radioButtonPanel.getSelectedLabel())).findFirst().get().getKey();
        return getControler().getPotentialProperNounCollection(key, field, cmd.toPreTreatmentServerMap());
    }

    /**
     * Permet de se procurer le consumer pour surligner
     * @return le consumer
     */
    private Consumer<List<Object>> getConsumerForHighlight() {
        return result -> {
            String word = String.valueOf(result.get(0));
            this.textHighlightPanel.highlightWord(word, Color.CYAN);
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
     * @return le consumer qui relance l'analyse
     */
    private Consumer<?> getRelaunchAnalyzeConsumer(Consumer<?> consumerBase) {
        return x -> {
            consumerBase.accept(null);
            loadText();
        };
    }
}
