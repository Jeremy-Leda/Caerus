package view.interfaces;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import view.beans.SpecificRow;

/**
 * 
 * Interfaces pour fournir les méthodes nécessaires aux IHM des textes spécifiques
 * 
 * @author jerem
 *
 */
public interface ISpecificTextModel {

	/**
	 * Permet de se procurer la map des headers (clé/valeur)
	 * @return la map des headers (clé/valeur)
	 */
	Map<String, String> getMapHeaderKeyFieldText();
	
	/**
	 * Permet de se procurer la map des headers (clé/label)
	 * @return la map des headers (clé/label)
	 */
	Map<String, String> getMapHeaderFieldTextLabelField();
	
	/**
	 * Permet de se procurer la map des liste de valeurs des champs spécifique (clé/Liste valeur)
	 * @return la map des liste de valeurs des champs spécifique (clé/Liste valeur)
	 */
	Map<String, List<String>> getMapKeyFieldListField();	
	
	/**
	 * Permet de se procurer la map des liste des champs spécifique (clé/label)
	 * @return la map des liste des champs spécifique (clé/label)
	 */
	Map<String, String> getMapTextLabelField();
	
	/**
	 * Permet de se procurer la liste des en têtes
	 * @return la liste des en têtes
	 */
	List<String> getHeaderList();
	
	/**
	 * Permet de se procurer la liste des lignes
	 * @return la liste des lignes
	 */
	List<SpecificRow> getSpecificRowList();
	
	
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
	 * Permet de savoir si on a un index de slectionné
	 * @return
	 */
	Boolean haveCurrentSelectedIndexInList();
	
	/**
	 * Permet de définir l'index sélectionné dans le model
	 * @param index index
	 */
	void setCurrentSelectedIndexInList(Integer index);
	
	/**
	 * Permet de se procurer les valeurs sélectionné
	 * @return la map sous forme de clé valeur
	 */
	Map<String,String> getCurrentSelectedKeyValueMap();
	
	/**
	 * Permet de se procurer l'index courant sélectionné dans la liste
	 * @return l'index
	 */
	Integer getCurrentSelectedIndexInList();
	
	/**
	 * Permet de charger tous les champs spécifiques
	 * 
	 * @param index index du champ specifique à charger
	 */
	void loadAllField(Integer index);
	
	/**
	 * Permet d'ajouter un specific text refresh panel
	 * 
	 * @param specificTextRefreshPanel specific text refresh panel
	 */
	void addSpecificTextRefresh(ISpecificTextRefreshPanel specificTextRefreshPanel);
	
	/**
	 * Permet de se procurer l'index courant
	 * @return l'index courant
	 */
	Integer getCurrentIndex();
	
	/**
	 * Permet de déterminer s'il y a une configuration précédente
	 * @return 
	 */
	Boolean havePreviousSpecificConfiguration();
	
	/**
	 * Permet de déterminer s'il y a une configuration suivante
	 * @return 
	 */
	Boolean haveNextSpecificConfiguration();
	
	/**
	 * Permet de connaitre le nombre maximum de configuration
	 * @return
	 */
	Integer getNbMaxConfiguration();
	
	/**
	 * Permet de créer le Jlabel
	 * @param text texte du jlabel
	 * @return
	 */
	JLabel createJLabel(String text);
	
	/**
	 * Permet de savoir s'il y a une erreur de structure sur l'index courant
	 * @return Vrai si c'est le cas
	 */
	Boolean haveErrorStructuredInCurrentIndex();
	
	/**
	 * Permet d'ajouter un consumer sur le chargement des champs
	 * @param consumer consumer à ajouter
	 */
	void addRefreshConsumerOnLoadAllField(Consumer<?> consumer);
	
	/**
	 * Permet de définir le consumer permettant de clear la selection
	 * @param consumer consumer à définir
	 */
	void setClearSelectionConsumer(Consumer<?> consumer);
	
	/**
	 * Permet d'ajouter un index à la liste des index sélectionné
	 * @param index index à ajouter
	 */
	void addIndexToSelectedIndexList(Integer index);
	
	/**
	 * Permet d'effectuer la modification à partir d'une cellule editable
	 * @param rowIndex index de la ligne
	 * @param columnIndex index de la colonne
	 * @param newValue nouvelle valeur
	 */
	void updateFromCell(Integer rowIndex, Integer columnIndex, String newValue);
	
	/**
	 * Permet de supprimer la liste des index sélectionné de la liste
	 */
	void clearAllSelectedIndexList();
}
