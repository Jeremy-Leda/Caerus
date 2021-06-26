package view.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotEmpty;

/**
 *
 * Commande permettant d'aller sauvegarder les informations des textes aux serveurs
 *
 */
@PojoBuilder
public class StateCorpusSaveActionCmd {

    @NotEmpty
    private String keyField;

    private String value;

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
