package ihm.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exceptions.LoadTextException;
import ihm.beans.PictureTypeEnum;
import ihm.controler.IConfigurationControler;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;
import utils.RessourcesUtils;

/**
 * 
 * Classe permettant de concevoir l'interface pour le chargement des textes =>
 * Interface permettant de choisir les configurations de lecture du texte
 * 
 * @author jerem
 *
 */
public class LoadTextConfigurationSelector extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3348054080637849772L;
	private static Logger logger = LoggerFactory.getLogger(LoadTextConfigurationSelector.class);
	private File folderFile;
	private final JTextField folderPath = new JTextField();
	private final JComboBox<String> typeConfigurationComboList = new JComboBox<String>();
	private final JButton startButton = new JButton(ConfigurationUtils.getInstance()
			.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_START_BUTTON_LABEL));
	private final JDialog frame = new JDialog((JFrame) null, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TITLE), true);
	private final IConfigurationControler controler;
	
	/**
	 * Constructeur
	 */
	public LoadTextConfigurationSelector(IConfigurationControler configurationControler) {
		this.controler = configurationControler;
		this.controler.clearAnalyze();
		createWindow();
	}

	/**
	 * Permet de créer la fenêtre
	 */
	private void createWindow() {
		init();
		repack();
		frame.setVisible(true);
	}

	/**
	 * Initialise la fenêtre et ses composants
	 */
	private void init() {

		JPanel panConfigurations = new JPanel();
		panConfigurations.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TYPE_CONFIGURATION_PANEL_TITLE)));
		BoxLayout boxLayoutConfiguration = new BoxLayout(panConfigurations, BoxLayout.Y_AXIS);
		panConfigurations.setLayout(boxLayoutConfiguration);
		JPanel subPanConfiguration = new JPanel();

		JLabel configurationLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TYPE_CONFIGURATION_LABEL));
		subPanConfiguration.add(configurationLabel);		 
		fillConfigurationDisplayList();
		subPanConfiguration.add(typeConfigurationComboList);

		panConfigurations.add(subPanConfiguration);


		JPanel panFolder = new JPanel();
		panFolder.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_PANEL_TITLE)));
		JLabel folderLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_LABEL));
		panFolder.add(folderLabel);
		folderPath.setEnabled(false);
		folderPath.setSize(100, 20);
		panFolder.add(folderPath);
		JButton chooseFolderButton = new JButton(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_BUTTON_LABEL));
		chooseFolderButton.addActionListener(openFolder());
		panFolder.add(chooseFolderButton);

		JPanel panStart = new JPanel();
		startButton.setEnabled(false);
		startButton.addActionListener(startAnalyse());
		panStart.add(startButton);

		JPanel content = new JPanel();
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(panConfigurations);
		content.add(panFolder);
		content.add(panStart);

		frame.setModal(true);
		this.frame.setIconImage(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO));
		frame.getContentPane().add(content, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}

	/**
	 * Permet de remplir la liste pour la configuration
	 */
	private void fillConfigurationDisplayList() {
		controler.getConfigurationNameList().forEach(name -> typeConfigurationComboList.addItem(name));
		this.typeConfigurationComboList.setSelectedItem(controler.getConfigurationName());
	}

	/**
	 * permet de créer la fenêtre de sélection du dossier
	 * 
	 * @return
	 */
	private ActionListener openFolder() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle(ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_BUTTON_FOLDER_CHOOSE_TITLE));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					folderFile = chooser.getSelectedFile();
					folderPath.setText(folderFile.getAbsolutePath());
					startButton.setEnabled(true);
					repack();
				}
			}
		};
	}

	public Boolean isLoaded() {
		return !this.controler.getListOfStructuredFileForAnalyze().isEmpty();
	}
	
	/**
	 * Permet de repack la fenêtre Position centrer
	 */
	private void repack() {
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	
	private void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	private ActionListener startAnalyse() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (typeConfigurationComboList.getItemCount() == 0) {
					try {
						controler.setCurrentConfiguration(ConfigurationUtils.getInstance().getClassicalConfiguration());
					} catch (Exception e1) {
						logger.error(e1.getMessage(), e1);
					}
				} else {
					controler.setCurrentConfiguration(typeConfigurationComboList.getSelectedItem().toString());
				}
				controler.setAnalyzeFolder(folderFile);
				try {
					controler.launchAnalyze();
				} catch (LoadTextException e1) {
					logger.error(e1.getMessage(), e1);
				}
				close();
			}
		};
	}

}
