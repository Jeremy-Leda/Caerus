package view.panel;

import org.apache.commons.lang3.StringUtils;
import view.interfaces.ITextBoxPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Permet de créer un panel pour les textBox
 * 
 * @author jerem
 *
 */
public class TextBoxPanel implements ITextBoxPanel {

	private final JPanel textBoxPanel;
	private Map<Integer, JTextField> textBoxMap = new LinkedHashMap<>();
	private Map<Integer, JLabel> labelBoxMap = new LinkedHashMap<>();
	private final Map<Integer, String> textBoxIdTextMap = new LinkedHashMap<>();
	private String titlePanel;
	private final JScrollPane contentWithScrollBar;
	private final Boolean enableScrollPane;

	/**
	 * Permet de créer un panel pour les textBox
	 *
	 *
	 * @param nbTextbox nb de textBox souhaités
	 */
	public TextBoxPanel(Integer nbTextbox, Boolean enableScrollPane, Integer nbColumns) {
		this.enableScrollPane = enableScrollPane;
		this.textBoxPanel = new JPanel();
		if (enableScrollPane) {
			this.textBoxPanel.setLayout(new BoxLayout(this.textBoxPanel, BoxLayout.Y_AXIS));
		}
		for (int i = 0; i < nbTextbox; i++) {
			JTextField jTextField = new JTextField();
			jTextField.setColumns(nbColumns);
			textBoxMap.put(i, jTextField);
			JLabel jLabel = new JLabel();
			labelBoxMap.put(i, jLabel);
			JPanel jPanel = new JPanel();
			jPanel.add(jLabel);
			jPanel.add(jTextField);
			textBoxPanel.add(jPanel);
		}
		contentWithScrollBar = new JScrollPane(this.textBoxPanel);
	}

	@Override
	public JComponent getJPanel() {
		if (enableScrollPane) {
			return this.contentWithScrollBar;
		} else {
			return this.textBoxPanel;
		}
	}

	@Override
	public void setStaticLabel(String titlePanel, Map<Integer, String> textBoxIdTextMap) {
		this.titlePanel = titlePanel;
		this.textBoxIdTextMap.clear();
		this.textBoxIdTextMap.putAll(textBoxIdTextMap);
		refresh();
	}

	@Override
	public String getValueOfTextBox(Integer number) {
		return textBoxMap.get(number).getText();
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	public void refresh() {
		if (StringUtils.isNotBlank(titlePanel)) {
			this.textBoxPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		}
		textBoxIdTextMap.entrySet().forEach(es -> {
			if (labelBoxMap.containsKey(es.getKey())) {
				labelBoxMap.get(es.getKey()).setText(es.getValue());
			}
		});
	}


	@Override
	public void addConsumerOnChange(Integer number, Consumer<?> consumer) {
		if (textBoxMap.containsKey(number) && null != consumer) {
			 textBoxMap.get(number).addKeyListener (new KeyListener() {

				 @Override
				 public void keyTyped(KeyEvent e) {

				 }

				 @Override
				 public void keyPressed(KeyEvent e) {

				 }

				 @Override
				 public void keyReleased(KeyEvent e) {
					 consumer.accept(null);
				 }
			});
		}
	}

}
