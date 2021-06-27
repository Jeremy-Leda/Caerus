package view.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 *
 * Commande permettant d'aller sauvegarder les informations spécifiques des textes aux serveurs
 *
 */
@PojoBuilder
public class StateCorpusSaveSpecificActionCmd {

    @NotNull
    private Map<String, List<String>> valueMap;

    @NotNull
    private Integer index;

    public Map<String, List<String>> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, List<String>> valueMap) {
        this.valueMap = valueMap;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
