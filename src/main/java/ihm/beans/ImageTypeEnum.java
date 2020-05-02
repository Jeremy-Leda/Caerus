package ihm.beans;

/**
 * 
 * Enumeration pour le type des images
 * 
 * @author jerem
 *
 */
public enum ImageTypeEnum {
	INFORMATION("icons/icons_information.png"),
	WARNING("icons/icons_warning.png");

	/**
	 * Constructeur pour l'image
	 * @param fileName
	 */
	private ImageTypeEnum(String fileName) {
		this.fileName = fileName;
	}

	private String fileName;

	/**
	 * Permet de récupérer le nom du fichier
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

}
