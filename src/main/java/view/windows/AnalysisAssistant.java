package view.windows;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.LemmatizationHierarchicalEditEnum;
import view.beans.LexicometricEditEnum;
import view.beans.PictureTypeEnum;
import view.beans.TokenizationHierarchicalEditEnum;
import view.cmd.ProfilWithTableCmd;
import view.cmd.ProfilWithTableCmdBuilder;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalysisAssistant extends ModalJFrameAbstract {

    // WIZARD
    private final IWizardPanel wizardPanel;
    private final ICheckBoxPanel checkBoxPanel;
    private JPanel content = new JPanel();
    private final Map<Integer, Long> checkBoxIdStepMap = new HashMap<>();

    public AnalysisAssistant(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler);
        this.wizardPanel = new WizardPanel(ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_WIZARD_PANEL_TITLE));
        this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());
        this.checkBoxPanel = new CheckBoxPanel(3, true);

        fillCheckBoxIdStepMap();
        createCheckBox();
        createStep_0();
        createStep_1();
        createStepTokenization();
        createStepLemmatization();
        createStepFrequency();
        disableStepOnStart();
        createWindow();
    }

    /**
     * Permet de remplir la map permettant de connecter les étapes avec les boutons radio
     */
    private void fillCheckBoxIdStepMap() {
        this.checkBoxIdStepMap.put(0, 2L);
        this.checkBoxIdStepMap.put(1, 3L);
        this.checkBoxIdStepMap.put(2, 4L);
    }

    @Override
    public void initComponents() {
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
     */
    private void createStep_1() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE2),
                true, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, this.checkBoxPanel));
    }

    /**
     * Permet de créer l'étape des stopWords
     */
    private void createStepTokenization() {
        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
                .titlePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE))
                .defaultProfile(getControler().getLexicometricDefaultProfile())
                .titleTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE))
                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.TOKENIZATION, TokenizationHierarchicalEditEnum.BASE))
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd);

//        IProfileWithTable profileWithTable = new ProfileWithTablePanel(ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE));
//        profileWithTable.createTable(Map.of(0, ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL)));
//        profileWithTable.setGetListFromProfileFunction(getTokenizationFunction());
//        profileWithTable.fillProfileSet(LexicometricAnalysisType.TOKENIZATION.getProfileSet(), getControler().getLexicometricDefaultProfile());
        profileWithTable.setInterfaceForTableAndAddButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
                false, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
    }

    /**
     * Permet de créer l'étape pour la lemmatization
     */
    private void createStepLemmatization() {
        ProfilWithTableCmd profilWithTableCmd = new ProfilWithTableCmdBuilder()
                .titlePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE))
                .defaultProfile(getControler().getLexicometricDefaultProfile())
                .titleTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE))
                .lexicometricConfiguration(getControler().getLexicometricConfiguration(LexicometricEditEnum.LEMMATIZATION, LemmatizationHierarchicalEditEnum.BASE))
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd);
//        IProfileWithTable profileWithTable = new ProfileWithTablePanel(ConfigurationUtils.getInstance()
//                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE),
//                ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE));
//        profileWithTable.createTable(Map.of(0, ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL),
//                1, ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL)));
//        profileWithTable.setGetListFromProfileFunction(getBaseLemmeFunction());
//        profileWithTable.fillProfileSet(LexicometricAnalysisType.LEMMATIZATION.getProfileSet(), getControler().getLexicometricDefaultProfile());
//        profileWithTable.setReferenceFromSourceFunction(0,1, getLemmeFromBaseFunction());
//        profileWithTable.setSaveDataInMemory(getSaveDataConsumer(LexicometricAnalysisType.LEMMATIZATION), List.of(0,1).stream().collect(Collectors.toCollection(LinkedList::new)));
        profileWithTable.setInterfaceForTableAndAddButton(0,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
        profileWithTable.setInterfaceForTableAndAddButton(1,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
                false, true);
        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
    }

    /**
     * Permet de créer l'étape pour la configuration des fréquences
     */
    private void createStepFrequency() {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE1),
                true, true);

        IActionPanel actionPanel = new ActionPanel(1);
        actionPanel.setStaticLabel("Fréquence", Map.of(0, "Fréquence"));
        actionPanel.addAction(0, e -> new GephiTest().script());

        this.wizardPanel.addStep(Arrays.asList(informationStep, actionPanel));
    }

    /**
     * Permet de créer les radio boutons
     */
    private void createCheckBox() {
        Map<Integer, String> mapCheckBox = new HashMap<>();
        mapCheckBox.put(0, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_OPTION_TOKENIZATION));
        mapCheckBox.put(1, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_OPTION_LEMMATIZATION));
        mapCheckBox.put(2, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_OPTION_FREQUENCY));
        this.checkBoxPanel.setStaticLabel(ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_OPTION_PANEL_TITLE), mapCheckBox);
        this.checkBoxPanel.addConsumerOnChange(0, e -> updateStateOfStep(0));
        this.checkBoxPanel.addConsumerOnChange(1, e -> updateStateOfStep(1));
        this.checkBoxPanel.addConsumerOnChange(2, e -> updateStateOfStep(2));
    }

    /**
     * Permet de mettre à jour l'état d'une étape en fonction d'un radio bouton
     * @param idButton identifiant du radio bouton
     */
    private void updateStateOfStep(Integer idButton) {
        Long idStep = this.checkBoxIdStepMap.get(idButton);
        Boolean state = this.checkBoxPanel.getCheckBoxIsChecked(idButton);
        this.wizardPanel.setStateOfStep(idStep, state);
    }

    /**
     * Permet de désactiver les étapes non coché par défaut
     */
    private void disableStepOnStart() {
        updateStateOfStep(0);
        updateStateOfStep(1);
        updateStateOfStep(2);
    }

    /**
     * Consumer pour le changement de page de l'assistant
     *
     * @return le consumer
     */
    private Consumer<?> changeConsumerForWizard() {
        return v -> {
            repack();
        };
    }

//    /**
//     * Permet de se procurer la fonction qui retourne la liste des token en fonction du profile
//     * @return La fonction qui retourne la liste des token en fonction du profile
//     */
//    private Function<String, Collection<String>> getTokenizationFunction() {
//        return profile -> getControler().getLexicometricAnalysis(profile).getTokenization().getWords();
//    }
//
//    /**
//     * Permet de se procurer la fonction qui retourne la liste des bases de lemme en fonction du profile
//     * @return La fonction qui retourne la liste des des bases de lemme en fonction du profile
//     */
//    private Function<String, Collection<String>> getBaseLemmeFunction() {
//        return profile -> getControler().getLexicometricAnalysis(profile).getLemmatization().getBaseListWordsMap().keySet();
//    }
//
//    /**
//     * Permet de se procurer la fonction qui retourne la liste des lemmes en fonction de la base
//     * @return La fonction qui retourne la liste des lemmes en fonction de la base
//     */
//    private BiFunction<String, String, Collection<String>> getLemmeFromBaseFunction() {
//        return (profile,base) -> getControler().getLexicometricAnalysis(profile).getLemmatization().getBaseListWordsMap().get(base);
//    }
//
//    /**
//     * Permet de se procurer le consumer pour sauvegarder les datas en mémoire
//     * @param lexicometricAnalysisType le type d'analyse
//     * @return le consumer
//     */
//    private Consumer<EditTable> getSaveDataConsumer(LexicometricAnalysisType lexicometricAnalysisType) {
//        return editTable -> {
//            editTable.setLexicometricAnalysisType(lexicometricAnalysisType);
//            getControler().saveLexicometricAnalysis(editTable);
//        };
//    }

}
