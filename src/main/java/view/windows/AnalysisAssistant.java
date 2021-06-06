package view.windows;

import controler.IConfigurationControler;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.beans.*;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AnalysisAssistant extends ModalJFrameAbstract {

    // WIZARD
    private final IWizardPanel wizardPanel= new WizardPanel(ConfigurationUtils.getInstance()
            .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_WIZARD_PANEL_TITLE));;
    private final ICheckBoxPanel checkBoxPanel;
    private JPanel content = new JPanel();
    private final IManageTextDisplayPanel displayTextsList;
    private final IActionPanel filterTextActionPanel;
    private ManageTextFilter manageTextFilter;
    private IChooseLexicometricAnalyzePanel chooseLexicometricAnalyzePanel = new ChooseLexicometricAnalyzePanel(wizardPanel);
    private IActionPanel chooseAnalyzeActionPanel;
    private final ICheckBoxPanel checkBoxFieldsPanel;
    private final Map<String, Integer> fieldNumberCheckBoxMap = new HashMap<>();


    public AnalysisAssistant(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler, false);
        this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());
        this.checkBoxPanel = new CheckBoxPanel(1, true);
        this.displayTextsList = new DisplayTextsFilteredWithPagingPanel(configurationControler);
        this.filterTextActionPanel = new ActionPanel(1);
        this.checkBoxFieldsPanel = new CheckBoxPanel(getControler().getFieldConfigurationNameLabelWithoutMetaMap().size(), true);
//        this.chooseAnalyzeComboBox = new ComboBoxPanel("Analyse Lexicométrique", "Choix de l'analyse");
//        this.chooseAnalyzeComboBox.addAndSelectItem("Nombre de token");
//        this.chooseAnalyzeComboBox.addAndSelectItem("Lemmatisation et numéro type");
//        this.chooseAnalyzeComboBox.addAndSelectItem("Type token ratio");
//        this.chooseAnalyzeComboBox.addAndSelectItem("Fréquence");
//        this.chooseAnalyzeActionPanel = new ActionPanel(2);
//        this.chooseAnalyzeActionPanel.setStaticLabel("Action", Map.of(0, "Lancer l'analyse", 1, "Consulter les résultats"));
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
    }

    /**
     * Permet de créer la zone d'action du choix de l'analyse
     */
    private void createChooseAnalyzeAction() {
        this.chooseAnalyzeActionPanel = new ActionPanel(2);
        this.chooseAnalyzeActionPanel.setStaticLabel(getMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0, getMessage(Constants.WINDOW_START_ANALYSIS_START_BUTTON_LABEL),
                        1, getMessage(Constants.WINDOW_START_ANALYSIS_CONSULT_RESULTS_BUTTON_LABEL)));
    }

//    /**
//     * Permet de créer l'étape 2 de l'assistant
//     */
//    private void createStep_2() {
//        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE3),
//                false, true);
//        this.wizardPanel.addStep(Arrays.asList(informationStep, displayTextsList, filterTextActionPanel));
//    }
//
//    /**
//     * Permet de créer l'étape des stopWords
//     */
//    private void createStepTokenization() {
//        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
//                .titlePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE))
//                .defaultProfile(getControler().getLexicometricDefaultProfile())
//                .titleTablePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE))
//                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.TOKENIZATION, TokenizationHierarchicalEditEnum.BASE))
//                .lexicometricEditEnum(LexicometricEditEnum.TOKENIZATION)
//                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
//                .build();
//        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
//        profileWithTable.setInterfaceForTableAndAddButton(0,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
//                false, true);
//        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
//    }
//
//    /**
//     * Permet de créer l'étape pour la lemmatization
//     */
//    private void createStepLemmatization() {
//        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
//                .titlePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE))
//                .defaultProfile(getControler().getLexicometricDefaultProfile())
//                .titleTablePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE))
//                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.LEMMATIZATION, LemmatizationHierarchicalEditEnum.BASE))
//                .lexicometricEditEnum(LexicometricEditEnum.LEMMATIZATION)
//                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
//                .build();
//        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
//        profileWithTable.setInterfaceForTableAndAddButton(0,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        profileWithTable.setInterfaceForTableAndAddButton(1,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
//                false, true);
//        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
//    }
//
//    /**
//     * Permet de créer l'étape pour la lemmatization
//     */
//    private void createStepLemmatizationByGrammaticalCategory() {
//        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
//                .titlePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE))
//                .defaultProfile(getControler().getLexicometricDefaultProfile())
//                .titleTablePanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE))
//                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY, LemmatizationByGrammaticalCategoryHierarchicalEditEnum.CATEGORY))
//                .lexicometricEditEnum(LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY)
//                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
//                .build();
//        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
//        profileWithTable.setInterfaceForTableAndAddButton(0,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        profileWithTable.setInterfaceForTableAndAddButton(1,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        profileWithTable.setInterfaceForTableAndAddButton(2,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
//        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
//                false, true);
//        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
//    }
//
//    /**
//     * Permet de se procurer le tableau d'édition préparer pour la gestion avec des mots et recherche avec filtre sur contains en text
//     * @return le tableau d'édition préparer pour la gestion avec des mots et recherche avec filtre sur contains en text
//     */
//    private Function2<IRootTable, Consumer<?>, ITableWithFilterAndEditPanel> getTableWithTextFilterAndEditPanelFunction() {
//        return (x, v) -> new TableWithFilterAndEditPanel<String>(StringUtils.EMPTY, x.getHeaderLabel(), v,
//                Comparator.comparing(StringUtils::stripAccents),
//                s -> StringUtils.isNotBlank(s.getStringValue()),
//                (s, f) -> s.toLowerCase(Locale.ROOT).contains(f.getStringValue()), getTableTextFilterPanel());
//    }
//
//    /**
//     * Permet de se procurer le table filter pour filtrer par rapport à une chaine de caractère
//     * @return le table filter pour filtrer par rapport à une chaine de caractère
//     */
//    private ITableFilterPanel getTableTextFilterPanel() {
//        TableFilterTextPanel tableFilterPanel = new TableFilterTextPanel();
//        tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_LABEL)));
//        return tableFilterPanel;
//    }
//
//    /**
//     * Permet de créer l'étape pour la configuration des fréquences
//     */
//    private void createStepFrequency() {
//        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE1),
//                true, true);
//
//        IActionPanel actionPanel = new ActionPanel(1);
//        actionPanel.setStaticLabel("Fréquence", Map.of(0, "Fréquence"));
//        actionPanel.addAction(0, e -> new GephiTest().script());
//
//        this.wizardPanel.addStep(Arrays.asList(informationStep, actionPanel));
//    }

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

//    /**
//     * Permet de créer les radio boutons
//     */
//    private void createCheckBox() {
//        Map<Integer, String> mapCheckBox = new HashMap<>();
//        mapCheckBox.put(0, ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TYPE_OPTION_TOKEN));
//        this.checkBoxPanel.setStaticLabel(ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TYPE_PANEL_TITLE), mapCheckBox);
//    }

//    /**
//     * Permet de mettre à jour l'état d'une étape en fonction d'un radio bouton
//     * @param idButton identifiant du radio bouton
//     */
//    private void updateStateOfStep(Integer idButton) {
//        Long idStep = this.checkBoxIdStepMap.get(idButton);
//        Boolean state = this.checkBoxPanel.getCheckBoxIsChecked(idButton);
//        this.wizardPanel.setStateOfStep(idStep, state);
//    }
//
//    /**
//     * Permet de désactiver les étapes non coché par défaut
//     */
//    private void disableStepOnStart() {
//        updateStateOfStep(0);
//        updateStateOfStep(1);
//        updateStateOfStep(2);
//    }

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
            this.fieldNumberCheckBoxMap.put(entry.getKey(), current);
            current++;
        }
        this.checkBoxFieldsPanel.setStaticLabel(getMessage(Constants.WINDOW_START_ANALYSIS_FIELD_MATERIAL_PANEL_TITLE), labels);
    }

}
