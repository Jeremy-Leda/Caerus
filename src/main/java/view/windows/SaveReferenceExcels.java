package view.windows;

import controler.IConfigurationControler;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.abstracts.ModalJFrameAbstract;
import view.beans.ExcelTypeGenerationEnum;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.*;
import view.panel.*;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 
 * Ihm pour sauvegarder les fichiers excel de référence
 * 
 * @author jerem
 *
 */
public class SaveReferenceExcels extends ModalJFrameAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2152554664482189441L;
	private static Logger logger = LoggerFactory.getLogger(SaveReferenceExcels.class);
	private final IFilePickerPanel filePickerPanel;
	private final ICheckBoxPanel checkBoxPanel;
	private final List<ICheckBoxTextFieldPanel> checkBoxTextFieldPanelList;
	private final IInformationPanel informationPanel;
	private final IActionPanel actionPanel;
	private final JPanel content;

	public SaveReferenceExcels(IConfigurationControler configurationControler, ExcelTypeGenerationEnum excelGenerationType) {
		super(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_CLASSICAL_PANEL_TITLE), configurationControler);
		String titlePanelAndFielPicker = ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_CLASSICAL_PANEL_TITLE);
		this.filePickerPanel = new FilePickerPanel(titlePanelAndFielPicker, titlePanelAndFielPicker, FilePickerTypeEnum.SAVE_FILE);
		this.filePickerPanel.addConsumerOnChooseFileOk(getConsumerUpdateInformation());
		this.checkBoxPanel = new CheckBoxPanel(3, true);
		this.checkBoxTextFieldPanelList = new ArrayList<ICheckBoxTextFieldPanel>();
		this.informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_CLASSICAL_INFORMATION_PANEL_TITLE),
				ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_CLASSICAL_INFORMATION_PANEL_TEXT_NOTHING), true, true);
		this.actionPanel = new ActionPanel(1);
		this.actionPanel.setEnabled(0, false);
		this.actionPanel.addAction(0, getGenerateExcelAction(excelGenerationType));
		this.content = new JPanel();
		createWindow();
	}

	@Override
	public void initComponents() {
		refreshLabelCheckBox();
		fillCheckBoxTextFieldPanelList();
		refreshActionPanelMessage();
		createContent();
	}

	/**
	 * Permet de créer le contenu
	 */
	private void createContent() {
		BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
		content.setLayout(boxlayout);
		content.add(this.filePickerPanel.getJPanel());
		content.add(this.checkBoxPanel.getJPanel());
		this.checkBoxTextFieldPanelList.forEach(chtf -> content.add(chtf.getJPanel()));
		content.add(this.informationPanel.getJPanel());
		content.add(actionPanel.getJPanel());
	}

	/**
	 * Met à jour le libellé des check box
	 */
	private void refreshLabelCheckBox() {
		Map<Integer, String> labelCheckBoxMap = new HashMap<>();
		labelCheckBoxMap.put(0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_HEADER_LABEL));
		labelCheckBoxMap.put(1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_KEY_LABEL));
		labelCheckBoxMap.put(2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_NUMBER_LABEL));
		this.checkBoxPanel.setStaticLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_OPTIONS_TITLE_PANEL),
				labelCheckBoxMap);
		this.checkBoxPanel.setChecked(0, true);
		this.checkBoxPanel.setChecked(1, true);
		this.checkBoxPanel.setChecked(2, true);
	}

	/**
	 * Permet de remplir la liste des checkBox text field panel
	 */
	private void fillCheckBoxTextFieldPanelList() {
		super.getControler().getConfigurationSpecificLabelNameFileMap().forEach((key, value) -> {
			ICheckBoxTextFieldPanel checkBoxTextFieldPanel = new CheckBoxTextFieldPanel(key,
					ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_SPECIFIC_CHECK_LABEL), value);
			checkBoxTextFieldPanelList.add(checkBoxTextFieldPanel);
			checkBoxTextFieldPanel.addConsumerOnTextFieldChange(getConsumerUpdateInformation());
			checkBoxTextFieldPanel.addConsumerOnCheckedChange(getConsumerUpdateInformation());
		});
	}

	private Consumer<Void> getConsumerUpdateInformation() {
		return (v) -> {
			if (StringUtils.isNotBlank(filePickerPanel.getFile())) {
				this.actionPanel.setEnabled(0, true);
				StringBuilder sb = new StringBuilder();
				sb.append(String.format(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_REFERENCE_FILE_LABEL),
						filePickerPanel.getFile()));
				checkBoxTextFieldPanelList.forEach(checkBoxTextField -> {
					if (checkBoxTextField.getCheckBoxIsChecked()) {
						sb.append("<br/>");
						sb.append(checkBoxTextField.getTitlePanel());
						sb.append(" : ");
						sb.append(getFileName(checkBoxTextField));
					}
				});
				informationPanel.refreshInformations(String.format(
						ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_SAVE_EXCEL_CLASSICAL_INFORMATION_PANEL_TEXT),
						sb));
				repack();
			}
		};
	}

	/**
	 * Permet de se procurer le nom du fichier
	 * @param checkBoxTextField checkboxtextfield dont on souhaite le nom du fichier
	 * @return le nom du fichier
	 */
	private String getFileName(ICheckBoxTextFieldPanel checkBoxTextField) {
		String nameFile = checkBoxTextField.getText();
		String extension = FilenameUtils.getExtension(nameFile);
		if (!"xlsx".equals(extension)) {
			StringBuilder sbNameFile = new StringBuilder(nameFile);
			sbNameFile.append(".xlsx");
			nameFile = sbNameFile.toString();
		}
		String base = FilenameUtils.removeExtension(filePickerPanel.getFile());
		String result = base + nameFile;
		return result;
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

	@Override
	public JPanel getContent() {
		return this.content;
	}

	@Override
	public String getWindowName() {
		return "window save excel reference";
	}
	
	private ActionListener getGenerateExcelAction(ExcelTypeGenerationEnum excelGenerationType) {
		return e -> executeOnServerWithProgressView(() -> {
			if (ExcelTypeGenerationEnum.ANALYZE_TEXTS.equals(excelGenerationType)) {
				getControler().generateExcelFromAnalyze(createExcelCmd());
			} else if (ExcelTypeGenerationEnum.MANAGE_TEXTS.equals(excelGenerationType)) {
				getControler().generateExcelFromTexts(createExcelCmd());
			}
		}, getControler(), getMessage(Constants.WINDOW_LOADING_EXPORT_EXCEL_LABEL), true, false);
	}
	
	/**
	 * Permet de créer la commande pour généré le fichier excel
	 * @return la commande
	 */
	private ExcelGenerateConfigurationCmd createExcelCmd() {
		ExcelGenerateConfigurationCmd cmd = new ExcelGenerateConfigurationCmd();
		cmd.setFileName(this.filePickerPanel.getFile());
		cmd.setHaveToGenerateReferenceText(Boolean.TRUE);
		cmd.setIsSpecificGeneration(null);
		cmd.setLabelSpecificChoose(null);
		cmd.setAddUniqueKey(this.checkBoxPanel.getCheckBoxIsChecked(1));
		cmd.setWithHeader(this.checkBoxPanel.getCheckBoxIsChecked(0));
		cmd.setAddNumber(this.checkBoxPanel.getCheckBoxIsChecked(2));
		this.checkBoxTextFieldPanelList.stream().filter(cb -> cb.getCheckBoxIsChecked()).forEach(checkBoxTextField -> {
			cmd.addLabelSpecificFileName(checkBoxTextField.getTitlePanel(), getFileName(checkBoxTextField));
		});
		return cmd;
	}

}
