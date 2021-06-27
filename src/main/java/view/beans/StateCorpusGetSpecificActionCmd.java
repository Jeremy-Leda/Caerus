package view.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 *
 * Commande permettant d'aller chercher les informations spécifiques des textes aux serveurs
 *
 */
@PojoBuilder
public class StateCorpusGetSpecificActionCmd {

    private String keyText;

    @NotNull
    private Integer index;

    public String getKeyText() {
        return keyText;
    }

    public void setKeyText(String keyText) {
        this.keyText = keyText;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
