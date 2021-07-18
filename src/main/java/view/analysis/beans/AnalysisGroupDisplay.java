package view.analysis.beans;

import model.PojoBuilder;
import model.analyze.beans.CartesianGroup;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

/**
 *
 * Objet d'affichage pour les résultats de regroupements
 *
 */
@PojoBuilder
public class AnalysisGroupDisplay {

    @NotEmpty(message = "la liste des groupes cartésiens ne peut pas être vide")
    private Set<CartesianGroup> cartesianGroupSet;

    @NotNull(message = "Les résultats ne peuvent pas être null")
    private AnalysisResultDisplay analysisResultDisplay;

    @NotNull(message = "la liste des clés ne peut pas être null")
    private Set<String> keySet;

    public AnalysisResultDisplay getAnalysisResultDisplay() {
        return analysisResultDisplay;
    }

    public void setAnalysisResultDisplay(AnalysisResultDisplay analysisResultDisplay) {
        this.analysisResultDisplay = analysisResultDisplay;
    }

    public Set<String> getKeySet() {
        return keySet;
    }

    public void setKeySet(Set<String> keySet) {
        this.keySet = keySet;
    }

    public Set<CartesianGroup> getCartesianGroupSet() {
        return cartesianGroupSet;
    }

    public void setCartesianGroupSet(Set<CartesianGroup> cartesianGroupSet) {
        this.cartesianGroupSet = cartesianGroupSet;
    }

    public String getTitle() {
        String reduce = cartesianGroupSet.stream().map(CartesianGroup::getValue).reduce((a, b) -> a + " - " + b).get();
        if (reduce.length() > 25) {
            return reduce.substring(0, 22) + "...";
        }
        return reduce;
    }
}
