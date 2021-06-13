package model.analyze.lexicometric.beans;

import model.PojoBuilder;
import view.beans.LexicometricAnalyzeTypeViewEnum;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PojoBuilder
public class LexicometricAnalyzeServerCmd {

    @NotEmpty
    private List<String> keyTextFilteredList;

    @NotNull
    private Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap = new HashMap<>();

    @NotNull
    private LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum;

    @NotEmpty
    private Set<String> fieldToAnalyzeSet;

    /**
     * Permet de se procurer la liste des clés filtrés
     * @return la liste des clés filtrés
     */
    public List<String> getKeyTextFilteredList() {
        return keyTextFilteredList;
    }

    /**
     * Permet de définir la liste des clés filtrés
     * @param keyTextFilteredList la liste des clés filtrés
     */
    public void setKeyTextFilteredList(List<String> keyTextFilteredList) {
        this.keyTextFilteredList = keyTextFilteredList;
    }

    /**
     * Permet de se procurer la map pour la liste des prétraitement pour les analyses lexicométrique
     * @return la map pour la liste des prétraitement pour les analyses lexicométrique
     */
    public Map<LexicometricConfigurationEnum, String> getPreTreatmentListLexicometricMap() {
        return preTreatmentListLexicometricMap;
    }

    /**
     * Permet de définir la map pour la liste des prétraitement pour les analyses lexicométrique
     * @param preTreatmentListLexicometricMap la map pour la liste des prétraitement pour les analyses lexicométrique
     */
    public void setPreTreatmentListLexicometricMap(Map<LexicometricConfigurationEnum, String> preTreatmentListLexicometricMap) {
        this.preTreatmentListLexicometricMap = preTreatmentListLexicometricMap;
    }

    /**
     * Permet de se procurer le type d'analyse lexicométrique
     * @return le type d'analyse lexicométrique
     */
    public LexicometricAnalyzeTypeEnum getLexicometricAnalyzeTypeEnum() {
        return lexicometricAnalyzeTypeEnum;
    }

    /**
     * Permet de définir le type d'analyse lexicométrique
     * @param lexicometricAnalyzeTypeEnum le type d'analyse lexicométrique
     */
    public void setLexicometricAnalyzeTypeEnum(LexicometricAnalyzeTypeEnum lexicometricAnalyzeTypeEnum) {
        this.lexicometricAnalyzeTypeEnum = lexicometricAnalyzeTypeEnum;
    }

    /**
     * Permet de se procurer la liste des champs à analyser
     * @return la liste des champs à analyser
     */
    public Set<String> getFieldToAnalyzeSet() {
        return fieldToAnalyzeSet;
    }

    /**
     * Permet de définir la liste des champs à analyser
     * @param fieldToAnalyzeSet la liste des champs à analyser
     */
    public void setFieldToAnalyzeSet(Set<String> fieldToAnalyzeSet) {
        this.fieldToAnalyzeSet = fieldToAnalyzeSet;
    }

    /// CONTROLE METIER

    /**
     * Permet de vérifier que le profile de lemmatisation est renseigné pour les lemmes type
     * @return Vrai si valide
     */
    @AssertTrue
    private boolean isLemmeTypeValid() {
        return !LexicometricAnalyzeTypeEnum.LEMME_TYPE.equals(getLexicometricAnalyzeTypeEnum())
                || this.getPreTreatmentListLexicometricMap().keySet().stream().filter(s -> s.isLemmatization()).findFirst().isPresent();
    }

    /**
     * Permet de vérifier que le profile de lemmatisation est renseigné pour les token ratio
     * @return Vrai si valide
     */
    @AssertTrue
    private boolean isTokenRatioIsValid() {
        return !LexicometricAnalyzeTypeEnum.TOKEN_RATIO.equals(getLexicometricAnalyzeTypeEnum())
                || this.getPreTreatmentListLexicometricMap().keySet().stream().filter(s -> s.isLemmatization()).findFirst().isPresent();
    }

    ///

    @Override
    public String toString() {
        return "LexicometricAnalyzeServerCmd{" +
                "keyTextFilteredList=" + keyTextFilteredList +
                ", preTreatmentListLexicometricMap=" + preTreatmentListLexicometricMap +
                ", lexicometricAnalyzeTypeEnum=" + lexicometricAnalyzeTypeEnum +
                ", fieldToAnalyzeSet=" + fieldToAnalyzeSet +
                '}';
    }
}
