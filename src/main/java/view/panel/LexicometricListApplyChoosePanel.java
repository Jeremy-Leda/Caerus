package view.panel;

import view.beans.LexicometricLemmatizationConfigurationEnum;
import view.beans.LexicometricProperNounConfigurationEnum;
import view.beans.LexicometricTokenizationConfigurationEnum;
import view.interfaces.ILexicometricConfigurationChoosePanel;
import view.interfaces.ILexicometricListApplyChoosePanel;
import view.interfaces.IWizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;

public class LexicometricListApplyChoosePanel implements ILexicometricListApplyChoosePanel {

    private final JPanel content = new JPanel();
    private final ILexicometricConfigurationChoosePanel lemmatizationChoosePanel;
    private final ILexicometricConfigurationChoosePanel stopWordChoosePanel;
    private final ILexicometricConfigurationChoosePanel properNounChoosePanel;

    public LexicometricListApplyChoosePanel(IWizardPanel wizardPanel, Boolean withStopWords, Boolean withLemmatization, Boolean withProperNoun) {
        lemmatizationChoosePanel = new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricLemmatizationConfigurationEnum.values());
        stopWordChoosePanel = new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricTokenizationConfigurationEnum.values());
        properNounChoosePanel = new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricProperNounConfigurationEnum.values());
        createWindow(withStopWords, withLemmatization, withProperNoun);
    }

    /**
     * Permet de créer la fenetre
     */
    private void createWindow(Boolean withStopWords, Boolean withLemmatization, Boolean withProperNoun) {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.setBorder(
                BorderFactory.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_ANALYSE_OPTION_PANEL_TITLE)));
        if (withStopWords) {
            content.add(stopWordChoosePanel.getJPanel());
        }
        if (withLemmatization) {
            content.add(lemmatizationChoosePanel.getJPanel());
        }
        if (withProperNoun) {
            content.add(properNounChoosePanel.getJPanel());
        }
    }

    @Override
    public JComponent getJPanel() {
        return this.content;
    }

    @Override
    public ILexicometricConfigurationChoosePanel getLemmatizationConfiguration() {
        return this.lemmatizationChoosePanel;
    }

    @Override
    public ILexicometricConfigurationChoosePanel getStopWordConfiguration() {
        return this.stopWordChoosePanel;
    }

    @Override
    public ILexicometricConfigurationChoosePanel getProperNounConfiguration() {
        return this.properNounChoosePanel;
    }
}
