package model.interfaces;

import model.excel.beans.ExcelSheet;

import java.io.IOException;
import java.util.List;

public interface ICreateExcel extends IProgressModel {

    /**
     *
     * Permet de créer une ligne dans le fichier Excel
     *
     * @param listCells liste des cellules
     */
    void createRow(List<String> listCells);

    /**
     * Permet de générer l'excel
     * @param progressBean progressbean pour suivre la progression
     * @throws IOException
     */
    void generateExcel(IProgressBean progressBean) throws IOException;

    /**
     * Permet de générer l'excel
     * @param excelSheetList Liste des feuilles excel à générer
     * @param withFormat Avec format (tableau et couleur)
     * @throws IOException
     */
    void generateExcel(List<ExcelSheet> excelSheetList, Boolean withFormat);

}
