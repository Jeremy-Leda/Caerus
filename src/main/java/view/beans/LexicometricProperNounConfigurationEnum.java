package view.beans;

import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import view.interfaces.ILexicometricConfigurationEnum;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Set;

public enum LexicometricProperNounConfigurationEnum implements ILexicometricConfigurationEnum {
    RADICAL(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_PROPER_NOUN_TABLE_HEADER_LABEL), LexicometricConfigurationEnum.PROPER_NOUN.getAllProfils().apply(null), LexicometricEditEnum.PROPER_NOUN);


    private final String label;
    private final String chooseListLabel;
    private final Set<String> profileSet;
    private final LexicometricEditEnum lexicometricEditEnum;

    LexicometricProperNounConfigurationEnum(String label, Set<String> profileSet, LexicometricEditEnum lexicometricEditEnum) {
        this.label = label;
        this.profileSet = profileSet;
        this.lexicometricEditEnum = lexicometricEditEnum;
        this.chooseListLabel = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_START_ANALYSIS_CHOOSE_TYPE_PROPER_NOUN_TREATMENT_OPTIONAL_LIST_LABEL);
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
