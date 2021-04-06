package view.panel.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import controler.IConfigurationControler;
import view.beans.ConsumerTextTypeEnum;
import view.beans.FunctionTextTypeEnum;
import view.interfaces.IConsumerAndFunctionText;

/**
 * 
 * Permet de se procurer les informations de function et de consommation
 * 
 * @author jerem
 *
 */
public class ConsumerAndFunctionTextModel implements IConsumerAndFunctionText {

	private final IConfigurationControler controler;
	private final Map<ConsumerTextTypeEnum, BiConsumer<String, String>> consumerMap = new HashMap<ConsumerTextTypeEnum, BiConsumer<String,String>>();
	private final Map<FunctionTextTypeEnum, Function<String, String>> functionMap = new HashMap<FunctionTextTypeEnum, Function<String,String>>();
	
	public ConsumerAndFunctionTextModel(IConfigurationControler controler) {
		super();
		this.controler = controler;
		fillAllMap();
	}
	
	/**
	 * Permet de remplir l'ensemble des maps
	 */
	private void fillAllMap() {
		this.consumerMap.put(ConsumerTextTypeEnum.NULL, null);
		this.consumerMap.put(ConsumerTextTypeEnum.CORPUS, getConsumerCorpusEditValue());
		this.functionMap.put(FunctionTextTypeEnum.NULL, null);
		this.functionMap.put(FunctionTextTypeEnum.CORPUS, getFunctionCorpusToGetValue());
	}

	/**
	 * Permet de se procurer le consumer pour l'édition du corpus
	 * @return le consumer pour l'édition du corpus
	 */
	private BiConsumer<String, String> getConsumerCorpusEditValue() {
		return (key,value) -> {
			this.controler.updateFieldInEditingCorpus(key, value);
		};
	}

	/**
	 * Permet de se procurer la function pour récupérer la valeur dans le corpus en cours
	 * @return la function
	 */
	private Function<String, String> getFunctionCorpusToGetValue() {
		return (key) -> {
			return this.controler.getFieldInEditingCorpus(key);
		};
	}

	@Override
	public BiConsumer<String, String> getConsumer(ConsumerTextTypeEnum type) {
		return this.consumerMap.get(type);
	}

	@Override
	public Function<String, String> getFunction(FunctionTextTypeEnum type) {
		return this.functionMap.get(type);
	}
	
}
