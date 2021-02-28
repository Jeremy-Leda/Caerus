package view.windows;

import controler.IConfigurationControler;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.ICheckBoxPanel;
import view.interfaces.IInformationPanel;
import view.interfaces.IWizardPanel;
import view.panel.CheckBoxPanel;
import view.panel.InformationPanel;
import view.panel.WizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AnalysisAssistant extends ModalJFrameAbstract {

    // WIZARD
    private final IWizardPanel wizardPanel;
    private final ICheckBoxPanel checkBoxPanel;
    private JPanel content = new JPanel();

    public AnalysisAssistant(String title, IConfigurationControler configurationControler) {
        super(title, configurationControler);
        this.wizardPanel = new WizardPanel(ConfigurationUtils.getInstance()
                .getDisplayMessage(Constants.WINDOW_START_ANALYSIS_WIZARD_PANEL_TITLE));
        this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());
        this.checkBoxPanel = new CheckBoxPanel(3, false);

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
