package model.excel.beans;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Bean contenant une liste de ligne, représentant un bloc à afficher dans une feuille excel
 *
 */
public class ExcelBlock {

    private final List<ExcelLine> excelLineLinkedList = new LinkedList<>();

    public ExcelBlock() {
    }

    public ExcelBlock(ExcelLine... lines) {
        excelLineLinkedList.addAll(Arrays.asList(lines));
    }

    public List<ExcelLine> getExcelLineLinkedList() {
        return excelLineLinkedList;
    }
}
