package view.panel;

import javax.swing.*;

import model.interfaces.IProgressModel;
import utils.RessourcesUtils;
import view.beans.PictureTypeEnum;
import view.interfaces.IProgressBarModel;
import view.interfaces.IProgressBarPanel;

import java.awt.*;
import java.util.function.Consumer;

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
	private final JPanel panel = new JPanel();

	/**
	 * Constructeur
	 * 
	 * @param progressBarModel Modele affecté
	 * @param label            libellé a afficher dans la progressbar
	 */
	public ProgressBarPanel(IProgressBarModel progressBarModel) {
		this.progressBarModel = progressBarModel;
		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);
		this.iconLabel = new JLabel(new ImageIcon(RessourcesUtils.getInstance().getAnimatedImage(PictureTypeEnum.PROGRESS)));
		this.labelProgress = new JLabel("Chargement");
		this.labelProgress.setFont(new Font("Arial", 25, 25));
	}

	@Override
	public JComponent getJPanel() {
		panel.setLayout(new OverlayLayout(panel));
		this.labelProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(this.iconLabel);
		panel.add(this.labelProgress);
		panel.setComponentZOrder(this.labelProgress, 0);
		return panel;
	}

	@Override
	public void launchTreatment(IProgressModel model) {
		this.progressBarModel.launchTreatment(this.progressBar, this.labelProgress, model);
	}

	@Override
	public void stop() {
		this.progressBarModel.stopExecutor();
	}


}
