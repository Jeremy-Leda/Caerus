package model.excel.beans;

import model.analyze.beans.StructuredText;

/**
 *
 * Texte structuré Excel pour conserver l'ordre des imports
 *
 */
public class StructuredTextExcel extends StructuredText {

    private final int number;

    /**
     *
     * Création d'un texte structuré
     *
     * @param number numéro du texte
     */
    public StructuredTextExcel(int number) {
        super(number);
        this.number = number;
    }

    /**
     * Permet de se procurer le numéro du texte
     * @return Numéro du texte
     */
    public int getNumber() {
        return number;
    }
}
