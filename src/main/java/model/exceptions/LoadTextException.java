package model.exceptions;

/**
 * 
 * Permet de déterminer lorsqu'une exception de chargement des textes s'est produites
 * 
 * @author Jeremy
 *
 */
public class LoadTextException extends Exception {

	/**
	 * Permet de déterminer le message d'erreur à retourner lors d'une erreur de chargement des textes s'est produites
	 * @param message
	 */
	public LoadTextException(String message) {
		super(message);
	}
	
}
