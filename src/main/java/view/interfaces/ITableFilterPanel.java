package view.interfaces;

import java.util.function.Consumer;

/**
 *
 * Interface pour filtrer dans les tableaux
 *
 */
public interface ITableFilterPanel<T> extends IAccessPanel{

    /**
     * Permet de se procurer le filtre
     * @return le filtre
     */
    ITableFilterObject<T> getFilter();

    /**
     * Permet d'ajouter un consumer sur le changement
     * @param consumer consumer pour gérer le changement
     */
    void addConsumerOnChange(Consumer<T> consumer);


}
