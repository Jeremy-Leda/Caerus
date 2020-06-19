package view.interfaces;

import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * Interface pour g�rer l'assistant
 * 
 * @author jerem
 *
 */
public interface IWizardPanel extends IAccessPanel {

	/**
	 * Permet d'ajouter une �tape � l'assistant
	 * 
	 * @param panelList liste des panels pour l'�tapes (seront affich�s les uns en
	 *                  dessous des autres)
	 */
	void addStep(List<IAccessPanel> panelList);
	
	/**
	 * Permet d'ajouter un consumer sur le changement d'une page
	 * @param consumer consumer
	 */
	void addConsumerOnChangeStep(Consumer<?> consumer);
	
	/**
	 * Permet de savoir si c'est la derni�re page
	 * @return Vrai si c'est le cas
	 */
	Boolean isLastPage();
	
	/**
	 * Permet de d�finir le retour � une �tape
	 * @param numStep Num�ro de l'�tape
	 */
	void setStep(Integer numStep);

}
