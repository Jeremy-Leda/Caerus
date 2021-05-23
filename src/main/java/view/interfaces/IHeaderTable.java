package view.interfaces;

/**
 * Interface pour les infos au sujet des headers des table
 */
public interface IHeaderTable {

    /**
     * Permet de se procurer le label de l'entête
     * @return le label de l'entête
     */
    String getHeaderLabel();

    /**
     * Permet de se procurer le label du filtre
     * @return le label du filtre
     */
    String getFilterLabel();

}
