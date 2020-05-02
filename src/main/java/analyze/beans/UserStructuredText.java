package analyze.beans;

import utils.KeyGenerator;

/**
 * 
 * Bean permettant de stocker les informations trait� par l'analyse et consultable par l'utilisateur
 * 
 * @author jerem
 *
 */
public class UserStructuredText {

	private final String fileName;
	private final Integer number;
	private final StructuredText structuredText;
	private final String key;
	
	/**
	 * Utilis� par le Json
	 */
	@SuppressWarnings("unused")
	private UserStructuredText() {
		this.fileName = null;
		this.number = null;
		this.structuredText = null;
		this.key = null;
	}
	
	/**
	 * Constructeur
	 * @param fileName Nom du fichier
	 * @param number num�ro 
	 * @param structuredText text structur�
	 */
	public UserStructuredText(String fileName, Integer number, StructuredText structuredText) {
		this.fileName = fileName;
		this.number = number;
		this.structuredText = structuredText;
		this.key = KeyGenerator.generateKey(structuredText);
	}
	
	/**
	 * Permet de se procurer le nom du fichier	
	 * @return le nom du fichier
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Permet de se procurer le num�ro du texte
	 * @return le num�ro du texte
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Permet de se procurer le texte structur�
	 * @return le texte structur�
	 */
	public StructuredText getStructuredText() {
		return structuredText;
	}

	/**
	 * Permet de se procurer la cl� g�n�r�
	 * @return la cl� g�n�r�
	 */
	public String getKey() {
		return key;
	}
	
	
	
}
