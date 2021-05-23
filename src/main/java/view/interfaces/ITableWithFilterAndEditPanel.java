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
 * @param <T> Type d'objet à alimenter dans le tableau
 *
 */
public interface ITableWithFilterAndEditPanel<T extends Object> extends IAccessPanel, IRefreshPanel {

    /**
     * Permet de remplir la table
     * @param collection collection pour la table
     */
    void fillTable(Collection<T> collection);

    /**
     * Permet de configurer le consumer pour le bouton ajouter
     * @param informationMessage Information pour l'utilisateur
     * @param label Label devant la zone de saisie
     */
    void setInterfaceForAddButton(String informationMessage, String label);

    /**
     * Permet de modifier le label pour le bouton ajouter et supprimer
     * @param addLabel Label du bouton ajouter
     * @param removeLabel Label du bouton supprimer
     */
    void setLabelForAddAndRemoveButton(String addLabel, String removeLabel);

    /**
     * Permet de se procurer les valeurs de la table
     * @return les valeurs de la table
     */
    Set<T> getValues();

    /**
     * Permet de définir un consumer à appliquer lors du changement de la ligne
     * @param consumer consumer à appliquer lors du changement de la ligne
     */
    void setConsumerForRowChanged(Consumer<T> consumer);

    /**
     * Permet de définir un consumer qui va rafraichir l'état des boutons ajouter de tous les tableaux
     * @param consumer consumer
     */
    void setConsumerForRefreshStateOfAllAddButton(Consumer<Void> consumer);

    /**
     * Permet de se procurer le {@link EditTableElement} en cours
     * @return {@link EditTableElement} en cours
     */
    Optional<EditTableElement> getEditTableElement();

    /**
     * Permet de se procurer la valeur sélectionné
     * @return la valeur selectionné
     */
    T getSelectedValue();

    /**
     * Permet de définir l'état du bouton ajouter
     * @param enabledAddButton état du bouton ajouter
     */
    void setEnabledAddButton(Boolean enabledAddButton);

    /**
     * Permet de savoir si une valeur a été sélectionné
     * @return Vrai si une valeur a été sélectionné
     */
    Boolean haveSelectedValue();

}
