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
import analyze.beans.CurrentUserConfiguration;
import analyze.beans.CurrentUserTexts;
import analyze.beans.LineError;
import analyze.beans.MemoryFile;
import analyze.beans.SaveCurrentFixedText;
import analyze.beans.SpecificConfiguration;
import analyze.beans.StructuredField;
import analyze.beans.StructuredFile;
import analyze.beans.StructuredText;
import analyze.beans.UserStructuredText;
import analyze.beans.specific.ConfigurationStructuredText;
import analyze.constants.ErrorTypeEnum;
import analyze.constants.FolderSettingsEnum;
import analyze.interfaces.IWriteText;
import ihm.utils.ConfigurationUtils;

/**
 * 
 * Permet de g�rer les r�glages et les instances en cours de l'utilisateur
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
	private final Map<FolderSettingsEnum, CurrentUserTexts> CURRENT_FOLDER_USER_TEXTS_MAP = new HashMap<FolderSettingsEnum, CurrentUserTexts>();
	// ERROR MAP
	private final List<LineError> LINES_ERROR_LIST = new LinkedList<LineError>();
	private final Map<ErrorTypeEnum, Set<String>> MAP_TYPE_ERROR_KEYS_LIST = new HashMap<>();
	private String currentErrorKey;
	private Integer totalKeysStructuredTextError = null;
	private Integer totalBlankLineError = null;
	private ErrorTypeEnum currentErrorTypeFixed = null;

	private Configuration currentConfiguration;
	private String editingCorpusNameFile;

	public UserSettings() {
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.BLANK_LINE, new HashSet<>());
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.META_BLANK_LINE, new HashSet<>());
		MAP_TYPE_ERROR_KEYS_LIST.put(ErrorTypeEnum.STRUCTURED_TEXT, new HashSet<>());
	}

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
	 * Permet d'ajouter une cl� en erreur
	 * 
	 * @param typeError type d'erreur
	 * @param key       cl� en erreur � ajouter
	 */
	public void addKeyError(ErrorTypeEnum typeError, String key) {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).add(key);
	}

	/**
	 * 
	 * Permet de se procurer la liste des cl�s en erreurs
	 * 
	 * @param typeError type de l'erreur
	 * @return la liste des cl�s en erreur
	 */
	public List<String> getKeysInError(ErrorTypeEnum typeError) {
		return Collections.unmodifiableList(new ArrayList<>(this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError)));
	}

	/**
	 * Permet de connaitre le nb de cl�s en erreur
	 * 
	 * @param typeError type d'erreurs
	 * @return Le nombre de cl� en erreur
	 */
	public Integer getNbKeysInError(ErrorTypeEnum typeError) {
		return this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).size();
	}

	/**
	 * Permet de nettoyer une liste de cl�s en erreur
	 * 
	 * @param typeError type de l'erreur
	 */
	public void clearKeysInError(ErrorTypeEnum typeError) {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(typeError).clear();
	}

	/**
	 * Permet de supprimer la cl� courante en erreur de la liste et de remettre �
	 * l'�tat initial le type et la cl� en cours de correction
	 */
	public void deleteCurrentErrorKey() {
		this.MAP_TYPE_ERROR_KEYS_LIST.get(currentErrorTypeFixed).remove(currentErrorKey);
		currentErrorKey = StringUtils.EMPTY;
		currentErrorTypeFixed = null;
	}

	/**
	 * Permet de vider la liste des cl�s en erreur pour les structures des textes
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
	 * Permet de vider la liste des cl�s en erreur pour les lignes vides
	 */
	public void clearBlankLineErrorList() {
		totalBlankLineError = null;
		clearKeysInError(ErrorTypeEnum.BLANK_LINE);
	}

	/**
	 * Permet d'ajouter un texte structur� utilisateur
	 * 
	 * @param folder             dossier � modifier
	 * @param userStructuredText texte structur� utilisateur
	 */
	public void addUserStructuredText(FolderSettingsEnum folder, UserStructuredText userStructuredText) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addUserStructuredText(userStructuredText);
	}

	/**
	 * Permet de vider la liste des textes structur�s utilisateur
	 * 
	 * @param folder dossier � modifier
	 */
	public void clearUserStructuredTextList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearUserStructuredTextList();
	}

	/**
	 * Permet de se procurer la liste des textes structur� utilisateur Une liste
	 * immutable est retourn�, cette liste ne peut donc pas �tre modifier
	 * 
	 * @param folder dossier � modifier
	 * @return la liste des textes structur� utilisateurs immutable
	 */
	public List<UserStructuredText> getUserStructuredTextList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getUserStructuredTextList();
	}

	/**
	 * Permet de se procurer le dossier
	 * 
	 * @param setting Reglage dont on souhaite le dossier
	 * @return le dossier
	 */
	public File getFolder(FolderSettingsEnum setting) {
		if (FOLDER_SETTINGS.containsKey(setting)) {
			return FOLDER_SETTINGS.get(setting);
		}
		return null;
	}

	/**
	 * Permet de d�finir un dossier de r�glages
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
	 * Permet de d�finir la configuration courante
	 * 
	 * @param currentConfiguration configuration � d�finir
	 */
	public void setCurrentConfiguration(Configuration currentConfiguration) {
		this.currentConfiguration = currentConfiguration;
	}

	/**
	 * Permet de se procurer le nom du fichier du corpus en cours d'�dition
	 * 
	 * @return le nom du fichier du corpus en cours d'�dition
	 */
	public String getEditingCorpusNameFile() {
		return editingCorpusNameFile;
	}

	/**
	 * Permet de d�finir le nom du corpus en cours d'�dition
	 * 
	 * @param editingCorpusNameFile nom du corpus en cours d'�dition
	 */
	public void setEditingCorpusNameFile(String editingCorpusNameFile) {
		this.editingCorpusNameFile = editingCorpusNameFile;
	}

	/**
	 * Permet d'ajouter des erreurs d�tect� au niveau des lignes Avant les erreurs
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
	 * Permet de mettre � jour la ligne en erreur
	 * 
	 * @param index     index de mise � jour
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
	 * Permet d'ajouter les champs m�ta au corpus d'�dition
	 * 
	 * @param metaFieldMap map des champs m�ta
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
	 * Permet d'ajouter le texte en cours d'�dition au corpus en cours
	 */
	public void addEditingTextToCurrentCorpus() {
		LinkedHashMap<String, String> orderedFieldMap = new LinkedHashMap<String, String>();
		getListField(false, true, true, true).keySet().stream().forEach(key -> {
			if (EDITING_FIELD_MAP.containsKey(key)) {
				logger.debug(key + ":" + EDITING_FIELD_MAP.get(key));
				orderedFieldMap.put(key, EDITING_FIELD_MAP.get(key));
			}
		});
		EDITING_CORPUS_TEXTS_LIST.add(orderedFieldMap);
		EDITING_FIELD_MAP.clear();
	}

	/**
	 * Permet de fournir une liste des champs m�ta de la configuration courante
	 * 
	 * @return la liste des champs m�ta
	 */
	public Map<String, String> getListFieldMetaFile() {
		return getListField(true, false, false, false);
	}

	/**
	 * Permet de fournir une liste des champs commun de la configuration courante
	 * (non m�ta et non sp�cifique)
	 * 
	 * @return la liste des champs commun
	 */
	public Map<String, String> getListFieldCommonFile() {
		return getListField(false, false, false, true);
	}

	/**
	 * Permet de se procurer les champs en t�te de la configuration sp�cifique
	 * d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		return getListField(getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getHeaderFieldList());
	}

	/**
	 * Permet de se procurer les champs de la configuration sp�cifique d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	public Map<String, String> getListFieldSpecific(Integer index) {
		return getListField(getCurrentConfiguration().getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getTreatmentFieldList());
	}

	/**
	 * Permet de connaitre le nombre maximum de configuration sp�cifique
	 * 
	 * @return le nombre de configuration sp�cifique
	 */
	public Integer getNbSpecificConfiguration() {
		return getCurrentConfiguration().getSpecificConfigurationList().size();
	}

	/**
	 * Permet de se procurer la liste des champs souhait�
	 * 
	 * @param isMeta     m�ta
	 * @param isSpecific sp�cifique
	 * @param isHeader   header
	 * @param isCommon   commun
	 * @return la liste des champs filtr�
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
			getCurrentConfiguration().getStructuredFieldList().stream()
					.sorted(Comparator.comparing(StructuredField::getOrder))
					.filter(s -> fieldsList.contains(s.getFieldName())).forEach(s -> {
						finalMap.put(s.getFieldName(), s.getFieldName() + " (" + s.getLabel() + ")");
					});
		}
		return finalMap;
	}

	/**
	 * Permet de cr�er un nouveau corpus
	 * 
	 * @param nameFile         nom du fichier
	 * @param metaFileFieldMap map des champ metafile
	 */
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		setEditingCorpusNameFile(nameFile);
		addMetaFieldsToEditingCorpus(metaFileFieldMap);
	}

	/**
	 * Permet de d�finir si un corpus est en cours d'�dition
	 * 
	 * @return Vrai si un corpus est en cours d'�dition
	 */
	public Boolean haveEditingCorpus() {
		return StringUtils.isNotBlank(getEditingCorpusNameFile());
	}

	/**
	 * Permet de supprimer le corpus en cours d'�dition
	 */
	public void clearEditingCorpus() {
		setEditingCorpusNameFile(StringUtils.EMPTY);
		EDITING_FIELD_MAP.clear();
	}

	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param key Cl� du champ � r�cup�rer
	 * @return la valeur
	 */
	public String getFieldInEditingCorpus(String key) {
		if (EDITING_FIELD_MAP.containsKey(key)) {
			return EDITING_FIELD_MAP.get(key);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Permet de mettre � jour un champ de le corpus en cours d'�dition
	 * 
	 * @param key   Cl�
	 * @param value Valeur
	 */
	public void updateFieldInEditingCorpus(String key, String value) {
		EDITING_FIELD_MAP.put(key, value);
	}

	/**
	 * Permet de mettre � jour la liste des champs sp�cifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilis�
	 * @param specificFieldMap champ � mettre � jour
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
	 * Permet de se procurer la map des champs sp�cifique
	 * 
	 * @param index index de la configuration utilis�
	 * @return la map des champs sp�cifique
	 */
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		String delimiter = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getDelimiter();
		List<String> treatmentFieldList = currentConfiguration.getSpecificConfigurationList().stream()
				.sorted(Comparator.comparing(SpecificConfiguration::getOrder)).collect(Collectors.toList()).get(index)
				.getTreatmentFieldList();
		Map<String, String> filteredMap = EDITING_FIELD_MAP.entrySet().stream()
				.filter(k -> treatmentFieldList.contains(k.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		Map<String, List<String>> mapFinal = filteredMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
				v -> new ArrayList<String>(Arrays.asList(v.getValue().split(delimiter)))));
		Map<String, List<String>> mapFinalOrdered = new LinkedHashMap<>();
		List<StructuredField> listStructuredField = currentConfiguration.getStructuredFieldList().stream()
				.filter(sf -> treatmentFieldList.contains(sf.getFieldName()))
				.sorted(Comparator.comparing(StructuredField::getOrder)).collect(Collectors.toList());
		for (StructuredField structuredField : listStructuredField) {
			List<String> listValues = mapFinal.get(structuredField.getFieldName());
			if (null == listValues) {
				listValues = new ArrayList<>();
			}
			mapFinalOrdered.put(structuredField.getFieldName(), listValues);
		}
		Integer nbMaxElement = mapFinalOrdered.values().stream().map(values -> values.size())
				.max(Comparator.comparing(Integer::valueOf)).get();
		mapFinalOrdered.entrySet().stream().filter(entry -> entry.getValue().size() < nbMaxElement).forEach(entry -> {
			while (entry.getValue().size() < nbMaxElement) {
				entry.getValue().add(StringUtils.SPACE);
			}
		});
		return mapFinalOrdered;
	}

	/**
	 * Permet d'�crire le corpus sur le disque
	 * 
	 * @param writer le writer
	 * @throws IOException erreur d'entr�e sortie
	 */
	public void writeCorpus(IWriteText writer) throws IOException {
		// On �crit les champ meta
		writeLines(writer, EDITING_METAFIELD_MAP);
		// On �crit les textes
		for (LinkedHashMap<String, String> text : EDITING_CORPUS_TEXTS_LIST) {
			writeLines(writer, text);
			writer.addBreakLine();
		}
	}

	/**
	 * Permet d'�crire les lignes d'une map de cl� valeur
	 * 
	 * @param writer     le writer
	 * @param mapToWrite la map � �crire
	 * @throws IOException Exception d'entr� sorti
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
	 * Permet de cr�er la ligne � �crire dans le fichier
	 * 
	 * @param key   cl� du champ
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
	 * @param folder          dossier � modifier
	 * @param memoryFilesList liste des memory files � ajouter
	 */
	public void addMemoryFilesList(FolderSettingsEnum folder, List<MemoryFile> memoryFilesList) {
		memoryFilesList.stream().forEach(mf -> this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addMemoryFile(mf));
	}

	/**
	 * Permet de vider la liste des memoryfiles
	 * 
	 * @param folder dossier � modifier
	 */
	public void clearMemoryFilesList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearMemoryFileList();
	}

	/**
	 * Permet de corriger toutes les lignes en erreur dans tous les fichiers en
	 * m�moire
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
	 * Permet de corriger les lignes en erreurs dans un fichier en m�moire
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
	 * @param key       Cl� du texte
	 * @param errorType Type d'erreur
	 */
	public void loadErrorText(String key, ErrorTypeEnum errorType) {
		this.clearEditingCorpus();
		this.currentErrorKey = key;
		this.currentErrorTypeFixed = errorType;
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP
				.get(FolderSettingsEnum.FOLDER_ANALYZE).getUserStructuredTextList().stream()
				.filter(text -> key.equals(text.getKey())).findFirst();
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
		SaveCurrentFixedText save = new SaveCurrentFixedText();
		save.setPath(getFolder(FolderSettingsEnum.FOLDER_ANALYZE));
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
		save.setLibraryPath(getFolder(FolderSettingsEnum.FOLDER_TEXTS).toPath());
		return save;
	}

	/**
	 * Permet de restaurer l'environnement de la configuration de l'utilisateur
	 * 
	 * @param save la configuration de l'utilisateur a restaurer
	 */
	public void restoreUserConfiguration(CurrentUserConfiguration save) {
		setFolder(FolderSettingsEnum.FOLDER_TEXTS, save.getLibraryPath().toFile());
	}

	/**
	 * Permet de restaurer l'environnement en cours de correction
	 * 
	 * @param save sauvegarde a restaurer
	 */
	public void restoreCurrentFixedTest(SaveCurrentFixedText save) {
		setFolder(FolderSettingsEnum.FOLDER_ANALYZE, save.getPath());
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.STRUCTURED_TEXT).addAll(save.getKeysStructuredTextErrorSet());
		save.getUserStructuredTextList().stream().forEach(ust -> this.CURRENT_FOLDER_USER_TEXTS_MAP
				.get(FolderSettingsEnum.FOLDER_ANALYZE).addUserStructuredText(ust));
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.BLANK_LINE).addAll(save.getKeysBlankLineErrorSet());
		this.MAP_TYPE_ERROR_KEYS_LIST.get(ErrorTypeEnum.META_BLANK_LINE).addAll(save.getKeysMetaBlankLineErrorSet());
	}

	/**
	 * Permet d'appliquer les modifications au texte structur�
	 */
	public void applyCurrentTextToStructuredText() {
		Optional<UserStructuredText> optionalUserStructuredText = this.CURRENT_FOLDER_USER_TEXTS_MAP
				.get(FolderSettingsEnum.FOLDER_ANALYZE).getUserStructuredTextList().stream()
				.filter(text -> this.currentErrorKey.equals(text.getKey())).findFirst();
		if (optionalUserStructuredText.isPresent()) {
			StructuredText structuredText = optionalUserStructuredText.get().getStructuredText();
			EDITING_FIELD_MAP.entrySet().stream()
					.forEach(entry -> structuredText.modifyContent(entry.getKey(), entry.getValue()));
			deleteCurrentErrorKey();
		}
	}
	
	/**
	 * Permet d'�crire les textes qui ont �t� corrig�
	 * 
	 * @param writer writer
	 * @throws IOException
	 */
	public void writeFixedText(IWriteText writer, String file) throws IOException {
		logger.debug(String.format("[DEBUT] writeFixedText : %s", file));
		List<UserStructuredText> userStructuredTextList = this.CURRENT_FOLDER_USER_TEXTS_MAP
				.get(FolderSettingsEnum.FOLDER_ANALYZE).getUserStructuredTextList().stream()
				.filter(ust -> ust.getFileName().equals(file)).collect(Collectors.toList());
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
		logger.debug(String.format("[FIN] writeFixedText : %s", file));
	}

	/**
	 * Permet de nettoyer les informations apr�s l'enregistrement des fichiers
	 */
	public void clearAfterWriteFixedText() {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(FolderSettingsEnum.FOLDER_ANALYZE).clearUserStructuredTextList();
		this.clearEditingCorpus();
		this.clearKeysStructuredTextErrorList();
		this.editingCorpusNameFile = StringUtils.EMPTY;
	}

	/**
	 * Permet de se procurer une map des cl�s valeur pour �criture
	 * 
	 * @param structureText Structure text
	 * @param key           Cl� � retenir
	 * @return la map � �crire
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
	 * Permet de nettoyer les informations stock�s suite � une pr�c�dente analyse
	 */
	public void clearAllSession(FolderSettingsEnum folder) {
		clearLineErrorList();
		clearCurrentFolderUserTexts(folder);
		clearEditingCorpus();
		clearKeysStructuredTextErrorList();
		clearBlankLineErrorList();
		clearKeysInError(ErrorTypeEnum.META_BLANK_LINE);
	}

	/**
	 * Permet de se procurer le nouveau repertoire incluant la configuration dans le
	 * chemin
	 * 
	 * @return le repertoire complet pour sauvegarder les textes
	 */
	public File getDirectoryForSaveTextsInLibrary() {
		File folderTexts = getFolder(FolderSettingsEnum.FOLDER_TEXTS);
		if (null != folderTexts && null != this.currentConfiguration) {
			File newDirectory = new File(folderTexts.getAbsolutePath(), this.currentConfiguration.getName());
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
	 * Permet d'ajouter un fichier structur�
	 * 
	 * @param folder         dossier � modifier
	 * @param structuredFile fichier structur�
	 */
	public void addStructuredFile(FolderSettingsEnum folder, StructuredFile structuredFile) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addStructuredFile(structuredFile);
	}

	/**
	 * Permet de vider la liste des fichier structur� utilisateur
	 * 
	 * @param folder dossier � modifier
	 */
	public void clearStructuredFileList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearStructuredFileList();
	}

	/**
	 * Permet de se procurer la liste des fichiers structur� Une liste immutable est
	 * retourn�, cette liste ne peut donc pas �tre modifier
	 * 
	 * @param folder dossier � modifier
	 * @return la liste des fichier structur� utilisateurs immutable
	 */
	public List<StructuredFile> getStructuredFileList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getStructuredFileList();
	}

	/**
	 * Permet d'ajouter un texte structur� sp�cifique
	 * 
	 * @param folder                      dossier � modifier
	 * @param configurationStructuredText un texte structur� sp�cifique
	 */
	public void addConfigurationStructuredText(FolderSettingsEnum folder,
			ConfigurationStructuredText configurationStructuredText) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).addConfigurationStructuredText(configurationStructuredText);
	}

	/**
	 * Permet de vider la liste des textes structur�s sp�cifique
	 * 
	 * @param folder dossier � modifier
	 */
	public void clearConfigurationStructuredTextList(FolderSettingsEnum folder) {
		this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).clearConfigurationStructuredTextList();
	}

	/**
	 * Permet de se procurer la liste des textes structur�s sp�cifique Une liste
	 * immutable est retourn�, cette liste ne peut donc pas �tre modifier
	 * 
	 * @param folder dossier � modifier
	 * @return la liste des textes structur�s sp�cifique utilisateurs immutable
	 */
	public List<ConfigurationStructuredText> getConfigurationStructuredTextList(FolderSettingsEnum folder) {
		return this.CURRENT_FOLDER_USER_TEXTS_MAP.get(folder).getConfigurationStructuredTextList();
	}

}
