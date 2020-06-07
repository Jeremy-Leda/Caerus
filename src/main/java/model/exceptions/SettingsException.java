package model.exceptions;

/**
 * 
 * Exception pour les r�glages utilisateur
 * 
 * @author jerem
 *
 */
public class SettingsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6266126022658208882L;
	
	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur de r�glages
	 * @param message message � retourner � l'utilisateur
	 */
	public SettingsException(String message) {
		super(message);
	}
	
	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur de r�glages
	 * @param message message � retourner � l'utilisateur
	 * @param ex exception
	 */
	public SettingsException(String message, Exception ex) {
		super(message, ex);
	}

}
