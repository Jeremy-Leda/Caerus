package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ihm.abstracts.ModalJFrameAbstract;
import ihm.beans.ConsumerTextTypeEnum;
import ihm.beans.FunctionTextTypeEnum;
import ihm.beans.TextIhmTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IActionOnClose;
import ihm.interfaces.IActionPanel;
import ihm.interfaces.IContentTextGenericPanel;
import ihm.interfaces.IFilePanel;
import ihm.panel.ActionPanel;
import ihm.panel.ContentTextGenericPanel;
import ihm.panel.FilePanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/**
 * 
 * Permet de créer un texte
 * 
 * @author jerem
 *
 */
public class CreateText extends ModalJFrameAbstract {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8889159585361173713L;
	private static Logger logger = LoggerFactory.getLogger(CreateText.class);
	

	private final IFilePanel filePanel;
	private final IContentTextGenericPanel informationsTextPanel;
	private final IActionPanel actionPanel;
	private final JPanel content;
	private IActionOnClose fillSpecificTextFrame;
	
	public CreateText(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_TITLE), configurationControler, false);
		this.filePanel = new FilePanel();
		this.informationsTextPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JSCROLLPANE, ConsumerTextTypeEnum.CORPUS, FunctionTextTypeEnum.CORPUS);
		this.actionPanel = new ActionPanel(3);
		this.content = new JPanel();
		super.addActionOnClose(closeAutomaticallySpecificText());
		createWindow();
	}
	
	@Override
	public void initComponents() {
		this.informationsTextPanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_PANEL_TITLE));
		refreshFilePanel();
		updateContentInformationsTextPanel();
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
		content.add(this.informationsTextPanel.getJPanel());
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
	 * Met à jour le contenu de l'information text panel
	 */
	private void updateContentInformationsTextPanel() {
		this.informationsTextPanel.refreshComponents(getControler().getConfigurationFieldCommonFile());
		this.informationsTextPanel.addKeyListenerOnAllField(getKeyListenerForIncreaseRow());
	}
	
	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_FILL_SPECIFIC_BUTTON_TITLE));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_CREATE_TEXT_AND_ADD_TEXT_BUTTON_TITLE));
		messageButtonMap.put(2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_CREATE_TEXT_AND_QUIT_BUTTON_TITLE));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_PANEL_TITLE), messageButtonMap);
		this.actionPanel.addAction(0, openFillSpecificText());
		this.actionPanel.addAction(1, addTextAndCreateAnother());
		this.actionPanel.addAction(2, addTextSaveCorpusAndQuit());
	}
	
	/**
	 * Execute l'action d'ouvrir une fenêtre pour remplir les specific text
	 * @return
	 */
	private ActionListener openFillSpecificText() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// on désactive les bouton
				setEnabledForAllButton(false);
				// on ouvre la fenêtre
				fillSpecificTextFrame = new FillSpecificText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_SPECIFIC_TITLE), getControler());
				fillSpecificTextFrame.addActionOnClose(enableAllButton());
			}
		};
	}
	
	/**
	 * Consumer pour récativer les boutons
	 * @return consumer pour réactiver les boutons
	 */
	private Consumer<Void> enableAllButton() {
		return (v) -> setEnabledForAllButton(true);
	}
	
	/**
	 * Permet de gérer l'activation des boutons
	 * @param isEnabled Vrai si actif, faux sinon
	 */
	private void setEnabledForAllButton(Boolean isEnabled) {
		actionPanel.setEnabled(0, isEnabled);
		actionPanel.setEnabled(1, isEnabled);
		actionPanel.setEnabled(2, isEnabled);
	}
	
	/**
	 * Consumer pour rattacher la fermeture de la fenêtre fille si présente
	 * @return
	 */
	private Consumer<Void> closeAutomaticallySpecificText() {
		return (v) -> {
			if (null != fillSpecificTextFrame) {
				fillSpecificTextFrame.closeFrame();
			}
		};
	}
	
	/**
	 * Execute l'action d'ajouter un texte
	 * @return
	 */
	private ActionListener addTextSaveCorpusAndQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().addEditingTextToCurrentCorpus();
				try {
					getControler().writeCorpus();
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
					//TODO A modifier
					JOptionPane.showMessageDialog(null, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				closeFrame();
			}
			
		};
	}
	
	/**
	 * Execute l'action d'ajouter un texte
	 * @return
	 */
	private ActionListener addTextAndCreateAnother() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().addEditingTextToCurrentCorpus();
				informationsTextPanel.reloadValue();
			}
			
		};
	}
	
	/**
	 * Permet d'ajouter un listener pour augmenter dynamiquement la zone de saisie.
	 * @return
	 */
	public KeyListener getKeyListenerForIncreaseRow() {
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					JTextArea source = (JTextArea)e.getSource();
					if (source.getRows() < 5) {
						source.setRows(source.getRows()+1);
						repack();
					}					
				}
			}
		};
	}

	@Override
	public String getWindowName() {
		return "Window for created text";
	}

}
