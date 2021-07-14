package view.interfaces;

import view.beans.SelectedObjectTable;

import java.util.Collection;
import java.util.Optional;

/**
 *
 * Interface pour le panel de la table de sélection
 *
 */
public interface ISelectedTablePanel<T> extends IAccessPanel {

    /**
     *
     * Permet de mettre à jour la liste des données
     *
     * @param data données à mettre à jour
     */
    void refreshData(Collection<SelectedObjectTable<T>> data);

    /**
     * Permet de se procurer les lignes sélectionné ou non
     * @param isSelected vrai si on souhaite retourné que les lignes sélectionné
     * @return  la liste des lignes en fonction de leur état
     */
    Collection<SelectedObjectTable<T>> getRowsByState(boolean isSelected);

    /**
     * Permet de filter
     * @param filterData le filtre
     */
    void filter(Optional<T> filterData);

}
