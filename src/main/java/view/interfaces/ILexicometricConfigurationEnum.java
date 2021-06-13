package view.interfaces;

import view.beans.LexicometricEditEnum;
import view.beans.LexicometricTokenizationConfigurationEnum;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public interface ILexicometricConfigurationEnum {

    /**
     * Permet de se procurer le libellé
     * @return le libellé
     */
    String getLabel();

    /**
     * Permet de se procurer la liste des profiles
     * @return la liste des profiles
     */
    Set<String> getProfileSet();

    /**
     * Permet de se procurer le type de lexicométrique edit
     * @return le type de lexicométrique edit
     */
    LexicometricEditEnum getLexicometricEditEnum();

    /**
     * Permet de se procurer le libellé pour le choix de la liste
     * @return le libellé pour le choix de la liste
     */
    String getChooseListLabel();
}
