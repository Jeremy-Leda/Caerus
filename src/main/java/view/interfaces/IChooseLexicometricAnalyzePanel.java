package view.interfaces;

import view.beans.LexicometricAnalyzeTypeViewEnum;

import java.util.Optional;

/**
 * Interface pour la sélection de l'analyse lexicométrique
 */
public interface IChooseLexicometricAnalyzePanel extends IAccessPanel{

    /**
     * Permet de se procurer les options si elles sont demandés
     * @return
     */
    Optional<ILexicometricListApplyChoosePanel> getOptionalILexicometricListApplyChoosePanel();


    /**
     * Permet de se procurer le type d'analyse à lancer
     * @return le type d'analyse à lancer
     */
    LexicometricAnalyzeTypeViewEnum getAnalyzeToLaunch();

}
