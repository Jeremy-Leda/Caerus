package view.windows;

import controler.IConfigurationControler;
import io.vavr.Function2;
import io.vavr.Function3;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import org.apache.commons.lang3.StringUtils;
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
                .lexicometricEditEnum(LexicometricEditEnum.TOKENIZATION)
                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
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
                .lexicometricEditEnum(LexicometricEditEnum.LEMMATIZATION)
                .tableWithFilterAndEditPanelFunction(getTableWithTextFilterAndEditPanelFunction())
                .build();
        IProfileWithTable profileWithTable = new ProfileWithTablePanel(profilWithTableCmd, getControler());
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
     * Permet de se procurer le tableau d'édition préparer pour la gestion avec des mots et recherche avec filtre sur contains en text
     * @return le tableau d'édition préparer pour la gestion avec des mots et recherche avec filtre sur contains en text
     */
    private Function2<IRootTable, Consumer<?>, ITableWithFilterAndEditPanel> getTableWithTextFilterAndEditPanelFunction() {
        return (x, v) -> new TableWithFilterAndEditPanel<String>(StringUtils.EMPTY, x.getHeaderLabel(), v,
                Comparator.comparing(StringUtils::stripAccents),
                s -> StringUtils.isNotBlank(s.getStringValue()),
                (s, f) -> s.toLowerCase(Locale.ROOT).contains(f.getStringValue()), getTableTextFilterPanel());
    }

    /**
     * Permet de se procurer le table filter pour filtrer par rapport à une chaine de caractère
     * @return le table filter pour filtrer par rapport à une chaine de caractère
     */
    private ITableFilterPanel getTableTextFilterPanel() {
        TableFilterTextPanel tableFilterPanel = new TableFilterTextPanel();
        tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_EDIT_FILTER_LABEL)));
        return tableFilterPanel;
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

}
