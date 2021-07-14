package view.interfaces;

import view.beans.SelectedObjectTable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 *
 * Model pour les tables à sélection
 *
 */
public interface ISelectedEditTableModel<T> {

    /**
     * Permet de se procurer la liste des lignes à afficher
     * @return la liste des lignes
     */
    Collection<SelectedObjectTable<T>> getRows();

    /**
     * Permet de se procurer la valeur de la ligne par son index
     * @param rowIndex index de la ligne
     * @return la valeur
     */
    SelectedObjectTable getRow(Integer rowIndex);

    /**
     * Permet de remplacer les valeurs à afficher
     * @param collection collection contenant la liste des valeurs
     */
    void cleanAndFill(Collection<SelectedObjectTable<T>> collection);


    /**
     * Permet de se procurer les lignes sélectionné ou non
     * @param isSelected vrai si on souhaite retourné que les lignes sélectionné
     * @return  la liste des lignes en fonction de leur état
     */
    Collection<SelectedObjectTable<T>> getRowsByState(boolean isSelected);

    /**
     * Permet de filter les données
     * @param filterFunction function pour le filtre
     * @param filter le filtre
     */
    void filterData(BiFunction<Collection<SelectedObjectTable<T>>, T, Collection<SelectedObjectTable<T>>> filterFunction, Optional<T> filter);
}
