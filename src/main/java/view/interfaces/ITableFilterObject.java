package view.interfaces;

/**
 * Interface pour la gestion des filtres sur les tableaux génériques
 */
public interface ITableFilterObject<T> {

    /**
     * Permet de se procurer la chaine de caractère sur laquelle on souhaite filtrer
     * @return
     */
    String getStringValue();

    /**
     * Permet de se procurer la valeur
     * @return la valeur
     */
    T getValue();

}
