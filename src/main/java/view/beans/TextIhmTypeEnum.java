package view.beans;

import controler.IConfigurationControler;
import view.interfaces.IContentTextPanel;
import view.panel.ContentScrollPaneTextAreaPanel;
import view.panel.ContentTextFieldTextPanel;

import java.util.function.BiFunction;

/**
 * 
 * Permet de définir le type de text attendu
 * 
 * @author jerem
 *
 */
public enum TextIhmTypeEnum {
	JTEXTFIELD(((controler, stateCorpusEnum) -> new ContentTextFieldTextPanel(controler, stateCorpusEnum))),
	JSCROLLPANE(((controler, stateCorpusEnum) -> new ContentScrollPaneTextAreaPanel(controler, stateCorpusEnum)));

	private final BiFunction<IConfigurationControler, StateCorpusEnum, IContentTextPanel> iContentTextPanelBiFunction;


	TextIhmTypeEnum(BiFunction<IConfigurationControler, StateCorpusEnum, IContentTextPanel> iContentTextPanelBiFunction) {
		this.iContentTextPanelBiFunction = iContentTextPanelBiFunction;
	}

	/**
	 * Permet de se procurer la bifunction pour récupérer le content text panel
	 * @return le content text panel
	 */
	public BiFunction<IConfigurationControler, StateCorpusEnum, IContentTextPanel> getiContentTextPanelBiFunction() {
		return iContentTextPanelBiFunction;
	}
}
