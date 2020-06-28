package model.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import model.analyze.beans.Configuration;
import model.analyze.beans.Content;
import model.analyze.beans.MemoryFile;
import model.analyze.beans.StructuredField;
import model.analyze.beans.StructuredFile;
import model.analyze.beans.StructuredText;
import model.analyze.beans.UserStructuredText;
import model.analyze.beans.specific.ConfigurationStructuredText;
import model.analyze.constants.ErrorTypeEnum;
import model.analyze.constants.FolderSettingsEnum;
import model.exceptions.StructuringException;
import utils.KeyGenerator;

/**
 * 
 * Classe en charge de structurer le fichier en m�moire
 * 
 * @author Jeremy
 *
 */
public class Structuring {

	private final StructuredFile structuredFile;
	private final List<Content> lstMetaContent = new ArrayList<Content>();

	public Structuring(MemoryFile memoryFile, FolderSettingsEnum folderType) {
		this(memoryFile, folderType, null);
	}

	public Structuring(MemoryFile memoryFile, FolderSettingsEnum folderType,
			ConfigurationStructuredText beanConfiguration) {
		this.structuredFile = construct(memoryFile, folderType, beanConfiguration);
	}

	public StructuredFile getStructuredFile() {
		return this.structuredFile;
	}

	/**
	 * Permet de construirer les textes structur�s au sein d'un m�me fichier
	 * 
	 * @param memoryFile        fichier en m�moire
	 * @param beanConfiguration bean de configuration n�cessaire pour le sp�cifique
	 * @return le fichier structur�
	 */
	private StructuredFile construct(MemoryFile memoryFile, FolderSettingsEnum folderType,
			ConfigurationStructuredText beanConfiguration) {
		final StructuredFile sf = new StructuredFile(memoryFile);
		final List<String> listLines = new ArrayList<String>();
		final List<StructuredField> listStructuredField = new ArrayList<StructuredField>();
		StructuredField structuredFieldNewText = null;
		memoryFile.createIterator();
		Integer number = 1;
		while (memoryFile.hasLine()) {
			String l = memoryFile.getNextLine();
			checkLineAndAddErrorIfNecessary(l, memoryFile);
			StructuredField structuredFieldIfPossible = getStructuredFieldIfPossible(l);
			if (null != structuredFieldIfPossible) {
				if (listStructuredField.contains(structuredFieldIfPossible)) {
					structuredFieldNewText = checkAndSaveInconsistencyChangeText(structuredFieldNewText, structuredFieldIfPossible, memoryFile.getCurrentLine());
					listStructuredField.clear();
					number = processStructuring(memoryFile, folderType, beanConfiguration, sf, listLines, number);
				} else if (null == structuredFieldNewText && !structuredFieldIfPossible.getIsMetaFile()) {
					structuredFieldNewText = structuredFieldIfPossible;
				}
				listStructuredField.add(structuredFieldIfPossible);
			}
			listLines.add(l);
		}
		if (!listLines.isEmpty()) {
			processStructuring(memoryFile, folderType, beanConfiguration, sf, listLines, number);
		}
		return sf;
	}
	
	/**
	 * Permet de v�rifier et de sauvegarder les probl�mes d'incoh�rences suite au changement de balise de r�f�rence pour les nouveaux textes
	 * @param oldStructuredField Ancien champ structur�
	 * @param newStructuredField Nouveau champ structur�
	 * @return
	 */
	private StructuredField checkAndSaveInconsistencyChangeText(StructuredField oldStructuredField, StructuredField newStructuredField, Integer nbLine) {
		if (null != oldStructuredField && !oldStructuredField.equals(newStructuredField)) {
			UserSettings.getInstance().addInconsistencyError(oldStructuredField, newStructuredField, nbLine);
		}
		return newStructuredField;
	}

	/**
	 * Permet de lancer l'analyse
	 * 
	 * @param memoryFile        fichier m�moire
	 * @param folderType        type de dossier
	 * @param beanConfiguration bean de configurat
	 * @param sf                fichier structurel
	 * @param listLines         liste des lignes
	 * @param number            num�ro
	 * @return le num�ro
	 */
	private Integer processStructuring(MemoryFile memoryFile, FolderSettingsEnum folderType,
			ConfigurationStructuredText beanConfiguration, final StructuredFile sf, final List<String> listLines,
			Integer number) {
		try {
			StructuredText structuredText = prepareStructuredText(listLines);
			String keyStructuredText = StringUtils.EMPTY;
			if (null != structuredText) {
				StringBuilder keyTextBuilder = new StringBuilder();
				keyTextBuilder.append(memoryFile.nameFile());
				keyTextBuilder.append(number.toString());
				structuredText.setUniqueKey(KeyGenerator.generateKey(keyTextBuilder.toString()));
				keyStructuredText = KeyGenerator.generateKey(structuredText);
				if (structuredText.getHaveBlankLine()) {
					UserSettings.getInstance().addKeyError(ErrorTypeEnum.BLANK_LINE, keyStructuredText);
				}
				if (structuredText.getHaveMetaBlankLine()) {
					UserSettings.getInstance().addKeyError(ErrorTypeEnum.META_BLANK_LINE, keyStructuredText);
				}
			}
			listLines.clear();
			if (null != beanConfiguration && null != structuredText) {
				try {
					sf.getListStructuredText().addAll(processingStructuredTextWithConfigurationBean(structuredText,
							beanConfiguration, memoryFile, sf));

				} catch (IndexOutOfBoundsException e) {
					UserSettings.getInstance().addKeyError(ErrorTypeEnum.STRUCTURED_TEXT, keyStructuredText);
				}

			} else if (null != structuredText) {
				UserStructuredText userStructuredText = new UserStructuredText(memoryFile.nameFile(), number,
						structuredText);
				UserSettings.getInstance().addUserStructuredText(folderType, userStructuredText);
				if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
					UserSettings.getInstance().addKeyToFilteredList(userStructuredText.getKey());
				}
				sf.getListStructuredText().add(structuredText);
			}
			number++;
		} catch (StructuringException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return number;
	}

	/**
	 * Permet de retraiter le texte structur� pour correspondre au sp�cifique
	 * demand�
	 * 
	 * @param structuredText    text structur�
	 * @param beanConfiguration bean de configuration pour le sp�cifique
	 * @return la liste des textes structur�
	 */
	private List<StructuredText> processingStructuredTextWithConfigurationBean(StructuredText structuredText,
			ConfigurationStructuredText beanConfiguration, MemoryFile memoryFile, StructuredFile structuredFile)
			throws IndexOutOfBoundsException {
		List<StructuredText> structuredTextProcessedList = new ArrayList<StructuredText>();
		final Map<String, List<String>> mapTagValuesProcessed = new HashedMap<String, List<String>>();
		beanConfiguration.getSpecificConfiguration().getTreatmentFieldList()
				.forEach(tag -> mapTagValuesProcessed.put(tag, structuredText.getContentWithDelimiterProcess(tag,
						beanConfiguration.getSpecificConfiguration().getDelimiter())));
		for (int i = 0; i < mapTagValuesProcessed.values().stream().mapToInt(s -> s.size()).max().getAsInt(); i++) {
			StructuredText structuredTextProcessed = structuredText.duplicate();
			for (String tag : mapTagValuesProcessed.keySet()) {
				structuredTextProcessed.modifyContent(tag, mapTagValuesProcessed.get(tag).get(i).trim());
			}
			structuredTextProcessedList.add(structuredTextProcessed);
		}
		return structuredTextProcessedList;
	}

	/**
	 * Permet de prepar� le texte structur�
	 * 
	 * @param textLines les lignes � structurer pour le m�me texte
	 * @return le texte structur�
	 * @throws StructuringException
	 */
	private StructuredText prepareStructuredText(List<String> textLines) throws StructuringException {
		if (textLines.isEmpty()) {
			return null;
		}
		StructuredText st = new StructuredText();
		Boolean listMetaToAddIsEmpty = lstMetaContent.isEmpty();
		if (!listMetaToAddIsEmpty) {
			st.getListContent().addAll(lstMetaContent);
		}
		integrateContentToStructuredText(st, textLines);
		st.setHaveBlankLine(st.getHaveBlankLine() || !checkAllFieldArePresent(st, Boolean.FALSE));
		if (listMetaToAddIsEmpty) {
			st.setHaveMetaBlankLine(st.getHaveMetaBlankLine() || !checkAllFieldArePresent(st, Boolean.TRUE));
		}
		return st;
	}

	/**
	 * Permet de savoir si tous les champs de la configuration sont renseign�
	 * 
	 * @param structuredText texte structur�
	 * @param configuration  configuration
	 * @param onlyMeta       Vrai : On inclut que les meta, Faux : on ignore les
	 *                       meta
	 * @return
	 */
	private Boolean checkAllFieldArePresent(StructuredText structuredText, Boolean onlyMeta) {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (onlyMeta) {
			return configuration.getStructuredFieldList().stream()
					.map(field -> !field.getIsMetaFile()
							|| StringUtils.isNotBlank(structuredText.getContent(field.getFieldName())))
					.reduce(Boolean::logicalAnd).get();
		} else {
			return configuration.getStructuredFieldList().stream()
					.map(field -> field.getIsMetaFile()
							|| StringUtils.isNotBlank(structuredText.getContent(field.getFieldName())))
					.reduce(Boolean::logicalAnd).get();
		}
	}

	/**
	 * Permet d'int�grer le contenu dans le texte structur�
	 * 
	 * @param st    le texte structur� � renseigner
	 * @param lines les lignes � traiter
	 * @throws StructuringException
	 */
	private void integrateContentToStructuredText(StructuredText st, List<String> lines) throws StructuringException {
		Content lastContent = null;
		List<StructuredField> listStructuredFields = new ArrayList<>();
		for (String line : lines) {
			lastContent = prepareAndIntegrateContent(st, line, lastContent, listStructuredFields);
		}
		setHaveBlankLine(st, lastContent, listStructuredFields.get(0));
	}

	/**
	 * Permet d'int�grer le contenu dans le texte structur�
	 * 
	 * @param st          le texte structur� � renseigner
	 * @param line        la ligne � traiter
	 * @param lastContent le dernier contenu trait�
	 * @param listStructuredFields La liste des structured fields qui doit ne contenir que le dernier champ structur� trait�
	 * @return le contenu trait�
	 * @throws StructuringException
	 */
	private Content prepareAndIntegrateContent(StructuredText st, String line, Content lastContent, List<StructuredField> listStructuredFields)
			throws StructuringException {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (StringUtils.startsWith(line, configuration.getBaseCode())) {
			String lineWithoutLedaBalise = StringUtils.remove(line, configuration.getBaseCode());
			Optional<StructuredField> optionalStructuredField = configuration.getStructuredFieldList().stream()
					.filter(field -> lineWithoutLedaBalise.startsWith(field.getFieldName())).findFirst();
			if (optionalStructuredField.isPresent()) {
				String tagName = optionalStructuredField.get().getFieldName();
				String value = StringUtils.remove(lineWithoutLedaBalise, tagName);
				if (!listStructuredFields.isEmpty()) {
					setHaveBlankLine(st, lastContent, listStructuredFields.get(0));
				}
				listStructuredFields.clear();
				listStructuredFields.add(optionalStructuredField.get());
				Content content = new Content(tagName, value);
				if (null != lastContent && tagName.equals(lastContent.getKey())) {
					content = lastContent;
				} else {
					st.getListContent().add(content);
				}
				if (optionalStructuredField.get().getIsMetaFile()) {
					lstMetaContent.add(content);
				}
				return content;
			}
		} else if (null != lastContent) {
			if (StringUtils.isNotBlank(lastContent.getValue())) {
				String currentValue = lastContent.getValue();
				lastContent.setValue(currentValue.concat("\n").concat(line));
			} else {
				lastContent.setValue(line);				
			}
			return lastContent;
			

		}
		// Ne devrait jamais se produire � cause du contr�le CheckLine
		return lastContent;
	}

	/**
	 * Permet de d�finir si il y a des lignes vides ou non
	 * @param st le texte structur� � renseigner
	 * @param lastContent le dernier contenu trait�
	 * @param lastStructuredField Le dernier champ structur� trait�
	 */
	private void setHaveBlankLine(StructuredText st, Content lastContent, StructuredField lastStructuredField) {
		if (null != lastStructuredField) {
			if (StringUtils.isBlank(lastContent.getValue())) {
				if (lastStructuredField.getIsMetaFile()) {
					st.setHaveMetaBlankLine(true);
				} else {
					st.setHaveBlankLine(true);
				}
			}
		}
	}

	/**
	 * Permet de v�rifier la ligne et d'ajouter les erreurs si n�cessaire
	 * 
	 * @param line       ligne
	 * @param memoryFile memory file
	 */
	private void checkLineAndAddErrorIfNecessary(String line, MemoryFile memoryFile) {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (StringUtils.isBlank(line)) {
			return;
		}
		if (!StringUtils.startsWith(line, "[") && !checkIfHaveFieldsCode(line)) {
			return;
		} else {
			if (StringUtils.startsWith(line, configuration.getBaseCode())) {
				String lineWithoutLedaBalise = StringUtils.remove(line, configuration.getBaseCode());
				Optional<StructuredField> optionalStructuredField = configuration.getStructuredFieldList().stream()
						.filter(field -> lineWithoutLedaBalise.startsWith(field.getFieldName())).findFirst();
				if (optionalStructuredField.isPresent()) {
					return;
				}
			} else if (!checkIfHaveFieldsCode(line)) {
				return;
			}
		}
		UserSettings.getInstance().addLineError(memoryFile.getPath(), line, memoryFile.getCurrentLine());
	}
	
	/**
	 * Permet de se procurer le champ structur� si possible
	 * @param line ligne � analyser
	 * @return le champ structur� ou null
	 */
	private StructuredField getStructuredFieldIfPossible(String line) {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (StringUtils.startsWith(line, configuration.getBaseCode())) {
			String lineWithoutLedaBalise = StringUtils.remove(line, configuration.getBaseCode());
			Optional<StructuredField> optionalStructuredField = configuration.getStructuredFieldList().stream()
					.filter(field -> lineWithoutLedaBalise.startsWith(field.getFieldName())).findFirst();
			if (optionalStructuredField.isPresent()) {
				return optionalStructuredField.get();
			}
		}
		return null;
	}

	/**
	 * Permet de v�rifier si la ligne d�tient des informations de balises
	 * 
	 * @param configuration configuration
	 * @param line          ligne � analyser
	 * @return Vrai s'il n'y a pas de balise
	 */
	private Boolean checkIfHaveFieldsCode(String line) {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (!StringUtils.contains(line,
				StringUtils.remove(StringUtils.remove(configuration.getBaseCode(), "["), "]"))) {
			return false;
		}
		if (!configuration.getStructuredFieldList().stream()
				.map(field -> StringUtils.contains(line,
						StringUtils.remove(StringUtils.remove(field.getFieldName(), "["), "]")))
				.reduce(Boolean::logicalOr).get()) {
			return false;
		}
		return true;
	}

}
