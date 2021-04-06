package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import model.analyze.beans.FilesToAnalyzeInformation;
import model.exceptions.LoadTextException;
import utils.RessourcesUtils;
import view.abstracts.ModalJFrameAbstract;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IAnalyzeConfiguration;
import view.interfaces.ICheckBoxPanel;
import view.interfaces.IComboBoxPanel;
import view.interfaces.IFilePickerPanel;
import view.interfaces.IInformationPanel;
import view.panel.ActionPanel;
import view.panel.CheckBoxPanel;
import view.panel.ComboBoxPanel;
import view.panel.FilePickerPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Classe permettant de concevoir l'interface pour le chargement des textes =>
 * Interface permettant de choisir les configurations de lecture du texte
 * 
 * @author jerem
 *
 */
public class LoadTextConfigurationSelector extends ModalJFrameAbstract implements IAnalyzeConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3348054080637849772L;
	private static Logger logger = LoggerFactory.getLogger(LoadTextConfigurationSelector.class);

	private final JPanel content;
	private final IComboBoxPanel comboBoxPanel;
	private final IFilePickerPanel filePickerPanel;
	private final IActionPanel actionPanel;
	private final IInformationPanel informationsFilesPanel;
	private final IInformationPanel warningFilesPanel;
	private final ICheckBoxPanel checkBoxPanel;

	/**
	 * Constructeur
	 */
	public LoadTextConfigurationSelector(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TITLE),
				configurationControler);
		getControler().clearAnalyze();
		this.content = new JPanel();
		this.comboBoxPanel = new ComboBoxPanel(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TYPE_CONFIGURATION_PANEL_TITLE),
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_TYPE_CONFIGURATION_LABEL));
		this.filePickerPanel = new FilePickerPanel(
				ConfigurationUtils.getInstance()
						.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(
						Constants.WINDOW_LOAD_TEXT_CONFIGURATION_FOLDER_LABEL),
				FilePickerTypeEnum.OPEN_FOLDER);
		this.actionPanel = new ActionPanel(1);
		this.informationsFilesPanel = new InformationPanel(PictureTypeEnum.INFORMATION, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_INFORMATIONS_PANEL_TITLE), ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_INFORMATIONS_MESSAGE_DEFAULT), true, true);
		this.warningFilesPanel = new InformationPanel(PictureTypeEnum.WARNING, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_WARNING_PANEL_TITLE), ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_WARNING_MESSAGE), false, false);
		this.warningFilesPanel.getJPanel().setVisible(false);
		this.checkBoxPanel = new CheckBoxPanel(1, false);
		createWindow();
	}

	@Override
	public void initComponents() {
		fillConfigurationDisplayList();
		initActionPanel();
		initCheckBoxPanel();
		this.filePickerPanel.addConsumerOnChooseFileOk(getConsumerAfterSelectFolder());
		createContent();
	}
	
	@Override
	public JPanel getContent() {
		return this.content;
	}
	
	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.comboBoxPanel.getJPanel());
		content.add(this.warningFilesPanel.getJPanel());
		content.add(this.checkBoxPanel.getJPanel());
		content.add(this.filePickerPanel.getJPanel());
		content.add(this.informationsFilesPanel.getJPanel());
		content.add(this.actionPanel.getJPanel());
	}
	
	/**
	 * Permet d'initialiser le panel de la checkbox
	 */
	private void initCheckBoxPanel() {
		Map<Integer, String> labelMap = new HashMap<>();
		labelMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_CHOOSE_SEARCH_LABEL));
		this.checkBoxPanel.setStaticLabel(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_CHOOSE_SEARCH_PANEL_TITLE), labelMap);
		this.checkBoxPanel.addConsumerOnChange(0, getConsumerAfterSelectFolder());
	}

	/**
	 * Permet d'initialiser le panel des actions
	 */
	private void initActionPanel() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXT_CONFIGURATION_START_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_START_PANEL_TITLE), messageButtonMap);
		this.actionPanel.setEnabled(0, false);
		this.actionPanel.addAction(0, startAnalyse());
	}
	
	/**
	 * Permet de remplir la liste pour la configuration
	 */
	private void fillConfigurationDisplayList() {
		comboBoxPanel.refresh(getControler().getConfigurationNameList());
		comboBoxPanel.selectItem(getControler().getConfigurationName());
	}

	
	/**
	 * Permet de se procurer le consumer pour activer le bouton après la selection
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerAfterSelectFolder() {
		return (v) -> {
			if (StringUtils.isNotBlank(filePickerPanel.getFile())) {
				try {
					FilesToAnalyzeInformation nameFileToAnalyzeList = getControler().getNameFileToAnalyzeList(new File(filePickerPanel.getFile()), this.checkBoxPanel.getCheckBoxIsChecked(0));
					if (null != nameFileToAnalyzeList) {
						actionPanel.setEnabled(0, nameFileToAnalyzeList.getLaunchAnalyzeIsOk());
						warningFilesPanel.getJPanel().setVisible(!nameFileToAnalyzeList.getLaunchAnalyzeIsOk());
					}
					refreshMessageInformations(nameFileToAnalyzeList.getNameFileList());
				} catch (Exception e) {
					warningFilesPanel.getJPanel().setVisible(true);
					actionPanel.setEnabled(0, false);
					logger.error(e.getMessage(), e);
				}
				repack();
			}
		};
	}
	
	/**
	 * Permet de rafraichir le message d'informations
	 * @param nameFileList liste des fichiers à analyser
	 */
	private void refreshMessageInformations(List<String> nameFileList) {
		List<String> nameFileHtmlList = nameFileList.stream().map(fileName -> "<li>" + fileName + "</li>").collect(Collectors.toList());
		StringBuilder sb = new StringBuilder();
		sb.append("<ul>");
		sb.append(StringUtils.join(nameFileHtmlList.toArray()));
		sb.append("</ul>");
		this.informationsFilesPanel.refreshInformations(String.format(ConfigurationUtils.getInstance()
				.getDisplayMessage(Constants.WINDOW_LOAD_TEXTS_INFORMATIONS_MESSAGE), sb.toString()));
	}

	/**
	 * Permet de lancer l'analyse
	 * @return
	 */
	private ActionListener startAnalyse() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBoxPanel.getItemCount() == 0) {
					try {
						getControler().setCurrentConfiguration(RessourcesUtils.getInstance().getBasicalConfiguration());
					} catch (Exception e1) {
						logger.error(e1.getMessage(), e1);
					}
				} else {
					getControler().setCurrentConfiguration(comboBoxPanel.getLabelSelected().toString());
				}
				getControler().setAnalyzeFolder(new File(filePickerPanel.getFile()));
				try {
					getControler().launchAnalyze(checkBoxPanel.getCheckBoxIsChecked(0));
				} catch (LoadTextException e1) {
					logger.error(e1.getMessage(), e1);
				}
				closeFrame();
			}
		};
	}


	@Override
	public String getWindowName() {
		return "Load Text Configuration Selector";
	}

	@Override
	public Boolean getWithSubFolderAnalyze() {
		return this.checkBoxPanel.getCheckBoxIsChecked(0);
	}

}
