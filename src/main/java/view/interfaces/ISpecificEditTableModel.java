package view.interfaces;

import view.beans.EditTableElement;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 *
 * Interface pour la gestion du modèle des données d'une table éditable
 *
 */
public interface ISpecificEditTableModel {

    /**
     * Permet de se procurer la liste des lignes à afficher
     * @return la liste des lignes
     */
    Collection<String> getRows();

    /**
     * Permet de se procurer la valeur de la ligne par son index
     * @param rowIndex index de la ligne
     * @return la valeur
     */
    String getRow(Integer rowIndex);

    /**
     * Permet de se procurer les valeurs du model
     * @return les valeurs
     */
    Set<String> getModelValues();

    /**
     * Permet de mettre à jour la valeur d'une ligne
     * @param oldValue l'ancienne valeur
     * @param newValue la nouvelle valeur
     */
    void update(String oldValue, String newValue);

    /**
     * Permet de supprimer une valeur
     * @param value la valeur à supprimer
     */
    void remove(String value);

    /**
     * Permet d'ajouter une valeur à la liste
     * @param value valeur à ajouter
     */
    void add(String value);

    /**
     * Permet de créer de manière automatique la liste des lignes à afficher dans la table
     * @param collection collection contenant la liste des valeur
     */
    void createSpecificRowList(Collection<String> collection);

    /**
     * Permet de filtrer la liste
     * @param value Le filtre à appliquer
     */
    void filter(String value);

    /**
     * Permet de se procurer le {@link EditTableElement} en cours
     * @return {@link EditTableElement} en cours
     */
    Optional<EditTableElement> getEditTableElement();

    /**
     * Permet de définir le {@link EditTableElement} en cours
     * @param editTableElement {@link EditTableElement} en cours
     */
    void setEditTableElement(Optional<EditTableElement> editTableElement);

    /**
     * Permet de définir le consumer pour mettre à jour la table après une insertion
     * @param consumer consumer pour mettre à jour la table après une insertion
     */
    void setConsumerForFireTableInserted(BiConsumer<Integer, Integer> consumer);

    /**
     * Permet de définir le consumer pour mettre à jour la table après une mise à jour
     * @param consumer consumer pour mettre à jour la table après une mise à jour
     */
    void setConsumerForFireTableUpdated(BiConsumer<Integer, Integer> consumer);

    /**
     * Permet de définir le consumer pour mettre à jour la table après une suppression
     * @param consumer consumer pour mettre à jour la table après une suppression
     */
    void setConsumerForFireTableDeleted(BiConsumer<Integer, Integer> consumer);

}
