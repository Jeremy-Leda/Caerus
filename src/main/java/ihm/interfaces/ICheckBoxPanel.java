package ihm.interfaces;

import java.util.Map;

/**
 * Interface permettant de controler le checkbox panel
 * @author jerem
 *
 */
public interface ICheckBoxPanel extends IAccessPanel {

	/**
	 * Permet de d�finir les libell�s statique
	 * @param titlePanel Titre du panel
	 * @param checkBoxIdTextMap Map numero de la checkbox et texte associ�
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> checkBoxIdTextMap);
	
	/**
	 * Permet de d�finir si la case � coch� est actif ou non
	 * @param number num�ro de la case � cocher
	 * @param enabled Actif ou non
	 */
	void setEnabled(Integer number, boolean enabled);
	
	/**
	 * Permet de savoir si la case � cocher est s�lectionn�
	 * @param number num�ro de la case � cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsChecked(Integer number);
	
	/**
	 * Permet de d�finir si la case � coch� est coch� ou non
	 * @param number num�ro de la case � cocher
	 * @param checked coch� ou non
	 */
	void setChecked(Integer number, boolean checked);
	
	/**
	 * Permet de savoir si la case � cocher est activ�
	 * @param number num�ro de la case � cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsEnabled(Integer number);
	
	
}
