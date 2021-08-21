package view.windows;

import controler.IConfigurationControler;
import model.analyze.beans.Progress;
import model.excel.CreateExcel;
import model.excel.beans.ExcelSheet;
import model.interfaces.ICreateExcel;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.interfaces.IExcelSheet;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.ICheckBoxPanel;
import view.interfaces.IFilePickerPanel;
import view.interfaces.IInformationPanel;
import view.panel.ActionPanel;
import view.panel.CheckBoxPanel;
import view.panel.FilePickerPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Window pour effectuer un export excel
 *
 */
public class ExportExcelWindow extends ModalJFrameAbstract {

    private final JPanel content = new JPanel();
    private final IFilePickerPanel filePickerPanel = new FilePickerPanel(getMessage(WINDOW_EXPORT_EXCEL_FILE_PICKER_PANEL_LABEL),
            getMessage(WINDOW_EXPORT_EXCEL_FILE_PICKER_PANEL_LABEL), FilePickerTypeEnum.SAVE_FILE);
    private final IInformationPanel informationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
            getMessage(WINDOW_INFORMATION_PANEL_LABEL),
            getMessage(WINDOW_EXPORT_EXCEL_INFORMATION_MESSAGE),
            false,
            false);
    private final ICheckBoxPanel checkBoxPanel = new CheckBoxPanel(1, false);
    private final IActionPanel actionPanel = new ActionPanel(1);
    private final List<IExcelSheet> iExcelSheetList;

    public ExportExcelWindow(IConfigurationControler configurationControler, List<IExcelSheet> iExcelSheetList) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_EXPORT_EXCEL_PANEL_TITLE), configurationControler);
        this.iExcelSheetList = iExcelSheetList;
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        initActionPanel();
        initCheckBox();
        content.add(informationPanel.getJPanel());
        content.add(filePickerPanel.getJPanel());
        content.add(checkBoxPanel.getJPanel());
        content.add(actionPanel.getJPanel());
    }

    private void initActionPanel() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL), Map.of(0, getMessage(WINDOW_EXPORT_EXCEL_BUTTON_LABEL)));
        this.actionPanel.addAction(0, e -> {
            try {
                createExcel();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    /**
     * Permet d'initialiser les checkbox
     */
    private void initCheckBox() {
        this.checkBoxPanel.setStaticLabel(getMessage(WINDOW_EXPORT_EXCEL_PREFERENCE_PANEL_TITLE), Map.of(0, getMessage(WINDOW_EXPORT_EXCEL_PREFERENCE_WITH_FORMAT_LABEL)));
        this.checkBoxPanel.setChecked(0, true);
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Export excel window";
    }

    private void createExcel() throws IOException {
        ICreateExcel createExcel = new CreateExcel(new File(filePickerPanel.getFile()));
        executeOnServerWithProgressView(() -> {
            List<ExcelSheet> excelSheetList = this.iExcelSheetList.stream().map(IExcelSheet::getExcelSheet).collect(Collectors.toList());
            createExcel.generateExcel(excelSheetList, this.checkBoxPanel.getCheckBoxIsChecked(0));
        }, createExcel, true, false);

    }
}
