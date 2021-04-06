package view.panel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.interfaces.ICheckBoxPanel;

/**
 * Permet de créer un panel pour les case à cocher
 * 
 * @author jerem
 *
 */
public class CheckBoxPanel implements ICheckBoxPanel {

	private final JPanel checkBoxPanel;
	private Map<Integer, JCheckBox> checkBoxMap = new LinkedHashMap<Integer, JCheckBox>();
	private final Map<Integer, String> checkBoxIdTextMap = new LinkedHashMap<>();
	private String titlePanel;
	private final JScrollPane contentWithScrollBar;
	private final Boolean enableScrollPane;
	
	/**
	 * Permet de créer un panel pour les checkbox
	 * 
	 * @param nbCheckbox nb de checkbox souhaités
	 */
	public CheckBoxPanel(Integer nbCheckbox, Boolean enableScrollPane) {
		this.enableScrollPane = enableScrollPane;
		this.checkBoxPanel = new JPanel();
		if (enableScrollPane) {
			this.checkBoxPanel.setLayout(new BoxLayout(this.checkBoxPanel, BoxLayout.Y_AXIS));
		}
		for (int i = 0; i < nbCheckbox; i++) {
			JCheckBox jCheckBox = new JCheckBox();
			checkBoxMap.put(i, jCheckBox);
			checkBoxPanel.add(jCheckBox);
		}
		contentWithScrollBar = new JScrollPane(this.checkBoxPanel);
	}

	@Override
	public JComponent getJPanel() {
		if (enableScrollPane) {
			return this.contentWithScrollBar;
		} else {
			return this.checkBoxPanel;
		}
	}

	@Override
	public void setStaticLabel(String titlePanel, Map<Integer, String> checkBoxIdTextMap) {
		this.titlePanel = titlePanel;
		this.checkBoxIdTextMap.clear();
		this.checkBoxIdTextMap.putAll(checkBoxIdTextMap);
		refresh();
	}

	@Override
	public void setEnabled(Integer number, boolean enabled) {
		if (checkBoxMap.containsKey(number)) {
			checkBoxMap.get(number).setEnabled(enabled);
		}
	}

	@Override
	public Boolean getCheckBoxIsChecked(Integer number) {
		if (checkBoxMap.containsKey(number)) {
			return checkBoxMap.get(number).isSelected();
		}
		return Boolean.FALSE;
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	public void refresh() {
		this.checkBoxPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		checkBoxIdTextMap.entrySet().forEach(es -> {
			if (checkBoxMap.containsKey(es.getKey())) {
				checkBoxMap.get(es.getKey()).setText(es.getValue());
			}
		});
	}

	@Override
	public void setChecked(Integer number, boolean checked) {
		if (checkBoxMap.containsKey(number)) {
			checkBoxMap.get(number).setSelected(checked);
		}
	}

	@Override
	public Boolean getCheckBoxIsEnabled(Integer number) {
		if (checkBoxMap.containsKey(number)) {
			return checkBoxMap.get(number).isEnabled();
		}
		return Boolean.FALSE;
	}

	@Override
	public void addConsumerOnChange(Integer number, Consumer<?> consumer) {
		if (checkBoxMap.containsKey(number) && null != consumer) {
			 checkBoxMap.get(number).addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					consumer.accept(null);
				}
			});
		}
	}

}
