package model.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import model.analyze.beans.Progress;
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

	public void generateExcel(Progress progressBean) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Textos");
			progressBean.setNbMaxElementForCurrentIterate(rows.size());
			for (int i = 0; i < rows.size(); i++) {
				progressBean.setCurrentElementForCurrentIterate(i);
				createRow(sheet, rows.get(i));
			}
			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		}
	}

	private void createRow(SXSSFSheet sheet, ExcelRow er) {
		SXSSFRow row = null;
		row = sheet.createRow(sheet.getLastRowNum()+1);
		for (String ecell : er.getCells()) {
			SXSSFCell cell = null;
			if (row.getLastCellNum() < 0) {
				cell = row.createCell(row.getLastCellNum()+1);
			} else {
				cell = row.createCell(row.getLastCellNum());
			}
			cell.setCellValue(new XSSFRichTextString(ecell.replaceAll("\\R", " ")));
		}
	}

}
