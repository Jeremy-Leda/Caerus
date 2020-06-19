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
	 * Permet de savoir quel radiobutton a �t� s�lectionn�
	 * @return le num�ro du bouton radio
	 */
	Integer getSelectedRadioButtonNumber();
	
	/**
	 * Permet de d�finir les libell�s statique
	 * @param titlePanel Titre du panel
	 * @param radioButtonIdTextMap Map numero du radio bouton et texte associ�
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> radioButtonIdTextMap);
	
	/**
	 * Permet de d�finir le radio boutons par d�faut (s�lectionn�)
	 * @param number Num�ro du radio bouton
	 */
	void setDefaultSelectedRadioButton(Integer number);
	
	/**
	 * Permet de d�finir l'action listener � execut� lors du changement de radio bouton
	 * @param actionListener action listener
	 */
	void setActionListener(ActionListener actionListener);
	
}
