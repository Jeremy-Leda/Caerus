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
	
	/**
	 * Constructeur 
	 * @param fieldName Champ en erreur
	 * @param numLine numéro de la ligne
	 */
	public BaseCodeError(String fieldName, Integer numLine) {
		this.typeImage = PictureTypeEnum.INCONSISTENCY;
		this.fieldName = fieldName;
		this.numLine = numLine;
	}

	/**
	 * Permet de se procurer l'image à afficher
	 * @return l'image à afficher
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
	 * Permet de se procurer le numéro de la ligne
	 * @return le numéro de la ligne
	 */
	public Integer getNumLine() {
		return numLine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((numLine == null) ? 0 : numLine.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseCodeError other = (BaseCodeError) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (numLine == null) {
			if (other.numLine != null)
				return false;
		} else if (!numLine.equals(other.numLine))
			return false;
		return true;
	}
	
	
	
}
