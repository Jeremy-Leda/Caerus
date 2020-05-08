package ihm.beans;

/**
 * 
 * Enumeration pour le type des images
 * 
 * @author jerem
 *
 */
public enum PictureTypeEnum {
	INFORMATION("icons/icons_information.png"),
	WARNING("icons/icons_warning.png");

	/**
	 * Constructeur pour l'image
	 * @param fileName
	 */
	private PictureTypeEnum(String fileName) {
		this.fileName = fileName;
	}

	private String fileName;

	/**
	 * Permet de r�cup�rer le nom du fichier
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

}