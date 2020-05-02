package ihm.panel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import ihm.interfaces.ISpecificTextModel;
import ihm.interfaces.ISpecificTextRefreshPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;
import utils.SwingUtils;

/**
 * 
 * Panel permettant d'agir sur les specifique du texte (ajouter, modifier,
 * supprimer)
 * 
 * @author jerem
 *
 */
public class SpecificControlPanel implements ISpecificTextRefreshPanel {

	private final Map<String, JTextField> mapHeaderKeyFieldTextField;
	private final Map<String, JTextField> mapKeyFieldTextField;
	private final ISpecificTextModel specificTextModel;
	private final JButton addSpecificButton;
	private final JButton modifySpecificButton;
	private final JButton deleteSpecificButton;
	private final JPanel createSpecificPanel;

	/**
	 * Constructeur
	 * 
	 * @param specificTextModel Modele pour la récupération des doonnées
	 */
	public SpecificControlPanel(ISpecificTextModel specificTextModel) {
		this.mapHeaderKeyFieldTextField = new LinkedHashMap<String, JTextField>();
		this.mapKeyFieldTextField = new LinkedHashMap<String, JTextField>();
		this.specificTextModel = specificTextModel;
		this.addSpecificButton = new JButton(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_ADD_LABEL));
		this.modifySpecificButton = new JButton(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_MODIFY_LABEL));
		this.deleteSpecificButton = new JButton(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_ACTION_DELETE_LABEL));
		this.createSpecificPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.createSpecificPanel.setBorder(BorderFactory
				.createTitledBorder(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CREATE_PANEL_TITLE)));
		this.createSpecificPanel.setLayout(new BoxLayout(createSpecificPanel, BoxLayout.Y_AXIS));
		configureActionButtons();
	}

	/**
	 * Permet de remplir la map pour les champs spécific
	 */
	private void fillSpecificFieldMap() {
		this.mapKeyFieldTextField.clear();
		this.specificTextModel.getMapTextLabelField().forEach((key, value) -> {
			JTextField textField = new JTextField(StringUtils.EMPTY, 30);
			textField.setEnabled(true);
			this.mapKeyFieldTextField.put(key, textField);
		});
		this.mapKeyFieldTextField.values().forEach(textField -> {
			textField.addKeyListener(SwingUtils.getInstance().checkAllFieldAreFilled(this.mapKeyFieldTextField.values(), addSpecificButton));
		});
	}

	/**
	 * Permet de remplis la map pour les entête spécifique
	 */
	private void fillSpecificHeaderFieldMap() {
		this.mapHeaderKeyFieldTextField.clear();
		this.specificTextModel.getMapHeaderKeyFieldText().forEach((key, value) -> {
			JTextField textField = new JTextField(StringUtils.EMPTY, 30);
			textField.setEnabled(true);
			textField.addFocusListener(saveValue(key));
			textField.setText(value);
			this.mapHeaderKeyFieldTextField.put(key, textField);
		});
	}

	/**
	 * Permet de charger l'affichage des champs textes de saisie
	 */
	private void loadDisplayFieldPanel() {
		createSpecificPanel.removeAll();
		this.specificTextModel.getMapHeaderFieldTextLabelField().forEach((key, label) -> {
			JPanel headerFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			headerFieldPanel.add(this.specificTextModel.createJLabel(label));
			headerFieldPanel.add(this.mapHeaderKeyFieldTextField.get(key));
			createSpecificPanel.add(headerFieldPanel);
		});
		this.specificTextModel.getMapTextLabelField().forEach((key, label) -> {
			JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			textFieldPanel.add(this.specificTextModel.createJLabel(label));
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
	 * Permet de remettre à zero l'affichage
	 */
	private void razDisplay() {
		addSpecificButton.setEnabled(Boolean.FALSE);
		modifySpecificButton.setEnabled(Boolean.FALSE);
		deleteSpecificButton.setEnabled(Boolean.FALSE);
	}

	/**
	 * Permet d'ajouter un ligne de champ spécifique
	 * 
	 * @return l'actionlistener
	 */
	private ActionListener addSpecificFields() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				specificTextModel.addSpecificField(mapKeyFieldTextField);
			}
		};
	}

	/**
	 * Permet de supprimer une ligne de champ spécifique
	 * 
	 * @return l'actionlistener
	 */
	private ActionListener removeSpecificField() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				specificTextModel.removeSpecificField();
			}
		};
	}

	/**
	 * Permet de modifier une ligne de champ spécifique
	 * 
	 * @return l'actionlistener
	 */
	private ActionListener modifySpecificField() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				specificTextModel.updateSpecificField(mapKeyFieldTextField);
			}
		};
	}

	/**
	 * Permet de sauvegarder la clé/valeur coté serveur sur la perte du focus
	 * 
	 * @param key Clé
	 * @return le focus listener
	 */
	private FocusListener saveValue(String key) {
		return new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				specificTextModel.updateField(key, ((JTextField) e.getSource()).getText());
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		};
	}

	@Override
	public void refresh() {
		fillSpecificHeaderFieldMap();
		fillSpecificFieldMap();
		razDisplay();
		loadDisplayFieldPanel();
	}

	@Override
	public void refreshAfterSelectedIndex() {
		if (this.specificTextModel.haveCurrentSelectedIndexInList()) {
			modifySpecificButton.setEnabled(Boolean.TRUE);
			deleteSpecificButton.setEnabled(Boolean.TRUE);
		} else {
			modifySpecificButton.setEnabled(Boolean.FALSE);
			deleteSpecificButton.setEnabled(Boolean.FALSE);
		}
		mapKeyFieldTextField.forEach((key, textField) -> {
			if (specificTextModel.getCurrentSelectedKeyValueMap().containsKey(key)) {
				textField.setText(specificTextModel.getCurrentSelectedKeyValueMap().get(key));
			} else {
				textField.setText(StringUtils.EMPTY);
			}
		});
		addSpecificButton
				.setEnabled(mapKeyFieldTextField.values().stream().map(s -> StringUtils.isNotBlank(s.getText())).reduce(Boolean::logicalAnd).get());
	}

	@Override
	public JComponent getJPanel() {
		return this.createSpecificPanel;
	}

}
