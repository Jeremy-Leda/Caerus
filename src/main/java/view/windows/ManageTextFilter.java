package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import controler.IConfigurationControler;
import view.abstracts.ModalJFrameAbstract;
import view.interfaces.IActionPanel;
import view.interfaces.IComboBoxPanel;
import view.interfaces.IManageActionFilterPanel;
import view.panel.ActionPanel;
import view.panel.ComboBoxPanel;
import view.panel.ManageFilterPanelWithActions;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Fenêtre permettant de filtrer les textes lors de leur gestion
 * 
 * @author jerem
 *
 */
public class ManageTextFilter extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9094281449897480175L;
	private final JPanel content;
	private final IComboBoxPanel corpusFilterPanel;
	private final IManageActionFilterPanel manageActionFilterPanel;
	private final IActionPanel actionPanel;
	private final String todosCorpus;
	private final Consumer<Void> consumerForRefreshAfterApplyFilters;
	
	public ManageTextFilter(IConfigurationControler configurationControler, Consumer<Void> consumerForRefreshAfterApplyFilters) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_GLOBAL_PANEL_TITLE),
				configurationControler, true);
		this.todosCorpus = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_CORPUS_ALL_LABEL);
		this.content = new JPanel();
		this.corpusFilterPanel = new ComboBoxPanel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_CORPUS_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_CORPUS_VALUE_LABEL));
		this.manageActionFilterPanel = new ManageFilterPanelWithActions(getConsumerToRepack());
		this.actionPanel = new ActionPanel(1);
		this.actionPanel.addAction(0, getActionFoApplyAllFilters());
		this.consumerForRefreshAfterApplyFilters = consumerForRefreshAfterApplyFilters;
		createWindow();
	}

	@Override
	public void initComponents() {
		createContent();
		refreshActionPanelMessage();
		fillFieldsForFilterText();
		fillCorpusFilterPanel();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.corpusFilterPanel.getJPanel());
		content.add(this.manageActionFilterPanel.getJPanel());
		content.add(this.actionPanel.getJPanel());
	}
	
	/**
	 * Permet de remplir la liste des corpus
	 */
	private void fillCorpusFilterPanel() {
		List<String> valuesList = new LinkedList<>();
		valuesList.add(this.todosCorpus);
		valuesList.addAll(getControler().getAllCorpusNameForFilteredText());
		this.corpusFilterPanel.refresh(valuesList);
	}
	
	/**
	 * Permet de rafraichir les libellés pour action
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_ACTION_APPLY_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_FILTERS_ACTION_PANEL_TITLE), messageButtonMap);
	}
	
	/**
	 * Permet de remplir la combobox pour le choix des champs à filtrer
	 */
	private void fillFieldsForFilterText() {
		Map<String, String> fieldNameLabelMap = new LinkedHashMap<>();
		for (Entry<String, String> entry : getControler().getFieldConfigurationNameLabelMap().entrySet()) {
			StringBuilder sb = new StringBuilder(entry.getKey().replace("[", "").replace("]", ""));
			sb.append(" (");
			sb.append(entry.getValue());
			sb.append(")");
			fieldNameLabelMap.put(entry.getKey(), sb.toString());
		}
		this.manageActionFilterPanel.initMapOfFields(fieldNameLabelMap);
	}
	
	/**
	 * Permet de se procurer un consumer pour repack
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerToRepack() {
		return v -> repack();
	}

	/**
	 * Permet de se procurer l'action pour appliquer les filtres
	 */
	private ActionListener getActionFoApplyAllFilters() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String corpusName = null;
				if (StringUtils.isNotBlank(corpusFilterPanel.getLabelSelected())
						&& !todosCorpus.equals(corpusFilterPanel.getLabelSelected())) {
					corpusName = corpusFilterPanel.getLabelSelected();
				}
				getControler().applyAllFiltersOnCorpusForFolderText(corpusName, manageActionFilterPanel.getAllFiltersList());
				if (null != consumerForRefreshAfterApplyFilters) {
					consumerForRefreshAfterApplyFilters.accept(null);
				}
				closeFrame();
			}
		};
	}
	
	
	@Override
	public JPanel getContent() {
		return this.content;
	}

	@Override
	public String getWindowName() {
		return "Window for filtered manage texts from library";
	}

}
