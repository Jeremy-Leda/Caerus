package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ihm.abstracts.ModalJFrameAbstract;
import ihm.beans.ConsumerTextTypeEnum;
import ihm.beans.FunctionTextTypeEnum;
import ihm.beans.TextIhmTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IActionPanel;
import ihm.interfaces.IContentTextGenericPanel;
import ihm.panel.ActionPanel;
import ihm.panel.ContentTextGenericPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;


/**
 * 
 * Permet de créer la fenêtre de création du corpus
 * 
 * @author jerem
 *
 */
public class CreateCorpus extends ModalJFrameAbstract {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8889159585361173713L;
	private static Logger logger = LoggerFactory.getLogger(CreateCorpus.class);
	

	private final IContentTextGenericPanel filePanel;
	private final IContentTextGenericPanel informationsCorpusPanel;
	private final IActionPanel actionPanel;
	private final JPanel content;
	private final Map<String, String> mapFilePanel;
	private static final String KEY_FILE = "FILE";
	
	
	public CreateCorpus(IConfigurationControler controler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_TITLE), controler);
		this.filePanel = new ContentTextGenericPanel(controler, TextIhmTypeEnum.JTEXTFIELD, ConsumerTextTypeEnum.NULL, FunctionTextTypeEnum.NULL);
		this.informationsCorpusPanel = new ContentTextGenericPanel(controler, TextIhmTypeEnum.JTEXTFIELD, ConsumerTextTypeEnum.NULL, FunctionTextTypeEnum.NULL);
		this.actionPanel = new ActionPanel(1);
		this.content = new JPanel();
		this.mapFilePanel = new HashMap<>();
		this.mapFilePanel.put(KEY_FILE, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_NAME_LABEL));
		createWindow();
	}

	@Override
	public void initComponents() {
		this.filePanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_FILE_PANEL_TITLE));
		this.informationsCorpusPanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_CONTENT_PANEL_TITLE));
		updateContentFilePanel();
		updateContentInformationsCorpusPanel();
		refreshActionPanelMessage();
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
		content.add(this.informationsCorpusPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}
	
	/**
	 * Met à jour le contenu du filePanel
	 */
	private void updateContentFilePanel() {
		this.filePanel.refreshComponents(this.mapFilePanel);
		this.filePanel.addKeyListenerOnAllField(getFileKeyListener());
	}
	
	/**
	 * Met à jour le contenu de l'information corpus panel
	 */
	private void updateContentInformationsCorpusPanel() {
		this.informationsCorpusPanel.refreshComponents(getControler().getConfigurationFieldMetaFile());
		this.informationsCorpusPanel.setEnabledOnAllField(false);
	}
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_ACTION_CREATE_TEXT_BUTTON_TITLE));		
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_ACTION_PANEL_TITLE), messageButtonMap);
		this.actionPanel.setEnabled(0, false);
		this.actionPanel.addAction(0, addText());
	}
	
	/**
	 * Permet de se procurer le key listener pour le champ File
	 * @return le key listener
	 */	
	private KeyListener getFileKeyListener() {
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				Boolean state = StringUtils.isNotBlank(((JTextField)e.getSource()).getText());
				actionPanel.setEnabled(0, state);
				informationsCorpusPanel.setEnabledOnAllField(state);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}
	
	
	/**
	 * Execute l'action d'ajouter un texte
	 * @return
	 */
	private ActionListener addText() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logger.debug("CALL NEW CORPUS");
				String corpusName = filePanel.getValue(KEY_FILE);
				getControler().createNewCorpus(corpusName, informationsCorpusPanel.getFieldValueMap());
				logger.debug("CLOSE CREATE CORPUS");
				closeFrame();
			}
			
		};
	}

	@Override
	public String getWindowName() {
		return "Window for created corpus";
	}
}
