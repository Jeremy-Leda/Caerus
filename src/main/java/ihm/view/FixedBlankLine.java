package ihm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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

public class FixedBlankLine extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9090854768642772256L;
	private static Logger logger = LoggerFactory.getLogger(FixedBlankLine.class);

	private Integer currentIndex;

	private final IFilePanel filePanel;
	private final IActionPanel actionPanel;
	private final IContentTextGenericPanel contentPanel;
	private final JPanel content;
	private IActionOnClose fillSpecificText;

	public FixedBlankLine(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_TEXT_TITLE), configurationControler, false);
		this.currentIndex = 0;
		this.filePanel = new FilePanel();
		this.actionPanel = new ActionPanel(2);
		this.contentPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JSCROLLPANE, ConsumerTextTypeEnum.CORPUS, FunctionTextTypeEnum.CORPUS);
		this.content = new JPanel();
		super.addActionOnClose(closeAutomaticallySpecificText());
		updateContentWithNextError();
		addActionListenerToActionPanel();
		createWindow();
	}

	@Override
	public void initComponents() {
		this.contentPanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_CONTENT_PANEL_TITLE));
		refreshFilePanel();
		refreshActionPanelMessage();
		createContent();
	}

	/**
	 * Permet de cr�er le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePanel.getJPanel());
		JScrollPane scrollPane = new JScrollPane(contentPanel.getJPanel());
		content.add(scrollPane);
		content.add(actionPanel.getJPanel());
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Execute l'action d'ajouter un texte
	 * 
	 * @return
	 */
	private ActionListener saveAndGoToNextIndexOrQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentIndex + 1 < getControler().getNbBlankLinesError()) {
					getControler().applyFixedErrorText();
					getControler().saveCurrentStateOfFixedText();
					currentIndex++;
					updateContentWithNextError();
					refreshActionPanelMessage();
					repack();
				} else {
					getControler().applyFixedErrorText();
					try {
						getControler().writeFixedText();
					} catch (IOException e1) {
						logger.error(e1.getMessage(), e1);
					}
					closeFrame();
				}
			}

		};
	}

	/**
	 * Permet de rafraichir le file panel
	 */
	private void refreshFilePanel() {
		this.filePanel.refresh(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_FILE_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_NAME_LABEL), getControler().getEditingCorpusName());
	}

	/**
	 * Met � jour le contenu avec l'erreur suivante
	 */
	private void updateContentWithNextError() {
		getControler().loadNextErrorBlankLine();
		this.contentPanel.refreshComponents(getControler().getConfigurationFieldCommonFile());
		refreshFilePanel();
	}

	/**
	 * Permet d'ajouter les actions au panel d'action
	 */
	private void addActionListenerToActionPanel() {
		this.actionPanel.addAction(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fillSpecificText = new FillSpecificText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_SPECIFIC_TITLE), getControler());
				actionPanel.setEnabled(0, Boolean.FALSE);
				fillSpecificText.addActionOnClose(v -> {
					actionPanel.setEnabled(0, Boolean.TRUE);
				});
			}
		});
		this.actionPanel.addAction(1, saveAndGoToNextIndexOrQuit());
	}
	
	/**
	 * Consumer pour rattacher la fermeture de la fen�tre fille si pr�sente
	 * @return
	 */
	private Consumer<Void> closeAutomaticallySpecificText() {
		return (v) -> {
			if (null != fillSpecificText) {
				fillSpecificText.closeFrame();
			}
		};
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_FILL_SPECIFIC_BUTTON_TITLE));
		Integer nbTextsError = getControler().getNbBlankLinesError();
		if (currentIndex + 1 == nbTextsError) {
			messageButtonMap.put(1,
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_NEXT_AND_SAVE_BUTTON_TITLE));
		} else {
			messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_NEXT_BUTTON_TITLE));
		}
		String title = String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_PANEL_TITLE),
				currentIndex + 1, nbTextsError);
		this.actionPanel.setStaticLabel(title, messageButtonMap);
	}

	@Override
	public String getWindowName() {
		return "Window for fixed blank line";
	}
}
