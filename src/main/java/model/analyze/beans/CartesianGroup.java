package model.analyze.beans;

import model.PojoBuilder;
import model.excel.beans.ExcelCell;
import model.excel.beans.ExcelLine;
import model.excel.beans.ExcelStringCell;
import model.excel.beans.ExcelStringCellBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@PojoBuilder
public class CartesianGroup {

    @NotBlank
    private String field;

    @NotBlank
    private String value;

    @NotBlank
    private String label;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartesianGroup that = (CartesianGroup) o;
        return Objects.equals(field, that.field) && Objects.equals(value, that.value) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, label);
    }

    /**
     * Permet de se procurer la ligne excel
     * @return la ligne excel
     */
    public ExcelLine toExcelLine() {
        ExcelCell label = new ExcelStringCellBuilder()
                .value(getLabel())
                .header(true)
                .build();
        ExcelCell value = new ExcelStringCellBuilder()
                .value(getValue())
                .build();
        return new ExcelLine(label, value);
    }
}
