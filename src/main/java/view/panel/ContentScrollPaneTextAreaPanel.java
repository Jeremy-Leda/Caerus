package view.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
	private Consumer<?> refreshDisplay;
	private static final Integer NB_CARAC = 50;
	private static final Integer NB_CARAC_MAX = 102;

	@Override
	public void consumerToEditValue(BiConsumer<String, String> consumerEditValue) {
		this.consumerEditValue = consumerEditValue;
	}

	@Override
	public JScrollPane createNewComponentWithText(String key) {
		JTextArea textArea = new JTextArea(1, NB_CARAC);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.addFocusListener(saveValue(key));
		textArea.addKeyListener(refreshDisplayKeyListener(textArea));
		if (null != this.functionToGetValue) {
			String content = StringUtils.trim(this.functionToGetValue.apply(key));
			textArea.setText(content);
			refreshNbLines(textArea);
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
		return ((JTextArea) ((JViewport) field.getComponent(0)).getComponent(0)).getText();
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
		((JTextArea) ((JViewport) field.getComponent(0)).getComponent(0)).setText(value);
	}

	/**
	 * Permet de se procurer le listener pour l'enregistrement sur la perte du focus
	 * 
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
		return ((JTextArea) ((JViewport) field.getComponent(0)).getComponent(0));
	}

	/**
	 * Permet de se procurer un key listener pour la mise à jour de l'affichage
	 * 
	 * @param textArea zone de texte
	 * @return le key listener
	 */
	private KeyListener refreshDisplayKeyListener(JTextArea textArea) {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				refreshNbLines(textArea);
				if (null != refreshDisplay) {
					refreshDisplay.accept(null);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		};
	}

	/**
	 * Permet de rafraichir le nombre du ligne du contenu de texte
	 * 
	 * @param textArea zone de texte
	 */
	private void refreshNbLines(JTextArea textArea) {
		StringTokenizer st = new StringTokenizer(textArea.getText(), StringUtils.LF);
		Integer nbLines = 0;
		while (st.hasMoreTokens()) {
			String text = (String) st.nextToken();
			BigDecimal nbLinesForThisLine = new BigDecimal(text.length()).setScale(0, RoundingMode.DOWN)
					.divide(new BigDecimal(NB_CARAC_MAX), RoundingMode.DOWN);
			nbLines += nbLinesForThisLine.intValueExact();
			nbLines++;
		}
		textArea.setRows(nbLines);
		if (nbLines > 20) {
			textArea.setRows(20);
		}
	}

	@Override
	public void setRefreshDisplayConsumer(Consumer<?> refreshDisplay) {
		this.refreshDisplay = refreshDisplay;
	}

}
