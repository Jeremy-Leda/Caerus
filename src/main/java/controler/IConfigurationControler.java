package controler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import model.analyze.beans.Configuration;
import model.analyze.beans.FilesToAnalyzeInformation;
import model.analyze.beans.StructuredFile;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import model.exceptions.LoadTextException;
import model.exceptions.MoveFileException;
import view.beans.BaseCodeError;
import view.beans.DisplayText;
import view.beans.ErrorStructuredLine;
import view.beans.ExportTypeEnum;
import view.beans.Filter;
import view.beans.InconsistencyError;

/**
 * 
 * Interface du controller pour la jonction au modèle
 * 
 * @author jerem
 *
 */
public interface IConfigurationControler {

	/**
	 * Permet de lancer l'analyse des textes
	 * @param withSubFolder Permet de déterminer si on recherche dans les sous dossiers
	 * @throws LoadTextException
	 */
	void launchAnalyze(Boolean withSubFolder) throws LoadTextException;
	
	/**
	 * Permet de définir la configuration courante
	 * 
	 * @param configuration configuration
	 */
	@Deprecated
	void setCurrentConfiguration(Configuration configuration);
	
	/**
	 * Permet de se procurer la liste des fichiers structurés pour l'analyse
	 * @return la liste des fichiers structuré
	 */
	List<StructuredFile> getListOfStructuredFileForAnalyze();
	
	/**
	 * Permet de se procurer la liste des fichiers structurés pour la bibliothéque
	 * @return la liste des fichiers structuré
	 */
	List<StructuredFile> getListOfStructuredFileForTexts();
	
	/**
	 * Permet de déplacer tous les fichiers de l'analyse vers la library
	 * @return le resultat du déplacement
	 * @throws IOException Erreur d'entrée sorties
	 * @throws MoveFileException Erreur de déplacement des fichiers
	 */
	Map<Path, Path> moveAllFilesFromTextAnalyzeToLibrary() throws IOException, MoveFileException;
	
	/**
	 * Permet de nettoyer les informations de l'analyse
	 */
	void clearAnalyze();
	
	/**
	 * Permet de nettoyer les informations provenant des textes
	 */
	void clearTexts();
	
	/**
	 * Permet de se procurer le dossier des textes
	 * 
	 * @return le dossier des textes
	 */
	File getTextsFolder();
	
	/**
	 * Permet de se procurer le dossier d'analyse des textes
	 * 
	 * @return le dossier d'analyse des textes
	 */
	File getAnalyzeFolder();
	
	/**
	 * Permet de se procurer le dossier contenant les configuration
	 * 
	 * @return le dossier contenants les configurations
	 */
	File getConfigurationFolder();
	
	/**
	 * Permet de se procurer le nom de la configuration
	 * 
	 * @return le nom de la configuration
	 */
	String getConfigurationName();
	
	/**
	 * Permet de définir le dossier des textes
	 * 
	 * @param textsFolder dossier des textes
	 */
	void setTextsFolder(File textsFolder);
	
	/**
	 * Permet de définir le dossier d'analyse des textes
	 * 
	 * @param textsAnalyze dossier d'analyse des textes
	 */
	void setAnalyzeFolder(File analyzeFolder);
	
	/**
	 * Permet de fournir une liste des champs méta de la configuration courante
	 * 
	 * @return la liste des champs méta
	 */
	Map<String, String> getConfigurationFieldMetaFile();
	
	/**
	 * Permet de fournir une liste des champs communs de la configuration courante
	 * 
	 * @return la liste des champs communs
	 */
	Map<String, String> getConfigurationFieldCommonFile();
	
	/**
	 * Permet de se procurer le nom du corpus en cours d'édition
	 * 
	 * @return le nom du corpus
	 */
	String getEditingCorpusName();
	
	/**
	 * Permet de créer un nouveau corpus
	 * 
	 * @param nameFile         nom du fichier
	 * @param metaFileFieldMap map des champ metafile
	 */
	void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap);
	
	/**
	 * Permet de définir si un corpus est en cours d'édition
	 * 
	 * @return Vrai si un corpus est en cours d'édition
	 */
	Boolean haveEditingCorpus();
	
	/**
	 * Permet de supprimer le corpus en cours d'édition (pas le fichier physique, suppression logique)
	 */
	void clearEditingCorpus();
	
	/**
	 * Permet de se procurer les champs de la configuration spécifique désiré
	 * 
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	Map<String, String> getListFieldSpecific(Integer index);
	
	/**
	 * Permet de se procurer les champs en tête de la configuration spécifique
	 * désiré
	 * 
	 * @param index index de la configuration
	 * @return les champs spécifiques
	 */
	Map<String, String> getListFieldHeaderSpecific(Integer index);
	
	/**
	 * Permet de connaitre le nombre maximum de configuration spécifique
	 * 
	 * @return le nombre de configuration spécifique
	 */
	Integer getNbSpecificConfiguration();
	
	/**
	 * Permet de mettre à jour la liste des champs spécifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilisé
	 * @param specificFieldMap champ à mettre à jour
	 */
	void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap);
	
	/**
	 * Permet de se procurer la map des champs spécifique
	 * 
	 * @param index index de la configuration utilisé
	 * @return la map des champs spécifique
	 */
	Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index);
	
	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param key Clé du champ à récupérer
	 * @return la valeur
	 */
	String getFieldInEditingCorpus(String key);
	
	/**
	 * Permet de mettre à jour un champ de le corpus en cours d'édition
	 * 
	 * @param key   Clé (le champ)
	 * @param value Valeur (la valeur)
	 */
	void updateFieldInEditingCorpus(String key, String value);
	
	/**
	 * Permet d'écrire le corpus
	 * 
	 * @throws IOException Erreur d'entrée sortie
	 */
	void writeCorpus() throws IOException;
	
	/**
	 * Permet d'ajouter le texte en cours d'édition au corpus courant
	 */
	void addEditingTextToCurrentCorpus();
	
	/**
	 * Permet de se procurer le nb de ligne en erreur
	 * 
	 * @return le nb de ligne en erreur
	 */
	Integer getNbLinesError();
	
	/**
	 * Permet de se procurer une ligne d'erreur
	 * 
	 * @param index index de la ligne
	 * @return la ligne en erreur
	 */
	ErrorStructuredLine getErrorLine(Integer index);
	
	/**
	 * Permet de mettre à jour une ligne en erreur
	 * 
	 * @param index     index de la ligne
	 * @param lineError ligne erreur à mettre à jour
	 */
	void updateLineError(Integer index, String lineFixed);
	
	/**
	 * Permet de mettre à jour les fichiers avec les lignes en erreur
	 * 
	 * @throws IOException
	 */
	void saveFileAfteFixedErrorLine() throws IOException;
	
	/**
	 * Permet de se procurer le nombre de texte en erreur
	 * 
	 * @return le nombre de texte en erreur
	 */
	Integer getNbTextsError();
	
	/**
	 * Permet de charger le texte en erreur suivant (depuis la liste des textes structuré utilisateur)
	 */
	void loadNextErrorText();
	
	/**
	 * Permet de savoir si il y a des textes en erreurs restants
	 * 
	 * @return Vrai si il y a des textes en erreurs restants
	 */
	Boolean haveTextsInErrorRemaining();
	
	/**
	 * Permet d'appliquer la correction sur les textes structurés utilisateur à
	 * partir du texte en cours
	 */
	void applyFixedErrorText();
	
	/**
	 * Permet d'appliquer l'édition sur les textes structurés utilisateur à
	 * partir du texte en cours
	 */
	void applyEditText();
	
	/**
	 * Permet de sauvegarder temporairement l'état (permet la reprise en cas de fermeture de l'application)
	 */
	void saveCurrentStateOfFixedText();
	
	/**
	 * Permet d'écrire le corpus
	 * 
	 * @throws IOException Erreur d'entrée sortie
	 */
	void writeFixedText() throws IOException;
	
	/**
	 * Permet de connaitre l'existance d'un fichier d'état
	 * 
	 * @return Vrai si un fichier d'état existe
	 */
	Boolean haveCurrentStateFile();
	
	/**
	 * Permet de restaurer l'état courant
	 * 
	 * @throws IOException Erreur d'entrée sorties
	 * @throws JsonMappingException Erreur de mapping
	 * @throws JsonParseException Erreur de parsing
	 */
	void restoreCurrentState() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * Permet de se procurer le nombre de ligne vide en erreur
	 * 
	 * @return le nombre de ligne vide en erreur
	 */
	Integer getNbBlankLinesError();
	
	/**
	 * Permet de charger le texte en erreur suivant pour les lignes vides
	 */
	void loadNextErrorBlankLine();
	
	/**
	 * Permet de se procurer le nombre de textes chargés pour l'analyse
	 * @return le nombre de textes chargés pour l'analyse
	 */
	Integer getNbTextLoadedForAnalyze();
	
	/**
	 * Permet de se procurer le nombre de textes chargés depuis la bibliothéque
	 * @return le nombre de textes chargés depuis la bibliothéque
	 */
	Integer getNbTextLoadedForTexts();
	
	/**
	 * Permet de savoir si il y a des lignes vide restants
	 * 
	 * @return Vrai si il y a des lignes vide restants
	 */
	Boolean haveBlankLinesInErrorRemaining();
	
	/**
	 * Permet de se procurer la map des configuration specifique (label, suffix du
	 * fichier)
	 * 
	 * @return la map
	 */
	Map<String, String> getConfigurationSpecificLabelNameFileMap();
	
	/**
	 * Permet de générer le fichier excel à partir des fichiers de l'analyse
	 * 
	 * @param cmd    commande de génération
	 * @throws IOException Erreur d'entrée sortie
	 */
	void generateExcelFromAnalyze(ExcelGenerateConfigurationCmd cmd) throws IOException;
	
	/**
	 * Permet de générer le fichier excel à partir des fichiers de la biliothéque de textes
	 * 
	 * @param cmd    commande de génération
	 * @throws IOException Erreur d'entrée sortie
	 */
	void generateExcelFromTexts(ExcelGenerateConfigurationCmd cmd) throws IOException;
	
	/**
	 * Permet de se procurer la configuration courante avec le champ et le label
	 * associé
	 * 
	 * @return la map
	 */
	Map<String, String> getFieldConfigurationNameLabelMap();
	
	/**
	 * Permet de se procurer la liste des champs à process
	 * 
	 * @param labelSpecificConfiguration label du specific dont on souhaite les
	 *                                   champs
	 * @return la liste
	 */
	List<String> getFieldListToProcess(String labelSpecificConfiguration);
	
	/**
	 * Permet de se procurer la liste des champs que l'on ne peux pas afficher si on
	 * selectionne le specifique en parametre
	 * 
	 * @param labelSpecificConfiguration label du specific dont on souhaite les
	 *                                   champs
	 * @return la liste
	 */
	List<String> getFieldListForbiddenToDisplay(String labelSpecificConfiguration);
	
	/**
	 * Permet de charger le texte en erreur suivant pour les lignes meta vides
	 */
	void loadNextErrorMetaBlankLine();
	
	/**
	 * Permet de savoir s'il reste des meta vides dans les textes
	 * 
	 * @return Vrai si oui
	 */
	Boolean haveMetaBlankLineInErrorRemaining();
	
	/**
	 * Permet de savoir le nombre de corpus contenant des lignes vide meta à
	 * corriger.
	 * 
	 * @return Le nombre de corpus contenant des lignes vide meta à corriger.
	 */
	Integer getNbMetaBlankLineToFixed();
	
	/**
	 * Permet de connaitre le nombre total de textes filtrés
	 * @return Le nombre total de textes filtrés
	 */
	Integer getNbDisplayTextListFromFilteredText();
	
	/**
	 * Permet de fournir la liste des textes à afficher en fonction des clés filtrés
	 * 
	 * @param start Index de début (commence à 0)
	 * @param nbTextToLoad Nombre de texte à charger par page
	 * @return la liste des textes à afficher
	 */
	List<DisplayText> getDisplayTextListFromFilteredText(Integer start, Integer nbTextToLoad);
	
	/**
	 * Permet de charger les textes de la bibliothéque de textes
	 * @throws LoadTextException exception lors du chargement des textes
	 */
	void loadTexts() throws LoadTextException;
	
	/**
	 * Permet de charger un texte filtré pour consultation/edition
	 * @param key Clé du texte à charger
	 */
	void loadFilteredText(String key);
	
	/**
	 * Permet d'écrire le fichier qui vient d'être éditer
	 * @throws IOException
	 */
	void writeEditText() throws IOException;
	
	/**
	 * Permet de supprimer un texte d'un corpus depuis la bibliothéque de texte
	 * @param key Clé du texte à supprimer
	 * @throws IOException
	 */
	void deleteTextAndWriteCorpusFromFolderText(String key) throws IOException;
	
	/**
	 * Permet de se procurer la liste des corpus pour le filtrage des textes
	 * @return la liste des corpus
	 */
	List<String> getAllCorpusNameForFilteredText();
	
	/**
	 * Permet d'appliquer tous les filtres sur les corpus de la bibliothéque
	 * @param corpusName Nom du corpus sur lequel on souhaite filtrer, empty si on souhaite balayer tous les corpus
	 * @param filtersList Liste des filtres à appliquer
	 */
	void applyAllFiltersOnCorpusForFolderText(String corpusName, List<Filter> filtersList);
	
	/**
	 * Permet d'ajouter un texte au corpus en cours d'édition
	 */
	void addTextToCurrentCorpusFromFolderText();
	
	/**
	 * Permet de préparer pour l'ajout d'un texte
	 */
	void cleanCurrentEditingCorpusForAddText();
	
	/**
	 * Permet de se procurer la liste des configurations possibles
	 * @return la liste des configurations possibles
	 */
	List<String> getConfigurationNameList();
	
	/**
	 * Permet de définir la configuration par son nom
	 * @param name nom de la configuration
	 */
	void setCurrentConfiguration(String name);
	
	/**
	 * Permet de définir si il reste des erreurs dans les structures spécifique du corpus en cours d'édition
	 * @return retourne vrai si c'est le cas, faux sinon
	 */
	Boolean haveErrorInSpecificFieldInEditingCorpus();
	
	/**
	 * Méthode permettant de se procurer la liste des fichiers a traité et la possibilité de pouvoir les traiter
	 * 
	 * @param pathFolderToAnalyze Répertoire à analyser
	 * @param withSubFolder Permet de déterminer si on recherche dans les sous dossiers
	 * @return la liste des fichiers a traité et la possibilité de pouvoir les traiter
	 * @throws IOException
	 */
	FilesToAnalyzeInformation getNameFileToAnalyzeList(File pathFolderToAnalyze, Boolean withSubFolder) throws IOException;
	
	/**
	 * Permet de se procurer la totalité des champs de la configuration
	 * Celle ci sous forme de map (clé, libellé)
	 * @return la liste de tous les champs
	 */
	Map<String, String> getAllField();
	
	/**
	 * Permet de se procurer une ligne structuré avec les éléments en paramétre
	 * @param field Champ
	 * @param content Contenu du champ
	 * @return La ligne structuré
	 */
	String getStructuredLine(String field, String content);
	
	/**
	 * Permet de supprimer le fichier d'enregistrement temporaire
	 */
	void removeCurrentStateFile();
	
	/**
	 * Permet d'exporter des documents en fonction du type
	 * @param typeExport Type d'export
	 * @param directory dossier d'export
	 * @param file Nom du fichier
	 * @throws IOException Erreur d'entrée sortie
	 */
	void export(ExportTypeEnum typeExport, String directory, String nameFile) throws IOException;
	
	/**
	 * Permet de savoir si il y a des erreurs potentielles au niveau du changement de textes (incohérence et risque de décalage)
	 * @return Vrai si des erreurs existe
	 */
	Boolean haveInconsistencyError();
	
	/**
	 * Permet de se procurer les erreurs potentielles d'incohérence
	 * @return la liste des erreurs potentielles d'incohérence
	 */
	List<InconsistencyError> getInconsistencyChangeTextErrorList();
	
	
	/**
	 * Permet de savoir si il y a des erreurs potentielles de balise code
	 * @return Vrai si des erreurs existe
	 */
	Boolean haveMissingBaseCodeError();
	
	/**
	 * Permet de se procurer les erreurs potentielles de balise code
	 * @return la liste des erreurs potentielles de balise code
	 */
	List<BaseCodeError> getMissingBaseCodeErrorList();
}
