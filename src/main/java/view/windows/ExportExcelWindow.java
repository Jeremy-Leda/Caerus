package view.windows;

import controler.IConfigurationControler;
import model.analyze.beans.Progress;
import model.excel.CreateExcel;
import model.excel.beans.ExcelSheet;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisGroupDisplay;
import view.analysis.beans.AnalysisResultDisplay;
import view.beans.FilePickerTypeEnum;
import view.beans.PictureTypeEnum;
import view.interfaces.IActionPanel;
import view.interfaces.IFilePickerPanel;
import view.interfaces.IInformationPanel;
import view.panel.ActionPanel;
import view.panel.FilePickerPanel;
import view.panel.InformationPanel;
import view.utils.ConfigurationUtils;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            getMessage(WINDOW_EXPORT_EXCEL_INFORMATION_LABEL),
            getMessage(WINDOW_EXPORT_EXCEL_INFORMATION_MESSAGE),
            false,
            false);
    private final IActionPanel actionPanel = new ActionPanel(1);
    private final List<AnalysisGroupDisplay> analysisGroupDisplayList;
    private final AnalysisResultDisplay analysisResultDisplay;

    public ExportExcelWindow(IConfigurationControler configurationControler, AnalysisResultDisplay analysisResultDisplay, List<AnalysisGroupDisplay> analysisGroupDisplayList) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_EXPORT_EXCEL_PANEL_TITLE), configurationControler);
        this.analysisGroupDisplayList = analysisGroupDisplayList;
        this.analysisResultDisplay = analysisResultDisplay;
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        initActionPanel();
        content.add(informationPanel.getJPanel());
        content.add(filePickerPanel.getJPanel());
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

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Export excel window";
    }

    private void createExcel() throws IOException {
        executeOnServer(() -> {
            Progress progress = new Progress(1);
            List<ExcelSheet> excelSheetList = new LinkedList<>();
            analysisGroupDisplayList.forEach(a -> {
                if (a == null) {
                    excelSheetList.add(analysisResultDisplay.toExcelSheet());
                } else {
                    excelSheetList.add(a.toExcelSheet());
                }
            });
            CreateExcel createExcel = new CreateExcel(new File(filePickerPanel.getFile()));
            createExcel.generateExcel(excelSheetList, progress);
            closeFrame();
        }, true);

    }
}
