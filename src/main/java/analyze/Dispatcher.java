package analyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.CurrentUserConfiguration;
import analyze.beans.MemoryFile;
import analyze.beans.SaveCurrentFixedText;
import analyze.beans.SpecificConfiguration;
import analyze.beans.StructuredField;
import analyze.beans.StructuredFile;
import analyze.beans.specific.ConfigurationStructuredText;
import analyze.constants.FolderSettingsEnum;
import excel.CreateExcel;
import excel.beans.ExcelGenerateConfigurationCmd;
import exceptions.LoadTextException;
import exceptions.MoveFileException;
import utils.JSonFactoryUtils;
import utils.PathUtils;

/**
 * 
 * Classe en charge de dispatcher les informations en chargeant les donn�es dans
 * differentes classes. Aucune communication n'existe entre les autres classes
 * 
 * @author Jeremy
 *
 */
public class Dispatcher {

	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private static final String FOLDER_CONTEXT = "context";
	private static final String FOLDER_TEXTS = "library";
	private static final String FOLDER_CONFIGURATIONS = "configurations";
	private static final String CONFIGURATION_CLASSIC_NAME = "Configuraci�n b�sica";
	private static final String FILE_CURRENT_STATE = "currentState.pyl";
	private static final String FILE_CURRENT_USER_CONFIGURATION = "currentUserConfiguration.pyl";
	private static final String ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT = "ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT";

	/**
	 * Constructeur
	 */
	public Dispatcher() {
		try {
			loadContextFromUserConfiguration();
			UserSettings.getInstance().clearCurrentFolderUserTextsMap();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Permet de lancer l'analyse des textes dans le dossier d'analyse
	 * 
	 * @throws IOException
	 * @throws LoadTextException
	 */
	public void launchAnalyze() throws IOException, LoadTextException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	/**
	 * Permet de charger les textes dans le folder des textes
	 * 
	 * @throws IOException
	 * @throws LoadTextException
	 */
	public void loadTexts() throws IOException, LoadTextException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_TEXTS);
	}

	/**
	 * Permet traiter et de charger les textes
	 * 
	 * @param folderType type du dossier � prendre en charge
	 * @throws IOException       erreur d'�ntr�e sortie
	 * @throws LoadTextException Exception d� au chargement des textes
	 */
	private void processAndLoadTexts(FolderSettingsEnum folderType) throws IOException, LoadTextException {
		logger.debug(String.format("CALL processAndLoadTexts => type %s", folderType));
		File pathToProcess;
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
			pathToProcess = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		} else {
			pathToProcess = UserSettings.getInstance().getFolder(folderType);
		}
		List<MemoryFile> memoryFiles = getMemoryFiles(pathToProcess.toString());
		UserSettings.getInstance().clearAllSession(folderType);
		UserSettings.getInstance().addMemoryFilesList(folderType, memoryFiles);
		memoryFiles.parallelStream().map(f -> new Structuring(f, folderType).getStructuredFile())
				.forEach(sf -> UserSettings.getInstance().addStructuredFile(folderType, sf));
		if (UserSettings.getInstance().getNbLineError() > 0) {
			if (FolderSettingsEnum.FOLDER_ANALYZE.equals(folderType)) {
				return;
			} else {
				throw new LoadTextException(ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT);
			}
		}
		if (null != UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList())
		UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().stream()
				.forEach(sc -> UserSettings.getInstance().addConfigurationStructuredText(folderType,
						new ConfigurationStructuredText(sc)));
		UserSettings.getInstance().getConfigurationStructuredTextList(folderType).stream()
				.forEach(st -> structuredTextSpecificProcess(memoryFiles, folderType, st));
	}

	/**
	 * M�thode permettant de lancer la mont�e en m�moire de l'ensemble du r�pertoire
	 * � traiter
	 * 
	 * @param pathFolderToAnalyze R�pertoire � analyser
	 * @return la liste des fichiers en m�moire
	 * @throws IOException
	 */
	private List<MemoryFile> getMemoryFiles(String pathFolderToAnalyze) throws IOException {
		final List<MemoryFile> listeSortie = new ArrayList<MemoryFile>();
		Files.walkFileTree(Paths.get(pathFolderToAnalyze), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!Files.isDirectory(file)) {
					logger.debug(String.format("Traitement du fichier %s", file.toString()));
					listeSortie.add(new Loader(file).getMemoryFile());
				}
				return FileVisitResult.CONTINUE;
			}
		});
		return listeSortie;
	}

	/**
	 * Permet de d�finir le dossier des textes
	 * 
	 * @param textsFolder dossier des textes
	 */
	public void setTextsFolder(File textsFolder) {
		UserSettings.getInstance().setFolder(FolderSettingsEnum.FOLDER_TEXTS, textsFolder);
		saveUserConfiguration();
	}

	/**
	 * Permet de g�n�rer le fichier excel
	 * 
	 * @param folder Type de dossier
	 * @param cmd    commande de g�n�ration
	 * @throws IOException
	 */
	public void generateExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (cmd.getHaveToGenerateReferenceText()) {
			generateClassicalExcel(folder, cmd);
		} else {
			generateCustomExcel(folder, cmd);
		}
	}

	/**
	 * permet de g�n�rer de l'excel classique
	 * 
	 * @param folder type de dossier
	 * @param cmd    cmd
	 * @throws IOException
	 */
	private void generateClassicalExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd)
			throws IOException {
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(UserSettings.getInstance().getStructuredFileList(folder),
				UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
		rows.forEach(r -> ce.createRow(r));
		ce.generateExcel();
		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
		cmd.getMapLabelSpecificFileName().forEach((key, value) -> {
			Optional<ConfigurationStructuredText> findFirstCst = UserSettings.getInstance()
					.getConfigurationStructuredTextList(folder).stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(key)).findFirst();
			if (findFirstCst.isPresent()) {
				List<StructuredField> listSf = UserSettings.getInstance().getCurrentConfiguration()
						.getStructuredFieldList().stream().filter(field -> !findFirstCst.get()
								.getSpecificConfiguration().getIgnoredFieldList().contains(field.getFieldName()))
						.collect(Collectors.toList());
				cmd.clearFieldListGenerate();
				listSf.forEach(sf -> cmd.addFieldToGenerate(sf.getFieldName()));
				try {
					createExcelSpecific(new File(value), findFirstCst.get(), cmd);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * permet de g�n�rer un excel custom
	 * 
	 * @param folder type de dossier
	 * @param cmd    command
	 * @throws IOException
	 */
	private void generateCustomExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (!cmd.getIsSpecificGeneration()) {
			ExcelStructuring es = new ExcelStructuring();
			List<List<String>> rows = es.getStructuringRows(UserSettings.getInstance().getStructuredFileList(folder),
					UserSettings.getInstance().getCurrentConfiguration(), cmd);
			CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
			rows.forEach(r -> ce.createRow(r));
			ce.generateExcel();
		} else {
			Optional<ConfigurationStructuredText> findFirstCst = UserSettings.getInstance()
					.getConfigurationStructuredTextList(folder).stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(cmd.getLabelSpecificChoose()))
					.findFirst();
			if (findFirstCst.isPresent()) {
				try {
					createExcelSpecific(new File(cmd.getFileName()), findFirstCst.get(), cmd);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Permet de g�n�rer le fichier structur� en fonction d'une configuration
	 * sp�cifique
	 * 
	 * @param memoryFiles           liste des fichiers m�moires
	 * @param configurationSpecific configuration specifique
	 */
	private void structuredTextSpecificProcess(List<MemoryFile> memoryFiles, FolderSettingsEnum folderType,
			ConfigurationStructuredText configurationSpecific) {
		List<StructuredFile> structuredFileList = memoryFiles.stream()
				.map(f -> new Structuring(f, folderType, configurationSpecific).getStructuredFile())
				.collect(Collectors.toList());
		configurationSpecific.getStructuredFileList().addAll(structuredFileList);
	}

	/**
	 * Permet de g�n�rer un fichier excel pour une configuraton sp�cifique On
	 * modifie le nom du fichier pour ajouter un suffixe en provenance de la
	 * configuration
	 * 
	 * @param commonPath            chemin du fichier g�n�rale
	 * @param configurationSpecific configuration sp�cifique
	 * @throws IOException io exception
	 */
	private void createExcelSpecific(File path, ConfigurationStructuredText configurationSpecific,
			ExcelGenerateConfigurationCmd cmd) throws IOException {
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(configurationSpecific.getStructuredFileList(),
				UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(path);
		rows.forEach(r -> ce.createRow(r));
		ce.generateExcel();
	}

	/**
	 * Permet d'�crire le corpus
	 * 
	 * @throws IOException Erreur d'entr�e sortie
	 */
	public void writeCorpus() throws IOException {
		StringBuilder fileName = new StringBuilder();
		fileName.append(UserSettings.getInstance().getEditingCorpusNameFile());
		fileName.append(".txt");
		try (Writer writer = new Writer(UserSettings.getInstance().getDirectoryForSaveTextsInLibrary(),
				fileName.toString())) {
			UserSettings.getInstance().writeCorpus(writer);
		}
	}

	/**
	 * Permet de sauvegarder temporairement l'�tat
	 */
	public void saveCurrentStateOfFixedText() {
		SaveCurrentFixedText save = UserSettings.getInstance().getSaveCurrentFixedText();
		try {
			JSonFactoryUtils.createJsonInFile(save, getCurrentStateFile());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Permet d'�crire le corpus
	 * 
	 * @throws IOException Erreur d'entr�e sortie
	 */
	public void writeFixedText() throws IOException {
		List<String> filesList = UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_ANALYZE)
				.stream().map(ust -> ust.getFileName()).distinct().collect(Collectors.toList());
		writeText(FolderSettingsEnum.FOLDER_ANALYZE, filesList);
		UserSettings.getInstance().clearAfterWriteFixedText();
		PathUtils.deleteFile(getCurrentStateFile());
	}

	/**
	 * Permet d'�crire le texte en cours d'�dition
	 * 
	 * @throws IOException
	 */
	public void writeEditText() throws IOException {
		StringBuilder corpusNameFile = new StringBuilder(UserSettings.getInstance().getEditingCorpusNameFile());
		corpusNameFile.append(".txt");
		writeText(FolderSettingsEnum.FOLDER_TEXTS, Arrays.asList(corpusNameFile.toString()));
		UserSettings.getInstance().clearEditingCorpus();
	}

	/**
	 * Permet d'�crire le fichier
	 * 
	 * @param folderType type du dossier
	 * @param filesList  liste des fichiers � �crire
	 * @throws IOException
	 */
	private void writeText(FolderSettingsEnum folderType, List<String> filesList) throws IOException {
		File directory = UserSettings.getInstance().getFolder(folderType);
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
			directory = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		}
		for (String file : filesList) {
			Boolean haveText = null;
			try (Writer writer = new Writer(directory, file)) {
				haveText = UserSettings.getInstance().writeText(folderType, writer, file);
			}
			if (null != haveText && !haveText) {
				PathUtils.deleteFile(new File(directory, file));
			}
		}
	}
	
	/**
	 * Permet de supprimer d�finitivement un texte
	 * Suppression logique et physique
	 * @param key Cl� du texte
	 * @param folderType type du dossier
	 * @throws IOException 
	 */
	public void deleteTextAndWriteCorpus(String key, FolderSettingsEnum folderType) throws IOException {
		String fileName = UserSettings.getInstance().getCorpusNameOfText(key, folderType);
		UserSettings.getInstance().deleteText(key, folderType);
		writeText(folderType, Arrays.asList(fileName.toString()));
	}

	/**
	 * Permet de connaitre l'existance d'un fichier d'�tat
	 * 
	 * @return Vrai si un fichier d'�tat existe
	 */
	public Boolean haveCurrentStateFile() {
		return getCurrentStateFile().exists();
	}

	/**
	 * Permet de restaurer l'�tat courant
	 * 
	 * @throws IOException          Erreur d'entr�e sorties
	 * @throws JsonMappingException Erreur de mapping
	 * @throws JsonParseException   Erreur de parsing
	 */
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
		InputStream is = new FileInputStream(getCurrentStateFile());
		SaveCurrentFixedText saveCurrentFixedText = JSonFactoryUtils.createObjectFromJsonFile(is,
				SaveCurrentFixedText.class);
		UserSettings.getInstance().restoreCurrentFixedTest(saveCurrentFixedText);
	}

	/**
	 * Permet de se procurer le fichier d'�tat Si le repertoire n'existe pas, il
	 * sera cr�� lors du passage dans cette m�thode. Le fichier quand � lui ne sera
	 * pas cr��
	 * 
	 * @return
	 */
	private File getCurrentStateFile() {
		String rootPath = PathUtils.getRootPath();
		File parentFile = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONTEXT);
		return new File(parentFile, FILE_CURRENT_STATE);
	}

	/**
	 * Permet de se procurer le fichier de configuration utililisateur Si le
	 * repertoire n'existe pas, il sera cr�� lors du passage dans cette m�thode. Le
	 * fichier quand � lui ne sera pas cr��
	 * 
	 * @return
	 */
	private File getUserConfigurationFile() {
		String rootPath = PathUtils.getRootPath();
		File parentFile = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONTEXT);
		return new File(parentFile, FILE_CURRENT_USER_CONFIGURATION);
	}

	/**
	 * Permet de charger le context � partir de la configuration utilisateur
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private void loadContextFromUserConfiguration() throws JsonParseException, JsonMappingException, IOException {
		File userConfigurationFile = getUserConfigurationFile();
		CurrentUserConfiguration currentUserConfiguration;
		if (userConfigurationFile.exists()) {
			InputStream is = new FileInputStream(userConfigurationFile);
			currentUserConfiguration = JSonFactoryUtils.createObjectFromJsonFile(is,
					CurrentUserConfiguration.class);
		} else {
			currentUserConfiguration = new CurrentUserConfiguration();
			String rootPath = PathUtils.getRootPath();
			File configurationsPath = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONFIGURATIONS);
			File textsPath = PathUtils.addFolderAndCreate(rootPath, FOLDER_TEXTS);
			currentUserConfiguration.setConfigurationPath(configurationsPath.toPath());
			currentUserConfiguration.setLibraryPath(textsPath.toPath());
			currentUserConfiguration.setDefaultConfiguration(CONFIGURATION_CLASSIC_NAME);
		}
		UserSettings.getInstance().restoreUserConfiguration(currentUserConfiguration);
		saveUserConfiguration();
	}

	/**
	 * Permet de sauvegarder temporairement l'�tat
	 */
	private void saveUserConfiguration() {
		CurrentUserConfiguration save = UserSettings.getInstance().getUserConfiguration();
		try {
			JSonFactoryUtils.createJsonInFile(save, getUserConfigurationFile());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Permet de se procurer la liste des champs � process
	 * 
	 * @param labelSpecificConfiguration label du specific dont on souhaite les
	 *                                   champs
	 * @return la liste
	 */
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		Optional<SpecificConfiguration> findFirstSpecific = UserSettings.getInstance().getCurrentConfiguration()
				.getSpecificConfigurationList().stream().filter(sc -> sc.getLabel().equals(labelSpecificConfiguration))
				.findFirst();
		if (findFirstSpecific.isPresent()) {
			return findFirstSpecific.get().getTreatmentFieldList();
		}
		return new ArrayList<>();
	}

	/**
	 * Permet de se procurer la liste des champs que l'on ne peux pas afficher si on
	 * selectionne le specifique en parametre
	 * 
	 * @param labelSpecificConfiguration label du specific dont on souhaite les
	 *                                   champs
	 * @return la liste
	 */
	public List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration) {
		List<SpecificConfiguration> forbiddenSpecificConfigurationList = UserSettings.getInstance()
				.getCurrentConfiguration().getSpecificConfigurationList().stream()
				.filter(sc -> !sc.getLabel().equals(labelSpecificConfiguration)).collect(Collectors.toList());
		return forbiddenSpecificConfigurationList.stream().flatMap(sc -> sc.getTreatmentFieldList().stream()).distinct()
				.collect(Collectors.toList());
	}

	/**
	 * Permet de d�placer les fichiers de l'analyze vers la librairie
	 * 
	 * @return La map des fichiers d�plac�s (key = ancien fichier, value = nouveau
	 *         fichier)
	 * @throws IOException       exception d'�criture
	 * @throws MoveFileException
	 */
	public Map<Path, Path> moveAllFilesFromTextAnalyzeToLibrary() throws IOException, MoveFileException {
		List<String> filesList = UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_ANALYZE)
				.stream().map(ust -> ust.getFileName()).distinct().collect(Collectors.toList());
		List<String> filesExistList = new ArrayList<>();
		Map<Path, Path> resultMapForMoveFiles = new HashMap<>();
		for (String file : filesList) {
			File oldFile = new File(UserSettings.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE), file);
			if (oldFile.exists()) {
				if (checkIfFileExistInFolderText(oldFile)) {
					filesExistList.add(oldFile.toPath().toString());
				}
			}
		}
		if (!filesExistList.isEmpty()) {
			throw new MoveFileException(StringUtils.join(filesExistList, ","));
		}
		for (String file : filesList) {
			File oldFile = new File(UserSettings.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE), file);
			Path newFile = moveFileInFolderText(oldFile);
			resultMapForMoveFiles.put(oldFile.toPath(), newFile);
		}
		return resultMapForMoveFiles;

	}

	/**
	 * Permet de savoir si le fichier existe dans la bibliotheque de textes (chemin
	 * avec la configuration)
	 * 
	 * @param file fichier a d�placer
	 * @return Vrai si il existe, Faux sinon
	 */
	private Boolean checkIfFileExistInFolderText(File file) {
		File textsFolder = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		if (null != textsFolder) {
			File newFile = new File(textsFolder.getAbsolutePath(), file.getName());
			return newFile.exists();
		}
		return Boolean.FALSE;
	}

	/**
	 * Permet de d�placer le fichier dans le dossier des textes
	 * 
	 * @param file fichier � d�placer
	 * @return le path du fichier d�plac� (nouveau fichier)
	 * @throws IOException
	 */
	private Path moveFileInFolderText(File file) throws IOException {
		File textsFolder = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		if (null != textsFolder) {
			return PathUtils.moveFile(file, textsFolder);
		}
		return null;
	}

}
