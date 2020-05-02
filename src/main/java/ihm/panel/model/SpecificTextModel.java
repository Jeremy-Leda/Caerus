package ihm.panel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import ihm.controler.IConfigurationControler;
import ihm.interfaces.IModalFrameRepack;
import ihm.interfaces.ISpecificTextModel;
import ihm.interfaces.ISpecificTextRefreshPanel;

/**
 * 
 * Classe permettant de gérer le chargement des informations pour les IHM de
 * textes spécifiques
 * 
 * @author jerem
 *
 */
public class SpecificTextModel implements ISpecificTextModel {

	// Cle = FieldName / Valeur = Contenu du texte
	private final Map<String, String> mapHeaderKeyFieldText;
	// Cle = FieldName / Valeur = Label (string)
	private final Map<String, String> mapHeaderFieldTextLabelField;
	// Cle = FieldName / Valeur = Liste des valeurs
	private final Map<String, List<String>> mapKeyFieldListField;
	// Cle = FieldName / Valeur = Label (string)
	private final Map<String, String> mapTextLabelField;
	// Controler
	private final IConfigurationControler controler;
	// Liste pour lancer le refresh de l'interface
	private final List<ISpecificTextRefreshPanel> specificTextRefreshPanelList;
	private final IModalFrameRepack modalFrameRepack;
	private Integer currentIndex;
	private Integer currentSelectedIndexInList;
	private Map<String,String> mapSelectedValue;
	private final List<Consumer<?>> refreshOnLoadFieldConsumerList;

	/**
	 * Constructeur
	 * 
	 * @param controler controler pour le chargment des données
	 */
	public SpecificTextModel(IConfigurationControler controler, IModalFrameRepack modalFrameRepack) {
		this.mapHeaderKeyFieldText = new LinkedHashMap<String, String>();
		this.mapHeaderFieldTextLabelField = new LinkedHashMap<String, String>();
		this.mapTextLabelField = new LinkedHashMap<String, String>();
		this.mapKeyFieldListField = new LinkedHashMap<String, List<String>>();
		this.mapSelectedValue = new HashMap<String, String>();
		this.refreshOnLoadFieldConsumerList = new ArrayList<>();
		this.controler = controler;
		this.specificTextRefreshPanelList = new ArrayList<ISpecificTextRefreshPanel>();
		this.modalFrameRepack = modalFrameRepack;
		this.currentIndex = 0;
		this.currentSelectedIndexInList = null;
	}

	/**
	 * Permet de se procurer la map des headers (clé/valeur)
	 * 
	 * @return la map des headers (clé/valeur)
	 */
	@Override
	public Map<String, String> getMapHeaderKeyFieldText() {
		return Collections.unmodifiableMap(this.mapHeaderKeyFieldText);
	}

	/**
	 * Permet de se procurer la map des headers (clé/label)
	 * 
	 * @return la map des headers (clé/label)
	 */
	@Override
	public Map<String, String> getMapHeaderFieldTextLabelField() {
		return Collections.unmodifiableMap(mapHeaderFieldTextLabelField);
	}

	/**
	 * Permet de se procurer la map des liste de valeurs des champs spécifique
	 * (clé/Liste valeur)
	 * 
	 * @return la map des liste de valeurs des champs spécifique (clé/Liste valeur)
	 */
	@Override
	public Map<String, List<String>> getMapKeyFieldListField() {
		return Collections.unmodifiableMap(mapKeyFieldListField);
	}

	/**
	 * Permet de se procurer la map des liste des champs spécifique (clé/label)
	 * 
	 * @return la map des liste des champs spécifique (clé/label)
	 */
	@Override
	public Map<String, String> getMapTextLabelField() {
		return Collections.unmodifiableMap(mapTextLabelField);
	}

	/**
	 * Permet de charger les entêtes
	 * 
	 * @param index index du champ specifique à charger
	 */
	private void loadHeader(Integer index) {
		this.mapHeaderFieldTextLabelField.clear();
		this.mapHeaderKeyFieldText.clear();
		this.controler.getListFieldHeaderSpecific(index).forEach((k, v) -> {
			StringBuilder sb = new StringBuilder(v.replace("[", "").replace("]", ""));
			sb.append(" : ");
			this.mapHeaderFieldTextLabelField.put(k, sb.toString());
			this.mapHeaderKeyFieldText.put(k, controler.getFieldInEditingCorpus(k));
		});
	}

	/**
	 * Permet de charger les listes de valeurs
	 * 
	 * @param index index du champ specifique à charger
	 */
	private void loadSpecificFieldListValue(Integer index) {
		this.mapKeyFieldListField.clear();
		Map<String, List<String>> specificFieldInEditingCorpus = this.controler.getSpecificFieldInEditingCorpus(index);
		specificFieldInEditingCorpus.entrySet().stream().forEach(entry -> {
			this.mapKeyFieldListField.put(entry.getKey(), entry.getValue());
		});
	}

	/**
	 * Permet de charger les champs spécifiques
	 * 
	 * @param index index du champ specifique à charger
	 */
	private void loadSpecificField(Integer index) {
		this.mapTextLabelField.clear();
		this.controler.getListFieldSpecific(index).forEach((k, v) -> {
			StringBuilder sb = new StringBuilder(v.replace("[", "").replace("]", ""));
			sb.append(" : ");
			this.mapTextLabelField.put(k, sb.toString());
		});
	}

	/**
	 * Permet de charger tous les champs spécifiques
	 * 
	 * @param index index du champ specifique à charger
	 */
	@Override
	public void loadAllField(Integer index) {
		this.currentIndex = index;
		this.loadHeader(index);
		this.loadSpecificField(index);
		this.loadSpecificFieldListValue(index);
		this.specificTextRefreshPanelList.forEach(st -> st.refresh());
		this.refreshOnLoadFieldConsumerList.forEach(consumer -> consumer.accept(null));
		this.modalFrameRepack.repack();
	}

	/**
	 * Permet d'ajouter un specific text refresh panel
	 * 
	 * @param specificTextRefreshPanel specific text refresh panel
	 */
	@Override
	public void addSpecificTextRefresh(ISpecificTextRefreshPanel specificTextRefreshPanel) {
		this.specificTextRefreshPanelList.add(specificTextRefreshPanel);
	}

	/**
	 * Permet de mettre à jour une valeur coté serveur
	 * 
	 * @param key   Clé
	 * @param value Valeur
	 */
	@Override
	public void updateField(String key, String value) {
		this.controler.updateFieldInEditingCorpus(key, value);
	}

	/**
	 * Permet d'ajouter les informations de la liste
	 * 
	 * @param mapKeyFieldTextField map des champs de l'interface
	 */
	@Override
	public void addSpecificField(Map<String, JTextField> mapKeyFieldTextField) {
		mapKeyFieldTextField.forEach((key, textField) -> {
			this.mapKeyFieldListField.get(key).add(StringUtils.trim(textField.getText()));
		});
		updateSpecificFieldControler();
	}

	/**
	 * Permet de mettre à jour les informations dans la liste
	 * 
	 * @param mapKeyFieldTextField map des champs de l'interface
	 */
	@Override
	public void updateSpecificField(Map<String, JTextField> mapKeyFieldTextField) {
		if (haveCurrentSelectedIndexInList()) {
			mapKeyFieldTextField.forEach((key, textField) -> {
				this.mapKeyFieldListField.get(key).set(this.currentSelectedIndexInList, StringUtils.trim(textField.getText()));
			});
			updateSpecificFieldControler();
		}
	}
	
	/**
	 * Permet de supprimer les champs sélectionnés
	 */
	@Override
	public void removeSpecificField() {
		if (haveCurrentSelectedIndexInList()) {
			this.mapKeyFieldListField.values().forEach(values -> values.remove(this.currentSelectedIndexInList.intValue()));
			updateSpecificFieldControler();
		}
	}

	/**
	 * Permet de mettre à jour tous les champs coté serveur
	 */
	private void updateSpecificFieldControler() {
		controler.updateSpecificFieldInEditingCorpus(currentIndex, mapKeyFieldListField);
		clearCurrentSelection();
		loadAllField(currentIndex);
	}

	/**
	 * Permet de vider la liste des sélections
	 */
	private void clearCurrentSelection() {
		this.currentSelectedIndexInList = null;
		this.mapSelectedValue.clear();
	}
	
	/**
	 * Permet de savoir si on a un index de slectionné
	 * @return
	 */
	@Override
	public Boolean haveCurrentSelectedIndexInList() {
		return null != this.currentSelectedIndexInList;
	}

	@Override
	public void setCurrentSelectedIndexInList(Integer index) {
		this.currentSelectedIndexInList = index;
		fillSelectedValueMap();
		this.specificTextRefreshPanelList.forEach(st -> st.refreshAfterSelectedIndex());
	}

	@Override
	public Map<String, String> getCurrentSelectedKeyValueMap() {
		return Collections.unmodifiableMap(this.mapSelectedValue);
	}

	/**
	 * Permet de remplir la map des valeurs
	 */
	private void fillSelectedValueMap() {
		if (haveCurrentSelectedIndexInList()) {
			this.mapKeyFieldListField.forEach((key,valueList) -> {
				this.mapSelectedValue.put(key, valueList.get(this.currentSelectedIndexInList));
			});
		}
	}

	@Override
	public Integer getCurrentSelectedIndexInList() {
		return this.currentSelectedIndexInList;
	}

	@Override
	public Integer getCurrentIndex() {
		return this.currentIndex;
	}

	@Override
	public Boolean havePreviousSpecificConfiguration() {
		return currentIndex > 0;
	}

	@Override
	public Boolean haveNextSpecificConfiguration() {
		return currentIndex < controler.getNbSpecificConfiguration() - 1;
	}

	@Override
	public Integer getNbMaxConfiguration() {
		return this.controler.getNbSpecificConfiguration();
	}

	@Override
	public JLabel createJLabel(String text) {
		return new JLabel(text);
	}

	@Override
	public Boolean haveErrorStructuredInCurrentIndex() {
		if (null != mapKeyFieldListField) {
			List<Long> nbNotBlankValueList = new ArrayList<>();
			mapKeyFieldListField.values().forEach(valueList -> {
				nbNotBlankValueList.add(valueList.stream().filter(value -> StringUtils.isNotBlank(value)).count());
			});
			return nbNotBlankValueList.stream().distinct().count() > 1;
		}
		return Boolean.FALSE;
	}

	@Override
	public void addRefreshConsumerOnLoadAllField(Consumer<?> consumer) {
		this.refreshOnLoadFieldConsumerList.add(consumer);
	}
	
}
