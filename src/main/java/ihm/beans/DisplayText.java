package ihm.beans;

import java.util.Map;

import javax.swing.ImageIcon;

import utils.RessourcesUtils;

/**
 * 
 * Bean permettant l'affichage d'un texte
 * 
 * @author jerem
 *
 */
public class DisplayText {

	private final String corpusName;
	private final PictureTypeEnum typeImage;
	private final Map<String, String> mapKeyValueList;
	private final String structuredKey;
	private final Integer index;
	
	/**
	 * Constructeur d'un texte � afficher
	 * @param corpusName Le nom du corpus
	 * @param index Emplacement du texte (numero dans la liste)
	 * @param mapKeyValueList map des cl�s et valeur
	 * @param structuredKey Cl� structurelle du texte
	 */
	public DisplayText(String corpusName, Integer index, Map<String, String> mapKeyValueList, String structuredKey) {
		super();
		this.corpusName = corpusName;
		this.typeImage = PictureTypeEnum.TEXT;
		this.mapKeyValueList = mapKeyValueList;
		this.structuredKey = structuredKey;
		this.index = index;
	}
	
	/**
	 * Permet de se procurer l'icone pour l'image
	 * @return l'icone pour l'image
	 */
	public ImageIcon getImageIcon() {
		return new ImageIcon(RessourcesUtils.getInstance().getImage(this.typeImage));
	}

	/**
	 * Permet de se procurer la map des cl�s valeurs
	 * @return la map cl� valeur
	 */
	public Map<String, String> getMapKeyValueList() {
		return mapKeyValueList;
	}

	/**
	 * Permet de se procurer la cl� du texte structur�
	 * @return la cl� du texte structur�
	 */
	public String getStructuredKey() {
		return structuredKey;
	}

	/**
	 * Permet de se procurer le nom du corpus
	 * @return le nom du corpus
	 */
	public String getCorpusName() {
		return corpusName;
	}

	/**
	 * Permet de se procurer le num�ro du texte (emplacement dans la liste)
	 * @return Le num�ro du texte
	 */
	public Integer getIndex() {
		return index;
	}
	
	
	
}
