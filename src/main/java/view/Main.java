package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controler.ConfigurationControler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.exceptions.LoadTextException;
import model.exceptions.MoveFileException;
import utils.RessourcesUtils;
import view.abstracts.ExecuteServerJFrameAbstract;
import view.beans.ActionOperationTypeEnum;
import view.beans.ActionUserTypeEnum;
import view.beans.ExcelTypeGenerationEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IAnalyzeConfiguration;
import view.interfaces.IGenericAccessPanel;
import view.panel.GenericAccessPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;
import view.windows.*;

/**
 * 
 * Classe principal pour l'interface Humain Machine
 * 
 * @author Jeremy
 *
 */
public class Main extends ExecuteServerJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1355570409059243038L;
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	private JMenuItem createAnalyze = new JMenuItem(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_OPEN_TEXT_FOLDER_TITLE)),
			stopWordsList = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL1_TITLE)),
			lemmatizationList = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL2_TITLE)),
			lemmatizationByGrammaticalCategoryList = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL3_TITLE)),
			properNounList = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL4_TITLE)),
			saveConfiguration = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_WRITE_EXCEL_TITLE)),
			saveCustomExcel = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_WRITE_EXCEL_CUSTOM_TITLE)),
			exit = new JMenuItem(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_EXIT_TITLE)),
			textLoadLibrary = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_LOAD_MENU_TITLE)),
			createTextLibrary = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_CREATE_MENU_TITLE)),
			manageTextLibrary = new JMenuItem(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_MANAGE_MENU_TITLE)),
			configurationLoadLibrary = new JMenuItem(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.CONFIGURATION_LIBRARY_LOAD_MENU_TITLE)),
			analysis_assistant = new JMenuItem(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL1_TITLE)),
			load_analysis = new JMenuItem(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL2_TITLE)),
			openAbout = new JMenuItem(ConfigurationUtils.getInstance().getDisplayMessage(Constants.MENU_ABOUT_OPEN)),
			importExcel = new JMenuItem(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL1_SUBLEVEL5_TITLE));
	private List<JMenuItem> languages = new ArrayList<JMenuItem>();
	private JMenu fileMenu = new JMenu(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_MENU_TITLE));
	private JMenu languageMenu = new JMenu(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.LANGUAGE_MENU_TITLE));
	private JMenu textLibrary = new JMenu(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_MENU_TITLE));
	private JMenu configurationLibrary = new JMenu(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.CONFIGURATION_LIBRARY_MENU_TITLE));
	private JMenu about = new JMenu(ConfigurationUtils.getInstance().getDisplayMessage(Constants.MENU_ABOUT));
	private JMenu analysis = new JMenu(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_TITLE));
	private JMenu manageList = new JMenu(
			ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_TITLE));
	private JMenuBar menuBar = new JMenuBar();
	private final JPanel panContent = new JPanel();
	private final JPanel panAnalyze = new JPanel();
	private final JPanel panLineError = new JPanel();
	private final JPanel panTextError = new JPanel();
	private final JPanel panMoveFileLibrary = new JPanel();
	private final JPanel panBlankLineError = new JPanel();
	private final JLabel stateConfigurationLibrayLabel = new JLabel();
	private final JLabel stateConfigurationLibrayValue = new JLabel();
	private final JLabel stateCurrentConfigurationLabel = new JLabel();
	private final JLabel stateCurrentConfigurationValue = new JLabel();
	private final JPanel subPanConfigurationState = new JPanel();
	private final JPanel subPanCurrentConfiguration = new JPanel();
	private final JPanel subPanAnalyzeState = new JPanel();
	private final JLabel stateAnalyzeLabel = new JLabel();
	private final JLabel stateAnalyzeValue = new JLabel();
	private final JLabel stateAnalyzeFolderLabel = new JLabel();
	private final JLabel stateAnalyzeFolderValue = new JLabel();
	private final JLabel stateAnalyzeConfigurationLabel = new JLabel();
	private final JLabel stateAnalyzeConfigurationValue = new JLabel();
	private final JLabel stateNbTextLoadedLabel = new JLabel();
	private final JLabel stateNbTextLoadedValue = new JLabel();
	private final JLabel nbLineErrorLabel = new JLabel();
	private final JLabel nbLineErrorValue = new JLabel();
	private final JButton fixedLineErrorButton = new JButton();
	private final JLabel nbTextErrorLabel = new JLabel();
	private final JLabel nbTextErrorValue = new JLabel();
	private final JLabel nbBlankLineErrorLabel = new JLabel();
	private final JLabel nbBlankLineErrorValue = new JLabel();
	private final JLabel haveMetaBlankLineErrorLabel = new JLabel();
	private final JLabel haveMetaBlankLineErrorValue = new JLabel();
	private final JButton fixedTextErrorButton = new JButton();
	private final JButton fixedBlankLineErrorButton = new JButton();
	private final JButton fixedMetaBlankLineErrorButton = new JButton();
	private final JButton moveFileLibraryButton = new JButton();
	private IAnalyzeConfiguration analyzeConfiguration;
	private IGenericAccessPanel errorInconsistencyPanel = new GenericAccessPanel();
	private JButton openInconsistencyErrorsButton = new JButton();
	private JButton openBaseCodeErrorsButton = new JButton();
	private final Integer progressMaxValue = 100;

	/**
	 * Constructeur
	 * 
	 * @throws IOException
	 * @throws HeadlessException
	 */
	public Main(Consumer<?> consumerOnClose) throws HeadlessException, IOException {
		super(new ConfigurationControler());
		// On regarde l'existence d'un état précédent
		if (getControler().haveCurrentStateFile()) {
			// on demande
			YesNoQuestion yesNoQuestion = new YesNoQuestion(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_RECOVERY_ERROR_STATE_TITLE),
					getControler(),
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_RECOVERY_ERROR_STATE_ANSWER));
//			Integer result = JOptionPane.showConfirmDialog(null,
//					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_RECOVERY_ERROR_STATE_ANSWER),
//					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_RECOVERY_ERROR_STATE_TITLE), 0);
			if (JOptionPane.YES_OPTION == yesNoQuestion.getAnswer()) {
				getControler().restoreCurrentState();
			} else {
				getControler().removeCurrentStateFile();
			}
		}
		try {
			if (StringUtils.isBlank(getControler().getConfigurationName())) {
				getControler().setCurrentConfiguration(RessourcesUtils.getInstance().getBasicalConfiguration());
			}
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}

		analyzeConfiguration = null;

		createWindow(consumerOnClose);

	}

	/**
	 * Permet de créer la fenêtre
	 * 
	 * @param consumerOnClose consumer à exécuter sur la fermeture
	 * @throws IOException
	 * @throws HeadlessException
	 */
	private void createWindow(Consumer<?> consumerOnClose) throws HeadlessException, IOException {
		this.setTitle(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_TITLE));
		this.setSize(400, 100);
		this.setLocationRelativeTo(null);
		this.setIconImages(getIconsListImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				logger.info("Welcome to Caerus");
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				consumerOnClose.accept(null);
				logger.info("GoodBye Caerus");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		initMenu();
		reloadLanguage();
		repack();
		this.setVisible(true);
	}

	/**
	 * Permet de se procurer la liste des icônes possible (taille différentes)
	 * 
	 * @return la liste des icônes
	 */
	private List<Image> getIconsListImage() {
		List<Image> allImages = new ArrayList<>();
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_16_16));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_32_32));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_64_64));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_96_96));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_128_128));
		allImages.add(RessourcesUtils.getInstance().getImage(PictureTypeEnum.LOGO_256_256));
		return allImages;
	}

	private void initMenu() {
		saveConfiguration.setEnabled(false);
		saveCustomExcel.setEnabled(false);
		fileMenu.add(createAnalyze);
		fileMenu.add(importExcel);
		fileMenu.add(saveConfiguration);
		fileMenu.add(saveCustomExcel);
//		fileMenu.addSeparator();
//		fileMenu.add(exit);

		stateCurrentConfigurationValue.setText(getControler().getConfigurationName());
		panContent.setLayout(new BoxLayout(panContent, BoxLayout.Y_AXIS));

		languages.addAll(ConfigurationUtils.getInstance().getMapLanguages().keySet().stream().map(s -> {
			JMenuItem menuItem = new JMenuItem(s);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ConfigurationUtils.getInstance()
							.loadBundleLangage(ConfigurationUtils.getInstance().getMapLanguages().get(s));
					reloadLanguage();
				}
			});
			return menuItem;
		}).collect(Collectors.toList()));
		languages.forEach(m -> languageMenu.add(m));

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		createAnalyze.addActionListener(event -> {
			analyzeConfiguration = new LoadTextConfigurationSelector(getControler());
			try {
				refreshDisplay();
				repack();
			} catch (HeadlessException | IOException e) {
				logger.error(e.getMessage(), e);
			}

		});

		stopWordsList.addActionListener(e -> new ManageStopWords(getControler()));
		lemmatizationList.addActionListener(e -> new ManageLemmatization(getControler()));
		lemmatizationByGrammaticalCategoryList.addActionListener(e -> new ManageLemmatizationByGrammaticalCategory(getControler()));
		properNounList.addActionListener(e -> new ManageProperNoun(getControler()));

		importExcel.addActionListener(e -> new ImportExcel(getControler()));

		configurationLoadLibrary.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ChooseConfiguration(getControler());
				stateCurrentConfigurationValue.setText(getControler().getConfigurationName());
			}
		});

		saveConfiguration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveReferenceExcels(getControler(), ExcelTypeGenerationEnum.ANALYZE_TEXTS);
			}
		});

		saveCustomExcel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveCustomExcel(getControler(), ExcelTypeGenerationEnum.ANALYZE_TEXTS);
			}
		});

		about.add(openAbout);
		openAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new UserInformation(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ABOUT_TITLE),
						getControler(), PictureTypeEnum.INFORMATION,
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ABOUT_MESSAGE_CONTENT));
			}
		});

		menuBar.add(fileMenu);
		menuBar.add(languageMenu);

		configurationLibrary.add(configurationLoadLibrary);
		menuBar.add(configurationLibrary);

		textLibrary.add(textLoadLibrary);
		textLoadLibrary.addActionListener(openFolderForSetTextsFolderLibrary(this));
		textLibrary.addSeparator();
		textLibrary.add(createTextLibrary);
		textLibrary.add(manageTextLibrary);
		createTextLibrary.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getControler().clearTexts();
				new CreateCorpus(getControler());
				if (getControler().haveEditingCorpus()) {
					IActionOnClose createText = new CreateText(getControler());
					createText.addActionOnClose((v) -> setEnabled(true));
					setEnabled(false);
				}
			}
		});

		manageTextLibrary.addActionListener(e -> super.executeOnServerWithProgressView(() -> {
			getControler().loadTexts();
			ManageText manageText = new ManageText(getControler());
			manageText.addActionOnClose((v) -> {
				setEnabled(true);
			});
			setEnabled(false);
		}, getControler(), false, Boolean.FALSE));
//		manageTextLibrary.addActionListener(e -> {
//			new ProgressBarView(r -> {
//				try {
//
//					getControler().loadTexts();
//					ManageText manageText = new ManageText(getControler());
//					manageText.addActionOnClose((v) -> {
//						setEnabled(true);
//					});
//					setEnabled(false);
//				} catch (LoadTextException e1) {
//					logger.error(e1.getMessage(), e);
//				}
//			}, getProgressConsumer(), progressMaxValue,
//					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_PROGRESS_BAR_LOAD_TEXT_LABEL));
//			getControler().resetProgress();
//		});

		menuBar.add(textLibrary);

		menuBar.add(analysis);
		analysis.add(analysis_assistant);

		analysis_assistant.addActionListener(e -> {
			super.executeOnServerWithProgressView(() -> getControler().loadTexts(), getControler(), false, Boolean.FALSE);
			AnalysisAssistant analysisAssistant = new AnalysisAssistant(
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CODE_TITLE), getControler());
			analysisAssistant.addActionOnClose((v) -> setEnabled(true));
			setEnabled(false);
		});
//		analysis_assistant.addActionListener(e -> super.executeOnServerWithProgressView(() -> {
//			getControler().loadTexts();
//			new AnalysisAssistant(
//					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CODE_TITLE), getControler());
//		}, false, Constants.WINDOW_PROGRESS_BAR_LOAD_TEXT_LABEL, Boolean.FALSE));

//		analysis_assistant.addActionListener(e -> {
//
//			AnalysisAssistant analysisAssistant = new AnalysisAssistant(
//					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CODE_TITLE), getControler());
//		});

		analysis.add(load_analysis);
		manageList.add(stopWordsList);
		manageList.add(lemmatizationList);
		manageList.add(lemmatizationByGrammaticalCategoryList);
		manageList.add(properNounList);
		analysis.add(manageList);

		menuBar.add(about);

		this.setJMenuBar(menuBar);

		createAnalyzePanel();
		createLineErrorPanel();
		createTextErrorPanel();
		createBlankLineErrorPanel();
		createMoveFileLibraryPanel();
		refreshEnabledAndValueWithTextsFolderLibrary();
		createErrorInconsistencyPanel();
		panContent.add(panAnalyze);
		panContent.add(panLineError);
		panContent.add(errorInconsistencyPanel.getJPanel());
		panContent.add(panTextError);
		panContent.add(panBlankLineError);
		panContent.add(panMoveFileLibrary);

		this.add(panContent);
	}

	/**
	 * Permet de se procurer le progress consumer
	 * 
	 * @return le progressConsumer
	 */
	private Consumer<Consumer<Integer>> getProgressConsumer() {
		return valueProgressSetter -> {
			while (getControler().getProgress() < progressMaxValue) {
				valueProgressSetter.accept(getControler().getProgress());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
	}

	/**
	 * Permet de repack la fenêtre Position centrer
	 */
	private void repack() {
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private void createAnalyzePanel() {
		JPanel subPanCommonAnalyzeState = new JPanel();
		subPanCommonAnalyzeState.add(stateAnalyzeLabel);
		subPanCommonAnalyzeState.add(stateAnalyzeValue);
		subPanConfigurationState.add(stateConfigurationLibrayLabel);
		subPanConfigurationState.add(stateConfigurationLibrayValue);
		subPanConfigurationState.setVisible(false);
		subPanCurrentConfiguration.add(stateCurrentConfigurationLabel);
		subPanCurrentConfiguration.add(stateCurrentConfigurationValue);
		panAnalyze.setLayout(new BoxLayout(panAnalyze, BoxLayout.Y_AXIS));
		subPanAnalyzeState.setLayout(new BoxLayout(subPanAnalyzeState, BoxLayout.Y_AXIS));
		JPanel subPanFolderAnalyzeState = new JPanel();
		subPanFolderAnalyzeState.add(stateAnalyzeFolderLabel);
		subPanFolderAnalyzeState.add(stateAnalyzeFolderValue);
		JPanel subPanConfigurationAnalyzeState = new JPanel();
		subPanConfigurationAnalyzeState.add(stateAnalyzeConfigurationLabel);
		subPanConfigurationAnalyzeState.add(stateAnalyzeConfigurationValue);
		JPanel subPanCommonNbTextLoaded = new JPanel();
		subPanCommonNbTextLoaded.add(stateNbTextLoadedLabel);
		subPanCommonNbTextLoaded.add(stateNbTextLoadedValue);
		subPanAnalyzeState.add(subPanFolderAnalyzeState);
		subPanAnalyzeState.add(subPanConfigurationAnalyzeState);
		subPanAnalyzeState.add(subPanCommonNbTextLoaded);
		subPanAnalyzeState.setVisible(false);
//		JPanel subPanButtonAnalyzeState = new JPanel();
//		subPanButtonAnalyzeState.add(stateAnalyzeButton);
//		stateAnalyzeButton.addActionListener(openFolderForAnalyzeAndLaunch(this));
		panAnalyze.add(subPanCommonAnalyzeState);
		panAnalyze.add(subPanConfigurationState);
		panAnalyze.add(subPanCurrentConfiguration);
		panAnalyze.add(subPanAnalyzeState);
//		panAnalyze.add(subPanButtonAnalyzeState);
	}

	/***
	 * Permet de créer le panel pour les erreurs de ligne
	 */
	private void createLineErrorPanel() {
		panLineError.setLayout(new BoxLayout(panLineError, BoxLayout.Y_AXIS));
		JPanel subPanNbError = new JPanel();
		subPanNbError.add(nbLineErrorLabel);
		subPanNbError.add(nbLineErrorValue);
		panLineError.add(subPanNbError);
		JPanel subPanButtonError = new JPanel();
		subPanButtonError.add(fixedLineErrorButton);
		panLineError.add(subPanButtonError);
		panLineError.setVisible(false);
		fixedLineErrorButton.addActionListener(fixedErrorLine());
	}

	/***
	 * Permet de créer le panel pour les erreurs de textes
	 */
	private void createTextErrorPanel() {
		panTextError.setLayout(new BoxLayout(panTextError, BoxLayout.Y_AXIS));
		JPanel subPanNbError = new JPanel();
		subPanNbError.add(nbTextErrorLabel);
		subPanNbError.add(nbTextErrorValue);
		panTextError.add(subPanNbError);
		JPanel subPanButtonError = new JPanel();
		subPanButtonError.add(fixedTextErrorButton);
		panTextError.add(subPanButtonError);
		panTextError.setVisible(false);
		fixedTextErrorButton.addActionListener(fixedErrorText());
	}

	/**
	 * Permet de créer le panel pour transférer les textes vers la bibliothèque de
	 * texte
	 */
	private void createMoveFileLibraryPanel() {
		panMoveFileLibrary.setLayout(new BoxLayout(panMoveFileLibrary, BoxLayout.Y_AXIS));
		JPanel subPanButton = new JPanel();
		subPanButton.add(moveFileLibraryButton);
		panMoveFileLibrary.add(subPanButton);
		panMoveFileLibrary.setVisible(false);
		moveFileLibraryButton.addActionListener(moveFileToLibraryAction());
		this.moveFileLibraryButton.setIcon(new ImageIcon(RessourcesUtils.getInstance().getImage(PictureTypeEnum.SAVE)));
	}

	/***
	 * Permet de créer le panel pour les erreurs de lignes vides
	 */
	private void createBlankLineErrorPanel() {
		panBlankLineError.setLayout(new BoxLayout(panBlankLineError, BoxLayout.Y_AXIS));
		JPanel subPanHaveMetaBlankLineError = new JPanel();
		subPanHaveMetaBlankLineError.add(haveMetaBlankLineErrorLabel);
		subPanHaveMetaBlankLineError.add(haveMetaBlankLineErrorValue);
		panBlankLineError.add(subPanHaveMetaBlankLineError);
		JPanel subPanNbBlankLineError = new JPanel();
		subPanNbBlankLineError.add(nbBlankLineErrorLabel);
		subPanNbBlankLineError.add(nbBlankLineErrorValue);
		panBlankLineError.add(subPanNbBlankLineError);
		JPanel subPanButtonError = new JPanel();
		subPanButtonError.add(fixedMetaBlankLineErrorButton);
		subPanButtonError.add(fixedBlankLineErrorButton);
		panBlankLineError.add(subPanButtonError);
		panBlankLineError.setVisible(false);
		fixedMetaBlankLineErrorButton.addActionListener(fixedMetaBlankLineError());
		fixedBlankLineErrorButton.addActionListener(fixedBlankLineError());
	}

	private void reloadLanguage() {
		stateConfigurationLibrayLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_CONFIGURATION_LIBRARY_PANEL_STATE_LABEL));
		panAnalyze.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_TITLE)));
		stateAnalyzeLabel.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_LABEL));
		stateAnalyzeFolderLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_FOLDER_LABEL));
		stateNbTextLoadedLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_NB_TEXT_LOADED_LABEL));
		stateCurrentConfigurationLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_CURRENT_CONFIGURATION_LABEL));
		stateAnalyzeConfigurationLabel.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_CONFIGURATION_LABEL));
//		stateAnalyzeButton.setText(ConfigurationUtils.getInstance()
//				.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_LOAD_BUTTON));
		createAnalyze
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_OPEN_TEXT_FOLDER_TITLE));
		saveConfiguration.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_WRITE_EXCEL_TITLE));
		saveCustomExcel
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_WRITE_EXCEL_CUSTOM_TITLE));
		exit.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_EXIT_TITLE));
		fileMenu.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.FILE_MENU_TITLE));
		about.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.MENU_ABOUT));
		openAbout.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.MENU_ABOUT_OPEN));
		configurationLibrary.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.CONFIGURATION_LIBRARY_MENU_TITLE));
		textLibrary.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_MENU_TITLE));
		analysis.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_TITLE));
		manageList.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_TITLE));
		stopWordsList.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL1_TITLE));
		lemmatizationList.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL2_TITLE));
		lemmatizationByGrammaticalCategoryList.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL3_SUBLEVEL3_TITLE));
		load_analysis.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL2_TITLE));
		analysis_assistant.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MENU_LEVEL6_SUBLEVEL1_TITLE));
		textLoadLibrary
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_LOAD_MENU_TITLE));
		createTextLibrary
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_CREATE_MENU_TITLE));
		manageTextLibrary
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.TEXT_LIBRARY_MANAGE_MENU_TITLE));
		configurationLoadLibrary.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.CONFIGURATION_LIBRARY_LOAD_MENU_TITLE));
		this.setTitle(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_TITLE));
		panLineError.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_LINE_ERROR_PANEL_TITLE)));
		nbLineErrorLabel
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_LINE_ERROR_NB_LABEL));
		fixedLineErrorButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_LINE_ERROR_FIXED_BUTTON_LABEL));
		panTextError.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_TEXT_ERROR_PANEL_TITLE)));
		nbTextErrorLabel
				.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_TEXT_ERROR_NB_LABEL));
		fixedTextErrorButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_TEXT_ERROR_FIXED_BUTTON_LABEL));

		panBlankLineError.setBorder(BorderFactory.createTitledBorder(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_BLANK_LINE_ERROR_PANEL_TITLE)));
		nbBlankLineErrorLabel.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_BLANK_LINE_ERROR_NB_LABEL));
		fixedBlankLineErrorButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_BLANK_LINE_ERROR_FIXED_BUTTON_LABEL));

		haveMetaBlankLineErrorLabel.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MAIN_META_BLANK_LINE_ERROR_LABEL));
		fixedMetaBlankLineErrorButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_META_BLANK_LINE_ERROR_FIXED_BUTTON_LABEL));

		panMoveFileLibrary.setBorder(BorderFactory.createTitledBorder(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MOVE_FILE_LIBRARY_PANEL_LABEL)));
		moveFileLibraryButton.setText(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MOVE_FILE_LIBRARY_BUTTON_LABEL));
		openInconsistencyErrorsButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_INCONSISTENCY_ERROR_DUPLICATE_BUTTON_LABEL));
		openBaseCodeErrorsButton.setText(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_INCONSISTENCY_ERROR_BASE_CODE_BUTTON_LABEL));
		errorInconsistencyPanel.refreshTitle(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MAIN_INCONSISTENCY_ERROR_PANEL_TITLE));
		try {
			refreshDisplay();
		} catch (HeadlessException | IOException e) {
			e.printStackTrace();
		}
		repack();
	}

	private void refreshDisplay() throws HeadlessException, IOException {
		stateCurrentConfigurationValue.setText(getControler().getConfigurationName());
		errorInconsistencyPanel.getJPanel().setVisible(Boolean.FALSE);
		if (getControler().getNbTextsError() == 0 && getControler().getNbBlankLinesError() == 0
				&& getControler().getListOfStructuredFileForAnalyze().isEmpty()) {
			panLineError.setVisible(false);
			panTextError.setVisible(false);

			subPanAnalyzeState.setVisible(false);
			saveConfiguration.setEnabled(false);
			saveCustomExcel.setEnabled(false);
			panBlankLineError.setVisible(false);
			panMoveFileLibrary.setVisible(false);
			stateAnalyzeValue.setText(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_VALUE_NONE));
		} else {

			stateNbTextLoadedValue.setText(getControler().getNbTextLoadedForAnalyze().toString());

			if (getControler().getNbLinesError() > 0) {
				fixedLineErrorButton.setEnabled(true);
				saveConfiguration.setEnabled(false);
				saveCustomExcel.setEnabled(false);
			} else {
				errorInconsistencyPanel.getJPanel().setVisible(Boolean.TRUE);
				openInconsistencyErrorsButton.setEnabled(getControler().haveInconsistencyError());
				openBaseCodeErrorsButton.setEnabled(getControler().haveMissingBaseCodeError());
				saveConfiguration.setEnabled(true);
				saveCustomExcel.setEnabled(true);
				fixedLineErrorButton.setEnabled(false);
				if (getControler().getNbTextsError() > 0) {
					fixedTextErrorButton.setEnabled(true);
					saveConfiguration.setEnabled(false);
					saveCustomExcel.setEnabled(false);
				} else {
					saveConfiguration.setEnabled(true);
					saveCustomExcel.setEnabled(true);
					fixedTextErrorButton.setEnabled(false);
					fixedBlankLineErrorButton.setEnabled(false);
					fixedMetaBlankLineErrorButton.setEnabled(false);
					haveMetaBlankLineErrorValue
							.setText(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_NO_LABEL));
					if (getControler().getNbBlankLinesError() > 0) {
						fixedBlankLineErrorButton.setEnabled(true);
					}
					if (getControler().haveMetaBlankLineInErrorRemaining()) {
						fixedMetaBlankLineErrorButton.setEnabled(true);
						haveMetaBlankLineErrorValue.setText(
								ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_YES_LABEL));
					}
					nbBlankLineErrorValue.setText(getControler().getNbBlankLinesError().toString());
					panBlankLineError.setVisible(true);
					panMoveFileLibrary.setVisible(true);
					if (getControler().getTextsFolder().isPresent()) {
						moveFileLibraryButton.setEnabled(true);
					}
				}
				nbTextErrorValue.setText(getControler().getNbTextsError().toString());
				panTextError.setVisible(true);

			}
			nbLineErrorValue.setText(getControler().getNbLinesError().toString());
			panLineError.setVisible(true);

			stateAnalyzeValue.setText(ConfigurationUtils.getInstance()
					.getDisplayMessage(Constants.WINDOW_MAIN_ANALYZE_PANEL_STATE_VALUE_SUCCESS));
			Optional<File> analyzeFolder = getControler().getAnalyzeFolder();
			if (analyzeFolder.isPresent()) {
				stateAnalyzeFolderValue.setText(analyzeFolder.get().toString());
			}
			stateAnalyzeConfigurationValue.setText(getControler().getConfigurationName());
			subPanAnalyzeState.setVisible(true);
		}
	}

	private ActionListener moveFileToLibraryAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Map<Path, Path> moveAllFilesFromTextAnalyzeToLibraryResultMap = getControler()
							.moveAllFilesFromTextAnalyzeToLibrary();
					String message = constructInformationFileMessage(moveAllFilesFromTextAnalyzeToLibraryResultMap);
					new UserInformation(
							ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_INFORMATION_PANEL_LABEL),
							getControler(), PictureTypeEnum.INFORMATION, message);
					getControler().clearAnalyze();
					refreshDisplay();
					repack();
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
					new UserInformation(
							ConfigurationUtils.getInstance().getDisplayMessage(
									Constants.WINDOW_INFORMATION_PANEL_LABEL),
							getControler(), PictureTypeEnum.WARNING,
							String.format(ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_MESSAGE_UNKNOW_ERROR), e1.getMessage()));
				} catch (MoveFileException e1) {
					logger.error(e1.getMessage(), e1);
					String message = constructErrorFileMessage(Arrays.asList(StringUtils.split(e1.getMessage(), ",")));
					new UserInformation(
							ConfigurationUtils.getInstance()
									.getDisplayMessage(Constants.WINDOW_INFORMATION_PANEL_LABEL),
							getControler(), PictureTypeEnum.WARNING, message);
				}
			}
		};
	}

	/**
	 * Permet de construire le message d'information
	 * 
	 * @param fileResultMap liste des fichiers en erreur
	 * @return le message d'erreur
	 */
	private String constructInformationFileMessage(Map<Path, Path> fileResultMap) {
		StringBuilder sb = new StringBuilder("<ul>");
		String messageFrom = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MESSAGE_FROM);
		String messageTo = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MESSAGE_TO);
		fileResultMap.entrySet().stream().forEach((entry) -> {
			String fileName = entry.getKey().getFileName().toString();
			sb.append("<li>").append(fileName).append("</li>").append("<ul>");
			sb.append("<li>").append(messageFrom).append(entry.getKey().toString()).append("</li>");
			sb.append("<li>").append(messageTo).append(entry.getValue().toString()).append("</li>");
			sb.append("</ul>");
		});
		sb.append("</ul>");
		return String.format(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MESSAGE_RESULT_MOVE_FILE),
				sb.toString());
	}

	/**
	 * Permet de construire l'erreur pour les fichiers déjà existant
	 * 
	 * @param listErreur liste des fichiers en erreur
	 * @return le message d'erreur
	 */
	private String constructErrorFileMessage(List<String> listErreur) {
		StringBuilder sb = new StringBuilder("<ul>");
		listErreur.stream().forEach(file -> sb.append("<li>").append(file).append("</li>"));
		sb.append("</ul>");
		return String.format(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MESSAGE_ERROR_MOVE_FILE_EXISTS),
				sb.toString());
	}

	private ActionListener fixedErrorLine() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new FixedErrorLine(getControler());
				launchAnalyze();
			}
		};
	}

	private ActionListener fixedErrorText() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				IActionOnClose fixedText = new FixedOrEditText(
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FIXED_TEXT_TITLE),
						getControler(), ActionUserTypeEnum.FOLDER_ANALYZE, ActionOperationTypeEnum.EDIT);
				fixedText.addActionOnClose((v) -> {
					setEnabled(true);
					launchAnalyze();
				});
				setEnabled(false);
			}
		};
	}

	private ActionListener fixedBlankLineError() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				IActionOnClose fixedBlankLine = new FixedBlankLine(getControler());
				fixedBlankLine.addActionOnClose((v) -> {
					setEnabled(true);
					launchAnalyze();
				});
				setEnabled(false);
			}
		};
	}

	private ActionListener fixedMetaBlankLineError() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new FixedOrEditCorpus(
						ConfigurationUtils.getInstance()
								.getDisplayMessage(Constants.WINDOW_FIXED_ERROR_META_BLANK_LINE_PANEL_TITLE),
						getControler(), true, ActionUserTypeEnum.FOLDER_ANALYZE);
				launchAnalyze();
			}
		};
	}

//	/**
//	 * permet de cr�er la fen�tre de s�lection du dossier et de lancer l'analyse
//	 * 
//	 * @param parent JFrame parente
//	 * @return
//	 */
//	private ActionListener openFolderForAnalyzeAndLaunch(JFrame parent) {
//		return new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JFileChooser chooser = new JFileChooser();
//				chooser.setCurrentDirectory(new java.io.File("."));
//				chooser.setDialogTitle(ConfigurationUtils.getInstance()
//						.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_BUTTON_FOLDER_CHOOSE_TITLE));
//				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//				chooser.setAcceptAllFileFilterUsed(false);
//				if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
//					getControler().setAnalyzeFolder(chooser.getSelectedFile());
//					try {
//						getControler()
//								.setCurrentConfiguration(ConfigurationUtils.getInstance().getClassicalConfiguration());
//					} catch (IOException e1) {
//						logger.error(e1.getMessage(), e1);
//					}
//					launchAnalyze();
//				}
//			}
//		};
//	}

	/**
	 * Permet de lancer l'analyse
	 */
	private void launchAnalyze() {
		try {
			getControler().clearAnalyze();
			refreshDisplay();
			Boolean withSubFolder = Boolean.FALSE;
			if (null != analyzeConfiguration) {
				withSubFolder = analyzeConfiguration.getWithSubFolderAnalyze();
			}
			getControler().launchAnalyze(withSubFolder);
			refreshDisplay();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (LoadTextException e) {
			logger.error(e.getMessage(), e);
		}
		repack();
	}

	/**
	 * Permet de rafraichir pour les activations et les valeurs suite à la présence
	 * du chemin de la librairie des textes
	 */
	private void refreshEnabledAndValueWithTextsFolderLibrary() {
		if (getControler().getTextsFolder().isPresent()) {
			subPanConfigurationState.setVisible(true);
			createTextLibrary.setEnabled(true);
			manageTextLibrary.setEnabled(true);
			moveFileLibraryButton.setEnabled(true);
			stateConfigurationLibrayValue.setText(getControler().getTextsFolder().get().toString());
		} else {
			subPanConfigurationState.setVisible(false);
			createTextLibrary.setEnabled(false);
			manageTextLibrary.setEnabled(false);
			moveFileLibraryButton.setEnabled(false);
			stateConfigurationLibrayValue.setText(StringUtils.EMPTY);
		}
	}

	/**
	 * Permet de créer la fenêtre de sélection du dossier et la définir en tant que
	 * bibliothèque de textes
	 * 
	 * @param parent JFrame parent
	 * @return
	 */
	private ActionListener openFolderForSetTextsFolderLibrary(JFrame parent) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle(ConfigurationUtils.getInstance().getDisplayMessage(
						Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_LIBRARY_BUTTON_FOLDER_CHOOSE_TITLE));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					getControler().setTextsFolder(chooser.getSelectedFile());
					refreshEnabledAndValueWithTextsFolderLibrary();
					repack();
				}
			}
		};
	}

	/**
	 * Permet créer le panel d'erreurs d'incohérences
	 */
	private void createErrorInconsistencyPanel() {
		openInconsistencyErrorsButton
				.setIcon(new ImageIcon(RessourcesUtils.getInstance().getImage(PictureTypeEnum.WARNING)));
		openInconsistencyErrorsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new InconsistencyErrorView(getControler());
				launchAnalyze();
			}
		});
		openBaseCodeErrorsButton
				.setIcon(new ImageIcon(RessourcesUtils.getInstance().getImage(PictureTypeEnum.WARNING)));
		openBaseCodeErrorsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new BaseCodeErrorView(getControler());
				launchAnalyze();
			}
		});

		JPanel subPanButtonError = new JPanel();
		subPanButtonError.add(openInconsistencyErrorsButton);
		subPanButtonError.add(openBaseCodeErrorsButton);
		errorInconsistencyPanel.addComponent(subPanButtonError);
	}

	@Override
	public void closeFrame() {

	}
}
