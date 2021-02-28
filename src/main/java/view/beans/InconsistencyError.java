package view.beans;

import javax.swing.ImageIcon;

import utils.RessourcesUtils;

/**
 * Bean pour les erreurs d'incoh�rences dans l'IHM
 * @author jerem
 *
 */
public class InconsistencyError {

	private final PictureTypeEnum typeImage;
	private final String oldFieldName;
	private final String newFieldName;
	private final Integer oldNumLine;
	private final Integer newNumLine;
	private final Boolean oldFieldIsMetaFile;
	private final String nameFile;
	
	/**
	 * Constructeur 
	 * @param oldFieldName ancien champ
	 * @param newFieldName nouveau champ
	 * @param oldNumLine num�ro de la ligne de l'ancien champ
	 * @param newNumLine num�ro de la ligne du nouveau champ
	 * @param oldFieldIsMetaFile Permet de d�terminer si le champ est de type m�ta
	 * @param nameFile Nom du fichier
	 */
	public InconsistencyError(String oldFieldName, String newFieldName, Integer oldNumLine, Integer newNumLine, Boolean oldFieldIsMetaFile, String nameFile) {
		this.typeImage = PictureTypeEnum.WARNING;
		this.oldFieldName = oldFieldName;
		this.newFieldName = newFieldName;
		this.oldNumLine = oldNumLine;
		this.newNumLine = newNumLine;
		this.oldFieldIsMetaFile = oldFieldIsMetaFile;
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
	 * Permet de se procurer le num�ro de la ligne de l'ancienne balise
	 * @return le num�ro de la ligne de l'ancienne balise
	 */
	public Integer getOldNumLine() {
		return oldNumLine;
	}
	
	/**
	 * Permet de se procurer le num�ro de la ligne de la nouvelle balise
	 * @return le num�ro de la ligne de la nouvelle balise
	 */
	public Integer getNewNumLine() {
		return newNumLine;
	}
	
	/**
	 * Permet de d�terminer si l'ancienne balise est une balise de type m�ta
	 * @return Vrai si c'est le cas
	 */
	public Boolean getOldFieldIsMetaFile() {
		return oldFieldIsMetaFile;
	}
	
	/**
	 * Permet de se procurer le nom du fichier
	 * @return le nom du fichier
	 */
	public String getNameFile() {
		return nameFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameFile == null) ? 0 : nameFile.hashCode());
		result = prime * result + ((newFieldName == null) ? 0 : newFieldName.hashCode());
		result = prime * result + ((newNumLine == null) ? 0 : newNumLine.hashCode());
		result = prime * result + ((oldFieldName == null) ? 0 : oldFieldName.hashCode());
		result = prime * result + ((oldNumLine == null) ? 0 : oldNumLine.hashCode());
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
		if (nameFile == null) {
			if (other.nameFile != null)
				return false;
		} else if (!nameFile.equals(other.nameFile))
			return false;
		if (newFieldName == null) {
			if (other.newFieldName != null)
				return false;
		} else if (!newFieldName.equals(other.newFieldName))
			return false;
		if (newNumLine == null) {
			if (other.newNumLine != null)
				return false;
		} else if (!newNumLine.equals(other.newNumLine))
			return false;
		if (oldFieldName == null) {
			if (other.oldFieldName != null)
				return false;
		} else if (!oldFieldName.equals(other.oldFieldName))
			return false;
		if (oldNumLine == null) {
			if (other.oldNumLine != null)
				return false;
		} else if (!oldNumLine.equals(other.oldNumLine))
			return false;
		return true;
	}
	


	
}
