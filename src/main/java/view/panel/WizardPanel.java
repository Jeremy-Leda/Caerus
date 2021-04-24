package view.panel;

import io.vavr.collection.Stream;
import view.interfaces.IAccessPanel;
import view.interfaces.IActionPanel;
import view.interfaces.IWizardPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 
 * Implementation du composant Assistant
 * 
 * @author jerem
 *
 */
public class WizardPanel implements IWizardPanel {

	private final Map<Long, List<IAccessPanel>> wizardStepMap;
	private final SortedMap<Long, Boolean> wizardStepEnabledMap;
	private final JPanel content;
	private final IActionPanel actionPanel;
	private final List<Consumer<?>> consumerOnChangeStepList;
	private Long currentStep;

	/**
	 * Constructeur
	 * 
	 * @param title Titre
	 */
	public WizardPanel(String title) {
		this.wizardStepMap = new LinkedHashMap<>();
		this.wizardStepEnabledMap = new TreeMap<>(Comparator.naturalOrder());
		this.actionPanel = new ActionPanel(2);
		this.currentStep = 0L;
		this.consumerOnChangeStepList = new ArrayList<Consumer<?>>();
		this.content = new JPanel();
		this.content.setBorder(BorderFactory.createTitledBorder(title));
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
	 * Permet de se procurer la fonction pour la mise à jour du titre du Jpanel
	 * Action
	 * 
	 * @return la fonction
	 */
	private Function<Void, String> getFunctionTitleJPanelActionRefresh() {
		return (v) -> String.format(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_WIZARD_NAVIGATION_PANEL_TITLE),
				this.currentStep + 1, getNbStepEnabled() + 1);
	}

	@Override
	public JComponent getJPanel() {
		return this.content;
	}

	@Override
	public void addStep(List<IAccessPanel> panelList) {
		Long nextIdStep = getNextIdStep();
		this.wizardStepEnabledMap.put(nextIdStep, Boolean.TRUE);
		this.wizardStepMap.put(nextIdStep, panelList);
		refreshActionPanel();
		if (nextIdStep == 0L) {
			refreshCurrentStep();
		}
	}

	@Override
	public void addConsumerOnChangeStep(Consumer<?> consumer) {
		if (null != consumer) {
			this.consumerOnChangeStepList.add(consumer);
		}
	}

	@Override
	public Boolean isLastPage() {
		return this.currentStep == getNbStepEnabled();
	}

	/**
	 * Action a effectué sur l'action previousStep
	 * 
	 * @return l'action
	 */
	private ActionListener previousStepAction() {
		return e -> {
			currentStep--;
			refreshCurrentStep();
		};
	}

	/**
	 * Action a effectué sur l'action nextStep
	 * 
	 * @return l'action
	 */
	private ActionListener nextStepAction() {
		return e -> {
			currentStep++;
			refreshCurrentStep();
		};
	}

	/**
	 * Permet de mettre à jour l'affichage pour l'étape en cours
	 */
	private void refreshCurrentStep() {
		List<Long> stepIdList = this.wizardStepEnabledMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
		Optional<Long> idStep = LongStream.range(0, stepIdList.size())
				.boxed()
				.filter(i -> i == currentStep)
				.map(i -> stepIdList.get(Math.toIntExact(i)))
				.findFirst();
		idStep.ifPresent(this::displayStep);
	}

	/**
	 * Permet d'afficher l'étape en fonction de son identifiant
	 * @param idStep identifiant de l'étape
	 */
	private void displayStep(Long idStep) {
		this.wizardStepMap.forEach((id, value) -> value.stream().forEach(panel -> panel.getJPanel().setVisible(id == idStep)));
		refreshActionPanel();
		refreshContent();
		this.consumerOnChangeStepList.forEach(v -> v.accept(null));
	}

	/**
	 * Permet de rafraichir le bloc action pour prendre en compte l'ensemble des modifications d'étapes
	 */
	private void refreshActionPanel() {
		this.actionPanel.setEnabled(0, currentStep > 0);
		this.actionPanel.setEnabled(1, currentStep < getNbStepEnabled());
		this.actionPanel.refresh();
	}

	@Override
	public void setStep(Long numStep) {
		if (numStep >= 0 && numStep <= getNbStepEnabled()) {
			currentStep = numStep;
			refreshCurrentStep();
		}
	}

	@Override
	public void setStateOfStep(Long numStep, Boolean enabled) {
		if (wizardStepEnabledMap.containsKey(numStep)) {
			this.wizardStepEnabledMap.put(numStep, enabled);
			refreshActionPanel();
		}
	}

	/**
	 * Permet de rafraichir le nb d'étape
	 */
	private Long getNbStepEnabled() {
		return this.wizardStepEnabledMap.values().stream().filter(Boolean::booleanValue).count() - 1;
	}

	/**
	 * Permet de se procurer le prochain id de l'étape
	 * @return le prochain id de l'étape
	 */
	private Long getNextIdStep() {
		if (this.wizardStepEnabledMap.isEmpty()) {
			return 0L;
		}
		return Stream.ofAll(this.wizardStepEnabledMap.keySet()).last() + 1;
	}

}
