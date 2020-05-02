package ihm.interfaces;

import java.util.function.BiConsumer;
import java.util.function.Function;

import ihm.beans.ConsumerTextTypeEnum;
import ihm.beans.FunctionTextTypeEnum;

/**
 * 
 * Interface pour se procurer les consumers et les functions du text
 * 
 * @author jerem
 *
 */
public interface IConsumerAndFunctionText {

	/**
	 * Permet de se procurer le consumer
	 * @param type type du consumer
	 * @return le consumer
	 */
	BiConsumer<String, String> getConsumer(ConsumerTextTypeEnum type);
	
	/**
	 * Permet de se procurer la fonction
	 * @param type type de la fonction
	 * @return le consumer
	 */
	Function<String, String> getFunction(FunctionTextTypeEnum type);
	
}
