package view.interfaces;

/**
 * 
 * Interface permettant de se procurer les m�thodes pour r�cup�rer les informations de l'analyse
 * 
 * @author jerem
 *
 */
public interface IAnalyzeConfiguration {

	/**
	 * Permet de savoir si l'analyse a �t� lanc� avec une recherche dans les sous dossiers
	 * @return Vrai si c'est le cas
	 */
	Boolean getWithSubFolderAnalyze();
	
}
