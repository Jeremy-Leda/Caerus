package view.interfaces;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 
 * Interface pour la cr�ation des panels textes g�n�riques
 * Permet la cr�ation des textarea avec scrollpane int�gr� ou des JtextField
 * 
 * @author jerem
 *
 */
public interface IContentTextGenericPanel extends IAccessPanel {
	
	/**
	 * Permet de cr�er les composants � l'aide de la map d'information
	 * @param informationFieldMap map d'information
	 */
	void refreshComponents(Map<String, String> informationFieldMap);
	
	/**
	 * Permet de rafraichir l'IHM
	 * @param titlePanel Titre du panel
	 */
	void refresh(String titlePanel);
	
	/**
	 * Permet d'ajouter un key listener sur un champ particulier
	 * @param key Cl� pour l'affectation du key listener
	 * @param keyListener Cl� listener � affecter
	 */
	void addKeyListener(String key, KeyListener keyListener);
	
	/**
	 * Permet d'ajouter un key listener sur l'ensemble des champs
	 * @param keyListener Cl� listener � affecter
	 */
	void addKeyListenerOnAllField(KeyListener keyListener);
	
	/**
	 * Permet de se procurer la map cl� valeur des champs
	 * @return la map contenant la cl� et valeur
	 */
	Map<String, String> getFieldValueMap();
	
	/**
	 * Permet de se procurer la valeur d'un champ
	 * @param key Cl� du champ
	 * @return la valeur
	 */
	String getValue(String key);
	
	/**
	 * Permet d'ajouter un focus listener sur un champ particulier
	 * @param key Cl� pour l'affectation du key listener
	 * @param focusListener Focus listener � affecter
	 */
	void addFocusListener(String key, FocusListener focusListener);
	
	/**
	 * Permet d'ajouter un focus listener sur l'ensemble des champs
	 * @param focusListener Focus listener � affecter
	 */
	void addFocusListenerOnAllField(FocusListener focusListener);
	
	/**
	 * Permet de d�finir si le champ est actif
	 * @param key Cl� du champ
	 * @param enabled Vrai, si actif faux sinon
	 */
	void setEnabled(String key, boolean enabled);
	
	/**
	 * Permet de d�finir si tous les champs sont actif
	 * @param enabled Vrai, si actif faux sinon
	 */
	void setEnabledOnAllField(boolean enabled);
	
	/**
	 * Permet de d�finir la valeur dans le champ texte � partir de la cl�
	 * @param key cl� du champ
	 * @param newValue nouvelle valeur
	 */
	void setValue(String key, String newValue);
	
	/**
	 * Permet de recharger les valeurs depuis le serveur
	 */
	void reloadValue();
	
	/**
	 * Permet de d�finir un consumer pour rafraichir l'interface
	 * @param refreshDisplay refresh display consumer
	 */
	void setRefreshDisplayConsumer(Consumer<?> refreshDisplay);
	
}
