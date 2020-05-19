package analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import analyze.beans.Configuration;
import analyze.beans.Content;
import analyze.beans.MemoryFile;
import analyze.beans.StructuredField;
import analyze.beans.StructuredFile;
import analyze.beans.StructuredText;
import analyze.beans.UserStructuredText;
import analyze.beans.specific.ConfigurationStructuredText;
import analyze.constants.ErrorTypeEnum;
import analyze.constants.FolderSettingsEnum;
import exceptions.StructuringException;
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
		memoryFile.createIterator();
		Integer number = 1;
		while (memoryFile.hasLine()) {
			String l = memoryFile.getNextLine();
			checkLineAndAddErrorIfNecessary(l, memoryFile);
			if (StringUtils.isEmpty(l)) {
				number = processStructuring(memoryFile, folderType, beanConfiguration, sf, listLines, number);
			} else {
				listLines.add(l);
			}
		}
		if (!listLines.isEmpty()) {
			processStructuring(memoryFile, folderType, beanConfiguration, sf, listLines, number);
		}
		return sf;
	}

	/**
	 * Permet de lancer l'analyse
	 * @param memoryFile fichier m�moire
	 * @param folderType type de dossier
	 * @param beanConfiguration bean de configurat
	 * @param sf fichier structurel
	 * @param listLines liste des lignes
	 * @param number num�ro
	 * @return le num�ro
	 */
	private Integer processStructuring(MemoryFile memoryFile, FolderSettingsEnum folderType,
			ConfigurationStructuredText beanConfiguration, final StructuredFile sf, final List<String> listLines,
			Integer number) {
		try {
			StructuredText structuredText = prepareStructuredText(listLines);
			StringBuilder keyTextBuilder = new StringBuilder();
			keyTextBuilder.append(memoryFile.nameFile());
			keyTextBuilder.append(number.toString());
			structuredText.setUniqueKey(KeyGenerator.generateKey(keyTextBuilder.toString()));
			String keyStructuredText = KeyGenerator.generateKey(structuredText);
			if (null != structuredText && structuredText.getHaveBlankLine()) {
				UserSettings.getInstance().addKeyError(ErrorTypeEnum.BLANK_LINE, keyStructuredText);
			}
			if (null != structuredText && structuredText.getHaveMetaBlankLine()) {
				UserSettings.getInstance().addKeyError(ErrorTypeEnum.META_BLANK_LINE, keyStructuredText);
			}
			listLines.clear();
			if (null != beanConfiguration && null != structuredText) {
				try {
					sf.getListStructuredText().addAll(processingStructuredTextWithConfigurationBean(structuredText,
							beanConfiguration, memoryFile, sf));
					
				} catch (IndexOutOfBoundsException e) {
					UserSettings.getInstance().addKeyError(ErrorTypeEnum.STRUCTURED_TEXT,keyStructuredText);
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
			ConfigurationStructuredText beanConfiguration, MemoryFile memoryFile, StructuredFile structuredFile) throws IndexOutOfBoundsException {
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
	private StructuredText prepareStructuredText(List<String> textLines)
			throws StructuringException {
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
			st.setHaveMetaBlankLine(
					st.getHaveMetaBlankLine() || !checkAllFieldArePresent(st, Boolean.TRUE));
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
	private Boolean checkAllFieldArePresent(StructuredText structuredText,
			Boolean onlyMeta) {
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
	 * @param st          le texte structur� � renseigner
	 * @param lines       les lignes � traiter
	 * @throws StructuringException
	 */
	private void integrateContentToStructuredText(StructuredText st, List<String> lines)
			throws StructuringException {
		Content lastContent = null;
		for (String line : lines) {
			lastContent = prepareAndIntegrateContent(st, line, lastContent);
		}
	}

	/**
	 * Permet d'int�grer le contenu dans le texte structur�
	 * 
	 * @param st          le texte structur� � renseigner
	 * @param line        la ligne � traiter
	 * @param lastContent le dernier contenu trait�
	 * @return le contenu trait�
	 * @throws StructuringException
	 */
	private Content prepareAndIntegrateContent(StructuredText st, String line,
			Content lastContent) throws StructuringException {
		Configuration configuration = UserSettings.getInstance().getCurrentConfiguration();
		if (StringUtils.startsWith(line, configuration.getBaseCode())) {
			String lineWithoutLedaBalise = StringUtils.remove(line, configuration.getBaseCode());
			Optional<StructuredField> optionalStructuredField = configuration.getStructuredFieldList().stream()
					.filter(field -> lineWithoutLedaBalise.startsWith(field.getFieldName())).findFirst();
			if (optionalStructuredField.isPresent()) {
				String tagName = optionalStructuredField.get().getFieldName();
				String value = StringUtils.remove(lineWithoutLedaBalise, tagName);
				if (StringUtils.isBlank(value)) {
					if (optionalStructuredField.get().getIsMetaFile()) {
						st.setHaveMetaBlankLine(true);
					} else {
						st.setHaveBlankLine(true);
					}
				}
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
		} else if (null != lastContent && StringUtils.isNotBlank(lastContent.getValue())) {
			String currentValue = lastContent.getValue();
			lastContent.setValue(currentValue.concat("\n").concat(line));
			return lastContent;
		}
		// Ne devrait jamais se produire � cause du contr�le CheckLine
		return lastContent;
	}

	/**
	 * Permet de v�rifier la ligne et d'ajouter les erreurs si n�cessaire
	 * 
	 * @param line          ligne
	 * @param memoryFile    memory file
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
	 * Permet de v�rifier si la ligne d�tient des informations de balises
	 * @param configuration configuration
	 * @param line ligne � analyser
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
