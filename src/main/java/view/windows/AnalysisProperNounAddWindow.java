package view.windows;

import controler.IConfigurationControler;
import model.analyze.constants.ActionEditTableEnum;
import model.analyze.lexicometric.beans.FillTableConfiguration;
import model.analyze.lexicometric.beans.LexicometricConfigurationEnum;
import model.analyze.lexicometric.interfaces.ILexicometricConfiguration;
import org.apache.commons.lang3.StringUtils;
import view.abstracts.ModalJFrameAbstract;
import view.beans.*;
import view.interfaces.*;
import view.panel.*;
import view.panel.model.EditTableModel;
import view.panel.model.SelectedTableModel;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import javax.swing.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 *
 * Fenêtre permettant de modifier les noms propres à partir des analyses
 *
 */
public class AnalysisProperNounAddWindow extends ModalJFrameAbstract {

    private final IAccessPanel textInformationPanel = new InformationPanel(PictureTypeEnum.INFORMATION,
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_INFORMATION_MESSAGE_PANEL_LABEL),
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_INFORMATION_MESSAGE),
            true, false);
    private final JPanel content = new JPanel();
//    private final IComboBoxPanel comboBoxPanel = new ComboBoxPanel(
//            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PROFIL_PANEL),
//            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PROFIL_LABEL));
    private final JPanel tablePanel = new JPanel();
    private final ITableWithFilterAndEditPanel<String> properNounTable = new TableWithFilterAndEditPanel<>(
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PROPER_NOUN_TABLE_PANEL_TITLE),
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PROPER_NOUN_TABLE_HEADER_LABEL),
            null,
            Comparator.naturalOrder(),
            Comparator.comparing(StringUtils::stripAccents),
            s -> StringUtils.isNotBlank(s.getStringValue()),
            (s, f) -> s.toLowerCase(Locale.ROOT).contains(f.getStringValue()),
            getTableTextFilterPanel(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PROPER_NOUN_TABLE_FILTER_LABEL)),
            true,
            Optional.empty());
    private final ISelectedTablePanel<String> wordTablePanel = new SelectedTablePanel<>(
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_WORD_TABLE_PANEL_TITLE),
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_WORD_TABLE_HEADER_LABEL),
            String.class,
            (col, filter) -> col.stream().filter(s -> s.getData().contains(filter)).collect(Collectors.toCollection(LinkedList::new)),
            ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_WORD_TABLE_FILTER_LABEL));
    private final IActionPanel actionPanel = new ActionPanel(3);
    private final Consumer<?> consumerRelaunchAnalyze;
    private final String selectedProfil;

    public AnalysisProperNounAddWindow(IConfigurationControler configurationControler, Collection<String> properNounSet, Consumer<?> consumerRelaunchAnalyze,
                                       String selectedProfil) {
        super(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_PANEL_TITLE), configurationControler, false);
        this.consumerRelaunchAnalyze = consumerRelaunchAnalyze;
        this.selectedProfil = selectedProfil;
        //this.comboBoxPanel.addConsumerOnSelectChange(getConsumerOnProfileChange());
        reloadProperNounList();
        wordTablePanel.refreshData(properNounSet.stream().map(s -> new SelectedObjectTable<>(s)).collect(Collectors.toCollection(LinkedList::new)));
        configureActionPanel();
        createWindow();
    }

    @Override
    public void initComponents() {
        BoxLayout boxlayout = new BoxLayout(content, BoxLayout.Y_AXIS);
        content.setLayout(boxlayout);
        content.add(this.textInformationPanel.getJPanel());
        this.tablePanel.add(wordTablePanel.getJPanel());
        this.tablePanel.add(properNounTable.getJPanel());
        content.add(this.tablePanel);
        content.add(this.actionPanel.getJPanel());
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public String getWindowName() {
        return "Analysis proper noun add window";
    }

    public void setProperNounSetOfText(Collection<String> properNounSet) {
        wordTablePanel.refreshData(properNounSet.stream().map(s -> new SelectedObjectTable<>(s)).collect(Collectors.toCollection(LinkedList::new)));
    }

    /**
     * Consumer pour le changement de la liste en fonction du profile
     * @return le consumer
     */
    private Consumer<String> getConsumerOnProfileChange() {
        return e -> reloadProperNounList();
    }
//
//    /**
//     * Permet de charger les profiles
//     */
//    private void loadAllProfils() {
//        Set<String> profilSet = LexicometricConfigurationEnum.PROPER_NOUN.getAllProfils().apply(null);
//        this.comboBoxPanel.refresh(profilSet);
//        profilSet.stream().findFirst().ifPresent(s -> this.comboBoxPanel.selectItem(s));
//    }

    /**
     * Permet de se procurer le table filter pour filtrer par rapport à une chaine de caractère
     * @param label Label du filtre
     * @return le table filter pour filtrer par rapport à une chaine de caractère
     */
    protected ITableFilterPanel getTableTextFilterPanel(String label) {
        TableFilterTextPanel tableFilterPanel = new TableFilterTextPanel();
        tableFilterPanel.setStaticLabel(StringUtils.EMPTY, Map.of(0, label));
        return tableFilterPanel;
    }

    /**
     * Permet de configurer le panel des actions
     */
    private void configureActionPanel() {
        this.actionPanel.setStaticLabel(getMessage(WINDOW_INFORMATION_ACTION_PANEL_LABEL),
                Map.of(0, getMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_SAVE_LIST_BUTTON_LABEL),
                        1, getMessage(WINDOW_ANALYSIS_PROPER_NOUN_ADD_RELAUNCH_ANALYZE_BUTTON_LABEL),
                        2, getMessage(WINDOW_INFORMATION_ACTION_BUTTON_LABEL)));
        this.actionPanel.setIconButton(0, PictureTypeEnum.SAVE);
        this.actionPanel.addAction(0, w -> saveAllCheckedProperNoun());
        this.actionPanel.addAction(1, w -> {
            consumerRelaunchAnalyze.accept(null);
            closeFrame();
        });
        this.actionPanel.addAction(2, w -> closeFrame());
    }

    /**
     * Permet de sauvegarder les noms propres sélectionné
     */
    private void saveAllCheckedProperNoun() {
        executeOnServer(() -> {
            wordTablePanel.getRowsByState(true).forEach(x -> {
                EditTableElement<String> editTableElement = new EditTableElementBuilder<String>()
                        .value(x.getData())
                        .actionEditTableEnum(ActionEditTableEnum.ADD)
                        .build();
                LexicometricConfigurationEnum.PROPER_NOUN.getEditTableElementBiConsumer().accept(selectedProfil, editTableElement);
            });
            LexicometricConfigurationEnum.PROPER_NOUN.getSaveInDiskConsumer().accept(selectedProfil);
            reloadProperNounList();
        }, true);
    }

    /**
     * Permet de recharger la liste des noms propres
     */
    private void reloadProperNounList() {
        ILexicometricConfiguration<String> lexicometricConfiguration = getControler().
                getLexicometricConfiguration(LexicometricEditEnum.PROPER_NOUN, ProperNounHierarchicalEditEnum.BASE);
        lexicometricConfiguration.getFillTableConfigurationList().stream()
                .filter(s -> s.getDest().equals(0))
                .findFirst()
                .ifPresent(x -> properNounTable.fillTable(x.getBiFunction().apply(selectedProfil, null)));
    }

}
