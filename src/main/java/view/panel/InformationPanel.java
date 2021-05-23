package view.panel;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import utils.RessourcesUtils;
import view.beans.PictureTypeEnum;
import view.interfaces.IInformationPanel;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * Classe pour afficher un panel d'information
 * 
 * @author jerem
 *
 */
public class InformationPanel implements IInformationPanel {

	private final PictureTypeEnum typeImage;
	private final String title;
	private final JPanel content;
	private final JLabel textPanel;
	private final JScrollPane contentWithScrollBar;
	private final Boolean enableScrollPane;
	private JLabel icon;
	
	/**
	 * Permet de créer une fenêtre d'information
	 * @param typeImage type de l'image
	 * @param title titre
	 * @param text texte à afficher
	 * @param enableScrollPane active la scrollpane
	 * @param onlyHorizontalScrollBar Permet de définir que la bar horizontal
	 */
	public InformationPanel(PictureTypeEnum typeImage, String title, String text, Boolean enableScrollPane, Boolean onlyHorizontalScrollBar) {
		this.enableScrollPane = enableScrollPane;
		this.typeImage = typeImage;
		this.title = title;
		this.textPanel = new JLabel(text);
		this.content = new JPanel();
		if (enableScrollPane && onlyHorizontalScrollBar) {
			this.contentWithScrollBar = new JScrollPane(this.content, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
		            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		} else {
			this.contentWithScrollBar = new JScrollPane(this.content);
		}
		createWindow();
	} 
	
	/**
	 * Permet de créer la fenetre
	 */
	private void createWindow() {
		content.setBorder(
				BorderFactory.createTitledBorder(this.title));
		icon = new JLabel(new ImageIcon(RessourcesUtils.getInstance().getAnimatedImage(this.typeImage)));
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
