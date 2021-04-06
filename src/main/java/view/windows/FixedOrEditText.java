package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ActionOperationTypeEnum;
import view.beans.ActionUserTypeEnum;
import view.beans.ConsumerTextTypeEnum;
import view.beans.DirectionTypeEnum;
import view.beans.FunctionTextTypeEnum;
import view.beans.PictureTypeEnum;
import view.beans.TextIhmTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IActionPanel;
import view.interfaces.IContentTextGenericPanel;
import view.interfaces.IFilePanel;
import view.panel.ActionPanel;
import view.panel.ContentTextGenericPanel;
import view.panel.FilePanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * Permet de corriger ou d'éditer un texte, ou d'ajouter un text dans le cadre
 * de la consultation d'un corpus
 * 
 * @author jerem
 *
 */
public class FixedOrEditText extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8889159585361173713L;
	private static Logger logger = LoggerFactory.getLogger(FixedOrEditText.class);
	private final IFilePanel filePanel;
	private final IContentTextGenericPanel informationsTextPanel;
	private final IActionPanel actionFixedTextPanel;
	private final IActionPanel actionManageTextPanel;
	private final IActionPanel actionAddTextPanel;
	private final IActionPanel actionNavigationPanel;
	private Integer currentIndex;
	private final JPanel content;
	private IActionOnClose fillSpecificTextFrame;
	private final ActionUserTypeEnum actionUserType;
	private final ActionOperationTypeEnum actionOperationType;
	private Integer scrollBarPosition;
	private Boolean isMaximumScrollbar;

	public FixedOrEditText(String title, IConfigurationControler configurationControler,
			ActionUserTypeEnum actionUserType, ActionOperationTypeEnum actionOperationType) {
		super(title, configurationControler, false);
		this.actionUserType = actionUserType;
		this.actionOperationType = actionOperationType;
		this.filePanel = new FilePanel();
		this.isMaximumScrollbar = Boolean.FALSE;
		this.informationsTextPanel = new ContentTextGenericPanel(configurationControler, TextIhmTypeEnum.JSCROLLPANE,
				ConsumerTextTypeEnum.CORPUS, FunctionTextTypeEnum.CORPUS);
		this.actionFixedTextPanel = new ActionPanel(2);
		this.actionManageTextPanel = new ActionPanel(3);
		this.actionAddTextPanel = new ActionPanel(3);
		this.actionNavigationPanel = new ActionPanel(2);
		this.content = new JPanel();
		this.currentIndex = 0;
		this.scrollBarPosition = 0;
		super.addActionOnClose(closeAutomaticallySpecificText());
		updateContentInformationsTextPanel();
		createWindow();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePanel.getJPanel());
		JScrollPane scrollPane = new JScrollPane(this.informationsTextPanel.getJPanel());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (e.getValueIsAdjusting()) {
					scrollBarPosition = e.getAdjustable().getValue();
					isMaximumScrollbar = e.getAdjustable().getMaximum() == (scrollBarPosition + scrollPane.getVerticalScrollBar().getModel().getExtent());
				} else {
					if (isMaximumScrollbar) {
						e.getAdjustable().setValue(e.getAdjustable().getMaximum());
					} else {
						e.getAdjustable().setValue(scrollBarPosition);
					}
				}

			}
		});
		content.add(scrollPane);
		if (ActionUserTypeEnum.FOLDER_ANALYZE.equals(actionUserType)) {
			content.add(actionFixedTextPanel.getJPanel());
		} else if (ActionUserTypeEnum.FOLDER_TEXTS.equals(actionUserType)) {
			if (ActionOperationTypeEnum.ADD.equals(actionOperationType)) {
				content.add(actionAddTextPanel.getJPanel());
			} else if (ActionOperationTypeEnum.EDIT.equals(actionOperationType)) {
				content.add(actionManageTextPanel.getJPanel());
				content.add(actionNavigationPanel.getJPanel());
				updateEnableNavigationPanel();
			}
		}
	}

	@Override
	public void initComponents() {
		this.informationsTextPanel.refresh(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_CONTENT_PANEL_TITLE));
		this.informationsTextPanel.setRefreshDisplayConsumer(s -> repack());
		refreshFilePanel();
		addActionPanelMessage();
		displayMessageForAction();
		displayIconIfHaveErrorInSpecific();
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Permet de rafraichir le file panel
	 */
	private void refreshFilePanel() {
		this.filePanel.refresh(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_FILE_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_CREATE_TEXT_NAME_LABEL),
				getControler().getEditingCorpusName());
	}

	/**
	 * Met à jour le contenu de l'information text panel
	 */
	private void updateContentInformationsTextPanel() {
		if (ActionUserTypeEnum.FOLDER_ANALYZE.equals(actionUserType)) {
			getControler().loadNextErrorText();
		} else if (ActionOperationTypeEnum.ADD.equals(actionOperationType)) {
			getControler().cleanCurrentEditingCorpusForAddText();
		}
		this.informationsTextPanel.refreshComponents(getControler().getConfigurationFieldCommonFile());
		this.informationsTextPanel.addKeyListenerOnAllField(getKeyListenerForIncreaseRow());
		displayIconIfHaveErrorInSpecific();
		refreshFilePanel();
	}

	/**
	 * Permet d'ajouter les actions au panel
	 */
	private void addActionPanelMessage() {
		Boolean isEnabledSpecific = getControler().getConfigurationSpecificLabelNameFileMap().size() > 0;
		this.actionFixedTextPanel.setEnabled(0, isEnabledSpecific);
		this.actionFixedTextPanel.addAction(0, openFixedSpecificText(Constants.WINDOW_FIXED_SPECIFIC_TITLE));
		this.actionFixedTextPanel.addAction(1, saveAndGoToNextIndexOrQuit());
		this.actionManageTextPanel.setEnabled(0, isEnabledSpecific);
		this.actionManageTextPanel.addAction(0, openFixedSpecificText(Constants.WINDOW_EDIT_SPECIFIC_TITLE));
		this.actionManageTextPanel.addAction(1, save());
		this.actionManageTextPanel.addAction(2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		this.actionAddTextPanel.setEnabled(0, isEnabledSpecific);
		this.actionAddTextPanel.addAction(0, openFixedSpecificText(Constants.WINDOW_CREATE_SPECIFIC_TITLE));
		this.actionAddTextPanel.addAction(1, addTextAndQuit());
		this.actionAddTextPanel.addAction(2, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		this.actionNavigationPanel.addAction(0, changeText(DirectionTypeEnum.PREVIOUS));
		this.actionNavigationPanel.addAction(1, changeText(DirectionTypeEnum.NEXT));
		this.actionFixedTextPanel.setIconButton(1, PictureTypeEnum.SAVE);
		this.actionManageTextPanel.setIconButton(1, PictureTypeEnum.SAVE);
		this.actionAddTextPanel.setIconButton(1, PictureTypeEnum.SAVE);
	}

	/**
	 * Permet d'afficher une icone de warning s'il y a une erreur dans les
	 * structures spécifiques
	 */
	private void displayIconIfHaveErrorInSpecific() {
		if (ActionUserTypeEnum.FOLDER_ANALYZE.equals(actionUserType)) {
			if (getControler().haveErrorInSpecificFieldInEditingCorpus()) {
				this.actionFixedTextPanel.setIconButton(0, PictureTypeEnum.WARNING);
			} else {
				this.actionFixedTextPanel.setIconButton(0, null);
			}
		}
	}

	/**
	 * Permet de gérer l'activation des boutons
	 * 
	 * @param isEnabled Vrai si actif, faux sinon
	 */
	private void setEnabledForAllButton(Boolean isEnabled) {
		actionFixedTextPanel.setEnabled(0, isEnabled);
		actionFixedTextPanel.setEnabled(1, isEnabled);
		actionAddTextPanel.setEnabled(0, isEnabled);
		actionAddTextPanel.setEnabled(1, isEnabled);
		actionAddTextPanel.setEnabled(2, isEnabled);
		actionManageTextPanel.setEnabled(0, isEnabled);
		actionManageTextPanel.setEnabled(1, isEnabled);
		actionManageTextPanel.setEnabled(2, isEnabled);
	}

	/**
	 * Consumer pour rattacher la fermeture de la fenêtre fille si présente
	 * 
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
	 * Permet d'afficher les messages pour les boutons actions
	 */
	private void displayMessageForAction() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_FILL_SPECIFIC_BUTTON_TITLE));
		Integer nbTextsError = getControler().getNbTextsError();
		if (currentIndex + 1 == nbTextsError) {
			messageButtonMap.put(1, ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_NEXT_AND_SAVE_BUTTON_TITLE));
		} else {
			messageButtonMap.put(1, ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_FIXED_TEXT_ACTION_NEXT_BUTTON_TITLE));
		}
		this.actionFixedTextPanel.setStaticLabel(
				String.format(ConfigurationUtils.getInstance().getDisplayMessage(
						Constants.WINDOW_FIXED_TEXT_ACTION_PANEL_TITLE), currentIndex + 1, nbTextsError),
				messageButtonMap);
		messageButtonMap.clear();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_FILL_SPECIFIC_BUTTON_TITLE));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_SAVE_LABEL));
		messageButtonMap.put(2, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_QUIT_LABEL));
		this.actionManageTextPanel.setStaticLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_PANEL_TITLE), messageButtonMap);
		messageButtonMap.clear();

		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_CREATE_TEXT_ACTION_FILL_SPECIFIC_BUTTON_TITLE));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_SAVE_AND_QUIT_LABEL));
		messageButtonMap.put(2, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_BUTTON_QUIT_LABEL));
		this.actionAddTextPanel.setStaticLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_ACTION_PANEL_TITLE), messageButtonMap);

		messageButtonMap.clear();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_PREVIOUS_BUTTON_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_NEXT_BUTTON_LABEL));
		this.actionNavigationPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_NAVIGATION_PANEL_TITLE),
				messageButtonMap);
	}

	/**
	 * Permet de mettre à jour les boutons de navigation
	 */
	private void updateEnableNavigationPanel() {
		actionNavigationPanel.setEnabled(0, getControler().haveTextInFilteredText(DirectionTypeEnum.PREVIOUS));
		actionNavigationPanel.setEnabled(1, getControler().haveTextInFilteredText(DirectionTypeEnum.NEXT));
	}

	/**
	 * Permet de changer de texte
	 * 
	 * @param direction sens de navigation
	 */
	private ActionListener changeText(DirectionTypeEnum direction) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().loadFilteredText(direction);
				updateContentInformationsTextPanel();
				updateEnableNavigationPanel();
				repack();
			}
		};
	}

	/**
	 * Permet de sauvegarder un texte et de quitter (édition)
	 * 
	 * @return
	 */
	private ActionListener save() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().applyEditText();
				try {
					getControler().writeEditText();
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
				new UserInformation(
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_INFORMATION_PANEL_LABEL),
						getControler(), PictureTypeEnum.INFORMATION,
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MESSAGE_SAVE));
			}
		};
	}

	/**
	 * Permet d'ajouter un texte et de quitter (édition du corpus)
	 * 
	 * @return
	 */
	private ActionListener addTextAndQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().addTextToCurrentCorpusFromFolderText();
				try {
					getControler().writeEditText();
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
				getControler().clearEditingCorpus();
				closeFrame();
			}
		};
	}

	/**
	 * Execute l'action de sauvegarder un texte (correction) et de passer au suivant
	 * ou de fermer la fenêtre
	 * 
	 * @return
	 */
	private ActionListener saveAndGoToNextIndexOrQuit() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentIndex + 1 < getControler().getNbTextsError()) {
					getControler().applyFixedErrorText();
					getControler().saveCurrentStateOfFixedText();
					currentIndex++;
					displayMessageForAction();
					updateContentInformationsTextPanel();
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
	 * Permet d'effectuer les actions nécessaires sur la fermeture de fenêtre des
	 * spécifiques
	 * 
	 * @return le consumer
	 */
	private Consumer<?> actionOnCloseSpecificFrame() {
		return v -> {
			setEnabledForAllButton(true);
			displayIconIfHaveErrorInSpecific();
		};
	}

	/**
	 * Execute l'action d'ajouter un texte
	 * 
	 * @param Titre de la fenêtre
	 * @return
	 */
	private ActionListener openFixedSpecificText(String title) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// on désactive les bouton
				setEnabledForAllButton(false);
				// on ouvre la fenêtre
				fillSpecificTextFrame = new FillSpecificText(ConfigurationUtils.getInstance().getDisplayMessage(title),
						getControler());
				fillSpecificTextFrame.addActionOnClose(actionOnCloseSpecificFrame());
			}
		};
	}

	/**
	 * Permet d'ajouter un listener pour augmenter dynamiquement la zone de saisie.
	 * 
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
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					JTextArea source = (JTextArea) e.getSource();
					if (source.getRows() < 5) {
						source.setRows(source.getRows() + 1);
						repack();
					}
				}
			}
		};
	}

	@Override
	public String getWindowName() {
		return "Window for fixed text";
	}

}
