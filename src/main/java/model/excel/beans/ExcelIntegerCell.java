package model.excel.beans;

import model.PojoBuilder;
import model.excel.beans.abstracts.ExcelCellAbstract;

@PojoBuilder
public class ExcelIntegerCell extends ExcelCellAbstract<Integer> {

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
