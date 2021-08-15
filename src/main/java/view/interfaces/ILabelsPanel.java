package view.interfaces;

/**
 *
 * Interface pour l'affichage d'un bloc de label
 *
 */
public interface ILabelsPanel extends IAccessPanel {

    /**
     * Permet de définir un label
     * @param id identifiant du label
     * @param header en tête (label du champ)
     * @param value valeur (valeur du champ)
     */
    void setLabel(Integer id, String header, String value);


    /**
     * Permet de définir un label
     * @param id identifiant du label
     * @param header en tête (label du champ)
     * @param boldHeader Vrai si bold
     * @param value valeur (valeur du champ)
     * @param boldValue Vrai si bold
     */
    void setLabel(Integer id, String header, Boolean boldHeader, String value, Boolean boldValue);

    /**
     * Permet d'ajouter un label non prévu à l'origine (incrémente le nombre)
     * @param header en tête (label du champ)
     * @param boldHeader Vrai si bold
     * @param value valeur (valeur du champ)
     * @param boldValue Vrai si bold
     */
    void addLabel(String header, Boolean boldHeader, String value, Boolean boldValue);

}
