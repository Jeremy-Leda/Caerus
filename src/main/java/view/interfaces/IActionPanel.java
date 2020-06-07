package view.interfaces;

import java.awt.event.ActionListener;
import java.util.Map;
import java.util.function.Function;

import view.beans.PictureTypeEnum;

/**
 * Interface permettant de controler l'action panel
 * @author jerem
 *
 */
public interface IActionPanel extends ISpecificTextRefreshPanel {

	/**
	 * Permet de définir les libellés statique
	 * @param titlePanel Titre du panel
	 * @param buttonIdTextMap Map numero du bouton et texte associé
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> buttonIdTextMap);
	
	/**
	 * Ajoute une action au bouton
	 * @param number numero du bouton
	 * @param action action associé
	 */
	void addAction(Integer number, ActionListener action);
	
	/**
	 * Permet de définir si le bouton est actif ou non
	 * @param number numéro du bouton
	 * @param enabled Actif ou non
	 */
	void setEnabled(Integer number, boolean enabled);
	
	/**
	 * Permet de définir le fonction pour la mise à jour du titre du jpanel
	 * @param titleJpanelFunction fonction pour se procurer le titre du Jpanel
	 */
	void setFunctionRefreshLabelTitleDynamically(Function<Void, String> titleJpanelFunction);
	
	/**
	 * Permet de définir une icone sur le bouton
	 * @param number numéro du bouton
	 * @param pictureType type d'image
	 */
	void setIconButton(Integer number, PictureTypeEnum pictureType);
	
}
