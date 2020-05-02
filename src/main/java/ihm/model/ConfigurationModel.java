package ihm.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
	public void analyzePath(String path, Configuration configuration) {
		try {
			dispatcher = new Dispatcher(path, configuration);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public List<StructuredFile> getListOfStructuredFile() {
		if (null != dispatcher) {
			return this.dispatcher.getStructuredFiles();
		}
		return null;
	}

	@Override
	public Boolean isLoaded() {
		return null != dispatcher;
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
		return null != this.dispatcher && this.dispatcher.getExcelCreated();
	}

	@Override
	public Boolean errorProcessing() throws IOException {
		return null != this.dispatcher && this.dispatcher.errorProcessing();
	}

	@Override
	public void clear() {
		this.dispatcher.getStructuredFiles().clear();
		this.dispatcher.getStructuringErrorList().clear();
	}

	@Override
	public List<StructuringError> getStructuringErrorList() {
		if (null != dispatcher) {
			return this.dispatcher.getStructuringErrorList();
		}
		return null;
	}
	
	@Override
	public File getTextsFolder() {
		if (null != dispatcher) {
			return this.dispatcher.getTextsFolder();
		}
		return null;
	}
	
	@Override
	public File getAnalyzeFolder() {
		if (null != dispatcher) {
			return this.dispatcher.getAnalyzeFolder();
		}
		return null;
	}
	
	@Override
	public String getConfigurationName() {
		if (null != dispatcher) {
			return this.dispatcher.getConfigurationName();
		}
		return null;
	}

	@Override
	public void setTextsFolder(File textsFolder) {
		if (null != dispatcher) {
			this.dispatcher.setTextsFolder(textsFolder);
		}
	}
	

	@Override
	public void setAnalyzeFolder(File folderAnalyze) {
		if (null != dispatcher) {
			this.dispatcher.setAnalyzeFolder(folderAnalyze);
		}
	}

	@Override
	public Map<String, String> getConfigurationFieldMetaFile() {
		if (null != dispatcher) {
			return this.dispatcher.getListFieldMetaFile();
		}
		return new HashMap<String, String>();
	}

	@Override
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		if (null != dispatcher) {
			this.dispatcher.createNewCorpus(nameFile, metaFileFieldMap);
		}
	}

	@Override
	public Boolean haveEditingCorpus() {
		if (null != dispatcher) {
			return this.dispatcher.haveEditingCorpus();
		}
		return false;
	}

	@Override
	public void clearEditingCorpus() {
		if (null != dispatcher) {
			this.dispatcher.clearEditingCorpus();
		}
	}

	@Override
	public Map<String, String> getConfigurationFieldCommonFile() {
		if (null != dispatcher) {
			return this.dispatcher.getListFieldCommonFile();
		}
		return new HashMap<String, String>();
	}

	@Override
	public String getEditingCorpusName() {
		if (null != dispatcher) {
			return this.dispatcher.getEditingCorpusName();
		}
		return StringUtils.EMPTY;
	}

	@Override
	public Map<String, String> getListFieldSpecific(Integer index) {
		if (null != dispatcher) {
			return this.dispatcher.getListFieldSpecific(index);
		}
		return new HashMap<String, String>();
	}
	
	@Override
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		if (null != dispatcher) {
			return this.dispatcher.getListFieldHeaderSpecific(index);
		}
		return new HashMap<String, String>();
	}

	@Override
	public Integer getNbSpecificConfiguration() {
		if (null != dispatcher) {
			return this.dispatcher.getNbSpecificConfiguration();
		}
		return 0;
	}

	@Override
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		if (null != dispatcher) {
			this.dispatcher.updateSpecificFieldInEditingCorpus(index, specificFieldMap);
		}
	}

	@Override
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		if (null != dispatcher) {
			return this.dispatcher.getSpecificFieldInEditingCorpus(index);
		}
		return new HashMap<>();
	}

	@Override
	public String getFieldInEditingCorpus(String key) {
		if (null != dispatcher) {
			return this.dispatcher.getFieldInEditingCorpus(key);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public void updateFieldInEditingCorpus(String key, String value) {
		if (null != dispatcher) {
			this.dispatcher.updateFieldInEditingCorpus(key, value);
		}
	}

	@Override
	public void writeCorpus() throws IOException {
		if (null != dispatcher) {
			this.dispatcher.writeCorpus();
		}
	}

	@Override
	public void addEditingTextToCurrentCorpus() {
		if (null != dispatcher) {
			this.dispatcher.addEditingTextToCurrentCorpus();
		}
	}

	@Override
	public Integer getNbLinesError() {
		if (null != dispatcher) {
			return this.dispatcher.getNbLinesError();
		}
		return 0;
	}

	@Override
	public LineError getErrorLine(Integer index) {
		if (null != dispatcher) {
			return this.dispatcher.getErrorLine(index);
		}
		return null;
	}

	@Override
	public void updateLineError(Integer index, LineError lineError) {
		if (null != dispatcher) {
			this.dispatcher.updateLineError(index, lineError);
		}
	}

	@Override
	public void saveFileAfteFixedErrorLine() throws IOException {
		if (null != dispatcher) {
			this.dispatcher.saveFileAfteFixedErrorLine();
		}
	}

	@Override
	public Integer getNbTextsError() {
		if (null != dispatcher) {
			return this.dispatcher.getNbTextsError();
		}
		return 0;
	}

	@Override
	public void loadNextErrorText() {
		if (null != dispatcher) {
			this.dispatcher.loadNextErrorText();
		}
	}

	@Override
	public void saveCurrentStateOfFixedText() {
		if (null != dispatcher) {
			this.dispatcher.saveCurrentStateOfFixedText();
		}
	}

	@Override
	public void writeFixedText() throws IOException {
		if (null != dispatcher) {
			this.dispatcher.writeFixedText();
		}
	}

	@Override
	public void applyFixedErrorText() {
		if (null != dispatcher) {
			this.dispatcher.applyFixedErrorText();
		}
	}

	@Override
	public Boolean haveCurrentStateFile() {
		if (null != dispatcher) {
			return this.dispatcher.haveCurrentStateFile();
		}
		return Boolean.FALSE;
	}

	@Override
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
		if (null != dispatcher) {
			this.dispatcher.restoreCurrentState();
		}
	}

	@Override
	public Boolean haveTextsInErrorRemaining() {
		if (null != dispatcher) {
			return this.dispatcher.haveTextsInErrorRemaining();
		}
		return Boolean.FALSE;
	}

	@Override
	public Integer getNbBlankLinesError() {
		if (null != dispatcher) {
			return this.dispatcher.getNbBlankLinesError();
		}
		return 0;
	}

	@Override
	public void loadNextErrorBlankLine() {
		if (null != dispatcher) {
			this.dispatcher.loadNextErrorBlankLine();
		}
	}

	@Override
	public Integer getNbTextLoaded() {
		if (null != dispatcher) {
			return this.dispatcher.getNbTextLoaded();
		}
		return 0;
	}

	@Override
	public Boolean haveBlankLinesInErrorRemaining() {
		if (null != dispatcher) {
			return this.dispatcher.haveBlankLinesInErrorRemaining();
		}
		return Boolean.FALSE;
	}

	@Override
	public Map<String, String> getConfigurationSpecificLabelNameFileMap() {
		if (null != dispatcher) {
			return this.dispatcher.getConfigurationSpecificLabelNameFileMap();
		}
		return new HashMap<>();
	}

	@Override
	public void generateExcel(ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (null != dispatcher && null != cmd) {
			this.dispatcher.generateExcel(cmd);
		}
	}

	@Override
	public Map<String, String> getFieldConfigurationNameLabelMap() {
		if (null != dispatcher) {
			return this.dispatcher.getFieldConfigurationNameLabelMap();
		}
		return new HashMap<>();
	}

	@Override
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		if (null != dispatcher) {
			return this.dispatcher.getFieldListToProcess(labelSpecificConfiguration);
		}
		return new ArrayList<String>();
	}

	@Override
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		if (null != dispatcher) {
			return this.dispatcher.getFieldListForbiddenToDisplay(labelSpecificConfiguration);
		}
		return new ArrayList<String>();
	}

	@Override
	public Boolean haveMetaBlankLineError() {
		if (null != dispatcher) {
			return this.dispatcher.haveMetaBlankLineError();
		}
		return Boolean.FALSE;
	}

	@Override
	public void loadNextErrorMetaBlankLine() {
		if (null != dispatcher) {
			this.dispatcher.loadNextErrorMetaBlankLine();
		}
	}


}
