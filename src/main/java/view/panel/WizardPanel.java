package view.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import view.interfaces.IAccessPanel;
import view.interfaces.IActionPanel;
import view.interfaces.IWizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Implementation du composant Assistant
 * 
 * @author jerem
 *
 */
public class WizardPanel implements IWizardPanel {

	private final Map<Integer, List<IAccessPanel>> wizardStepMap;
	private final JPanel content;
	private final IActionPanel actionPanel;
	private Integer nbStep;
	private final List<Consumer<?>> consumerOnChangeStepList;
	private Integer currentStep;

	/**
	 * Constructeur
	 * 
	 * @param title Titre
	 */
	public WizardPanel(String title) {
		this.wizardStepMap = new LinkedHashMap<Integer, List<IAccessPanel>>();
		this.content = new JPanel();
		this.content.setBorder(BorderFactory.createTitledBorder(title));
		this.actionPanel = new ActionPanel(2);
		this.nbStep = -1;
		this.currentStep = 0;
		this.consumerOnChangeStepList = new ArrayList<Consumer<?>>();
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		initActionContent();
	}

	/**
	 * Permet de réinitialiser le contenu
	 */
	private void refreshContent() {
		content.removeAll();
		wizardStepMap.values().stream().flatMap(panelList -> panelList.stream())
				.filter(panel -> panel.getJPanel().isVisible()).forEach(panel -> content.add(panel.getJPanel()));
		content.add(this.actionPanel.getJPanel());
	}

	/**
	 * Permet d'initialiser le contenu du panel action
	 */
	private void initActionContent() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_PREVIOUS_BUTTON_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_NEXT_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(null, messageButtonMap);
		this.actionPanel.setFunctionRefreshLabelTitleDynamically(getFunctionTitleJPanelActionRefresh());
		this.actionPanel.addAction(0, previousStepAction());
		this.actionPanel.addAction(1, nextStepAction());

	}

	/**
	 * Permet de se produire la fonction pour la mise à jour du titre du Jpanel
	 * Action
	 * 
	 * @return la fonction
	 */
	private Function<Void, String> getFunctionTitleJPanelActionRefresh() {
		return (v) -> String.format(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_PANEL_TITLE),
				this.currentStep + 1, this.nbStep + 1);
	}

	@Override
	public JComponent getJPanel() {
		return this.content;
	}

	@Override
	public void addStep(List<IAccessPanel> panelList) {
		this.nbStep++;
		this.wizardStepMap.put(nbStep, panelList);
		refreshCurrentStep();
	}

	@Override
	public void addConsumerOnChangeStep(Consumer<?> consumer) {
		if (null != consumer) {
			this.consumerOnChangeStepList.add(consumer);
		}
	}

	@Override
	public Boolean isLastPage() {
		return this.currentStep == this.nbStep;
	}

	/**
	 * Action a effectué sur l'action previousStep
	 * 
	 * @return l'action
	 */
	private ActionListener previousStepAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentStep--;
				refreshCurrentStep();
			}
		};
	}

	/**
	 * Action a effectué sur l'action nextStep
	 * 
	 * @return l'action
	 */
	private ActionListener nextStepAction() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentStep++;
				refreshCurrentStep();
			}
		};
	}

	/**
	 * Permet de mettre à jour l'affichage pour l'étape en cours
	 */
	private void refreshCurrentStep() {
		this.wizardStepMap.forEach(
				(key, value) -> value.stream().forEach(panel -> panel.getJPanel().setVisible(currentStep == key)));
		this.actionPanel.setEnabled(0, currentStep > 0);
		this.actionPanel.setEnabled(1, currentStep < nbStep);
		this.actionPanel.refresh();
		refreshContent();
		this.consumerOnChangeStepList.forEach(v -> v.accept(null));
	}

	@Override
	public void setStep(Integer numStep) {
		if (numStep >= 0 && numStep <= nbStep) {
			currentStep = numStep;
			refreshCurrentStep();
		}
	}

}
