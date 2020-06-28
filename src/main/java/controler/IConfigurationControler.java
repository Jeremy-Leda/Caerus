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
 * Interface du controller pour la jonction au mod�le
 * 
 * @author jerem
 *
 */
public interface IConfigurationControler {

	/**
	 * Permet de lancer l'analyse des textes
	 * @param withSubFolder Permet de d�terminer si on recherche dans les sous dossiers
	 * @throws LoadTextException
	 */
	void launchAnalyze(Boolean withSubFolder) throws LoadTextException;
	
	/**
	 * Permet de d�finir la configuration courante
	 * 
	 * @param configuration configuration
	 */
	@Deprecated
	void setCurrentConfiguration(Configuration configuration);
	
	/**
	 * Permet de se procurer la liste des fichiers structur�s pour l'analyse
	 * @return la liste des fichiers structur�
	 */
	List<StructuredFile> getListOfStructuredFileForAnalyze();
	
	/**
	 * Permet de se procurer la liste des fichiers structur�s pour la biblioth�que
	 * @return la liste des fichiers structur�
	 */
	List<StructuredFile> getListOfStructuredFileForTexts();
	
	/**
	 * Permet de d�placer tous les fichiers de l'analyse vers la library
	 * @return le resultat du d�placement
	 * @throws IOException Erreur d'entr�e sorties
	 * @throws MoveFileException Erreur de d�placement des fichiers
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
	 * Permet de d�finir le dossier des textes
	 * 
	 * @param textsFolder dossier des textes
	 */
	void setTextsFolder(File textsFolder);
	
	/**
	 * Permet de d�finir le dossier d'analyse des textes
	 * 
	 * @param textsAnalyze dossier d'analyse des textes
	 */
	void setAnalyzeFolder(File analyzeFolder);
	
	/**
	 * Permet de fournir une liste des champs m�ta de la configuration courante
	 * 
	 * @return la liste des champs m�ta
	 */
	Map<String, String> getConfigurationFieldMetaFile();
	
	/**
	 * Permet de fournir une liste des champs communs de la configuration courante
	 * 
	 * @return la liste des champs communs
	 */
	Map<String, String> getConfigurationFieldCommonFile();
	
	/**
	 * Permet de se procurer le nom du corpus en cours d'�dition
	 * 
	 * @return le nom du corpus
	 */
	String getEditingCorpusName();
	
	/**
	 * Permet de cr�er un nouveau corpus
	 * 
	 * @param nameFile         nom du fichier
	 * @param metaFileFieldMap map des champ metafile
	 */
	void createNewCorpus(String nameFile, Map<String, String> metaFileFieldMap);
	
	/**
	 * Permet de d�finir si un corpus est en cours d'�dition
	 * 
	 * @return Vrai si un corpus est en cours d'�dition
	 */
	Boolean haveEditingCorpus();
	
	/**
	 * Permet de supprimer le corpus en cours d'�dition (pas le fichier physique, suppression logique)
	 */
	void clearEditingCorpus();
	
	/**
	 * Permet de se procurer les champs de la configuration sp�cifique d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	Map<String, String> getListFieldSpecific(Integer index);
	
	/**
	 * Permet de se procurer les champs en t�te de la configuration sp�cifique
	 * d�sir�
	 * 
	 * @param index index de la configuration
	 * @return les champs sp�cifiques
	 */
	Map<String, String> getListFieldHeaderSpecific(Integer index);
	
	/**
	 * Permet de connaitre le nombre maximum de configuration sp�cifique
	 * 
	 * @return le nombre de configuration sp�cifique
	 */
	Integer getNbSpecificConfiguration();
	
	/**
	 * Permet de mettre � jour la liste des champs sp�cifique pour la configuration
	 * en cours
	 * 
	 * @param index            index de la configuration utilis�
	 * @param specificFieldMap champ � mettre � jour
	 */
	void updateSpecificFieldInEditingCorpus(Integer index, Map<String, List<String>> specificFieldMap);
	
	/**
	 * Permet de se procurer la map des champs sp�cifique
	 * 
	 * @param index index de la configuration utilis�
	 * @return la map des champs sp�cifique
	 */
	Map<String, List<String>> getSpecificFieldInEditingCorpus(Integer index);
	
	/**
	 * Permet de se procurer la valeur d'un champ
	 * 
	 * @param key Cl� du champ � r�cup�rer
	 * @return la valeur
	 */
	String getFieldInEditingCorpus(String key);
	
	/**
	 * Permet de mettre � jour un champ de le corpus en cours d'�dition
	 * 
	 * @param key   Cl� (le champ)
	 * @param value Valeur (la valeur)
	 */
	void updateFieldInEditingCorpus(String key, String value);
	
	/**
	 * Permet d'�crire le corpus
	 * 
	 * @throws IOException Erreur d'entr�e sortie
	 */
	void writeCorpus() throws IOException;
	
	/**
	 * Permet d'ajouter le texte en cours d'�dition au corpus courant
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
	 * Permet de mettre � jour une ligne en erreur
	 * 
	 * @param index     index de la ligne
	 * @param lineError ligne erreur � mettre � jour
	 */
	void updateLineError(Integer index, String lineFixed);
	
	/**
	 * Permet de mettre � jour les fichiers avec les lignes en erreur
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
	 * Permet de charger le texte en erreur suivant (depuis la liste des textes structur� utilisateur)
	 */
	void loadNextErrorText();
	
	/**
	 * Permet de savoir si il y a des textes en erreurs restants
	 * 
	 * @return Vrai si il y a des textes en erreurs restants
	 */
	Boolean haveTextsInErrorRemaining();
	
	/**
	 * Permet d'appliquer la correction sur les textes structur�s utilisateur �
	 * partir du texte en cours
	 */
	void applyFixedErrorText();
	
	/**
	 * Permet d'appliquer l'�dition sur les textes structur�s utilisateur �
	 * partir du texte en cours
	 */
	void applyEditText();
	
	/**
	 * Permet de sauvegarder temporairement l'�tat (permet la reprise en cas de fermeture de l'application)
	 */
	void saveCurrentStateOfFixedText();
	
	/**
	 * Permet d'�crire le corpus
	 * 
	 * @throws IOException Erreur d'entr�e sortie
	 */
	void writeFixedText() throws IOException;
	
	/**
	 * Permet de connaitre l'existance d'un fichier d'�tat
	 * 
	 * @return Vrai si un fichier d'�tat existe
	 */
	Boolean haveCurrentStateFile();
	
	/**
	 * Permet de restaurer l'�tat courant
	 * 
	 * @throws IOException Erreur d'entr�e sorties
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
	 * Permet de se procurer le nombre de textes charg�s pour l'analyse
	 * @return le nombre de textes charg�s pour l'analyse
	 */
	Integer getNbTextLoadedForAnalyze();
	
	/**
	 * Permet de se procurer le nombre de textes charg�s depuis la biblioth�que
	 * @return le nombre de textes charg�s depuis la biblioth�que
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
	 * Permet de g�n�rer le fichier excel � partir des fichiers de l'analyse
	 * 
	 * @param cmd    commande de g�n�ration
	 * @throws IOException Erreur d'entr�e sortie
	 */
	void generateExcelFromAnalyze(ExcelGenerateConfigurationCmd cmd) throws IOException;
	
	/**
	 * Permet de g�n�rer le fichier excel � partir des fichiers de la bilioth�que de textes
	 * 
	 * @param cmd    commande de g�n�ration
	 * @throws IOException Erreur d'entr�e sortie
	 */
	void generateExcelFromTexts(ExcelGenerateConfigurationCmd cmd) throws IOException;
	
	/**
	 * Permet de se procurer la configuration courante avec le champ et le label
	 * associ�
	 * 
	 * @return la map
	 */
	Map<String, String> getFieldConfigurationNameLabelMap();
	
	/**
	 * Permet de se procurer la liste des champs � process
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
	 * Permet de savoir le nombre de corpus contenant des lignes vide meta �
	 * corriger.
	 * 
	 * @return Le nombre de corpus contenant des lignes vide meta � corriger.
	 */
	Integer getNbMetaBlankLineToFixed();
	
	/**
	 * Permet de connaitre le nombre total de textes filtr�s
	 * @return Le nombre total de textes filtr�s
	 */
	Integer getNbDisplayTextListFromFilteredText();
	
	/**
	 * Permet de fournir la liste des textes � afficher en fonction des cl�s filtr�s
	 * 
	 * @param start Index de d�but (commence � 0)
	 * @param nbTextToLoad Nombre de texte � charger par page
	 * @return la liste des textes � afficher
	 */
	List<DisplayText> getDisplayTextListFromFilteredText(Integer start, Integer nbTextToLoad);
	
	/**
	 * Permet de charger les textes de la biblioth�que de textes
	 * @throws LoadTextException exception lors du chargement des textes
	 */
	void loadTexts() throws LoadTextException;
	
	/**
	 * Permet de charger un texte filtr� pour consultation/edition
	 * @param key Cl� du texte � charger
	 */
	void loadFilteredText(String key);
	
	/**
	 * Permet d'�crire le fichier qui vient d'�tre �diter
	 * @throws IOException
	 */
	void writeEditText() throws IOException;
	
	/**
	 * Permet de supprimer un texte d'un corpus depuis la biblioth�que de texte
	 * @param key Cl� du texte � supprimer
	 * @throws IOException
	 */
	void deleteTextAndWriteCorpusFromFolderText(String key) throws IOException;
	
	/**
	 * Permet de se procurer la liste des corpus pour le filtrage des textes
	 * @return la liste des corpus
	 */
	List<String> getAllCorpusNameForFilteredText();
	
	/**
	 * Permet d'appliquer tous les filtres sur les corpus de la biblioth�que
	 * @param corpusName Nom du corpus sur lequel on souhaite filtrer, empty si on souhaite balayer tous les corpus
	 * @param filtersList Liste des filtres � appliquer
	 */
	void applyAllFiltersOnCorpusForFolderText(String corpusName, List<Filter> filtersList);
	
	/**
	 * Permet d'ajouter un texte au corpus en cours d'�dition
	 */
	void addTextToCurrentCorpusFromFolderText();
	
	/**
	 * Permet de pr�parer pour l'ajout d'un texte
	 */
	void cleanCurrentEditingCorpusForAddText();
	
	/**
	 * Permet de se procurer la liste des configurations possibles
	 * @return la liste des configurations possibles
	 */
	List<String> getConfigurationNameList();
	
	/**
	 * Permet de d�finir la configuration par son nom
	 * @param name nom de la configuration
	 */
	void setCurrentConfiguration(String name);
	
	/**
	 * Permet de d�finir si il reste des erreurs dans les structures sp�cifique du corpus en cours d'�dition
	 * @return retourne vrai si c'est le cas, faux sinon
	 */
	Boolean haveErrorInSpecificFieldInEditingCorpus();
	
	/**
	 * M�thode permettant de se procurer la liste des fichiers a trait� et la possibilit� de pouvoir les traiter
	 * 
	 * @param pathFolderToAnalyze R�pertoire � analyser
	 * @param withSubFolder Permet de d�terminer si on recherche dans les sous dossiers
	 * @return la liste des fichiers a trait� et la possibilit� de pouvoir les traiter
	 * @throws IOException
	 */
	FilesToAnalyzeInformation getNameFileToAnalyzeList(File pathFolderToAnalyze, Boolean withSubFolder) throws IOException;
	
	/**
	 * Permet de se procurer la totalit� des champs de la configuration
	 * Celle ci sous forme de map (cl�, libell�)
	 * @return la liste de tous les champs
	 */
	Map<String, String> getAllField();
	
	/**
	 * Permet de se procurer une ligne structur� avec les �l�ments en param�tre
	 * @param field Champ
	 * @param content Contenu du champ
	 * @return La ligne structur�
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
	 * @throws IOException Erreur d'entr�e sortie
	 */
	void export(ExportTypeEnum typeExport, String directory, String nameFile) throws IOException;
	
	/**
	 * Permet de savoir si il y a des erreurs potentielles au niveau du changement de textes (incoh�rence et risque de d�calage)
	 * @return Vrai si des erreurs existe
	 */
	Boolean haveInconsistencyError();
	
	/**
	 * Permet de se procurer les erreurs potentielles d'incoh�rence
	 * @return la liste des erreurs potentielles d'incoh�rence
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
