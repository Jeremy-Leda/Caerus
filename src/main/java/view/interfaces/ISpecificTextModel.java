package view.interfaces;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * Interfaces pour fournir les m�thodes n�cessaires aux IHM des textes sp�cifiques
 * 
 * @author jerem
 *
 */
public interface ISpecificTextModel {

	/**
	 * Permet de se procurer la map des headers (cl�/valeur)
	 * @return la map des headers (cl�/valeur)
	 */
	Map<String, String> getMapHeaderKeyFieldText();
	
	/**
	 * Permet de se procurer la map des headers (cl�/label)
	 * @return la map des headers (cl�/label)
	 */
	Map<String, String> getMapHeaderFieldTextLabelField();
	
	/**
	 * Permet de se procurer la map des liste de valeurs des champs sp�cifique (cl�/Liste valeur)
	 * @return la map des liste de valeurs des champs sp�cifique (cl�/Liste valeur)
	 */
	Map<String, List<String>> getMapKeyFieldListField();	
	
	/**
	 * Permet de se procurer la map des liste des champs sp�cifique (cl�/label)
	 * @return la map des liste des champs sp�cifique (cl�/label)
	 */
	Map<String, String> getMapTextLabelField();
	
	/**
	 * Permet de mettre � jour une valeur cot� serveur
	 * @param key Cl�
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
	 * Permet de mettre � jour les informations dans la liste
	 * 
	 * @param mapKeyFieldTextField map des champs de l'interface
	 */
	void updateSpecificField(Map<String, JTextField> mapKeyFieldTextField);
	
	/**
	 * Permet de supprimer les champs s�lectionn�s
	 */
	void removeSpecificField();
	
	/**
	 * Permet de savoir si on a un index de slectionn�
	 * @return
	 */
	Boolean haveCurrentSelectedIndexInList();
	
	/**
	 * Permet de d�finir l'index s�lectionn� dans le model
	 * @param index index
	 */
	void setCurrentSelectedIndexInList(Integer index);
	
	/**
	 * Permet de se procurer les valeurs s�lectionn�
	 * @return la map sous forme de cl� valeur
	 */
	Map<String,String> getCurrentSelectedKeyValueMap();
	
	/**
	 * Permet de se procurer l'index courant s�lectionn� dans la liste
	 * @return l'index
	 */
	Integer getCurrentSelectedIndexInList();
	
	/**
	 * Permet de charger tous les champs sp�cifiques
	 * 
	 * @param index index du champ specifique � charger
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
	 * Permet de d�terminer s'il y a une configuration pr�c�dente
	 * @return 
	 */
	Boolean havePreviousSpecificConfiguration();
	
	/**
	 * Permet de d�terminer s'il y a une configuration suivante
	 * @return 
	 */
	Boolean haveNextSpecificConfiguration();
	
	/**
	 * Permet de connaitre le nombre maximum de configuration
	 * @return
	 */
	Integer getNbMaxConfiguration();
	
	/**
	 * Permet de cr�er le Jlabel
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
	 * @param consumer consumer � ajouter
	 */
	void addRefreshConsumerOnLoadAllField(Consumer<?> consumer);
	
}
