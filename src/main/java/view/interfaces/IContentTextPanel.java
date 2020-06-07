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
	 * Permet de définir le consumer pour l'édition de la valeur
	 * @param consumerEditValue consumer pour l'edition
	 */
	void consumerToEditValue(BiConsumer<String, String> consumerEditValue);
	
	/**
	 * Permet de définir une function pour se procurer la valeur à editer
	 * @param functionToGetValue function
	 */
	void functionToGetValue(Function<String, String> functionToGetValue);



}
