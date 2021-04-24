package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ErrorStructuredLine;
import view.beans.PictureTypeEnum;
import view.interfaces.IGenericAccessPanel;
import view.interfaces.IInformationPanel;
import view.interfaces.IListPanel;
import view.interfaces.IRadioButtonPanel;
import view.interfaces.IWizardPanel;
import view.panel.GenericAccessPanel;
import view.panel.InformationPanel;
import view.panel.ListPanel;
import view.panel.RadioButtonPanel;
import view.panel.WizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * Interface pour corriger les erreurs de lignes mal formaté
 * 
 * @author jerem
 *
 */
public class FixedErrorLine extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2892809132534513107L;
	private static Logger logger = LoggerFactory.getLogger(FixedErrorLine.class);
	private final JPanel panContent = new JPanel();
	private final JButton nextButton = new JButton();
	private final JLabel numberLineValue = new JLabel();
	private final JLabel pathLineValue = new JLabel();
	private final JTextArea textErrorLine;
	private final JTextArea textFixedLine;
	private final IListPanel listPanel;
	private final Map<String, String> fieldsListMap = new LinkedHashMap<String, String>();
	private final JTextArea textUserSelected;
	private final JPanel subPanFixedErrorLine;
	private final JTextArea dataUserSelected = new JTextArea(1, 50);

	private final JTextArea dataTextArea;
	private final IRadioButtonPanel radioButtonPanel;

	private final IInformationPanel informationExpertPanel;

	// WIZARD
	private final IWizardPanel wizardPanel;

	private Integer currentIndex = 0;
	private JPanel content = new JPanel();
	private Boolean isModeExpert = Boolean.FALSE;

	public FixedErrorLine(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_TITLE),
				configurationControler);
		listPanel = new ListPanel(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_LIST_FIELD_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_LIST_FIELD_LABEL));
		this.textUserSelected = new JTextArea(1, 50);
		this.textUserSelected.setWrapStyleWord(true);
		this.textUserSelected.setLineWrap(true);
		this.textUserSelected.setEditable(false);

		this.textErrorLine = new JTextArea(1, 50);
		this.textErrorLine.setWrapStyleWord(true);
		this.textErrorLine.setLineWrap(true);
		this.textErrorLine.setEditable(false);

		this.textFixedLine = new JTextArea(1, 50);
		this.textFixedLine.setWrapStyleWord(true);
		this.textFixedLine.setLineWrap(true);
		this.textFixedLine.setEditable(false);

		this.subPanFixedErrorLine = new JPanel();
		this.subPanFixedErrorLine.setVisible(false);
		this.dataTextArea = new JTextArea(1, 50);
		this.dataTextArea.setWrapStyleWord(true);
		this.dataTextArea.setLineWrap(true);
		this.dataTextArea.setEditable(true);
		this.wizardPanel = new WizardPanel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_WIZARD_PANEL_TITLE));
		this.wizardPanel.addConsumerOnChangeStep(changeConsumerForWizard());

		this.radioButtonPanel = new RadioButtonPanel(2);
		this.informationExpertPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE_EXPERT),
				true, true);

		this.fieldsListMap.putAll(getControler().getAllField());
		this.listPanel.addConsumerOnSelectChange(afterChangeSelectionList());
		dataTextArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				refreshFixedText();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		createStep_0();
		createStep_1();
		createStep_2();
		createStep_3();
		initRadioButtonPanel();
		createWindow();
	}

	/**
	 * Permet d'initialiser les boutons radio
	 */
	private void initRadioButtonPanel() {
		Map<Integer, String> radioButtonMap = new HashMap<Integer, String>();
		radioButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_MODE_WIZARD_LABEL));
		radioButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_MODE_EXPERT_LABEL));
		this.radioButtonPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_MODE_PANEL_TITLE),
				radioButtonMap);
		this.radioButtonPanel.setDefaultSelectedRadioButton(0);
		this.radioButtonPanel.setActionListener(changeModeListener());
	}

	/**
	 * Action sur le changement de mode
	 * 
	 * @return L'action
	 */
	private ActionListener changeModeListener() {
		return v -> {
			isModeExpert = radioButtonPanel.getSelectedRadioButtonNumber() == 1;
			refreshAfterChangeMode();
		};
	}

	/**
	 * Permet de rafraichir après le changement de mode
	 */
	private void refreshAfterChangeMode() {
		if (isModeExpert) {
			informationExpertPanel.getJPanel().setVisible(Boolean.TRUE);
			wizardPanel.getJPanel().setVisible(Boolean.FALSE);
			this.subPanFixedErrorLine.setVisible(Boolean.TRUE);
			this.textFixedLine.setEditable(Boolean.TRUE);
			this.textFixedLine.setText(this.textErrorLine.getText());
			refreshRowNum(this.textFixedLine);
			this.nextButton.setEnabled(Boolean.TRUE);
		} else {
			informationExpertPanel.getJPanel().setVisible(Boolean.FALSE);
			wizardPanel.getJPanel().setVisible(Boolean.TRUE);
			this.textFixedLine.setEditable(Boolean.FALSE);
			this.textFixedLine.setText(StringUtils.EMPTY);
			this.wizardPanel.setStep(0L);
		}
		repack();
	}

	/**
	 * Permet de créer l'étape 0 de l'assistant
	 */
	private void createStep_0() {
		IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE_ETAPE1),
				true, true);
		this.wizardPanel.addStep(Arrays.asList(informationStep));
	}

	/**
	 * Permet de créer l'étape 1 de l'assitant
	 */
	private void createStep_1() {
		IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE_ETAPE2),
				true, true);
		this.wizardPanel.addStep(Arrays.asList(informationStep, listPanel));
	}

	/**
	 * Permet de créer l'étape 2 de l'assitant
	 */
	private void createStep_2() {
		IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE_ETAPE3),
				true, true);
		IGenericAccessPanel genericPanel = new GenericAccessPanel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_SELECTED_DATA_PANEL_TITLE));
		JPanel panelSelectText = new JPanel();
		JLabel libelleSelectionText = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_SELECTED_DATA_SELECT_TEXT_LABEL));
		JButton selectTextButton = new JButton(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_SELECTED_DATA_SELECT_TEXT_BUTTON_LABEL));
		panelSelectText.add(libelleSelectionText);
		panelSelectText.add(this.textUserSelected);
		panelSelectText.add(selectTextButton);
		genericPanel.addComponent(panelSelectText);

		JPanel panelSelectedText = new JPanel();
		JLabel libelleSelectedText = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_SELECTED_DATA_SELECTED_TEXT_LABEL));
		panelSelectedText.add(libelleSelectedText);
		dataUserSelected.setWrapStyleWord(true);
		dataUserSelected.setLineWrap(true);
		dataUserSelected.setEditable(false);
		panelSelectedText.add(dataUserSelected);

		selectTextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dataUserSelected.setText(textUserSelected.getSelectedText());
				refreshRowNum(dataUserSelected);
				dataTextArea.setText(textUserSelected.getSelectedText());
				repack();
			}
		});
		genericPanel.addComponent(panelSelectedText);

		this.wizardPanel.addStep(Arrays.asList(informationStep, genericPanel));
	}

	/**
	 * Permet de créer l'étape 3 de l'assitant
	 */
	private void createStep_3() {
		IInformationPanel informationStep = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_INFORMATION_MESSAGE_ETAPE4),
				true, true);
		this.wizardPanel.addStep(Arrays.asList(informationStep));
	}

	/**
	 * Consumer pour le changement de page de l'assistant
	 * 
	 * @return le consumer
	 */
	private Consumer<?> changeConsumerForWizard() {
		return v -> {
			this.nextButton
					.setEnabled(this.wizardPanel.isLastPage() && StringUtils.isNotBlank(this.textFixedLine.getText()));
			if (this.wizardPanel.isLastPage()) {
				subPanFixedErrorLine.setVisible(true);
				refreshFixedText();
			} else {
				subPanFixedErrorLine.setVisible(false);
			}
			refreshRowNum(textUserSelected);
			refreshRowNum(dataTextArea);
			repack();
		};
	}

	@Override
	public void initComponents() {
		fillTextWithErrorAndRefreshDisplay();
		panContent.setLayout(new BoxLayout(panContent, BoxLayout.Y_AXIS));
		JPanel subPanPathErrorLine = new JPanel();
		JLabel pathLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_FILE_LABEL));
		subPanPathErrorLine.add(pathLineLabel);
		subPanPathErrorLine.add(pathLineValue);
		panContent.add(subPanPathErrorLine);
		JPanel subPanNumberErrorLine = new JPanel();
		JLabel numberLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_NUMBER_LABEL));
		subPanNumberErrorLine.add(numberLineLabel);
		subPanNumberErrorLine.add(numberLineValue);
		panContent.add(subPanNumberErrorLine);
		JPanel subPanErrorLine = new JPanel();
		JLabel errorLineLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_ERROR_LABEL));
		subPanErrorLine.add(errorLineLabel);
		subPanErrorLine.add(textErrorLine);
		panContent.add(subPanErrorLine);
		JLabel fixedErrorLineLabel = new JLabel();
		fixedErrorLineLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_LINE_FIXED_LABEL));
		subPanFixedErrorLine.add(fixedErrorLineLabel);
		subPanFixedErrorLine.add(textFixedLine);
		panContent.add(subPanFixedErrorLine);

		JPanel panAction = new JPanel();
		panAction.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_TITLE)));
		panAction.add(nextButton);

		nextButton.addActionListener(saveLine());
		informationExpertPanel.getJPanel().setVisible(Boolean.FALSE);

		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(panContent);
		content.add(radioButtonPanel.getJPanel());
		content.add(informationExpertPanel.getJPanel());
		content.add(wizardPanel.getJPanel());
		content.add(panAction);
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Action a effectuer après le changement dans la liste
	 * 
	 * @return Le consumer
	 */
	private Consumer<?> afterChangeSelectionList() {
		return c -> {
			refreshFixedText();
			refreshRowNum(textUserSelected);
			refreshRowNum(dataTextArea);
			repack();
		};
	}

	/**
	 * Permet de rafraichir le texte de la balise corrigé
	 */
	private void refreshFixedText() {
		if (StringUtils.isNotBlank(listPanel.getLabelSelected())) {
			Optional<Entry<String, String>> optionalFieldFound = this.fieldsListMap.entrySet().stream()
					.filter(s -> listPanel.getLabelSelected().equals(s.getValue())).findFirst();
			if (optionalFieldFound.isPresent()) {
				String structuredLine = getControler().getStructuredLine(optionalFieldFound.get().getKey(),
						this.dataTextArea.getText());
				textFixedLine.setText(structuredLine);
				refreshRowNum(textFixedLine);
				return;
			}
		}
		textFixedLine.setText(StringUtils.EMPTY);
	}

	/**
	 * Permet de remplir la liste
	 */
	private void fillList() {
		listPanel.refresh(fieldsListMap.values().stream().collect(Collectors.toList()));
	}

	/**
	 * Permet de charger l'erreur suivante
	 */
	private void fillTextWithErrorAndRefreshDisplay() {
		logger.debug("[DEBUT] fillTextWithErrorAndRefreshDisplay");
		logger.debug(String.format("Load index %d", currentIndex));
		ErrorStructuredLine errorLine = getControler().getErrorLine(currentIndex);
		if (null != errorLine) {
			logger.debug(String.format("Ligne charg� %s", errorLine));
			fillList();
			String panContentLabel = String.format(
					ConfigurationUtils.getInstance()
							.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_CONTENT_PANEL_TITLE),
					currentIndex + 1, getControler().getNbLinesError());
			panContent.setBorder(BorderFactory.createTitledBorder(panContentLabel));
			textErrorLine.setText(errorLine.getLine());
			refreshRowNum(textErrorLine);
			numberLineValue.setText(errorLine.getNumLine().toString());
			pathLineValue.setText(errorLine.getPath());
			this.textUserSelected.setText(errorLine.getLine());
			this.dataUserSelected.setText(StringUtils.EMPTY);
			this.dataTextArea.setText(StringUtils.EMPTY);
			refreshRowNum(this.dataUserSelected);
			if (currentIndex + 1 == getControler().getNbLinesError()) {
				nextButton.setText(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_QUIT_BUTTON_LABEL));
			} else {
				nextButton.setText(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_LINE_ACTION_PANEL_SAVE_NEXT_BUTTON_LABEL));
			}
			refreshAfterChangeMode();
		}
		logger.debug("[FIN] fillTextWithErrorAndRefreshDisplay");
	}

	/**
	 * Permet de sauvegarder la ligne et gérer le passage à la suivante ou quitter
	 * 
	 * @return l'action
	 */
	private ActionListener saveLine() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().updateLineError(currentIndex, textFixedLine.getText());
				if (currentIndex + 1 == getControler().getNbLinesError()) {
					saveFileAndQuit();
				} else {
					currentIndex++;
					fillTextWithErrorAndRefreshDisplay();
				}
			}
		};
	}

	/**
	 * Permet de sauvegarder puis quiter
	 */
	private void saveFileAndQuit() {
		try {
			getControler().saveFileAfterFixedErrorLine();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		closeFrame();
	}

	@Override
	public String getWindowName() {
		return "Window for fixed error of structured line";
	}

	/**
	 * Permet d'adapter le nombre de ligne pour le textarea
	 * 
	 * @param component composant text area
	 */
	private void refreshRowNum(JTextArea component) {
		refreshRowNum(95, component);
	}

	/**
	 * Permet d'adapter le nombre de ligne pour le textarea
	 * 
	 * @param nbCharByLine Nombre de caractére par ligne
	 * @param component    composant text area
	 */
	private void refreshRowNum(Integer nbCharByLine, JTextArea component) {
		if (component.getText().length() > 0) {
			BigDecimal nbLine = new BigDecimal(component.getText().length()).divide(new BigDecimal(nbCharByLine),
					RoundingMode.UP);
			nbLine.setScale(1, RoundingMode.UP);
			component.setRows(nbLine.intValue());
		} else {
			component.setRows(1);
		}
	}

}
