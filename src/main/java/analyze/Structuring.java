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
import analyze.beans.StructuringError;
import analyze.beans.StructuringErrorDetails;
import analyze.beans.UserStructuredText;
import analyze.beans.specific.ConfigurationStructuredText;
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

	public Structuring(MemoryFile memoryFile, Configuration configuration) {
		this(memoryFile, configuration, null);
	}

	public Structuring(MemoryFile memoryFile, Configuration configuration,
			ConfigurationStructuredText beanConfiguration) {
		this.structuredFile = construct(memoryFile, configuration, beanConfiguration);
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
	private StructuredFile construct(MemoryFile memoryFile, Configuration configuration,
			ConfigurationStructuredText beanConfiguration) {
		final StructuredFile sf = new StructuredFile(memoryFile);
		final List<String> listLines = new ArrayList<String>();
		memoryFile.createIterator();
		Integer number = 1;
		while (memoryFile.hasLine()) {
			String l = memoryFile.getNextLine();
			checkLineAndAddErrorIfNecessary(configuration, l, memoryFile);
			if (StringUtils.isEmpty(l)) {
				try {
					StructuredText structuredText = prepareStructuredText(listLines, configuration);
					StringBuilder keyTextBuilder = new StringBuilder();
					keyTextBuilder.append(memoryFile.nameFile());
					keyTextBuilder.append(number.toString());
					structuredText.setUniqueKey(KeyGenerator.generateKey(keyTextBuilder.toString()));
					if (null != structuredText && structuredText.getHaveBlankLine()) {
						String keyStructuredText = KeyGenerator.generateKey(structuredText);
						UserSettings.getInstance().addKeyBlankLineError(keyStructuredText);
					}
					if (null != structuredText && structuredText.getHaveMetaBlankLine()) {
						String keyStructuredText = KeyGenerator.generateKey(structuredText);
						UserSettings.getInstance().addKeyMetaBlankLineError(keyStructuredText);
					}
					listLines.clear();
					if (null != beanConfiguration && null != structuredText) {
						sf.getListStructuredText().addAll(processingStructuredTextWithConfigurationBean(structuredText,
								beanConfiguration, memoryFile, sf));
					} else if (null != structuredText) {
						UserStructuredText userStructuredText = new UserStructuredText(memoryFile.nameFile(), number,
								structuredText);
						UserSettings.getInstance().addUserStructuredText(userStructuredText);
						number++;
						sf.getListStructuredText().add(structuredText);
					}
				} catch (StructuringException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			} else {
				listLines.add(l);
			}
		}
		return sf;
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
			ConfigurationStructuredText beanConfiguration, MemoryFile memoryFile, StructuredFile structuredFile) {
		String keyStructuredText = KeyGenerator.generateKey(structuredText);
		// beanConfiguration.getSpecificConfiguration().getIgnoredFieldList().forEach(it
		// -> structuredText.deleteContent(it));
		List<StructuredText> structuredTextProcessedList = new ArrayList<StructuredText>();
		final Map<String, List<String>> mapTagValuesProcessed = new HashedMap<String, List<String>>();
		beanConfiguration.getSpecificConfiguration().getTreatmentFieldList()
				.forEach(tag -> mapTagValuesProcessed.put(tag, structuredText.getContentWithDelimiterProcess(tag,
						beanConfiguration.getSpecificConfiguration().getDelimiter())));
		try {
			for (int i = 0; i < mapTagValuesProcessed.values().stream().mapToInt(s -> s.size()).max().getAsInt(); i++) {
				StructuredText structuredTextProcessed = structuredText.duplicate();
				for (String tag : mapTagValuesProcessed.keySet()) {
					structuredTextProcessed.modifyContent(tag, mapTagValuesProcessed.get(tag).get(i).trim());
				}
				structuredTextProcessedList.add(structuredTextProcessed);
			}
		} catch (IndexOutOfBoundsException e) {
			UserSettings.getInstance().addKeyStructuredTextError(keyStructuredText);
			StringBuilder sb = new StringBuilder();
			beanConfiguration.getSpecificConfiguration().getTreatmentFieldList()
					.forEach(s -> sb.append(s).append(" : ").append(structuredText.getContent(s)).append("\n"));
			structuredFile.getListStructuringError()
					.add(constructStructuringError(mapTagValuesProcessed, memoryFile.nameFile(), sb.toString()));
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
	private StructuredText prepareStructuredText(List<String> textLines, Configuration configuration)
			throws StructuringException {
		if (textLines.isEmpty()) {
			return null;
		}
		StructuredText st = new StructuredText();
		Boolean listMetaToAddIsEmpty = lstMetaContent.isEmpty();
		if (!listMetaToAddIsEmpty) {
			st.getListContent().addAll(lstMetaContent);
		}
		integrateContentToStructuredText(st, configuration, textLines);
		st.setHaveBlankLine(st.getHaveBlankLine() || !checkAllFieldArePresent(st, configuration, Boolean.FALSE));
		if (listMetaToAddIsEmpty) {
			st.setHaveMetaBlankLine(
					st.getHaveMetaBlankLine() || !checkAllFieldArePresent(st, configuration, Boolean.TRUE));
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
	private Boolean checkAllFieldArePresent(StructuredText structuredText, Configuration configuration,
			Boolean onlyMeta) {
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
	 * @param lastContent le dernier contenu trait�
	 * @throws StructuringException
	 */
	private void integrateContentToStructuredText(StructuredText st, Configuration configuration, List<String> lines)
			throws StructuringException {
		Content lastContent = null;
		for (String line : lines) {
			lastContent = prepareAndIntegrateContent(st, configuration, line, lastContent);
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
	private Content prepareAndIntegrateContent(StructuredText st, Configuration configuration, String line,
			Content lastContent) throws StructuringException {
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
	 * @param configuration configuration
	 * @param line          ligne
	 * @param memoryFile    memory file
	 */
	private void checkLineAndAddErrorIfNecessary(Configuration configuration, String line, MemoryFile memoryFile) {
		if (StringUtils.isBlank(line)) {
			return;
		}
		if (!StringUtils.startsWith(line, "[") && !checkIfHaveFieldsCode(configuration, line)) {
			return;
		} else {
			if (StringUtils.startsWith(line, configuration.getBaseCode())) {
				String lineWithoutLedaBalise = StringUtils.remove(line, configuration.getBaseCode());
				Optional<StructuredField> optionalStructuredField = configuration.getStructuredFieldList().stream()
						.filter(field -> lineWithoutLedaBalise.startsWith(field.getFieldName())).findFirst();
				if (optionalStructuredField.isPresent()) {
					return;
				}
			} else if (!checkIfHaveFieldsCode(configuration, line)) {
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
	private Boolean checkIfHaveFieldsCode(Configuration configuration, String line) {
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

	
	
	/**
	 * 
	 * Permet de construire l'erreur a partir des �l�ments du texte
	 * 
	 * @param mapTagValuesProcessed la map des tags en train d'�tre compil�s
	 * @param keyFile               la cl� du fichier
	 * @param keyText               la cl� du texte
	 * @return l'erreur de structure
	 */
	private StructuringError constructStructuringError(final Map<String, List<String>> mapTagValuesProcessed,
			String keyFile, String keyText) {
		StructuringError st = new StructuringError(keyFile, keyText);
		mapTagValuesProcessed.entrySet().forEach(es -> {
			StructuringErrorDetails sed = new StructuringErrorDetails(es.getKey());
			sed.getListElements().addAll(es.getValue());
			st.getDetails().add(sed);
		});
		return st;
	}

}
