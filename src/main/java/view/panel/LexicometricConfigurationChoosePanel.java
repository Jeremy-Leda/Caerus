package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.beans.LexicometricEditEnum;
import view.beans.LexicometricTokenizationConfigurationEnum;
import view.interfaces.IComboBoxPanel;
import view.interfaces.ILexicometricConfigurationChoosePanel;
import view.interfaces.ILexicometricConfigurationEnum;
import view.interfaces.IWizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LexicometricConfigurationChoosePanel implements ILexicometricConfigurationChoosePanel {

    private final JPanel content = new JPanel();
    private final IComboBoxPanel chooseComboBox;
    private final IComboBoxPanel chooseListComboBox = new ComboBoxPanel(StringUtils.EMPTY,
            ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_PROFILE_TREATMENT_OPTIONAL_LIST_LABEL));
    private final ILexicometricConfigurationEnum[] values;
    private final IWizardPanel wizardPanel;

    public LexicometricConfigurationChoosePanel(IWizardPanel wizardPanel, ILexicometricConfigurationEnum[] values) {
        this.values = values;
        this.wizardPanel = wizardPanel;
        ILexicometricConfigurationEnum iLexicometricConfigurationEnum = Arrays.stream(values).findFirst().get();
        chooseComboBox = new ComboBoxPanel(StringUtils.EMPTY, iLexicometricConfigurationEnum.getChooseListLabel());
        configureChooseComboBox();
        createWindow();
    }

    private void configureChooseComboBox() {
        Set<String> elementSet = new HashSet<>();
        elementSet.add(StringUtils.EMPTY);
        elementSet.addAll(Arrays.stream(values).map(e -> e.getLabel()).collect(Collectors.toSet()));
        chooseComboBox.refresh(elementSet);
        chooseComboBox.addConsumerOnSelectChange(e -> {
            Optional<ILexicometricConfigurationEnum> optionalConfigurationEnum =
                    Arrays.stream(values).filter(x -> x.getLabel().equals(chooseComboBox.getLabelSelected())).findFirst();
            optionalConfigurationEnum.ifPresent(configurationEnum -> chooseListComboBox.refresh(configurationEnum.getProfileSet()));
            wizardPanel.refresh();
        });
    }

    /**
     * Permet de créer la fenêtre
     */
    private void createWindow() {
        content.add(chooseComboBox.getJPanel());
        content.add(chooseListComboBox.getJPanel());
    }

    @Override
    public JComponent getJPanel() {
        return content;
    }

    @Override
    public LexicometricEditEnum getLexicometricEditEnum() {
        Optional<ILexicometricConfigurationEnum> optionalConfigurationEnum =
                Arrays.stream(values).filter(x -> x.getLabel().equals(chooseComboBox.getLabelSelected())).findFirst();
        return optionalConfigurationEnum.map(s -> s.getLexicometricEditEnum()).orElse(null);
    }

    @Override
    public String getProfile() {
        return chooseListComboBox.getLabelSelected();
    }
}
