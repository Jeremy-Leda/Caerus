package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.analyze.UserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.*;
import view.interfaces.*;
import view.panel.ActionPanel;
import view.panel.ContentTextGenericPanel;
import view.panel.LabelsPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/***
 * Fenêtre permettant de corriger les données vides ou d'éditer le corpus
 * 
 * @author jerem
 *
 */
public class FixedOrEditCorpus extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1660889209023320528L;
	private static Logger logger = LoggerFactory.getLogger(FixedOrEditCorpus.class);
	private final ILabelsPanel labelsPanel = new LabelsPanel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_FILE_PANEL_TITLE), 2);
	private final IContentTextGenericPanel informationsCorpusPanel;
	private final IActionPanel actionManagePanel;
	private final IActionPanel actionFixedPanel;
	private final JPanel content;
	private final ActionUserTypeEnum actionUserType;
	private IActionOnClose addTextPanel;

	public FixedOrEditCorpus(String title, IConfigurationControler configurationControler, Boolean isModal,
			ActionUserTypeEnum actionUserType) {
		super(title, configurationControler, isModal);
		this.actionUserType = actionUserType;
		this.informationsCorpusPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JTEXTFIELD,
				StateCorpusEnum.EDIT);
		this.actionFixedPanel = new ActionPanel(1);
		this.actionManagePanel = new ActionPanel(3);
		this.content = new JPanel();
		super.addActionOnClose(closeAutomaticallyAddText());
		updateContentWithNextError();
		createWindow();
	}

	@Override
	public void initComponents() {
		refreshLabelsPanel();
		refreshActionPanelMessage();
		addActionPanel();
		createContent();

	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.labelsPanel.getJPanel());
		content.add(this.informationsCorpusPanel.getJPanel());
		if (ActionUserTypeEnum.FOLDER_ANALYZE.equals(actionUserType)) {
			content.add(actionFixedPanel.getJPanel());
		} else if (ActionUserTypeEnum.FOLDER_TEXTS.equals(actionUserType)) {
			content.add(actionManagePanel.getJPanel());
		}
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Permet d'ajouter les actions au boutons
	 */
	private void addActionPanel() {
		this.actionFixedPanel.addAction(0, saveAndGoToNextOrQuit());
		this.actionManagePanel.addAction(0, openPanelForAddText());
		this.actionManagePanel.addAction(1, saveAndQuit());
		this.actionManagePanel.addAction(2, e -> closeFrame());
		this.actionFixedPanel.setIconButton(0, PictureTypeEnum.SAVE);
		this.actionManagePanel.setIconButton(1, PictureTypeEnum.SAVE);
	}

	/**
	 * Permet de sauvegarder un corpus et de quitter (édition)
	 * 
	 * @return
	 */
	private ActionListener saveAndQuit() {
		return e -> {
			getControler().applyEditText();
			try {
				getControler().writeEditText();
			} catch (IOException e1) {
				logger.error(e1.getMessage(), e1);
			}
			getControler().clearEditingCorpus();
			closeFrame();
		};
	}

	/**
	 * Permet d'ouvrir pour ajouter un texte
	 * 
	 * @return
	 */
	private ActionListener openPanelForAddText() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addTextPanel = new FixedOrEditText(
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_TITLE),
						getControler(), ActionUserTypeEnum.FOLDER_TEXTS, ActionOperationTypeEnum.ADD);
				addTextPanel.addActionOnClose(() -> {
					actionManagePanel.setEnabled(0, Boolean.TRUE);
					actionManagePanel.setEnabled(1, Boolean.TRUE);
					actionManagePanel.setEnabled(2, Boolean.TRUE);
				});
				actionManagePanel.setEnabled(0, Boolean.FALSE);
				actionManagePanel.setEnabled(1, Boolean.FALSE);
				actionManagePanel.setEnabled(2, Boolean.FALSE);
			}
		};
	}
	
	/**
	 * Consumer pour rattacher la fermeture de la fenêtre fille si présente
	 * 
	 * @return
	 */
	private Runnable closeAutomaticallyAddText() {
		return () -> {
			if (null != addTextPanel) {
				addTextPanel.closeFrame();
			}
		};
	}

	/**
	 * Permet de rafraichir les labels
	 */
	private void refreshLabelsPanel() {
		this.labelsPanel.setLabel(0, getMessage(Constants.WINDOW_CREATE_TEXT_NAME_LABEL), true, getControler().getEditingCorpusName(), true);
		UserSettings.getInstance().getNumberOfCurrentEditingFile().ifPresent(n ->
				this.labelsPanel.setLabel(1, getMessage(Constants.WINDOW_CREATE_TEXT_NUMBER_LABEL), true, n.toString(), true));
	}

	/**
	 * Met à jour le contenu de l'information corpus panel
	 */
	private void updateContentInformationsCorpusPanel() {
		this.informationsCorpusPanel.refresh(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_CORPUS_CONTENT_PANEL_TITLE));
		this.informationsCorpusPanel.refreshComponents(getControler().getConfigurationFieldMetaFile());
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		if (getControler().getNbMetaBlankLineToFixed() > 1) {
			messageButtonMap.put(0, ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_META_BLANK_LINE_PANEL_SAVE_NEXT_BUTTON_LABEL));
		} else {
			messageButtonMap.put(0, ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_META_BLANK_LINE_PANEL_SAVE_QUIT_BUTTON_LABEL));
		}
		this.actionFixedPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_PANEL_TITLE),
				messageButtonMap);

		messageButtonMap.clear();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_ADD_TEXT_ACTION_BUTTON_SAVE_AND_QUIT_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_SAVE_AND_QUIT_LABEL));
		messageButtonMap.put(2, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_QUIT_LABEL));
		this.actionManagePanel.setStaticLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_PANEL_TITLE), messageButtonMap);

	}

	/**
	 * Met à jour le contenu avec l'erreur suivante
	 */
	private void updateContentWithNextError() {
		if (ActionUserTypeEnum.FOLDER_ANALYZE.equals(actionUserType)) {
			getControler().loadNextErrorMetaBlankLine();
		}
		updateContentInformationsCorpusPanel();
		refreshLabelsPanel();
		refreshActionPanelMessage();
	}

	/**
	 * Execute l'action d'ajouter un texte
	 * 
	 * @return
	 */
	private ActionListener saveAndGoToNextOrQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().applyFixedErrorText();
				if (getControler().haveMetaBlankLineInErrorRemaining()) {
					updateContentWithNextError();
				} else {
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

	@Override
	public String getWindowName() {
		return "Window for fixed meta blank line";
	}

}
