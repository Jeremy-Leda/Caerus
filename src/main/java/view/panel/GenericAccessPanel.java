package view.panel;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.junit.platform.commons.util.StringUtils;

import view.interfaces.IGenericAccessPanel;

/**
 * 
 * Permet de créer un panel Générique sur mesure
 * 
 * @author jerem
 *
 */
public class GenericAccessPanel implements IGenericAccessPanel {

	private final JPanel content;
	private final List<JComponent> componentList;
	
	/**
	 * Constructeur
	 */
	public GenericAccessPanel() {
		this(null);
	}
	
	/**
	 * Constructeur
	 * @param title titre du panel
	 */
	public GenericAccessPanel(String title) {
		this.content = new JPanel();
		this.componentList = new LinkedList<JComponent>();
		if (StringUtils.isNotBlank(title)) {
			refreshTitle(title);
		}
		BoxLayout boxlayout = new BoxLayout(this.content, BoxLayout.Y_AXIS);
		this.content.setLayout(boxlayout);
	}

	@Override
	public JComponent getJPanel() {
		return this.content;
	}

	@Override
	public void addComponent(JComponent component) {
		if (null != component) {
			this.componentList.add(component);
			refreshContent();
		}
	}

	/**
	 * Permet de rafraichir le contenu
	 */
	private void refreshContent() {
		this.content.removeAll();
		this.componentList.forEach(component -> this.content.add(component));
	}

	@Override
	public void refreshTitle(String title) {
		this.content.setBorder(
				BorderFactory.createTitledBorder(title));
	}
	
}
