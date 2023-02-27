package view.interfaces;

import view.beans.FrequencyOrder;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface IFrequencyOrderTextModel {

    /**
     * Permet d'effectuer la modification à partir d'une cellule editable
     * @param rowIndex index de la ligne
     * @param columnIndex index de la colonne
     * @param newValue nouvelle valeur
     */
    void updateFromCell(Integer rowIndex, Integer columnIndex, Object newValue);

    List<FrequencyOrder> getFrequencyOrderList();

    void setClearSelectionConsumer(Consumer<?> consumer);

    /**
     * Permet de supprimer la liste des index sélectionné de la liste
     */
    void clearAllSelectedIndexList();

    /**
     * Permet d'ajouter un index à la liste des index sélectionné
     * @param index index à ajouter
     */
    void addIndexToSelectedIndexList(Integer index);

    /**
     * Permet de définir l'index sélectionné dans le model
     * @param index index
     */
    void setCurrentSelectedIndexInList(Integer index);

    /**
     * Permet de mettre à jour une valeur coté serveur
     * @param key Clé
     * @param value Valeur
     */
    void updateField(String key, String value);

    /**
     * Permet d'ajouter les informations de la liste
     *
     * @param mapKeyFieldTextField map des champs de l'interface
     */
    void addSpecificField(Map<String, JTextField> mapKeyFieldTextField);

    /**
     * Permet de mettre à jour les informations dans la liste
     *
     * @param mapKeyFieldTextField map des champs de l'interface
     */
    void updateSpecificField(Map<String, JTextField> mapKeyFieldTextField);

    /**
     * Permet de supprimer les champs sélectionnés
     */
    void removeSpecificField();

    /**
     * Permet de se procurer les valeurs sélectionné
     * @return la map sous forme de clé valeur
     */
    Map<String,String> getCurrentSelectedKeyValueMap();

    /**
     * Permet de se procurer la map des liste des champs spécifique (clé/label)
     * @return la map des liste des champs spécifique (clé/label)
     */
    Map<String, String> getMapTextLabelField();

    /**
     * Permet de créer le Jlabel
     * @param text texte du jlabel
     * @return
     */
    JLabel createJLabel(String text);

    /**
     * Permet de savoir si on a un index de slectionné
     * @return
     */
    Boolean haveCurrentSelectedIndexInList();

    /**
     * Permet d'ajouter un specific text refresh panel
     *
     * @param specificTextRefreshPanel specific text refresh panel
     */
    void addSpecificTextRefresh(ISpecificTextRefreshPanel specificTextRefreshPanel);

    /**
     * Permet de charger tous les champs spécifiques
     */
    void loadAllField();

}
