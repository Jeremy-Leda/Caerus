package view.interfaces;

import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * Interface pour g�rer les listBox
 * 
 * @author jerem
 *
 */
public interface IListPanel extends IAccessPanel {
	
	/**
	 * Permet de se procurer le label selectionn�
	 * @return le label selectionne
	 */
	String getLabelSelected();

	/**
	 * Permet d'ajouter un consumer sur le changement de la selection
	 * @param consumer consumer
	 */
	void addConsumerOnSelectChange(Consumer<?> consumer);
	
	/**
	 * Permet de mettre � jour la combobox avec les labels suivants
	 * @param labels labels
	 */
	void refresh(List<String> labels);
	
	/**
	 * Permet de de s�lectionner un item
	 * @param itemToSelect item � selectionner
	 */
	void selectItem(String itemToSelect);
	
	/**
	 * Permet de se procurer le nombre d'items
	 * @return
	 */
	int getItemCount();
	
}
