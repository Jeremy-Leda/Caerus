package view.interfaces;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 
 * Interface permettant de se procurer et d'agir sur les texts content
 * 
 * @author jerem
 *
 */
public interface IContentTextPanel extends IContentTextGenericPanel {

	/**
	 * Permet de d�finir le consumer pour l'�dition de la valeur
	 * @param consumerEditValue consumer pour l'edition
	 */
	void consumerToEditValue(BiConsumer<String, String> consumerEditValue);
	
	/**
	 * Permet de d�finir une function pour se procurer la valeur � editer
	 * @param functionToGetValue function
	 */
	void functionToGetValue(Function<String, String> functionToGetValue);



}
