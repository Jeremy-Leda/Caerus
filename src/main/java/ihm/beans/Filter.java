package ihm.beans;

import javax.swing.ImageIcon;

import utils.RessourcesUtils;

/**
 * 
 * Bean permettant de gérer un filtre
 * 
 * @author jerem
 *
 */
public class Filter {

	private final PictureTypeEnum typeImage;
	private String field;
	private String label;
	private String value;
	private FilterTypeEnum type;
	
	/**
	 * Constructeur pour créer un filtre
	 * @param field Champ à filtrer
	 * @param label label pour le champ
	 * @param value valeur du champ
	 * @param type type d'opération
	 */
	public Filter(String field, String label, String value, FilterTypeEnum type) {
		super();
		this.typeImage = PictureTypeEnum.FILTER;
		this.field = field;
		this.label = label;
		this.value = value;
		this.type = type;
	}

	/**
	 * Permet de se procurer l'image à afficher
	 * @return l'image à afficher
	 */
	public ImageIcon getImageIcon() {
		return new ImageIcon(RessourcesUtils.getInstance().getImage(this.typeImage));
	}

	/**
	 * Permet de se procurer le champ
	 * @return le champ
	 */
	public String getField() {
		return field;
	}

	/**
	 * Permet de se procurer le libellé
	 * @return le libellé
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Permet de se procurer la valeur
	 * @return la valeur
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Permet de se procurer le type d'opération
	 * @return le type d'opération
	 */
	public FilterTypeEnum getType() {
		return type;
	}
	
	
	
}
