package model.excel.beans.abstracts;

import model.excel.beans.ExcelCell;

import javax.validation.constraints.NotNull;

/**
 *
 * Classe abstraite pour les cellules Excel
 *
 * @param <T> Type de cellule
 */
public abstract class ExcelCellAbstract<T> implements ExcelCell<T> {

    @NotNull(message = "Le contenu de la valeur ne peut pas être null")
    private T value;

    private boolean isHeader = false;

    @Override
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

}
