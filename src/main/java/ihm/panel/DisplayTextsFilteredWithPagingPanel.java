package ihm.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ihm.beans.ActionUserTypeEnum;
import ihm.beans.DirectionTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.interfaces.IAccessPanel;
import ihm.interfaces.IActionOnClose;
import ihm.interfaces.IRefreshTextDisplayPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;
import ihm.view.FixedText;

/**
 * 
 * Panel permettant d'afficher les textes avec une pagination
 * 
 * @author jerem
 *
 */
public class DisplayTextsFilteredWithPagingPanel implements IAccessPanel {

	private final JPanel content;
	private final JLabel currentPositionLabel;
	private final JComboBox<Integer> nbTextsByPageCombo;
	private final IRefreshTextDisplayPanel textDisplayPanel;
	private final JButton previousButton;
	private final JButton nextButton;
	private final JButton editButton;
	private final JButton deleteButton;
	private final List<Integer> listNbTextsByPage;
	private final IConfigurationControler controler;

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
		this.editButton = new JButton();
		this.deleteButton = new JButton();
		this.listNbTextsByPage = Arrays.asList(10, 20, 50);
		initComponentsAndCreateContent();
	}

	/**
	 * Permet d'initialiser les composants et de créer le contenu
	 */
	private void initComponentsAndCreateContent() {
		fillAndConfigureNbTextsByPageCombo();
		textDisplayPanel.setNbTextByPage((Integer)this.nbTextsByPageCombo.getSelectedItem());
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
		this.editButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_EDIT_BUTTON_LABEL));
		this.deleteButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_DELETE_BUTTON_LABEL));
		panel.add(label);
		panel.add(this.nbTextsByPageCombo);
		panel.add(this.editButton);
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
		this.nextButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_NEXT_BUTTON_LABEL));
		panel.add(this.previousButton);
		panel.add(this.nextButton);
		return panel;
	}

	/**
	 * Permet de rafraichir la position courante
	 */
	private void refreshCurrentPosition() {
		this.currentPositionLabel.setText(
				String.format(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_CURRENT_POSITION_LABEL),
				textDisplayPanel.getCurrentPage(), textDisplayPanel.getMaxPage()));
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
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerToChangeItem() {
		return v -> {
			Boolean haveTextSelected = null != textDisplayPanel.getDisplayTextSelected();
			editButton.setEnabled(haveTextSelected);
			deleteButton.setEnabled(haveTextSelected);
		};
	}

	/**
	 * Permet de configurer les boutons
	 */
	private void configureActionButtons() {
		this.editButton.setEnabled(false);
		this.deleteButton.setEnabled(false);
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
		this.editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controler.loadFilteredText(textDisplayPanel.getDisplayTextSelected().getStructuredKey());
				new FixedText(controler, ActionUserTypeEnum.FOLDER_TEXTS);
			}
		});
	}

	@Override
	public JComponent getJPanel() {
		return this.content;
	}

}
