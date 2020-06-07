package model.analyze.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Classe permettant de se procurer le détail des erreurs pour une erreur de
 * structure
 * 
 * @author jerem
 *
 */
public class StructuringErrorDetails {

	private final String keyStructure;
	private final List<String> listElements = new ArrayList<String>();	
	
	/**
	 * Constructeur
	 * @param keyStructure Clé de la structure
	 */
	public StructuringErrorDetails(String keyStructure) {
		super();
		this.keyStructure = keyStructure;
	}

	/**
	 * Permet de se procurer la clé de la structure
	 * @return la clé de la structure
	 */
	public String getKeyStructure() {
		return keyStructure;
	}
	
	/**
	 * Permet de se procurer la liste des éléments concernés
	 * @return la liste des éléments
	 */
	public List<String> getListElements() {
		return listElements;
	}
	
}
