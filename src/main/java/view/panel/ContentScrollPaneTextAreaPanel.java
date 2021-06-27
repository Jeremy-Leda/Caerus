package view.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import controler.IConfigurationControler;
import org.apache.commons.lang3.StringUtils;

import org.apache.regexp.RE;
import view.abstracts.ContentTextPanelAbstract;
import view.beans.*;

/**
 * 
 * Permet de se procurer un panel utiliser pour la gestion du contenu des textes
 * Permet la création de textarea
 * 
 * @author jerem
 *
 */
public class ContentScrollPaneTextAreaPanel extends ContentTextPanelAbstract<JScrollPane> {

	private final StateCorpusEnum stateCorpusAction;
	private Consumer<?> refreshDisplay;
	private static Integer NB_CARAC;
	private static final Integer NB_CARAC_MAX = 102;
	private final IConfigurationControler controler;
	private Optional<String> optionalKeyText = Optional.empty();
	private Boolean isReadOnly = false;

	public ContentScrollPaneTextAreaPanel(IConfigurationControler controler, StateCorpusEnum stateCorpusAction) {
		this.controler = controler;
		this.stateCorpusAction = stateCorpusAction;
		if (stateCorpusAction.equals(StateCorpusEnum.READ)) {
			NB_CARAC = 30;
		} else {
			NB_CARAC = 50;
		}
	}

	@Override
	public JScrollPane createNewComponentWithText(String key) {
		JTextArea textArea = new JTextArea(1, NB_CARAC);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.addFocusListener(saveValue(controler, stateCorpusAction, key));
		textArea.addKeyListener(refreshDisplayKeyListener(textArea));
		textArea.setEditable(!isReadOnly);
		this.stateCorpusAction.getOptionalStateCorpusGetActionCmdStringBiFunction().ifPresent(c -> {
			final StateCorpusGetActionCmd cmd = super.getStateCorpusGetActionCmd(optionalKeyText, key);
			String contentServer = c.apply(controler, cmd);
			String contentTrim = StringUtils.trim(contentServer);
			textArea.setText(contentTrim);
			refreshNbLines(textArea);
		});
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		return areaScrollPane;
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
	public String getValueFromField(JScrollPane field) {
		return ((JTextArea) ((JViewport) field.getComponent(0)).getComponent(0)).getText();
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
	public void setValueToField(JScrollPane field, String value) {
		((JTextArea) ((JViewport) field.getComponent(0)).getComponent(0)).setText(value);
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

	@Override
	public void setKeyText(String keyText) {
		this.optionalKeyText = Optional.ofNullable(keyText);
	}
}
