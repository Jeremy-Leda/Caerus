package view.interfaces;

import java.awt.event.ActionListener;
import java.util.Map;

/**
 * 
 * Interface pour la gestion des radio Boutons
 * 
 * @author jerem
 *
 */
public interface IRadioButtonPanel extends IAccessPanel {

	/**
	 * Permet de savoir quel radiobutton a été sélectionné
	 * @return le numéro du bouton radio
	 */
	Integer getSelectedRadioButtonNumber();
	
	/**
	 * Permet de définir les libellés statique
	 * @param titlePanel Titre du panel
	 * @param radioButtonIdTextMap Map numero du radio bouton et texte associé
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> radioButtonIdTextMap);
	
	/**
	 * Permet de définir le radio boutons par défaut (sélectionné)
	 * @param number Numéro du radio bouton
	 */
	void setDefaultSelectedRadioButton(Integer number);
	
	/**
	 * Permet de définir l'action listener à executé lors du changement de radio bouton
	 * @param actionListener action listener
	 */
	void setActionListener(ActionListener actionListener);
	
}
