package analyze;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.Configuration;
import analyze.beans.LineError;
import analyze.beans.MemoryFile;
import analyze.beans.SaveCurrentFixedText;
import analyze.beans.SpecificConfiguration;
import analyze.beans.StructuredField;
import analyze.beans.StructuredText;
import analyze.beans.UserStructuredText;
import analyze.constants.ErrorTypeEnum;
import analyze.constants.FolderSettingsEnum;
import analyze.interfaces.IWriteText;
import ihm.utils.ConfigurationUtils;

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
	private final Map<FolderSettingsEnum, File> FOLDER_SETTINGS = new HashMap<FolderSettingsEnum, File>();
	private final List<LinkedHashMap<String, String>> EDITING_CORPUS_TEXTS_LIST = new LinkedList<LinkedHashMap<String, String>>();
	private final Map<String, String> EDITING_METAFIELD_MAP = new HashMap<String, String>();
	private final Map<String, String> EDITING_FIELD_MAP = new HashMap<String, String>();
	private final List<MemoryFile> MEMORY_FILES_LIST = new ArrayList<>();
	private final List<UserStructuredText> USER_STRUCTURED_TEXT_LIST = new ArrayList<>();
	// ERROR MAP
	private final List<LineError> LINES_ERROR_LIST = new LinkedList<LineError>();
	private final Set<String> KEYS_STRUCTURED_TEXT_ERROR_LIST = new HashSet<>();
	private final Set<String> KEYS_BLANK_LINE_ERROR_LIST = new HashSet<>();
	private final Set<String> KEYS_META_BLANK_LINE_ERROR_LIST = new HashSet<>();
	private String currentErrorKey;
	private Integer totalKeysStructuredTextError = null;
	private Integer totalBlankLineError = null;
	private ErrorTypeEnum currentErrorTypeFixed = null;

	private Configuration currentConfiguration;
	private String editingCorpusNameFile;

	/**
	 * Permet de se procurer l'instance statique
	 * 
	 * @return l'instance statique
	 */
	public static UserSettings getInstance() {
		if (null == _instance) {
			_instance = new UserSettings();
			/***********************************************/
			// TODO a supprimer
			try {
				_instance.setCurrentConfiguration(ConfigurationUtils.getInstance().getClassicalConfiguration());
			} catch (JsonParseException e) {
				logger.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			/***********************************************/
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
			totalKeysStructuredTextError = this.KEYS_STRUCTURED_TEXT_ERROR_LIST.size();
		}
		return totalKeysStructuredTextError;
	}

	/**
	 * Permet de savoir s'il y a des textes en erreurs a corrigé
	 * 
	 * @return Vrai si il y a des textes a corrigé
	 */
	public Boolean haveTextsInErrorRemaining() {
		return !getKeysStructuredTextErrorList().isEmpty();
	}

	/**
	 * Permet d'ajouter une clé à la liste des clés en erreur pour les structures
	 * des textes
	 * 
	 * @param key clé à ajouter
	 */
	public void addKeyStructuredTextError(String key) {
		this.KEYS_STRUCTURED_TEXT_ERROR_LIST.add(key);
	}

	/**
	 * Permet de vider la liste des clés en erreur pour les structures des textes
	 */
	public void clearKeysStructuredTextErrorList() {
		totalKeysStructuredTextError = null;
		this.KEYS_STRUCTURED_TEXT_ERROR_LIST.clear();
	}

	/**
	 * Permet de se procurer la liste des clés des textes utilisateurs en erreur
	 * 
	 * @return la liste des clés des textes utilisateurs en erreur immutable
	 */
	public List<String> getKeysStructuredTextErrorList() {
		return Collections.unmodifiableList(new ArrayList<>(this.KEYS_STRUCTURED_TEXT_ERROR_LIST));
	}

	/**
	 * Permet de se procurer le nombre de ligne vide en erreur
	 * 
	 * @return le nombre de ligne vide en erreur
	 */
	public Integer getNbBlankLineError() {
		if (null == totalBlankLineError) {
			totalBlankLineError = this.KEYS_BLANK_LINE_ERROR_LIST.size();
		}
		return totalBlankLineError;
	}
	
	/**
	 * Permet de savoir si il y a des lignes vide dans les balises meta d'un texte
	 * 
	 * @return Vrai si présence de ligne meta vide
	 */
	public Boolean haveMetaBlankLineError() {
		return !this.KEYS_META_BLANK_LINE_ERROR_LIST.isEmpty();
	}

	/**
	 * Permet de savoir s'il y a des lignes vides en erreurs a corrigé
	 * 
	 * @return Vrai si il y a des lignes vide en erreur a corrigé
	 */
	public Boolean haveBlankLineInErrorRemaining() {
		return !getKeysBlankLineErrorList().isEmpty();
	}

	/**
	 * Permet d'ajouter une clé à la liste des clés en erreur pour les lignes vides
	 * 
	 * @param key clé à ajouter
	 */
	public void addKeyBlankLineError(String key) {
		this.KEYS_BLANK_LINE_ERROR_LIST.add(key);
	}

	/**
	 * Permet de vider la liste des clés en erreur pour les lignes vides
	 */
	public void clearBlankLineErrorList() {
		totalBlankLineError = null;
		this.KEYS_BLANK_LINE_ERROR_LIST.clear();
	}
	
	/**
	 * Permet d'ajouter une clé à la liste des clés en erreur pour les lignes meta vides
	 * 
	 * @param key clé à ajouter
	 */
	public void addKeyMetaBlankLineError(String key) {
		this.KEYS_META_BLANK_LINE_ERROR_LIST.add(key);
	}

	/**
	 * Permet de vider la liste des clés en erreur pour les lignes meta vides
	 */
	public void clearMetaBlankLineErrorList() {
		this.KEYS_META_BLANK_LINE_ERROR_LIST.clear();
	}

	/**
	 * Permet de se procurer la liste des clés des lignes vide
	 * 
	 * @return la liste des clés des lignes vide immutable
	 */
	public List<String> getKeysBlankLineErrorList() {
		return Collections.unmodifiableList(new ArrayList<>(this.KEYS_BLANK_LINE_ERROR_LIST));
	}
	
	/**
	 * Permet de se procurer la liste des clés des meta lignes vide
	 * 
	 * @return la liste des clés des meta lignes vide immutable
	 */
	public List<String> getKeysMetaBlankLineErrorList() {
		return Collections.unmodifiableList(new ArrayList<>(this.KEYS_META_BLANK_LINE_ERROR_LIST));
	}

	/**
	 * Permet d'ajouter un texte structuré utilisateur
	 * 
	 * @param userStructuredText texte structuré utilisateur
	 */
	public void addUserStructuredText(UserStructuredText userStructuredText) {
		this.USER_STRUCTURED_TEXT_LIST.add(userStructuredText);
	}

	/**
	 * Permet de vider la liste des textes structurés utilisateur
	 */
	public void clearUserStructuredTextList() {
		this.USER_STRUCTURED_TEXT_LIST.clear();
	}

	/**
	 * Permet de se procurer la liste des textes structuré utilisateur Une liste
	 * immutable est retourné, cette liste ne peut donc pas être modifier
	 * 
	 * @return la liste des textes structuré utilisateurs immutable
	 */
	public List<UserStructuredText> getUserStructuredTextList() {
		return Collections.unmodifiableList(this.USER_STRUCTURED_TEXT_LIST);
	}

	/**
	 * Permet de se procurer le dossier
	 * 
	 * @param setting Reglage dont on souhaite le dossier
	 * @return le dossier
	 */
	public File getFolder(FolderSettingsEnum setting) {
		return FOLDER_SETTINGS.get(setting);
	}

	/**
	 * Permet de définir un dossier de réglages
	 * 
	 * @param setting type de dossier
	 * @param folder  dossier
	 */
	public void setFolder(FolderSettingsEnum setting, File folder) {
		FOLDER_SETTINGS.put(setting, folder);
	}

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
		logger.debug("[DEBUT] addMetaFieldsToEditingCorpus");
		getListFieldMetaFile().keySet().stream().forEach(key -> {
			if (metaFieldMap.containsKey(key)) {
				logger.debug(key + ":" + metaFieldMap.get(key));
				EDITING_METAFIELD_MAP.put(key, metaFieldMap.get(key));
			}
		});
		logger.debug("[FIN] addMetaFieldsToEditingCorpus");
	}

	/**
	 * Permet d'ajouter le texte en cours d'édition au corpus en cours
	 */
	public void addEditingTextToCurrentCorpus() {
		logger.debug("[DEBUT] addEditingTextToCurrentCorpus");
		LinkedHashMap<String, String> orderedFieldMap = new LinkedHashMap<String, String>();
		getListField(false, true, true, true).keySet().stream().forEach(key -> {
			if (EDITING_FIELD_MAP.containsKey(key)) {
				logger.debug(key + ":" + EDITING_FIELD_MAP.get(key));
				orderedFieldMap.put(key, EDITING_FIELD_MAP.get(key));
			}
		});
		EDITING_CORPUS_TEXTS_LIST.add(orderedFieldMap);
		EDITING_FIELD_MAP.clear();
		logger.debug("[FIN] addEditingTextToCurrentCorpus");
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
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index).getHeaderFieldList());
	}

	/**
	 * Permet de se procurer les champs de la configuration spécifique désiré
	 * 
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	public Map<String, String> getListFieldSpecific(Integer index) {
		return getListField(getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index).getTreatmentFieldList());
	}

	/**
	 * Permet de connaitre le nombre maximum de configuration spécifique
	 * 
	 * @return le nombre de configuration spécifique
	 */
	public Integer getNbSpecificConfiguration() {
		return getCurrentConfiguration().getSpecificConfigurationList().size();
	}

	/**
	 * Permet de se procurer la liste des champs souhaité
	 * 
	 * @param isMeta     méta
	 * @param isSpecific spécifique
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
		HashMap<String, String> finalMap = new LinkedHashMap<String, String>();
		if (null != getCurrentConfiguration()) {
			getCurrentConfiguration().getStructuredFieldList().stream().sorted(Comparator.comparing(StructuredField::getOrder))
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
		logger.debug("[DEBUT] updateFieldInEditingCorpus");
		logger.debug(key + ":" + value);
		EDITING_FIELD_MAP.put(key, value);
		logger.debug("[FIN] updateFieldInEditingCorpus");
	}

	/**
	 * Permet de mettre à jour la liste des champs spécifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilisé
	 * @param specificFieldMap champ à mettre à jour
	 */
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		logger.debug("[DEBUT] updateSpecificFieldInEditingCorpus");
		String delimiter = currentConfiguration.getSpecificConfigurationList().stream().sorted(Comparator.comparing(SpecificConfiguration::getOrder))
				.collect(Collectors.toList()).get(index).getDelimiter();
		Map<String, String> transformedMap = specificFieldMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, v -> StringUtils.join(v.getValue(), delimiter)));
		EDITING_FIELD_MAP.putAll(transformedMap);
		logger.debug("[FIN] updateSpecificFieldInEditingCorpus");
	}

	/**
	 * Permet de se procurer la map des champs spécifique
	 * 
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifique
	 */
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		logger.debug("[DEBUT] getSpecificFieldInEditingCorpus");
		String delimiter = currentConfiguration.getSpecificConfigurationList().stream().sorted(Comparator.comparing(SpecificConfiguration::getOrder))
				.collect(Collectors.toList()).get(index).getDelimiter();
		List<String> treatmentFieldList = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index).getTreatmentFieldList();
		Map<String, String> filteredMap = EDITING_FIELD_MAP.entrySet().stream().filter(k -> treatmentFieldList.contains(k.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Map<String, List<String>> mapFinal = filteredMap.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, v -> new ArrayList<String>(Arrays.asList(v.getValue().split(delimiter)))));
		Map<String, List<String>> mapFinalOrdered = new LinkedHashMap<>();
		List<StructuredField> listStructuredField = currentConfiguration.getStructuredFieldList().stream()
				.filter(sf -> treatmentFieldList.contains(sf.getFieldName())).sorted(Comparator.comparing(StructuredField::getOrder))
				.collect(Collectors.toList());
		for (StructuredField structuredField : listStructuredField) {
			List<String> listValues = mapFinal.get(structuredField.getFieldName());
			if (null == listValues) {
				listValues = new ArrayList<>();
			}
			mapFinalOrdered.put(structuredField.getFieldName(), listValues);
		}
		Integer nbMaxElement = mapFinalOrdered.values().stream().map(values -> values.size()).max(Comparator.comparing(Integer::valueOf)).get();
		mapFinalOrdered.entrySet().stream().filter(entry -> entry.getValue().size() < nbMaxElement).forEach(entry -> {
			while (entry.getValue().size() < nbMaxElement) {
				entry.getValue().add(StringUtils.SPACE);
			}
		});
		logger.debug("[FIN] getSpecificFieldInEditingCorpus");
		return mapFinalOrdered;
	}

	/**
	 * Permet d'écrire le corpus sur le disque
	 * 
	 * @param writer le writer
	 * @throws IOException erreur d'entrée sortie
	 */
	public void writeCorpus(IWriteText writer) throws IOException {
		logger.debug("[DEBUT] writeCorpus");
		// On écrit les champ meta
		writeLines(writer, EDITING_METAFIELD_MAP);
		// On écrit les textes
		for (LinkedHashMap<String, String> text : EDITING_CORPUS_TEXTS_LIST) {
			writeLines(writer, text);
			writer.addBreakLine();
		}
		logger.debug("[FIN] writeCorpus");
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
	 * @param memoryFilesList liste des memory files à ajouter
	 */
	public void addMemoryFilesList(List<MemoryFile> memoryFilesList) {
		MEMORY_FILES_LIST.addAll(memoryFilesList);
	}

	/**
	 * Permet de vider la liste des memoryfiles
	 */
	public void clearMemoryFilesList() {
		MEMORY_FILES_LIST.clear();
	}

	/**
	 * Permet de corriger toutes les lignes en erreur dans tous les fichiers en
	 * mémoire
	 * 
	 * @throws IOException
	 */
	public void fixedErrorLinesInAllMemoryFiles() throws IOException {
		logger.debug("[DEBUT] fixedErrorLinesInAllMemoryFiles");
		List<Path> pathList = LINES_ERROR_LIST.stream().map(lineError -> lineError.getPath()).distinct().collect(Collectors.toList());
		for (Path path : pathList) {
			Optional<MemoryFile> findFirstMemoryFile = MEMORY_FILES_LIST.stream().filter(memoryFile -> path.equals(memoryFile.getPath())).findFirst();
			if (!findFirstMemoryFile.isPresent()) {
				continue;
			}
			MemoryFile memoryFile = findFirstMemoryFile.get();
			fixedErrorLinesInOneMemoryFile(memoryFile);
		}
		logger.debug("[FIN] fixedErrorLinesInAllMemoryFiles");
	}

	/**
	 * Permet de corriger les lignes en erreurs dans un fichier en mémoire
	 * 
	 * @param memoryFile fichier en memoire
	 * @throws IOException
	 */
	private void fixedErrorLinesInOneMemoryFile(MemoryFile memoryFile) throws IOException {
		logger.debug("[DEBUT] fixedErrorLinesInOneMemoryFile");
		List<LineError> errorLines = LINES_ERROR_LIST.stream().filter(errorLine -> memoryFile.getPath().equals(errorLine.getPath()))
				.collect(Collectors.toList());
		errorLines.forEach(lineError -> memoryFile.updateLine(lineError.getIndex(), lineError.getLineFixed()));
		try (Writer writer = new Writer(memoryFile.getPath())) {
			memoryFile.writeLines(writer);
		}
		logger.debug("[FIN] fixedErrorLinesInOneMemoryFile");
	}

	/**
	 * Permet de charger un texte en erreur pour la correction
	 * 
	 * @param key Clé du texte
	 */
	public void loadErrorText(String key, ErrorTypeEnum errorType) {
		this.clearEditingCorpus();
		this.currentErrorKey = key;
		this.currentErrorTypeFixed = errorType;
		Optional<UserStructuredText> optionalUserStructuredText = this.USER_STRUCTURED_TEXT_LIST.stream().filter(text -> key.equals(text.getKey()))
				.findFirst();
		if (optionalUserStructuredText.isPresent()) {
			setEditingCorpusNameFile(FilenameUtils.removeExtension(optionalUserStructuredText.get().getFileName()));
			optionalUserStructuredText.get().getStructuredText().getListContent()
					.forEach(content -> this.updateFieldInEditingCorpus(content.getKey(), content.getValue()));
		}
	}

	/**
	 * Permet de se procurer les modifications en cours pour sauvegarder
	 * 
	 * @return les modifications en cours
	 */
	public SaveCurrentFixedText getSaveCurrentFixedText() {
		logger.debug("[DEBUT] getSaveCurrentFixedText");
		SaveCurrentFixedText save = new SaveCurrentFixedText();
		save.setPath(getFolder(FolderSettingsEnum.FOLDER_ANALYZE));
		save.setKeysStructuredTextErrorSet(Collections.unmodifiableSet(KEYS_STRUCTURED_TEXT_ERROR_LIST));
		save.setUserStructuredTextList(getUserStructuredTextList());
		save.setKeysBlankLineErrorSet(Collections.unmodifiableSet(KEYS_BLANK_LINE_ERROR_LIST));
		save.setKeysMetaBlankLineErrorSet(Collections.unmodifiableSet(KEYS_META_BLANK_LINE_ERROR_LIST));
		logger.debug("[FIN] getSaveCurrentFixedText");
		return save;
	}

	/**
	 * Permet de restaurer l'environnement en cours de correction
	 * 
	 * @param save sauvegarde a restaurer
	 */
	public void restoreCurrentFixedTest(SaveCurrentFixedText save) {
		logger.debug("[DEBUT] restoreCurrentFixedTest");
		setFolder(FolderSettingsEnum.FOLDER_ANALYZE, save.getPath());
		KEYS_STRUCTURED_TEXT_ERROR_LIST.addAll(save.getKeysStructuredTextErrorSet());
		USER_STRUCTURED_TEXT_LIST.addAll(save.getUserStructuredTextList());
		KEYS_BLANK_LINE_ERROR_LIST.addAll(save.getKeysBlankLineErrorSet());
		KEYS_META_BLANK_LINE_ERROR_LIST.addAll(save.getKeysMetaBlankLineErrorSet());
		logger.debug("[FIN] restoreCurrentFixedTest");
	}

	/**
	 * Permet d'appliquer les modifications au texte structuré
	 */
	public void applyCurrentTextToStructuredText() {
		logger.debug("[DEBUT] applyCurrentTextToStructuredText");
		Optional<UserStructuredText> optionalUserStructuredText = this.USER_STRUCTURED_TEXT_LIST.stream()
				.filter(text -> this.currentErrorKey.equals(text.getKey())).findFirst();
		if (optionalUserStructuredText.isPresent()) {
			StructuredText structuredText = optionalUserStructuredText.get().getStructuredText();
			EDITING_FIELD_MAP.entrySet().stream().forEach(entry -> structuredText.modifyContent(entry.getKey(), entry.getValue()));
			switch (currentErrorTypeFixed) {
			case BLANK_LINE:
				deleteBlankLineCurrentErrorKey();
				break;
			case STRUCTURED_TEXT:
				deleteStructuredCurrentErrorKey();
				break;
			case META_BLANK_LINE:
				deleteMetaBlankLineCurrentErrorKey();
				break;
			}
		}
		logger.debug("[FIN] applyCurrentTextToStructuredText");
	}

	/**
	 * Permet de supprimer la clé en erreur pour les structures en erreur
	 */
	public void deleteStructuredCurrentErrorKey() {
		logger.debug("[DEBUT] deleteCurrentErrorKey");
		KEYS_STRUCTURED_TEXT_ERROR_LIST.remove(currentErrorKey);
		currentErrorKey = StringUtils.EMPTY;
		currentErrorTypeFixed = null;
		logger.debug("[FIN] deleteCurrentErrorKey");
	}

	/**
	 * Permet de supprimer la clé en erreur pour les lignes blanches en erreur
	 */
	public void deleteBlankLineCurrentErrorKey() {
		logger.debug("[DEBUT] deleteBlankLineCurrentErrorKey");
		KEYS_BLANK_LINE_ERROR_LIST.remove(currentErrorKey);
		currentErrorKey = StringUtils.EMPTY;
		currentErrorTypeFixed = null;
		logger.debug("[FIN] deleteBlankLineCurrentErrorKey");
	}
	
	/**
	 * Permet de supprimer la clé en erreur pour les meta lignes blanches en erreur
	 */
	public void deleteMetaBlankLineCurrentErrorKey() {
		logger.debug("[DEBUT] deleteMetaBlankLineCurrentErrorKey");
		KEYS_META_BLANK_LINE_ERROR_LIST.remove(currentErrorKey);
		currentErrorKey = StringUtils.EMPTY;
		currentErrorTypeFixed = null;
		logger.debug("[FIN] deleteMetaBlankLineCurrentErrorKey");
	}

	/**
	 * Permet d'écrire les textes qui ont été corrigé
	 * 
	 * @param writer writer
	 * @throws IOException
	 */
	public void writeFixedText(IWriteText writer, String file) throws IOException {
		logger.debug(String.format("[DEBUT] writeFixedText : %s", file));
		List<UserStructuredText> userStructuredTextList = getUserStructuredTextList().stream().filter(ust -> ust.getFileName().equals(file))
				.collect(Collectors.toList());
		Map<String, String> listFieldMetaFile = getListFieldMetaFile();
		StructuredText structuredText = userStructuredTextList.get(0).getStructuredText();
		Map<String, String> mapFieldMetaFileToWrite = getMapToWrite(structuredText, listFieldMetaFile.keySet());
		writeLines(writer, mapFieldMetaFileToWrite);
		Map<String, String> listFieldCommonFile = getListField(false, true, true, true);
		List<UserStructuredText> orderedUserStructuredText = userStructuredTextList.stream()
				.sorted(Comparator.comparing(UserStructuredText::getNumber)).collect(Collectors.toList());
		for (UserStructuredText userStructuredText : orderedUserStructuredText) {
			Map<String, String> mapFieldCommonFileToWrite = getMapToWrite(userStructuredText.getStructuredText(), listFieldCommonFile.keySet());
			writeLines(writer, mapFieldCommonFileToWrite);
			writer.addBreakLine();
		}
		this.clearUserStructuredTextList();
		this.clearEditingCorpus();
		this.clearKeysStructuredTextErrorList();
		this.editingCorpusNameFile = StringUtils.EMPTY;
		logger.debug(String.format("[FIN] writeFixedText : %s", file));
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
	 * Permet de nettoyer les informations stockés suite à une précédente analyse
	 */
	public void clearAllSession() {
		clearLineErrorList();
		clearMemoryFilesList();
		clearEditingCorpus();
		clearKeysStructuredTextErrorList();
		clearUserStructuredTextList();
		clearBlankLineErrorList();
		clearMetaBlankLineErrorList();
	}

}
