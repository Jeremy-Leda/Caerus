package view.interfaces;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Interface permettant de controler le textbox panel
 * @author jerem
 *
 */
public interface ITextBoxPanel extends IAccessPanel {

	/**
	 * Permet de définir les libellés statique
	 * @param titlePanel Titre du panel
	 * @param textBoxIdTextMap Map numéro de la textbox et texte associé
	 */
	void setStaticLabel(String titlePanel, Map<Integer,String> textBoxIdTextMap);
	

	/**
	 * Permet de se procurer la valeur de la textBox
	 * @param number numéro de la case à cocher
	 * @return la valeur de la textBox
	 */
	String getValueOfTextBox(Integer number);

	
	/**
	 * Permet d'ajouter un consumer sur le changement de la valeur
	 * @param number numéro de la case à cocher
	 * @param consumer consumer
	 */
	void addConsumerOnChange(Integer number, Consumer<?> consumer);
	
	
}
