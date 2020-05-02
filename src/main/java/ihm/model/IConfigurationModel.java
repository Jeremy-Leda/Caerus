package ihm.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.Configuration;
import analyze.beans.LineError;
import analyze.beans.StructuredFile;
import analyze.beans.StructuringError;
import excel.beans.ExcelGenerateConfigurationCmd;

public interface IConfigurationModel {

	void analyzePath(String path, Configuration configuration);
	
	List<StructuredFile> getListOfStructuredFile();
	
	Boolean isLoaded();
	
	void createExcel(File path);
	
	Boolean isExcelCreated();
	
	Boolean errorProcessing() throws IOException;
	
	void clear();
	
	List<StructuringError> getStructuringErrorList();
	
	File getTextsFolder();
	
	File getAnalyzeFolder();
	
	String getConfigurationName();
	
	void setTextsFolder(File textsFolder);
	
	void setAnalyzeFolder(File analyzeFolder);
	
	Map<String, String> getConfigurationFieldMetaFile();
	
	Map<String, String> getConfigurationFieldCommonFile();
	
	String getEditingCorpusName();
	
	void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap);
	
	Boolean haveEditingCorpus();
	
	void clearEditingCorpus();
	
	Map<String, String> getListFieldSpecific(Integer index);
	
	Map<String, String> getListFieldHeaderSpecific(Integer index);
	
	Integer getNbSpecificConfiguration();
	
	void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap);
	
	Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index);
	
	String getFieldInEditingCorpus(String key);
	
	void updateFieldInEditingCorpus(String key, String value);
	
	void writeCorpus() throws IOException;
	
	void addEditingTextToCurrentCorpus();
	
	Integer getNbLinesError();
	
	LineError getErrorLine(Integer index);
	
	void updateLineError(Integer index, LineError lineError);
	
	void saveFileAfteFixedErrorLine() throws IOException;
	
	Integer getNbTextsError();
	
	void loadNextErrorText();
	
	void saveCurrentStateOfFixedText();
	
	void applyFixedErrorText();
	
	void writeFixedText() throws IOException;
	
	Boolean haveCurrentStateFile();
	
	void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException;
	
	Boolean haveTextsInErrorRemaining();
	
	Integer getNbBlankLinesError();
	
	void loadNextErrorBlankLine();
	
	Integer getNbTextLoaded();
	
	Boolean haveBlankLinesInErrorRemaining();
	
	Map<String, String> getConfigurationSpecificLabelNameFileMap();
	
	void generateExcel(ExcelGenerateConfigurationCmd cmd) throws IOException;
	
	Map<String, String> getFieldConfigurationNameLabelMap();
	
	List<String> getFieldListToProcess(String labelSpecificConfiguration);
	
	List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration);
	
	Boolean haveMetaBlankLineError();
	
	void loadNextErrorMetaBlankLine();
}
