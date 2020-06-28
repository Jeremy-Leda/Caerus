package model.analyze;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import model.analyze.beans.Configuration;
import model.analyze.beans.CurrentUserConfiguration;
import model.analyze.beans.FilesToAnalyzeInformation;
import model.analyze.beans.MemoryFile;
import model.analyze.beans.SaveCurrentFixedText;
import model.analyze.beans.SpecificConfiguration;
import model.analyze.beans.StructuredField;
import model.analyze.beans.StructuredFile;
import model.analyze.beans.StructuredText;
import model.analyze.beans.specific.ConfigurationStructuredText;
import model.analyze.constants.FolderSettingsEnum;
import model.excel.CreateExcel;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import model.exceptions.LoadTextException;
import model.exceptions.MoveFileException;
import utils.JSonFactoryUtils;
import utils.PathUtils;
import utils.RessourcesUtils;

/**
 * 
 * Classe en charge de dispatcher les informations en chargeant les données dans
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
	private static final String CONFIGURATION_CLASSIC_NAME = "Configuración básica";
	private static final String FILE_CURRENT_STATE = "currentState.pyl";
	private static final String FILE_CURRENT_USER_CONFIGURATION = "currentUserConfiguration.pyl";
	private static final String ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT = "ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT";

	/**
	 * Constructeur
	 */
	public Dispatcher() {
		try {
			createDefaultConfigurationIfFolderIsEmpty();
			loadContextFromUserConfiguration();
			UserSettings.getInstance().clearCurrentFolderUserTextsMap();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Permet de lancer l'analyse des textes dans le dossier d'analyse
	 * 
	 * @param depth Profondeur de recherche dans le dossier
	 * @throws IOException
	 * @throws LoadTextException
	 */
	public void launchAnalyze(Integer depth) throws IOException, LoadTextException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_ANALYZE, depth);
	}

	/**
	 * Permet de charger les textes dans le folder des textes
	 * 
	 * @throws IOException
	 * @throws LoadTextException
	 */
	public void loadTexts() throws IOException, LoadTextException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_TEXTS, 1);
	}

	/**
	 * Permet traiter et de charger les textes
	 * 
	 * @param folderType type du dossier à prendre en charge
	 * @param depth      Profondeur de recherche dans le dossier
	 * @throws IOException       erreur d'éntrée sortie
	 * @throws LoadTextException Exception dû au chargement des textes
	 */
	private void processAndLoadTexts(FolderSettingsEnum folderType, Integer depth)
			throws IOException, LoadTextException {
		logger.debug(String.format("CALL processAndLoadTexts => type %s", folderType));
		File pathToProcess;
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
			pathToProcess = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		} else {
			pathToProcess = UserSettings.getInstance().getFolder(folderType);
		}
		List<MemoryFile> memoryFiles = getMemoryFiles(pathToProcess.toString(), depth);
		UserSettings.getInstance().clearAllSession(folderType);
		UserSettings.getInstance().addMemoryFilesList(folderType, memoryFiles);
		memoryFiles.stream().map(f -> new Structuring(f, folderType).getStructuredFile())
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
	 * Méthode permettant de lancer la montée en mémoire de l'ensemble du répertoire
	 * à traiter
	 * 
	 * @param pathFolderToAnalyze Répertoire à analyser
	 * @param depth               Profondeur de la recherche des fichiers
	 * @return la liste des fichiers en mémoire
	 * @throws IOException
	 */
	private List<MemoryFile> getMemoryFiles(String pathFolderToAnalyze, Integer depth) throws IOException {
		final List<MemoryFile> listeSortie = new ArrayList<MemoryFile>();
		Files.walkFileTree(Paths.get(pathFolderToAnalyze), EnumSet.noneOf(FileVisitOption.class), depth,
				new SimpleFileVisitor<Path>() {
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
	 * Méthode permettant de se procurer la liste des fichiers a traité et la
	 * possibilité de pouvoir les traiter
	 * 
	 * @param pathFolderToAnalyze Répertoire à analyser
	 * @param depth               profondeur pour la recherche
	 * @return la liste des fichiers a traité et la possibilité de pouvoir les
	 *         traiter
	 * @throws IOException
	 */
	public FilesToAnalyzeInformation getNameFileToAnalyzeList(File pathFolderToAnalyze, Integer depth)
			throws IOException {
		final List<String> nameFileList = new ArrayList<String>();
		final List<Boolean> launchAnalyzeIsOkList = new ArrayList<Boolean>();
		Files.walkFileTree(pathFolderToAnalyze.toPath(), EnumSet.noneOf(FileVisitOption.class), depth,
				new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (!Files.isDirectory(file)) {
							String fileName = file.getFileName().toString();
							nameFileList.add(fileName);
							launchAnalyzeIsOkList.add("txt".equals(FilenameUtils.getExtension(fileName)));
						}
						return FileVisitResult.CONTINUE;
					}
				});
		boolean launchAnalyzeIsOk = launchAnalyzeIsOkList.stream().allMatch(b -> Boolean.TRUE.equals(b));
		return new FilesToAnalyzeInformation(nameFileList, launchAnalyzeIsOk);
	}

	/**
	 * Permet de définir le dossier des textes
	 * 
	 * @param textsFolder dossier des textes
	 */
	public void setTextsFolder(File textsFolder) {
		UserSettings.getInstance().setFolder(FolderSettingsEnum.FOLDER_TEXTS, textsFolder);
		saveUserConfiguration();
	}
	
	/**
	 * Permet de changer la configuration et de sauvegarder le changement
	 * @param currentConfiguration Configuration courante
	 */
	public void setCurrentConfigurationWithSaveUserConfiguration(Configuration currentConfiguration) {
		UserSettings.getInstance().setCurrentConfiguration(currentConfiguration);
		saveUserConfiguration();
	}

	/**
	 * Permet de générer le fichier excel
	 * 
	 * @param folder Type de dossier
	 * @param cmd    commande de génération
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
	 * permet de générer de l'excel classique
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
	 * permet de générer un excel custom
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
	 * Permet de générer le fichier structuré en fonction d'une configuration
	 * spécifique
	 * 
	 * @param memoryFiles           liste des fichiers mémoires
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
	 * Permet de générer un fichier excel pour une configuraton spécifique On
	 * modifie le nom du fichier pour ajouter un suffixe en provenance de la
	 * configuration
	 * 
	 * @param commonPath            chemin du fichier générale
	 * @param configurationSpecific configuration spécifique
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
	 * Permet d'écrire le corpus
	 * 
	 * @throws IOException Erreur d'entrée sortie
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
	 * Permet de sauvegarder temporairement l'état
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
	 * Permet de supprimer le fichier d'enregistrement temporaire
	 */
	public void removeCurrentStateFile() {
		PathUtils.deleteFile(getCurrentStateFile());
	}

	/**
	 * Permet d'écrire le corpus
	 * 
	 * @throws IOException Erreur d'entrée sortie
	 */
	public void writeFixedText() throws IOException {
		List<String> filesList = UserSettings.getInstance().getUserStructuredTextList(FolderSettingsEnum.FOLDER_ANALYZE)
				.stream().map(ust -> ust.getFileName()).distinct().collect(Collectors.toList());
		writeText(FolderSettingsEnum.FOLDER_ANALYZE, filesList);
		UserSettings.getInstance().clearAfterWriteFixedText();
		PathUtils.deleteFile(getCurrentStateFile());
	}

	/**
	 * Permet d'écrire le texte en cours d'édition
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
	 * Permet d'écrire le fichier
	 * 
	 * @param folderType type du dossier
	 * @param filesList  liste des fichiers à écrire
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
	 * Permet de supprimer définitivement un texte Suppression logique et physique
	 * 
	 * @param key        Clé du texte
	 * @param folderType type du dossier
	 * @throws IOException
	 */
	public void deleteTextAndWriteCorpus(String key, FolderSettingsEnum folderType) throws IOException {
		String fileName = UserSettings.getInstance().getCorpusNameOfText(key, folderType);
		UserSettings.getInstance().deleteText(key, folderType);
		writeText(folderType, Arrays.asList(fileName.toString()));
	}

	/**
	 * Permet de connaitre l'existance d'un fichier d'état
	 * 
	 * @return Vrai si un fichier d'état existe
	 */
	public Boolean haveCurrentStateFile() {
		return getCurrentStateFile().exists();
	}

	/**
	 * Permet de restaurer l'état courant
	 * 
	 * @throws IOException          Erreur d'entrée sorties
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
	 * Permet de se procurer le fichier d'état Si le repertoire n'existe pas, il
	 * sera créé lors du passage dans cette méthode. Le fichier quand à lui ne sera
	 * pas créé
	 * 
	 * @return
	 */
	private File getCurrentStateFile() {
		String rootPath = PathUtils.getCaerusFolder();
		File parentFile = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONTEXT);
		return new File(parentFile, FILE_CURRENT_STATE);
	}

	/**
	 * Permet de se procurer le fichier de configuration utililisateur Si le
	 * repertoire n'existe pas, il sera créé lors du passage dans cette méthode. Le
	 * fichier quand à lui ne sera pas créé
	 * 
	 * @return
	 */
	private File getUserConfigurationFile() {
		String rootPath = PathUtils.getCaerusFolder();
		File parentFile = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONTEXT);
		return new File(parentFile, FILE_CURRENT_USER_CONFIGURATION);
	}

	/**
	 * Permet de charger le context à partir de la configuration utilisateur
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
			currentUserConfiguration = JSonFactoryUtils.createObjectFromJsonFile(is, CurrentUserConfiguration.class);
		} else {
			currentUserConfiguration = new CurrentUserConfiguration();
			String rootPath = PathUtils.getCaerusFolder();
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
	 * Permet de sauvegarder temporairement l'état
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
	 * Permet de se procurer la liste des champs à process
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
	 * Permet de déplacer les fichiers de l'analyze vers la librairie
	 * 
	 * @return La map des fichiers déplacés (key = ancien fichier, value = nouveau
	 *         fichier)
	 * @throws IOException       exception d'écriture
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
	 * @param file fichier a déplacer
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
	 * Permet de déplacer le fichier dans le dossier des textes
	 * 
	 * @param file fichier à déplacer
	 * @return le path du fichier déplacé (nouveau fichier)
	 * @throws IOException
	 */
	private Path moveFileInFolderText(File file) throws IOException {
		File textsFolder = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		if (null != textsFolder) {
			return PathUtils.moveFile(file, textsFolder);
		}
		return null;
	}

	/**
	 * Permet d'ajouter la configuration par défaut si le dossier des configurations
	 * est vide
	 * 
	 * @throws IOException          Exception d'entrée sortie
	 * @throws JsonMappingException Json mapping exception
	 * @throws JsonParseException   Json parse exception
	 */
	private void createDefaultConfigurationIfFolderIsEmpty()
			throws JsonParseException, JsonMappingException, IOException {
		String rootPath = PathUtils.getCaerusFolder();
		File configurationsPath = PathUtils.addFolderAndCreate(rootPath, FOLDER_CONFIGURATIONS);
		if (configurationsPath.isDirectory() && configurationsPath.list().length == 0) {
			Configuration basicalConfiguration = RessourcesUtils.getInstance().getBasicalConfiguration();
			JSonFactoryUtils.createJsonInFile(basicalConfiguration,
					new File(configurationsPath, CONFIGURATION_CLASSIC_NAME + ".json"));
		}
	}

	/**
	 * Permet de sauvegarder tous les documents
	 * 
	 * @param directory Repertoire de sauvegarde
	 * @throws IOException Erreur d'entrée sortie
	 */
	public void exportAllDocuments(File directory) throws IOException {
		File directoryForSaveTextsInLibrary = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		for (File file : directoryForSaveTextsInLibrary.listFiles()) {
			PathUtils.copyFile(file, new File(directory, file.getName()));
		}
	}

	/**
	 * Permet de sauvegarder un document
	 * 
	 * @param directory Repertoire de sauvegarde
	 * @param nameFile  Nom du fichier à sauvegarder
	 * @throws IOException Erreur d'entrée sortie
	 */
	public void exportDocument(File directory, String nameFile) throws IOException {
		File directoryForSaveTextsInLibrary = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		Optional<File> optionalFileFound = Arrays.asList(directoryForSaveTextsInLibrary.listFiles()).stream()
				.filter(f -> StringUtils.defaultString(nameFile).equals(f.getName())).findFirst();
		if (optionalFileFound.isPresent()) {
			PathUtils.copyFile(optionalFileFound.get(), new File(directory, nameFile));
		} else {
			throw new IOException("File not found");
		}
	}

	/**
	 * Permet de sauvegarder les textes du résultat de la recherche dans un nouveau
	 * document
	 * 
	 * @param directory Repertoire de sauvegarde
	 * @param nameFile  Nom du fichier pour la sauvegarde
	 * @throws IOException Erreur d'entrée sortie
	 */
	public void exportResultOfSearch(File directory, String nameFile) throws IOException {
		List<StructuredText> structuredTextList = UserSettings.getInstance()
				.getUserStructuredTextList(FolderSettingsEnum.FOLDER_TEXTS).parallelStream()
				.filter(ust -> UserSettings.getInstance().getKeysFilteredList().contains(ust.getKey()))
				.map(ust -> ust.getStructuredText()).collect(Collectors.toList());
		writeCorpus(directory, nameFile, structuredTextList);
	}

	/**
	 * Permet d'écrire le corpus
	 * 
	 * @param structuredTextList Liste des textes structurés
	 * @throws IOException Erreur d'entrée sortie
	 */
	public void writeCorpus(File directory, String nameFile, List<StructuredText> structuredTextList)
			throws IOException {
		try (Writer writer = new Writer(directory, nameFile)) {
			UserSettings.getInstance().writeCorpus(writer, structuredTextList);
		}
	}

}
