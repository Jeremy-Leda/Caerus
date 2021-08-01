package view.analysis.beans;

import model.PojoBuilder;
import model.analyze.beans.CartesianGroup;
import model.excel.beans.ExcelBlock;
import model.excel.beans.ExcelLine;
import model.excel.beans.ExcelSheet;
import model.excel.beans.ExcelSheetBuilder;
import view.analysis.beans.interfaces.IExcelSheet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Objet d'affichage pour les résultats de regroupements
 *
 */
@PojoBuilder
public class AnalysisGroupDisplay implements IExcelSheet {

    @NotEmpty(message = "la liste des groupes cartésiens ne peut pas être vide")
    private Set<CartesianGroup> cartesianGroupSet;

    @NotNull(message = "Les résultats ne peuvent pas être null")
    private AnalysisResultDisplay analysisResultDisplay;

    @NotNull(message = "la liste des clés ne peut pas être null")
    private Set<String> keySet;

    public AnalysisResultDisplay getAnalysisResultDisplay() {
        return analysisResultDisplay;
    }

    public void setAnalysisResultDisplay(AnalysisResultDisplay analysisResultDisplay) {
        this.analysisResultDisplay = analysisResultDisplay;
    }

    public Set<String> getKeySet() {
        return keySet;
    }

    public void setKeySet(Set<String> keySet) {
        this.keySet = keySet;
    }

    public Set<CartesianGroup> getCartesianGroupSet() {
        return cartesianGroupSet;
    }

    public void setCartesianGroupSet(Set<CartesianGroup> cartesianGroupSet) {
        this.cartesianGroupSet = cartesianGroupSet;
    }

    public String getTitle() {
        String reduce = cartesianGroupSet.stream().map(CartesianGroup::getValue).reduce((a, b) -> a + " - " + b).get();
        if (reduce.length() > 25) {
            return reduce.substring(0, 22) + "...";
        }
        return reduce;
    }

    @Override
    public ExcelSheet getExcelSheet() {
        List<ExcelLine> excelLineCartesianList = this.cartesianGroupSet.stream().map(CartesianGroup::toExcelLine).collect(Collectors.toList());
        List<ExcelBlock> excelBlockList = new LinkedList<>();
        excelBlockList.add(new ExcelBlock(excelLineCartesianList.toArray(ExcelLine[]::new)));
        excelBlockList.addAll(analysisResultDisplay.toExcelBlockList());
        return new ExcelSheetBuilder()
                .name(getTitle())
                .excelBlockList(excelBlockList)
                .nbColumnMax(2)
                .build();
    }
}
