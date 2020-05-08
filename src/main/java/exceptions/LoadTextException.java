package exceptions;

/**
 * 
 * Permet de d�terminer lorsqu'une exception de chargement des textes s'est prouites
 * 
 * @author Jeremy
 *
 */
public class LoadTextException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5829592015258537708L;

	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur de chargement des textes s'est prouites
	 * @param message
	 */
	public LoadTextException(String message) {
		super(message);
	}
	
}
