package view.interfaces;

import view.beans.EditTableElement;
import view.beans.SpecificRow;

import java.util.*;
import java.util.function.Consumer;

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
    LinkedList<SpecificRow> getRows();


    SpecificRow getSpecificRow(Integer index);

    /**
     * Permet de se procurer les valeurs du model
     * @return les valeurs
     */
    Set<String> getModelValues();

    /**
     * Permet de mettre à jour la valeur d'une ligne
     * @param id identifiant
     * @param newValue la nouvelle valeur
     */
    void update(Integer id, String newValue);

    /**
     * Permet de supprimer une valeur
     * @param id identifiant
     */
    void remove(Integer id);

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

}
