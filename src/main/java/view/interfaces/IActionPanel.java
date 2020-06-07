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
	 * Permet de d�finir les libell�s statique
	 * @param titlePanel Titre du panel
	 * @param buttonIdTextMap Map numero du bouton et texte associ�
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> buttonIdTextMap);
	
	/**
	 * Ajoute une action au bouton
	 * @param number numero du bouton
	 * @param action action associ�
	 */
	void addAction(Integer number, ActionListener action);
	
	/**
	 * Permet de d�finir si le bouton est actif ou non
	 * @param number num�ro du bouton
	 * @param enabled Actif ou non
	 */
	void setEnabled(Integer number, boolean enabled);
	
	/**
	 * Permet de d�finir le fonction pour la mise � jour du titre du jpanel
	 * @param titleJpanelFunction fonction pour se procurer le titre du Jpanel
	 */
	void setFunctionRefreshLabelTitleDynamically(Function<Void, String> titleJpanelFunction);
	
	/**
	 * Permet de d�finir une icone sur le bouton
	 * @param number num�ro du bouton
	 * @param pictureType type d'image
	 */
	void setIconButton(Integer number, PictureTypeEnum pictureType);
	
}
