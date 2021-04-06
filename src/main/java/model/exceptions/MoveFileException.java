package model.exceptions;

/**
 * 
 * Exception pour les déplacement de fichier
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
	 * Permet de déterminer le message d'erreur à retourner lors d'une erreur de déplacement de fichier
	 * @param message message à retourner à l'utilisateur
	 */
	public MoveFileException(String message) {
		super(message);
	}
	
	/**
	 * Permet de déterminer le message d'erreur à retourner lors d'une erreur de déplacement de fichier
	 * @param message message à retourner à l'utilisateur
	 * @param ex exception
	 */
	public MoveFileException(String message, Exception ex) {
		super(message, ex);
	}

}
