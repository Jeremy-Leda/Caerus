package view.beans;

import javax.swing.ImageIcon;

import utils.RessourcesUtils;

/**
 * Bean pour les erreurs de code de base dans l'IHM
 * @author jerem
 *
 */
public class BaseCodeError {

	private final PictureTypeEnum typeImage;
	private final String fieldName;
	private final Integer numLine;
	private final String nameFile;
	
	/**
	 * Constructeur 
	 * @param fieldName Champ en erreur
	 * @param numLine num�ro de la ligne
	 * @param nameFile Nom du fichier
	 */
	public BaseCodeError(String fieldName, Integer numLine, String nameFile) {
		this.typeImage = PictureTypeEnum.WARNING;
		this.fieldName = fieldName;
		this.numLine = numLine;
		this.nameFile = nameFile;
	}

	/**
	 * Permet de se procurer l'image � afficher
	 * @return l'image � afficher
	 */
	public ImageIcon getImageIcon() {
		return new ImageIcon(RessourcesUtils.getInstance().getImage(this.typeImage));
	}

	/**
	 * Permet de se procurer le nom du champ
	 * @return le nom du champ
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Permet de se procurer le num�ro de la ligne
	 * @return le num�ro de la ligne
	 */
	public Integer getNumLine() {
		return numLine;
	}

	/**
	 * Permet de se procurer le nom du fichier
	 * @return le nom du fichier
	 */
	public String getNameFile() {
		return nameFile;
	}
	
	
}
