package model.analyze.lexicometric.interfaces;

import model.analyze.lexicometric.beans.FillTableConfiguration;
import view.beans.EditTableElement;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiConsumer;

/**
 * Interface pour la configuration des éléments lexicometric
 * @param <T> Type d'objet à retourner
 */
public interface ILexicometricConfiguration<T extends Object> {

    /**
     * Permet de se procurer la liste des profiles existants
     * @return la liste des profiles existants
     */
    Set<String> getProfilesSet();

    /**
     * Permet de se procurer la liste des objets permettant la stratégie de remplissage de la liste IHM
     * @return la liste des objets permettant la stratégie de remplissage de la liste IHM
     */
    List<FillTableConfiguration<T>> getFillTableConfigurationList();

    /**
     * Permet de se procurer le consumer d'édition permettant de mettre à jour les listes coté serveur
     * @return le consumer d'édition permettant de mettre à jour les listes coté serveur
     */
    BiConsumer<String, EditTableElement> getEditConsumer();

//    /**
//     * Permet de se procurer l'ordre hiérarchique de sauvegarde des données sur le serveur
//     * @return l'ordre hiérarchique de sauvegarde des données sur le serveur
//     */
//    SortedSet<Integer> getSaveHierarchicalOrder();

}
