package model.excel.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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

    public String getName() {
        return name;
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
}
