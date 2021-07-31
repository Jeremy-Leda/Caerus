package model.excel.beans;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Bean représentant une ligne à afficher dans un fichier Excel
 *
 */
public class ExcelLine {

    private final List<ExcelCell> excelCellLinkedList = new LinkedList<>();

    public ExcelLine() {
    }

    public ExcelLine(ExcelCell... cells) {
        excelCellLinkedList.addAll(Arrays.asList(cells));
    }

    public List<ExcelCell> getExcelCellLinkedList() {
        return excelCellLinkedList;
    }
}
