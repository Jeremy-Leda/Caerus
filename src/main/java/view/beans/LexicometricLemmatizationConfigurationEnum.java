package view.beans;

import view.interfaces.ILexicometricConfigurationEnum;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Permet de se procurer liste des lemmes possibles sous forme d'énumération
 */
public enum LexicometricLemmatizationConfigurationEnum implements ILexicometricConfigurationEnum {
    RADICAL(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_TABLE_HEADER_LABEL), model.analyze.lexicometric.beans.LexicometricConfigurationEnum.LEMMATIZATION.getAllProfils().apply(null), LexicometricEditEnum.LEMMATIZATION),
    RADICAL_BY_CLASS(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_TABLE_HEADER_LABEL), model.analyze.lexicometric.beans.LexicometricConfigurationEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY.getAllProfils().apply(null), LexicometricEditEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY);

    private final String label;
    private final String chooseListLabel;
    private final Set<String> profileSet;
    private final LexicometricEditEnum lexicometricEditEnum;

    LexicometricLemmatizationConfigurationEnum(String label, Set<String> profileSet, LexicometricEditEnum lexicometricEditEnum) {
        this.label = label;
        this.profileSet = profileSet;
        this.lexicometricEditEnum = lexicometricEditEnum;
        this.chooseListLabel = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_TYPE_LEMMATIZATION_TREATMENT_OPTIONAL_LIST_LABEL);
    }

    /**
     * Permet de se procurer le libellé
     * @return le libellé
     */
    public String getLabel() {
        return label;
    }

    /**
     * Permet de se procurer la liste des profiles
     * @return la liste des profiles
     */
    public Set<String> getProfileSet() {
        return profileSet;
    }

    /**
     * Permet de se procurer le type de lexicométrique edit
     * @return le type de lexicométrique edit
     */
    public LexicometricEditEnum getLexicometricEditEnum() {
        return lexicometricEditEnum;
    }

    /**
     * Permet de se procurer le libellé pour le choix de la liste
     * @return le libellé pour le choix de la liste
     */
    public String getChooseListLabel() {
        return chooseListLabel;
    }
}
