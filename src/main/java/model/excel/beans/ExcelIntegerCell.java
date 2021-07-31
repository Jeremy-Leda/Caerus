package model.excel.beans;

import model.PojoBuilder;

import javax.validation.constraints.NotNull;

@PojoBuilder
public class ExcelIntegerCell implements ExcelCell<Integer> {

    @NotNull(message = "Le contenu de la valeur ne peut pas être null")
    private Integer value;

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
