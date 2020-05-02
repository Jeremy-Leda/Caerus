package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ihm.abstracts.ModalJFrameAbstract;
import ihm.beans.ConsumerTextTypeEnum;
import ihm.beans.FunctionTextTypeEnum;
import ihm.beans.TextIhmTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IActionPanel;
import ihm.interfaces.IContentTextGenericPanel;
import ihm.interfaces.IFilePanel;
import ihm.panel.ActionPanel;
import ihm.panel.ContentTextGenericPanel;
import ihm.panel.FilePanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/***
 * Fenêtre permettant de corriger les méta données vides des textes
 * 
 * @author jerem
 *
 */
public class FixedMetaBlankLine extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1660889209023320528L;
	private static Logger logger = LoggerFactory.getLogger(FixedMetaBlankLine.class);

	private final IFilePanel filePanel;
	private final IContentTextGenericPanel informationsCorpusPanel;
	private final IActionPanel actionPanel;
	private final JPanel content;

	public FixedMetaBlankLine(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_META_BLANK_LINE_PANEL_TITLE), configurationControler);
		this.filePanel = new FilePanel();
		this.informationsCorpusPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JTEXTFIELD, ConsumerTextTypeEnum.CORPUS, FunctionTextTypeEnum.CORPUS);
		this.actionPanel = new ActionPanel(1);
		this.content = new JPanel();
		updateContentWithNextError();
		createWindow();
	}
	

	@Override
	public void initComponents() {
		refreshFilePanel();
		refreshActionPanelMessage();
		createContent();
	}
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePanel.getJPanel());
		content.add(this.informationsCorpusPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}


	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Permet de rafraichir le file panel
	 */
	private void refreshFilePanel() {
		this.filePanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_FILE_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_NAME_LABEL), getControler().getEditingCorpusName());
	}
	
	/**
	 * Met à jour le contenu de l'information corpus panel
	 */
	private void updateContentInformationsCorpusPanel() {
		this.informationsCorpusPanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_CONTENT_PANEL_TITLE));
		this.informationsCorpusPanel.refreshComponents(getControler().getConfigurationFieldMetaFile());
	}
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_META_BLANK_LINE_PANEL_SAVE_QUIT_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_PANEL_TITLE), messageButtonMap);
		this.actionPanel.addAction(0, saveAndQuit());
	}
	
	/**
	 * Met à jour le contenu avec l'erreur suivante
	 */
	private void updateContentWithNextError() {
		getControler().loadNextErrorMetaBlankLine();
		updateContentInformationsCorpusPanel();
	}
	
	
	/**
	 * Execute l'action d'ajouter un texte
	 * 
	 * @return
	 */
	private ActionListener saveAndQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
					getControler().applyFixedErrorText();
					try {
						getControler().writeFixedText();
					} catch (IOException e1) {
						logger.error(e1.getMessage(), e1);
					}
					closeFrame();
			}

		};
	}
	
	@Override
	public String getWindowName() {
		return "Window for fixed meta blank line";
	}

}
