package controler;

import model.ConfigurationModel;
import model.IConfigurationModel;
import model.analyze.LexicometricAnalysis;
import model.analyze.UserSettings;
import model.analyze.beans.*;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.constants.TypeFilterTextEnum;
import model.analyze.lexicometric.analyze.beans.Text;
import model.analyze.lexicometric.analyze.beans.Token;
import model.analyze.lexicometric.beans.LexicometricAnalyzeServerCmd;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import model.excel.beans.ExcelImportConfigurationCmd;
import model.exceptions.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.analysis.beans.AnalysisGroupDisplay;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.*;
import view.interfaces.IHierarchicalTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * Controller pour la jonction au modèle
 * 
 * @author jerem
 *
 */
public class ConfigurationControler implements IConfigurationControler {

	private static final String EXTENSION_TXT = ".txt";
	private final Logger logger = LoggerFactory.getLogger(ConfigurationControler.class);
	private final IConfigurationModel configurationModel = new ConfigurationModel();
	private String currentKeyFilteredText;

	@Override
	public void launchAnalyze(Boolean withSubFolder) throws LoadTextException {
		checkConfigurationAndFolder(this.configurationModel.getAnalyzeFolder());
		configurationModel.launchAnalyze(withSubFolder ? Integer.MAX_VALUE : 1);
	}

	@Override
	public void setCurrentConfiguration(Configuration configuration) {
		if (null != configuration) {
			this.configurationModel.setCurrentConfiguration(configuration);
		}
	}

	@Override
	public Optional<File> getTextsFolder() {
		return this.configurationModel.getTextsFolder();
	}

	@Override
	public Optional<File> getAnalyzeFolder() {
		return this.configurationModel.getAnalyzeFolder();
	}

	@Override
	public String getConfigurationName() {
		return this.configurationModel.getConfigurationName();
	}

	@Override
	public void setTextsFolder(File textsFolder) {
		if (null != textsFolder && textsFolder.isDirectory()) {
			this.configurationModel.setTextsFolder(textsFolder);
		}
	}

	@Override
	public void setAnalyzeFolder(File analyzeFolder) {
		if (null != analyzeFolder && analyzeFolder.isDirectory()) {
			this.configurationModel.setAnalyzeFolder(analyzeFolder);
		}
	}

	@Override
	public Map<String, String> getConfigurationFieldMetaFile() {
		if (StringUtils.isNotEmpty(getConfigurationName())) {
			return this.configurationModel.getConfigurationFieldMetaFile();
		}
		return new HashMap<>();
	}

	@Override
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		if (StringUtils.isNotBlank(nameFile)) {
			clearEditingCorpus();
			this.configurationModel.createNewCorpus(nameFile, metaFileFieldMap);
		}
	}

	@Override
	public Boolean haveEditingCorpus() {
		return this.configurationModel.haveEditingCorpus();
	}

	@Override
	public void clearEditingCorpus() {
		this.configurationModel.clearEditingCorpus();
	}

	@Override
	public Map<String, String> getConfigurationFieldCommonFile() {
		if (StringUtils.isNotEmpty(getConfigurationName())) {
			return this.configurationModel.getConfigurationFieldCommonFile();
		}
		return new HashMap<>();
	}

	@Override
	public Map<String, String> getFieldConfigurationNameLabelWithoutMetaMap() {
		return this.configurationModel.getFieldConfigurationNameLabelWithoutMetaMap();
	}

	@Override
	public String getEditingCorpusName() {
		return this.configurationModel.getEditingCorpusName();
	}

	@Override
	public Map<String, String> getListFieldSpecific(Integer index) {
		if (!haveEditingCorpus()) {
			return new HashMap<>();
		}
		if (index < getNbSpecificConfiguration()) {
			return this.configurationModel.getListFieldSpecific(index);
		}
		return this.configurationModel.getListFieldSpecific(getNbSpecificConfiguration() - 1);
	}

	@Override
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		if (!haveEditingCorpus()) {
			return new HashMap<>();
		}
		if (index < getNbSpecificConfiguration()) {
			return this.configurationModel.getListFieldHeaderSpecific(index);
		}
		return this.configurationModel.getListFieldHeaderSpecific(getNbSpecificConfiguration() - 1);
	}

	@Override
	public Integer getNbSpecificConfiguration() {
		if (StringUtils.isNotEmpty(getConfigurationName())) {
			return this.configurationModel.getNbSpecificConfiguration();
		}
		return 0;
	}

	@Override
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		if (haveEditingCorpus() && null != index && index < getNbSpecificConfiguration()) {
			this.configurationModel.updateSpecificFieldInEditingCorpus(index, specificFieldMap);
		}
	}

	@Override
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		if (!haveEditingCorpus()) {
			return new HashMap<>();
		}
		if (null != index && index < getNbSpecificConfiguration()) {
			return this.configurationModel.getSpecificFieldInEditingCorpus(index);
		}
		return this.configurationModel.getSpecificFieldInEditingCorpus(getNbSpecificConfiguration() - 1);
	}

	@Override
	public Map<String, List<String>> getSpecificFieldInUserStructuredText(String keyText, Integer index) {
		if (null != index && index < getNbSpecificConfiguration()) {
			return this.configurationModel.getSpecificFieldInUserStructuredText(keyText, index);
		}
		return this.configurationModel.getSpecificFieldInUserStructuredText(keyText,getNbSpecificConfiguration() - 1);
	}

	@Override
	public String getFieldInEditingCorpus(String key) {
		if (haveEditingCorpus() && StringUtils.isNotBlank(key)) {
			return this.configurationModel.getFieldInEditingCorpus(key);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public void updateFieldInEditingCorpus(String key, String value) {
		if (haveEditingCorpus() && StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			this.configurationModel.updateFieldInEditingCorpus(key, value);
		}
	}

	@Override
	public void writeCorpus() throws IOException {
		if (haveEditingCorpus()) {
			this.configurationModel.writeCorpus();
		}
	}

	@Override
	public void addEditingTextToCurrentCorpus() {
		if (haveEditingCorpus()) {
			this.configurationModel.addEditingTextToCurrentCorpus();
		}
	}

	@Override
	public Integer getNbLinesError() {
		return this.configurationModel.getNbLinesError();
	}

	@Override
	public ErrorStructuredLine getErrorLine(Integer index) {
		if (null != index && index < getNbLinesError()) {
			LineError errorLine = this.configurationModel.getErrorLine(index);
			return new ErrorStructuredLine(errorLine.getPath().toString(), errorLine.getLineWithError(),
					errorLine.getIndex() + 1);
		}
		return null;
	}

	@Override
	public void updateLineError(Integer index, String lineFixed) {
		if (null != index && index < getNbLinesError()) {
			LineError errorLine = this.configurationModel.getErrorLine(index);
			errorLine.setLineFixed(lineFixed);
			this.configurationModel.updateLineError(index, errorLine);
		}
	}

	@Override
	public void saveFileAfterFixedErrorLine() throws IOException {
		if (getNbLinesError() > 0) {
			this.configurationModel.saveFileAfterFixedErrorLine();
		}
	}

	@Override
	public Integer getNbTextsError() {
		return this.configurationModel.getNbTextsError();
	}

	@Override
	public void loadNextErrorText() {
		if (haveTextsInErrorRemaining()) {
			this.configurationModel.loadNextErrorText();
		}
	}

	@Override
	public void saveCurrentStateOfFixedText() {
		this.configurationModel.saveCurrentStateOfFixedText();
	}

	@Override
	public void writeFixedText() throws IOException {
		this.configurationModel.writeFixedText();
	}

	@Override
	public void applyFixedErrorText() {
		this.configurationModel.applyFixedErrorText();
	}

	@Override
	public Boolean haveCurrentStateFile() {
		return this.configurationModel.haveCurrentStateFile();
	}

	@Override
	public void restoreCurrentState() throws IOException {
		this.configurationModel.restoreCurrentState();
	}

	@Override
	public Boolean haveTextsInErrorRemaining() {
		return this.configurationModel.haveTextsInErrorRemaining();
	}

	@Override
	public Integer getNbBlankLinesError() {
		return this.configurationModel.getNbBlankLinesError();
	}

	@Override
	public void loadNextErrorBlankLine() {
		if (haveBlankLinesInErrorRemaining()) {
			this.configurationModel.loadNextErrorBlankLine();
		}
	}

	@Override
	public Boolean haveBlankLinesInErrorRemaining() {
		return this.configurationModel.haveBlankLinesInErrorRemaining();
	}

	@Override
	public Map<String, String> getConfigurationSpecificLabelNameFileMap() {
		if (StringUtils.isNotEmpty(getConfigurationName())) {
			return this.configurationModel.getConfigurationSpecificLabelNameFileMap();
		}
		return new HashMap<>();
	}

	@Override
	public Map<String, String> getFieldConfigurationNameLabelMap() {
		if (StringUtils.isNotEmpty(getConfigurationName())) {
			return this.configurationModel.getFieldConfigurationNameLabelMap();
		}
		return new HashMap<>();
	}

	@Override
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		if (StringUtils.isNotBlank(labelSpecificConfiguration)) {
			return this.configurationModel.getFieldListToProcess(labelSpecificConfiguration);
		}
		return new ArrayList<>();
	}

	@Override
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		if (StringUtils.isNotBlank(labelSpecificConfiguration)) {
			return this.configurationModel.getFieldListForbiddenToDisplay(labelSpecificConfiguration);
		}
		return new ArrayList<>();
	}

	@Override
	public void loadNextErrorMetaBlankLine() {
		if (haveMetaBlankLineInErrorRemaining()) {
			this.configurationModel.loadNextErrorMetaBlankLine();
		}
	}

	@Override
	public Boolean haveMetaBlankLineInErrorRemaining() {
		return this.configurationModel.haveMetaBlankLineInErrorRemaining();
	}

	@Override
	public Integer getNbMetaBlankLineToFixed() {
		return this.configurationModel.getNbMetaBlankLineToFixed();
	}

	@Override
	public Map<Path, Path> moveAllFilesFromTextAnalyzeToLibrary() throws IOException, MoveFileException {
		return this.configurationModel.moveAllFilesFromTextAnalyzeToLibrary();
	}

	@Override
	public List<StructuredFile> getListOfStructuredFileForAnalyze() {
		return this.configurationModel.getListOfStructuredFileForAnalyze();
	}

	@Override
	public List<StructuredFile> getListOfStructuredFileForTexts() {
		return this.configurationModel.getListOfStructuredFileForTexts();
	}

	@Override
	public void clearAnalyze() {
		this.configurationModel.clearAnalyze();
	}

	@Override
	public void clearTexts() {
		this.configurationModel.clearTexts();
	}

	@Override
	public Integer getNbTextLoadedForAnalyze() {
		return this.configurationModel.getNbTextLoadedForAnalyze();
	}

	@Override
	public Integer getNbTextLoadedForTexts() {
		return this.configurationModel.getNbTextLoadedForTexts();
	}

	@Override
	public void generateExcelFromAnalyze(ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (null != cmd) {
			this.configurationModel.generateExcelFromAnalyze(cmd);
		}
	}

	@Override
	public void generateExcelFromTexts(ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (null != cmd) {
			this.configurationModel.getTextsLoadedForTextsList().parallelStream()
					.filter(ust -> this.configurationModel.getKeyFilteredList().contains(ust.getKey()))
					.forEach(ust -> cmd.addUniqueKey(ust.getStructuredText().getUniqueKey()));
			this.configurationModel.generateExcelFromTexts(cmd);
		}
	}

	@Override
	public List<DisplayText> getDisplayTextListFromFilteredText(Integer start, Integer nbTextToLoad) {
		Set<DisplayText> setTextList = new LinkedHashSet<>();
		// SI configuration existe
		if (StringUtils.isNotBlank(this.configurationModel.getConfigurationName())) {
			Map<String, String> configurationFieldCommonFile = this.configurationModel
					.getConfigurationFieldCommonFile();
			int end = start + nbTextToLoad;
			if (end > this.configurationModel.getKeyFilteredList().size()) {
				end = this.configurationModel.getKeyFilteredList().size();
			}
			// PARCOURS La liste des clés filtrés
			for (int i = start; i < end; i++) {
				String key = this.configurationModel.getKeyFilteredList().get(i);
				this.configurationModel.loadKeyFiltered(key);
				Map<String, String> mapKeyValue = new LinkedHashMap<>();
				// PARCOURS La liste des champs commun pour se procurer le label et sa valeur
				configurationFieldCommonFile.forEach((key1, value1) -> {
					String value = this.configurationModel.getFieldInEditingCorpus(key1);
					mapKeyValue.put(value1, value);
				});
				setTextList.add(new DisplayText(this.configurationModel.getEditingCorpusName(), i, mapKeyValue, key));
			}
		}
		return Collections.unmodifiableList(new LinkedList<>(setTextList));
	}

	@Override
	public void loadTexts() throws LoadTextException {
		checkConfigurationAndFolder(this.configurationModel.getTextsFolder());
		configurationModel.loadTexts();
	}

	/**
	 * Permet de vérifier que la configuration et le dossier d'analyse existe
	 */
	private void checkConfigurationAndFolder(Optional<File> folder) {
		ServerException serverException = new ServerException();
		if (StringUtils.isBlank(this.configurationModel.getConfigurationName())) {
			serverException.addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.ERROR_CONFIGURATION)
					.build());
		}
		if (folder.isEmpty()) {
			serverException.addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.ERROR_ANALYZE_FOLDER)
					.build());
		}
		if (!serverException.getInformationExceptionSet().isEmpty()) {
			throw serverException;
		}
	}

	@Override
	public Integer getNbDisplayTextListFromFilteredText() {
		return this.configurationModel.getKeyFilteredList().size();
	}

	@Override
	public void loadFilteredText(String key) {
		if (null != key) {
			this.configurationModel.loadKeyFiltered(key);
			this.currentKeyFilteredText = key;
		}
	}

	@Override
	public void writeEditText() throws IOException {
		this.configurationModel.writeEditText();
	}

	@Override
	public void applyEditText() {
		this.configurationModel.applyEditText();
	}

	@Override
	public void deleteTextAndWriteCorpusFromFolderText(String key) throws IOException {
		if (null != key) {
			this.configurationModel.deleteTextAndWriteCorpusFromFolderText(key);
		}
	}

	@Override
	public List<String> getAllCorpusNameForFilteredText() {
		return this.configurationModel.getAllCorpusName(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public void applyAllFiltersOnCorpusForFolderText(String corpusName, List<Filter> filtersList) {
		if (null != filtersList) {
			List<FilterText> filtersTextList = filtersList.stream().map(f -> {
				if (FilterTypeEnum.CONTAINS.equals(f.getType())) {
					return new FilterText(f.getField(), TypeFilterTextEnum.CONTAINS, StringUtils.trim(f.getValue()));
				} else if (FilterTypeEnum.EQUAL.equals(f.getType())) {
					return new FilterText(f.getField(), TypeFilterTextEnum.EQUAL, StringUtils.trim(f.getValue()));
				}
				return null;
			}).collect(Collectors.toList());
			FilterCorpus filterCorpus = new FilterCorpus(corpusName, filtersTextList);
			this.configurationModel.applyAllFiltersOnCorpusForFolderText(filterCorpus);
		}
	}

	@Override
	public void addTextToCurrentCorpusFromFolderText() {
		this.configurationModel.addTextToCurrentCorpus(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public void cleanCurrentEditingCorpusForAddText() {
		this.configurationModel.cleanCurrentEditingCorpusForAddText();
	}

	@Override
	public List<String> getConfigurationNameList() {
		return this.configurationModel.getConfigurationNameList();
	}

	@Override
	public void setCurrentConfiguration(String name) {
		if (StringUtils.isNotBlank(name)) {
			this.configurationModel.setCurrentConfiguration(name);
		}
	}

	@Override
	public Optional<File> getConfigurationFolder() {
		return this.configurationModel.getConfigurationFolder();
	}

	@Override
	public Boolean haveErrorInSpecificFieldInEditingCorpus() {
		if (this.configurationModel.haveEditingCorpus()) {
			return this.configurationModel.haveErrorInSpecificFieldInEditingCorpus();
		}
		return false;
	}

	@Override
	public FilesToAnalyzeInformation getNameFileToAnalyzeList(File pathFolderToAnalyze, Boolean withSubFolder)
			throws IOException {
		if (null != pathFolderToAnalyze && pathFolderToAnalyze.isDirectory()) {
			Integer depth = withSubFolder ? Integer.MAX_VALUE : 1;
			return this.configurationModel.getNameFileToAnalyzeList(pathFolderToAnalyze, depth);
		}
		return null;
	}

	@Override
	public Map<String, String> getAllField() {
		return this.configurationModel.getAllField();
	}

	@Override
	public String getStructuredLine(String field, String content) {
		return this.configurationModel.getBaseCode() +
				field +
				content.trim();
	}

	@Override
	public void removeCurrentStateFile() {
		if (this.configurationModel.haveCurrentStateFile()) {
			this.configurationModel.removeCurrentStateFile();
		}
	}

	@Override
	public void export(ExportTypeEnum typeExport, String directory, String nameFile) throws IOException {
		if (null != typeExport && StringUtils.isNotBlank(directory)) {
			if (StringUtils.isNotBlank(nameFile) && !StringUtils.contains(nameFile, EXTENSION_TXT)) {
				nameFile = StringUtils.join(nameFile, EXTENSION_TXT);
			}
			this.configurationModel.export(typeExport, directory, nameFile);
		}
	}

	@Override
	public Boolean haveInconsistencyError() {
		return this.configurationModel.haveInconsistencyError();
	}

	@Override
	public List<InconsistencyError> getInconsistencyChangeTextErrorList() {
		return this.configurationModel.getInconsistencyChangeTextErrorList().stream()
				.map(error -> new InconsistencyError(error.getOldStructuredFieldNewText().getFieldName(),
						error.getNewStructuredFieldNewText().getFieldName(), error.getOldLine(), error.getNewLine(),
						error.getOldStructuredFieldNewText().getIsMetaFile(), error.getNameFile()))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Boolean haveMissingBaseCodeError() {
		return this.configurationModel.haveMissingBaseCodeError();
	}

	@Override
	public List<BaseCodeError> getMissingBaseCodeErrorList() {
		return this.configurationModel.getMissingBaseCodeErrorList().stream()
				.map(error -> new BaseCodeError(error.getStructuredFieldFound().getFieldName(), error.getLine(),
						error.getNameFile()))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Boolean haveTextInFilteredText(DirectionTypeEnum direction) {
		if (null != this.currentKeyFilteredText) {
			List<String> keyFilteredList = this.configurationModel.getKeyFilteredList();
			int indexKey = keyFilteredList.indexOf(this.currentKeyFilteredText);
			return switch (direction) {
				case PREVIOUS -> indexKey > 0;
				case NEXT -> indexKey < (keyFilteredList.size() - 1);
			};
		}
		return false;
	}

	@Override
	public void loadFilteredText(DirectionTypeEnum direction) {
		if (null != this.currentKeyFilteredText) {
			List<String> keyFilteredList = this.configurationModel.getKeyFilteredList();
			int indexKey = keyFilteredList.indexOf(this.currentKeyFilteredText);
			switch (direction) {
				case PREVIOUS -> loadFilteredText(keyFilteredList.get(indexKey - 1));
				case NEXT -> loadFilteredText(keyFilteredList.get(indexKey + 1));
			}
		}
	}

	@Override
	public String getDelimiterSpecific(Integer index) {
		return this.configurationModel.getDelimiterSpecific(index);
	}

	@Override
	public Integer getProgress() {
		return this.configurationModel.getProgress();
	}

	@Override
	public void cancel() {
		this.configurationModel.cancel();
	}

	@Override
	public boolean treatmentIsCancelled() {
		return this.configurationModel.treatmentIsCancelled();
	}

	@Override
	public boolean isRunning() {
		return this.configurationModel.isRunning();
	}

	@Override
	public void resetProgress() {
		this.configurationModel.resetProgress();
	}

	@Override
	public void importExcel(ExcelImportConfigurationCmd excelImportConfigurationCmd) throws ImportExcelException, IOException, LoadTextException {
		Set<InformationException> informationExceptionSet = new HashSet<>();
		if (Objects.isNull(excelImportConfigurationCmd.getFileToImport()) || !excelImportConfigurationCmd.getFileToImport().exists()) {
			informationExceptionSet.add(new InformationExceptionBuilder()
				.errorCode(ErrorCode.FILE_NOT_EXIST)
				.objectInError(excelImportConfigurationCmd)
				.build());
		}
		if (excelImportConfigurationCmd.getFieldToImportList().isEmpty()) {
			informationExceptionSet.add(new InformationExceptionBuilder()
					.errorCode(ErrorCode.NONE_FIELD_SELECTED)
					.objectInError(excelImportConfigurationCmd)
					.build());
		}
		if (informationExceptionSet.isEmpty()) {
			informationExceptionSet.addAll(this.configurationModel.importExcel(excelImportConfigurationCmd));
		}

		if (!informationExceptionSet.isEmpty()) {
			ServerException serverException = new ServerException();
			informationExceptionSet.forEach(serverException::addInformationException);
			throw serverException;
		}
	}

	@Override
	public String getLexicometricDefaultProfile() {
		return this.configurationModel.getLexicometricDefaultProfile();
	}

	@Override
	public ILexicometricConfiguration getLexicometricConfiguration(IHierarchicalTable lexicometricEditEnum, ILexicometricHierarchical lexicometricHierarchical) {
		LexicometricConfigurationEnum lexicometricConfigurationEnumFromViewEnum = LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(lexicometricEditEnum);
		ILexicometricHierarchical lexicometricHierarchicalServer = lexicometricConfigurationEnumFromViewEnum.getLexicometricHierarchicalViewToLexicometricHierarchicalServer().apply(lexicometricHierarchical);
		return lexicometricConfigurationEnumFromViewEnum.getLexicometricHierarchicalILexicometricConfigurationFunction().apply(lexicometricHierarchicalServer);
	}

	@Override
	public void addConfigurationLexicometricProfile(String olProfile, String newProfileName, LexicometricEditEnum lexicometricEditEnum, Boolean isCopy) {
		LexicometricConfigurationEnum lexicometricConfigurationEnumFromViewEnum = LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(lexicometricEditEnum);
		lexicometricConfigurationEnumFromViewEnum.getAddProfilConsumer().accept(newProfileName);
		if (isCopy) {
			lexicometricConfigurationEnumFromViewEnum.getCopyConsumer().accept(olProfile, newProfileName);
		}
	}

	@Override
	public void removeConfigurationLexicometricProfile(String profileName, LexicometricEditEnum lexicometricEditEnum) {
		LexicometricConfigurationEnum lexicometricConfigurationEnumFromViewEnum = LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(lexicometricEditEnum);
		lexicometricConfigurationEnumFromViewEnum.getRemoveProfilConsumer().accept(profileName);
	}

	@Override
	public void saveLexicometricProfilInDisk(LexicometricEditEnum lexicometricEditEnum, String profileToSave) {
		LexicometricConfigurationEnum lexicometricConfigurationEnumFromViewEnum = LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(lexicometricEditEnum);
		lexicometricConfigurationEnumFromViewEnum.getSaveInDiskConsumer().accept(profileToSave);
	}

	@Override
	public void saveLexicometricAllProfilInDisk(LexicometricEditEnum lexicometricEditEnum) {
		LexicometricConfigurationEnum lexicometricConfigurationEnumFromViewEnum = LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(lexicometricEditEnum);
		lexicometricConfigurationEnumFromViewEnum.getAllProfils().apply(null).forEach(p -> lexicometricConfigurationEnumFromViewEnum.getSaveInDiskConsumer().accept(p));
	}

	@Override
	public List<String> getFilteredTextKeyList() {
		if (StringUtils.isNotBlank(this.configurationModel.getConfigurationName())) {
			return this.configurationModel.getKeyFilteredList();
		}
		return Collections.emptyList();
	}

	@Override
	public void launchLexicometricAnalyze(LexicometricAnalyzeServerCmd cmd) {
		cmd.getLexicometricAnalyzeTypeEnum().getAnalyzeServerCmdConsumer().accept(cmd);
	}

	@Override
	public String getValueFromKeyTextAndField(String key, String field) {
		Optional<UserStructuredText> textFromKey = UserSettings.getInstance().getTextFromKey(key);
		if (textFromKey.isPresent()) {
			return textFromKey.get().getStructuredText().getContent(field);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getValueFromKeyTextAndFieldWithAnalyzeTreatment(String key, String field, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
		return LexicometricAnalysis.getInstance().getTextPreTreatment(getValueFromKeyTextAndField(key, field), preTreatmentListLexicometricMap);
	}

	@Override
	public Collection<String> getPotentialProperNounCollection(Set<String> key, Set<String> fieldSet, Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
		return LexicometricAnalysis.getInstance().getPotentialProperNounCollection(key, fieldSet, preTreatmentListLexicometricMap);
	}

	@Override
	public Collection<String> getKeyTextSetWithSelectedWordsFromAnalyze(Set<String> keySet, Set<String> wordSet) {
		return LexicometricAnalysis.getInstance().getKeyTextSetWithSelectedWords(keySet, wordSet);
	}

	@Override
	public Set<AnalysisGroupDisplay> getAnalysisGroupDisplaySet(Set<String> keySet, Set<String> fieldSet) {
		return LexicometricAnalysis.getInstance().getAnalysisGroupDisplaySet(keySet, fieldSet);
	}

	@Override
	public Collection<FrequencyOrder> getFrequencyOrderList() {
		return configurationModel.getFrequencyOrderList();
	}

	@Override
	public void saveFrequencyOrderInDisk() {
		configurationModel.saveFrequencyOrderInDisk();
	}

	@Override
	public void saveFrequencyOrder(Collection<FrequencyOrder> frequencyOrderCollection) {
		configurationModel.saveFrequencyOrder(frequencyOrderCollection);
	}
}
