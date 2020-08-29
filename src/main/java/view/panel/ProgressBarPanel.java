package view.panel;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import view.interfaces.IProgressBarModel;
import view.interfaces.IProgressBarPanel;

/**
 * 
 * Permet de créer un panel avec une progressBar
 * 
 * @author jerem
 *
 */
public class ProgressBarPanel implements IProgressBarPanel {

	private final JProgressBar progressBar;
	private final IProgressBarModel progressBarModel;

	/**
	 * Constructeur
	 * 
	 * @param progressBarModel Modele affecté
	 * @param label            libellé a afficher dans la progressbar
	 */
	public ProgressBarPanel(IProgressBarModel progressBarModel, String label) {
		this.progressBarModel = progressBarModel;
		this.progressBar = new JProgressBar();
		this.progressBar.setString(label);
		this.progressBar.setStringPainted(true);
	}

	@Override
	public JComponent getJPanel() {
		JPanel panel = new JPanel();
		panel.add(progressBar);
		return panel;
	}

	@Override
	public void launchTreatment() {
		this.progressBarModel.launchTreatment(this.progressBar);
	}

}
