package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.beans.LexicometricEditEnum;
import view.beans.LexicometricLemmatizationConfigurationEnum;
import view.beans.LexicometricProperNounConfigurationEnum;
import view.beans.LexicometricTokenizationConfigurationEnum;
import view.interfaces.ILexicometricConfigurationChoosePanel;
import view.interfaces.ILexicometricListApplyChoosePanel;
import view.interfaces.IWizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class LexicometricListApplyChoosePanel implements ILexicometricListApplyChoosePanel {

    private final JPanel content = new JPanel();
    private final Map<LexicometricEditEnum, LexicometricConfigurationChoosePanel> lexicometricListApplyChoosePanelMap = new HashMap<>();
    private final Collection<LexicometricEditEnum> lexicometricEditEnumCollection = new HashSet<>();


    public LexicometricListApplyChoosePanel(IWizardPanel wizardPanel, Boolean withStopWords, Boolean withLemmatization, Boolean withProperNoun) {
        lexicometricListApplyChoosePanelMap.put(LexicometricEditEnum.LEMMATIZATION,
                new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricLemmatizationConfigurationEnum.values(), LexicometricEditEnum.LEMMATIZATION));
        lexicometricListApplyChoosePanelMap.put(LexicometricEditEnum.TOKENIZATION,
                new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricTokenizationConfigurationEnum.values(), LexicometricEditEnum.TOKENIZATION));
        lexicometricListApplyChoosePanelMap.put(LexicometricEditEnum.PROPER_NOUN,
                new LexicometricConfigurationChoosePanel(wizardPanel, LexicometricProperNounConfigurationEnum.values(), LexicometricEditEnum.PROPER_NOUN));
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
            content.add(lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.TOKENIZATION).getJPanel());
            lexicometricEditEnumCollection.add(LexicometricEditEnum.TOKENIZATION);
        }
        if (withLemmatization) {
            content.add(lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.LEMMATIZATION).getJPanel());
            lexicometricEditEnumCollection.add(LexicometricEditEnum.LEMMATIZATION);
        }
        if (withProperNoun) {
            content.add(lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.PROPER_NOUN).getJPanel());
            lexicometricEditEnumCollection.add(LexicometricEditEnum.PROPER_NOUN);
        }
    }

    @Override
    public JComponent getJPanel() {
        return this.content;
    }

    @Override
    public ILexicometricConfigurationChoosePanel getLemmatizationConfiguration() {
        return lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.LEMMATIZATION);
    }

    @Override
    public ILexicometricConfigurationChoosePanel getStopWordConfiguration() {
        return lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.TOKENIZATION);
    }

    @Override
    public ILexicometricConfigurationChoosePanel getProperNounConfiguration() {
        return lexicometricListApplyChoosePanelMap.get(LexicometricEditEnum.PROPER_NOUN);
    }

    @Override
    public Boolean isValid() {
        return lexicometricListApplyChoosePanelMap.values().stream()
                .filter(x -> lexicometricEditEnumCollection.contains(x.getGeneralLexicometricEditEnum()))
                .map(LexicometricConfigurationChoosePanel::isValid)
                .reduce(Boolean::logicalAnd)
                .orElse(true);
    }
}
