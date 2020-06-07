package view.panel;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import view.interfaces.IAccessPanel;
import view.interfaces.ISpecificTextModel;
import view.interfaces.ISpecificTextRefreshPanel;

/**
 * 
 * Permet de créer la structure Control + List Panel
 * 
 * @author jerem
 *
 */
public class SpecificControlAndListPanel implements IAccessPanel {

	private final JPanel content;
	private final ISpecificTextModel specificTextModel;
	private final ISpecificTextRefreshPanel entryContentSpecificTextPanel;
	private final ISpecificTextRefreshPanel detailListContentSpecificTextPanel;
	
	/**
	 * Constructeur
	 * @param specificTextModel specific text model
	 */
	public SpecificControlAndListPanel(ISpecificTextModel specificTextModel) {
		this.specificTextModel = specificTextModel;
		this.entryContentSpecificTextPanel = new SpecificControlPanel(this.specificTextModel);
		this.detailListContentSpecificTextPanel = new SpecificListPanel(this.specificTextModel);
		this.specificTextModel.addSpecificTextRefresh(this.entryContentSpecificTextPanel);
		this.specificTextModel.addSpecificTextRefresh(this.detailListContentSpecificTextPanel);
		this.content = new JPanel();
		createContent();
	}
	
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.entryContentSpecificTextPanel.getJPanel());
		content.add(this.detailListContentSpecificTextPanel.getJPanel());
	}
	
	@Override
	public JComponent getJPanel() {
		return this.content;
	}


}
