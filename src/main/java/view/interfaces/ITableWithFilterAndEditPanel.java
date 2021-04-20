package view.interfaces;

import view.beans.EditTableElement;

import javax.swing.event.ListSelectionListener;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * Interface pour l'utilisation des tables avec filtre et édition
 *
 */
public interface ITableWithFilterAndEditPanel extends IAccessPanel, IRefreshPanel {

    /**
     * Permet de remplir la table
     * @param collection collection pour la table
     */
    void fillTable(Collection<String> collection);

    /**
     * Permet de configurer le consumer pour le bouton ajouter
     * @param informationMessage Information pour l'utilisateur
     * @param label Label devant la zone de saisie
     */
    void setInterfaceForAddButton(String informationMessage, String label);

    /**
     * Permet de se procurer les valeurs de la table
     * @return les valeurs de la table
     */
    Set<String> getValues();

    /**
     * Permet de définir un consumer à appliquer lors du changement de la ligne
     * @param consumer consumer à appliquer lors du changement de la ligne
     */
    void setConsumerForRowChanged(Consumer<String> consumer);

    /**
     * Permet de se procurer le {@link EditTableElement} en cours
     * @return {@link EditTableElement} en cours
     */
    Optional<EditTableElement> getEditTableElement();

}