package view.interfaces;

import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * Interface pour gérer l'assistant
 * 
 * @author jerem
 *
 */
public interface IWizardPanel extends IAccessPanel {

	/**
	 * Permet d'ajouter une étape à l'assistant
	 * 
	 * @param panelList liste des panels pour l'étapes (seront affichés les uns en
	 *                  dessous des autres)
	 */
	void addStep(List<IAccessPanel> panelList);
	
	/**
	 * Permet d'ajouter un consumer sur le changement d'une page
	 * @param consumer consumer
	 */
	void addConsumerOnChangeStep(Consumer<?> consumer);
	
	/**
	 * Permet de savoir si c'est la dernière page
	 * @return Vrai si c'est le cas
	 */
	Boolean isLastPage();
	
	/**
	 * Permet de définir le retour à une étape
	 * @param numStep Numéro de l'étape
	 */
	void setStep(Long numStep);

	/**
	 * Permet d'activer ou désactiver une étape de l'assistant
	 * Permettra de faire comme si l'étape n'avait jamais existé
	 * @param numStep Numéro de l'étape
	 * @param enabled état de l'étape
	 */
	void setStateOfStep(Long numStep, Boolean enabled);

	/**
	 * Permet de rafraichir l'affichage
	 */
	void refresh();

}
