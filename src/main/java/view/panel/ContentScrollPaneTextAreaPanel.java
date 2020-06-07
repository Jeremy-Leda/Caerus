package view.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import org.apache.commons.lang3.StringUtils;

import view.abstracts.ContentTextPanelAbstract;

/**
 * 
 * Permet de se procurer un panel utiliser pour la gestion du contenu des textes
 * Permet la création de textarea
 * 
 * @author jerem
 *
 */
public class ContentScrollPaneTextAreaPanel extends ContentTextPanelAbstract<JScrollPane> {
	
	private Function<String, String> functionToGetValue;
	private BiConsumer<String, String> consumerEditValue;
	
	@Override
	public void consumerToEditValue(BiConsumer<String, String> consumerEditValue) {
		this.consumerEditValue = consumerEditValue;
	}

	@Override
	public JScrollPane createNewComponentWithText(String key) {
		JTextArea textArea = new JTextArea(1, 50);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.addFocusListener(saveValue(key));
		if (null != this.functionToGetValue) {
			String content = this.functionToGetValue.apply(key);
			Integer nbLines = new StringTokenizer(content, StringUtils.LF).countTokens();
			if (nbLines == 0) {
				nbLines = 1; // On initialise à un car par défaut on a une ligne. Et pas 0.
			}
			textArea.setRows(nbLines);
			if (nbLines > 20) {
				textArea.setRows(20);
			}
			textArea.setText(content);
		}
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		return areaScrollPane;
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
	public String getValueFromField(JScrollPane field) {
		return ((JTextArea)((JViewport)field.getComponent(0)).getComponent(0)).getText();
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
	public void setValueToField(JScrollPane field, String value) {
		((JTextArea)((JViewport)field.getComponent(0)).getComponent(0)).setText(value);
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
					consumerEditValue.accept(key, ((JTextArea) e.getSource()).getText());
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		};
	}

	@Override
	public JComponent getObjectForListener(JScrollPane field) {
		return ((JTextArea)((JViewport)field.getComponent(0)).getComponent(0));
	}

	

}
