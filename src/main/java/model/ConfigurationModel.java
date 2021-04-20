package model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import model.analyze.Dispatcher;
import model.analyze.UserFolder;
import model.analyze.UserLexicometricAnalysisSettings;
import model.analyze.UserSettings;
import model.analyze.beans.*;
import model.analyze.constants.ErrorTypeEnum;
import model.analyze.constants.FolderSettingsEnum;
import model.analyze.edit.LexicometricEditTableService;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import model.excel.beans.ExcelImportConfigurationCmd;
import model.exceptions.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.beans.EditTable;
import view.beans.EditTableElement;
import view.beans.ExportTypeEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 
 * Cette classe permet d'intéragir avec les informations stockés et effectuer
 * des actions Il fait des appels au dispatcher et à la configuration
 * utilisateur
 * 
 * @author jerem
 *
 */
public class ConfigurationModel implements IConfigurationModel {

	private Logger logger = LoggerFactory.getLogger(ConfigurationModel.class);
	private Dispatcher dispatcher = new Dispatcher();
	private LexicometricEditTableService lexicometricEditTableService = new LexicometricEditTableService();

	@Override
	public void launchAnalyze(Integer depth) throws LoadTextException {
		logger.debug("CALL launchAnalyze");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			this.dispatcher.launchAnalyze(depth);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		stopWatch.stop();
		logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
		logger.info(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
	}

	@Override
	public void setCurrentConfiguration(Configuration configuration) {
		logger.debug(String.format("CALL setCurrentConfiguration : Name %s", configuration.getName()));
		UserSettings.getInstance().setCurrentConfiguration(configuration);
	}

	@Override
	public List<StructuredFile> getListOfStructuredFileForAnalyze() {
		logger.debug("CALL getListOfStructuredFileForAnalyze");
		return UserSettings.getInstance().getStructuredFileList(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	@Override
	public List<StructuredFile> getListOfStructuredFileForTexts() {
		logger.debug("CALL getListOfStructuredFileForTexts");
		return UserSettings.getInstance().getStructuredFileList(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public void clearAnalyze() {
		logger.debug("CALL clearAnalyze");
		UserSettings.getInstance().clearAllSession(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	@Override
	public void clearTexts() {
		logger.debug("CALL clearText");
		UserSettings.getInstance().clearAllSession(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public Optional<File> getTextsFolder() {
		logger.debug("CALL getTextsFolder");
		return UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public Optional<File> getAnalyzeFolder() {
		logger.debug("CALL getAnalyzeFolder");
		return UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	@Override
	public String getConfigurationName() {
		logger.debug("CALL getConfigurationName");
		if (null != UserSettings.getInstance().getCurrentConfiguration()) {
			return UserSettings.getInstance().getCurrentConfiguration().getName();
		}
		return StringUtils.EMPTY;
	}

	@Override
	public void setTextsFolder(File textsFolder) {
		logger.debug(String.format("CALL setTextsFolder : Folder %s", textsFolder));
		this.dispatcher.setTextsFolder(textsFolder);
	}

	@Override
	public void setAnalyzeFolder(File folderAnalyze) {
		logger.debug(String.format("CALL setAnalyzeFolder : Folder %s", folderAnalyze));
		UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_ANALYZE, folderAnalyze);
	}

	@Override
	public Map<String, String> getConfigurationFieldMetaFile() {
		logger.debug("CALL getConfigurationFieldMetaFile");
		return UserSettings.getInstance().getListFieldMetaFile();
	}

	@Override
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		logger.debug(String.format("CALL createNewCorpus : Nom du fichier %s", nameFile));
		UserSettings.getInstance().createNewCorpus(nameFile, metaFileFieldMap);
	}

	@Override
	public Boolean haveEditingCorpus() {
		logger.debug("CALL haveEditingCorpus");
		return UserSettings.getInstance().haveEditingCorpus();
	}

	@Override
	public void clearEditingCorpus() {
		logger.debug("CALL clearEditingCorpus");
		UserSettings.getInstance().clearEditingCorpus();
	}

	@Override
	public Map<String, String> getConfigurationFieldCommonFile() {
		logger.debug("CALL getConfigurationFieldCommonFile");
		return UserSettings.getInstance().getListFieldCommonFile();
	}

	@Override
	public String getEditingCorpusName() {
		logger.debug("CALL getEditingCorpusName");
		return UserSettings.getInstance().getEditingCorpusNameFile();
	}

	@Override
	public Map<String, String> getListFieldSpecific(Integer index) {
		logger.debug(String.format("CALL getListFieldSpecific : index %d", index));
		return UserSettings.getInstance().getListFieldSpecific(index);
	}

	@Override
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		logger.debug(String.format("CALL getListFieldHeaderSpecific : index %d", index));
		return UserSettings.getInstance().getListFieldHeaderSpecific(index);
	}

	@Override
	public Integer getNbSpecificConfiguration() {
		logger.debug("CALL getNbSpecificConfiguration");
		return UserSettings.getInstance().getNbSpecificConfiguration();
	}

	@Override
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		logger.debug(String.format("CALL updateSpecificFieldInEditingCorpus : index %d", index));
		UserSettings.getInstance().updateSpecificFieldInEditingCorpus(index, specificFieldMap);
	}

	@Override
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		logger.debug(String.format("CALL getSpecificFieldInEditingCorpus : index %d", index));
		return UserSettings.getInstance().getSpecificFieldInEditingCorpus(index);
	}

	@Override
	public String getFieldInEditingCorpus(String key) {
		logger.debug(String.format("CALL getFieldInEditingCorpus : key %s", key));
		return UserSettings.getInstance().getFieldInEditingCorpus(key);
	}

	@Override
	public void updateFieldInEditingCorpus(String key, String value) {
		logger.debug(String.format("CALL updateFieldInEditingCorpus : key %s - value %s", key, value));
		UserSettings.getInstance().updateFieldInEditingCorpus(key, value);
	}

	@Override
	public void writeCorpus() throws IOException {
		logger.debug("CALL writeCorpus");
		this.dispatcher.writeCorpus();
	}

	@Override
	public void addEditingTextToCurrentCorpus() {
		logger.debug("CALL addEditingTextToCurrentCorpus");
		UserSettings.getInstance().addEditingTextToCurrentCorpus();
	}

	@Override
	public Integer getNbLinesError() {
		logger.debug("CALL getNbLinesError");
		return UserSettings.getInstance().getNbLineError();
	}

	@Override
	public LineError getErrorLine(Integer index) {
		logger.debug(String.format("CALL getErrorLine : index %d", index));
		return UserSettings.getInstance().getLineError(index);
	}

	@Override
	public void updateLineError(Integer index, LineError lineError) {
		logger.debug(String.format("CALL updateLineError : index %d", index));
		UserSettings.getInstance().updateLineError(index, lineError);
	}

	@Override
	public void saveFileAfterFixedErrorLine() throws IOException {
		logger.debug("CALL saveFileAfteFixedErrorLine");
		UserSettings.getInstance().fixedErrorLinesInAllMemoryFiles();
	}

	@Override
	public Integer getNbTextsError() {
		logger.debug("CALL getNbTextsError");
		return UserSettings.getInstance().getNbTextsError();
	}

	@Override
	public void loadNextErrorText() {
		logger.debug("CALL loadNextErrorText");
		UserSettings.getInstance().loadErrorText(
				UserSettings.getInstance().getKeysInError(ErrorTypeEnum.STRUCTURED_TEXT).iterator().next(),
				ErrorTypeEnum.STRUCTURED_TEXT);
	}

	@Override
	public void saveCurrentStateOfFixedText() {
		logger.debug("CALL saveCurrentStateOfFixedText");
		this.dispatcher.saveCurrentStateOfFixedText();
	}

	@Override
	public void writeFixedText() throws IOException {
		logger.debug("CALL writeFixedText");
		this.dispatcher.writeFixedText();
	}

	@Override
	public void applyFixedErrorText() {
		logger.debug("CALL applyFixedErrorText");
		UserSettings.getInstance().applyCurrentTextToStructuredText(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	@Override
	public Boolean haveCurrentStateFile() {
		logger.debug("CALL haveCurrentStateFile");
		return this.dispatcher.haveCurrentStateFile();
	}

	@Override
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
		logger.debug("CALL restoreCurrentState");
		this.dispatcher.restoreCurrentState();
	}

	@Override
	public Boolean haveTextsInErrorRemaining() {
		logger.debug("CALL haveTextsInErrorRemaining");
		return UserSettings.getInstance().haveErrorRemaining(ErrorTypeEnum.STRUCTURED_TEXT);
	}

	@Override
	public Integer getNbBlankLinesError() {
		logger.debug("CALL getNbBlankLinesError");
		return UserSettings.getInstance().getNbBlankLineError();
	}

	@Override
	public void loadNextErrorBlankLine() {
		logger.debug("CALL loadNextErrorBlankLine");
		UserSettings.getInstance().loadErrorText(
				UserSettings.getInstance().getKeysInError(ErrorTypeEnum.BLANK_LINE).iterator().next(), ErrorTypeEnum.BLANK_LINE);
	}

	@Override
	public Integer getNbTextLoadedForAnalyze() {
		logger.debug("CALL getNbTextLoadedForAnalyze");
		return UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_ANALYZE).size();
	}

	@Override
	public Integer getNbTextLoadedForTexts() {
		logger.debug("CALL getNbTextLoadedForTexts");
		return UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_TEXTS).size();
	}

	@Override
	public Boolean haveBlankLinesInErrorRemaining() {
		logger.debug("CALL haveBlankLinesInErrorRemaining");
		return UserSettings.getInstance().haveErrorRemaining(ErrorTypeEnum.BLANK_LINE);
	}

	@Override
	public Map<String, String> getConfigurationSpecificLabelNameFileMap() {
		logger.debug("CALL getConfigurationSpecificLabelNameFileMap");
		if (null != UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList()) {
			return UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().stream().collect(
					Collectors.toMap(SpecificConfiguration::getLabel, SpecificConfiguration::getNameFileSuffix));
		}
		return new HashMap<String, String>();
	}

	@Override
	public void generateExcelFromAnalyze(ExcelGenerateConfigurationCmd cmd) throws IOException {
		logger.debug("CALL generateExcelFromAnalyze");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		this.dispatcher.generateExcel(FolderSettingsEnum.FOLDER_ANALYZE, cmd);
		stopWatch.stop();
		logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
	}

	@Override
	public void generateExcelFromTexts(ExcelGenerateConfigurationCmd cmd) throws IOException {
		logger.debug("CALL generateExcelFromTexts");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		this.dispatcher.generateExcel(FolderSettingsEnum.FOLDER_TEXTS, cmd);
		stopWatch.stop();
		logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
	}

	@Override
	public Map<String, String> getFieldConfigurationNameLabelMap() {
		logger.debug("CALL getFieldConfigurationNameLabelMap");
		Map<String, String> mapField = new LinkedHashMap<String, String>();
		UserSettings.getInstance().getCurrentConfiguration().getStructuredFieldList().stream()
				.sorted(Comparator.comparing(StructuredField::getOrder))
				.forEach(sf -> mapField.put(sf.getFieldName(), sf.getLabel()));
		return mapField;
	}

	@Override
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		logger.debug(String.format("CALL getFieldListToProcess : label %s", labelSpecificConfiguration));
		return this.dispatcher.getFieldListToProcess(labelSpecificConfiguration);
	}

	@Override
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		logger.debug(String.format("CALL getFieldListForbiddenToDisplay : label %s", labelSpecificConfiguration));
		return this.dispatcher.getFieldListForbiddenToDisplay(labelSpecificConfiguration);
	}

	@Override
	public void loadNextErrorMetaBlankLine() {
		logger.debug("CALL loadNextErrorMetaBlankLine");
		UserSettings.getInstance().loadErrorText(
				UserSettings.getInstance().getKeysInError(ErrorTypeEnum.META_BLANK_LINE).iterator().next(),
				ErrorTypeEnum.META_BLANK_LINE);
	}

	@Override
	public Boolean haveMetaBlankLineInErrorRemaining() {
		logger.debug("CALL haveMetaBlankLineInErrorRemaining");
		return UserSettings.getInstance().haveErrorRemaining(ErrorTypeEnum.META_BLANK_LINE);
	}

	@Override
	public Integer getNbMetaBlankLineToFixed() {
		logger.debug("CALL getNbMetaBlankLineToFixed");
		return UserSettings.getInstance().getNbKeysInError(ErrorTypeEnum.META_BLANK_LINE);
	}

	@Override
	public Map<Path, Path> moveAllFilesFromTextAnalyzeToLibrary() throws IOException, MoveFileException {
		logger.debug("CALL moveAllFilesFromTextAnalyzeToLibrary");
		return this.dispatcher.moveAllFilesFromTextAnalyzeToLibrary();
	}

	@Override
	public List<String> getKeyFilteredList() {
		logger.debug("CALL getKeyFilteredList");
		return UserSettings.getInstance().getKeysFilteredList();
	}

	@Override
	public void loadKeyFiltered(String key) {
		logger.debug(String.format("CALL getFieldListToProcess : key %s", key));
		UserSettings.getInstance().loadFilteredText(key);
	}

	@Override
	public void loadTexts() throws LoadTextException {
		logger.debug("CALL loadTexts");
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			this.dispatcher.loadTexts();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		stopWatch.stop();
		logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
	}

	@Override
	public List<UserStructuredText> getTextsLoadedForTextsList() {
		logger.debug("CALL getTextsLoadedForTextsList");
		return UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public void writeEditText() throws IOException {
		logger.debug("CALL writeEditText");
		this.dispatcher.writeEditText();
	}

	@Override
	public void applyEditText() {
		logger.debug("CALL applyEditText");
		UserSettings.getInstance().applyCurrentTextToStructuredText(FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public void deleteTextAndWriteCorpusFromFolderText(String key) throws IOException {
		logger.debug("CALL deleteTextAndWriteCorpusFromFolderText");
		this.dispatcher.deleteTextAndWriteCorpus(key, FolderSettingsEnum.FOLDER_TEXTS);
	}

	@Override
	public List<String> getAllCorpusName(FolderSettingsEnum folderType) {
		logger.debug("CALL getAllCorpusName");
		return UserSettings.getInstance().getAllCorpusName(folderType);
	}

	@Override
	public void applyAllFiltersOnCorpusForFolderText(FilterCorpus filterCorpus) {
		logger.debug("CALL applyAllFiltersOnCorpusForFolderText");
		if (null != filterCorpus) {
			if (StringUtils.isBlank(filterCorpus.getCorpusName()) && filterCorpus.getFiterTextList().isEmpty()) {
				UserSettings.getInstance().addAllUserStructuredTextToKeyFilterList(FolderSettingsEnum.FOLDER_TEXTS);
			} else {
				UserSettings.getInstance().applyFilterOnCorpusForFolderText(filterCorpus,
						FolderSettingsEnum.FOLDER_TEXTS);
			}
		}
	}

	@Override
	public void addTextToCurrentCorpus(FolderSettingsEnum folderType) {
		logger.debug("CALL addTextToCurrentCorpus");
		UserSettings.getInstance().addTextToCurrentCorpus(folderType);
	}

	@Override
	public void cleanCurrentEditingCorpusForAddText() {
		logger.debug("CALL cleanCurrentEditingCorpusForAddText");
		UserSettings.getInstance().cleanCurrentEditingCorpusForAddText();
	}

	@Override
	public List<String> getConfigurationNameList() {
		logger.debug("CALL getConfigurationNameList");
		return UserSettings.getInstance().getConfigurationList().stream().map(c -> c.getName())
				.collect(Collectors.toList());
	}

	@Override
	public void setCurrentConfiguration(String name) {
		logger.debug(String.format("CALL setCurrentConfiguration : Nom de la configuration %s", name));
		Optional<Configuration> findFirstConfiguration = UserSettings.getInstance().getConfigurationList().stream()
				.filter(c -> name.equals(c.getName())).findFirst();
		if (findFirstConfiguration.isPresent()) {
			this.dispatcher.setCurrentConfigurationWithSaveUserConfiguration(findFirstConfiguration.get());
		}
	}

	@Override
	public Optional<File> getConfigurationFolder() {
		logger.debug("CALL getConfigurationFolder");
		return UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS);
	}

	@Override
	public Boolean haveErrorInSpecificFieldInEditingCorpus() {
		logger.debug("CALL haveErrorInSpecificFieldInEditingCorpus");
		return UserSettings.getInstance().haveErrorInSpecificFieldInEditingCorpus();
	}

	@Override
	public FilesToAnalyzeInformation getNameFileToAnalyzeList(File pathFolderToAnalyze, Integer depth)
			throws IOException {
		logger.debug("CALL pathFolderToAnalyze");
		return this.dispatcher.getNameFileToAnalyzeList(pathFolderToAnalyze, depth);
	}

	@Override
	public Map<String, String> getAllField() {
		logger.debug("CALL getAllField");
		return UserSettings.getInstance().getAllListField();
	}

	@Override
	public String getBaseCode() {
		logger.debug("CALL getBaseCode");
		return UserSettings.getInstance().getCurrentConfiguration().getBaseCode();
	}

	@Override
	public void removeCurrentStateFile() {
		logger.debug("CALL removeCurrentStateFile");
		this.dispatcher.removeCurrentStateFile();
	}

	@Override
	public void export(ExportTypeEnum typeExport, String directory, String nameFile) throws IOException {
		logger.debug(String.format("CALL export : TypeExport %s, Directory %s, nameFile %s", typeExport, directory,
				StringUtils.defaultString(nameFile)));
		switch (typeExport) {
		case ALL_DOCUMENTS:
			this.dispatcher.exportAllDocuments(new File(directory));
			break;
		case DOCUMENT:
			this.dispatcher.exportDocument(new File(directory), nameFile);
			break;
		case SEARCH_RESULTS:
			this.dispatcher.exportResultOfSearch(new File(directory), nameFile);
			break;
		}

	}

	@Override
	public Boolean haveInconsistencyError() {
		logger.debug("CALL haveInconsistencyError");
		return UserSettings.getInstance().haveInconsistencyErrors();
	}

	@Override
	public List<InconsistencyChangeText> getInconsistencyChangeTextErrorList() {
		logger.debug("CALL getAllInconsistencyChangeTextErrorList");
		return UserSettings.getInstance().getInconsistencyErrorList();
	}

	@Override
	public Boolean haveMissingBaseCodeError() {
		logger.debug("CALL haveMissingBaseCodeError");
		return UserSettings.getInstance().haveMissingBaseCodeError();
	}

	@Override
	public List<MissingBaseCode> getMissingBaseCodeErrorList() {
		logger.debug("CALL getMissingBaseCodeErrorList");
		return UserSettings.getInstance().getMissingBaseCodeErrorList();
	}

	@Override
	public String getDelimiterSpecific(Integer index) {
		logger.debug("CALL getDelimiterSpecific");
		return UserSettings.getInstance().getDelimiterSpecific(index);
	}

	@Override
	public Integer getProgress() {
		return dispatcher.getProgress();
	}

	@Override
	public void resetProgress() {
		this.dispatcher.resetProgress();
	}

	@Override
	public Set<InformationException> importExcel(ExcelImportConfigurationCmd excelImportConfigurationCmd) throws ImportExcelException, IOException, LoadTextException {
		logger.debug(String.format("CALL importExcel => file : %s", excelImportConfigurationCmd.getFileToImport().getAbsolutePath()));
		Set<InformationException> informationExceptionSet = new HashSet<>();
		if (Objects.isNull(excelImportConfigurationCmd.getConfiguration())) {
			excelImportConfigurationCmd.setConfiguration(UserSettings.getInstance().getCurrentConfiguration());
		}
		if (!UserSettings.getInstance().getConfigurationList().contains(excelImportConfigurationCmd.getConfiguration())) {
			informationExceptionSet.add(new InformationExceptionBuilder()
					.errorCode(ErrorCode.ERROR_CONFIGURATION)
					.objectInError(excelImportConfigurationCmd)
					.build());
		}
		informationExceptionSet.addAll(excelImportConfigurationCmd.validate());
		if (informationExceptionSet.isEmpty()) {
			informationExceptionSet.addAll(this.dispatcher.importExcel(excelImportConfigurationCmd));
		}
		return informationExceptionSet;
	}

	@Override
	public LexicometricAnalysis getLexicometricAnalysis() {
		logger.debug("CALL getLexicometricAnalysis");
		return UserLexicometricAnalysisSettings.getInstance().getLexicometricAnalysis();
	}

	@Override
	public String getLexicometricDefaultProfile() {
		logger.debug("CALL getLexicometricDefaultProfile");
		return UserLexicometricAnalysisSettings.getInstance().getUserProfile();
	}

	@Override
	public void saveTokenization(EditTable editTable) {

	}

	@Override
	public void saveLemmatization(EditTable editTable) {
		logger.debug("CALL saveLemmatization");
		logger.debug(editTable.toString());
		Optional<Lemmatization> lemmatizationOptional = getLexicometricAnalysis().getLemmatizationSet().stream().filter(lemmatization -> lemmatization.getProfile().equals(editTable.getProfil())).findFirst();
		if (lemmatizationOptional.isEmpty()) {
			return;
		}
		Map<String, Set<String>> baseListWordsMap = lemmatizationOptional.get().getBaseListWordsMap();
		EditTableElement editTableElement = editTable.getEditTableElement();
		Map<String, Set<String>> result = (Map<String, Set<String>>) editTableElement.getActionEditTableEnum().getApplyFunction().apply(lexicometricEditTableService, editTableElement, baseListWordsMap);
		Set<String> keyForValueNotInitializedSet = result.entrySet().stream().filter(entry -> Objects.isNull(entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toSet());
		keyForValueNotInitializedSet.forEach(key -> result.put(key, new HashSet<>()));
		lemmatizationOptional.get().setBaseListWordsMap(result);
	}
}
