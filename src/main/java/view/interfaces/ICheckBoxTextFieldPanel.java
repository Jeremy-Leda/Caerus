package view.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Permet de gérer un composant contenant une check box associ à un champ texte
 * 
 * @author jerem
 *
 */
public interface ICheckBoxTextFieldPanel extends IAccessPanel {

	
	/**
	 * Permet de savoir si la case à cocher est sélectionné
	 * @param number numéro de la case à cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsChecked();
	
	/**
	 * Permet de se procurer le texte dans le champ texte
	 * @return le texte
	 */
	String getText();
	
	/**
	 * Permet de mettre à jour le texte
	 * @param text texte à écrire
	 */
	void setText(String text);
	
	/**
	 * Permet d'ajouter un consumer sur le text field lorsque le contenu change
	 * @param consumer consumer
	 */
	void addConsumerOnTextFieldChange(Consumer<?> consumer);
	
	/**
	 * Permet d'ajouter un consumer sur le changement de la case a cocher
	 * @param consumer consumer
	 */
	void addConsumerOnCheckedChange(Consumer<?> consumer);
	
	/**
	 * Permet de se procurer le titre du panel
	 * @return le titre du panel
	 */
	String getTitlePanel();
	
}
