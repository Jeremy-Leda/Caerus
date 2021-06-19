package view.analysis.beans;

import model.PojoBuilder;
import view.panel.analysis.model.AnalysisRow;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bean pour l'affichage des résultats
 */
@PojoBuilder
public class AnalysisResultDisplay {

    private Set<AnalysisTokenDisplay> analysisTokenDisplaySet;
    @NotNull
    private String key;
    private Integer NbToken;
    private Long NbOccurrency;

    /**
     * Permet de se procurer la liste des tokens à afficher
     * @return la liste des tokens à afficher
     */
    public Set<AnalysisTokenDisplay> getAnalysisTokenDisplaySet() {
        return analysisTokenDisplaySet;
    }

    /**
     * Permet de définir la liste des tokens à afficher
     * @param analysisTokenDisplaySet la liste des tokens à afficher
     */
    public void setAnalysisTokenDisplaySet(Set<AnalysisTokenDisplay> analysisTokenDisplaySet) {
        this.analysisTokenDisplaySet = analysisTokenDisplaySet;
    }

    /**
     * Permet de se procurer la clé
     * @return la clé
     */
    public String getKey() {
        return key;
    }

    /**
     * Permet de définir la clé
     * @param key la clé à définir
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Permet de se procurer le nombre de token
     * @return le nombre de token
     */
    public Integer getNbToken() {
        return NbToken;
    }

    /**
     * Permet de définir le nombre de token
     * @param nbToken le nombre de token
     */
    public void setNbToken(Integer nbToken) {
        NbToken = nbToken;
    }

    /**
     * Permet de se procurer le nombre d'occurence
     * @return le nombre d'occurence
     */
    public Long getNbOccurrency() {
        return NbOccurrency;
    }

    /**
     * Permet de définir le nombre d'occurence
     * @param nbOccurrency le nombre d'occurence
     */
    public void setNbOccurrency(Long nbOccurrency) {
        NbOccurrency = nbOccurrency;
    }

    /**
     * Permet de convertir le résultat en liste de lignes pour token
     * @return liste de lignes pour token
     */
    public List<AnalysisRow> toAnalysisTokenRowList() {
        return this.getAnalysisTokenDisplaySet().stream().map(AnalysisTokenDisplay::toTokenRow).collect(Collectors.toCollection(LinkedList::new));
    }
}
