package view.interfaces;

/**
 * 
 * Interface permettant de se procurer les méthodes pour récupérer les informations de l'analyse
 * 
 * @author jerem
 *
 */
public interface IAnalyzeConfiguration {

	/**
	 * Permet de savoir si l'analyse a été lancé avec une recherche dans les sous dossiers
	 * @return Vrai si c'est le cas
	 */
	Boolean getWithSubFolderAnalyze();
	
}
