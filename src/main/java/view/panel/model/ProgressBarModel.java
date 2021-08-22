package view.panel.model;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.*;

import model.interfaces.IProgressModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.interfaces.IProgressBarModel;
import view.panel.ProgressBarPanel;
import view.services.ExecutionService;
import view.windows.AnalysisTokenDetailResultWindow;

/**
 * 
 * Permet de créer la partie modèle de la progressBar
 * 
 * @author jerem
 *
 */
public class ProgressBarModel implements IProgressBarModel {

	private final Runnable actionProgressBarConsumer;
	private final Runnable closeWindowConsumer;
	private ExecutorService executorService;
	private static Logger logger = LoggerFactory.getLogger(ProgressBarModel.class);
	private Boolean run;

	public ProgressBarModel(Runnable actionProgressBarConsumer,
							Runnable closeWindowConsumer) {
		this.actionProgressBarConsumer = actionProgressBarConsumer;
		this.closeWindowConsumer = closeWindowConsumer;
	}

	public ProgressBarModel() {
		this.actionProgressBarConsumer = null;
		this.closeWindowConsumer = null;
	}

	@Override
	public void launchTreatment(JProgressBar progressBar, JLabel labelProgress, IProgressModel model) {
		if (null != progressBar) {
			executorService = Executors.newFixedThreadPool(2);
			run = true;
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
			executorService.execute(() -> {
				while (run) {
					int value = model.getProgress();
					progressBar.setValue(value);
					labelProgress.setText(getPercentage(value));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			});
			if (this.actionProgressBarConsumer != null) {
				executorService.execute(this::launchTreatment);
			}
		}
	}


	@Override
	public void stopExecutor() {
		run = false;
		executorService.shutdownNow();
	}

	private void launchTreatment() {
		actionProgressBarConsumer.run();
		closeWindowConsumer.run();
		stopExecutor();
	}

	private String getPercentage(int value) {
		StringBuilder sb = new StringBuilder();
		sb.append(value).append(" %");
		return sb.toString();
	}
}
