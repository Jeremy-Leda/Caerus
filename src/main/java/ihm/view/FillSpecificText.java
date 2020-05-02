package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ihm.abstracts.ModalJFrameAbstract;
import ihm.beans.ImageTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IAccessPanel;
import ihm.interfaces.IActionPanel;
import ihm.interfaces.IFilePanel;
import ihm.interfaces.ISpecificTextModel;
import ihm.panel.ActionPanel;
import ihm.panel.FilePanel;
import ihm.panel.InformationPanel;
import ihm.panel.SpecificControlAndListPanel;
import ihm.panel.model.SpecificTextModel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/**
 * 
 * Classe permettant de remplir les informations spécifique pour l'utilisateur
 * 
 * @author jerem
 *
 */
public class FillSpecificText extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8494314347827794576L;
	private final ISpecificTextModel specificTextModel;
	private final IFilePanel filePanel;
	private final IAccessPanel specificContentPanel;
	private final IActionPanel actionPanel;
	private final IAccessPanel textInformationPanel;
	private final IAccessPanel textWarningPanel;
	private final JPanel content;
	
	public FillSpecificText(String title, IConfigurationControler configurationControler) {
		super(title, configurationControler, false);
		this.specificTextModel = new SpecificTextModel(configurationControler, this);
		this.filePanel = new FilePanel();
		this.actionPanel = new ActionPanel(3);
		this.textInformationPanel = new InformationPanel(ImageTypeEnum.INFORMATION, 
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SPECIFIC_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SPECIFIC_INFORMATION_PANEL_TEXT), false);
		this.textWarningPanel = new InformationPanel(ImageTypeEnum.WARNING, 
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SPECIFIC_WARNING_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SPECIFIC_WARNING_PANEL_TEXT), false);
		this.specificTextModel.addSpecificTextRefresh(this.actionPanel);
		this.specificContentPanel = new SpecificControlAndListPanel(this.specificTextModel);
		this.specificTextModel.addRefreshConsumerOnLoadAllField(refreshDisplayWarning());
		this.content = new JPanel();
		createWindow();
	}
	
	@Override
	public void initComponents() {
		refreshFilePanel();
		addActionListenerToActionPanel();
		initLabelActionPanel();
		this.specificTextModel.loadAllField(0);
		this.actionPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
		this.actionPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}
	
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePanel.getJPanel());
		content.add(this.textInformationPanel.getJPanel());
		content.add(this.textWarningPanel.getJPanel());
		content.add(this.specificContentPanel.getJPanel());
		content.add(this.actionPanel.getJPanel());
	}
	

	/**
	 * Permet d'ajouter les actions au panel d'action
	 */
	private void addActionListenerToActionPanel() {
		this.actionPanel.addAction(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				specificTextModel.loadAllField(specificTextModel.getCurrentIndex()-1);
				actionPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
				actionPanel.setEnabled(1, specificTextModel.haveNextSpecificConfiguration());
				actionPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
			}
		});
		this.actionPanel.addAction(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				specificTextModel.loadAllField(specificTextModel.getCurrentIndex()+1);
				actionPanel.setEnabled(0, specificTextModel.havePreviousSpecificConfiguration());
				actionPanel.setEnabled(1, specificTextModel.haveNextSpecificConfiguration());
				actionPanel.setEnabled(2, !specificTextModel.haveNextSpecificConfiguration());
			}
		});
		this.actionPanel.addAction(2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
	}
	
	/**
	 * Consumer permettant l'affichage du warning
	 * @return le consumer
	 */
	private Consumer<Void> refreshDisplayWarning() {
		return (v) -> {
			this.textWarningPanel.getJPanel().setVisible(this.specificTextModel.haveErrorStructuredInCurrentIndex());
			repack();
		};
	}
	
	/**
	 * Permet de rafraichir le file panel
	 */
	private void refreshFilePanel() {
		this.filePanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CONTEXT_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_CONTEXT_PANEL_FILE_LABEL), getControler().getEditingCorpusName());
	}
	
	/**
	 * Permet de rafraichir les boutons
	 */
	private void initLabelActionPanel() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_ACTION_PANEL_BUTTON_PREVIOUS_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_ACTION_PANEL_BUTTON_NEXT_LABEL));
		messageButtonMap.put(2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_ACTION_PANEL_BUTTON_FINISH_LABEL));
		this.actionPanel.setStaticLabel(null, messageButtonMap);
		this.actionPanel.setFunctionRefreshLabelTitleDynamically(getFunctionTitleJPanelActionRefresh());
	}
	
	/**
	 * Permet de se produire la fonction pour la mise à jour du titre du Jpanel Action
	 * @return la fonction
	 */
	private Function<Void, String> getFunctionTitleJPanelActionRefresh() {
		StringBuilder sb = new StringBuilder(getControler().getConfigurationName());
		sb.append(" %d / %d");
		return (v) -> String.format(sb.toString(),
				this.specificTextModel.getCurrentIndex() + 1, this.specificTextModel.getNbMaxConfiguration());
	}
	
	@Override
	public String getWindowName() {
		return "Window for fill specific informations";
	}
	
}
