package view.panel;

import org.apache.commons.lang3.StringUtils;
import utils.RessourcesUtils;
import view.beans.LexicometricAnalyzeTypeViewEnum;
import view.interfaces.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permet de se procurer un panel pour la sélection de l'analyse lexicométrique
 */
public class ChooseLexicometricAnalyzePanel implements IChooseLexicometricAnalyzePanel {

    private JPanel content = new JPanel();
    private IComboBoxPanel chooseAnalyzeComboBoxPanel;
    private final IWizardPanel wizardPanel;
    private Optional<ILexicometricListApplyChoosePanel> optionalILexicometricListApplyChoosePanel = Optional.empty();

    public ChooseLexicometricAnalyzePanel(IWizardPanel wizardPanel) {
        createChooseAnalyzeComboBox();
        this.wizardPanel = wizardPanel;
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.setBorder(
                BorderFactory.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_ANALYSE_PANEL_TITLE)));
        content.add(chooseAnalyzeComboBoxPanel.getJPanel());
    }

    @Override
    public JComponent getJPanel() {
        return content;
    }

    public void setOptionalPanel(Optional<ILexicometricListApplyChoosePanel> panel) {
        optionalILexicometricListApplyChoosePanel = panel;
        content.removeAll();
        content.validate();
        content.add(chooseAnalyzeComboBoxPanel.getJPanel());
        panel.ifPresent(accessPanel -> content.add(accessPanel.getJPanel()));
        content.revalidate();
        content.repaint();
        wizardPanel.refresh();
    }

    /**
     * Permet de créer la liste des analyses
     */
    private void createChooseAnalyzeComboBox() {
        this.chooseAnalyzeComboBoxPanel = new ComboBoxPanel(StringUtils.EMPTY,
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_ANALYSE_LABEL));
        Set<String> lexicometricAnalyzeTypeSet = new HashSet<>();
        lexicometricAnalyzeTypeSet.add(StringUtils.EMPTY);
        lexicometricAnalyzeTypeSet.addAll(Arrays.stream(LexicometricAnalyzeTypeViewEnum.values()).map(e -> e.getLabel()).collect(Collectors.toSet()));
        this.chooseAnalyzeComboBoxPanel.refresh(lexicometricAnalyzeTypeSet);
        this.chooseAnalyzeComboBoxPanel.addConsumerOnSelectChange(e -> {
            Optional<LexicometricAnalyzeTypeViewEnum> optionalLexicometricAnalyzeTypeViewEnum =
                    LexicometricAnalyzeTypeViewEnum.fromLabel(chooseAnalyzeComboBoxPanel.getLabelSelected());

            optionalLexicometricAnalyzeTypeViewEnum.ifPresent(lexicometricAnalyzeTypeViewEnum ->
                    setOptionalPanel(lexicometricAnalyzeTypeViewEnum.getOptionalPanel().apply(wizardPanel)));
        });
    }

    @Override
    public Optional<ILexicometricListApplyChoosePanel> getOptionalILexicometricListApplyChoosePanel() {
        return this.optionalILexicometricListApplyChoosePanel;
    }

    @Override
    public LexicometricAnalyzeTypeViewEnum getAnalyzeToLaunch() {
        return LexicometricAnalyzeTypeViewEnum.fromLabel(this.chooseAnalyzeComboBoxPanel.getLabelSelected()).get();
    }
}
