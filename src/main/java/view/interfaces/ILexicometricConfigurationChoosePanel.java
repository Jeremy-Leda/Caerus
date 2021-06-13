package view.interfaces;

import view.beans.LexicometricEditEnum;

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

}
