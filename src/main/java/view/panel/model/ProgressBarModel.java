package view.panel.model;

import java.util.function.Consumer;

import javax.swing.JProgressBar;

import view.interfaces.IProgressBarModel;

/**
 * 
 * Permet de créer la partie modèle de la progressBar
 * 
 * @author jerem
 *
 */
public class ProgressBarModel implements IProgressBarModel {

	private final Consumer<Void> actionProgressBarConsumer;
	private final Consumer<Consumer<Integer>> updateProgressBarConsumer;
	private final Consumer<Void> closeWindowConsumer;
	private JProgressBar progressBar;
	private final Integer maximumValue;

	public ProgressBarModel(Consumer<Void> actionProgressBarConsumer,
			Consumer<Consumer<Integer>> updateProgressBarConsumer, Consumer<Void> closeWindowConsumer,
			Integer maximumValue) {
		this.actionProgressBarConsumer = actionProgressBarConsumer;
		this.updateProgressBarConsumer = updateProgressBarConsumer;
		this.closeWindowConsumer = closeWindowConsumer;
		this.maximumValue = maximumValue;
	}

	@Override
	public void launchTreatment(JProgressBar progressBar) {
		this.progressBar = progressBar;
		if (null != actionProgressBarConsumer && null != updateProgressBarConsumer && null != progressBar) {
			progressBar.setMinimum(0);
			progressBar.setMaximum(this.maximumValue);
			new Thread(() -> {
				actionProgressBarConsumer.accept(null);
				closeWindowConsumer.accept(null);
			}).start();
			new Thread(() -> updateProgressBarConsumer.accept(getUpdateProgressBarConsumer())).start();
		}
	}

	/**
	 * Permet de mettre à jour la valeur de la progress bar
	 * 
	 * @return le consumer
	 */
	private Consumer<Integer> getUpdateProgressBarConsumer() {
		return value -> {
			progressBar.setValue(value);
		};
	}
}
