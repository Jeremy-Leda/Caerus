package model.interfaces;

/**
 *
 * Interface pour la gestion de la progression
 *
 */
public interface IProgressBean {

    /**
     * Permet de définir l'itération en cours (la premiére itération est à 1)
     *
     * @param currentIterate numéro de l'itération
     */
    void setCurrentIterate(Integer currentIterate);

    /**
     * Permet de définir le nombre maximum d'éléments dans l'itération en cours
     *
     * @param nbMaxElementForCurrentIterate le nombre maximum d'éléments dans
     *                                      l'itération en cours
     */
    void setNbMaxElementForCurrentIterate(Integer nbMaxElementForCurrentIterate);

    /**
     * Permet de définir le numéro de l'élément courant pour l'itération en cours
     *
     * @param currentElementForCurrentIterate le numéro de l'élément courant pour
     *                                        l'itération en cours
     */
    void setCurrentElementForCurrentIterate(Integer currentElementForCurrentIterate);

}
