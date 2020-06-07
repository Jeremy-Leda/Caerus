package view.beans;

/**
 * 
 * Enumeration pour le type des images
 * 
 * @author jerem
 *
 */
public enum PictureTypeEnum {
	INFORMATION("icons/icons_information.png"),
	TEXT("icons/icons_text.png"),
	FILTER("icons/icons_filter.png"),
	SAVE("icons/icons_save.png"),
	LOGO("icons/icons_logo.png"),
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
	 * Permet de récupérer le nom du fichier
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

}
