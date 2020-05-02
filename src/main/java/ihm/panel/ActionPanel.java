package ihm.panel;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import ihm.interfaces.IActionPanel;

/**
 * Permet de créer un panel action
 * @author jerem
 *
 */
public class ActionPanel implements IActionPanel {

	private JPanel actionPanel = new JPanel();
	private Map<Integer, JButton> buttonMap = new HashMap<Integer, JButton>();
	private final Map<Integer, String> buttonIdTextMap = new HashMap<>();
	private String titlePanel;
	private Function<Void, String> titleJpanelFunction;
	
	/**
	 * Permet de créer un panel action
	 * @param nbButton nb de boutons souhaités
	 */
	public ActionPanel(Integer nbButton) {
		for (int i = 0; i < nbButton; i++) {
			JButton jButton = new JButton();
			buttonMap.put(i, jButton);
			actionPanel.add(jButton);
		}
	}

	/*
	 * 
	 */
	@Override
	public void setStaticLabel(String titlePanel, Map<Integer, String> buttonIdTextMap) {
		this.titlePanel = titlePanel;
		this.buttonIdTextMap.clear();
		this.buttonIdTextMap.putAll(buttonIdTextMap);
		refresh();
	}

	/*
	 * 
	 */
	@Override
	public void addAction(Integer number, ActionListener action) {
		if (buttonMap.containsKey(number)) {
			buttonMap.get(number).addActionListener(action);
		}
	}

	/*
	 * 
	 */
	@Override
	public JComponent getJPanel() {
		return this.actionPanel;
	}

	@Override
	public void setEnabled(Integer number, boolean enabled) {
		if (buttonMap.containsKey(number)) {
			buttonMap.get(number).setEnabled(enabled);
		}
	}

	@Override
	public void refresh() {
		if (StringUtils.isNotBlank(this.titlePanel)) {
			this.actionPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		} else if (null != titleJpanelFunction) {
			this.actionPanel.setBorder(BorderFactory.createTitledBorder(titleJpanelFunction.apply(null)));
		}
		buttonIdTextMap.entrySet().forEach(es -> {
			if (buttonMap.containsKey(es.getKey())) {
				buttonMap.get(es.getKey()).setText(es.getValue());
			}
		});
	}

	@Override
	public void refreshAfterSelectedIndex() {
		refresh();
	}

	@Override
	public void setFunctionRefreshLabelTitleDynamically(Function<Void, String> titleJpanelFunction) {
		this.titleJpanelFunction = titleJpanelFunction;
	}
	
}
