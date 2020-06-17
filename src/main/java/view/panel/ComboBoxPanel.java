package view.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import view.interfaces.IComboBoxPanel;

/**
 * 
 * Combo box panel
 * 
 * @author jerem
 *
 */
public class ComboBoxPanel implements IComboBoxPanel{

	private final JPanel comboBoxPanel;
	private final JLabel labelCombo;
	private final JComboBox<String> comboBox;
	
	public ComboBoxPanel(String titlePanel, String label) {
		this.comboBoxPanel = new JPanel();
		this.comboBoxPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		this.labelCombo = new JLabel(label);
		this.comboBox = new JComboBox<String>();
		createContent();
	}
	
	private void createContent() {
		this.comboBoxPanel.add(labelCombo);
		this.comboBoxPanel.add(comboBox);
	}

	@Override
	public JComponent getJPanel() {
		return this.comboBoxPanel;
	}

	@Override
	public String getLabelSelected() {
		return this.comboBox.getSelectedItem().toString();
	}

	@Override
	public void addConsumerOnSelectChange(Consumer<?> consumer) {
		this.comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				consumer.accept(null);
			}
		});
	}

	@Override
	public void refresh(List<String> labels) {
		this.comboBox.removeAllItems();
		labels.forEach(l -> this.comboBox.addItem(l));
	}

	@Override
	public void selectItem(String itemToSelect) {
		this.comboBox.setSelectedItem(itemToSelect);
	}

	@Override
	public int getItemCount() {
		return this.comboBox.getItemCount();
	}
	
}
