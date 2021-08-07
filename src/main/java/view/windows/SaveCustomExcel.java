package view.windows;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controler.IConfigurationControler;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ExcelTypeGenerationEnum;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
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
 * Permet de sauvegarder un fichier excel personnalisé
 * 
 * @author jerem
 *
 */
public class SaveCustomExcel extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5458991532109751279L;
	private static Logger logger = LoggerFactory.getLogger(SaveCustomExcel.class);
	private final IFilePickerPanel filePickerPanel;
	private final ICheckBoxPanel checkBoxOptionsPanel;
	private final IComboBoxPanel comboBoxPanel;
	private final ICheckBoxPanel checkBoxFieldsPanel;
	private final IInformationPanel informationPanel;
	private final IActionPanel actionPanel;
	private final IActionPanel actionSelectedFieldPanel;
	private final JPanel content;
	private final String defaultLabelNothing = ConfigurationUtils.getInstance()
			.getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_LIST_SPECIFIC_LABEL_NOTHING);
	private final Map<String, Integer> fieldNumberCheckBoxMap;

	public SaveCustomExcel(IConfigurationControler configurationControler, ExcelTypeGenerationEnum excelGenerationType) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_PANEL_TITLE), configurationControler);
		this.fieldNumberCheckBoxMap = new HashMap<>();
		String titlePanelAndFielPicker = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_SPECIFIC_PANEL_TITLE);
		this.filePickerPanel = new FilePickerPanel(titlePanelAndFielPicker, titlePanelAndFielPicker, FilePickerTypeEnum.SAVE_FILE);
		this.filePickerPanel.addConsumerOnChooseFileOk(getConsumerUpdateInformation());
		this.checkBoxOptionsPanel = new CheckBoxPanel(1, false);
		this.comboBoxPanel = new ComboBoxPanel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_LIST_SPECIFIC_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_LIST_SPECIFIC_LABEL));
		this.checkBoxFieldsPanel = new CheckBoxPanel(getControler().getFieldConfigurationNameLabelMap().size(), true);
		this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_INFORMATION_PANEL_TEXT_NOTHING), false, true);
		this.actionPanel = new ActionPanel(1);
		this.actionPanel.setEnabled(0, false);
		this.actionPanel.addAction(0, getGenerateExcelAction(excelGenerationType));
		this.actionSelectedFieldPanel = new ActionPanel(2);
		this.content = new JPanel();
		this.comboBoxPanel.addConsumerOnSelectChange(getConsumerEnableAndCheckBoxFieldsFromLabelSpecific());
		createWindow();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePickerPanel.getJPanel());
		content.add(this.checkBoxOptionsPanel.getJPanel());
		content.add(this.comboBoxPanel.getJPanel());
		content.add(this.actionSelectedFieldPanel.getJPanel());
		content.add(this.checkBoxFieldsPanel.getJPanel());
		content.add(this.informationPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}

	/**
	 * Permet de mettre à jour la combo box avec les informations
	 */
	private void refreshComboBoxPanel() {
		List<String> listLabels = new ArrayList<>();
		listLabels.add(defaultLabelNothing);
		getControler().getConfigurationSpecificLabelNameFileMap().keySet().forEach(label -> listLabels.add(label));
		this.comboBoxPanel.refresh(listLabels);
	}

	/**
	 * Met à jour le libellé des check box
	 */
	private void refreshLabelCheckBoxOptions() {
		Map<Integer, String> labelCheckBoxMap = new HashMap<>();
		labelCheckBoxMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_HEADER_LABEL));
		this.checkBoxOptionsPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_TITLE_PANEL),
				labelCheckBoxMap);
	}

	/**
	 * Met à jour le libellé des check box
	 */
	private void refreshLabelCheckBoxFields() {
		Integer current = 0;
		Map<Integer, String> labels = new HashMap<>();
		for (Entry<String, String> entry : getControler().getFieldConfigurationNameLabelMap().entrySet()) {
			StringBuilder sb = new StringBuilder(entry.getKey().replace("[", "").replace("]", ""));
			sb.append(" (");
			sb.append(entry.getValue());
			sb.append(")");
			labels.put(current, sb.toString());
			this.fieldNumberCheckBoxMap.put(entry.getKey(), current);
			current++;
		}
		this.checkBoxFieldsPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_LIST_FIELDS_TITLE_PANEL), labels);
	}

	@Override
	public void initComponents() {
		refreshLabelCheckBoxOptions();
		refreshActionPanelMessage();
		refreshComboBoxPanel();
		refreshLabelCheckBoxFields();
		refreshActionSelectedFieldPanelMessage();
		setActionSelectedField();
		createContent();
	}

	@Override
	public JPanel getContent() {
		return this.content;
	}

	@Override
	public String getWindowName() {
		return "window save excel custom";
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_ACTION_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_ACTION_TITLE_PANEL),
				messageButtonMap);
		this.actionPanel.setIconButton(0, PictureTypeEnum.SAVE);
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionSelectedFieldPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_ACTION_DESELECT_ALL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_ACTION_SELECT_ALL));
		this.actionSelectedFieldPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_ACTION_TITLE_PANEL), messageButtonMap);
	}

	/**
	 * Consumer de mise à jour des informations
	 * 
	 * @return
	 */
	private Consumer<Void> getConsumerUpdateInformation() {
		return (v) -> {
			if (StringUtils.isNotBlank(filePickerPanel.getFile())) {
				this.actionPanel.setEnabled(0, true);
				StringBuilder sb = new StringBuilder();
				sb.append(String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_FILE_LABEL),
						filePickerPanel.getFile()));
				informationPanel.refreshInformations(
						String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_INFORMATION_PANEL_TEXT),
								sb.toString()));
				repack();
			}
		};
	}

	/**
	 * Permet de mettre à jour les actions pour les boutons de masses
	 */
	private void setActionSelectedField() {
		this.actionSelectedFieldPanel.addAction(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setAllCheckBoxFields(false);
			}
		});
		this.actionSelectedFieldPanel.addAction(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setAllCheckBoxFields(true);
			}
		});
	}

	/**
	 * Consumer permettant de générer le fichier excel
	 * 
	 * @return consumer
	 */
	private ActionListener getGenerateExcelAction(ExcelTypeGenerationEnum excelGenerationType) {
		return e -> {
			new ProgressBarView(r -> {
				try {
					if (ExcelTypeGenerationEnum.ANALYZE_TEXTS.equals(excelGenerationType)) {
						getControler().generateExcelFromAnalyze(createExcelCmd());
					} else if (ExcelTypeGenerationEnum.MANAGE_TEXTS.equals(excelGenerationType)) {
						getControler().generateExcelFromTexts(createExcelCmd());
					}
					closeFrame();
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}, getProgressConsumer(100, getControler()), 100);
			getControler().resetProgress();
		};
	}

	/**
	 * Permet de créer la commande pour généré le fichier excel
	 * 
	 * @return la commande
	 */
	private ExcelGenerateConfigurationCmd createExcelCmd() {
		ExcelGenerateConfigurationCmd cmd = new ExcelGenerateConfigurationCmd();
		cmd.setFileName(this.filePickerPanel.getFile());
		cmd.setHaveToGenerateReferenceText(Boolean.FALSE);
		cmd.setAddUniqueKey(Boolean.TRUE);
		cmd.setIsSpecificGeneration(!defaultLabelNothing.equals(comboBoxPanel.getLabelSelected()));
		if (defaultLabelNothing.equals(comboBoxPanel.getLabelSelected())) {
			cmd.setLabelSpecificChoose(null);
		} else {
			cmd.setLabelSpecificChoose(comboBoxPanel.getLabelSelected());
		}
		Integer currentIndex = 0;
		for (Entry<String, String> entry : getControler().getFieldConfigurationNameLabelMap().entrySet()) {
			if (this.checkBoxFieldsPanel.getCheckBoxIsChecked(currentIndex)) {
				cmd.addFieldToGenerate(entry.getKey());
			}
			currentIndex++;
		}
		cmd.setWithHeader(this.checkBoxOptionsPanel.getCheckBoxIsChecked(0));
		return cmd;
	}

	/**
	 * Permet de mettre à jour l'état des cases à cocher en masse
	 * 
	 * @param checked etat
	 */
	private void setAllCheckBoxFields(Boolean checked) {
		for (int i = 0; i < getControler().getFieldConfigurationNameLabelMap().size(); i++) {
			if (this.checkBoxFieldsPanel.getCheckBoxIsEnabled(i)) {
				this.checkBoxFieldsPanel.setChecked(i, checked);
			}
		}
	}
	
	/**
	 * Permet de se procurer le consumer pour la mise à jour
	 * @return le consumer
	 */
	private Consumer<Void> getConsumerEnableAndCheckBoxFieldsFromLabelSpecific() {
		return (v) -> {
			setEnableAndCheckBoxFieldsFromLabelSpecific(this.comboBoxPanel.getLabelSelected());
		};
	}
	
	/**
	 * Permet de mettre à jour les case à cocher en fonction du traitement
	 * @param label label du traitement
	 */
	private void setEnableAndCheckBoxFieldsFromLabelSpecific(String label) {
		if (defaultLabelNothing.equals(label)) {
			for (int i = 0; i < getControler().getFieldConfigurationNameLabelMap().size(); i++) {
				this.checkBoxFieldsPanel.setEnabled(i, true);
			}
		} else {		
			getControler().getFieldListToProcess(label).stream().forEach(field -> {
				this.checkBoxFieldsPanel.setChecked(fieldNumberCheckBoxMap.get(field), true);
				this.checkBoxFieldsPanel.setEnabled(fieldNumberCheckBoxMap.get(field), false);
			});
			getControler().getFieldListForbiddenToDisplay(label).stream().forEach(field -> {
				this.checkBoxFieldsPanel.setChecked(fieldNumberCheckBoxMap.get(field), false);
				this.checkBoxFieldsPanel.setEnabled(fieldNumberCheckBoxMap.get(field), false);
			});
		}
	}
}
