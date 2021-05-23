package view.panel;

import javax.swing.*;

import utils.RessourcesUtils;
import view.beans.PictureTypeEnum;
import view.interfaces.IProgressBarModel;
import view.interfaces.IProgressBarPanel;

import java.awt.*;

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
	private final JLabel iconLabel;
	private final JLabel labelProgress;

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
		this.iconLabel = new JLabel(new ImageIcon(RessourcesUtils.getInstance().getAnimatedImage(PictureTypeEnum.PROGRESS)));
		this.labelProgress = new JLabel();
		this.labelProgress.setFont(new Font("Arial", 25, 25));
	}

	@Override
	public JComponent getJPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new OverlayLayout(panel));
		this.labelProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(this.iconLabel);
		panel.add(this.labelProgress);
		panel.setComponentZOrder(this.labelProgress, 0);
		return panel;
	}

	@Override
	public void launchTreatment() {
		this.progressBarModel.launchTreatment(this.progressBar, this.labelProgress);
	}

}
