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
	 * @param keyFile Clé du fichier
	 * @param keyTexte Clé du texte
	 */
	public StructuringError(String keyFile, String keyText) {
		super();
		this.keyFile = keyFile;
		this.keyText = keyText;
	}

	/**
	 * Permet de se procurer la clé du fichier
	 * @return la clé du fichier
	 */
	public String getKeyFile() {
		return keyFile;
	}

	/**
	 * Permet de se procurer la clé du texte
	 * @return clé du texte
	 */
	public String getKeyText() {
		return keyText;
	}

	/**
	 * Permet de se procurer les détails des erreurs
	 * @return la liste des détails
	 */
	public List<StructuringErrorDetails> getDetails() {
		return lstDetails;
	}
	
	
	
}
