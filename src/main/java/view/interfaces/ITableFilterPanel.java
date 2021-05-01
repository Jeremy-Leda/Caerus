package view.interfaces;

import java.util.function.Consumer;

/**
 *
 * Interface pour filtrer dans les tableaux
 *
 */
public interface ITableFilterPanel extends IAccessPanel{

    /**
     * Permet de se procurer le filtre
     * @return le filtre
     */
    ITableFilterObject getFilter();

    /**
     * Permet d'ajouter un consumer sur le changement
     * @param consumer consumer pour gérer le changement
     */
    void addConsumerOnChange(Consumer<?> consumer);


}
