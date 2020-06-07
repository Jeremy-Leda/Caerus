package model.exceptions;

/**
 * 
 * Permet de d�terminer lorsqu'une exception de construction de la structure se produit
 * 
 * @author Jeremy
 *
 */
public class StructuringException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5829592015258537708L;

	/**
	 * Permet de d�terminer le message d'erreur � retourner lors d'une erreur structurante
	 * @param message
	 */
	public StructuringException(String message) {
		super(message);
	}
	
}
