package exceptions;

/**
 * 
 * Exception pour les d�placement de fichier
 * 
 * @author jerem
 *
 */
public class MoveFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6266126022658208882L;
	
	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur de d�placement de fichier
	 * @param message message � retourner � l'utilisateur
	 */
	public MoveFileException(String message) {
		super(message);
	}
	
	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur de d�placement de fichier
	 * @param message message � retourner � l'utilisateur
	 * @param ex exception
	 */
	public MoveFileException(String message, Exception ex) {
		super(message, ex);
	}

}
