package model.exceptions;

/**
 *
 * Permet de déterminer lorsqu'une exception de l'import du fichier excel s'est produites
 *
 * @author Jeremy
 *
 */
public class ImportExcelException extends Exception {

    /**
     * Permet de déterminer le message d'erreur à retourner lors d'une erreur de l'import du fichier excel s'est produites
     * @param message
     */
    public ImportExcelException(String message) {
        super(message);
    }
}
