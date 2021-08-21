package view.panel.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.*;

import view.interfaces.IProgressBarModel;
import view.panel.ProgressBarPanel;
import view.services.ExecutionService;

/**
 * 
 * Permet de créer la partie modèle de la progressBar
 * 
 * @author jerem
 *
 */
public class ProgressBarModel implements IProgressBarModel {

	private final Runnable actionProgressBarConsumer;
	private final Consumer<Consumer<Integer>> updateProgressBarConsumer;
	private final Runnable closeWindowConsumer;
	private JProgressBar progressBar;
	private JLabel labelProgress;
	private final Integer maximumValue;
	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	public ProgressBarModel(Runnable actionProgressBarConsumer,
							Consumer<Consumer<Integer>> updateProgressBarConsumer,
							Runnable closeWindowConsumer,
							Integer maximumValue) {
		this.actionProgressBarConsumer = actionProgressBarConsumer;
		this.updateProgressBarConsumer = updateProgressBarConsumer;
		this.closeWindowConsumer = closeWindowConsumer;
		this.maximumValue = maximumValue;
	}

	public ProgressBarModel() {
		this.actionProgressBarConsumer = null;
		this.updateProgressBarConsumer = null;
		this.closeWindowConsumer = null;
		this.maximumValue = null;
	}

	@Override
	public void launchTreatment(JProgressBar progressBar, JLabel labelProgress) {
		launchTreatment(progressBar, labelProgress, this.updateProgressBarConsumer, maximumValue);
	}

	@Override
	public void launchTreatment(JProgressBar progressBar, JLabel labelProgress, Consumer<Consumer<Integer>> updateProgressBarConsumer, Integer maximumValue) {
		this.progressBar = progressBar;
		this.labelProgress = labelProgress;
		if (null != updateProgressBarConsumer && null != progressBar) {
			progressBar.setMinimum(0);
			progressBar.setMaximum(maximumValue);
			executorService.execute(() -> updateProgressBarConsumer.accept(getUpdateProgressBarConsumer()));
			if (this.actionProgressBarConsumer != null) {
				executorService.execute(this::launchTreatment);
			}
		}
	}

	private void launchTreatment() {
		actionProgressBarConsumer.run();
		closeWindowConsumer.run();
		executorService.shutdownNow();
	}

	/**
	 * Permet de mettre à jour la valeur de la progress bar
	 * 
	 * @return le consumer
	 */
	private Consumer<Integer> getUpdateProgressBarConsumer() {
		return value -> {
			progressBar.setValue(value);
			labelProgress.setText(getPercentage(value));
		};
	}

	private String getPercentage(int value) {
		StringBuilder sb = new StringBuilder();
		int realValue = value * 100 / this.progressBar.getMaximum();
		sb.append(realValue).append(" %");
		return sb.toString();
	}
}
