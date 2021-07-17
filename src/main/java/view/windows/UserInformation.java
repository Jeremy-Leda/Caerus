package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.panel.ActionPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Permet d'afficher une information à l'utilisateur
 * 
 * @author jerem
 *
 */
public class UserInformation extends ModalJFrameAbstract {

	private final JPanel content;
	private final InformationPanel infoPanel;
	private final IActionPanel actionPanel;
	
	public UserInformation(String title, IConfigurationControler configurationControler, PictureTypeEnum pictureType, String message) {
		this(title, configurationControler, pictureType, message, true);
	}

	public UserInformation(String title, IConfigurationControler configurationControler, PictureTypeEnum pictureType, String message, Boolean isModal) {
		super(title, configurationControler, isModal);
		this.content = new JPanel();
		this.infoPanel = new InformationPanel(pictureType, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_MESSAGE_PANEL_LABEL), message, true, true);
		this.actionPanel = new ActionPanel(1);
		createWindow();
	}

	@Override
	public void initComponents() {
		refreshActionPanelMessage();
		createContent();
	}
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.infoPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_BUTTON_LABEL));		
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_ACTION_PANEL_LABEL), messageButtonMap);
		this.actionPanel.addAction(0, closeAction());
	}
	
	/**
	 * Permet de fermer la fenêtre
	 * @return l'action listener
	 */
	private ActionListener closeAction() {
		return e -> closeFrame();
	}

	@Override
	public String getWindowName() {
		return "Window for user information";
	}

}
