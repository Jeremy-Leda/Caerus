package view.panel;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JComponent;

import controler.IConfigurationControler;
import view.beans.ConsumerTextTypeEnum;
import view.beans.FunctionTextTypeEnum;
import view.beans.TextIhmTypeEnum;
import view.interfaces.IConsumerAndFunctionText;
import view.interfaces.IContentTextGenericPanel;
import view.interfaces.IContentTextPanel;
import view.panel.model.ConsumerAndFunctionTextModel;

/**
 * 
 * Classe permettant de créer un panel contenant les informations pour
 * l'affichage des contenu de texte
 * 
 * @author jerem
 *
 */
public class ContentTextGenericPanel implements IContentTextGenericPanel {

	private final IContentTextPanel contentTextPanel;
	private final IConsumerAndFunctionText consumerFunctionText;

	/**
	 * Constructeur
	 * 
	 * @param controler controleur
	 * @param typeIhm   type d'ihm
	 * @param consumerType Type du consumer pour mettre à jour la valeur
	 * @param functionType function du consumer pour se procurer la valeur
	 */
	public ContentTextGenericPanel(IConfigurationControler controler, TextIhmTypeEnum typeIhm, ConsumerTextTypeEnum consumerType,
			FunctionTextTypeEnum functionType) {
		super();
		this.consumerFunctionText = new ConsumerAndFunctionTextModel(controler);
		switch (typeIhm) {
		case JSCROLLPANE:
			this.contentTextPanel = new ContentScrollPaneTextAreaPanel();
			break;
		case JTEXTFIELD:
			this.contentTextPanel = new ContentTextFieldTextPanel();
			break;
		default:
			this.contentTextPanel = new ContentScrollPaneTextAreaPanel();
			break;
		}
		this.contentTextPanel.consumerToEditValue(this.consumerFunctionText.getConsumer(consumerType));
		this.contentTextPanel.functionToGetValue(this.consumerFunctionText.getFunction(functionType));
	}

	@Override
	public void refresh(String titlePanel) {
		this.contentTextPanel.refresh(titlePanel);
	}

	@Override
	public JComponent getJPanel() {
		return this.contentTextPanel.getJPanel();
	}

	@Override
	public void refreshComponents(Map<String, String> informationFieldMap) {
		this.contentTextPanel.refreshComponents(informationFieldMap);
	}

	@Override
	public void addKeyListener(String key, KeyListener keyListener) {
		this.contentTextPanel.addKeyListener(key, keyListener);
	}

	@Override
	public void addKeyListenerOnAllField(KeyListener keyListener) {
		this.contentTextPanel.addKeyListenerOnAllField(keyListener);
	}
	
	@Override
	public void addFocusListener(String key, FocusListener focusListener) {
		this.contentTextPanel.addFocusListener(key, focusListener);
	}
	
	@Override
	public void addFocusListenerOnAllField(FocusListener focusListener) {
		this.contentTextPanel.addFocusListenerOnAllField(focusListener);
	}
	
	@Override
	public Map<String, String> getFieldValueMap() {
		return this.contentTextPanel.getFieldValueMap();
	}
	
	@Override
	public String getValue(String key) {
		return this.contentTextPanel.getValue(key);
	}
	
	@Override
	public void setEnabled(String key, boolean enabled) {
		this.contentTextPanel.setEnabled(key, enabled);
	}
	
	@Override
	public void setEnabledOnAllField(boolean enabled) {
		this.contentTextPanel.setEnabledOnAllField(enabled);
	}

	@Override
	public void setValue(String key, String newValue) {
		this.contentTextPanel.setValue(key, newValue);
	}
	
	@Override
	public void reloadValue() {
		this.contentTextPanel.reloadValue();
	}

	@Override
	public void setRefreshDisplayConsumer(Consumer<?> refreshDisplay) {
		this.contentTextPanel.setRefreshDisplayConsumer(refreshDisplay);
	}

}
