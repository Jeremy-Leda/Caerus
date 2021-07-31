package view.analysis.beans;

import model.PojoBuilder;
import model.excel.beans.*;
import view.panel.analysis.model.AnalysisRow;
import view.utils.ConfigurationUtils;

import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static view.utils.Constants.*;

/**
 * Bean pour l'affichage des résultats
 */
@PojoBuilder
public class AnalysisResultDisplay {

    private Set<AnalysisTokenDisplay> analysisTokenDisplaySet;
    @NotNull
    private String key;
    private Integer NbToken;
    private Long NbOccurrency;

    /**
     * Permet de se procurer la liste des tokens à afficher
     * @return la liste des tokens à afficher
     */
    public Set<AnalysisTokenDisplay> getAnalysisTokenDisplaySet() {
        return analysisTokenDisplaySet;
    }

    /**
     * Permet de définir la liste des tokens à afficher
     * @param analysisTokenDisplaySet la liste des tokens à afficher
     */
    public void setAnalysisTokenDisplaySet(Set<AnalysisTokenDisplay> analysisTokenDisplaySet) {
        this.analysisTokenDisplaySet = analysisTokenDisplaySet;
    }

    /**
     * Permet de se procurer la clé
     * @return la clé
     */
    public String getKey() {
        return key;
    }

    /**
     * Permet de définir la clé
     * @param key la clé à définir
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Permet de se procurer le nombre de token
     * @return le nombre de token
     */
    public Integer getNbToken() {
        return NbToken;
    }

    /**
     * Permet de définir le nombre de token
     * @param nbToken le nombre de token
     */
    public void setNbToken(Integer nbToken) {
        NbToken = nbToken;
    }

    /**
     * Permet de se procurer le nombre d'occurence
     * @return le nombre d'occurence
     */
    public Long getNbOccurrency() {
        return NbOccurrency;
    }

    /**
     * Permet de définir le nombre d'occurence
     * @param nbOccurrency le nombre d'occurence
     */
    public void setNbOccurrency(Long nbOccurrency) {
        NbOccurrency = nbOccurrency;
    }

    /**
     * Permet de convertir le résultat en liste de lignes pour token
     * @return liste de lignes pour token
     */
    public List<AnalysisRow> toAnalysisTokenRowList() {
        return this.getAnalysisTokenDisplaySet().stream().map(AnalysisTokenDisplay::toTokenRow).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Permet de se procurer la feuille excel
     * @return la feuille excel
     */
    public ExcelSheet toExcelSheet() {
        // Creation de la feuille
        return new ExcelSheetBuilder()
                .name(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_GLOBAL_LABEL))
                .excelBlockList(toExcelBlockList())
                .build();
    }

    /**
     * Permet de se procurer la liste des bloc excels
     * @return la liste des blocs excel
     */
    public List<ExcelBlock> toExcelBlockList() {
        // Creation du premier bloc
        ExcelCell cellTotalTokensLabel = new ExcelStringCellBuilder()
                .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_TOTAL_TOKENS_LABEL))
                .build();
        ExcelCell cellTotalWordsLabel = new ExcelStringCellBuilder()
                .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_TOTAL_WORDS_LABEL))
                .build();
        ExcelCell cellTotalTokens = new ExcelIntegerCellBuilder()
                .value(this.getNbToken())
                .build();
        ExcelCell cellTotalWords = new ExcelIntegerCellBuilder()
                .value(Math.toIntExact(this.getNbOccurrency()))
                .build();
        ExcelBlock excelBlockHeader = new ExcelBlock(new ExcelLine(cellTotalTokensLabel, cellTotalTokens), new ExcelLine(cellTotalWordsLabel, cellTotalWords));

        // Creation du deuxième bloc
        ExcelCell cellWordLabel = new ExcelStringCellBuilder()
                .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_1_LABEL))
                .build();
        ExcelCell cellNbTokenLabel = new ExcelStringCellBuilder()
                .value(ConfigurationUtils.getInstance().getDisplayMessage(WINDOW_RESULT_TOKEN_ANALYSIS_TABLE_HEADER_COLUMN_2_LABEL))
                .build();
        ExcelBlock excelBlockValues = new ExcelBlock(new ExcelLine(cellWordLabel, cellNbTokenLabel));
        excelBlockValues.getExcelLineLinkedList().addAll(analysisTokenDisplaySet.stream().map(AnalysisTokenDisplay::toExcelLine).collect(Collectors.toList()));

        return List.of(excelBlockHeader, excelBlockValues);
    }
}
