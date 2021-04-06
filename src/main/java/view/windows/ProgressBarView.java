package view.windows;

import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

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

	/**
	 * Permet de construire une vue pour la progressBar
	 * 
	 * @param actionProgressBarConsumer action pour la progressBar
	 * @param updateProgressBarConsumer consumer de mise à jour pour la progressBar
	 * @param maximumValue              valeur max de la porgressBar
	 * @param label                     libellé a afficher dans la progressbar
	 */
	public ProgressBarView(Consumer<Void> actionProgressBarConsumer,
			Consumer<Consumer<Integer>> updateProgressBarConsumer, Integer maximumValue, String label) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_PROGRESS_BAR_PANEL_TITLE), null);
		this.progressBarModel = new ProgressBarModel(actionProgressBarConsumer, updateProgressBarConsumer,
				r -> closeFrame(), maximumValue);
		this.progressBarPanel = new ProgressBarPanel(this.progressBarModel, label);
		this.content = new JPanel();
		createWindow(frame -> frame.setUndecorated(false), frame -> this.progressBarPanel.launchTreatment());
//		setUndecorated(true);
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
