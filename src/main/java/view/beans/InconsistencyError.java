package view.beans;

import javax.swing.ImageIcon;

import utils.RessourcesUtils;

/**
 * Bean pour les erreurs d'incohérences dans l'IHM
 * @author jerem
 *
 */
public class InconsistencyError {

	private final PictureTypeEnum typeImage;
	private final String oldFieldName;
	private final String newFieldName;
	private final Integer numLine;
	
	/**
	 * Constructeur 
	 * @param oldFieldName ancien champ
	 * @param newFieldName nouveau champ
	 * @param numLine numéro de la ligne
	 */
	public InconsistencyError(String oldFieldName, String newFieldName, Integer numLine) {
		this.typeImage = PictureTypeEnum.INCONSISTENCY;
		this.oldFieldName = oldFieldName;
		this.newFieldName = newFieldName;
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
	 * Permet de se procurer l'ancien nom du champ
	 * @return l'ancien nom du champ
	 */
	public String getOldFieldName() {
		return oldFieldName;
	}

	/**
	 * Permet de se procurer le nouveau nom du champ
	 * @return le nouveau nom du champ
	 */
	public String getNewFieldName() {
		return newFieldName;
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
		result = prime * result + ((newFieldName == null) ? 0 : newFieldName.hashCode());
		result = prime * result + ((numLine == null) ? 0 : numLine.hashCode());
		result = prime * result + ((oldFieldName == null) ? 0 : oldFieldName.hashCode());
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
		InconsistencyError other = (InconsistencyError) obj;
		if (newFieldName == null) {
			if (other.newFieldName != null)
				return false;
		} else if (!newFieldName.equals(other.newFieldName))
			return false;
		if (numLine == null) {
			if (other.numLine != null)
				return false;
		} else if (!numLine.equals(other.numLine))
			return false;
		if (oldFieldName == null) {
			if (other.oldFieldName != null)
				return false;
		} else if (!oldFieldName.equals(other.oldFieldName))
			return false;
		return true;
	}
	
}
