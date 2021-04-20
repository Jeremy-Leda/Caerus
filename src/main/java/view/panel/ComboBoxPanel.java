package view.panel;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
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
		if (StringUtils.isNotBlank(titlePanel)) {
			this.comboBoxPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		}
		this.labelCombo = new JLabel(label);
		this.comboBox = new JComboBox<>();
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
		return Optional.ofNullable(this.comboBox.getSelectedItem()).orElse(StringUtils.EMPTY).toString();
	}

	@Override
	public void addConsumerOnSelectChange(Consumer<?> consumer) {
		this.comboBox.addItemListener(e -> consumer.accept(null));
	}

	@Override
	public void refresh(Collection<String> labels) {
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

	@Override
	public Boolean itemExist(String item) {
		return IntStream.range(0, this.comboBox.getItemCount())
				.anyMatch(i -> item.equals(this.comboBox.getItemAt(i)));
	}

	@Override
	public void addAndSelectItem(String newItem) {
		this.comboBox.addItem(newItem);
		this.selectItem(newItem);
	}

	@Override
	public void delete(String itemToDelete) {
		this.comboBox.removeItem(itemToDelete);
	}

}
