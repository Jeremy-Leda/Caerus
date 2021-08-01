package model.excel.beans;

import model.PojoBuilder;
import model.excel.beans.abstracts.ExcelCellAbstract;

/**
 *
 * Bean contenant le contenu String d'une cellule à écrire dans un fichier Excel
 *
 */
@PojoBuilder
public class ExcelStringCell extends ExcelCellAbstract<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

}
