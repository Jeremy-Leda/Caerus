package analyze.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Classe permettant de contenir les erreurs de structures
 * 
 * @author jerem
 *
 */
public class StructuringError {

	private final String keyFile;
	private final String keyText;
	private final List<StructuringErrorDetails> lstDetails = new ArrayList<StructuringErrorDetails>();
	
	/**
	 * Constructeur
	 * @param keyFile Cl� du fichier
	 * @param keyTexte Cl� du texte
	 */
	public StructuringError(String keyFile, String keyText) {
		super();
		this.keyFile = keyFile;
		this.keyText = keyText;
	}

	/**
	 * Permet de se procurer la cl� du fichier
	 * @return la cl� du fichier
	 */
	public String getKeyFile() {
		return keyFile;
	}

	/**
	 * Permet de se procurer la cl� du texte
	 * @return cl� du texte
	 */
	public String getKeyText() {
		return keyText;
	}

	/**
	 * Permet de se procurer les d�tails des erreurs
	 * @return la liste des d�tails
	 */
	public List<StructuringErrorDetails> getDetails() {
		return lstDetails;
	}
	
	
	
}
