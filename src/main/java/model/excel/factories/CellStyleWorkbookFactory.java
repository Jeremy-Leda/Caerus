package model.excel.factories;

import model.excel.interfaces.ICellStyleWorkbook;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class CellStyleWorkbookFactory implements ICellStyleWorkbook {

    private final Workbook workbook;

    public CellStyleWorkbookFactory(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public CellStyle getTableStyle(BorderStyle borderStyle) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        return cellStyle;
    }

    @Override
    public CellStyle getHeaderTableStyle(BorderStyle borderStyle) {
        CellStyle cellStyle = getTableStyle(borderStyle);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        cellStyle.setFont(font);
        ((XSSFCellStyle)cellStyle).setFillForegroundColor(getRGBColor(74,86,145));
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private XSSFColor getRGBColor(int red, int green, int blue) {
        byte[] rgb = new byte[3];
        rgb[0] = (byte) red;
        rgb[1] = (byte) green;
        rgb[2] = (byte) blue;
        return new XSSFColor(rgb, new DefaultIndexedColorMap());
    }
}
