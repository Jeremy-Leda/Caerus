package ihm.interfaces;

import java.util.Map;

/**
 * Interface permettant de controler le checkbox panel
 * @author jerem
 *
 */
public interface ICheckBoxPanel extends IAccessPanel {

	/**
	 * Permet de définir les libellés statique
	 * @param titlePanel Titre du panel
	 * @param checkBoxIdTextMap Map numero de la checkbox et texte associé
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> checkBoxIdTextMap);
	
	/**
	 * Permet de définir si la case à coché est actif ou non
	 * @param number numéro de la case à cocher
	 * @param enabled Actif ou non
	 */
	void setEnabled(Integer number, boolean enabled);
	
	/**
	 * Permet de savoir si la case à cocher est sélectionné
	 * @param number numéro de la case à cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsChecked(Integer number);
	
	/**
	 * Permet de définir si la case à coché est coché ou non
	 * @param number numéro de la case à cocher
	 * @param checked coché ou non
	 */
	void setChecked(Integer number, boolean checked);
	
	/**
	 * Permet de savoir si la case à cocher est activé
	 * @param number numéro de la case à cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsEnabled(Integer number);
	
	
}
