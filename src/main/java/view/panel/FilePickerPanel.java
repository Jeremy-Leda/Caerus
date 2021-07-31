package view.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import view.beans.FilePickerTypeEnum;
import view.interfaces.IFilePickerPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Permet d'afficher un panel pour choisir le fichier
 * 
 * @author jerem
 *
 */
public class FilePickerPanel implements IFilePickerPanel {

	private final JPanel filePickerPanel;
	private JLabel label;
	private JTextField textField;
	private JButton button;
	private Consumer<?> consumerChooseFileOk;

	private JFileChooser fileChooser;

	/**
	 * Permet de créer un filepicker
	 * 
	 * @param titlePanel     Titre du panel
	 * @param dialogTitle    Titre de la fenêtre de dialogue
	 * @param filePickerType Type de filepicker
	 */
	public FilePickerPanel(String titlePanel, String dialogTitle, FilePickerTypeEnum filePickerType) {
		this(titlePanel, dialogTitle, null, filePickerType);
	}

	/**
	 * Permet de créer un filepicker
	 * 
	 * @param titlePanel     Titre du panel
	 * @param dialogTitle    Titre de la fenêtre de dialogue
	 * @param label          Libellé devant la zone de texte
	 * @param filePickerType Type de filepicker
	 */
	public FilePickerPanel(String titlePanel, String dialogTitle, String label, FilePickerTypeEnum filePickerType) {
		this.filePickerPanel = new JPanel();
		this.filePickerPanel.setBorder(BorderFactory.createTitledBorder(titlePanel));
		this.fileChooser = new JFileChooser();
		this.fileChooser.setDialogTitle(dialogTitle);
		switch (filePickerType) {
		case SAVE_FILE:
			this.label = new JLabel(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_PANEL_LABEL));
			this.button = new JButton(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_PANEL_BUTTON));
			break;
		case OPEN_FOLDER:
			this.label = new JLabel(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_LABEL));
			this.button = new JButton(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_BUTTON_LABEL));
			this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			break;
		case IMPORT_FILE:
			this.label = new JLabel(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_FILE_PICKER_PANEL_LABEL));
			this.button = new JButton(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_FILE_PICKER_PANEL_BUTTON));
			this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			break;

		}
		if (StringUtils.isNotBlank(label)) {
			this.label.setText(label);
		}
		this.button.addActionListener(e -> buttonActionPerformed(filePickerType));
		this.textField = new JTextField(30);
		this.textField.setEnabled(false);
		this.filePickerPanel.add(this.label);
		this.filePickerPanel.add(this.textField);
		this.filePickerPanel.add(this.button);
	}

	@Override
	public JComponent getJPanel() {
		return this.filePickerPanel;
	}

	@Override
	public String getFile() {
		return this.textField.getText();
	}

	@Override
	public void addConsumerOnChooseFileOk(Consumer<?> consumer) {
		this.consumerChooseFileOk = consumer;
	}

	/**
	 * Permet d'activer l'affichage de la fenêtre pour choisir le fichier
	 * 
	 * @param filePickerType Type de filePicker choisis
	 */
	private void buttonActionPerformed(FilePickerTypeEnum filePickerType) {
		switch (filePickerType) {
			case SAVE_FILE:
				processForSaveFile();
				break;
			case OPEN_FOLDER:
				processForOpenFolder();
			case IMPORT_FILE:
				processForImportFolder();
			break;
		}
		if (StringUtils.isNotBlank(getFile()) && null != this.consumerChooseFileOk) {
			this.consumerChooseFileOk.accept(null);
		}
	}

	/**
	 * Permet d'effectuer le traitement pour le choix de la sauvegarde du fichier
	 */
	private void processForSaveFile() {
		if (fileChooser.showSaveDialog(getJPanel()) == JFileChooser.APPROVE_OPTION) {
			String absolutePath = this.fileChooser.getSelectedFile().getAbsolutePath();
			String extension = FilenameUtils.getExtension(absolutePath);
			if (!"xlsx".equals(extension)) {
				StringBuilder sb = new StringBuilder(absolutePath);
				sb.append(".xlsx");
				absolutePath = sb.toString();
			}
			textField.setText(absolutePath);
		}
	}

	/**
	 * Permet d'effectuer le traitement pour le choix de l'ouverture du dossier
	 */
	private void processForOpenFolder() {
		if (fileChooser.showOpenDialog(getJPanel()) == JFileChooser.APPROVE_OPTION) {
			if (this.fileChooser.getSelectedFile().isDirectory()) {
				textField.setText(this.fileChooser.getSelectedFile().getAbsolutePath());
			} else {
				textField.setText(this.fileChooser.getSelectedFile().getParent());
			}
		}
	}

	/**
	 * Permet d'effectuer le traitement pour le choix de l'import d'un fichier
	 */
	private void processForImportFolder() {
		if (fileChooser.showOpenDialog(getJPanel()) == JFileChooser.APPROVE_OPTION) {
			if (this.fileChooser.getSelectedFile().isFile()) {
				String absolutePath = this.fileChooser.getSelectedFile().getAbsolutePath();
				String extension = FilenameUtils.getExtension(absolutePath);
				if ("xlsx".equals(extension)) {
					textField.setText(absolutePath);
				}
			}
		}
	}
}
