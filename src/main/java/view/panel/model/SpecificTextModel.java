package view.panel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import controler.IConfigurationControler;
import view.beans.SpecificRow;
import view.interfaces.IModalFrameRepack;
import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;

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

	// Liste des en-têtes
	private final List<String> headerList;
	// Liste des lignes
	private final List<SpecificRow> specificRowList;

	private final Map<String, Integer> mapKeyIndex;

	// Controler
	private final IConfigurationControler controler;
	// Liste pour lancer le refresh de l'interface
	private final List<ISpecificTextRefreshPanel> specificTextRefreshPanelList;
	private final IModalFrameRepack modalFrameRepack;
	private Integer currentIndex;
	private Integer currentSelectedIndexInList;
	private Map<String, String> mapSelectedValue;
	private final List<Consumer<?>> refreshOnLoadFieldConsumerList;
	private Consumer<?> clearSelectionConsumer;
	private final String HTML_BOLD_HEADER = "<html><b>%s</b></html>";
	private final List<Integer> allSelectedIndexList;

	/**
	 * Constructeur
	 * 
	 * @param controler controler pour le chargment des données
	 */
	public SpecificTextModel(IConfigurationControler controler, IModalFrameRepack modalFrameRepack) {
		this.mapKeyIndex = new HashMap<String, Integer>();
		this.mapHeaderKeyFieldText = new LinkedHashMap<String, String>();
		this.mapHeaderFieldTextLabelField = new LinkedHashMap<String, String>();
		this.mapTextLabelField = new LinkedHashMap<String, String>();
		this.mapKeyFieldListField = new LinkedHashMap<String, List<String>>();
		this.headerList = new LinkedList<String>();
		this.specificRowList = new LinkedList<SpecificRow>();
		this.mapSelectedValue = new HashMap<String, String>();
		this.refreshOnLoadFieldConsumerList = new ArrayList<>();
		this.controler = controler;
		this.specificTextRefreshPanelList = new ArrayList<ISpecificTextRefreshPanel>();
		this.modalFrameRepack = modalFrameRepack;
		this.currentIndex = 0;
		this.currentSelectedIndexInList = null;
		allSelectedIndexList = new ArrayList<>();
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

		this.headerList.clear();
		this.specificRowList.clear();
		this.mapKeyIndex.clear();
		this.headerList.add("");

		specificFieldInEditingCorpus.entrySet().stream().forEach(entry -> {
			// String valueKeyField =
			// this.controler.getListFieldSpecific(index).get(entry.getKey());
			this.mapKeyIndex.put(entry.getKey(), this.headerList.size());
			String labelControler = this.controler.getListFieldSpecific(index).get(entry.getKey());
			this.headerList.add(String.format(HTML_BOLD_HEADER, labelControler.replace("[", "").replace("]", "")));
			for (int j = 0; j < entry.getValue().size(); j++) {
				if (this.specificRowList.size() <= j) {
					SpecificRow row = new SpecificRow();
					row.addSpecific(String.valueOf(j + 1));
					this.specificRowList.add(row);
				}
				this.specificRowList.get(j).addSpecific(entry.getValue().get(j));
			}
		});
		removeEmptyLine();
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
		clearCurrentSelection();
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
//		boolean allValueOfListIsEmpty = this.mapKeyFieldListField.values().stream().allMatch(valueList -> valueList.stream().filter(s -> StringUtils.isNotBlank(s)).count() == 0);
//		if (allValueOfListIsEmpty) {
//			mapKeyFieldListField.values().forEach(list -> list.clear());
//		}
//		mapKeyFieldTextField.forEach((key, textField) -> {
//			this.mapKeyFieldListField.get(key).add(StringUtils.trim(textField.getText()));
//		});

		SpecificRow newRow = new SpecificRow();
		newRow.getSpecificList().add(String.valueOf(this.specificRowList.size()));
		this.mapKeyIndex.entrySet().stream().sorted(Comparator.comparing(Entry::getValue)).forEach(entry -> {
			if (mapKeyFieldTextField.containsKey(entry.getKey())) {
				JTextField textField = mapKeyFieldTextField.get(entry.getKey());
				newRow.getSpecificList().add(StringUtils.trim(textField.getText()));
			}
		});
		this.specificRowList.add(newRow);

//		mapKeyFieldTextField.forEach((key, textField) -> {
//			Integer integer = this.mapKeyIndex.get(key);
//		});

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
//			mapKeyFieldTextField.forEach((key, textField) -> {
//				this.mapKeyFieldListField.get(key).set(this.currentSelectedIndexInList, StringUtils.trim(textField.getText()));
//			});
			updateListModel(mapKeyFieldTextField);
			removeEmptyLine();
			updateSpecificFieldControler();
		}
	}

	/**
	 * Permet de mettre à jour le modéle par rapport aux champs de l'interface
	 * 
	 * @param mapKeyFieldTextField map des champs de l'interface
	 */
	private void updateListModel(Map<String, JTextField> mapKeyFieldTextField) {
		mapKeyFieldTextField.forEach((key, textField) -> {
			Integer index = this.mapKeyIndex.get(key);
			if (StringUtils.isBlank(textField.getText())) {
				shiftCell(key);
			} else {
				this.specificRowList.get(this.currentSelectedIndexInList).getSpecificList().set(index,
						StringUtils.trim(textField.getText()));
			}
		});
	}

	/**
	 * Permet de décaler les valeurs pour un champ spécifié
	 * 
	 * @param key champ
	 */
	private void shiftCell(String key) {
		Integer index = this.mapKeyIndex.get(key);
		for (int i = this.currentSelectedIndexInList; i < this.specificRowList.size(); i++) {
			Integer nextCell = i + 1;
			String value = StringUtils.EMPTY;
			if (nextCell < this.specificRowList.size()) {
				value = this.specificRowList.get(nextCell).getSpecificList().get(index);
			}
			this.specificRowList.get(i).getSpecificList().set(index, StringUtils.trim(value));
		}
	}

	/**
	 * Permet de supprimer les lignes vides automatiquement
	 */
	private void removeEmptyLine() {
		List<SpecificRow> listToRemove = new ArrayList<>();
		for (SpecificRow sr : this.specificRowList) {
			if (this.mapKeyIndex.values().stream().allMatch(v -> StringUtils.isBlank(sr.getSpecificList().get(v)))) {
				listToRemove.add(sr);
			}
		}
		if (!listToRemove.isEmpty()) {
			listToRemove.stream().forEach(row -> this.specificRowList.remove(row));
			fixedPosition();
		}
	}

	/**
	 * Permet de corriger la position qui est renseigner dans chaque ligne
	 */
	private void fixedPosition() {
		for (int i = 0; i < this.specificRowList.size(); i++) {
			this.specificRowList.get(i).getSpecificList().set(0, String.valueOf(i + 1));
		}
	}

	/**
	 * Permet de supprimer les champs sélectionnés
	 */
	@Override
	public void removeSpecificField() {
		if (haveCurrentSelectedIndexInList() && !this.allSelectedIndexList.isEmpty()) {
			List<SpecificRow> newList = new LinkedList<SpecificRow>();
			for (int i = 0; i < this.specificRowList.size(); i++) {
				if (!this.allSelectedIndexList.contains(i)) {
					newList.add(this.specificRowList.get(i));
				}
			}
			this.specificRowList.clear();
			this.specificRowList.addAll(newList);
			this.allSelectedIndexList.clear();
			updateSpecificFieldControler();
		}
	}

	/**
	 * Permet de mettre à jour tous les champs coté serveur
	 */
	private void updateSpecificFieldControler() {
		// controler.updateSpecificFieldInEditingCorpus(currentIndex,
		// mapKeyFieldListField);
		controler.updateSpecificFieldInEditingCorpus(currentIndex, constructMapKeyListValueForControler());
		clearCurrentSelection();
		loadAllField(currentIndex);
	}

	/**
	 * Permet de construire la liste des spécfiques à destination du controler
	 * 
	 * @return la liste des clé valeur
	 */
	private Map<String, List<String>> constructMapKeyListValueForControler() {
		Map<String, List<String>> mapKeyListValueForControler = new HashMap<>();
		String delimiterSpecific = controler.getDelimiterSpecific(currentIndex);
		this.mapKeyIndex.forEach((key, value) -> {
			List<String> listValue = new LinkedList<>();
			this.specificRowList.forEach(row -> {
				List<String> allValues = Arrays
						.asList(StringUtils.split(row.getSpecificList().get(value), delimiterSpecific));
				listValue.addAll(allValues);
			});
			listValue.removeIf(v -> StringUtils.isBlank(v));
			mapKeyListValueForControler.put(key, listValue);
		});
		return mapKeyListValueForControler;
	}

	/**
	 * Permet de vider la liste des sélections
	 */
	private void clearCurrentSelection() {
		if (null != this.clearSelectionConsumer) {
			this.clearSelectionConsumer.accept(null);
		}
		// this.setCurrentSelectedIndexInList(-1);
		this.mapSelectedValue.clear();
	}

	/**
	 * Permet de savoir si on a un index de slectionné
	 * 
	 * @return
	 */
	@Override
	public Boolean haveCurrentSelectedIndexInList() {
		return null != this.currentSelectedIndexInList && this.currentSelectedIndexInList >= 0;
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
			this.mapKeyFieldListField.forEach((key, valueList) -> {
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

	@Override
	public List<String> getHeaderList() {
		return Collections.unmodifiableList(this.headerList);
	}

	@Override
	public List<SpecificRow> getSpecificRowList() {
		return Collections.unmodifiableList(this.specificRowList);
	}

	@Override
	public void setClearSelectionConsumer(Consumer<?> consumer) {
		this.clearSelectionConsumer = consumer;
	}

	@Override
	public void addIndexToSelectedIndexList(Integer index) {
		this.allSelectedIndexList.add(index);
	}

	@Override
	public void updateFromCell(Integer rowIndex, Integer columnIndex, String newValue) {
		this.specificRowList.get(rowIndex).getSpecificList().set(columnIndex, StringUtils.trim(newValue));
		updateSpecificFieldControler();
	}

	@Override
	public void clearAllSelectedIndexList() {
		this.allSelectedIndexList.clear();
	}

}
