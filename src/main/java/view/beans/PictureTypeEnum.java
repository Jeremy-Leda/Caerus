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
	LOGO_16_16("icons/icons_logo_16_16.png"),
	LOGO_32_32("icons/icons_logo_32_32.png"),
	LOGO_48_48("icons/icons_logo_48_48.png"),
	LOGO_64_64("icons/icons_logo_64_64.png"),
	LOGO_96_96("icons/icons_logo_96_96.png"),
	LOGO_128_128("icons/icons_logo_128_128.png"),
	LOGO_256_256("icons/icons_logo_256_256.png"),
	WARNING("icons/icons_warning.png"),
	INCONSISTENCY("icons/icons_inconsistency.png");

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
