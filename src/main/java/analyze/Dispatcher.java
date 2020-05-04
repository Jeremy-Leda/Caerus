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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import analyze.beans.Configuration;
import analyze.beans.LineError;
import analyze.beans.MemoryFile;
import analyze.beans.SaveCurrentFixedText;
import analyze.beans.SpecificConfiguration;
import analyze.beans.StructuredField;
import analyze.beans.StructuredFile;
import analyze.beans.StructuringError;
import analyze.beans.specific.ConfigurationStructuredText;
import analyze.constants.ErrorTypeEnum;
import analyze.constants.FolderSettingsEnum;
import excel.CreateExcel;
import excel.beans.ExcelGenerateConfigurationCmd;
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
	private List<StructuredFile> structuredFiles = new ArrayList<>();
	private Boolean excelCreated = Boolean.FALSE;
	private List<ConfigurationStructuredText> configurationStructuredTextListe = new ArrayList<>();
	private static final String FOLDER_CONTEXT = "context";
	private static final String FILE_CURRENT_STATE = "currentState.pyl";

	/**
	 * Permet de lancer l'analyse des textes dans le dossier d'analyse
	 * @throws IOException 
	 */
	public void launchAnalyze() throws IOException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_ANALYZE);
	}
	
	/**
	 * Permet de charger les textes dans le folder des textes
	 * @throws IOException 
	 */
	public void loadTexts() throws IOException {
		processAndLoadTexts(FolderSettingsEnum.FOLDER_TEXTS);
	}
	
	/**
	 * Permet de d�finir la configuration courante
	 * @param configuration configuration
	 */
	public void setCurrentConfiguration(Configuration configuration) {
		UserSettings.getInstance().setCurrentConfiguration(configuration);
	}
	
	private void processAndLoadTexts(FolderSettingsEnum folderType) throws IOException {
		//UserSettings.getInstance().setCurrentConfiguration(configuration);
		//this.setAnalyzeFolder(UserSettings.getInstance().getFolder(folderType));
		logger.debug("Analyze : " + UserSettings.getInstance().getFolder(folderType));
		List<MemoryFile> memoryFiles = getMemoryFiles(UserSettings.getInstance().getFolder(folderType).toString());
		UserSettings.getInstance().clearAllSession();
		UserSettings.getInstance().addMemoryFilesList(memoryFiles);
		structuredFiles = memoryFiles.parallelStream().map(f -> new Structuring(f, folderType).getStructuredFile()).collect(Collectors.toList());
		if (UserSettings.getInstance().getNbLineError() > 0) {
			return;
		}
		UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().stream()
				.forEach(sc -> configurationStructuredTextListe.add(new ConfigurationStructuredText(sc)));
		configurationStructuredTextListe.stream().forEach(st -> structuredTextSpecificProcess(memoryFiles, folderType, st));

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
	 * Permet de se procurer la liste des fichiers structur�s
	 * 
	 * @return
	 */
	public List<StructuredFile> getStructuredFiles() {
		return this.structuredFiles;
	}

	/**
	 * Permet de se procurer le dossier des textes
	 * 
	 * @return le dossier des textes
	 */
	public File getTextsFolder() {
		return UserSettings.getInstance().getFolder(FolderSettingsEnum.FOLDER_TEXTS);
	}

	/**
	 * Permet de d�finir le dossier des textes
	 * 
	 * @param textsFolder dossier des textes
	 */
	public void setTextsFolder(File textsFolder) {
		UserSettings.getInstance().setFolder(FolderSettingsEnum.FOLDER_TEXTS, textsFolder);
	}

	/**
	 * Permet de se procurer le dossier d'analyse des textes
	 * 
	 * @return le dossier d'analyse des textes
	 */
	public File getAnalyzeFolder() {
		return UserSettings.getInstance().getFolder(FolderSettingsEnum.FOLDER_ANALYZE);
	}

	/**
	 * Permet de d�finir le dossier d'analyse des textes
	 * 
	 * @param textsAnalyze dossier d'analyse des textes
	 */
	public void setAnalyzeFolder(File textsAnalyze) {
		UserSettings.getInstance().setFolder(FolderSettingsEnum.FOLDER_ANALYZE, textsAnalyze);
	}

	/**
	 * Permet de fournir une liste des champs m�ta de la configuration courante
	 * 
	 * @return la liste des champs m�ta
	 */
	public Map<String, String> getListFieldMetaFile() {
		return UserSettings.getInstance().getListFieldMetaFile();
	}

	/**
	 * Permet de fournir une liste des champs commun de la configuration courante
	 * (non m�ta et non sp�cifique)
	 * 
	 * @return la liste des champs commun
	 */
	public Map<String, String> getListFieldCommonFile() {
		return UserSettings.getInstance().getListFieldCommonFile();
	}

	/**
	 * Permet de se procurer les champs en t�te de la configuration sp�cifique
	 * d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	public Map<String, String> getListFieldHeaderSpecific(Integer index) {
		return UserSettings.getInstance().getListFieldHeaderSpecific(index);
	}

	/**
	 * Permet de se procurer les champs de la configuration sp�cifique d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	public Map<String, String> getListFieldSpecific(Integer index) {
		return UserSettings.getInstance().getListFieldSpecific(index);
	}

	/**
	 * Permet de connaitre le nombre maximum de configuration sp�cifique
	 * 
	 * @return le nombre de configuration sp�cifique
	 */
	public Integer getNbSpecificConfiguration() {
		return UserSettings.getInstance().getNbSpecificConfiguration();
	}

	/**
	 * Permet de se procurer le nom de la configuration
	 * 
	 * @return le nom de la configuration
	 */
	public String getConfigurationName() {
		return UserSettings.getInstance().getCurrentConfiguration().getName();
	}

	/**
	 * Permet de cr�er le fichier excel
	 * 
	 * @param path chemin du fichier excel
	 * @throws IOException erreur IO
	 */
	public void createExcel(File path) throws IOException {
//		excelCreated = Boolean.FALSE;
//		ExcelStructuring es = new ExcelStructuring();
//		List<List<String>> rows = es.getStructuringRows(this.structuredFiles, UserSettings.getInstance().getCurrentConfiguration());
//		CreateExcel ce = new CreateExcel(path);
//		rows.forEach(r -> ce.createRow(r));
//		ce.generateExcel();
//		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
//		configurationStructuredTextListe.stream().forEach(st -> {
//			try {
//				createExcelSpecific(path, st);
//			} catch (IOException e) {
//				System.err.println("erreur");
//			}
//		});
//		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
//		excelCreated = Boolean.TRUE;
	}

	/**
	 * Permet de g�n�rer le fichier excel
	 * 
	 * @param cmd commande de g�n�ration
	 * @throws IOException
	 */
	public void generateExcel(ExcelGenerateConfigurationCmd cmd) throws IOException {
		if (cmd.getHaveToGenerateReferenceText()) {
			generateClassicalExcel(cmd);
		} else {
			generateCustomExcel(cmd);
		}
	}

	/**
	 * permet de g�n�rer de l'excel classique
	 * @param cmd cmd
	 * @throws IOException
	 */
	private void generateClassicalExcel(ExcelGenerateConfigurationCmd cmd) throws IOException {
		excelCreated = Boolean.FALSE;
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(this.structuredFiles, UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
		rows.forEach(r -> ce.createRow(r));
		ce.generateExcel();
		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
		cmd.getMapLabelSpecificFileName().forEach((key, value) -> {
			Optional<ConfigurationStructuredText> findFirstCst = configurationStructuredTextListe.stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(key)).findFirst();
			if (findFirstCst.isPresent()) {
				List<StructuredField> listSf = UserSettings.getInstance().getCurrentConfiguration().getStructuredFieldList().stream()
						.filter(field -> !findFirstCst.get().getSpecificConfiguration().getIgnoredFieldList().contains(field.getFieldName()))
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
		/* -- SPECIFIC CONFIGURATION EXCEL PROCESSING -- */
		excelCreated = Boolean.TRUE;
	}
	
	/**
	 * permet de g�n�rer un excel custom
	 * @param cmd command
	 * @throws IOException
	 */
	private void generateCustomExcel(ExcelGenerateConfigurationCmd cmd) throws IOException {
		excelCreated = Boolean.FALSE;
		if (!cmd.getIsSpecificGeneration()) {
			ExcelStructuring es = new ExcelStructuring();
			List<List<String>> rows = es.getStructuringRows(this.structuredFiles, UserSettings.getInstance().getCurrentConfiguration(), cmd);
			CreateExcel ce = new CreateExcel(new File(cmd.getFileName()));
			rows.forEach(r -> ce.createRow(r));
			ce.generateExcel();
		} else {
			Optional<ConfigurationStructuredText> findFirstCst = configurationStructuredTextListe.stream()
					.filter(s -> s.getSpecificConfiguration().getLabel().equals(cmd.getLabelSpecificChoose())).findFirst();
			if (findFirstCst.isPresent()) {
				try {
					createExcelSpecific(new File(cmd.getFileName()), findFirstCst.get(), cmd);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		excelCreated = Boolean.TRUE;
	}

	public Boolean getExcelCreated() {
		return excelCreated;
	}

	/* -- GENERIC METHOD FOR SPECIFIC PROCESS -- */

	/**
	 * Permet de g�n�rer le fichier structur� en fonction d'une configuration
	 * sp�cifique
	 * 
	 * @param memoryFiles           liste des fichiers m�moires
	 * @param configurationSpecific configuration specifique
	 */
	private void structuredTextSpecificProcess(List<MemoryFile> memoryFiles, FolderSettingsEnum folderType,
			ConfigurationStructuredText configurationSpecific) {
		List<StructuredFile> structuredFileList = memoryFiles.stream() // TODO passer en parallel
				.map(f -> new Structuring(f, folderType, configurationSpecific).getStructuredFile()).collect(Collectors.toList());
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
	private void createExcelSpecific(File path, ConfigurationStructuredText configurationSpecific, ExcelGenerateConfigurationCmd cmd)
			throws IOException {
		ExcelStructuring es = new ExcelStructuring();
		List<List<String>> rows = es.getStructuringRows(configurationSpecific.getStructuredFileList(),
				UserSettings.getInstance().getCurrentConfiguration(), cmd);
		CreateExcel ce = new CreateExcel(path);
		rows.forEach(r -> ce.createRow(r));
		ce.generateExcel();
	}

	public List<StructuringError> getStructuringErrorList() {
		List<StructuringError> structuringErrorList = new ArrayList<>();
		structuringErrorList.addAll(getStructuringErrorListNotEmpty(structuredFiles));
		configurationStructuredTextListe.stream()
				.forEach(st -> structuringErrorList.addAll(getStructuringErrorListNotEmpty(st.getStructuredFileList())));
		return structuringErrorList;
	}

	private List<StructuringError> getStructuringErrorListNotEmpty(List<StructuredFile> structuredFileList) {
		return structuredFileList.stream().filter(sf -> !sf.getListStructuringError().isEmpty()).flatMap(sf -> sf.getListStructuringError().stream())
				.collect(Collectors.toList());
	}

	/**
	 * Permet de traiter les erreurs et de sp�cifier si des erreurs ont �t� trait�s
	 * 
	 * @return Vrai si erreur, faux sinon
	 * @throws IOException
	 */
	public Boolean errorProcessing() throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(errorProcessing(structuredFiles));
		configurationStructuredTextListe.stream().forEach(st -> sb.append(errorProcessing(st.getStructuredFileList())));
		if (sb.length() > 0) {
			Files.write(Paths.get("D:/erreurs.txt"), sb.toString().getBytes());
			return true;
		}
		return false;
	}

	/**
	 * Permet de se procurer les erreurs
	 * 
	 * @param listStructuredFile la liste des fichiers structur� � traiter
	 * @return la liste des erreurs
	 */
	private String errorProcessing(List<StructuredFile> listStructuredFile) {
		final StringBuilder sb = new StringBuilder();
		if (listStructuredFile != null) {
			listStructuredFile.stream().filter(s -> !s.getListStructuringError().isEmpty()).flatMap(s -> s.getListStructuringError().stream())
					.forEach(s -> {
						sb.append(errorProcessing(s)).append("\n\n");
					});
		}
		return sb.toString();
	}

	/**
	 * Permet de g�rer une erreur de structure
	 * 
	 * @param structuringError erreur de structure � traiter
	 * @return l'erreur trait�
	 */
	private String errorProcessing(StructuringError structuringError) {
		final StringBuilder sb = new StringBuilder("/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\\n");
		sb.append("Erreur de structure � l'emplacement suivant : \n");
		sb.append("Fichier : ").append(structuringError.getKeyFile()).append("\n");
		sb.append("Emplacement concern� : \n").append(structuringError.getKeyText()).append("\n");
		sb.append("D�tail : \n");
		structuringError.getDetails().forEach(s -> {
			sb.append("=> Cl� concern� ").append(s.getKeyStructure()).append(" avec ").append(s.getListElements().size()).append(" �l�ment(s) : \n");
			s.getListElements().forEach(e -> {
				sb.append("=> => ").append(e).append("\n");
			});
		});
		sb.append("/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\/*\\\n");
		return sb.toString();
	}

	/**
	 * Permet de cr�er un nouveau corpus
	 * 
	 * @param nameFile         nom du fichier
	 * @param metaFileFieldMap map des champ metafile
	 */
	public void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap) {
		UserSettings.getInstance().createNewCorpus(nameFile, metaFileFieldMap);
	}

	/**
	 * Permet de d�finir si un corpus est en cours d'�dition
	 * 
	 * @return Vrai si un corpus est en cours d'�dition
	 */
	public Boolean haveEditingCorpus() {
		return UserSettings.getInstance().haveEditingCorpus();
	}

	/**
	 * Permet de supprimer le corpus en cours d'�dition
	 */
	public void clearEditingCorpus() {
		UserSettings.getInstance().clearEditingCorpus();
	}

	/**
	 * Permet de se procurer le nom du corpus en cours d'�dition
	 * 
	 * @return le nom du corpus
	 */
	public String getEditingCorpusName() {
		return UserSettings.getInstance().getEditingCorpusNameFile();
	}

	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param key Cl� du champ � r�cup�rer
	 * @return la valeur
	 */
	public String getFieldInEditingCorpus(String key) {
		return UserSettings.getInstance().getFieldInEditingCorpus(key);
	}

	/**
	 * Permet de mettre � jour un champ de le corpus en cours d'�dition
	 * 
	 * @param key   Cl�
	 * @param value Valeur
	 */
	public void updateFieldInEditingCorpus(String key, String value) {
		UserSettings.getInstance().updateFieldInEditingCorpus(key, value);
	}

	/**
	 * Permet de mettre � jour la liste des champs sp�cifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilis�
	 * @param specificFieldMap champ � mettre � jour
	 */
	public void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap) {
		UserSettings.getInstance().updateSpecificFieldInEditingCorpus(index, specificFieldMap);
	}

	/**
	 * Permet de se procurer la map des champs sp�cifique
	 * 
	 * @param index index de la configuration utilis�
	 * @return la map des champs sp�cifique
	 */
	public Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index) {
		return UserSettings.getInstance().getSpecificFieldInEditingCorpus(index);
	}

	/**
	 * Permet d'ajouter le texte en cours d'�dition au corpus courant
	 */
	public void addEditingTextToCurrentCorpus() {
		UserSettings.getInstance().addEditingTextToCurrentCorpus();
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
		try (Writer writer = new Writer(UserSettings.getInstance().getFolder(FolderSettingsEnum.FOLDER_TEXTS), fileName.toString())) {
			UserSettings.getInstance().writeCorpus(writer);
		}
	}

	/**
	 * Permet de se procurer le nb de ligne en erreur
	 * 
	 * @return le nb de ligne en erreur
	 */
	public Integer getNbLinesError() {
		return UserSettings.getInstance().getNbLineError();
	}

	/**
	 * Permet de se procurer une ligne d'erreur
	 * 
	 * @param index index de la ligne
	 * @return la ligne en erreur
	 */
	public LineError getErrorLine(Integer index) {
		return UserSettings.getInstance().getLineError(index);
	}

	/**
	 * Permet de mettre � jour une ligne en erreur
	 * 
	 * @param index     index de la ligne
	 * @param lineError ligne erreur � mettre � jour
	 */
	public void updateLineError(Integer index, LineError lineError) {
		logger.debug("[DEBUT] updateLineError");
		UserSettings.getInstance().updateLineError(index, lineError);
		logger.debug("[FIN] updateLineError");
	}

	/**
	 * Permet de mettre � jour les fichiers avec les lignes en erreur
	 * 
	 * @throws IOException
	 */
	public void saveFileAfteFixedErrorLine() throws IOException {
		logger.debug("[DEBUT] saveFileAfteFixedErrorLine");
		UserSettings.getInstance().fixedErrorLinesInAllMemoryFiles();
		logger.debug("[FIN] saveFileAfteFixedErrorLine");
	}

	/**
	 * Permet de se procurer le nombre de texte en erreur
	 * 
	 * @return le nombre de texte en erreur
	 */
	public Integer getNbTextsError() {
		return UserSettings.getInstance().getNbTextsError();
	}

	/**
	 * Permet de charger le texte en erreur suivant
	 * 
	 */
	public void loadNextErrorText() {
		logger.debug("[DEBUT] loadErrorText");
		UserSettings.getInstance().loadErrorText(UserSettings.getInstance().getKeysStructuredTextErrorList().get(0), ErrorTypeEnum.STRUCTURED_TEXT);
		logger.debug("[FIN] loadErrorText");
	}

	/**
	 * Permet d'appliquer la correction sur les textes structur�s utilisateur �
	 * partir du texte en cours
	 */
	public void applyFixedErrorText() {
		logger.debug("[DEBUT] applyFixedErrorText");
		UserSettings.getInstance().applyCurrentTextToStructuredText();
		logger.debug("[FIN] applyFixedErrorText");
	}

	/**
	 * Permet de sauvegarder temporairement l'�tat
	 */
	public void saveCurrentStateOfFixedText() {
		logger.debug("[DEBUT] saveCurrentStateOfFixedText");
		SaveCurrentFixedText save = UserSettings.getInstance().getSaveCurrentFixedText();
		try {
			JSonFactoryUtils.createJsonInFile(save, getCurrentStateFile());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("[FIN] saveCurrentStateOfFixedText");
	}

	/**
	 * Permet d'�crire le corpus
	 * 
	 * @throws IOException Erreur d'entr�e sortie
	 */
	public void writeFixedText() throws IOException {
		logger.debug("[DEBUT] writeFixedText");
		List<String> filesList = UserSettings.getInstance().getUserStructuredTextList().stream().map(ust -> ust.getFileName()).distinct()
				.collect(Collectors.toList());
		for (String file : filesList) {
			try (Writer writer = new Writer(getAnalyzeFolder(), file)) {
				UserSettings.getInstance().writeFixedText(writer, file);
			}
		}
		UserSettings.getInstance().clearAfterWriteFixedText();
		PathUtils.deleteFile(getCurrentStateFile());
		logger.debug("[FIN] writeFixedText");
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
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException {
		InputStream is = new FileInputStream(getCurrentStateFile());
		SaveCurrentFixedText saveCurrentFixedText = JSonFactoryUtils.createObjectFromJsonFile(is, SaveCurrentFixedText.class);
		UserSettings.getInstance().restoreCurrentFixedTest(saveCurrentFixedText);
	}

	/**
	 * Permet de savoir si il y a des textes en erreurs restants
	 * 
	 * @return Vrai si il y a des textes en erreurs restants
	 */
	public Boolean haveTextsInErrorRemaining() {
		logger.debug("[INFO] haveTextsInErrorRemaining");
		return UserSettings.getInstance().haveTextsInErrorRemaining();
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
	 * Permet de se procurer le nombre de ligne vide en erreur
	 * 
	 * @return le nombre de ligne vide en erreur
	 */
	public Integer getNbBlankLinesError() {
		return UserSettings.getInstance().getNbBlankLineError();
	}

	/**
	 * Permet de charger le texte en erreur suivant
	 * 
	 */
	public void loadNextErrorBlankLine() {
		logger.debug("[DEBUT] loadNextErrorBlankLine");
		UserSettings.getInstance().loadErrorText(UserSettings.getInstance().getKeysBlankLineErrorList().get(0), ErrorTypeEnum.BLANK_LINE);
		logger.debug("[FIN] loadNextErrorBlankLine");
	}

	/**
	 * Permet de connaitre le nombre de texte charg�
	 * 
	 * @return le nombre de texte charg�
	 */
	public Integer getNbTextLoaded() {
		return UserSettings.getInstance().getUserStructuredTextList().size();
	}

	/**
	 * Permet de savoir si il y a des lignes vide restants
	 * 
	 * @return Vrai si il y a des lignes vide restants
	 */
	public Boolean haveBlankLinesInErrorRemaining() {
		return UserSettings.getInstance().haveBlankLineInErrorRemaining();
	}
	
	/**
	 * Permet de savoir si il y a des erreurs de ligne vide dans les balises m�ta
	 * @return Vrai si c'est le cas
	 */
	public Boolean haveMetaBlankLineError() {
		return UserSettings.getInstance().haveMetaBlankLineError();
	}
	
	/**
	 * Permet de savoir s'il reste des meta vides dans les textes
	 * @return Vrai si oui
	 */
	public Boolean haveMetaBlankLineInErrorRemaining() {
		return UserSettings.getInstance().haveMetaBlankLineInErrorRemaining();
	}

	/**
	 * Permet de charger le texte en erreur suivant
	 * 
	 */
	public void loadNextErrorMetaBlankLine() {
		logger.debug("[DEBUT] loadNextErrorMetaBlankLine");
		UserSettings.getInstance().loadErrorText(UserSettings.getInstance().getKeysMetaBlankLineErrorList().get(0), ErrorTypeEnum.META_BLANK_LINE);
		logger.debug("[FIN] loadNextErrorMetaBlankLine");
	}
	
	/**
	 * Permet de se procurer la map des configuration specifique (label, suffix du
	 * fichier)
	 * 
	 * @return la map
	 */
	public Map<String, String> getConfigurationSpecificLabelNameFileMap() {
		return UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList().stream()
				.collect(Collectors.toMap(SpecificConfiguration::getLabel, SpecificConfiguration::getNameFileSuffix));
	}

	/**
	 * Permet de se procurer la configuration courante avec le champ et le label
	 * associ�
	 * 
	 * @return la map
	 */
	public Map<String, String> getFieldConfigurationNameLabelMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		UserSettings.getInstance().getCurrentConfiguration().getStructuredFieldList().stream().sorted(Comparator.comparing(StructuredField::getOrder))
				.forEach(sf -> {
					map.put(sf.getFieldName(), sf.getLabel());
				});
		return map;
	}

	/**
	 * Permet de se procurer la liste des champs � process
	 * 
	 * @param labelSpecificConfiguration label du specific dont on souhaite les
	 *                                   champs
	 * @return la liste
	 */
	public List<String> getFieldListToProcess(String labelSpecificConfiguration) {
		Optional<SpecificConfiguration> findFirstSpecific = UserSettings.getInstance().getCurrentConfiguration().getSpecificConfigurationList()
				.stream().filter(sc -> sc.getLabel().equals(labelSpecificConfiguration)).findFirst();
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
		List<SpecificConfiguration> forbiddenSpecificConfigurationList = UserSettings.getInstance().getCurrentConfiguration()
				.getSpecificConfigurationList().stream().filter(sc -> !sc.getLabel().equals(labelSpecificConfiguration)).collect(Collectors.toList());
		return forbiddenSpecificConfigurationList.stream().flatMap(sc -> sc.getTreatmentFieldList().stream()).distinct().collect(Collectors.toList());
	}
	
	/**
	 * Permet de savoir le nombre de corpus contenant des lignes vide meta � corriger.
	 * @return Le nombre de corpus contenant des lignes vide meta � corriger.
	 */
	public Integer getNbMetaBlankLineToFixed() {
		return UserSettings.getInstance().getNbMetaBlankLineToFixed();
	}

}
