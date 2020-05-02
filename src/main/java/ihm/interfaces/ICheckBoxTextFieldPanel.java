package ihm.interfaces;

import java.util.function.Consumer;

/**
 * 
 * Permet de g�rer un composant contenant une check box associ � un champ texte
 * 
 * @author jerem
 *
 */
public interface ICheckBoxTextFieldPanel extends IAccessPanel {

	
	/**
	 * Permet de savoir si la case � cocher est s�lectionn�
	 * @param number num�ro de la case � cocher
	 * @return Vrai si c'est le cas
	 */
	Boolean getCheckBoxIsChecked();
	
	/**
	 * Permet de se procurer le texte dans le champ texte
	 * @return le texte
	 */
	String getText();
	
	/**
	 * Permet de mettre � jour le texte
	 * @param text texte � �crire
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
