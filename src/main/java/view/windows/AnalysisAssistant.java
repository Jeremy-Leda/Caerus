package view.windows;

import controler.IConfigurationControler;
import model.analyze.constants.LexicometricAnalysisType;
import view.abstracts.ModalJFrameAbstract;
import view.beans.EditTable;
import view.beans.PictureTypeEnum;
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
    private IActionPanel actionPanel;
    private JPanel content = new JPanel();
    private Boolean reconstructInProgress;

    public AnalysisAssistant(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler);
        this.wizardPanel = new WizardPanel(ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_WIZARD_PANEL_TITLE));
        this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());
        this.checkBoxPanel = new CheckBoxPanel(3, true);
        this.reconstructInProgress = Boolean.FALSE;

        createCheckBox();
        createStep_0();
        createStep_1();
        createWindow();
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
        this.checkBoxPanel.addConsumerOnChange(0, e -> constructEditDataForAnalyze());
        this.checkBoxPanel.addConsumerOnChange(1, e -> constructEditDataForAnalyze());
        this.checkBoxPanel.addConsumerOnChange(2, e -> constructEditDataForAnalyze());
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

    private void constructEditDataForAnalyze() {
        if (!reconstructInProgress) {
            reconstructInProgress = Boolean.TRUE;
            this.wizardPanel.removeAll();
            createStep_0();
            createStep_1();
            if (this.checkBoxPanel.getCheckBoxIsChecked(0)) {
                //LexicometricConfigurationView lexicometricAnalysis = getControler().getLexicometricAnalysis();
                IProfileWithTable profileWithTable = new ProfileWithTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE),
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE));
                profileWithTable.createTable(Map.of(0, ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL)));



                //profileWithTable.fillTable(0, lexicometricAnalysis.getTokenization().getWords());
                profileWithTable.setGetListFromProfileFunction(getTokenizationFunction());
                profileWithTable.fillProfileSet(LexicometricAnalysisType.TOKENIZATION.getProfileSet(), getControler().getLexicometricDefaultProfile());
                profileWithTable.setInterfaceForTableAndAddButton(0,
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL));

//                ITableWithFilterAndEditPanel tableWithFilterAndEditPanel = new TableWithFilterAndEditPanel(ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE),
//                        ConfigurationUtils.getInstance()
//                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL));
//                tableWithFilterAndEditPanel.fillTable(lexicometricAnalysis.getTokenization().getWords());
//                tableWithFilterAndEditPanel.fillProfileSet(LexicometricAnalysisType.TOKENIZATION.getProfileSet());
//                tableWithFilterAndEditPanel.setInterfaceForAddButton(
//                        ConfigurationUtils.getInstance()
//                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_INFORMATION_MESSAGE),
//                        ConfigurationUtils.getInstance()
//                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_ADD_TEXT_LABEL)
//                );
                //IEditListConfiguration editListConfiguration = new EditListConfiguration("Editer les token", "Token", lexicometricAnalysis.getTokenization().getWords(), 1);

                addStepEditData(
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
                        profileWithTable);
            }
            if (this.checkBoxPanel.getCheckBoxIsChecked(1)) {
                //LexicometricConfigurationView lexicometricAnalysis = getControler().getLexicometricAnalysis();
                IProfileWithTable profileWithTable = new ProfileWithTablePanel(ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_PANEL_TITLE),
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_PANEL_TITLE));
                profileWithTable.createTable(Map.of(0, ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL),
                        1, ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_TOKEN_TABLE_HEADER_LABEL)));
                profileWithTable.setGetListFromProfileFunction(getBaseLemmeFunction());
                profileWithTable.fillProfileSet(LexicometricAnalysisType.LEMMATIZATION.getProfileSet(), getControler().getLexicometricDefaultProfile());
                profileWithTable.setReferenceFromSourceFunction(0,1, getLemmeFromBaseFunction());
                profileWithTable.setSaveDataInMemory(getSaveDataConsumer(LexicometricAnalysisType.LEMMATIZATION), List.of(0,1).stream().collect(Collectors.toCollection(LinkedList::new)));
//                profileWithTable.fillTable(0, lexicometricAnalysis.getTokenization().getWords());
//                profileWithTable.fillTable(1, Collections.emptyList());
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
                addStepEditData(
                        ConfigurationUtils.getInstance()
                                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_TOKEN),
                        profileWithTable);
                //addStepEditData("Panel Lemmatisations", "Editer les lemmes");
            }
            if (this.checkBoxPanel.getCheckBoxIsChecked(2)) {
                addStepEditData("Panel Fréquence", "Configurer les fréquences");
            }
            this.wizardPanel.reconstructWizard();
        }
        reconstructInProgress = Boolean.FALSE;
    }

    /**
     * Permet de se procurer la fonction qui retourne la liste des token en fonction du profile
     * @return La fonction qui retourne la liste des token en fonction du profile
     */
    private Function<String, Collection<String>> getTokenizationFunction() {
        return profile -> getControler().getLexicometricAnalysis(profile).getTokenization().getWords();
    }

    /**
     * Permet de se procurer la fonction qui retourne la liste des bases de lemme en fonction du profile
     * @return La fonction qui retourne la liste des des bases de lemme en fonction du profile
     */
    private Function<String, Collection<String>> getBaseLemmeFunction() {
        return profile -> getControler().getLexicometricAnalysis(profile).getLemmatization().getBaseListWordsMap().keySet();
    }

    /**
     * Permet de se procurer la fonction qui retourne la liste des lemmes en fonction de la base
     * @return La fonction qui retourne la liste des lemmes en fonction de la base
     */
    private BiFunction<String, String, Collection<String>> getLemmeFromBaseFunction() {
        return (profile,base) -> getControler().getLexicometricAnalysis(profile).getLemmatization().getBaseListWordsMap().get(base);
    }

    /**
     * Permet de se procurer le consumer pour sauvegarder les datas en mémoire
     * @param lexicometricAnalysisType le type d'analyse
     * @return le consumer
     */
    private Consumer<EditTable> getSaveDataConsumer(LexicometricAnalysisType lexicometricAnalysisType) {
        return editTable -> {
            editTable.setLexicometricAnalysisType(lexicometricAnalysisType);
            getControler().saveLexicometricAnalysis(editTable);
        };
    }


    private void addStepEditData(String titlePanel, String buttonLabel) {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_MESSAGE_ETAPE1),
                true, true);

        IActionPanel actionPanel = new ActionPanel(1);
        actionPanel.setStaticLabel(titlePanel, Map.of(0, buttonLabel));

        this.wizardPanel.addStep(Arrays.asList(informationStep, actionPanel));
    }

    private void addStepEditData(String information, IProfileWithTable profileWithTable) {
        IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
                ConfigurationUtils.getInstance()
                        .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_INFORMATION_PANEL_TITLE),
                information,
                false, true);

        this.wizardPanel.addStep(Arrays.asList(informationStep, profileWithTable));
    }



}
