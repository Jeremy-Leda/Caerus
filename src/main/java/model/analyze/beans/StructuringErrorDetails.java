package model.analyze.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Classe permettant de se procurer le d�tail des erreurs pour une erreur de
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
	 * @param keyStructure Cl� de la structure
	 */
	public StructuringErrorDetails(String keyStructure) {
		super();
		this.keyStructure = keyStructure;
	}

	/**
	 * Permet de se procurer la cl� de la structure
	 * @return la cl� de la structure
	 */
	public String getKeyStructure() {
		return keyStructure;
	}
	
	/**
	 * Permet de se procurer la liste des �l�ments concern�s
	 * @return la liste des �l�ments
	 */
	public List<String> getListElements() {
		return listElements;
	}
	
}
