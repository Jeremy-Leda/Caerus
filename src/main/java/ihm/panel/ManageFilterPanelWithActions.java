package ihm.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import ihm.beans.Filter;
import ihm.beans.FilterTypeEnum;
import ihm.interfaces.IManageActionFilterPanel;
import ihm.interfaces.IManageFilterPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/**
 * 
 * Classe permettant d'afficher la fenêtre des filtres avec les actions associés
 * 
 * @author jerem
 *
 */
public class ManageFilterPanelWithActions implements IManageActionFilterPanel {

	private final Map<String, String> mapFields;
	private final IManageFilterPanel manageFilterPanel;
	private final JPanel content;
	private final JComboBox<String> fieldsComboBox;
	private final JComboBox<String> filterTypeComboBox;
	private final JTextField valueOfFilterText;
	private final JButton addButton;
	private final JButton deleteButton;
	private final Map<String, FilterTypeEnum> keyLabelValueTypeMap;
	private final Consumer<Void> consumerToRepack;

	/**
	 * Constructeur
	 */
	public ManageFilterPanelWithActions(Consumer<Void> consumerToRepack) {
		this.consumerToRepack = consumerToRepack;
		this.mapFields = new LinkedHashMap<String, String>();
		this.manageFilterPanel = new ManageFilterPanel(getConsumerForFilterItemChange());
		this.content = new JPanel();
		this.fieldsComboBox = new JComboBox<String>();
		this.filterTypeComboBox = new JComboBox<String>();
		this.valueOfFilterText = new JTextField(StringUtils.EMPTY, 30);
		this.addButton = new JButton();
		this.deleteButton = new JButton();
		this.keyLabelValueTypeMap = new HashMap<String, FilterTypeEnum>();
		createContent();
	}

	/**
	 * Permet de rafraichir l'interface
	 */
	public void refresh() {
		fillFilterFieldsComboBox();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(this.content, BoxLayout.Y_AXIS);
		this.content.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_PANEL_TITLE)));
		this.content.setLayout(boxlayout);
		this.content.add(createFilterPanel());
		this.content.add(this.manageFilterPanel.getJPanel());
		this.content.add(createDeleteButtonPanel());
		fillFilterTypeComboBox();
		refresh();
	}

	/**
	 * Permet de créer le panel pour le bouton de suppression
	 * 
	 * @return le panel pour le bouton de suppression
	 */
	private JPanel createDeleteButtonPanel() {
		JPanel deleteButtonPanel = new JPanel();
		this.deleteButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_DELETE_FILTER_BUTTON_LABEL));
		deleteButtonPanel.add(this.deleteButton);
		this.deleteButton.addActionListener(getDeleteActionListener());
		this.deleteButton.setEnabled(Boolean.FALSE);
		return deleteButtonPanel;
	}

	/**
	 * Permet de créer le panel pour les filtres
	 * 
	 * @return le panel pour les filtres
	 */
	private JPanel createFilterPanel() {
		JPanel filterPanel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(filterPanel, BoxLayout.Y_AXIS);
		filterPanel.setLayout(boxlayout);
		JPanel subFilterPanel = new JPanel();
		JLabel typeFilterLabel = new JLabel();
		typeFilterLabel.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_TYPE_FILTER_LABEL));
		subFilterPanel.add(typeFilterLabel);
		subFilterPanel.add(this.fieldsComboBox);
		subFilterPanel.add(this.filterTypeComboBox);
		filterPanel.add(subFilterPanel);
		JPanel subValueFilterPanel = new JPanel();
		JLabel valueFilterLabel = new JLabel();
		valueFilterLabel.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_VALUE_FILTER_LABEL));
		subValueFilterPanel.add(valueFilterLabel);
		subValueFilterPanel.add(this.valueOfFilterText);
		filterPanel.add(subValueFilterPanel);
		JPanel subButtonFilterPanel = new JPanel();
		this.addButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_ADD_FILTER_BUTTON_LABEL));
		this.addButton.setEnabled(Boolean.FALSE);
		this.addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addFilter();
				valueOfFilterText.setText(StringUtils.EMPTY);
				addButton.setEnabled(Boolean.FALSE);
			}
		});
		this.valueOfFilterText.addKeyListener(getKeyListenerForEnableAddFilterButton());
		subButtonFilterPanel.add(this.addButton);
		filterPanel.add(subButtonFilterPanel);
		return filterPanel;
	}

	/**
	 * Permet de remplir la combobox pour les types
	 */
	private void fillFilterTypeComboBox() {
		this.filterTypeComboBox.removeAllItems();
		for (FilterTypeEnum typeEnum : FilterTypeEnum.values()) {
			StringBuilder keyName = new StringBuilder(Constants.WINDOW_FILTER_TYPE_PREFIX);
			keyName.append(typeEnum.name());
			String value = ConfigurationUtils.getInstance().getDisplayMessage(keyName.toString());
			this.keyLabelValueTypeMap.put(value, typeEnum);
			this.filterTypeComboBox.addItem(value);
		}
	}

	/**
	 * Permet de remplir les champs pour la combobox
	 */
	private void fillFilterFieldsComboBox() {
		this.fieldsComboBox.removeAllItems();		
		mapFields.entrySet().stream()
				.filter(entry -> !this.manageFilterPanel.getAllFiltersList().stream()
						.filter(filter -> entry.getKey().equals(filter.getField())).findFirst().isPresent())
				.forEach(entry -> {
					this.fieldsComboBox.addItem(entry.getValue());
				});
	}

	/**
	 * Permet d'ajouter un filtre
	 */
	private void addFilter() {
		if (null != this.fieldsComboBox.getSelectedItem() && null != this.filterTypeComboBox.getSelectedItem()
				&& StringUtils.isNotBlank(this.valueOfFilterText.getText())) {
			Optional<Entry<String, String>> findFirstField = this.mapFields.entrySet().stream()
					.filter(entry -> this.fieldsComboBox.getSelectedItem().equals(entry.getValue())).findFirst();
			if (findFirstField.isPresent()) {
				Filter filterToAdd = new Filter(findFirstField.get().getKey(),
						this.fieldsComboBox.getSelectedItem().toString(), this.valueOfFilterText.getText(),
						this.keyLabelValueTypeMap.get(this.filterTypeComboBox.getSelectedItem()));
				this.manageFilterPanel.addFilter(filterToAdd);
				refresh();
				consumerToRepack.accept(null);
			}
		}
	}
	
	/**
	 * Permet de se procurer l'action pour supprimer un élément
	 * @return
	 */
	private ActionListener getDeleteActionListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (null != manageFilterPanel.getFilterSelected()) {
					manageFilterPanel.removeFilter(manageFilterPanel.getFilterSelected());
					refresh();
					consumerToRepack.accept(null);
				}
				deleteButton.setEnabled(Boolean.FALSE);
			}
		};
	}

	public KeyListener getKeyListenerForEnableAddFilterButton() {
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				 addButton.setEnabled(StringUtils.isNotBlank(valueOfFilterText.getText()));
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}
	
	/**
	 * Permet de se procurer le consumer pour le changement des items sur la liste des filtres
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerForFilterItemChange() {
		return v -> deleteButton.setEnabled(null != manageFilterPanel.getFilterSelected());
	}
	
	@Override
	public JComponent getJPanel() {
		return this.content;
	}

	@Override
	public List<Filter> getAllFiltersList() {
		return this.manageFilterPanel.getAllFiltersList();
	}

	@Override
	public void initMapOfFields(Map<String, String> mapKeyFieldValueLabel) {
		if (null != mapKeyFieldValueLabel && !mapKeyFieldValueLabel.isEmpty()) {
			this.mapFields.clear();
			mapKeyFieldValueLabel.entrySet().stream().forEach(entry -> mapFields.put(entry.getKey(), entry.getValue()));
			refresh();
		}
	}

}
