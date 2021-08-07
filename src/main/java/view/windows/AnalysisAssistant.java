package view.windows;

import controler.IConfigurationControler;
import io.vavr.Tuple2;
import model.analyze.LexicometricAnalysis;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.*;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class AnalysisAssistant extends ModalJFrameAbstract {

    // WIZARD
    private final IWizardPanel wizardPanel= new WizardPanel(ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_WIZARD_PANEL_TITLE));
    //private final ICheckBoxPanel checkBoxPanel;
    private JPanel content = new JPanel();
    private final IManageTextDisplayPanel displayTextsList;
    private final IActionPanel filterTextActionPanel;
    private ManageTextFilter manageTextFilter;
    private IChooseLexicometricAnalyzePanel chooseLexicometricAnalyzePanel = new ChooseLexicometricAnalyzePanel(wizardPanel);
    private IActionPanel chooseAnalyzeActionPanel;
    private final ICheckBoxPanel checkBoxFieldsPanel;


    public AnalysisAssistant(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler, false);
        this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());
        //this.checkBoxPanel = new CheckBoxPanel(1, true);
        this.displayTextsList = new DisplayTextsFilteredWithPagingPanel(configurationControler);
        this.filterTextActionPanel = new ActionPanel(1);
        this.checkBoxFieldsPanel = new CheckBoxPanel(getControler().getFieldConfigurationNameLabelWithoutMetaMap().size(), true);
        super.addActionOnClose(closeAutomaticallyOtherChildrenWindow());
        createWindow();
    }

    @Override
    public void initComponents() {
        refreshActionTextFilterPanel();
        refreshLabelCheckBoxFields();
        createChooseAnalyzeAction();
        createStep_0();
        createStep_1();
        createStep_2();
        createStep_3();
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(wizardPanel.getJPanel());

    }

    @Override
    public JPanel getContent() {
        return this.content;
    }

    @Override
    public String getWindowName() {
        return "Window for analysis assistant";
    }


    /**
     * Permet de créer l'étape 0 de l'assistant
     */
    private void createStep_0() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE1),
                true, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep));
    }

    /**
     * Permet de créer l'étape 1 de l'assistant
     * Choix des textes à analyser
     */
    private void createStep_1() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE2),
                false, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, displayTextsList, filterTextActionPanel));
    }

    /**
     * Permet de créer l'étape 2 de l'assistant
     * Choix des champs à analyser
     */
    private void createStep_2() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE3),
                false, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, checkBoxFieldsPanel));
    }

    /**
     * Permet de créer l'étape 3 de l'assistant
     * Choix de l'analyse à effectuer
     */
    private void createStep_3() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE4),
                false, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, chooseLexicometricAnalyzePanel, chooseAnalyzeActionPanel));
        //FIXME rajouter l'activation que une fois que tout a été sélectionné et est ok pour lancer l'analyse
        this.wizardPanel.addEnableDisableConsumer(3L, s -> chooseAnalyzeActionPanel.setEnabled(1, chooseLexicometricAnalyzePanel.isValidForStartAnalysis()));
    }

    /**
     * Permet de créer la zone d'action du choix de l'analyse
     */
    private void createChooseAnalyzeAction() {
        this.chooseAnalyzeActionPanel = new ActionPanel(3);
        this.chooseAnalyzeActionPanel.setStaticLabel(getMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0, getMessage(Constants.WINDOW_START_ANALYSIS_HELP_BUTTON_LABEL),
                        1, getMessage(Constants.WINDOW_START_ANALYSIS_START_BUTTON_LABEL),
                        2, getMessage(Constants.WINDOW_START_ANALYSIS_CONSULT_RESULTS_BUTTON_LABEL)));
        // FIXME passer en progress
        this.chooseAnalyzeActionPanel.setIconButton(0, PictureTypeEnum.WARNING);
        this.chooseAnalyzeActionPanel.setEnabled(1, false);
        this.chooseAnalyzeActionPanel.setEnabled(2, false);
        this.chooseAnalyzeActionPanel.addAction(0, e -> {
            this.chooseAnalyzeActionPanel.setEnabled(0, false);
            UserInformation userInformation = new UserInformation(getMessage(Constants.WINDOW_HELP_USER_TITLE),
                    getControler(),
                    PictureTypeEnum.INFORMATION,
                    getMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_OPTIONALS_LISTE_MESSAGE),
                    false);
            userInformation.addActionOnClose(x -> this.chooseAnalyzeActionPanel.setEnabled(0, true));
        });
        this.chooseAnalyzeActionPanel.addAction(1, e -> executeOnServerWithProgressView(() -> {
            chooseLexicometricAnalyzePanel.getAnalyzeToLaunch().getBiConsumerAnalysis().accept(getControler(), getLexicometricAnalyzeCmd());
            this.chooseAnalyzeActionPanel.setEnabled(2, true);
        }, LexicometricAnalysis.getInstance(), false, false));
        this.chooseAnalyzeActionPanel.addAction(2, e -> {
            AtomicReference<AnalysisResultDisplay> analysisResultDisplayAtomicReference = new AtomicReference<>();
            LexicometricAnalyzeCmd lexicometricAnalyzeCmd = getLexicometricAnalyzeCmd();
            executeOnServerWithProgressView(() -> analysisResultDisplayAtomicReference.set(chooseLexicometricAnalyzePanel.getAnalyzeToLaunch().getFunctionDisplayResult()
                    .apply(lexicometricAnalyzeCmd)), LexicometricAnalysis.getInstance(), false, false);
            new AnalysisTokenResultWindow(getControler(), analysisResultDisplayAtomicReference.get(),
                    lexicometricAnalyzeCmd,
                    chooseLexicometricAnalyzePanel.getAnalyzeToLaunch().getLexicometricAnalyzeTypeEnum());
        });
    }

    /**
     * Permet de rafraichir l'affichage pour le bouton de filtre des textes
     * Etape 2
     */
    private void refreshActionTextFilterPanel() {
        Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
        messageButtonMap.put(0,
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_FILTERS_BUTTON_LABEL));
        this.filterTextActionPanel.setStaticLabel(StringUtils.EMPTY, messageButtonMap);
        this.filterTextActionPanel.addAction(0, e -> {
            manageTextFilter = new ManageTextFilter(getControler(), v -> {
                displayTextsList.refresh();
                repack();
            });
        });
    }

    /**
     * Consumer pour le changement de page de l'assistant
     *
     * @return le consumer
     */
    private Consumer<?> changeConsumerForWizard() {
        return v -> {
            repack(true);
        };
    }

    /**
     * Consumer pour rattacher la fermeture de la fenêtre fille si présente
     *
     * @return
     */
    private Consumer<Void> closeAutomaticallyOtherChildrenWindow() {
        return (v) -> {
            if (null != manageTextFilter) {
                manageTextFilter.closeFrame();
            }
            this.displayTextsList.close();
        };
    }

    /**
     * Met à jour le libellé des check box
     */
    private void refreshLabelCheckBoxFields() {
        Integer current = 0;
        Map<Integer, String> labels = new HashMap<>();
        for (Map.Entry<String, String> entry : getControler().getFieldConfigurationNameLabelWithoutMetaMap().entrySet()) {
            labels.put(current, entry.getValue());
            current++;
        }
        this.checkBoxFieldsPanel.setStaticLabel(getMessage(Constants.WINDOW_START_ANALYSIS_FIELD_MATERIAL_PANEL_TITLE), labels);
    }

    /**
     * Permet de se procurer la commande de lancement de l'analyse lexicométrique
     * @return la commande de lancement de l'analyse lexicométrique
     */
    private LexicometricAnalyzeCmd getLexicometricAnalyzeCmd() {
        Map<LexicometricEditEnum, String> preTreatmentListLexicometricMap = new HashMap<>();
        Optional<ILexicometricListApplyChoosePanel> optionalILexicometricListApplyChoosePanel = this.chooseLexicometricAnalyzePanel.getOptionalILexicometricListApplyChoosePanel();
        if (optionalILexicometricListApplyChoosePanel.isPresent()) {
            Optional<Tuple2<LexicometricEditEnum, String>> stopWordEntry = getOptionalEntryForLexicometricPanel(optionalILexicometricListApplyChoosePanel.get().getStopWordConfiguration());
            stopWordEntry.ifPresent(s -> preTreatmentListLexicometricMap.put(s._1, s._2));
            Optional<Tuple2<LexicometricEditEnum, String>> lemmeEntry = getOptionalEntryForLexicometricPanel(optionalILexicometricListApplyChoosePanel.get().getLemmatizationConfiguration());
            lemmeEntry.ifPresent(s -> preTreatmentListLexicometricMap.put(s._1, s._2));
            Optional<Tuple2<LexicometricEditEnum, String>> properNounEntry = getOptionalEntryForLexicometricPanel(optionalILexicometricListApplyChoosePanel.get().getProperNounConfiguration());
            properNounEntry.ifPresent(s -> preTreatmentListLexicometricMap.put(s._1, s._2));
        }
        Set<String> fieldToAnalyzeSet = new HashSet<>();
        Integer currentIndex = 0;
        for (Map.Entry<String, String> entry : getControler().getFieldConfigurationNameLabelWithoutMetaMap().entrySet()) {
            if (this.checkBoxFieldsPanel.getCheckBoxIsChecked(currentIndex)) {
                fieldToAnalyzeSet.add(entry.getKey());
            }
            currentIndex++;
        }
        return new LexicometricAnalyzeCmdBuilder()
                .keyTextFilteredList(getControler().getFilteredTextKeyList())
                .preTreatmentListLexicometricMap(preTreatmentListLexicometricMap)
                .fieldToAnalyzeSet(fieldToAnalyzeSet)
                .build();
    }

    /**
     * Permet de se procurer le tuple pour la récupération des informations sur les traitements lexicométrique
     * @param lexicometricConfigurationChoosePanel panel dont on souhaite récupérer les informations
     * @return le tuple pour la récupération des informations sur les traitements lexicométrique
     */
    private Optional<Tuple2<LexicometricEditEnum, String>> getOptionalEntryForLexicometricPanel(ILexicometricConfigurationChoosePanel lexicometricConfigurationChoosePanel) {
        Optional<LexicometricEditEnum> lexicometricEditEnum = Optional.ofNullable(lexicometricConfigurationChoosePanel.getLexicometricEditEnum());
        if (lexicometricEditEnum.isPresent() && StringUtils.isNotBlank(lexicometricConfigurationChoosePanel.getProfile())) {
            return Optional.of(new Tuple2(lexicometricEditEnum.get(), lexicometricConfigurationChoosePanel.getProfile()));
        }
        return Optional.empty();
    }

}
