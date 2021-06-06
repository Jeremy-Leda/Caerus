package view.panel;

import org.apache.commons.lang3.StringUtils;
import utils.RessourcesUtils;
import view.beans.LemmeTypeViewEnum;
import view.interfaces.IAccessPanel;
import view.interfaces.IComboBoxPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Permet de fournir un panel pour choisir les lemmes à appliquer
 *
 */
public class LemmeApplyChoosePanel implements IAccessPanel {

    private final JPanel content = new JPanel();
    private final IComboBoxPanel chooseLemmeComboBox = new ComboBoxPanel(StringUtils.EMPTY,
            ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_TYPE_TREATMENT_OPTIONAL_LIST_LABEL));
    private final IComboBoxPanel chooseListComboBox = new ComboBoxPanel(StringUtils.EMPTY,
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_PROFILE_TREATMENT_OPTIONAL_LIST_LABEL));

    /**
     * Constructeur
     */
    public LemmeApplyChoosePanel() {
        configureChooseLemmeComboBox();
        createWindow();
    }

    private void configureChooseLemmeComboBox() {
        Set<String> lemmeList = new HashSet<>();
        lemmeList.add(StringUtils.EMPTY);
        lemmeList.addAll(Arrays.stream(LemmeTypeViewEnum.values()).map(e -> e.getLabel()).collect(Collectors.toSet()));
        chooseLemmeComboBox.refresh(lemmeList);
        chooseLemmeComboBox.addConsumerOnSelectChange(e -> {
            Optional<LemmeTypeViewEnum> optionalLemmeTypeViewEnum = LemmeTypeViewEnum.fromLabel(chooseLemmeComboBox.getLabelSelected());
            optionalLemmeTypeViewEnum.ifPresent(lemmeTypeViewEnum -> chooseListComboBox.refresh(lemmeTypeViewEnum.getProfileSet()));
        });
    }


    /**
     * Permet de créer la fenetre
     */
    private void createWindow() {
        content.setBorder(
                BorderFactory.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_ANALYSE_OPTION_PANEL_TITLE)));
        content.add(chooseLemmeComboBox.getJPanel());
        content.add(chooseListComboBox.getJPanel());
    }

    @Override
    public JComponent getJPanel() {
        return content;
    }
}
