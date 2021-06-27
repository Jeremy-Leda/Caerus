package view.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.*;

import controler.IConfigurationControler;
import org.apache.commons.lang3.StringUtils;

import view.abstracts.ContentTextPanelAbstract;
import view.beans.*;


/**
 * 
 * Permet de se procurer un panel utiliser pour la gestion du contenu des textes
 * Permet la création de textfield
 * 
 * @author jerem
 *
 */
public class ContentTextFieldTextPanel extends ContentTextPanelAbstract<JTextField> {

	private final StateCorpusEnum stateCorpusAction;
	private final IConfigurationControler controler;
	private Optional<String> optionalKeyText = Optional.empty();
	private Boolean isReadOnly = false;
	/**
	 * Constructeur
	 * @param controler controller
	 */
	public ContentTextFieldTextPanel(IConfigurationControler controler, StateCorpusEnum stateCorpusAction) {
		this.controler = controler;
		this.stateCorpusAction = stateCorpusAction;
	}

	@Override
	public void refreshComponents(Map<String, String> informationFieldMap) {
		clearAndFillMap(informationFieldMap);
	}

	@Override
	public void setReadOnly(Boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public JTextField createNewComponentWithText(String key) {
		JTextField textField;
		if (stateCorpusAction.equals(StateCorpusEnum.READ)) {
			textField = new JTextField(StringUtils.EMPTY, 10);
		} else {
			textField = new JTextField(StringUtils.EMPTY, 30);
		}
		textField.setEditable(!isReadOnly);
		textField.addFocusListener(saveValue(controler, stateCorpusAction, key));
		this.stateCorpusAction.getOptionalStateCorpusGetActionCmdStringBiFunction().ifPresent(c -> {
			final StateCorpusGetActionCmd cmd = super.getStateCorpusGetActionCmd(optionalKeyText, key);
			String contentServer = c.apply(controler, cmd);
			textField.setText(contentServer);
		});
		return textField;
	}
	
	@Override
	public String getValueFromField(JTextField field) {
		return field.getText();
	}
	
	@Override
	public void reloadValue() {
		super.getFieldValueMap().keySet().forEach(key -> {
			final StateCorpusGetActionCmd cmd = super.getStateCorpusGetActionCmd(optionalKeyText, key);
			this.stateCorpusAction.getOptionalStateCorpusGetActionCmdStringBiFunction().ifPresentOrElse(
					c -> super.setValue(key, c.apply(controler, cmd)),
					() -> super.setValue(key, StringUtils.EMPTY));
		});
	}
	
	@Override
	public void setValueToField(JTextField field, String value) {
		field.setText(value);
	}
	


	@Override
	public JComponent getObjectForListener(JTextField field) {
		return field;
	}

	@Override
	public void setRefreshDisplayConsumer(Consumer<?> refreshDisplay) {

	}

	@Override
	public void setKeyText(String keyText) {
		this.optionalKeyText = Optional.ofNullable(keyText);
	}
}
