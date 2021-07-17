package view.interfaces;

import view.beans.LexicometricEditEnum;
import view.panel.LexicometricListApplyChoosePanel;

/**
 *
 * Interface pour le choix des lemmes
 *
 */
public interface ILexicometricConfigurationChoosePanel extends IAccessPanel{

    /**
     * Permet de se procurer le type de configuration lexicométrique
     * @return le type de configuration lexicométrique
     */
    LexicometricEditEnum getLexicometricEditEnum();

    /**
     * Permet de se procurer le profil à utiliser
     * @return le profil à utiliser
     */
    String getProfile();

    /**
     * Permet de savoir si le profil a bien été renseigné
     * @return Vrai si le profil à bien été renseigné
     */
    Boolean isValid();

    /**
     * Permet de se procurer le type de configuration lexicométrique général
     * @return le type de configuration lexicométrique général
     */
    LexicometricEditEnum getGeneralLexicometricEditEnum();

}
