package model.analyze.lexicometric.interfaces;

import java.util.Map;

/**
 * Interface pour la gestion hiérarchique des datas
 * @param <T> Type de données
 */
public interface ILexicometricHierarchical<T extends Object> {

    /**
     * Permet de se procurer la map hiérarchique pour la récupération des données
     * @return la map hiérarchique pour la récupération des données
     */
    Map<T, Integer> getHierarchicalIntegerMap();


}
