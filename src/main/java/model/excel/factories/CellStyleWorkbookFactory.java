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
    private final CellStyle cellStyle;
    private final CellStyle cellHeaderStyle;

    public CellStyleWorkbookFactory(Workbook workbook) {
        this.workbook = workbook;
        this.cellStyle = workbook.createCellStyle();
        this.cellHeaderStyle = workbook.createCellStyle();
    }

    @Override
    public CellStyle getTableStyle(BorderStyle borderStyle) {
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        return cellStyle;
    }

    @Override
    public CellStyle getHeaderTableStyle(BorderStyle borderStyle) {
        cellHeaderStyle.setBorderTop(borderStyle);
        cellHeaderStyle.setBorderRight(borderStyle);
        cellHeaderStyle.setBorderBottom(borderStyle);
        cellHeaderStyle.setBorderLeft(borderStyle);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        cellHeaderStyle.setFont(font);
        ((XSSFCellStyle)cellHeaderStyle).setFillForegroundColor(getRGBColor(74,86,145));
        cellHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellHeaderStyle;
    }

    private XSSFColor getRGBColor(int red, int green, int blue) {
        byte[] rgb = new byte[3];
        rgb[0] = (byte) red;
        rgb[1] = (byte) green;
        rgb[2] = (byte) blue;
        return new XSSFColor(rgb, new DefaultIndexedColorMap());
    }
}
