package view.analysis.beans.interfaces;

import model.excel.beans.ExcelSheet;

/**
 * Interface permettant de convertir des beans en feuille excel
 */
public interface IExcelSheet {

    /**
     * Permet de se procurer la représentation en feuille Excel
     * @return la représentation en feuille Excel
     */
    ExcelSheet getExcelSheet();

}
