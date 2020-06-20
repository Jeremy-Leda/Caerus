package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ExportTypeEnum;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IComboBoxPanel;
import view.interfaces.IFilePickerPanel;
import view.interfaces.IGenericAccessPanel;
import view.interfaces.IRadioButtonPanel;
import view.panel.ActionPanel;
import view.panel.ComboBoxPanel;
import view.panel.FilePickerPanel;
import view.panel.GenericAccessPanel;
import view.panel.RadioButtonPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Vue pour l'export des documents
 * 
 * @author jerem
 *
 */
public class ExportDocument extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4965336379261975128L;
	private final JPanel content;
	private final IFilePickerPanel chooseFolderPanel;
	private final IRadioButtonPanel modePanel;
	private final IComboBoxPanel chooseDocumentPanel;
	private final IGenericAccessPanel chooseNameFilePanel;
	private final IActionPanel buttonsPanel;
	private final JTextField nameFile;
	private static Logger logger = LoggerFactory.getLogger(ExportDocument.class);

	public ExportDocument(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_TITLE),
				configurationControler);
		this.content = new JPanel();
		this.chooseFolderPanel = new FilePickerPanel(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_DIRECTORY_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_DIRECTORY_DIALOG_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(
						Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_DIRECTORY_LABEL),
				FilePickerTypeEnum.OPEN_FOLDER);
		this.modePanel = new RadioButtonPanel(3);
		this.chooseDocumentPanel = new ComboBoxPanel(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_DOCUMENT_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_DOCUMENT_LABEL));
		this.chooseNameFilePanel = new GenericAccessPanel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_FILE_PANEL_TITLE));
		this.buttonsPanel = new ActionPanel(2);
		this.nameFile = new JTextField(40);
		createWindow();
	}

	@Override
	public void initComponents() {
		refreshActionPanelMessage();
		initModePanel();
		fillCorpusFilterPanel();
		this.chooseNameFilePanel.addComponent(createChooseNameFileSubPanel());
		this.nameFile.addKeyListener(getKeyListenerForNameFile());
		this.chooseDocumentPanel.addConsumerOnSelectChange(v -> enabledExportIfOk());
		this.chooseFolderPanel.addConsumerOnChooseFileOk(v -> enabledExportIfOk());
		refreshDisplay();
		enabledExportIfOk();
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	@Override
	public String getWindowName() {
		return "Export Document";
	}

	/**
	 * Créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(this.content, BoxLayout.Y_AXIS);
		this.content.setLayout(boxlayout);
		this.content.add(this.chooseFolderPanel.getJPanel());
		this.content.add(this.modePanel.getJPanel());
		this.content.add(this.chooseDocumentPanel.getJPanel());
		this.content.add(this.chooseNameFilePanel.getJPanel());
		this.content.add(this.buttonsPanel.getJPanel());
	}

	/**
	 * Permet de rafraichir l'affichage pour les bouttons
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_BUTTON_EXPORT_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_BUTTON_CLOSE_LABEL));
		this.buttonsPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_BUTTON_PANEL_TITLE),
				messageButtonMap);
		this.buttonsPanel.addAction(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					goForExport();
					new UserInformation(
							ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_INFORMATION_MESSAGE_TITLE),
							getControler(), PictureTypeEnum.INFORMATION, ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_INFORMATION_MESSAGE));
					closeFrame();
				} catch (IOException e1) {
					new UserInformation(
							ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_INFORMATION_PANEL_LABEL),
							getControler(), PictureTypeEnum.WARNING, e1.getMessage());
				}
			}
		});
		this.buttonsPanel.addAction(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
		this.buttonsPanel.setIconButton(0, PictureTypeEnum.SAVE);
	}

	/**
	 * Permet d'initialiser les boutons radio
	 */
	private void initModePanel() {
		Map<Integer, String> radioButtonMap = new HashMap<Integer, String>();
		radioButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_MODE_ALL_DOCUMENTS_LABEL));
		radioButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_MODE_DOCUMENT_LABEL));
		radioButtonMap.put(2, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_MODE_RESULT_SEARCH_LABEL));
		this.modePanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_MODE_PANEL_TITLE),
				radioButtonMap);
		this.modePanel.setActionListener(getChangeModeActionListener());
		this.modePanel.setDefaultSelectedRadioButton(0);
	}

	/**
	 * Permet de remplir la liste des corpus
	 */
	private void fillCorpusFilterPanel() {
		List<String> valuesList = new LinkedList<>();
		valuesList.add(StringUtils.EMPTY);
		valuesList.addAll(getControler().getAllCorpusNameForFilteredText());
		this.chooseDocumentPanel.refresh(valuesList);
	}

	/**
	 * Permet de créer le sous panel pour le choix du nom du fichier
	 * 
	 * @return le sous panel
	 */
	private JPanel createChooseNameFileSubPanel() {
		JPanel panel = new JPanel();
		JLabel chooseNameLabel = new JLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_EXPORT_DOCUMENT_CHOOSE_FILE_LABEL));
		panel.add(chooseNameLabel);
		panel.add(this.nameFile);
		return panel;
	}

	/**
	 * Permet de gérer le changement de mode
	 * 
	 * @return l'action pour la gestion
	 */
	private ActionListener getChangeModeActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshDisplay();
				enabledExportIfOk();
				repack();
			}
		};
	}

	/**
	 * permet de rafraichir l'affichage
	 */
	private void refreshDisplay() {
		switch (getExportTypeSelected()) {
		case ALL_DOCUMENTS:
			chooseDocumentPanel.getJPanel().setVisible(Boolean.FALSE);
			chooseNameFilePanel.getJPanel().setVisible(Boolean.FALSE);
			break;
		case DOCUMENT:
			chooseDocumentPanel.getJPanel().setVisible(Boolean.TRUE);
			chooseNameFilePanel.getJPanel().setVisible(Boolean.FALSE);
			chooseDocumentPanel.selectItem(StringUtils.EMPTY);
			break;
		case SEARCH_RESULTS:
			chooseDocumentPanel.getJPanel().setVisible(Boolean.FALSE);
			chooseNameFilePanel.getJPanel().setVisible(Boolean.TRUE);
			nameFile.setText(StringUtils.EMPTY);
			break;
		}
	}

	/**
	 * Permet de se procurer le type d'export sélectionné
	 * 
	 * @return le type d'export sélectionné
	 */
	private ExportTypeEnum getExportTypeSelected() {
		switch (this.modePanel.getSelectedRadioButtonNumber()) {
		case 0:
			return ExportTypeEnum.ALL_DOCUMENTS;
		case 1:
			return ExportTypeEnum.DOCUMENT;
		case 2:
			return ExportTypeEnum.SEARCH_RESULTS;
		default:
			return null;
		}
	}

	/**
	 * Permet d'activer ou non l'export
	 */
	private void enabledExportIfOk() {
		Boolean enabled = StringUtils.isNotBlank(this.chooseFolderPanel.getFile());
		switch (getExportTypeSelected()) {
		case ALL_DOCUMENTS:
			break;
		case DOCUMENT:
			enabled = enabled && StringUtils.isNotBlank(this.chooseDocumentPanel.getLabelSelected());
			break;
		case SEARCH_RESULTS:
			enabled = enabled && StringUtils.isNotBlank(this.nameFile.getText());
			break;
		}
		this.buttonsPanel.setEnabled(0, enabled);
	}

	/**
	 * Permet de se procurer le key listener pour le nom du fichier
	 * 
	 * @return le key listener
	 */
	private KeyListener getKeyListenerForNameFile() {
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				enabledExportIfOk();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};
	}

	/**
	 * Permet d'effectuer l'export
	 * 
	 * @throws IOException Erreur d'entrée sortie
	 */
	private void goForExport() throws IOException {
		logger.debug("Go for export");
		switch (getExportTypeSelected()) {
		case ALL_DOCUMENTS:
			getControler().export(getExportTypeSelected(), chooseFolderPanel.getFile(), null);
			break;
		case DOCUMENT:
			getControler().export(getExportTypeSelected(), chooseFolderPanel.getFile(),
					chooseDocumentPanel.getLabelSelected());
			break;
		case SEARCH_RESULTS:
			getControler().export(getExportTypeSelected(), chooseFolderPanel.getFile(), nameFile.getText());
			break;
		}
	}

}
