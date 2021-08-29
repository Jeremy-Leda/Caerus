package model.excel.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * Bean contenant le contenu d'une feuille excel
 *
 */
@PojoBuilder
public class ExcelSheet {

    @NotBlank(message = "Le nom de la feuille ne peut pas être vide")
    private String name;

    @NotEmpty(message = "Une feuille excel doit au moins disposer d'un bloc de ligne")
    private List<ExcelBlock> excelBlockList;

    @NotNull(message = "Le nombre de colonne maximum ne peut pas être null")
    private Integer nbColumnMax;

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        String excelTitle = name.replaceAll("/[^a-zA-Z ]/g", "")
                .replaceAll("[\\p{Punct}&&[^'-]]+", "")
                .replaceAll("¿", "")
                .replaceAll("^\"|\"$", "")
                .replaceAll("“", "")
                .replaceAll("”", "")
                .replaceAll("»", "")
                .replaceAll("«", "")
                .replaceAll("^\'|\'$", "")
                .replaceAll("‘", "")
                .replaceAll("—", "")
                .replaceAll("¡", "");
        if (excelTitle.length() > 31) {
            return excelTitle.substring(0, 30);
        }
        return excelTitle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExcelBlock> getExcelBlockList() {
        return excelBlockList;
    }

    public void setExcelBlockList(List<ExcelBlock> excelBlockList) {
        this.excelBlockList = excelBlockList;
    }

    public Integer getNbColumnMax() {
        return nbColumnMax;
    }

    public void setNbColumnMax(Integer nbColumnMax) {
        this.nbColumnMax = nbColumnMax;
    }
}
