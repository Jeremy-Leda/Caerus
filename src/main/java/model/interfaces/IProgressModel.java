package model.interfaces;

/**
 *
 * Interface pour la gestion de la progression
 *
 */
public interface IProgressModel {

    /**
     * Permet de se procurer la progression
     * @return la progression
     */
    Integer getProgress();

    /**
     * Permet d'annuler le traitement
     */
    void cancel();

    /**
     * Permet de savoir si le traitement a été annulé
     * @return Vrai si le traitement a été annulé
     */
    boolean treatmentIsCancelled();

    /**
     * Permet de savoir si le traitement est en cours ou non
     * @return Vrai si le traitement est en cours
     */
    boolean isRunning();

    /**
     * Permet de remettre à zéro la progression
     */
    void resetProgress();

}
