package view.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.beans.ActionOperationTypeEnum;
import view.beans.ActionUserTypeEnum;
import view.beans.DirectionTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IManageTextDisplayPanel;
import view.interfaces.IRefreshTextDisplayPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.FixedOrEditCorpus;
import view.windows.FixedOrEditText;

/**
 * 
 * Panel permettant d'afficher les textes avec une pagination
 * 
 * @author jerem
 *
 */
public class DisplayTextsFilteredWithPagingPanel implements IManageTextDisplayPanel {

	private static Logger logger = LoggerFactory.getLogger(DisplayTextsFilteredWithPagingPanel.class);
	private final JPanel content;
	private final JLabel currentPositionLabel;
	private final JComboBox<Integer> nbTextsByPageCombo;
	private final IRefreshTextDisplayPanel textDisplayPanel;
	private final JButton previousButton;
	private final JButton nextButton;
	private final JButton editButtonCorpus;
	private final JButton editButtonText;
	private final JButton deleteButton;
	private final List<Integer> listNbTextsByPage;
	private final IConfigurationControler controler;
	private IActionOnClose fixedOrEditTextPanel;
	private IActionOnClose fixedOrEditCorpusPanel;
	private Boolean forcedDisabledButton;
	private Consumer<Void> consumerOnOpenEditText;
	private Consumer<Void> consumerOnCloseEditText;

	public DisplayTextsFilteredWithPagingPanel(IConfigurationControler controler) {
		this.controler = controler;
		this.content = new JPanel();
		this.content.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_PANEL_LABEL)));
		this.currentPositionLabel = new JLabel();
		this.nbTextsByPageCombo = new JComboBox<>();
		this.textDisplayPanel = new DisplayTextsFilteredPanel(controler, null, getConsumerToChangeItem());
		this.previousButton = new JButton();
		this.nextButton = new JButton();
		this.editButtonCorpus = new JButton();
		this.editButtonText = new JButton();
		this.deleteButton = new JButton();
		this.forcedDisabledButton = Boolean.FALSE;
		this.listNbTextsByPage = Arrays.asList(10, 20, 50);
		initComponentsAndCreateContent();
	}

	/**
	 * Permet d'initialiser les composants et de créer le contenu
	 */
	private void initComponentsAndCreateContent() {
		fillAndConfigureNbTextsByPageCombo();
		textDisplayPanel.setNbTextByPage((Integer) this.nbTextsByPageCombo.getSelectedItem());
		configureActionButtons();
		changeEnabledStateOfDirectionButton();
		refreshCurrentPosition();
		createContent();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(this.content, BoxLayout.Y_AXIS);
		this.content.setLayout(boxlayout);
		this.content.add(getnbTextsAndButtonTextPanel());
		JPanel panelCurrentPosition = new JPanel();
		panelCurrentPosition.add(this.currentPositionLabel);
		this.content.add(panelCurrentPosition);
		this.content.add(this.textDisplayPanel.getJPanel());
		this.content.add(getButtonsPanel());
	}

	/**
	 * Permet de se procurer le panel pour le nb de textes (combobox)
	 * 
	 * @return le panel
	 */
	private JPanel getnbTextsAndButtonTextPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_NB_TEXTS_BY_PAGE_LABEL));
		this.editButtonCorpus.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_CORPUS_EDIT_BUTTON_LABEL));
		this.editButtonText.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_EDIT_BUTTON_LABEL));
		this.deleteButton.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_DELETE_BUTTON_LABEL));
		panel.add(label);
		panel.add(this.nbTextsByPageCombo);
		panel.add(this.editButtonCorpus);
		panel.add(this.editButtonText);
		panel.add(this.deleteButton);
		return panel;
	}

	/**
	 * Permet de se procurer le panel avec les bouttons
	 * 
	 * @return le panel
	 */
	private JPanel getButtonsPanel() {
		JPanel panel = new JPanel();
		this.previousButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_PREVIOUS_BUTTON_LABEL));
		this.nextButton.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_NEXT_BUTTON_LABEL));
		panel.add(this.previousButton);
		panel.add(this.nextButton);
		return panel;
	}

	/**
	 * Permet de rafraichir la position courante
	 */
	private void refreshCurrentPosition() {
		this.currentPositionLabel.setText(String.format(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_CURRENT_POSITION_LABEL),
				textDisplayPanel.getCurrentPage(), textDisplayPanel.getMaxPage()));
	}

	@Override
	public void refresh() {
		this.textDisplayPanel.refresh();
		refreshCurrentPosition();
		changeEnabledStateOfDirectionButton();
	}

	/**
	 * Permet de remplir et de configurer la combo box pour le nombre de textes par
	 * pages
	 */
	private void fillAndConfigureNbTextsByPageCombo() {
		this.listNbTextsByPage.stream().sorted().forEach(nbText -> this.nbTextsByPageCombo.addItem(nbText));
		this.nbTextsByPageCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				textDisplayPanel.setNbTextByPage((Integer) e.getItem());
				changeEnabledStateOfDirectionButton();
				refreshCurrentPosition();
			}
		});
		this.nbTextsByPageCombo.setSelectedIndex(0);
	}

	/**
	 * Permet de changer l'état des boutons
	 */
	private void changeEnabledStateOfDirectionButton() {
		this.previousButton.setEnabled(this.textDisplayPanel.isEnabled(DirectionTypeEnum.PREVIOUS));
		this.nextButton.setEnabled(this.textDisplayPanel.isEnabled(DirectionTypeEnum.NEXT));
	}

	/**
	 * Permet de se procurer le consumer pour le changement des textes selectionné
	 * 
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerToChangeItem() {
		return v -> {
			if (forcedDisabledButton) {
				editButtonText.setEnabled(Boolean.FALSE);
				deleteButton.setEnabled(Boolean.FALSE);
				editButtonCorpus.setEnabled(Boolean.FALSE);
			} else {
				Boolean haveTextSelected = null != textDisplayPanel.getDisplayTextSelected();
				editButtonText.setEnabled(haveTextSelected);
				deleteButton.setEnabled(haveTextSelected);
				editButtonCorpus.setEnabled(haveTextSelected);
			}
		};
	}

	/**
	 * Permet de configurer les boutons
	 */
	private void configureActionButtons() {
		this.editButtonText.setEnabled(Boolean.FALSE);
		this.deleteButton.setEnabled(Boolean.FALSE);
		this.editButtonCorpus.setEnabled(Boolean.FALSE);
		this.previousButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textDisplayPanel.changePage(DirectionTypeEnum.PREVIOUS);
				changeEnabledStateOfDirectionButton();
				refreshCurrentPosition();
			}
		});
		this.nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textDisplayPanel.changePage(DirectionTypeEnum.NEXT);
				changeEnabledStateOfDirectionButton();
				refreshCurrentPosition();
			}
		});
		this.editButtonText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controler.loadFilteredText(textDisplayPanel.getDisplayTextSelected().getStructuredKey());
				fixedOrEditTextPanel = new FixedOrEditText(
						ConfigurationUtils.getInstance()
								.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EDIT_TEXT_PANEL_TITLE),
						controler, ActionUserTypeEnum.FOLDER_TEXTS, ActionOperationTypeEnum.EDIT);
				setEnabledAllButton(false);
				if (null != consumerOnOpenEditText) {
					consumerOnOpenEditText.accept(null);
				}
				fixedOrEditTextPanel.addActionOnClose(() -> {
					fixedOrEditTextPanel = null;
					setEnabledAllButton(true);
					if (null != consumerOnCloseEditText) {
						consumerOnCloseEditText.accept(null);
					}
					textDisplayPanel.refresh();
				});
			}
		});

		this.editButtonCorpus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controler.loadFilteredText(textDisplayPanel.getDisplayTextSelected().getStructuredKey());
				fixedOrEditCorpusPanel = new FixedOrEditCorpus(
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_CORPUS_TITLE),
						controler, false, ActionUserTypeEnum.FOLDER_TEXTS);
				setEnabledAllButton(false);
				if (null != consumerOnOpenEditText) {
					consumerOnOpenEditText.accept(null);
				}
				fixedOrEditCorpusPanel.addActionOnClose(() -> {
					fixedOrEditCorpusPanel = null;
					setEnabledAllButton(true);
					if (null != consumerOnCloseEditText) {
						consumerOnCloseEditText.accept(null);
					}
					textDisplayPanel.refresh();
				});
			}
		});

		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// on demande confirmation avant la suppression
				Integer result = JOptionPane.showConfirmDialog(null,
						ConfigurationUtils.getInstance()
								.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_DELETE_TEXT_ACTION_MESSAGE_CONTENT),
						ConfigurationUtils.getInstance()
								.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_DELETE_TEXT_ACTION_MESSAGE_TITLE),
						0);
				if (JOptionPane.YES_OPTION == result && null != textDisplayPanel.getDisplayTextSelected()) {
					// suppression
					try {
						controler.deleteTextAndWriteCorpusFromFolderText(
								textDisplayPanel.getDisplayTextSelected().getStructuredKey());
						textDisplayPanel.refresh();
					} catch (IOException e1) {
						logger.error(e1.getMessage(), e1);
					}
				}
			}
		});
	}

	@Override
	public JComponent getJPanel() {
		return this.content;
	}

	@Override
	public void close() {
		if (null != fixedOrEditTextPanel) {
			fixedOrEditTextPanel.closeFrame();
		}
		if (null != fixedOrEditCorpusPanel) {
			fixedOrEditCorpusPanel.closeFrame();
		}
	}

	@Override
	public void setEnabledAllButton(Boolean enable) {
		this.forcedDisabledButton = !enable;
		if (this.forcedDisabledButton) {
			deleteButton.setEnabled(Boolean.FALSE);
			editButtonText.setEnabled(Boolean.FALSE);
			editButtonCorpus.setEnabled(Boolean.FALSE);
		} else {
			textDisplayPanel.refresh();
		}
	}

	@Override
	public void addConsumerOnOpenEditText(Consumer<Void> consumer) {
		this.consumerOnOpenEditText = consumer;
	}

	@Override
	public void addConsumerOnCloseEditText(Consumer<Void> consumer) {
		this.consumerOnCloseEditText = consumer;
	}

}
