package view.panel;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.lang3.StringUtils;

import view.interfaces.IRadioButtonPanel;

/**
 * 
 * Permet de gérer les boutons radio
 * 
 * @author jerem
 *
 */
public class RadioButtonPanel implements IRadioButtonPanel {

	private JPanel radioButtonPanel = new JPanel();
	private Map<Integer, JRadioButton> radioButtonMap = new HashMap<Integer, JRadioButton>();

	/**
	 * 
	 * Constructeur
	 * 
	 * @param nbRadioButton Nombre de radio bouton
	 */
	public RadioButtonPanel(Integer nbRadioButton) {
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < nbRadioButton; i++) {
			JRadioButton button = new JRadioButton();
			radioButtonMap.put(i, button);
			group.add(button);
			radioButtonPanel.add(button);
		}
	}

	@Override
	public JComponent getJPanel() {
		return radioButtonPanel;
	}

	@Override
	public Integer getSelectedRadioButtonNumber() {
		Optional<Entry<Integer, JRadioButton>> findFirstSelectedRadioButton = radioButtonMap.entrySet().stream()
				.filter(entry -> entry.getValue().isSelected()).findFirst();
		if (findFirstSelectedRadioButton.isPresent()) {
			return findFirstSelectedRadioButton.get().getKey();
		}
		return null;
	}

	@Override
	public void setStaticLabel(String titlePanel, Map<Integer, String> radioButtonIdTextMap) {
		if (StringUtils.isNotBlank(titlePanel)) {
			this.radioButtonPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		}
		radioButtonIdTextMap.entrySet().forEach(es -> {
			if (radioButtonMap.containsKey(es.getKey())) {
				radioButtonMap.get(es.getKey()).setText(es.getValue());
			}
		});
	}

	@Override
	public void setDefaultSelectedRadioButton(Integer number) {
		if (radioButtonMap.containsKey(number)) {
			radioButtonMap.get(number).setSelected(Boolean.TRUE);
		}
	}

	@Override
	public void setActionListener(ActionListener actionListener) {
		radioButtonMap.values().forEach(b -> b.addActionListener(actionListener));
	}

	@Override
	public String getSelectedLabel() {
		return this.radioButtonMap.get(getSelectedRadioButtonNumber()).getText();
	}


}
