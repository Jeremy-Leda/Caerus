package view.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import view.abstracts.ContentTextPanelAbstract;


/**
 * 
 * Permet de se procurer un panel utiliser pour la gestion du contenu des textes
 * Permet la création de textfield
 * 
 * @author jerem
 *
 */
public class ContentTextFieldTextPanel extends ContentTextPanelAbstract<JTextField> {

	private Function<String, String> functionToGetValue;
	private BiConsumer<String, String> consumerEditValue;
	
	@Override
	public void consumerToEditValue(BiConsumer<String, String> consumerEditValue) {
		this.consumerEditValue = consumerEditValue;		
	}

	@Override
	public void functionToGetValue(Function<String, String> functionToGetValue) {
		this.functionToGetValue = functionToGetValue;
	}

	@Override
	public void refreshComponents(Map<String, String> informationFieldMap) {
		clearAndFillMap(informationFieldMap);
	}

	@Override
	public JTextField createNewComponentWithText(String key) {
		JTextField textField = new JTextField(StringUtils.EMPTY, 30);
		textField.addFocusListener(saveValue(key));
		if (null != this.functionToGetValue) {
			String content = this.functionToGetValue.apply(key);
			textField.setText(content);
		}
		return textField;
	}
	
	@Override
	public String getValueFromField(JTextField field) {
		return field.getText();
	}
	
	@Override
	public void reloadValue() {
		super.getFieldValueMap().keySet().forEach(key -> {
			String newValue = StringUtils.EMPTY;
			if (null != this.functionToGetValue) {
				newValue = this.functionToGetValue.apply(key);
			}
			super.setValue(key, newValue);
		});
	}
	
	@Override
	public void setValueToField(JTextField field, String value) {
		field.setText(value);
	}
	
	/**
	 * Permet de se procurer le listener pour l'enregistrement sur la perte du focus
	 * @param key Clé
	 * @return le focus listener
	 */
	private FocusListener saveValue(String key) {
		return new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (null != consumerEditValue) {
					consumerEditValue.accept(key, ((JTextField) e.getSource()).getText());
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		};
	}

	@Override
	public JComponent getObjectForListener(JTextField field) {
		return field;
	}
	

}
