package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ExcelTypeGenerationEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionOnClose;
import view.interfaces.IActionPanel;
import view.interfaces.IInformationPanel;
import view.interfaces.IManageTextDisplayPanel;
import view.panel.ActionPanel;
import view.panel.DisplayTextsFilteredWithPagingPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Permet d'afficher la fenêtre de gestion des textes
 * 
 * @author jerem
 *
 */
public class ManageText extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6375903939438774209L;
	private final JPanel content;
	private final IInformationPanel informationPanel;
	private final IManageTextDisplayPanel displayTextsList;
	private final IActionPanel actionPanel;
	private IActionOnClose manageTextFilter;

	public ManageText(IConfigurationControler configurationControler) {

		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_TITLE),
				configurationControler, false);
		this.displayTextsList = new DisplayTextsFilteredWithPagingPanel(configurationControler);
		this.actionPanel = new ActionPanel(4);
		this.content = new JPanel();
		this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_INFORMATION_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_INFORMATION_LABEL),
				false, true);
		super.addActionOnClose(closeAutomaticallyOtherChildrenWindow());
		this.displayTextsList.addConsumerOnCloseEditText(getConsumerOnCloseEditText());
		this.displayTextsList.addConsumerOnOpenEditText(getConsumerOnOpenEditText());
		refreshActionPanelMessage();
		createWindow();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.informationPanel.getJPanel());
		content.add(this.displayTextsList.getJPanel());
		content.add(this.actionPanel.getJPanel());
	}

	/**
	 * Permet de rafraichir l'affichage pour les bouttons
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0,
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_FILTERS_BUTTON_LABEL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_CLASSICAL_BUTTON_LABEL));
		messageButtonMap.put(2, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_SPECIFIC_BUTTON_LABEL));
		messageButtonMap.put(3, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_EXPORT_DOCUMENT_TEXT_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_MANAGE_TEXTS_GENERATE_EXCEL_PANEL_TITLE), messageButtonMap);
		this.actionPanel.addAction(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				manageTextFilter = new ManageTextFilter(getControler(), v -> {
					displayTextsList.refresh();
					repack();
				});
//				displayTextsList.setEnabledAllButton(false);
//				actionPanel.setEnabled(0, Boolean.FALSE);
//				manageTextFilter.addActionOnClose(v -> {
//					displayTextsList.setEnabledAllButton(true);
//					actionPanel.setEnabled(0, Boolean.TRUE);
//					manageTextFilter = null;
//				});
			}
		});
		this.actionPanel.addAction(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveReferenceExcels(getControler(), ExcelTypeGenerationEnum.MANAGE_TEXTS);
			}
		});
		this.actionPanel.addAction(2, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SaveCustomExcel(getControler(), ExcelTypeGenerationEnum.MANAGE_TEXTS);
			}
		});
		this.actionPanel.addAction(3, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportDocument(getControler());
			}
		});
	}

	@Override
	public void initComponents() {
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	/**
	 * Permet de se procurer le consumer sur l'ouverture de la fenêtre d'édition
	 * 
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerOnOpenEditText() {
		return v -> {
			actionPanel.setEnabled(0, Boolean.FALSE);
		};
	}

	/**
	 * Permet de se procurer le consumer sur la fermeture de la fenêtre d'édition
	 * 
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerOnCloseEditText() {
		return v -> {
			actionPanel.setEnabled(0, Boolean.TRUE);
		};
	}

	/**
	 * Consumer pour rattacher la fermeture de la fenêtre fille si présente
	 * 
	 * @return
	 */
	private Consumer<Void> closeAutomaticallyOtherChildrenWindow() {
		return (v) -> {
			if (null != manageTextFilter) {
				manageTextFilter.closeFrame();
			}
			this.displayTextsList.close();
		};
	}

	@Override
	public String getWindowName() {
		return "Window for manage texts from library";
	}

}
