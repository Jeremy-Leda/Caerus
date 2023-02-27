package view.analysis.beans;

import model.PojoBuilder;
import model.excel.beans.ExcelIntegerCell;
import model.excel.beans.ExcelLine;
import model.excel.beans.ExcelStringCell;
import view.beans.FrequencyOrder;
import view.panel.analysis.model.AnalysisRow;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@PojoBuilder
public class AnalysisFrequencyOrder {

    @NotNull
    private FrequencyOrder frequencyOrderText;

    private Optional<FrequencyOrder> optionalFrequencyOrderRepository;

    public FrequencyOrder getFrequencyOrderText() {
        return frequencyOrderText;
    }

    public void setFrequencyOrderText(FrequencyOrder frequencyOrderText) {
        this.frequencyOrderText = frequencyOrderText;
    }

    public Optional<FrequencyOrder> getOptionalFrequencyOrderRepository() {
        return optionalFrequencyOrderRepository;
    }

    public void setOptionalFrequencyOrderRepository(Optional<FrequencyOrder> optionalFrequencyOrderRepository) {
        this.optionalFrequencyOrderRepository = optionalFrequencyOrderRepository;
    }

    /**
     * Permet de transformer le token en ligne de type token
     * @return ligne de type token
     */
    public AnalysisRow toTokenRow() {
        AnalysisRow row = new AnalysisRow();
        row.getAnalysisList().addAll(List.of(frequencyOrderText.getOrder(), frequencyOrderText.getWord(), frequencyOrderText.getFrequency()));
        optionalFrequencyOrderRepository.ifPresent(x -> row.getAnalysisList().addAll(List.of(x.getOrder(), x.getWord(), x.getFrequency())));
        return row;
    }

    public ExcelLine toExcelLine() {
        ExcelIntegerCell orderText = new ExcelIntegerCell();
        orderText.setValue(frequencyOrderText.getOrder());
        ExcelStringCell wordText = new ExcelStringCell();
        wordText.setValue(frequencyOrderText.getWord());
        ExcelIntegerCell frequencyText = new ExcelIntegerCell();
        frequencyText.setValue(frequencyOrderText.getFrequency());
        ExcelIntegerCell orderRepo = new ExcelIntegerCell();
        orderRepo.setValue(optionalFrequencyOrderRepository.orElse(new FrequencyOrder()).getOrder());
        ExcelStringCell wordRepo = new ExcelStringCell();
        wordRepo.setValue(optionalFrequencyOrderRepository.orElse(new FrequencyOrder()).getWord());
        ExcelIntegerCell frequencyRepo = new ExcelIntegerCell();
        frequencyRepo.setValue(optionalFrequencyOrderRepository.orElse(new FrequencyOrder()).getFrequency());
        if (optionalFrequencyOrderRepository.isPresent()) {
            return new ExcelLine(orderText, wordText, frequencyText, orderRepo, wordRepo, frequencyRepo);
        }
        return new ExcelLine(orderText, wordText, frequencyText);
    }
}
