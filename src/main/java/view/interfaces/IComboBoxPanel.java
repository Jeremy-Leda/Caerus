package view.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * Interface pour gérer les combobox
 * 
 * @author jerem
 *
 */
public interface IComboBoxPanel extends IAccessPanel {
	
	/**
	 * Permet de se procurer le label selectionné
	 * @return le label selectionne
	 */
	String getLabelSelected();

	/**
	 * Permet d'ajouter un consumer sur le changement de la selection
	 * @param consumer consumer
	 */
	void addConsumerOnSelectChange(Consumer<?> consumer);
	
	/**
	 * Permet de mettre à jour la combobox avec les labels suivants
	 * @param labels labels
	 */
	void refresh(Collection<String> labels);
	
	/**
	 * Permet de de sélectionner un item
	 * @param itemToSelect item à sélectionner
	 */
	void selectItem(String itemToSelect);
	
	/**
	 * Permet de se procurer le nombre d'items
	 * @return
	 */
	int getItemCount();

	/**
	 * Permet de savoir si l'item existe
	 * @param item item à vérifier
	 * @return Vrai si l'item existe
	 */
	Boolean itemExist(String item);

	/**
	 * Permet d'ajouter un item et de le sélectionner
	 * @param newItem nouvel item
	 */
	void addAndSelectItem(String newItem);

	/**
	 * Permet de supprimer l'item passer en paramètre
	 * @param itemToDelete l'item passer en paramètre à supprimer
	 */
	void delete(String itemToDelete);
	
}
