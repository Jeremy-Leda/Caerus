package ihm.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import analyze.beans.Configuration;
import ihm.beans.ConfigurationType;
import ihm.controler.IConfigurationControler;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

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
	private final Map<String, ConfigurationType> mapDisplayToConfiguration = new HashMap<>();
	private File folderFile;
	private final JTextField folderPath = new JTextField();
	private final JPanel subPanExpertConfiguration = new JPanel();
	private final JComboBox<String> typeConfigurationComboList = new JComboBox<String>();
	private final JButton startButton = new JButton(ConfigurationUtils.getInstance()
			.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_START_BUTTON_LABEL));
	private final JDialog frame = new JDialog((JFrame) null, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TITLE), true);
	private final IConfigurationControler controler;
	
	/**
	 * Constructeur
	 */
	public LoadTextConfigurationSelector(IConfigurationControler configurationControler) {
		for (ConfigurationType configurationType : ConfigurationType.values()) {
			StringBuilder sb = new StringBuilder(Constants.TYPE_CONFIGURATION_PREFIX);
			sb.append(configurationType.name());
			String displayMessage = ConfigurationUtils.getInstance().getDisplayMessage(sb.toString());
			mapDisplayToConfiguration.put(displayMessage, configurationType);
		}
		this.controler = configurationControler;
		this.controler.clear();
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
		typeConfigurationComboList.addItemListener(new ItemConfigurationChangeListener());
		fillConfigurationDisplayList(typeConfigurationComboList);
		subPanConfiguration.add(typeConfigurationComboList);

		JLabel configurationExpertLabel = new JLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TYPE_CONFIGURATION_EXPERT_LABEL));
		JComboBox<String> typeConfigurationExpertComboList = new JComboBox<String>();
		subPanExpertConfiguration.add(configurationExpertLabel);
		subPanExpertConfiguration.add(typeConfigurationExpertComboList);

		panConfigurations.add(subPanConfiguration);
		panConfigurations.add(subPanExpertConfiguration);
		subPanExpertConfiguration.setVisible(false);

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
		frame.getContentPane().add(content, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}

	/**
	 * Permet de remplir la liste pour la configuration
	 */
	private void fillConfigurationDisplayList(JComboBox<String> typeConfigurationComboList) {
		mapDisplayToConfiguration.keySet().stream().forEach(s -> typeConfigurationComboList.addItem(s));
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
		return null != this.controler.getListOfStructuredFile();
	}
	
	public Boolean errorProcessing() {
		try {
			return this.controler.errorProcessing();
		} catch (IOException e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(null, "Une erreur s'est produite pendant le traitement des erreurs", "Erreur", JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}
	
	/**
	 * Permet de repack la fenêtre Position centrer
	 */
	private void repack() {
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Classe permettant de gérer le change de configuration
	 * 
	 * @author jerem
	 *
	 */
	private class ItemConfigurationChangeListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				if (ConfigurationType.DIDACTIC.equals(mapDisplayToConfiguration.get(event.getItem()))) {
					subPanExpertConfiguration.setVisible(false);
				} else if (ConfigurationType.DIDACTIC_EXPERT.equals(mapDisplayToConfiguration.get(event.getItem()))) {
					subPanExpertConfiguration.setVisible(true);
				}
				repack();
			}
		}
	}
	
	private void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	private ActionListener startAnalyse() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Configuration configuration = new Configuration();
				if (ConfigurationType.DIDACTIC.equals(mapDisplayToConfiguration.get(typeConfigurationComboList.getSelectedItem()))) {
					try {
						configuration = ConfigurationUtils.getInstance().getClassicalConfiguration();
					} catch (Exception e1) {
						System.err.println(e1.getMessage());
						System.err.println("Erreur");
					}
				}
				controler.analyzePath(folderFile, configuration);
				close();
			}
		};
	}

}
