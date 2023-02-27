package model.analyze;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import model.abstracts.ProgressAbstract;
import model.analyze.beans.*;
import model.analyze.beans.specific.ConfigurationStructuredText;
import model.analyze.cache.FileOrderCache;
import model.analyze.constants.FolderSettingsEnum;
import model.excel.CreateExcel;
import model.excel.ImportExcel;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import model.excel.beans.ExcelImportConfigurationCmd;
import model.excel.beans.StructuredTextExcel;
import model.exceptions.*;
import model.interfaces.IProgressBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JSonFactoryUtils;
import utils.PathUtils;
import utils.RessourcesUtils;
import view.beans.FrequencyOrder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 
 * Classe en charge de dispatcher les informations en chargeant les données dans
 * différentes classes. Aucune communication n'existe entre les autres classes
 * 
 * @author Jeremy
 *
 */
public class Dispatcher extends ProgressAbstract {

	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private static final String FOLDER_CONTEXT = "context";
	private static final String FOLDER_TEXTS = "library";
	private static final String FOLDER_CONFIGURATIONS = "configurations";
	private static final String FOLDER_ANALYZE_CONFIGURATIONS = "configurations_analysis";
	private static final String FOLDER_ANALYZE_REPOSITORY = "repository";
	private static final String ANALYZE_CONFIGURATION_DEFAULT_NAME = "LexicometricAnalyze";
	private static final String ANALYZE_LEMMATIZATION_BY_GRAMMATICAL_CATEGORY_DEFAULT_NAME = "LemmatizationByGrammaticalCategoryAnalyze";
	private static final String CONFIGURATION_CLASSIC_NAME = "Configuración básica";
	private static final String FILE_CURRENT_STATE = "currentState.pyl";
	private static final String FILE_CURRENT_USER_CONFIGURATION = "currentUserConfiguration.pyl";
	private static final String FILE_FREQUENCY_ORDER_REPOSITORY = "frequencyOrder.pyl";
	private static final String ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT = "ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT";

	/**
	 * Constructeur
	 */
	public Dispatcher() {
		try {
			createDefaultConfigurationIfFolderIsEmpty();
			createDefaultAnalyzeConfigurationIfFolderIsEmpty();
			loadContextFromUserConfiguration();
			loadFrequencyOrderFromDisk();
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
	 * @throws IOException       erreur d'entrée sortie
	 * @throws LoadTextException Exception dù au chargement des textes
	 */
	private void processAndLoadTexts(FolderSettingsEnum folderType, Integer depth)
			throws IOException, LoadTextException {
		logger.debug(String.format("CALL processAndLoadTexts => type %s", folderType));
		Integer nbMaxIterate = 1 + UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().size();
		IProgressBean progressBean = super.createProgressBean(nbMaxIterate);
		progressBean.setCurrentIterate(1);
		File pathToProcess;
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
			pathToProcess = UserSettings.getInstance().getDirectoryForSaveTextsInLibrary();
		} else {
			Optional<File> folder = UserFolder.getInstance().getFolder(folderType);
			pathToProcess = folder.orElseThrow(() -> new ServerException().addInformationException(
					new InformationExceptionBuilder()
							.errorCode(ErrorCode.TECHNICAL_ERROR)
							.parameters(Set.of("processAndLoadTexts", folderType.name()))
							.build()));
		}
		List<MemoryFile> memoryFiles = getMemoryFiles(pathToProcess.toString(), depth);
		UserSettings.getInstance().clearAllSession(folderType);
		UserSettings.getInstance().addMemoryFilesList(folderType, memoryFiles);
		progressBean.setNbMaxElementForCurrentIterate(memoryFiles.size());
		for (int i = 0; i < memoryFiles.size(); i++) {
			if (treatmentIsCancelled()) {
				return;
			}
			UserSettings.getInstance().addStructuredFile(folderType,
					new Structuring(memoryFiles.get(i), folderType).getStructuredFile());

			progressBean.setCurrentElementForCurrentIterate(i);
		}
		if (UserSettings.getInstance().getNbLineError() > 0) {
			if (FolderSettingsEnum.FOLDER_ANALYZE.equals(folderType)) {
				return;
			} else {
				throw new LoadTextException(ERROR_IN_LOAD_TEXT_FOR_FOLDER_TEXT);
			}
		}
		UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().stream()
				.forEach(sc -> UserSettings.getInstance().addConfigurationStructuredText(folderType,
						new ConfigurationStructuredText(sc)));
		List<ConfigurationStructuredText> configurationStructuredTextList = UserSettings.getInstance()
				.getConfigurationStructuredTextList(folderType);
		for (int i = 0; i < configurationStructuredTextList.size(); i++) {
			if (treatmentIsCancelled()) {
				return;
			}
			structuredTextSpecificProcess(memoryFiles, folderType, configurationStructuredTextList.get(i), i,
					progressBean);
		}
		UserSettings.getInstance().saveAllErrorForFixed();
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
	 * Méthode permettant de se procurer la liste des fichiers à traiter et la
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
		UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_TEXTS, textsFolder);
		saveUserConfiguration();
	}

	/**
	 * Permet de changer la configuration et de sauvegarder le changement
	 * 
	 * @param currentConfiguration Configuration courante
	 */
	public void setCurrentConfigurationWithSaveUserConfiguration(Configuration currentConfiguration) {
		try {
			createDefaultConfigurationOrderIfFolderIsEmpty(currentConfiguration);
			UserSettings.getInstance().setCurrentConfiguration(currentConfiguration);
			saveUserConfiguration();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * Permet de générer le fichier excel
	 * 
	 * @param folder Type de dossier
	 * @param cmd    commande de génération
	 * @throws IOException
	 */
	public void generateExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd) {
		try {
			if (cmd.getHaveToGenerateReferenceText()) {
				generateClassicalExcel(folder, cmd);
			} else {
				generateCustomExcel(folder, cmd);
			}
		} catch (IOException ex) {
			throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.build());
		}
	}

	/**
	 * permet de générer de l'Excel classique
	 * 
	 * @param folder type de dossier
	 * @param cmd    cmd
	 * @throws IOException
	 */
	private void generateClassicalExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd) throws IOException {
		Integer nbMaxIterate = cmd.getMapLabelSpecificFileName().size() + 1;
		IProgressBean progressBean = super.createProgressBean(nbMaxIterate);
		Integer currentIterate = 1;
		progressBean.setCurrentIterate(currentIterate);
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(UserSettings.getInstance().getStructuredFileList(folder),
				UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
		rows.forEach(r -> ce.createRow(r));
		ce.generateExcel(progressBean);
		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
		for (Entry<String, String> entry : cmd.getMapLabelSpecificFileName().entrySet()) {
			if (treatmentIsCancelled()) {
				break;
			}
			Optional<ConfigurationStructuredText> findFirstCst = UserSettings.getInstance()
					.getConfigurationStructuredTextList(folder).stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(entry.getKey())).findFirst();
			if (findFirstCst.isPresent()) {
				currentIterate++;
				progressBean.setCurrentIterate(currentIterate);
				List<StructuredField> listSf = UserSettings.getInstance().getCurrentConfiguration()
						.getStructuredFieldList().stream().filter(field -> !findFirstCst.get()
								.getSpecificConfiguration().getIgnoredFieldList().contains(field.getFieldName()))
						.collect(Collectors.toList());
				cmd.clearFieldListGenerate();
				listSf.forEach(sf -> cmd.addFieldToGenerate(sf.getFieldName()));
				cmd.setConfigurationSpecificOrder(findFirstCst.get().getSpecificConfiguration().getOrder());
				createExcelSpecific(new File(entry.getValue()), findFirstCst.get(), cmd, progressBean);
			}
		}
	}

	/**
	 * permet de générer un excel custom
	 * 
	 * @param folder type de dossier
	 * @param cmd    command
	 * @throws IOException
	 */
	private void generateCustomExcel(FolderSettingsEnum folder, ExcelGenerateConfigurationCmd cmd) throws IOException {
		IProgressBean progressBean = super.createProgressBean(1);
		progressBean.setCurrentIterate(1);
		if (!cmd.getIsSpecificGeneration()) {
			ExcelStructuring es = new ExcelStructuring();
			List<List<String>> rows = es.getStructuringRows(UserSettings.getInstance().getStructuredFileList(folder),
					UserSettings.getInstance().getCurrentConfiguration(), cmd);
			CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
			rows.forEach(r -> ce.createRow(r));
			ce.generateExcel(progressBean);
		} else {
			Optional<ConfigurationStructuredText> findFirstCst = UserSettings.getInstance()
					.getConfigurationStructuredTextList(folder).stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(cmd.getLabelSpecificChoose()))
					.findFirst();
			if (findFirstCst.isPresent()) {
				try {
					createExcelSpecific(new File(cmd.getFileName()), findFirstCst.get(), cmd, progressBean);
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
	 * @param configurationSpecific configuration spécifique
	 */
	private void structuredTextSpecificProcess(List<MemoryFile> memoryFiles, FolderSettingsEnum folderType,
			ConfigurationStructuredText configurationSpecific, Integer currentConfigurationSpecificIndex,
			IProgressBean progressBean) {
		for (int i = 0; i < memoryFiles.size(); i++) {
			progressBean.setCurrentIterate(2 + currentConfigurationSpecificIndex);
			progressBean.setNbMaxElementForCurrentIterate(memoryFiles.size());
			progressBean.setCurrentElementForCurrentIterate(i);
			configurationSpecific.getStructuredFileList()
					.add(new Structuring(memoryFiles.get(i), folderType, configurationSpecific).getStructuredFile());
		}
	}

	/**
	 * Permet de générer un fichier excel pour une configuration spécifique On
	 * modifie le nom du fichier pour ajouter un suffixe en provenance de la
	 * configuration
	 * 
	 * @param path            chemin du fichier générale
	 * @param configurationSpecific configuration spécifique
	 * @throws IOException io exception
	 */
	private void createExcelSpecific(File path, ConfigurationStructuredText configurationSpecific,
			ExcelGenerateConfigurationCmd cmd, IProgressBean progressBean) throws IOException {
		cmd.setSpecificConfiguration(Boolean.TRUE);
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(configurationSpecific.getStructuredFileList(),
				UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(path);
		for (List<String> row :rows) {
			if (treatmentIsCancelled()) {
				break;
			}
			ce.createRow(row);
		}
		ce.generateExcel(progressBean);
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
	}

	/**
	 * Permet d'écrire le fichier
	 * 
	 * @param folderType type du dossier
	 * @param filesList  liste des fichiers à écrire
	 * @throws IOException
	 */
	private void writeText(FolderSettingsEnum folderType, List<String> filesList) throws IOException {
		Optional<File> directory = UserFolder.getInstance().getFolder(folderType);
		if (FolderSettingsEnum.FOLDER_TEXTS.equals(folderType)) {
			directory = Optional.ofNullable(UserSettings.getInstance().getDirectoryForSaveTextsInLibrary());
		}
		File realDirectory = directory.orElseThrow(() -> new ServerException().addInformationException(
				new InformationExceptionBuilder()
				.errorCode(ErrorCode.TECHNICAL_ERROR)
				.parameters(Set.of("writeText", folderType.name(), filesList.toString()))
				.build()
				));
		for (String file : filesList) {
			Boolean haveText;
			try (Writer writer = new Writer(realDirectory, file)) {
				haveText = UserSettings.getInstance().writeText(folderType, writer, file);
			}
			if (Boolean.FALSE.equals(haveText)) {
				PathUtils.deleteFile(new File(realDirectory, file));
				Configuration currentConfiguration = UserSettings.getInstance().getCurrentConfiguration();
				FileOrderCache.getInstance().deleteFileOrderInCacheAndSave(currentConfiguration, file);
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
		writeText(folderType, Arrays.asList(fileName));
	}

	/**
	 * Permet de connaitre l'existence d'un fichier d'état
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
	 * Permet de se procurer le fichier de configuration utilisateur Si le
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
	 * Permet de se procurer le fichier des ordres de fréquence Si le
	 * repertoire n'existe pas, il sera créé lors du passage dans cette méthode. Le
	 * fichier quand à lui ne sera pas créé
	 *
	 * @return
	 */
	private File getFrequencyOrderFile() {
		String rootPath = PathUtils.getCaerusFolder();
		File parentFile = PathUtils.addFolderAndCreate(rootPath, FOLDER_ANALYZE_REPOSITORY);
		return new File(parentFile, FILE_FREQUENCY_ORDER_REPOSITORY);
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
		createDefaultConfigurationOrderIfFolderIsEmpty(UserSettings.getInstance().getCurrentConfiguration());
		UserLexicometricAnalysisSettings.getInstance().restoreUserConfiguration(currentUserConfiguration);
		saveUserConfiguration();
	}

	private void loadFrequencyOrderFromDisk() throws JsonParseException, JsonMappingException, IOException {
		File frequencyOrderFile = getFrequencyOrderFile();
		if (frequencyOrderFile.exists()) {
			InputStream is = new FileInputStream(frequencyOrderFile);
			FrequencyOrderRepository frequencyOrderRepositoryFromJsonFile = JSonFactoryUtils.createFrequencyOrderRepositoryFromJsonFile(is);
			UserSettings.getInstance().saveFrequencyOrderSet(frequencyOrderRepositoryFromJsonFile.getFrequencyOrderSet());
		}
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
	 * Permet de sauvegarder le fichier des ordres des fréquences sur le disque
	 */
	public void saveFrequencyOrderInFile() {
		Set<FrequencyOrder> frequencyOrderSet = UserSettings.getInstance().getFrequencyOrderSet();
		FrequencyOrderRepository frequencyOrderRepository = new FrequencyOrderRepository();
		frequencyOrderRepository.setFrequencyOrderSet(frequencyOrderSet);
		try {
			JSonFactoryUtils.createJsonInFile(frequencyOrderRepository, getFrequencyOrderFile());
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
	 * sélectionne le spécifique en paramètre
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
	 * Permet de déplacer les fichiers de l'analyse vers la librairie
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
		File directory = UserFolder.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE)
							.orElseThrow(() -> new ServerException().addInformationException(
									new InformationExceptionBuilder()
									.errorCode(ErrorCode.TECHNICAL_ERROR)
									.parameters(Set.of("moveAllFilesFromTextAnalyzeToLibrary", FolderSettingsEnum.FOLDER_ANALYZE.name()))
									.build()
							));
		for (String file : filesList) {
			File oldFile = new File(directory, file);
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
			File oldFile = new File(directory, file);
			Path newFile = moveFileInFolderText(oldFile);
			resultMapForMoveFiles.put(oldFile.toPath(), newFile);
		}
		return resultMapForMoveFiles;

	}

	/**
	 * Permet de savoir si le fichier existe dans la bibliothèque de textes (chemin
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
		File configurationsPath = checkAndGetDefaultPath(FOLDER_CONFIGURATIONS);
		createDefaultFile(RessourcesUtils.getInstance().getBasicalConfiguration(),
				configurationsPath, CONFIGURATION_CLASSIC_NAME, true);
	}

	/**
	 * Permet d'ajouter la configuration de l'ordre par défaut si le dossier des configurations d'ordre
	 * est vide
	 *
	 * @throws IOException          Exception d'entrée sortie
	 * @throws JsonMappingException Json mapping exception
	 * @throws JsonParseException   Json parse exception
	 */
	private void createDefaultConfigurationOrderIfFolderIsEmpty(Configuration configuration)
			throws JsonParseException, JsonMappingException, IOException {
		File configurationsPath = checkAndGetDefaultPath(RessourcesUtils.FOLDER_ORDER_CONFIGURATION);
		createDefaultFile(new FilesOrder(),
				configurationsPath, configuration.getConfigurationOrderNameFile(), false);
	}

	/**
	 * Permet d'ajouter la configuration pour l'analyse par défaut si le dossier des configurations
	 * est vide
	 *
	 * @throws IOException          Exception d'entrée sortie
	 * @throws JsonMappingException Json mapping exception
	 * @throws JsonParseException   Json parse exception
	 */
	private void createDefaultAnalyzeConfigurationIfFolderIsEmpty()
			throws JsonParseException, JsonMappingException, IOException {
		File configurationsPath = checkAndGetDefaultPath(FOLDER_ANALYZE_CONFIGURATIONS);
		UserFolder.getInstance().putFolder(FolderSettingsEnum.FOLDER_CONFIGURATIONS_LEXICOMETRIC_ANALYSIS,
				configurationsPath);
		createDefaultFile(RessourcesUtils.getInstance().getAnalyzeConfiguration(), configurationsPath,
				ANALYZE_CONFIGURATION_DEFAULT_NAME, true);
		createDefaultFile(RessourcesUtils.getInstance().getLemmatizationByGrammaticalCategoryConfiguration(),
				configurationsPath, ANALYZE_LEMMATIZATION_BY_GRAMMATICAL_CATEGORY_DEFAULT_NAME, true);
	}

	/**
	 * Permet de créer un fichier par défaut avant le chargement de la configuration (si celui ci n'existe pas)
	 * @param object Objet à créer
	 * @param configurationsPath chemin de la configuration
	 * @param fileName nom du fichier
	 * @param <T> Type d'objet
	 */
	private <T> void createDefaultFile(T object, File configurationsPath, String fileName, Boolean addExtension) {
		if (addExtension) {
			fileName = fileName + ".json";
		}
		File defaultFile = new File(configurationsPath, fileName);
		if (!defaultFile.exists()) {
			try {
				JSonFactoryUtils.createJsonInFile(object, defaultFile);
			} catch (IOException e) {
				throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.objectInError(defaultFile)
					.build());
			}
		}
	}

	/**
	 * Permet de vérifier le répertoire par défaut et d'obtenir le chemin
	 * @param folder répertoire par défaut à vérifier
	 * @return le repertoire par défaut validé
	 */
	private File checkAndGetDefaultPath(String folder) {
		String rootPath = PathUtils.getCaerusFolder();
		File configurationsPath = PathUtils.addFolderAndCreate(rootPath, folder);
		if (!configurationsPath.isDirectory()) {
			throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.objectInError(configurationsPath)
					.build());
		}
		return configurationsPath;
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



	/**
	 * Permet de lancer l'import excel
	 * @param excelImportConfigurationCmd commande pour effectuer l'import excel
	 */
    public Set<InformationException> importExcel(ExcelImportConfigurationCmd excelImportConfigurationCmd) throws IOException, ImportExcelException, LoadTextException {

    	Set<InformationException> informationExceptionSet = new HashSet<>();

    	IProgressBean progressBean = super.createProgressBean(3);

		// Check Excel
		progressBean.setCurrentIterate(1);
		progressBean.setNbMaxElementForCurrentIterate(1);
		logger.debug("[IMPORT_EXCEL]Vérification du fichier excel");
		Set<StructuredField> structuredFieldSet = excelImportConfigurationCmd.getFieldToImportList().stream().map(code ->
				excelImportConfigurationCmd.getConfiguration().getStructuredFieldList().stream().filter(structuredField -> structuredField.getFieldName().equals(code)).findFirst().get())
				.collect(Collectors.toSet());
		Map<String, String> labelFieldMap = structuredFieldSet.stream().collect(Collectors.toMap(StructuredField::getLabel, StructuredField::getFieldName));

		ImportExcel importExcel = new ImportExcel(excelImportConfigurationCmd.getFileToImport(), labelFieldMap, excelImportConfigurationCmd.getSheetName());
		boolean checkExcelIsValid = importExcel.checkExcelIsValid();
		if (!checkExcelIsValid) {
			informationExceptionSet.add(new InformationExceptionBuilder()
					.errorCode(ErrorCode.INVALID_FILE_EXCEL)
					.objectInError(excelImportConfigurationCmd)
					.build());
			return informationExceptionSet;
		}
		progressBean.setCurrentElementForCurrentIterate(1);

		progressBean.setCurrentIterate(2);
		progressBean.setNbMaxElementForCurrentIterate(1);
		// Charge le dossier et la configuration
		Configuration oldConfiguration = UserSettings.getInstance().getCurrentConfiguration();
		UserSettings.getInstance().setCurrentConfiguration(excelImportConfigurationCmd.getConfiguration());
		this.loadTexts();

		//Import le excel
		logger.debug("[IMPORT_EXCEL]Création des textes structurés depuis le fichier excel");
		Set<StructuredTextExcel> structuredTextSet = importExcel.readExcelForImport();



		// Si c'est spécifique on retraite les informations
		if (excelImportConfigurationCmd.getIsSpecificImport()) {
			logger.debug("[IMPORT_EXCEL]Retraitement des textes structurés depuis le fichier excel");
			Optional<SpecificConfiguration> specificConfigurationOptional = excelImportConfigurationCmd.getConfiguration().getSpecificConfigurationList().stream().filter(s -> s.getLabel().equals(excelImportConfigurationCmd.getLabelSpecificChoose())).findFirst();
			if (!specificConfigurationOptional.isPresent()) {
				informationExceptionSet.add(new InformationExceptionBuilder()
						.errorCode(ErrorCode.INVALID_SPECIFIC_CONFIGURATION)
						.objectInError(excelImportConfigurationCmd)
						.build());
			}
			SpecificConfiguration specificConfiguration = specificConfigurationOptional.get();
			boolean anyMatchDelimiterPresent = structuredTextSet.stream().anyMatch(structuredTextExcel ->
					specificConfiguration.getTreatmentFieldList().stream().filter(field -> structuredTextExcel.getContent(field).contains(specificConfiguration.getDelimiter())).findFirst().isPresent());
			if (anyMatchDelimiterPresent) {
				informationExceptionSet.add(new InformationExceptionBuilder()
						.errorCode(ErrorCode.INVALID_FILE_EXCEL_SPECIFIC_CONFIGURATION)
						.objectInError(excelImportConfigurationCmd)
						.build());
			}

			Map<String, List<StructuredTextExcel>> keyStructuredTextMap = structuredTextSet.stream().collect(Collectors.groupingBy(s -> s.getUniqueKey()));
			AtomicInteger i = new AtomicInteger();
			i.set(1);
			structuredTextSet = keyStructuredTextMap.entrySet().stream().map(entry -> {
				StructuredTextExcel st = new StructuredTextExcel(i.getAndIncrement());
				st.setUniqueKey(entry.getKey());
				specificConfiguration.getTreatmentFieldList().forEach(field -> {
					st.modifyContent(field, entry.getValue().stream().map(structuredText -> structuredText.getContent(field)).collect(Collectors.joining(specificConfiguration.getDelimiter())));
				});
				return st;
			}).collect(Collectors.toCollection(
					() -> new TreeSet<>(Comparator.comparing(StructuredTextExcel::getNumber))
			));
		}
		progressBean.setCurrentElementForCurrentIterate(1);


		//On sauvegarde
		progressBean.setCurrentIterate(3);
		progressBean.setNbMaxElementForCurrentIterate(1);
		logger.debug("[IMPORT_EXCEL]Mise à jour des informations dans le textes utilisateur en mémoire");
		Set<UserStructuredText> modifyUserStructuredTextSet = new HashSet<>();
		structuredTextSet.forEach(structuredText -> {
			Optional<UserStructuredText> userStructuredTextByKey = UserSettings.getInstance().getUserStructuredTextByKey(FolderSettingsEnum.FOLDER_TEXTS, structuredText.getUniqueKey());
			if (userStructuredTextByKey.isPresent()) {
				structuredText.getListContent().forEach(content -> {
					userStructuredTextByKey.get().getStructuredText().modifyContent(content.getKey(), content.getValue());
				});
				modifyUserStructuredTextSet.add(userStructuredTextByKey.get());
			}
		});
		logger.debug("[IMPORT_EXCEL]Sauvegarde des textes sur le disque");
		List<String> listFiles = modifyUserStructuredTextSet.stream().map(userStructuredText -> userStructuredText.getFileName()).distinct().collect(Collectors.toList());
		writeText(FolderSettingsEnum.FOLDER_TEXTS, listFiles);

		//On reset la configuration
		UserSettings.getInstance().setCurrentConfiguration(oldConfiguration);
		UserSettings.getInstance().clearAllSession(FolderSettingsEnum.FOLDER_TEXTS);
		importExcel.closeAndClean();
		progressBean.setCurrentElementForCurrentIterate(1);

		return informationExceptionSet;
    }

    /**
	 * Permet de changer la configuration courante
     */
	public void setCurrentConfiguration(Configuration configuration) {
    	try {
			createDefaultConfigurationOrderIfFolderIsEmpty(configuration);
			UserSettings.getInstance().setCurrentConfiguration(configuration);
		}catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
