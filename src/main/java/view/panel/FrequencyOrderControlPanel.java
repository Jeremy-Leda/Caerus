package view.panel;

import org.apache.commons.lang3.StringUtils;
import utils.SwingUtils;
import view.interfaces.IFrequencyOrderTextModel;
import view.interfaces.ISpecificTextRefreshPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrequencyOrderControlPanel implements ISpecificTextRefreshPanel {

    private final Map<String, JTextField> mapKeyFieldTextField;
    private final IFrequencyOrderTextModel frequencyOrderTextModel;
    private final JButton addSpecificButton;
    private final JButton modifySpecificButton;
    private final JButton deleteSpecificButton;
    private final JPanel createSpecificPanel;

    /**
     * Constructeur
     *
     * @param frequencyOrderTextModel Modele pour la récupération des doonnées
     */
    public FrequencyOrderControlPanel(IFrequencyOrderTextModel frequencyOrderTextModel) {
        this.mapKeyFieldTextField = new LinkedHashMap<>();
        this.frequencyOrderTextModel = frequencyOrderTextModel;
        this.addSpecificButton = new JButton(
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_ADD_LABEL));
        this.modifySpecificButton = new JButton(
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_MODIFY_LABEL));
        this.deleteSpecificButton = new JButton(
                ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_DELETE_LABEL));
        this.createSpecificPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.createSpecificPanel.setBorder(BorderFactory
                .createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FREQUENCY_ORDER_CREATE_PANEL_TITLE)));
        this.createSpecificPanel.setLayout(new BoxLayout(createSpecificPanel, BoxLayout.Y_AXIS));
        configureActionButtons();
    }

    /**
     * Permet de configurer les boutons d'actions
     */
    private void configureActionButtons() {
        addSpecificButton.addActionListener(addSpecificFields());
        deleteSpecificButton.addActionListener(removeSpecificField());
        modifySpecificButton.addActionListener(modifySpecificField());
        addSpecificButton.setEnabled(Boolean.FALSE);
        deleteSpecificButton.setEnabled(Boolean.FALSE);
        modifySpecificButton.setEnabled(Boolean.FALSE);
        SwingUtils.getInstance().addEnterKeyListener(addSpecificButton);
        SwingUtils.getInstance().addEnterKeyListener(modifySpecificButton);
        SwingUtils.getInstance().addEnterKeyListener(deleteSpecificButton);
    }

    /**
     * Permet d'ajouter un ligne de champ spécifique
     *
     * @return l'actionlistener
     */
    private ActionListener addSpecificFields() {
        return e -> frequencyOrderTextModel.addSpecificField(mapKeyFieldTextField);
    }

    /**
     * Permet de supprimer une ligne de champ spécifique
     *
     * @return l'actionlistener
     */
    private ActionListener removeSpecificField() {
        return e -> frequencyOrderTextModel.removeSpecificField();
    }

    /**
     * Permet de modifier une ligne de champ spécifique
     *
     * @return l'actionlistener
     */
    private ActionListener modifySpecificField() {
        return e -> frequencyOrderTextModel.updateSpecificField(mapKeyFieldTextField);
    }

    /**
     * Permet de remettre à zero l'affichage
     */
    private void razDisplay() {
        addSpecificButton.setEnabled(Boolean.FALSE);
        modifySpecificButton.setEnabled(Boolean.FALSE);
        deleteSpecificButton.setEnabled(Boolean.FALSE);
    }

    @Override
    public JComponent getJPanel() {
        return this.createSpecificPanel;
    }


    /**
     * Permet de charger l'affichage des champs textes de saisie
     */
    private void loadDisplayFieldPanel() {
        createSpecificPanel.removeAll();
        this.frequencyOrderTextModel.getMapTextLabelField().forEach((key, label) -> {
            JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            textFieldPanel.add(this.frequencyOrderTextModel.createJLabel(label));
            textFieldPanel.add(this.mapKeyFieldTextField.get(key));
            createSpecificPanel.add(textFieldPanel);
        });
        JPanel panCreateSpecificButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panCreateSpecificButtons.add(addSpecificButton);
        panCreateSpecificButtons.add(modifySpecificButton);
        panCreateSpecificButtons.add(deleteSpecificButton);
        createSpecificPanel.add(panCreateSpecificButtons);
    }

    /**
     * Permet de remplir la map pour les champs spécific
     */
    private void fillSpecificFieldMap() {
        this.mapKeyFieldTextField.clear();
        this.frequencyOrderTextModel.getMapTextLabelField().forEach((key, value) -> {
            JTextField textField = new JTextField(StringUtils.EMPTY, 30);
            textField.setEnabled(true);
            this.mapKeyFieldTextField.put(key, textField);
        });
        this.mapKeyFieldTextField.values().forEach(textField -> {
            textField.addKeyListener(SwingUtils.getInstance().checkAllFieldAreFilled(this.mapKeyFieldTextField.values(), addSpecificButton));
        });
    }

    @Override
    public void refresh() {
        fillSpecificFieldMap();
        razDisplay();
        loadDisplayFieldPanel();
    }

    @Override
    public void refreshAfterSelectedIndex() {
        if (this.frequencyOrderTextModel.haveCurrentSelectedIndexInList()) {
            modifySpecificButton.setEnabled(Boolean.TRUE);
            deleteSpecificButton.setEnabled(Boolean.TRUE);
        } else {
            modifySpecificButton.setEnabled(Boolean.FALSE);
            deleteSpecificButton.setEnabled(Boolean.FALSE);
        }
        mapKeyFieldTextField.forEach((key, textField) -> {
            if (frequencyOrderTextModel.getCurrentSelectedKeyValueMap().containsKey(key)) {
                textField.setText(frequencyOrderTextModel.getCurrentSelectedKeyValueMap().get(key));
            } else {
                textField.setText(StringUtils.EMPTY);
            }
        });
        addSpecificButton
                .setEnabled(mapKeyFieldTextField.values().stream().map(s -> StringUtils.isNotBlank(s.getText())).reduce(Boolean::logicalAnd).get());
    }
}
