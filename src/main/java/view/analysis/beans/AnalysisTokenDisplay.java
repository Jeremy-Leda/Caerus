package view.analysis.beans;

import model.PojoBuilder;
import model.excel.beans.*;
import view.panel.analysis.model.AnalysisRow;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Bean pour l'affichage d'un token
 *
 */
@PojoBuilder
public class AnalysisTokenDisplay {

    @NotEmpty
    private String word;
    private Long nbOcurrency;

    @NotNull
    private AnalysisFrequencyOrder analysisFrequencyOrder;

    /**
     * Permet de se procurer le mot
     * @return le mot
     */
    public String getWord() {
        return word;
    }

    /**
     * Permet de définir le mot
     * @param word le mot à définir
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Permet de se procurer le nombre d'occurence
     * @return le nombre d'occurrence
     */
    public Long getNbOcurrency() {
        return nbOcurrency;
    }

    /**
     * Permet de définir le nombre d'occurence
     * @param nbOcurrency le nombre d'occurence
     */
    public void setNbOcurrency(Long nbOcurrency) {
        this.nbOcurrency = nbOcurrency;
    }

    public AnalysisFrequencyOrder getAnalysisFrequencyOrder() {
        return analysisFrequencyOrder;
    }

    public void setAnalysisFrequencyOrder(AnalysisFrequencyOrder analysisFrequencyOrder) {
        this.analysisFrequencyOrder = analysisFrequencyOrder;
    }

    /**
     * Permet de transformer le token en ligne de type token
     * @return ligne de type token
     */
    public AnalysisRow toTokenRow() {
        AnalysisRow row = new AnalysisRow();
        row.getAnalysisList().addAll(List.of(word, nbOcurrency));
        return row;
    }

    /**
     * Permet de se procurer la ligne excel
     * @return la ligne excel
     */
    public ExcelLine toExcelLine() {
        ExcelStringCell excelCellWord = new ExcelStringCell();
        excelCellWord.setValue(word);
        ExcelIntegerCell excelCellOccurrency = new ExcelIntegerCell();
        excelCellOccurrency.setValue(Math.toIntExact(nbOcurrency));
        return new ExcelLine(excelCellWord, excelCellOccurrency);
    }
}
