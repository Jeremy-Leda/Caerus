package model.excel.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotNull;

/**
 *
 * Bean contenant le contenu String d'une cellule à écrire dans un fichier Excel
 *
 */
@PojoBuilder
public class ExcelStringCell implements ExcelCell<String> {

    @NotNull(message = "Le contenu de la valeur ne peut pas être null")
    private String value;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
