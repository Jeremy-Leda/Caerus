package view.interfaces;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 
 * Interface pour la création des panels textes génériques
 * Permet la création des textarea avec scrollpane intégré ou des JtextField
 * 
 * @author jerem
 *
 */
public interface IContentTextGenericPanel extends IAccessPanel {
	
	/**
	 * Permet de créer les composants à l'aide de la map d'information
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
	 * @param key Clé pour l'affectation du key listener
	 * @param keyListener Clé listener à affecter
	 */
	void addKeyListener(String key, KeyListener keyListener);
	
	/**
	 * Permet d'ajouter un key listener sur l'ensemble des champs
	 * @param keyListener Clé listener à affecter
	 */
	void addKeyListenerOnAllField(KeyListener keyListener);
	
	/**
	 * Permet de se procurer la map clé valeur des champs
	 * @return la map contenant la clé et valeur
	 */
	Map<String, String> getFieldValueMap();
	
	/**
	 * Permet de se procurer la valeur d'un champ
	 * @param key Clé du champ
	 * @return la valeur
	 */
	String getValue(String key);
	
	/**
	 * Permet d'ajouter un focus listener sur un champ particulier
	 * @param key Clé pour l'affectation du key listener
	 * @param focusListener Focus listener à affecter
	 */
	void addFocusListener(String key, FocusListener focusListener);
	
	/**
	 * Permet d'ajouter un focus listener sur l'ensemble des champs
	 * @param focusListener Focus listener à affecter
	 */
	void addFocusListenerOnAllField(FocusListener focusListener);
	
	/**
	 * Permet de définir si le champ est actif
	 * @param key Clé du champ
	 * @param enabled Vrai, si actif faux sinon
	 */
	void setEnabled(String key, boolean enabled);
	
	/**
	 * Permet de définir si tous les champs sont actif
	 * @param enabled Vrai, si actif faux sinon
	 */
	void setEnabledOnAllField(boolean enabled);

	/**
	 * Permet de définir si les champs sont en readOnly
	 * @param isReadOnly vrai si readonly, faux sinon
	 */
	void setReadOnly(Boolean isReadOnly);
	
	/**
	 * Permet de définir la valeur dans le champ texte à partir de la clé
	 * @param key clé du champ
	 * @param newValue nouvelle valeur
	 */
	void setValue(String key, String newValue);
	
	/**
	 * Permet de recharger les valeurs depuis le serveur
	 */
	void reloadValue();
	
	/**
	 * Permet de définir un consumer pour rafraichir l'interface
	 * @param refreshDisplay refresh display consumer
	 */
	void setRefreshDisplayConsumer(Consumer<?> refreshDisplay);

	/**
	 * Permet de définir la clé du texte à utiliser
	 * @param keyText Clé du texte
	 */
	void setKeyText(String keyText);
	
}
