package excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import excel.beans.ExcelRow;

public class CreateExcel {

	private final File path;
	private final List<ExcelRow> rows = new ArrayList<ExcelRow>();

	public CreateExcel(File path) {
		super();
		this.path = path;
	}

	public void createRow(List<String> listCells) {
		ExcelRow row = new ExcelRow();
		listCells.stream().forEach(s -> row.addCell(s));
		rows.add(row);
	}

	public void generateExcel() throws IOException {
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet sheet = workbook.createSheet("Textos");
			rows.forEach(er -> createRow(sheet, er));

			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		}
	}

	private void createRow(HSSFSheet sheet, ExcelRow er) {
		HSSFRow row = null;
		if (sheet.getPhysicalNumberOfRows()>1) {
			row = sheet.createRow(sheet.getLastRowNum()+1);
		} else {
			row = sheet.createRow(sheet.getLastRowNum()+sheet.getPhysicalNumberOfRows());
		}
		for (String ecell : er.getCells()) {
			HSSFCell cell = null;
			if (row.getLastCellNum() < 0) {
				cell = row.createCell(row.getLastCellNum()+1);
			} else {
				cell = row.createCell(row.getLastCellNum());
			}
			cell.setCellValue(new HSSFRichTextString(ecell.replaceAll("\\R", " ")));
		}
	}

}
