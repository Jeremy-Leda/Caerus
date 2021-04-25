package view.interfaces;

/**
 * Interface pour savoir si la table est root
 */
public interface IRootTable extends IHeaderTable {

    /**
     * Permet de savoir si la table est root
     * @return
     */
    Boolean isRoot();

    /**
     * Ordre hiérarchique de mise à jour
     * @return l'ordre hierarchique de mise à jour
     */
    Integer hierarchicalOrder();

    /**
     * Ordre d'affichage
     * @return l'ordre d'affichage
     */
    Integer displayOrder();
}
