package model.excel.interfaces;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 *
 * Interface permettant de se procurer les différents styles pouvant être appliqués sur les cellules excel
 *
 */
public interface ICellStyleWorkbook {

    /**
     * Permet d'appliquer un style de tableau
     * @param borderStyle Style de bordure
     * @return le style de tableau
     */
    CellStyle getTableStyle(BorderStyle borderStyle);

    /**
     * Permet d'appliquer un style de tableau spécifique en tête
     * @param borderStyle Style de bordure
     * @return le style de tableau
     */
    CellStyle getHeaderTableStyle(BorderStyle borderStyle);

}
