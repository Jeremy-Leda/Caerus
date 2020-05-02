package ihm.panel;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ihm.beans.ImageTypeEnum;
import ihm.interfaces.IInformationPanel;
import utils.RessourcesUtils;

/**
 * 
 * Classe pour afficher un panel d'information
 * 
 * @author jerem
 *
 */
public class InformationPanel implements IInformationPanel {

	private final ImageTypeEnum typeImage;
	private final String title;
	private final JPanel content;
	private final JLabel textPanel;
	private final JScrollPane contentWithScrollBar;
	private final Boolean enableScrollPane;
	
	/**
	 * Permet de créer une fenêtre d'information
	 * @param typeImage type de l'image
	 * @param title titre
	 * @param text texte à afficher
	 */
	public InformationPanel(ImageTypeEnum typeImage, String title, String text, Boolean enableScrollPane) {
		this.enableScrollPane = enableScrollPane;
		this.typeImage = typeImage;
		this.title = title;
		this.textPanel = new JLabel(text);
		this.content = new JPanel();
		this.contentWithScrollBar = new JScrollPane(this.content);
		createWindow();
	} 
	
	/**
	 * Permet de créer la fenetre
	 */
	private void createWindow() {
		content.setBorder(
				BorderFactory.createTitledBorder(this.title));
		JLabel icon = new JLabel(new ImageIcon(RessourcesUtils.getInstance().getImage(this.typeImage)));
		content.add(icon);
		content.add(textPanel);
	}
	
	@Override
	public JComponent getJPanel() {
		if (enableScrollPane) {			
			return this.contentWithScrollBar;
		} else {
			return this.content;
		}
	}

	@Override
	public void refreshInformations(String informations) {
		this.textPanel.setText(informations);
	}

}
