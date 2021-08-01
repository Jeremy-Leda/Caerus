package model.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.excel.beans.ExcelBlock;
import model.excel.beans.ExcelLine;
import model.excel.beans.ExcelSheet;
import model.excel.factories.CellStyleWorkbookFactory;
import model.excel.interfaces.ICellStyleWorkbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import model.analyze.beans.Progress;
import model.excel.beans.ExcelRow;

public class CreateExcel {

	private final File path;
	private final List<ExcelRow> rows = new ArrayList<ExcelRow>();
	private int nbColumnMax = 0;

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
		SXSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
		for (String ecell : er.getCells()) {
			SXSSFCell cell;
			if (row.getLastCellNum() < 0) {
				cell = row.createCell(row.getLastCellNum()+1);
			} else {
				cell = row.createCell(row.getLastCellNum());
			}
			cell.setCellValue(new XSSFRichTextString(ecell.replaceAll("\\R", " ")));
		}
	}

	public void generateExcel(List<ExcelSheet> excelSheetList, Progress progressBean) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			progressBean.setNbMaxElementForCurrentIterate(excelSheetList.size());
			for (int i = 0; i < excelSheetList.size(); i++) {
				createSheet(workbook, excelSheetList.get(i));
				progressBean.setCurrentElementForCurrentIterate(i);
			}
			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		}
	}

	public void createSheet(SXSSFWorkbook workbook, ExcelSheet excelSheet) {
		SXSSFSheet sheet = workbook.createSheet(excelSheet.getName());
		ICellStyleWorkbook cellStyleWorkbook = new CellStyleWorkbookFactory(workbook);
		sheet.trackAllColumnsForAutoSizing();
		excelSheet.getExcelBlockList().forEach(excelBlock -> {
			createBlock(sheet, excelBlock, cellStyleWorkbook);
			sheet.createRow(sheet.getLastRowNum() + 1);
		});
		for (int i = 0; i < excelSheet.getNbColumnMax(); i++) {
			sheet.autoSizeColumn(i);
		}

	}

	public void createBlock(SXSSFSheet sheet, ExcelBlock excelBlock, ICellStyleWorkbook cellStyleWorkbook) {
		excelBlock.getExcelLineLinkedList().forEach(line -> {
			SXSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			createLine(row, line, true, cellStyleWorkbook);
		});
	}

	public void createLine(SXSSFRow row, ExcelLine excelLine, boolean isTableStyle, ICellStyleWorkbook cellStyleWorkbook) {
		excelLine.getExcelCellLinkedList().forEach(l -> {
			int cellNumber = row.getLastCellNum() < 0 ?  row.getLastCellNum()+1 : row.getLastCellNum();
			SXSSFCell cell = row.createCell(cellNumber);
			if (isTableStyle) {
				cell.setCellStyle(l.isHeader() ? cellStyleWorkbook.getHeaderTableStyle(BorderStyle.DOUBLE) : cellStyleWorkbook.getTableStyle(BorderStyle.MEDIUM));
			}
			if (l.getType().equals(String.class)) {
				String value = (String) l.getValue();
				cell.setCellValue(new XSSFRichTextString(value.replaceAll("\\R", " ")));
				cell.setCellType(CellType.STRING);
			} else if (l.getType().equals(Integer.class)) {
				Integer value = (Integer) l.getValue();
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(value);
			}
		});
	}

	public void createRowWithEmptyValues(SXSSFRow row, int nbColumn) {
		for (int i = 0; i < nbColumn; i++) {
			SXSSFCell cell = row.createCell(i);
			cell.setCellValue(StringUtils.EMPTY);
		}
	}




}
