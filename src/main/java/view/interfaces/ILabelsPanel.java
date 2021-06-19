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

}
