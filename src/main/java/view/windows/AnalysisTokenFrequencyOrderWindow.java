package view.windows;

import controler.IConfigurationControler;
import model.excel.beans.*;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.analysis.beans.AnalysisFrequencyOrder;
import view.analysis.beans.AnalysisGroupDisplay;
import view.analysis.beans.AnalysisResultDisplay;
import view.analysis.beans.AnalysisTokenDisplay;
import view.analysis.beans.interfaces.IExcelSheet;
import view.components.DragAndDropCloseableTabbedPane;
import view.interfaces.IActionPanel;
import view.interfaces.ILabelsPanel;
import view.panel.ActionPanel;
import view.panel.LabelsPanel;
import view.panel.analysis.TableAnalysisPanel;
import view.utils.ConfigurationUtils;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

public class AnalysisTokenFrequencyOrderWindow extends ModalJFrameAbstract {

    private final DragAndDropCloseableTabbedPane tabbedPane = new DragAndDropCloseableTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

    private final JPanel content = new JPanel();

    private final IActionPanel actionPanel = new ActionPanel(1);

    private final LinkedList<String> headerLinkedList;

    private final LinkedList<String> excludeHeaderLinkedList;

    private final Set<AnalysisResultDisplay> analysisResultDisplaySet;

    private final Set<AnalysisGroupDisplay> analysisGroupDisplaySet;

    private final IConfigurationControler controler;

    private final ILabelsPanel labelsPanel;

    public AnalysisTokenFrequencyOrderWindow(IConfigurationControler controler,
                                             Set<AnalysisResultDisplay> analysisResultDisplaySet,
                                             Collection<AnalysisGroupDisplay> analysisGroupDisplaySet) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_PANEL_TITLE), controler, false);
        this.controler = controler;
        this.analysisResultDisplaySet = analysisResultDisplaySet;
        this.analysisGroupDisplaySet = new HashSet<>(analysisGroupDisplaySet);
        this.headerLinkedList = new LinkedList<>();
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_ORDER_COLUMN_LABEL));
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_WORD_COLUMN_LABEL));
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_FREQUENCY_COLUMN_LABEL));
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_ORDER_COLUMN_LABEL));
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_WORD_COLUMN_LABEL));
        this.headerLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_FREQUENCY_COLUMN_LABEL));
        this.excludeHeaderLinkedList = new LinkedList<>();
        this.excludeHeaderLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_ORDER_COLUMN_LABEL));
        this.excludeHeaderLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_WORD_COLUMN_LABEL));
        this.excludeHeaderLinkedList.add(getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_FREQUENCY_COLUMN_LABEL));
        this.labelsPanel = new LabelsPanel(getMessage(WINDOW_RESULT_TOKEN_TOTAL_PANEL_TITLE), 2);
        this.tabbedPane.addChangeListener(s -> repack());
        initActionPanel();
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        constructTab();
        content.add(this.tabbedPane);
        content.add(this.actionPanel.getJPanel());
        this.tabbedPane.addChangeListener(s -> repack());
    }

    /**
     * Permet d'initialiser les boutons
     */
    private void initActionPanel() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_RESULT_TOKEN_ACTION_PANEL_TITLE),
                Map.of(0, getMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_ACTION_EXPORT_LABEL)));
        this.actionPanel.addAction(0, e -> {
            List<IExcelSheet> sheetsList = new LinkedList<>(constructExport());
            List<IExcelSheet> groupList = analysisGroupDisplaySet.stream().map(this::constructExport).flatMap(x -> x.stream()).collect(Collectors.toList());
            sheetsList.addAll(groupList);
            new ExportExcelWindow(getControler(), sheetsList);
        });
    }

    private List<IExcelSheet> constructExport() {
        List<AnalysisFrequencyOrder> orphan = analysisResultDisplaySet.stream()
                .filter(x -> !x.getExcludeTexts())
                .flatMap(x -> x.getAnalysisTokenDisplaySet().stream())
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isEmpty())
                .collect(Collectors.toList());
        List<AnalysisFrequencyOrder> general = analysisResultDisplaySet.stream()
                .filter(x -> !x.getExcludeTexts())
                .flatMap(x -> x.getAnalysisTokenDisplaySet().stream())
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isPresent())
                .collect(Collectors.toList());
        return List.of(new FrequencyOrderExportExcel(general, false), new FrequencyOrderExportExcel(orphan, true));
    }

    private List<IExcelSheet> constructExport(AnalysisGroupDisplay analysisGroupDisplay) {
        List<AnalysisFrequencyOrder> orphan = analysisGroupDisplay
                .getAnalysisResultDisplay()
                .getAnalysisTokenDisplaySet().stream()
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isEmpty())
                .collect(Collectors.toList());
        List<AnalysisFrequencyOrder> general = analysisGroupDisplay
                .getAnalysisResultDisplay()
                .getAnalysisTokenDisplaySet().stream()
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isPresent())
                .collect(Collectors.toList());
        return List.of(new FrequencyOrderExportExcel(general, false, analysisGroupDisplay.getTitle() + " - "), new FrequencyOrderExportExcel(orphan, true, analysisGroupDisplay.getTitle() + " - "));
    }

    private void constructTab() {
        Set<AnalysisFrequencyOrder> excludeTexts = getDefaultAnalysisResultDisplayIfEmpty(analysisResultDisplaySet, true);
        Set<AnalysisFrequencyOrder> globalTexts = getDefaultAnalysisResultDisplayIfEmpty(analysisResultDisplaySet, false);
        String globalTitle = getMessage(WINDOW_RESULT_TOKEN_GLOBAL_LABEL);
        String excludeTitle = getMessage(WINDOW_RESULT_TOKEN_EXCLUDE_LABEL);
        JPanel globalContent = createContentTab(globalTexts, headerLinkedList, List.of(Integer.class, String.class, Integer.class, Integer.class, String.class, Integer.class));
        JPanel excludeContent = createContentTab(excludeTexts, excludeHeaderLinkedList, List.of(Integer.class, String.class, Integer.class));
        this.tabbedPane.addTab(globalTitle, globalContent);
        this.tabbedPane.addTab(excludeTitle, excludeContent);
        analysisGroupDisplaySet.forEach(this::addGroupDisplayTab);
    }

    private void addGroupDisplayTab(AnalysisGroupDisplay analysisGroupDisplay) {
        Set<AnalysisFrequencyOrder> excludeTexts = getDefaultAnalysisGroupDisplayIfEmpty(analysisGroupDisplay.getAnalysisResultDisplay(), true);
        Set<AnalysisFrequencyOrder> globalTexts = getDefaultAnalysisGroupDisplayIfEmpty(analysisGroupDisplay.getAnalysisResultDisplay(), false);
        String globalTitle = analysisGroupDisplay.getTitle() + " " + getMessage(WINDOW_RESULT_TOKEN_GLOBAL_LABEL);
        String excludeTitle = analysisGroupDisplay.getTitle() + " " + getMessage(WINDOW_RESULT_TOKEN_EXCLUDE_LABEL);
        JPanel globalContent = createContentTab(globalTexts, headerLinkedList, List.of(Integer.class, String.class, Integer.class, Integer.class, String.class, Integer.class));
        JPanel excludeContent = createContentTab(excludeTexts, excludeHeaderLinkedList, List.of(Integer.class, String.class, Integer.class));
        this.tabbedPane.addTab(globalTitle, globalContent);
        this.tabbedPane.addTab(excludeTitle, excludeContent);
    }


    private Set<AnalysisFrequencyOrder> getDefaultAnalysisGroupDisplayIfEmpty(AnalysisResultDisplay analysisResultDisplay, boolean withExcludeTexts) {
        return analysisResultDisplay.getAnalysisTokenDisplaySet().stream()
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isEmpty() == withExcludeTexts)
                .collect(Collectors.toSet());
    }


    private Set<AnalysisFrequencyOrder> getDefaultAnalysisResultDisplayIfEmpty(Set<AnalysisResultDisplay> analysisResultDisplaySet, boolean withExcludeTexts) {
        return analysisResultDisplaySet.stream()
                .filter(x -> !x.getExcludeTexts())
                .flatMap(x -> x.getAnalysisTokenDisplaySet().stream())
                .map(AnalysisTokenDisplay::getAnalysisFrequencyOrder)
                .filter(analysisFrequencyOrder -> analysisFrequencyOrder.getOptionalFrequencyOrderRepository().isEmpty() == withExcludeTexts)
                .collect(Collectors.toSet());
    }

    private JPanel createContentTab(Set<AnalysisFrequencyOrder> analysisResultDisplay, LinkedList<String> headerLinkedList, List<Class<?>> classList) {
        JPanel content = new JPanel();
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        TableAnalysisPanel tableAnalysisPanel = new TableAnalysisPanel(getMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_PANEL_TITLE),
                headerLinkedList, classList, analysisResultDisplay.stream().map(AnalysisFrequencyOrder::toTokenRow).collect(Collectors.toList()));
        content.add(tableAnalysisPanel.getJPanel());
        return content;
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Window for token frequency order";
    }



    private class FrequencyOrderExportExcel implements IExcelSheet {

        private final List<AnalysisFrequencyOrder> frequencyOrderList;

        private final boolean orphan;

        private final String titlePrefix;

        private FrequencyOrderExportExcel(List<AnalysisFrequencyOrder> frequencyOrderList, boolean orphan) {
            this(frequencyOrderList, orphan, StringUtils.EMPTY);
        }

        private FrequencyOrderExportExcel(List<AnalysisFrequencyOrder> frequencyOrderList, boolean orphan, String titlePrefix) {
            this.frequencyOrderList = frequencyOrderList;
            this.orphan = orphan;
            this.titlePrefix = titlePrefix;
        }

        private ExcelBlock toExcelBlock() {
            ExcelCell cellFrequencyOrderLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_ORDER_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelCell cellWordLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_WORD_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelCell cellFrequencyLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_TEXT_FREQUENCY_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelCell cellFrequencyOrderRepoLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_ORDER_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelCell cellWordRepoLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_WORD_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelCell cellFrequencyRepoLabel = new ExcelStringCellBuilder()
                    .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_REPO_FREQUENCY_COLUMN_LABEL))
                    .header(true)
                    .build();
            ExcelBlock excelBlockValues = new ExcelBlock(new ExcelLine(cellFrequencyOrderLabel, cellWordLabel, cellFrequencyLabel, cellFrequencyOrderRepoLabel, cellWordRepoLabel, cellFrequencyRepoLabel));
            if (orphan)  {
                excelBlockValues = new ExcelBlock(new ExcelLine(cellFrequencyOrderLabel, cellWordLabel, cellFrequencyLabel));
            }
            excelBlockValues.getExcelLineLinkedList().addAll(frequencyOrderList.stream().map(AnalysisFrequencyOrder::toExcelLine).collect(Collectors.toList()));
            return excelBlockValues;
        }

        @Override
        public ExcelSheet getExcelSheet() {
            int nbColumnMax = 6;
            String title = titlePrefix + ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_EXPORT_GENERAL_LABEL);
            if (orphan) {
                nbColumnMax = 3;
                title = titlePrefix + ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_FREQUENCY_ORDER_EXPORT_ORPHELAN_LABEL);
            }
            // Creation de la feuille
            return new ExcelSheetBuilder()
                    .name(title)
                    .excelBlockList(List.of(toExcelBlock()))
                    .nbColumnMax(nbColumnMax)
                    .build();
        }
    }

}
