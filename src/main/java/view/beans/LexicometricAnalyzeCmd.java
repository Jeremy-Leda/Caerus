package view.beans;

import model.PojoBuilder;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Commande pour l'appel d'une analyse lexicométrique
 *
 */
@PojoBuilder
public class LexicometricAnalyzeCmd {

    @NotEmpty
    private List<String> keyTextFilteredList;

    @NotNull
    private Map<LexicometricEditEnum, String> preTreatmentListLexicometricMap = new HashMap<>();

    @NotEmpty
    private Set<String> fieldToAnalyzeSet;

    @NotNull
    private String label;

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
    public Map<LexicometricEditEnum, String> getPreTreatmentListLexicometricMap() {
        return preTreatmentListLexicometricMap;
    }

    /**
     * Permet de définir la map pour la liste des prétraitement pour les analyses lexicométrique
     * @return la map pour la liste des prétraitement pour les analyses lexicométrique
     */
    public void setPreTreatmentListLexicometricMap(Map<LexicometricEditEnum, String> preTreatmentListLexicometricMap) {
        this.preTreatmentListLexicometricMap = preTreatmentListLexicometricMap;
    }

    /**
     * Permet de se procurer la map destiné au serveur
     * @return la map destiné au serveur
     */
    public Map<LexicometricConfigurationEnum, String> toPreTreatmentServerMap() {
       return this.preTreatmentListLexicometricMap.entrySet().stream().collect(Collectors.toMap(e -> LexicometricConfigurationEnum.getLexicometricConfigurationEnumFromViewEnum(e.getKey()), Map.Entry::getValue));
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

    /**
     * Permet de se procurer le label de l'analyse
     * @return le label de l'analyse
     */
    public String getLabel() {
        return label;
    }

    /**
     * Permet de définir le label de l'analyse
     * @param label le label de l'analyse
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
