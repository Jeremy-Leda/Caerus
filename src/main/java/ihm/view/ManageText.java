package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ihm.abstracts.ModalJFrameAbstract;
import ihm.beans.ExcelTypeGenerationEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IAccessPanel;
import ihm.interfaces.IActionPanel;
import ihm.panel.ActionPanel;
import ihm.panel.DisplayTextsFilteredWithPagingPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/**
 * 
 * Permet d'afficher la fenêtre de gestion des textes
 * 
 * @author jerem
 *
 */
public class ManageText extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6375903939438774209L;
	private final JPanel content;
	private final IAccessPanel displayTextsList;
	private final IActionPanel actionPanel;
	
	public ManageText(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_TITLE), configurationControler, false);
		this.displayTextsList = new DisplayTextsFilteredWithPagingPanel(configurationControler);
		this.actionPanel = new ActionPanel(2);
		this.content = new JPanel();
		refreshActionPanelMessage();
		createWindow();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.displayTextsList.getJPanel());
		content.add(this.actionPanel.getJPanel());
	}
	
	/**
	 * Permet de rafraichir l'affichage pour les bouttons
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_CLASSICAL_BUTTON_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_SPECIFIC_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_PANEL_TITLE), messageButtonMap);
		this.actionPanel.addAction(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveReferenceExcels(getControler(), ExcelTypeGenerationEnum.MANAGE_TEXTS);
			}
		});
		this.actionPanel.addAction(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveCustomExcel(getControler(), ExcelTypeGenerationEnum.MANAGE_TEXTS);
			}
		});
	}
	
	
	@Override
	public void initComponents() {
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	@Override
	public String getWindowName() {
		return "Window for manage texts from library";
	}

}
