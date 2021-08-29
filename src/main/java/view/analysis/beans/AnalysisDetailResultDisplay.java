package view.analysis.beans;

import model.PojoBuilder;
import model.excel.beans.*;
import org.apache.commons.lang3.StringUtils;
import view.analysis.beans.interfaces.IExcelSheet;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Objet d'affichage pour les résultats de regroupements
 *
 */
@PojoBuilder
public class AnalysisDetailResultDisplay implements IExcelSheet {

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

    @Override
    public ExcelSheet getExcelSheet() {
        // Creation du premier bloc
        List<ExcelLine> excelLines = fieldValueMap.entrySet().stream().map(entry -> {
            ExcelCell header = new ExcelStringCellBuilder()
                    .value(entry.getKey())
                    .header(true)
                    .build();
            ExcelCell value = new ExcelStringCellBuilder()
                    .value(entry.getValue())
                    .header(false)
                    .build();
            return new ExcelLine(header, value);
        }).collect(Collectors.toList());
        List<ExcelBlock> excelBlockList = new ArrayList<>();
        excelBlockList.add(new ExcelBlock(excelLines.toArray(ExcelLine[]::new)));
        excelBlockList.addAll(analysisResultDisplay.toExcelBlockList());
        // Creation de la feuille
        return new ExcelSheetBuilder()
                .name(getIdentification())
                .excelBlockList(excelBlockList)
                .nbColumnMax(2)
                .build();
    }
}
