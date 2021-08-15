package model.analyze.beans;

import org.apache.commons.lang3.StringUtils;
import utils.KeyGenerator;

/**
 * 
 * Bean permettant de stocker les informations traité par l'analyse et consultable par l'utilisateur
 * 
 * @author jerem
 *
 */
public class UserStructuredText {

	private final String fileName;
	private final Integer number;
	private final StructuredText structuredText;
	private final String key;
	private final Integer documentNumber;
	
	/**
	 * Utilisé par le Json
	 */
	@SuppressWarnings("unused")
	private UserStructuredText() {
		this.fileName = null;
		this.number = null;
		this.structuredText = null;
		this.key = null;
		this.documentNumber = null;
	}
	
	/**
	 * Constructeur
	 * @param fileName Nom du fichier
	 * @param number numéro 
	 * @param structuredText text structuré
	 */
	public UserStructuredText(String fileName, Integer number, Integer documentNumber, StructuredText structuredText) {
		this.fileName = fileName;
		this.number = number;
		this.documentNumber = documentNumber;
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
	 * Permet de se procurer le nom du fichier sans extension
	 * @return le nom du fichier sans extension
	 */
	public String getFileNameWithoutExtension() {
		return fileName.replaceAll(".txt", StringUtils.EMPTY);
	}
	
	/**
	 * Permet de se procurer le numéro du texte
	 * @return le numéro du texte
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Permet de se procurer le texte structuré
	 * @return le texte structuré
	 */
	public StructuredText getStructuredText() {
		return structuredText;
	}

	/**
	 * Permet de se procurer la clé généré
	 * @return la clé généré
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Permet de se procurer le numéro du document
	 * @return le nuémro du document
	 */
	public Integer getDocumentNumber() {
		return documentNumber;
	}
}
