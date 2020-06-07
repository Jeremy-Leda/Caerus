package model.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.excel.beans.ExcelRow;

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
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet sheet = workbook.createSheet("Textos");
			rows.forEach(er -> createRow(sheet, er));

			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		}
	}

	private void createRow(XSSFSheet sheet, ExcelRow er) {
		XSSFRow row = null;
		row = sheet.createRow(sheet.getLastRowNum()+1);
		for (String ecell : er.getCells()) {
			XSSFCell cell = null;
			if (row.getLastCellNum() < 0) {
				cell = row.createCell(row.getLastCellNum()+1);
			} else {
				cell = row.createCell(row.getLastCellNum());
			}
			cell.setCellValue(new XSSFRichTextString(ecell.replaceAll("\\R", " ")));
		}
	}

}
