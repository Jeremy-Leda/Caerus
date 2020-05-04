package ihm.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.Dispatcher;
import analyze.beans.Configuration;
import analyze.beans.LineError;
import analyze.beans.StructuredFile;
import analyze.beans.StructuringError;
import excel.beans.ExcelGenerateConfigurationCmd;

public class ConfigurationModel implements IConfigurationModel {

	private Logger logger = LoggerFactory.getLogger(ConfigurationModel.class);
	private Dispatcher dispatcher = new Dispatcher();

	@Override
	public void launchAnalyze() {
		try {
			this.dispatcher.launchAnalyze();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void setCurrentConfiguration(Configuration configuration) {
		this.dispatcher.setCurrentConfiguration(configuration);
	}

	@Override
	public List<StructuredFile> getListOfStructuredFile() {
			return this.dispatcher.getStructuredFiles();
	}

	@Override
	public void createExcel(File path) {
		try {
			this.dispatcher.createExcel(path);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public Boolean isExcelCreated() {
		return this.dispatcher.getExcelCreated();
	}

	@Override
	public Boolean errorProcessing() throws IOException {
		return this.dispatcher.errorProcessing();
	}

	@Override
	public void clear() {
		this.dispatcher.getStructuredFiles().clear();
		this.dispatcher.getStructuringErrorList().clear();
	}

	@Override
	public List<StructuringError> getStructuringErrorList() {
		return this.dispatcher.getStructuringErrorList();
	}

	@Override
	public File getTextsFolder() {
		return this.dispatcher.getTextsFolder();
	}

	@Override
	public File getAnalyzeFolder() {
		return this.dispatcher.getAnalyzeFolder();
	}

	@Override
	public String getConfigurationName() {
		return this.dispatcher.getConfigurationName();
	}

	@Override
	public void setTextsFolder(File textsFolder) {
		this.dispatcher.setTextsFolder(textsFolder);
	}

	@Override
	public void setAnalyzeFolder(File folderAnalyze) {
		this.dispatcher.setAnalyzeFolder(folderAnalyze);
	}

	@Override
	public Map<String, String> getConfigurationFieldMetaFile() {
		return this.dispatcher.getListFieldMetaFile();
	}

	@Override
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		this.dispatcher.createNewCorpus(nameFile, metaFileFieldMap);
	}

	@Override
	public Boolean haveEditingCorpus() {
		return this.dispatcher.haveEditingCorpus();
	}

	@Override
	public void clearEditingCorpus() {
		this.dispatcher.clearEditingCorpus();
	}

	@Override
	public Map<String, String> getConfigurationFieldCommonFile() {
		return this.dispatcher.getListFieldCommonFile();
	}

	@Override
	public String getEditingCorpusName() {
		return this.dispatcher.getEditingCorpusName();
	}

	@Override
	public Map<String, String> getListFieldSpecific(Integer index) {
		return this.dispatcher.getListFieldSpecific(index);
	}

	@Override
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		return this.dispatcher.getListFieldHeaderSpecific(index);
	}

	@Override
	public Integer getNbSpecificConfiguration() {
		return this.dispatcher.getNbSpecificConfiguration();
	}

	@Override
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		this.dispatcher.updateSpecificFieldInEditingCorpus(index, specificFieldMap);
	}

	@Override
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		return this.dispatcher.getSpecificFieldInEditingCorpus(index);
	}

	@Override
	public String getFieldInEditingCorpus(String key) {
		return this.dispatcher.getFieldInEditingCorpus(key);
	}

	@Override
	public void updateFieldInEditingCorpus(String key, String value) {
		this.dispatcher.updateFieldInEditingCorpus(key, value);
	}

	@Override
	public void writeCorpus() throws IOException {
		this.dispatcher.writeCorpus();
	}

	@Override
	public void addEditingTextToCurrentCorpus() {
		this.dispatcher.addEditingTextToCurrentCorpus();
	}

	@Override
	public Integer getNbLinesError() {
		return this.dispatcher.getNbLinesError();
	}

	@Override
	public LineError getErrorLine(Integer index) {
		return this.dispatcher.getErrorLine(index);
	}

	@Override
	public void updateLineError(Integer index, LineError lineError) {
		this.dispatcher.updateLineError(index, lineError);
	}

	@Override
	public void saveFileAfteFixedErrorLine() throws IOException {
		this.dispatcher.saveFileAfteFixedErrorLine();
	}

	@Override
	public Integer getNbTextsError() {
		return this.dispatcher.getNbTextsError();
	}

	@Override
	public void loadNextErrorText() {
		this.dispatcher.loadNextErrorText();
	}

	@Override
	public void saveCurrentStateOfFixedText() {
		this.dispatcher.saveCurrentStateOfFixedText();
	}

	@Override
	public void writeFixedText() throws IOException {
		this.dispatcher.writeFixedText();
	}

	@Override
	public void applyFixedErrorText() {
		this.dispatcher.applyFixedErrorText();
	}

	@Override
	public Boolean haveCurrentStateFile() {
		return this.dispatcher.haveCurrentStateFile();
	}

	@Override
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
		this.dispatcher.restoreCurrentState();
	}

	@Override
	public Boolean haveTextsInErrorRemaining() {
		return this.dispatcher.haveTextsInErrorRemaining();
	}

	@Override
	public Integer getNbBlankLinesError() {
		return this.dispatcher.getNbBlankLinesError();
	}

	@Override
	public void loadNextErrorBlankLine() {
		this.dispatcher.loadNextErrorBlankLine();
	}

	@Override
	public Integer getNbTextLoaded() {
		return this.dispatcher.getNbTextLoaded();
	}

	@Override
	public Boolean haveBlankLinesInErrorRemaining() {
		return this.dispatcher.haveBlankLinesInErrorRemaining();
	}

	@Override
	public Map<String, String> getConfigurationSpecificLabelNameFileMap() {
		return this.dispatcher.getConfigurationSpecificLabelNameFileMap();
	}

	@Override
	public void generateExcel(ExcelGenerateConfigurationCmd cmd) throws IOException {
		this.dispatcher.generateExcel(cmd);
	}

	@Override
	public Map<String, String> getFieldConfigurationNameLabelMap() {
		return this.dispatcher.getFieldConfigurationNameLabelMap();
	}

	@Override
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		return this.dispatcher.getFieldListToProcess(labelSpecificConfiguration);
	}

	@Override
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		return this.dispatcher.getFieldListForbiddenToDisplay(labelSpecificConfiguration);
	}

	@Override
	public Boolean haveMetaBlankLineError() {
		return this.dispatcher.haveMetaBlankLineError();
	}

	@Override
	public void loadNextErrorMetaBlankLine() {
		this.dispatcher.loadNextErrorMetaBlankLine();
	}

	@Override
	public Boolean haveMetaBlankLineInErrorRemaining() {
		return this.dispatcher.haveMetaBlankLineInErrorRemaining();
	}

	@Override
	public Integer getNbMetaBlankLineToFixed() {
		return this.dispatcher.getNbMetaBlankLineToFixed();
	}

}
