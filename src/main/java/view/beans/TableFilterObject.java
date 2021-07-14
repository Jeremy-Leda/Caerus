package view.beans;

import model.PojoBuilder;
import view.interfaces.ITableFilterObject;

/**
 *
 * Classe utilisé pour filtrer les tableaux
 *
 */
@PojoBuilder
public class TableFilterObject<T> implements ITableFilterObject<T> {

    private String stringValue;

    @Override
    public String getStringValue() {
        return this.stringValue;
    }

    @Override
    public T getValue() {
        return (T) stringValue;
    }

    /**
     * Permet de définir le filtre en chaine de caractère
     * @param stringValue valeur de la chaine
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
