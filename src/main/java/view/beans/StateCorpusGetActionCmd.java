package view.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotEmpty;

/**
 *
 * Commande permettant d'aller chercher les informations des textes aux serveurs
 *
 */
@PojoBuilder
public class StateCorpusGetActionCmd {

    private String keyText;

    @NotEmpty
    private String keyField;

    public String getKeyText() {
        return keyText;
    }

    public void setKeyText(String keyText) {
        this.keyText = keyText;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }
}
