package view.analysis.beans;

import model.PojoBuilder;
import org.apache.commons.lang3.StringUtils;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 *
 * Objet d'affichage pour les résultats de regroupements
 *
 */
@PojoBuilder
public class AnalysisDetailResultDisplay {

    @NotNull
    private AnalysisResultDisplay analysisResultDisplay;

    @NotEmpty
    private Map<String, String> fieldValueMap;

    @NotNull
    private Integer fileNumber;

    @NotNull
    private Integer materialNumber;

    private final String NUMBER_LABEL = "n°";

    public AnalysisResultDisplay getAnalysisResultDisplay() {
        return analysisResultDisplay;
    }

    public void setAnalysisResultDisplay(AnalysisResultDisplay analysisResultDisplay) {
        this.analysisResultDisplay = analysisResultDisplay;
    }

    public Map<String, String> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<String, String> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    public String getIdentification() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_DOCUMENT_LABEL));
        sb.append(StringUtils.SPACE);
        sb.append(NUMBER_LABEL);
        sb.append(fileNumber);
        sb.append(" - ");
        sb.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_MATERIAL_LABEL));
        sb.append(StringUtils.SPACE);
        sb.append(NUMBER_LABEL);
        sb.append(materialNumber);
        return sb.toString();
    }

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }

    public Integer getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(Integer materialNumber) {
        this.materialNumber = materialNumber;
    }
}
