package view.beans;

import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Permet de se procurer liste des lemmes possibles sous forme d'énumération
 */
public enum LemmeTypeViewEnum {
    RADICAL(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_TABLE_HEADER_LABEL), LexicometricConfigurationEnum.LEMMATIZATION.getAllProfils().apply(null)),
    RADICAL_BY_CLASS(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_TABLE_HEADER_LABEL), LexicometricConfigurationEnum.LEMMATIZATION_BY_GRAMMATICAL_CATEGORY.getAllProfils().apply(null));

    private final String label;
    private final Set<String> profileSet;

    LemmeTypeViewEnum(String label, Set<String> profileSet) {
        this.label = label;
        this.profileSet = profileSet;
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
     * Permet de se procurer la valeur de l'énum à partir de son label
     * @param label label à rechercher
     * @return Optional valeur de l'énum retrouvé
     */
    public static Optional<LemmeTypeViewEnum> fromLabel(String label) {
        return Arrays.stream(values()).filter(e -> e.getLabel().equals(label)).findFirst();
    }
}
