package model.exceptions;

/**
 * 
 * Exception pour les réglages utilisateur
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
	 * Permet de déterminer le message d'erreur à retourner lors d'une erreur de réglages
	 * @param message message à retourner à l'utilisateur
	 */
	public SettingsException(String message) {
		super(message);
	}
	
	/**
	 * Permet de déterminer le message d'erreur à retourner lors d'une erreur de réglages
	 * @param message message à retourner à l'utilisateur
	 * @param ex exception
	 */
	public SettingsException(String message, Exception ex) {
		super(message, ex);
	}

}
