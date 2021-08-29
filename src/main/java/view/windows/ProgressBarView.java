package view.windows;

import java.awt.*;
import java.util.function.Consumer;

import javax.swing.*;

import model.interfaces.IProgressModel;
import view.abstracts.ModalJFrameAbstract;
import view.interfaces.IProgressBarModel;
import view.interfaces.IProgressBarPanel;
import view.panel.ProgressBarPanel;
import view.panel.model.ProgressBarModel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

public class ProgressBarView extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7853324764841749772L;
	private final IProgressBarModel progressBarModel;
	private final JPanel content;
	private final IProgressBarPanel progressBarPanel;
	private final JLabel labelProgress;
	private final IProgressModel model;
	/**
	 * Permet de construire une vue pour la progressBar
	 *
	 * @param actionProgressBarConsumer action pour la progressBar
	 * @param model Le model pour la barre de progression
	 */
	public ProgressBarView(Runnable actionProgressBarConsumer, IProgressModel model, String informationLoading) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_PROGRESS_BAR_PANEL_TITLE), null);
		this.model = model;
		this.progressBarModel = new ProgressBarModel(actionProgressBarConsumer,
				() -> closeFrame());
		this.progressBarPanel = new ProgressBarPanel(this.progressBarModel);
		this.progressBarPanel.getJPanel().setAlignmentX(Component.CENTER_ALIGNMENT);
		this.content = new JPanel();
		this.labelProgress = new JLabel(informationLoading);
		this.labelProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.labelProgress.setBorder(BorderFactory.createEmptyBorder(30,0,0,0));
		createWindow(null, frame -> {
			this.progressBarPanel.launchTreatment(model);
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		});
	}

	@Override
	public void initComponents() {
		createContent();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.progressBarPanel.getJPanel());
		content.add(labelProgress);
	}


	@Override
	public void dispose() {
		if (this.model.isRunning()) {
			this.labelProgress.setText(getMessage(Constants.WINDOW_LOADING_CANCEL_LABEL));
			this.model.cancel();
		}
		super.dispose();
	}

	@Override
	public JPanel getContent() {
		return content;
	}

	@Override
	public String getWindowName() {
		return "progressBar View";
	}

}
