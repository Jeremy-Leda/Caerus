package ihm.controler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.Configuration;
import analyze.beans.LineError;
import analyze.beans.StructuredFile;
import excel.beans.ExcelGenerateConfigurationCmd;
import exceptions.LoadTextException;
import exceptions.MoveFileException;
import ihm.beans.ErrorStructuredLine;
import ihm.model.ConfigurationModel;
import ihm.model.IConfigurationModel;

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
			this.configurationModel.generateExcelFromTexts(cmd);
		}
	}

}
