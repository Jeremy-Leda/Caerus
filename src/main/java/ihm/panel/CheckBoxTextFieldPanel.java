package ihm.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ihm.interfaces.ICheckBoxTextFieldPanel;

/**
 * 
 * Permet de gérer un composant avec une check box et un champ texte associé
 * 
 * @author jerem
 *
 */
public class CheckBoxTextFieldPanel implements ICheckBoxTextFieldPanel {

	private final JPanel checkBoxTextFieldPanel = new JPanel();
	private final JCheckBox checkBox = new JCheckBox();
	private final JTextField textField = new JTextField();
	private final String titlePanel;
	
	/**
	 * Permet de créer le composant
	 * @param titlePanel Titre du panel
	 * @param labelCheckBox Libellé du checkbox
	 * @param textFieldText Contenu du champ texte
	 */
	public CheckBoxTextFieldPanel(String titlePanel, String labelCheckBox, String textFieldText) {
		this.titlePanel = titlePanel;
		this.checkBoxTextFieldPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		this.checkBoxTextFieldPanel.setLayout(new BoxLayout(this.checkBoxTextFieldPanel, BoxLayout.Y_AXIS));
		this.checkBox.setText(labelCheckBox);
		this.textField.setText(textFieldText);
		this.checkBox.setSelected(true);
		this.checkBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				textField.setEnabled(checkBox.isSelected());
			}
		});
		this.checkBoxTextFieldPanel.add(checkBox);
		this.checkBoxTextFieldPanel.add(textField);
	}
	
	@Override
	public JComponent getJPanel() {
		return this.checkBoxTextFieldPanel;
	}

	@Override
	public Boolean getCheckBoxIsChecked() {
		return this.checkBox.isSelected();
	}

	@Override
	public String getText() {
		return this.textField.getText();
	}

	@Override
	public void setText(String text) {
		this.setText(text);
	}

	@Override
	public void addConsumerOnTextFieldChange(Consumer<?> consumer) {
		this.textField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				consumer.accept(null);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	@Override
	public String getTitlePanel() {
		return this.titlePanel;
	}

	@Override
	public void addConsumerOnCheckedChange(Consumer<?> consumer) {
		this.checkBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				consumer.accept(null);
			}
		});
	}

}
