package model.analyze;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import model.analyze.beans.*;
import model.exceptions.ErrorCode;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.analyze.beans.specific.ConfigurationStructuredText;
import model.analyze.constants.ErrorTypeEnum;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.constants.TypeFilterTextEnum;
import model.analyze.interfaces.IWriteText;
import utils.JSonFactoryUtils;
import utils.KeyGenerator;
import utils.PathUtils;
import utils.RessourcesUtils;

/**
 * 
 * Permet de gérer les réglages et les instances en cours de l'utilisateur
 * 
 * @author jerem
 *
 */
public class UserSettings {

	private static final Logger logger = LoggerFactory.getLogger(UserSettings.class);
	private static UserSettings _instance;
	private final List<LinkedHashMap<String, String>> EDITING_CORPUS_TEXTS_LIST = new LinkedList<>();
	private final Map<String, String> EDITING_METAFIELD_MAP = new HashMap<>();
	private final Map<String, String> EDITING_FIELD_MAP = new HashMap<>();
	private final Map<FolderSettingsEnum, CurrentUserTexts> CURRENT_FOLDER_USER_TEXTS_MAP = new HashMap<>();
	private final Set<String> MAP_FILTER_KEY_LIST = Collections.synchronizedSet(new LinkedHashSet<>());
	// ERROR MAP
	private final List<LineError> LINES_ERROR_LIST = new LinkedList<>();
	private final Map<ErrorTypeEnum, Map<String, Map<Integer, String>>> MAP_TYPE_ERROR_KEYS_LIST_BY_FILE = new ConcurrentHashMap<>();
	private final Map<ErrorTypeEnum, Set<String>> MAP_TYPE_ERROR_KEYS_LIST = new HashMap<>();
	private final List<InconsistencyChangeText> INCONSISTENCY_CHANGE_TEXT_ERROR_LIST = new LinkedList<>();
	private final Set<MissingBaseCode> MISSING_BASE_CODE_LIST = new LinkedHashSet<>();
	private String currentEditKey;
	private Integer totalKeysStructuredTextError = null;
	private Integer totalBlankLineError = null;
	private ErrorTypeEnum currentErrorTypeFixed = null;
	private FilterCorpus lastFilterCorpus = null;
	private final Set<Configuration> configurationsList = new HashSet<>();

	private Configuration currentConfiguration;
	private String editingCorpusNameFile;

	public UserSettings() {
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.BLANK_LINE, Collections.synchronizedSet(new LinkedHashSet<>()));
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.META_BLANK_LINE, Collections.synchronizedSet(new LinkedHashSet<>()));
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.STRUCTURED_TEXT, Collections.synchronizedSet(new LinkedHashSet<>()));
	}

	/**
	 * Permet de se procurer l'instance statique
	 * 
	 * @return l'instance statique
	 */
	public static UserSettings getInstance() {
		if (null == _instance) {
			_instance = new UserSettings();
		}
		return _instance;
	}

	/**
	 * Permet de se procurer le nombre de texte en erreur
	 * 
	 * @return le nombre de texte en erreur
	 */
	public Integer getNbTextsError() {
		if (null == totalKeysStructuredTextError) {
			totalKeysStructuredTextError = this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.STRUCTURED_TEXT).size();
		}
		return totalKeysStructuredTextError;
	}

	/**
	 * Permet de savoir si il reste des erreurs
	 * 
	 * @param typeError type d'erreurs
	 * @return Vrai s'il reste des erreurs
	 */
	public Boolean haveErrorRemaining(ErrorTypeEnum typeError) {
		return !this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).isEmpty();
	}

	/**
	 * Permet d'ajouter une clé en erreur
	 * 
	 * @param typeError type d'erreur
	 * @param key       clé en erreur à ajouter
	 */
	public void addKeyError(ErrorTypeEnum typeError, String key) {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).add(key);
	}

	/**
	 * 
	 * Permet de se procurer la liste des clés en erreurs
	 * 
	 * @param typeError type de l'erreur
	 * @return la liste des clés en erreur
	 */
	public Set<String> getKeysInError(ErrorTypeEnum typeError) {
		return Collections.unmodifiableSet(this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError));
	}

	/**
	 * Permet de connaitre le nb de clés en erreur
	 * 
	 * @param typeError type d'erreurs
	 * @return Le nombre de clé en erreur
	 */
	public Integer getNbKeysInError(ErrorTypeEnum typeError) {
		return this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).size();
	}

	/**
	 * Permet de nettoyer une liste de clés en erreur
	 * 
	 * @param typeError type de l'erreur
	 */
	public void clearKeysInError(ErrorTypeEnum typeError) {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).clear();
	}

	/**
	 * Permet de supprimer la clé courante en erreur de la liste et de remettre à
	 * l'état initial le type et la clé en cours de correction
	 */
	public void deleteCurrentErrorKey() {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(currentErrorTypeFixed).remove(currentEditKey);
		currentEditKey = StringUtils.EMPTY;
		currentErrorTypeFixed = null;
	}

	/**
	 * Permet de vider la liste des clés en erreur pour les structures des textes
	 */
	public void clearKeysStructuredTextErrorList() {
		totalKeysStructuredTextError = null;
		clearKeysInError(ErrorTypeEnum.STRUCTURED_TEXT);
	}

	/**
	 * Permet de se procurer le nombre de ligne vide en erreur
	 * 
	 * @return le nombre de ligne vide en erreur
	 */
	public Integer getNbBlankLineError() {
		if (null == totalBlankLineError) {
			totalBlankLineError = getNbKeysInError(ErrorTypeEnum.BLANK_LINE);
		}
		return totalBlankLineError;
	}

	/**
	 * Permet de vider la liste des clés en erreur pour les lignes vides
	 */
	public void clearBlankLineErrorList() {
		totalBlankLineError = null;
		clearKeysInError(ErrorTypeEnum.BLANK_LINE);
	}

	/**
	 * Permet d'ajouter un texte structuré utilisateur
	 * 
	 * @param folder             dossier à modifier
	 * @param userStructuredText texte structuré utilisateur
	 */
	public void addUserStructuredText(FolderSettingsEnum folder, UserStructuredText userStructuredText) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addUserStructuredText(userStructuredText);
	}

	/**
	 * Permet de vider la liste des textes structurés utilisateur
	 * 
	 * @param folder dossier à modifier
	 */
	public void clearUserStructuredTextList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearUserStructuredTextList();
	}

	/**
	 * Permet de se procurer la liste des textes structuré utilisateur Une liste
	 * immutable est retourné, cette liste ne peut donc pas être modifier
	 * 
	 * @param folder dossier à modifier
	 * @return la liste des textes structuré utilisateurs immutable
	 */
	public List<UserStructuredText> getUserStructuredTextList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getUserStructuredTextList();
	}

	/**
	 * Permet de se procurer le texte structuré en fonction de sa clé unique
	 *
	 * @param folder dossier ou se trouve les textes
	 * @param uniqueKey Clé unique du texte
	 * @return le texte structuré Optional
	 */
	public Optional<UserStructuredText> getUserStructuredTextByKey(FolderSettingsEnum folder, String uniqueKey) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getUserStructuredTextList().stream()
				.filter(userStructuredText -> userStructuredText.getStructuredText().getUniqueKey().equals(uniqueKey))
				.findFirst();
	}

//	/**
//	 * Permet de se procurer le dossier
//	 *
//	 * @param setting Réglage dont on souhaite le dossier
//	 * @return le dossier
//	 */
//	public File getFolder(FolderSettingsEnum setting) {
//		if (FOLDER_SETTINGS.containsKey(setting)) {
//			return FOLDER_SETTINGS.get(setting);
//		}
//		return null;
//	}
//
//	/**
//	 * Permet de définir un dossier de réglages
//	 *
//	 * @param setting type de dossier
//	 * @param folder  dossier
//	 */
//	public void setFolder(FolderSettingsEnum setting, File folder) {
//		FOLDER_SETTINGS.put(setting, folder);
//	}

	/**
	 * Permet de se procurer la configuration courante
	 * 
	 * @return la configuration courante
	 */
	public Configuration getCurrentConfiguration() {
		return currentConfiguration;
	}

	/**
	 * Permet de définir la configuration courante
	 * 
	 * @param currentConfiguration configuration à définir
	 */
	public void setCurrentConfiguration(Configuration currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

	/**
	 * Permet de se procurer le nom du fichier du corpus en cours d'édition
	 * 
	 * @return le nom du fichier du corpus en cours d'édition
	 */
	public String getEditingCorpusNameFile() {
		return editingCorpusNameFile;
	}

	/**
	 * Permet de définir le nom du corpus en cours d'édition
	 * 
	 * @param editingCorpusNameFile nom du corpus en cours d'édition
	 */
	public void setEditingCorpusNameFile(String editingCorpusNameFile) {
		if (StringUtils.isNotBlank(editingCorpusNameFile)) {
			Optional<FileOrder> numberOfNameFile = getNumberOfNameFile(editingCorpusNameFile);
			if (numberOfNameFile.isEmpty()) {
				generateAndSaveNumberOfNameFiles(editingCorpusNameFile);
			}
		}
		this.editingCorpusNameFile = editingCorpusNameFile;
	}

	/**
	 * Permet d'ajouter des erreurs détecté au niveau des lignes Avant les erreurs
	 * structurelle. Provient du memory file
	 * 
	 * @param line  ligne
	 * @param index index dans le fichier
	 */
	public void addLineError(Path path, String line, Integer index) {
		LINES_ERROR_LIST.add(new LineError(path, index, line));
	}

	/**
	 * Permet de se procurer le nombre d'erreur au niveau des lignes
	 * 
	 * @return le nombre d'erreur
	 */
	public Integer getNbLineError() {
		return LINES_ERROR_LIST.size();
	}

	/**
	 * Permet de se procurer une ligne d'erreur
	 * 
	 * @param index index de la ligne d'erreur
	 * @return la ligne d'erreur
	 */
	public LineError getLineError(Integer index) {
		return LINES_ERROR_LIST.get(index);
	}

	/**
	 * Permet d'ajouter des erreurs détecté au niveau des changement de repérage de
	 * textes
	 * 
	 * 
	 * @param oldStructuredFieldNewText ancienne balise de changement de texte
	 * @param newStructuredFieldNewText nouvelle balise de changement de texte
	 * @param oldLine                   numéro de la ligne de l'ancienne balise
	 * @param newLine                   numéro de la ligne de la nouvelle balise
	 * @param nameFile                  nom du fichier
	 */
	public void addInconsistencyError(StructuredField oldStructuredFieldNewText,
			StructuredField newStructuredFieldNewText, Integer oldLine, Integer newLine, String nameFile) {
		INCONSISTENCY_CHANGE_TEXT_ERROR_LIST.add(new InconsistencyChangeText(oldStructuredFieldNewText,
				newStructuredFieldNewText, oldLine, newLine, nameFile));
	}

	/**
	 * Permet de se procurer un booléen pour savoir si il y a des erreurs
	 * potentielles au niveau des incohérences de changement de texte
	 * 
	 * @return Vrai si il y a des erreurs
	 */
	public Boolean haveInconsistencyErrors() {
		return !INCONSISTENCY_CHANGE_TEXT_ERROR_LIST.isEmpty();
	}

	/**
	 * Permet d'ajouter des erreurs détecté au niveau du repérage des balises
	 * 
	 * @param structuredFieldFound balise repéré
	 * @param line                 numéro de la ligne
	 * @param nameFile             nom du fichier
	 */
	public void addMissingBaseCodeError(StructuredField structuredFieldFound, Integer line, String nameFile) {
		MISSING_BASE_CODE_LIST.add(new MissingBaseCode(structuredFieldFound, line, nameFile));
	}

	/**
	 * Permet de se procurer un booléen pour savoir si il y a des erreurs
	 * potentielles au niveau du repérage des balises
	 * 
	 * @return Vrai si il y a des erreurs
	 */
	public Boolean haveMissingBaseCodeError() {
		return !MISSING_BASE_CODE_LIST.isEmpty();
	}

	/**
	 * Permet de se procurer la liste des erreurs d'inconsistance au niveau des
	 * textes
	 * 
	 * @return la liste non modifiables des erreurs d'inconsistance
	 */
	public List<InconsistencyChangeText> getInconsistencyErrorList() {
		return Collections.unmodifiableList(this.INCONSISTENCY_CHANGE_TEXT_ERROR_LIST);
	}

	/**
	 * Permet de se procurer la liste des erreurs d'inconsistance au niveau du
	 * rep�rage des balises
	 * 
	 * @return la liste non modifiables des erreurs d'inconsistance
	 */
	public List<MissingBaseCode> getMissingBaseCodeErrorList() {
		return Collections.unmodifiableList(new LinkedList<>(this.MISSING_BASE_CODE_LIST));
	}

	/**
	 * Permet de supprimer toutes les erreurs d'inconsistance de la liste
	 */
	public void clearInconsistencyErrorList() {
		INCONSISTENCY_CHANGE_TEXT_ERROR_LIST.clear();
	}

	/**
	 * Permet de supprimer toutes les erreurs d'inconsistance au niveau du repérage
	 * des balises de la liste
	 */
	public void clearMissingBaseCodeErrorList() {
		MISSING_BASE_CODE_LIST.clear();
	}

	/**
	 * Permet de mettre à jour la ligne en erreur
	 * 
	 * @param index     index de mise à jour
	 * @param lineError la ligne en erreur
	 */
	public void updateLineError(Integer index, LineError lineError) {
		LINES_ERROR_LIST.set(index, lineError);
	}

	/**
	 * Permet de supprimer toutes les erreurs de la liste
	 */
	public void clearLineErrorList() {
		LINES_ERROR_LIST.clear();
	}

	/**
	 * Permet d'ajouter les champs méta au corpus d'édition
	 * 
	 * @param metaFieldMap map des champs méta
	 */
	public void addMetaFieldsToEditingCorpus(Map<String, String> metaFieldMap) {
		getListFieldMetaFile().keySet().stream().forEach(key -> {
			if (metaFieldMap.containsKey(key)) {
				logger.debug(key + ":" + metaFieldMap.get(key));
				EDITING_METAFIELD_MAP.put(key, metaFieldMap.get(key));
			}
		});
	}

	/**
	 * Permet d'ajouter le texte en cours d'édition au corpus en cours
	 */
	public void addEditingTextToCurrentCorpus() {
		LinkedHashMap<String, String> orderedFieldMap = new LinkedHashMap<>();
		getListField(false, true, true, true).keySet().stream().forEach(key -> {
			if (EDITING_FIELD_MAP.containsKey(key)) {
				orderedFieldMap.put(key, EDITING_FIELD_MAP.get(key));
			}
		});
		EDITING_CORPUS_TEXTS_LIST.add(orderedFieldMap);
		EDITING_FIELD_MAP.clear();
	}

	/**
	 * Permet de fournir une liste des champs méta de la configuration courante
	 * 
	 * @return la liste des champs méta
	 */
	public Map<String, String> getListFieldMetaFile() {
		return getListField(true, false, false, false);
	}

	/**
	 * Permet de fournir une liste des champs commun de la configuration courante
	 * (non méta et non spécifique)
	 * 
	 * @return la liste des champs commun
	 */
	public Map<String, String> getListFieldCommonFile() {
		return getListField(false, false, false, true);
	}

	/**
	 * Permet de se procurer les champs en tête de la configuration spécifique
	 * désiré
	 * 
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		return getListField(getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getHeaderFieldList());
	}

	/**
	 * Permet de se procurer les champs de la configuration spécifique désiré
	 * 
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	public Map<String, String> getListFieldSpecific(Integer index) {
		return getListField(getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getTreatmentFieldList());
	}

	/**
	 * Permet de se procurer les champs en tête de la configuration spécifique
	 * désiré
	 *
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	public String getDelimiterSpecific(Integer index) {
		return getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getDelimiter();
	}

	/**
	 * Permet de connaitre le nombre maximum de configuration spécifiques
	 * 
	 * @return le nombre de configuration spécifiques
	 */
	public Integer getNbSpecificConfiguration() {
		return getCurrentConfiguration().getSpecificConfigurationList().size();
	}

	/**
	 * Permet de se procurer la totalité des champs
	 */
	public Map<String, String> getAllListField() {
		return getListField(true, true, true, true);
	}

	/**
	 * Permet de se procurer la totalité des champs sauf les meta
	 */
	public Map<String, String> getAllListFieldWithoutMeta() {
		return getListField(false, true, true, true);
	}

	/**
	 * Permet de se procurer la liste des champs souhaité
	 * 
	 * @param isMeta     méta
	 * @param isSpecific spécifiques
	 * @param isHeader   header
	 * @param isCommon   commun
	 * @return la liste des champs filtré
	 */
	private Map<String, String> getListField(Boolean isMeta, Boolean isSpecific, Boolean isHeader, Boolean isCommon) {
		List<String> fieldName = new ArrayList<>();
		if (isMeta) {
			fieldName.addAll(getCurrentConfiguration().getMetaFieldList());
		}
		if (isSpecific) {
			fieldName.addAll(getCurrentConfiguration().getSpecificFieldList());
		}
		if (isCommon) {
			fieldName.addAll(getCurrentConfiguration().getCommonFieldList());
		}
		if (isHeader) {
			fieldName.addAll(getCurrentConfiguration().getSpecificHeaderFieldList());
		}
		return getListField(fieldName);
	}

	/**
	 * Permet de se procurer la liste des champs sous forme de map
	 * 
	 * @param fieldsList liste des champs
	 * @return la map
	 */
	private Map<String, String> getListField(List<String> fieldsList) {
		HashMap<String, String> finalMap = new LinkedHashMap<>();
		if (null != getCurrentConfiguration()) {
			getCurrentConfiguration().getStructuredFieldList().stream()
					.sorted(Comparator.comparing(StructuredField::getOrder))
					.filter(s -> fieldsList.contains(s.getFieldName())).forEach(s -> {
						finalMap.put(s.getFieldName(), s.getFieldName() + " (" + s.getLabel() + ")");
					});
		}
		return finalMap;
	}

	/**
	 * Permet de créer un nouveau corpus
	 * 
	 * @param nameFile         nom du fichier
	 * @param metaFileFieldMap map des champ metafile
	 */
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		setEditingCorpusNameFile(nameFile);
		addMetaFieldsToEditingCorpus(metaFileFieldMap);
	}

	/**
	 * Permet de définir si un corpus est en cours d'édition
	 * 
	 * @return Vrai si un corpus est en cours d'édition
	 */
	public Boolean haveEditingCorpus() {
		return StringUtils.isNotBlank(getEditingCorpusNameFile());
	}

	/**
	 * Permet de supprimer le corpus en cours d'édition
	 */
	public void clearEditingCorpus() {
		setEditingCorpusNameFile(StringUtils.EMPTY);
		EDITING_FIELD_MAP.clear();
		EDITING_CORPUS_TEXTS_LIST.clear();
	}

	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param key Clé du champ à récupérer
	 * @return la valeur
	 */
	public String getFieldInEditingCorpus(String key) {
		if (EDITING_FIELD_MAP.containsKey(key)) {
			return EDITING_FIELD_MAP.get(key);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Permet de mettre à jour un champ de le corpus en cours d'édition
	 * 
	 * @param key   Clé
	 * @param value Valeur
	 */
	public void updateFieldInEditingCorpus(String key, String value) {
		EDITING_FIELD_MAP.put(key, value);
	}

	/**
	 * Permet de mettre à jour la liste des champs spécifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilisé
	 * @param specificFieldMap champ à mettre à jour
	 */
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		String delimiter = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getDelimiter();
		Map<String, String> transformedMap = specificFieldMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, v -> StringUtils.join(v.getValue(), delimiter)));
		EDITING_FIELD_MAP.putAll(transformedMap);
	}

	/**
	 * Permet de savoir s'il y a des champs spécifiques en erreur
	 * 
	 * @return Vrai si c'est le cas, faux sinon
	 */
	public Boolean haveErrorInSpecificFieldInEditingCorpus() {
		int nbConfiguration = currentConfiguration.getSpecificConfigurationList().size();
		for (int i = 0; i < nbConfiguration; i++) {
			Map<String, List<String>> mapOfSpecificFieldProcessedInEditingCorpus = getMapOfSpecificFieldProcessedInEditingCorpus(
					i);
			long nbListWithDifferentNbValues = mapOfSpecificFieldProcessedInEditingCorpus.values().stream()
					.map(list -> list.size()).distinct().count();
			if (nbListWithDifferentNbValues > 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Permet de se procurer la map des champs spécifique traité avec l'adjonction
	 * pour avoir le même nombre d'élément pour les textes en cours d'édition
	 * 
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifique
	 */
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		Map<String, List<String>> mapFinalOrdered = getMapOfSpecificFieldProcessedInEditingCorpus(index);
		return getSpecificFieldMap(mapFinalOrdered, index);
	}

	/**
	 * Permet de se procurer la map des champs spécifique traité avec l'adjonction
	 * pour avoir le même nombre d'élément pour les textes stockés
	 *
	 * @param keyText Clé du texte
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifique
	 */
	public Map<String, List<String>> getSpecificFieldInUserStructuredText(String keyText, Integer index) {
		Map<String, List<String>> mapFinalOrdered = getMapOfSpecificFieldProcessedInStructuredText(keyText, index);
		return getSpecificFieldMap(mapFinalOrdered, index);
	}

	/**
	 * Permet de se procurer la map des champs spécifique traité avec l'adjonction
	 * pour avoir le même nombre d'élément
	 *
	 * @param textSpecificFieldMap Map des champs spécifiques contenu par les textes
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifique
	 */
	public Map<String, List<String>> getSpecificFieldMap(Map<String, List<String>> textSpecificFieldMap, Integer index) {
		Integer nbMaxElement = textSpecificFieldMap.values().stream().map(values -> values.size())
				.max(Comparator.comparing(Integer::valueOf)).get();
		textSpecificFieldMap.entrySet().stream().filter(entry -> entry.getValue().size() < nbMaxElement).forEach(entry -> {
			while (entry.getValue().size() < nbMaxElement) {
				entry.getValue().add(StringUtils.SPACE);
			}
		});
		return textSpecificFieldMap;
	}

	/**
	 * Permet de se procurer la map des champs spécifiques qui a été traité pour le corpus en cours d'édition
	 * 
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifiques traité
	 */
	private Map<String, List<String>> getMapOfSpecificFieldProcessedInEditingCorpus(Integer index) {
		return getMapOfSpecificFieldProcessed(EDITING_FIELD_MAP, index);
	}

	/**
	 * Permet de se procurer la map des champs spécifiques qui a été traité pour un texte demandé par son identifiant
	 *
	 * @param keyText Clé du texte
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifiques traité
	 */
	private Map<String, List<String>> getMapOfSpecificFieldProcessedInStructuredText(String keyText, Integer index) {
		Optional<UserStructuredText> textFromKey = getTextFromKey(keyText);
		if (textFromKey.isPresent()) {
			UserStructuredText userStructuredText = textFromKey.get();
			Map<String, String> fieldValueMap = userStructuredText.getStructuredText().getListContent().stream()
					.collect(Collectors.toMap(Content::getKey, Content::getValue));
			return getMapOfSpecificFieldProcessed(fieldValueMap, index);
		}
		return new HashMap<>();
	}

	/**
	 * Permet de se procurer la map des champs spécifiques qui a été traité
	 * @param fieldValueMap Les valeurs brutes
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifiques traité
	 */
	private Map<String, List<String>> getMapOfSpecificFieldProcessed(Map<String, String> fieldValueMap, Integer index) {
		String delimiter = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getDelimiter();
		List<String> treatmentFieldList = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getTreatmentFieldList();
		Map<String, String> filteredMap = fieldValueMap.entrySet().stream()
				.filter(k -> treatmentFieldList.contains(k.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Map<String, List<String>> mapFinal = filteredMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				v -> new ArrayList<>(Arrays.asList(v.getValue().split(delimiter)))));
		Map<String, List<String>> mapFinalOrdered = new LinkedHashMap<>();
		List<StructuredField> listStructuredField = currentConfiguration.getStructuredFieldList().stream()
				.filter(sf -> treatmentFieldList.contains(sf.getFieldName()))
				.sorted(Comparator.comparing(StructuredField::getOrder)).collect(Collectors.toList());
		for (StructuredField structuredField : listStructuredField) {
			List<String> listValues = mapFinal.get(structuredField.getFieldName());
			if (null == listValues) {
				listValues = new ArrayList<>();
			}
			// listValues.removeIf(v -> StringUtils.isBlank(v));
			mapFinalOrdered.put(structuredField.getFieldName(), listValues);
		}
		return mapFinalOrdered;
	}

	/**
	 * Permet d'écrire le corpus sur le disque
	 * 
	 * @param writer le writer
	 * @throws IOException erreur d'entrée sortie
	 */
	public void writeCorpus(IWriteText writer) throws IOException {
		// On écrit les champ meta
		writeLines(writer, EDITING_METAFIELD_MAP);
		// On écrit les textes
		for (LinkedHashMap<String, String> text : EDITING_CORPUS_TEXTS_LIST) {
			writeLines(writer, text);
			writer.addBreakLine();
		}
	}

	/**
	 * Permet d'écrire le corpus sur le disque
	 * 
	 * @param writer             le writer
	 * @param structuredTextList Liste des textes structurés à écrire
	 * @throws IOException erreur d'entrée sortie
	 */
	public void writeCorpus(IWriteText writer, List<StructuredText> structuredTextList) throws IOException {
		Map<String, String> metaFieldMap = getListFieldMetaFile().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, s -> StringUtils.EMPTY));
		Map<String, String> listAllCommonField = getListField(false, true, true, true);
		// On écrit les champ meta
		writeLines(writer, metaFieldMap);
		// On écrit les textes structurés
		for (StructuredText structuredText : structuredTextList) {
			Map<String, String> text = new LinkedHashMap<>();
			listAllCommonField.keySet().forEach(key -> text.put(key, structuredText.getContent(key)));
			writeLines(writer, text);
			writer.addBreakLine();
		}
	}

	/**
	 * Permet d'écrire les lignes d'une map de clé valeur
	 * 
	 * @param writer     le writer
	 * @param mapToWrite la map à écrire
	 * @throws IOException Exception d'entré sorti
	 */
	private void writeLines(IWriteText writer, Map<String, String> mapToWrite) throws IOException {
		Iterator<Entry<String, String>> iterator = mapToWrite.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			String lineToWrite = createLine(entry.getKey(), entry.getValue());
			writer.writeLineWithBreakLineAfter(lineToWrite);
		}
	}

	/**
	 * Permet de créer la ligne à écrire dans le fichier
	 * 
	 * @param key   clé du champ
	 * @param value valeur du champ
	 * @return la ligne
	 */
	private String createLine(String key, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentConfiguration.getBaseCode());
		sb.append(key);
		sb.append(value);
		return sb.toString();
	}

	/**
	 * Permet d'ajouter les memory files
	 * 
	 * @param folder          dossier à modifier
	 * @param memoryFilesList liste des memory files à ajouter
	 */
	public void addMemoryFilesList(FolderSettingsEnum folder, List<MemoryFile> memoryFilesList) {
		memoryFilesList.stream().forEach(mf -> this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addMemoryFile(mf));
	}

	/**
	 * Permet de vider la liste des memory files
	 * 
	 * @param folder dossier à modifier
	 */
	public void clearMemoryFilesList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearMemoryFileList();
	}

	/**
	 * Permet de corriger toutes les lignes en erreur dans tous les fichiers en
	 * mémoire
	 * 
	 * @throws IOException
	 */
	public void fixedErrorLinesInAllMemoryFiles() throws IOException {
		List<Path> pathList = LINES_ERROR_LIST.stream().map(lineError -> lineError.getPath()).distinct()
				.collect(Collectors.toList());
		for (Path path : pathList) {
			Optional<MemoryFile> findFirstMemoryFile = this.CURRENT_FOLDER_USER_TEXTS_MAP
					.get(FolderSettingsEnum.FOLDER_ANALYZE).getMemoryFileList().stream()
					.filter(memoryFile -> path.equals(memoryFile.getPath())).findFirst();
			if (!findFirstMemoryFile.isPresent()) {
				continue;
			}
			MemoryFile memoryFile = findFirstMemoryFile.get();
			fixedErrorLinesInOneMemoryFile(memoryFile);
		}
	}

	/**
	 * Permet de corriger les lignes en erreurs dans un fichier en mémoire
	 * 
	 * @param memoryFile fichier en memoire
	 * @throws IOException
	 */
	private void fixedErrorLinesInOneMemoryFile(MemoryFile memoryFile) throws IOException {
		List<LineError> errorLines = LINES_ERROR_LIST.stream()
				.filter(errorLine -> memoryFile.getPath().equals(errorLine.getPath())).collect(Collectors.toList());
		errorLines.forEach(lineError -> memoryFile.updateLine(lineError.getIndex(), lineError.getLineFixed()));
		try (Writer writer = new Writer(memoryFile.getPath())) {
			memoryFile.writeLines(writer);
		}
	}

	/**
	 * Permet de charger un texte en erreur pour la correction
	 * 
	 * @param key       Clé du texte
	 * @param errorType Type d'erreur
	 */
	public void loadErrorText(String key, ErrorTypeEnum errorType) {
		this.currentErrorTypeFixed = errorType;
		loadText(key, FolderSettingsEnum.FOLDER_ANALYZE);
	}

	/**
	 * Permet de se procurer les modifications en cours pour sauvegarder
	 * 
	 * @return les modifications en cours
	 */
	public SaveCurrentFixedText getSaveCurrentFixedText() {
		SaveCurrentFixedText save = new SaveCurrentFixedText();
		save.setPath(UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE).orElseGet(null));
		save.setKeysStructuredTextErrorSet(
				Collections.unmodifiableSet(this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.STRUCTURED_TEXT)));
		save.setUserStructuredTextList(
				this.CURRENT_FOLDER_USER_TEXTS_MAP.get(FolderSettingsEnum.FOLDER_ANALYZE).getUserStructuredTextList());
		save.setKeysBlankLineErrorSet(Collections.unmodifiableSet(
				Collections.unmodifiableSet(this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.BLANK_LINE))));
		save.setKeysMetaBlankLineErrorSet(Collections.unmodifiableSet(
				Collections.unmodifiableSet(this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.META_BLANK_LINE))));
		return save;
	}

	/**
	 * Permet de se procurer la configuration de l'utilisateur
	 * 
	 * @return la configuration de l'utilisateur
	 */
	public CurrentUserConfiguration getUserConfiguration() {
		CurrentUserConfiguration save = new CurrentUserConfiguration();
		Optional<File> folderTexts = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_TEXTS);
		if (folderTexts.isPresent()) {
			save.setLibraryPath(folderTexts.get().toPath());
		}
		Optional<File> folderConfigurations = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS);
		if (folderConfigurations.isPresent()) {
			save.setConfigurationPath(folderConfigurations.get().toPath());
		}
		Configuration currentConfiguration = getCurrentConfiguration();
		if (null != currentConfiguration) {
			save.setDefaultConfiguration(currentConfiguration.getName());
		}
		return save;
	}

	/**
	 * Permet de restaurer l'environnement de la configuration de l'utilisateur
	 * 
	 * @param save la configuration de l'utilisateur a restaurer
	 */
	public void restoreUserConfiguration(CurrentUserConfiguration save) {
		if (null != save.getLibraryPath()) {
			UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_TEXTS, save.getLibraryPath().toFile());
		}
		if (null != save.getConfigurationPath()) {
			UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS, save.getConfigurationPath().toFile());
		}
		try {
			loadConfigurationsList();
			Optional<Configuration> findFirstConfiguration = configurationsList.stream().filter(
					c -> null != save.getDefaultConfiguration() && save.getDefaultConfiguration().equals(c.getName()))
					.findFirst();
			if (findFirstConfiguration.isPresent()) {
				this.setCurrentConfiguration(findFirstConfiguration.get());
			} else if (!configurationsList.isEmpty()) {
				this.setCurrentConfiguration(configurationsList.stream().findFirst().get());
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Permet de restaurer l'environnement en cours de correction
	 * 
	 * @param save sauvegarde a restaurer
	 */
	public void restoreCurrentFixedTest(SaveCurrentFixedText save) {
		UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_ANALYZE, save.getPath());
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.STRUCTURED_TEXT).addAll(save.getKeysStructuredTextErrorSet());
		save.getUserStructuredTextList().stream().forEach(ust -> this.CURRENT_FOLDER_USER_TEXTS_MAP
				.get(FolderSettingsEnum.FOLDER_ANALYZE).addUserStructuredText(ust));
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.BLANK_LINE).addAll(save.getKeysBlankLineErrorSet());
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.META_BLANK_LINE).addAll(save.getKeysMetaBlankLineErrorSet());
	}

	/**
	 * Permet d'appliquer les modifications au texte structuré
	 */
	public void applyCurrentTextToStructuredText(FolderSettingsEnum folderType) {
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(text -> this.currentEditKey.equals(text.getKey()))
				.findFirst();
		if (optionalUserStructuredText.isPresent()) {
			StructuredText structuredText = optionalUserStructuredText.get().getStructuredText();
			EDITING_FIELD_MAP.entrySet().stream()
					.forEach(entry -> structuredText.modifyContent(entry.getKey(), entry.getValue()));
			if (FolderSettingsEnum.FOLDER_ANALYZE.equals(folderType)) {
				deleteCurrentErrorKey();
			}
		}
	}

	/**
	 * Permet d'écrire les textes qui ont été corrigé
	 * 
	 * @param writer writer
	 * @return Vrai si le fichier a pu être écris, Faux sinon et dans ce cas il
	 *         faudra supprimer le fichier car il ne doit rien contenir
	 * @throws IOException
	 */
	public Boolean writeText(FolderSettingsEnum folderType, IWriteText writer, String file) throws IOException {
		List<UserStructuredText> userStructuredTextList = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(ust -> ust.getFileName().equals(file))
				.collect(Collectors.toList());
		if (userStructuredTextList.isEmpty()) {
			// si aucun texte trouvé, on retourne false pour supprimer le fichier
			return false;
		}
		Map<String, String> listFieldMetaFile = getListFieldMetaFile();
		StructuredText structuredText = userStructuredTextList.get(0).getStructuredText();
		Map<String, String> mapFieldMetaFileToWrite = getMapToWrite(structuredText, listFieldMetaFile.keySet());
		writeLines(writer, mapFieldMetaFileToWrite);
		Map<String, String> listFieldCommonFile = getListField(false, true, true, true);
		List<UserStructuredText> orderedUserStructuredText = userStructuredTextList.stream()
				.sorted(Comparator.comparing(UserStructuredText::getNumber)).collect(Collectors.toList());
		for (UserStructuredText userStructuredText : orderedUserStructuredText) {
			Map<String, String> mapFieldCommonFileToWrite = getMapToWrite(userStructuredText.getStructuredText(),
					listFieldCommonFile.keySet());
			writeLines(writer, mapFieldCommonFileToWrite);
			writer.addBreakLine();
		}
		return true;
	}

	/**
	 * Permet de nettoyer les informations après l'enregistrement des fichiers
	 */
	public void clearAfterWriteFixedText() {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(FolderSettingsEnum.FOLDER_ANALYZE).clearUserStructuredTextList();
		this.clearEditingCorpus();
		this.clearKeysStructuredTextErrorList();
		this.editingCorpusNameFile = StringUtils.EMPTY;
	}

	/**
	 * Permet de se procurer une map des clés valeur pour écriture
	 * 
	 * @param structureText Structure text
	 * @param key           Clé à retenir
	 * @return la map à écrire
	 */
	private Map<String, String> getMapToWrite(StructuredText structureText, Set<String> key) {
		Map<String, String> map = new LinkedHashMap<>();
		key.stream().forEach(k -> {
			StringBuilder newKey = new StringBuilder(currentConfiguration.getBaseCode());
			newKey.append(k);
			map.put(k, structureText.getContent(k));
		});
		return map;
	}

	/**
	 * Permet de nettoyer les informations stockés suite à une précédente analyse ou
	 * chargement de textes
	 */
	public void clearAllSession(FolderSettingsEnum folder) {
		clearCurrentFolderUserTexts(folder);
		clearEditingCorpus();
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folder)) {
			clearKeyFilteredList();
		}
		if (FolderSettingsEnum.FOLDER_ANALYZE.equals(folder)) {
			clearInconsistencyErrorList();
			clearMissingBaseCodeErrorList();
			clearLineErrorList();
			clearKeysStructuredTextErrorList();
			clearBlankLineErrorList();
			clearKeysInError(ErrorTypeEnum.META_BLANK_LINE);
		}
	}

	/**
	 * Permet de se procurer le nouveau repertoire incluant la configuration dans le
	 * chemin
	 * 
	 * @return le repertoire complet pour sauvegarder les textes
	 */
	public File getDirectoryForSaveTextsInLibrary() {
		Optional<File> folderTexts = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_TEXTS);
		if (folderTexts.isPresent() && null != this.currentConfiguration) {
			File newDirectory = new File(folderTexts.get().getAbsolutePath(), this.currentConfiguration.getName());
			if (!newDirectory.exists()) {
				newDirectory.mkdirs();
			}
			return newDirectory;
		}
		return null;
	}

	/**
	 * Permet de clear la map de la liste des textes
	 */
	public void clearCurrentFolderUserTextsMap() {
		CURRENT_FOLDER_USER_TEXTS_MAP.clear();
		clearCurrentFolderUserTexts(FolderSettingsEnum.FOLDER_ANALYZE);
		clearCurrentFolderUserTexts(FolderSettingsEnum.FOLDER_TEXTS);
	}

	/**
	 * Permet de nettoyer la liste des textes du dossier courant.
	 * 
	 * @param folder Dossier courant
	 */
	public void clearCurrentFolderUserTexts(FolderSettingsEnum folder) {
		CURRENT_FOLDER_USER_TEXTS_MAP.put(folder, new CurrentUserTexts());
	}

	/**
	 * Permet d'ajouter un fichier structuré
	 * 
	 * @param folder         dossier à modifier
	 * @param structuredFile fichier structuré
	 */
	public void addStructuredFile(FolderSettingsEnum folder, StructuredFile structuredFile) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addStructuredFile(structuredFile);
	}

	/**
	 * Permet de vider la liste des fichier structuré utilisateur
	 * 
	 * @param folder dossier à modifier
	 */
	public void clearStructuredFileList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearStructuredFileList();
	}

	/**
	 * Permet de se procurer la liste des fichiers structuré Une liste immutable est
	 * retourné, cette liste ne peut donc pas être modifier
	 * 
	 * @param folder dossier à modifier
	 * @return la liste des fichier structuré utilisateurs immutable
	 */
	public List<StructuredFile> getStructuredFileList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getStructuredFileList();
	}

	/**
	 * Permet d'ajouter un texte structuré spécifique
	 * 
	 * @param folder                      dossier à modifier
	 * @param configurationStructuredText un texte structuré spécifique
	 */
	public void addConfigurationStructuredText(FolderSettingsEnum folder,
			ConfigurationStructuredText configurationStructuredText) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addConfigurationStructuredText(configurationStructuredText);
	}

	/**
	 * Permet de vider la liste des textes structurés spécifique
	 * 
	 * @param folder dossier à modifier
	 */
	public void clearConfigurationStructuredTextList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearConfigurationStructuredTextList();
	}

	/**
	 * Permet de se procurer la liste des textes structurés spécifique Une liste
	 * immutable est retourné, cette liste ne peut donc pas être modifier
	 * 
	 * @param folder dossier à modifier
	 * @return la liste des textes structurés spécifique utilisateurs immutable
	 */
	public List<ConfigurationStructuredText> getConfigurationStructuredTextList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getConfigurationStructuredTextList();
	}

	/**
	 * Permet de se procurer la liste des clés filtrés pour l'affichage des
	 * résultats
	 * 
	 * @return la liste de clés filtrés
	 */
	public List<String> getKeysFilteredList() {
		return Collections.unmodifiableList(new LinkedList<>(this.MAP_FILTER_KEY_LIST));
	}

	/**
	 * Permet d'ajouter une clé à la liste des clés filtrés
	 * 
	 * @param key Clé à ajouter
	 */
	public synchronized void addKeyToFilteredList(String key) {
		this.MAP_FILTER_KEY_LIST.add(key);
	}

	/**
	 * Permet de vider la liste des clés filtrés
	 */
	public void clearKeyFilteredList() {
		this.MAP_FILTER_KEY_LIST.clear();
	}

	/**
	 * Permet de charger un texte en cours d'édition
	 * 
	 * @param key clé du texte
	 */
	public void loadFilteredText(String key) {
		loadText(key, FolderSettingsEnum.FOLDER_TEXTS);
	}

	/**
	 * Permet de se procurer le texte utilisateur si il existe
	 * @param key Clé du texte
	 * @return le texte utilisateur si il existe
	 */
	public Optional<UserStructuredText> getTextFromKey(String key) {
		return getTextFromKey(key, FolderSettingsEnum.FOLDER_TEXTS);
	}
//
//	/**
//	 * Permet de trier une liste de clé
//	 * @param keyTexts liste des clés triés
//	 * @return la liste des clés trié
//	 */
//	public Set<String> getOrderedKeyText(Collection<String> keyTexts) {
//		return keyTexts.stream()
//				.map(this::getTextFromKey)
//				.filter(Optional::isPresent)
//				.map(Optional::get)
//				.sorted(Comparator.comparing(UserStructuredText::getNumber))
//				.map(UserStructuredText::getKey)
//				.collect(Collectors.toCollection(LinkedHashSet::new));
//	}



	/**
	 * Permet de charger un texte
	 * 
	 * @param key        clé du texte
	 * @param folderType Type du dossier
	 */
	private void loadText(String key, FolderSettingsEnum folderType) {
		this.clearEditingCorpus();
		this.currentEditKey = key;
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(text -> key.equals(text.getKey())).findFirst();
		if (optionalUserStructuredText.isPresent()) {
			setEditingCorpusNameFile(FilenameUtils.removeExtension(optionalUserStructuredText.get().getFileName()));
			optionalUserStructuredText.get().getStructuredText().getListContent()
					.forEach(content -> this.updateFieldInEditingCorpus(content.getKey(), content.getValue()));
		} else {
			logger.error(String.format("Clé %s non trouvé", key));
		}
	}

	/**
	 * Permet de se procurer le texte utilisateur si il existe
	 * @param key Clé du texte
	 * @param folderType Type du dossier
	 * @return le texte utilisateur si il existe
	 */
	private Optional<UserStructuredText> getTextFromKey(String key, FolderSettingsEnum folderType) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(text -> key.equals(text.getKey())).findFirst();
	}

	/**
	 * Permet de supprimer un texte d'un corpus (suppression logique)
	 * 
	 * @param key        Clé du texte à supprimer
	 * @param folderType type du dossier
	 */
	public void deleteText(String key, FolderSettingsEnum folderType) {
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(text -> key.equals(text.getKey())).findFirst();
		if (optionalUserStructuredText.isPresent()) {
			this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
					.deleteTextOfUserStructuredTextList(optionalUserStructuredText.get());
			this.MAP_FILTER_KEY_LIST.remove(key);
		}
	}

	/**
	 * Permet de se procurer le nom du corpus associé à la clé du texte
	 * 
	 * @param key        Clé du texte
	 * @param folderType type du dossier
	 * @return Le nom du corpus
	 */
	public String getCorpusNameOfText(String key, FolderSettingsEnum folderType) {
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType)
				.getUserStructuredTextList().stream().filter(text -> key.equals(text.getKey())).findFirst();
		if (optionalUserStructuredText.isPresent()) {
			return optionalUserStructuredText.get().getFileName();
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Permet de se procurer la liste des corpus
	 * 
	 * @param folderType type de dossier
	 * @return la liste des corpus
	 */
	public List<String> getAllCorpusName(FolderSettingsEnum folderType) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType).getUserStructuredTextList().stream()
				.map(ust -> ust.getFileName()).distinct().sorted().collect(Collectors.toList());
	}

	/**
	 * Permet de filtrer sur les corpus
	 * 
	 * @param filterCorpus filtre sur le corpus
	 * @param folderType   Type de dossier sur lequel on souhaite rechercher
	 */
	public void applyFilterOnCorpusForFolderText(FilterCorpus filterCorpus, FolderSettingsEnum folderType) {
		Set<String> mapKeyFilteredList = new LinkedHashSet<>();
		Set<UserStructuredText> listUserStructuredText = new LinkedHashSet<>();
		this.lastFilterCorpus = filterCorpus;
		if (null != filterCorpus) {
			if (StringUtils.isNotBlank(filterCorpus.getCorpusName())) {
				listUserStructuredText
						.addAll(this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType).getUserStructuredTextList().stream()
								.filter(ust -> filterCorpus.getCorpusName().equals(ust.getFileName()))
								.collect(Collectors.toList()));
			} else {
				listUserStructuredText
						.addAll(this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType).getUserStructuredTextList());
			}
			if (!listUserStructuredText.isEmpty()) {
				listUserStructuredText.stream().sorted(Comparator.comparing(UserStructuredText::getFileName)
						.thenComparing(UserStructuredText::getNumber)).forEach(ust -> {
							if (filterCorpus.getFiterTextList().stream().allMatch(getPredicateForFilterText(ust))) {
								mapKeyFilteredList.add(ust.getKey());
							}
						});
			}
			MAP_FILTER_KEY_LIST.clear();
			MAP_FILTER_KEY_LIST.addAll(mapKeyFilteredList);
		} else {
			addAllUserStructuredTextToKeyFilterList(folderType);
		}
	}

	/**
	 * Permet de se procurer le filtre sur le filter text pour filtrer sur les
	 * textes structuré utilisateurs
	 * 
	 * @param userStructuredText texte à vérifier
	 * @return le prédicat à appliquer
	 */
	private Predicate<FilterText> getPredicateForFilterText(UserStructuredText userStructuredText) {
		return new Predicate<>() {

			@Override
			public boolean test(FilterText filterText) {
				if (StringUtils.isNotBlank(filterText.getField()) && null != filterText.getTypeFilter()
						&& null != filterText.getValue()) {
					String content = userStructuredText.getStructuredText().getContent(filterText.getField());
					if (TypeFilterTextEnum.CONTAINS.equals(filterText.getTypeFilter())) {
						return StringUtils.defaultString(content).contains(filterText.getValue());
					} else if (TypeFilterTextEnum.EQUAL.equals(filterText.getTypeFilter())) {
						return StringUtils.defaultString(content).equals(filterText.getValue());
					}
				}
				return false;
			}
		};
	}

	/**
	 * Permet de réinitialiser le filtre avec l'ensemble des textes (raz)
	 * 
	 * @param folderType Type de dossier
	 */
	public void addAllUserStructuredTextToKeyFilterList(FolderSettingsEnum folderType) {
		MAP_FILTER_KEY_LIST.clear();
		MAP_FILTER_KEY_LIST.addAll(this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType).getUserStructuredTextList()
				.stream().map(ust -> ust.getKey()).collect(Collectors.toList()));
	}

	/**
	 * Permet d'ajouter un texte au corpus courant
	 * 
	 * @param folderType type de dossier
	 */
	public void addTextToCurrentCorpus(FolderSettingsEnum folderType) {
		CurrentUserTexts currentUserTexts = this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folderType);
		StringBuilder sb = new StringBuilder();
		sb.append(getEditingCorpusNameFile());
		sb.append(".txt");
		String corpusName = sb.toString();
		Integer number = (int) currentUserTexts.getUserStructuredTextList().stream()
				.filter(ust -> corpusName.equals(ust.getFileName())).count() + 1;
		StructuredText structuredText = new StructuredText(number);
		getListField(true, true, true, true).keySet().stream().forEach(key -> {
			if (EDITING_FIELD_MAP.containsKey(key)) {
				structuredText.modifyContent(key, EDITING_FIELD_MAP.get(key));
			}
		});
		StringBuilder keyTextBuilder = new StringBuilder();
		keyTextBuilder.append(corpusName);
		keyTextBuilder.append(number);
		structuredText.setUniqueKey(KeyGenerator.generateKey(keyTextBuilder.toString()));
		Optional<FileOrder> numberOfNameFile = getNumberOfNameFile(getEditingCorpusNameFile());
		UserStructuredText userStructuredText = new UserStructuredText(corpusName, number, numberOfNameFile.get().getNumber(), structuredText);
		currentUserTexts.addUserStructuredText(userStructuredText);
		applyFilterOnCorpusForFolderText(this.lastFilterCorpus, folderType);
	}

	/**
	 * Permet de préparer pour l'ajout d'un texte
	 */
	public void cleanCurrentEditingCorpusForAddText() {
		getListField(false, true, true, true).keySet().stream().forEach(key -> {
			if (EDITING_FIELD_MAP.containsKey(key)) {
				EDITING_FIELD_MAP.remove(key);
			}
		});
	}

	/**
	 * Permet de charger la liste des configurations
	 * 
	 * @throws IOException erreur d'entrée sortie
	 */
	private void loadConfigurationsList() throws IOException {
		Optional<File> configurationFolder = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS);
		if (configurationFolder.isPresent() && configurationFolder.get().exists()) {
			Files.walkFileTree(Paths.get(configurationFolder.get().toURI()), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (!Files.isDirectory(file)) {
						logger.debug(String.format("Loading Configuration %s", file.toString()));
						try (InputStream is = FileUtils.openInputStream(file.toFile())) {
							Configuration configurationFromJsonFile = JSonFactoryUtils
									.createConfigurationFromJsonFile(is);
							configurationsList.add(configurationFromJsonFile);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	/**
	 * Permet de se procurer la liste des configurations possibles
	 * 
	 * @return la liste des configurations
	 */
	public Set<Configuration> getConfigurationList() {
		return Collections.unmodifiableSet(this.configurationsList);
	}

	/**
	 * Permet de sauvegarder les erreurs de manière à ce que celle ci soit trié
	 */
	public void saveAllErrorForFixed() {
		if (!this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.isEmpty()) {
			this.MAP_TYPE_ERROR_KEYS_LIST.clear();
			this.MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.BLANK_LINE, new LinkedHashSet<>());
			this.MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.META_BLANK_LINE, new LinkedHashSet<>());
			this.MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.STRUCTURED_TEXT, new LinkedHashSet<>());
			saveErrorForFixed(ErrorTypeEnum.STRUCTURED_TEXT);
			saveErrorForFixed(ErrorTypeEnum.BLANK_LINE);
			saveErrorForFixed(ErrorTypeEnum.META_BLANK_LINE);
		}
		this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.clear();
	}

	/**
	 * Permet de sauvegarder les erreurs demandé de manière trié
	 * 
	 * @param typeError type d'erreur à trier
	 */
	private void saveErrorForFixed(ErrorTypeEnum typeError) {
		if (this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.containsKey(typeError)) {
			Map<String, Map<Integer, String>> errorMap = this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.get(typeError);
			errorMap.keySet().stream().sorted().forEach(keyFile -> {
				this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).addAll(errorMap.get(keyFile).entrySet().stream()
						.sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList()));
			});
		}
	}

	/**
	 * Permet de sauvegarder le type d'erreur en fonction du fichier
	 * 
	 * @param typeError type d'erreur
	 * @param file      le fichier
	 * @param number    le numéro
	 * @param keyError  la clé d'erreur
	 */
	public void addKeyErrorByFile(ErrorTypeEnum typeError, String file, Integer number, String keyError) {
		if (!this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.containsKey(typeError)) {
			this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.put(typeError, new ConcurrentHashMap<>());
		}
		if (!this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.get(typeError).containsKey(file)) {
			this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.get(typeError).put(file, new HashMap<>());
		}
		this.MAP_TYPE_ERROR_KEYS_LIST_BY_FILE.get(typeError).get(file).put(number, keyError);
	}

	/**
	 * Permet de se procurer le numéro d'un nom de fichier
	 * @param nameFile nom du fichier
	 * @return le numéro du fichier optional
	 */
	private Optional<FileOrder> getNumberOfNameFile(String nameFile) {
		String realName = nameFile + ".txt";
		File orderFile = getFileOfOrders();
		return getFilesOrder(orderFile).getFileOrderSet().stream().filter(x -> x.getNameFile().equals(realName)).findFirst();
	}

	/**
	 * Permet de générer un numéro pour un nom de fichier
	 * @param nameFile nom du fichier
	 * @return le nom du fichier
	 */
	private void generateAndSaveNumberOfNameFiles(String nameFile) {
		String realName = nameFile + ".txt";
		File orderFile = getFileOfOrders();
		FilesOrder filesOrderFromJsonFile = getFilesOrder(orderFile);
		generateNumberOfNameFileAndSaveConfig(filesOrderFromJsonFile, realName, orderFile);
	}

	/**
	 * Permet de définir le numéro du fichier
	 * @param memoryFile memory file dont on souhaite sauvegarder le numéro
	 */
	public void setNumberOfMemoryFile(MemoryFile memoryFile) {
		if (Objects.isNull(memoryFile.getNumber())) {
			File orderFile = getFileOfOrders();
			FilesOrder filesOrderFromJsonFile = getFilesOrder(orderFile);
			Optional<FileOrder> optionalFileOrder = filesOrderFromJsonFile.getFileOrderSet().stream()
					.filter(x -> x.getNameFile().equals(memoryFile.nameFile()))
					.findFirst();
			optionalFileOrder.ifPresentOrElse(x -> memoryFile.setNumber(x.getNumber()),
					() -> memoryFile.setNumber(generateNumberOfNameFileAndSaveConfig(filesOrderFromJsonFile, memoryFile.nameFile(), orderFile)));
		}
	}

	/**
	 * Permet de se procurer le fichier des ordres
	 * @return le fichier des ordres
	 */
	private File getFileOfOrders() {
		String rootPath = PathUtils.getCaerusFolder();
		File directory = new File(rootPath, RessourcesUtils.FOLDER_ORDER_CONFIGURATION);
		return new File(directory, getCurrentConfiguration().getConfigurationOrderNameFile());
	}

	/**
	 * Permet de se procurer l'objet qui gére les ordres
	 * @param orderFile fichier des ordres
	 * @return l'objet qui géres les ordres
	 */
	private FilesOrder getFilesOrder(File orderFile) {
		FilesOrder filesOrderFromJsonFile;
		try (InputStream is = FileUtils.openInputStream(orderFile)) {
			filesOrderFromJsonFile = JSonFactoryUtils.createFilesOrderFromJsonFile(is);
		} catch (IOException exception) {
			throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.exceptionParent(exception)
					.build());
		}
		return filesOrderFromJsonFile;
	}

	/**
	 * Permet de générer un numéro et de sauvegarder la configuration
	 * @param filesOrderFromJsonFile objet pour stocker les ordres
	 * @param nameFile Nom du fichier
	 * @param orderFile fichier de sauvegarde des ordres
	 * @return le numéro
	 */
	private Integer generateNumberOfNameFileAndSaveConfig(FilesOrder filesOrderFromJsonFile, String nameFile, File orderFile) {
		FileOrder fileOrder = new FileOrder();
		fileOrder.setNameFile(nameFile);
		String numericNameFile = nameFile.replaceAll(".txt", "");
		if (isInt(numericNameFile)) {
			fileOrder.setNumber(Integer.parseInt(numericNameFile));
		} else {
			Integer number = filesOrderFromJsonFile.getFileOrderSet().stream()
					.filter(x -> x.getNameFile().equals(nameFile))
					.findFirst()
					.map(FileOrder::getNumber)
					.orElse(filesOrderFromJsonFile.getMaxNumber());
			fileOrder.setNumber(number);
		}
		filesOrderFromJsonFile.getFileOrderSet().add(fileOrder);
		try {
			JSonFactoryUtils.createJsonInFile(filesOrderFromJsonFile, orderFile);
		} catch (IOException exception) {
			throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.exceptionParent(exception)
					.build());
		}
		return fileOrder.getNumber();
	}

	/**
	 * Permet de savoir si la chaine de caractére est un entier
	 * @param s chaine de caractére
	 * @return Vrai si c'est un entier
	 */
	private boolean isInt(String s)
	{
		try {
			int i = Integer.parseInt(s);
			return true;
		} catch(NumberFormatException er) {
			return false;
		}
	}

}
