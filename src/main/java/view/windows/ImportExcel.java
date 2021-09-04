package view.windows;

import controler.IConfigurationControler;
import model.excel.beans.ExcelImportConfigurationCmd;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static view.utils.Constants.WINDOW_LOADING_IMPORT_EXCEL_LABEL;
import static view.utils.Constants.WINDOW_PROGRESS_BAR_IMPORT_EXCEL_LABEL;

/**
 * 
 * Permet d'importer un fichier excel
 * 
 * @author jerem
 *
 */
public class ImportExcel extends ModalJFrameAbstract {

	private static Logger logger = LoggerFactory.getLogger(ImportExcel.class);
	private final IFilePickerPanel filePickerPanel;
	private final IComboBoxPanel comboBoxPanel;
	private final ICheckBoxPanel checkBoxFieldsPanel;
	private final IInformationPanel informationPanel;
	private final IActionPanel actionPanel;
	private final IActionPanel actionSelectedFieldPanel;
	private final ITextBoxPanel textBoxPanel;
	private final JPanel content;
	private final String defaultLabelNothing = ConfigurationUtils.getInstance()
			.getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_LIST_SPECIFIC_LABEL_NOTHING);
	private final Map<String, Integer> fieldNumberCheckBoxMap;

	public ImportExcel(IConfigurationControler configurationControler) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_PANEL_TITLE), configurationControler);
		this.fieldNumberCheckBoxMap = new HashMap<>();
		String titlePanelAndFielPicker = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_FILE_PICKER_PANEL_TITLE);
		this.filePickerPanel = new FilePickerPanel(titlePanelAndFielPicker, titlePanelAndFielPicker, FilePickerTypeEnum.IMPORT_FILE);
		this.filePickerPanel.addConsumerOnChooseFileOk(getConsumerUpdateInformation());
		this.comboBoxPanel = new ComboBoxPanel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_LIST_SPECIFIC_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_LIST_SPECIFIC_LABEL));
		this.checkBoxFieldsPanel = new CheckBoxPanel(getControler().getFieldConfigurationNameLabelMap().size(), true);
		this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_INFORMATION_PANEL_TEXT_NOTHING), false, true);
		this.actionPanel = new ActionPanel(1);
		this.actionPanel.setEnabled(0, false);
		this.actionPanel.addAction(0, getImportExcelAction());
		this.actionSelectedFieldPanel = new ActionPanel(2);
		this.content = new JPanel();
		this.comboBoxPanel.addConsumerOnSelectChange(getConsumerEnableAndCheckBoxFieldsFromLabelSpecific());
		this.textBoxPanel = new TextBoxPanel(1, false, 12);
		this.textBoxPanel.addConsumerOnChange(0, getConsumerUpdateInformation());
		createWindow();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePickerPanel.getJPanel());
		content.add(this.textBoxPanel.getJPanel());
		content.add(this.comboBoxPanel.getJPanel());
		content.add(this.actionSelectedFieldPanel.getJPanel());
		content.add(this.checkBoxFieldsPanel.getJPanel());
		content.add(this.informationPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}

	/**
	 * Permet de mettre à jour la text box avec les informations
	 */
	private void refreshTextBoxPanel() {
		Map<Integer, String> messageTextMap = new HashMap<Integer, String>();
		messageTextMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_SHEET_NAME_LABEL));
		this.textBoxPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_SHEET_NAME_PANEL_TITLE), messageTextMap);
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
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_LIST_FIELDS_TITLE_PANEL), labels);
	}

	@Override
	public void initComponents() {
		refreshTextBoxPanel();
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
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_PRINCIPAL_ACTION_BUTTON_LABEL));
		this.actionPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_PRINCIPAL_ACTION_TITLE_PANEL),
				messageButtonMap);
	}

	/**
	 * Permet de rafraichir l'affichage
	 */
	private void refreshActionSelectedFieldPanelMessage() {
		Map<Integer, String> messageButtonMap = new HashMap<Integer, String>();
		messageButtonMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_ACTION_DESELECT_ALL));
		messageButtonMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_ACTION_SELECT_ALL));
		this.actionSelectedFieldPanel.setStaticLabel(
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_ACTION_TITLE_PANEL), messageButtonMap);
	}

	/**
	 * Consumer de mise à jour des informations
	 * 
	 * @return
	 */
	private Consumer<Void> getConsumerUpdateInformation() {
		return (v) -> {
			//FIXME adapter l'affichage pour les différents choix
			if (StringUtils.isNotBlank(filePickerPanel.getFile()) && StringUtils.isNotBlank(textBoxPanel.getValueOfTextBox(0))) {
				this.actionPanel.setEnabled(0, true);
				StringBuilder sb = new StringBuilder();
				sb.append(String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_FILE_LABEL),
						filePickerPanel.getFile()));
				informationPanel.refreshInformations(
						String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_IMPORT_EXCEL_INFORMATION_PANEL_TEXT),
								sb.toString()));
				repack();
			} else {
				this.actionPanel.setEnabled(0, false);
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
	 * Consumer permettant d'importer le fichier excel
	 * 
	 * @return consumer
	 */
	private ActionListener getImportExcelAction() {
		return e-> executeOnServerWithProgressView(() -> getControler().importExcel(importExcelCmd()),
				getControler(),
				getMessage(WINDOW_LOADING_IMPORT_EXCEL_LABEL),
				Boolean.TRUE,
				Boolean.TRUE);

	}

	/**
	 * Permet de créer la commande pour importer le fichier excel
	 * 
	 * @return la commande
	 */
	private ExcelImportConfigurationCmd importExcelCmd() {
		ExcelImportConfigurationCmd cmd = new ExcelImportConfigurationCmd(
				new File(this.filePickerPanel.getFile()),
				this.textBoxPanel.getValueOfTextBox(0));
		if (defaultLabelNothing.equals(comboBoxPanel.getLabelSelected())) {
			cmd.setLabelSpecificChoose(null);
		} else {
			cmd.setLabelSpecificChoose(comboBoxPanel.getLabelSelected());
			cmd.setIsSpecificImport(Boolean.TRUE);
		}
		Integer currentIndex = 0;
		for (Entry<String, String> entry : getControler().getFieldConfigurationNameLabelMap().entrySet()) {
			if (this.checkBoxFieldsPanel.getCheckBoxIsChecked(currentIndex)) {
				cmd.getFieldToImportList().add(entry.getKey());
			}
			currentIndex++;
		}
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
