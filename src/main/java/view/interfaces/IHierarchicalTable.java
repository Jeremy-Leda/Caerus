package view.interfaces;

import java.util.Set;

/**
 *
 * Interface permettant de présenter les méthodes pour la gestion des tables hiérarchique
 *
 */
public interface IHierarchicalTable {

    /**
     * Permet de se procurer les tables hiérarchique
     * @return le set des tables hiérarchique
     */
    Set<IRootTable> getHierarchicalTableSet();
}
