package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IGenericAccessPanel;
import view.interfaces.IInformationPanel;
import view.interfaces.IManageInconsistencyBaseCodeErrorPanel;
import view.interfaces.IManageInconsistencyErrorPanel;
import view.panel.ActionPanel;
import view.panel.GenericAccessPanel;
import view.panel.InformationPanel;
import view.panel.ManageInconsistencyBaseCodeErrorPanel;
import view.panel.ManageInconsistencyErrorPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Fenêtre pour l'affichage des erreurs d'incohérences
 * 
 * @author jerem
 *
 */
public class InconsistencyErrorView extends ModalJFrameAbstract {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8336151891903782150L;
	private final JPanel content;
	private final IInformationPanel informationPanel;
	private final IManageInconsistencyErrorPanel inconsistencyErrorsPanel;
	private final IManageInconsistencyBaseCodeErrorPanel inconsistencyBaseCodeErrorPanel;
	private final IActionPanel actionPanel;
	private final IGenericAccessPanel genericInconsistencyErrorsPanel;
	private final IGenericAccessPanel genericInconsistencyBaseCodeErrorsPanel;
	
	
	/**
	 * Constructeur
	 * @param configurationControler controller
	 */
	public InconsistencyErrorView(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_TITLE), configurationControler);
		this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION, 
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_MESSAGE_PANEL_TITLE), 
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_MESSAGE), true, true);
		this.inconsistencyErrorsPanel = new ManageInconsistencyErrorPanel();
		this.inconsistencyBaseCodeErrorPanel = new ManageInconsistencyBaseCodeErrorPanel();
		this.genericInconsistencyErrorsPanel = new GenericAccessPanel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_PANEL_TITLE));
		this.genericInconsistencyErrorsPanel.addComponent(this.inconsistencyErrorsPanel.getJPanel());
		this.genericInconsistencyBaseCodeErrorsPanel = new GenericAccessPanel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_MISSING_BASE_CODE_PANEL_TITLE));
		this.genericInconsistencyBaseCodeErrorsPanel.addComponent(this.inconsistencyBaseCodeErrorPanel.getJPanel());
		this.actionPanel = new ActionPanel(1);
		this.content = new JPanel();
		createWindow();
	}

	@Override
	public void initComponents() {
		fillErrorsPanel();
		refreshActionPanel();
		createContent();
	}

	@Override
	public JPanel getContent() {
		return content;
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.informationPanel.getJPanel());
		if (getControler().haveInconsistencyError()) {
			content.add(this.genericInconsistencyErrorsPanel.getJPanel());
		}
		if (getControler().haveMissingBaseCodeError()) {
			content.add(this.genericInconsistencyBaseCodeErrorsPanel.getJPanel());
		}
		content.add(this.actionPanel.getJPanel());
	}
	
	/**
	 * Permet de remplir la liste des erreurs
	 */
	private void fillErrorsPanel() {
		getControler().getInconsistencyChangeTextErrorList().forEach(error -> this.inconsistencyErrorsPanel.addInconsistencyError(error));
		getControler().getMissingBaseCodeErrorList().forEach(error -> this.inconsistencyBaseCodeErrorPanel.addInconsistencyBaseCodeError(error));
	}
	
	/**
	 * Permet de rafraichir les libellés et action pou les boutons
	 */
	private void refreshActionPanel() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_BUTTONS_CLOSE_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_BUTTONS_PANEL_TITLE), messageButtonMap);
		this.actionPanel.addAction(0, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
	}
	
	@Override
	public String getWindowName() {
		return "Inconsistency error";
	}

}
