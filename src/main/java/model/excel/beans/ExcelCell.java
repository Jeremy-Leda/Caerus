package model.excel.beans;

/**
 *
 * Bean contenant le contenu d'une cellule à écrire dans un fichier Excel
 *
 */
public interface ExcelCell<T> {

    Class<T> getType();

    T getValue();

    boolean isHeader();
}
