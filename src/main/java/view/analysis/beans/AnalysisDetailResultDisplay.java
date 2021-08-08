package view.analysis.beans;

import model.PojoBuilder;

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
        //FIXME texte
        sb.append("Manual n°");
        sb.append(fileNumber);
        sb.append(" - Material n°");
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
