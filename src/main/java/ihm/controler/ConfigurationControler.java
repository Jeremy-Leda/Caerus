package ihm.controler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.Configuration;
import analyze.beans.FilterCorpus;
import analyze.beans.FilterText;
import analyze.beans.LineError;
import analyze.beans.StructuredFile;
import analyze.constants.FolderSettingsEnum;
import analyze.constants.TypeFilterTextEnum;
import excel.beans.ExcelGenerateConfigurationCmd;
import exceptions.LoadTextException;
import exceptions.MoveFileException;
import ihm.beans.DisplayText;
import ihm.beans.ErrorStructuredLine;
import ihm.beans.Filter;
import ihm.beans.FilterTypeEnum;
import ihm.model.ConfigurationModel;
import ihm.model.IConfigurationModel;

/**
 * 
 * Controller pour la jonction au modèle
 * 
 * @author jerem
 *
 */
public class ConfigurationControler implements IConfigurationControler {

	private Logger logger = LoggerFactory.getLogger(ConfigurationControler.class);
	private IConfigurationModel configurationModel = new ConfigurationModel();

	@Override
	public void launchAnalyze() throws LoadTextException {
		if (StringUtils.isNotBlank(this.configurationModel.getConfigurationName())
				&& null != this.configurationModel.getAnalyzeFolder()
				&& this.configurationModel.getAnalyzeFolder().isDirectory()) {
			configurationModel.launchAnalyze();
		} else {
			logger.error("Configuration or path is not compatible");
		}
	}

	@Override
	public void setCurrentConfiguration(Configuration configuration) {
		if (null != configuration) {
			this.configurationModel.setCurrentConfiguration(configuration);
		}
	}

	@Override
	public File getTextsFolder() {
		return this.configurationModel.getTextsFolder();
	}

	@Override
	public File getAnalyzeFolder() {
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
	public String getEditingCorpusName() {
		return this.configurationModel.getEditingCorpusName();
	}

	@Override
	public Map<String, String> getListFieldSpecific(Integer index) {
		if (!haveEditingCorpus()) {
			return new HashMap<String, String>();
		}
		if (index < getNbSpecificConfiguration()) {
			return this.configurationModel.getListFieldSpecific(index);
		}
		return this.configurationModel.getListFieldSpecific(getNbSpecificConfiguration() - 1);
	}

	@Override
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		if (!haveEditingCorpus()) {
			return new HashMap<String, String>();
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
			return new HashMap<String, List<String>>();
		}
		if (null != index && index < getNbSpecificConfiguration()) {
			return this.configurationModel.getSpecificFieldInEditingCorpus(index);
		}
		return this.configurationModel.getSpecificFieldInEditingCorpus(getNbSpecificConfiguration() - 1);
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
	public void saveFileAfteFixedErrorLine() throws IOException {
		if (getNbLinesError() > 0) {
			this.configurationModel.saveFileAfteFixedErrorLine();
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
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
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
		return new ArrayList<String>();
	}

	@Override
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		if (StringUtils.isNotBlank(labelSpecificConfiguration)) {
			return this.configurationModel.getFieldListForbiddenToDisplay(labelSpecificConfiguration);
		}
		return new ArrayList<String>();
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
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();
			this.configurationModel.getTextsLoadedForTextsList().parallelStream()
					.filter(ust -> this.configurationModel.getKeyFilteredList().contains(ust.getKey()))
					.forEach(ust -> cmd.addUniqueKey(ust.getStructuredText().getUniqueKey()));
			this.configurationModel.generateExcelFromTexts(cmd);
			stopWatch.stop();
			logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
		}
	}

	@Override
	public List<DisplayText> getDisplayTextListFromFilteredText(Integer start, Integer nbTextToLoad) {
		Set<DisplayText> setTextList = new LinkedHashSet<>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		// SI configuration existe
		if (StringUtils.isNotBlank(this.configurationModel.getConfigurationName())) {
			Map<String, String> configurationFieldCommonFile = this.configurationModel
					.getConfigurationFieldCommonFile();
			Integer end = start + nbTextToLoad;
			if (end > this.configurationModel.getKeyFilteredList().size()) {
				end = this.configurationModel.getKeyFilteredList().size();
			}
			// PARCOURS La liste des clés filtrés
			for (int i = start; i < end; i++) {
				String key = this.configurationModel.getKeyFilteredList().get(i);
				this.configurationModel.loadKeyFiltered(key);
				Map<String, String> mapKeyValue = new LinkedHashMap<>();
				// PARCOURS La liste des champs commun pour se procurer le label et sa valeur
				configurationFieldCommonFile.entrySet().stream().forEach((entry) -> {
					String value = this.configurationModel.getFieldInEditingCorpus(entry.getKey());
					mapKeyValue.put(entry.getValue(), value);
				});
				setTextList.add(new DisplayText(this.configurationModel.getEditingCorpusName(), i, mapKeyValue, key));
			}
		}
		stopWatch.stop();
		logger.debug(String.format("Time Elapsed: %d ms", stopWatch.getTime(TimeUnit.MILLISECONDS)));
		return Collections.unmodifiableList(new LinkedList<>(setTextList));
	}

	@Override
	public void loadTexts() throws LoadTextException {
		if (StringUtils.isNotBlank(this.configurationModel.getConfigurationName())
				&& null != this.configurationModel.getTextsFolder()
				&& this.configurationModel.getTextsFolder().isDirectory()) {
			configurationModel.loadTexts();
		} else {
			logger.error("Configuration or path is not compatible");
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
	public File getConfigurationFolder() {
		return this.configurationModel.getConfigurationFolder();
	}

}
