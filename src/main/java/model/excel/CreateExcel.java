package model.excel;

import model.abstracts.ProgressAbstract;
import model.analyze.Dispatcher;
import model.analyze.beans.Progress;
import model.excel.beans.*;
import model.excel.factories.CellStyleWorkbookFactory;
import model.excel.interfaces.ICellStyleWorkbook;
import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import model.interfaces.ICreateExcel;
import model.interfaces.IProgressBean;
import model.interfaces.IProgressModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CreateExcel extends ProgressAbstract implements ICreateExcel {

	private final File path;
	private final List<ExcelRow> rows = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(CreateExcel.class);

	public CreateExcel(File path) {
		this(path, 0);
	}

	public CreateExcel(File path, Integer nbMaxIterate) {
		super();
		this.path = path;
		super.createProgressBean(nbMaxIterate);
	}

	@Override
	public void createRow(List<String> listCells) {
		ExcelRow row = new ExcelRow();
		listCells.forEach(row::addCell);
		rows.add(row);
	}

	@Override
	public void generateExcel(IProgressBean progressBean) throws IOException {
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			SXSSFSheet sheet = workbook.createSheet("Textos");
			progressBean.setNbMaxElementForCurrentIterate(rows.size());
			for (int i = 0; i < rows.size(); i++) {
				if (treatmentIsCancelled()) {
					break;
				}
				progressBean.setCurrentElementForCurrentIterate(i);
				createRow(sheet, rows.get(i));
			}
			if (treatmentIsCancelled()) {
				return;
			}
			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		}
	}

	private void createRow(SXSSFSheet sheet, ExcelRow er) {
		SXSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
		for (String ecell : er.getCells()) {
			if (treatmentIsCancelled()) {
				break;
			}
			SXSSFCell cell;
			if (row.getLastCellNum() < 0) {
				cell = row.createCell(row.getLastCellNum()+1);
			} else {
				cell = row.createCell(row.getLastCellNum());
			}
			cell.setCellValue(new XSSFRichTextString(ecell.replaceAll("\\R", " ")));
		}
	}

	@Override
	public void generateExcel(List<ExcelSheet> excelSheetList, Boolean withFormat) {
		super.createProgressBean(excelSheetList.size());
		List<String> listSheetName = new ArrayList<>();
		try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
			for (int i = 0; i < excelSheetList.size(); i++) {
				if (treatmentIsCancelled()) {
					break;
				}
				super.getProgressBean().setCurrentIterate(i+1);
				ExcelSheet currentSheet = excelSheetList.get(i);
				createSheet(workbook, currentSheet, listSheetName, 1, withFormat);
				listSheetName.add(currentSheet.getFormattedName().toLowerCase(Locale.ROOT));
			}
			if (treatmentIsCancelled()) {
				return;
			}
			try (FileOutputStream fos = new FileOutputStream(path)) {
				workbook.write(fos);
			}
		} catch (Exception ex) {
			throw new ServerException().addInformationException(new InformationExceptionBuilder()
					.errorCode(ErrorCode.TECHNICAL_ERROR)
					.stackTraceElements(ex.getStackTrace())
					.exceptionParent(ex)
					.build());
		}
	}

	public void createSheet(SXSSFWorkbook workbook, ExcelSheet excelSheet, List<String> listSheetName, int numberSheetAddIfExist, Boolean withFormat) {
		if (listSheetName.contains(excelSheet.getFormattedName().toLowerCase(Locale.ROOT))) {
			String newName = excelSheet.getFormattedName() + " " + numberSheetAddIfExist;
			if (numberSheetAddIfExist > 1) {
				newName = excelSheet.getFormattedName().substring(0, excelSheet.getFormattedName().length() - 1) + numberSheetAddIfExist;
			}
			excelSheet.setName(newName);
			createSheet(workbook, excelSheet, listSheetName, numberSheetAddIfExist + 1, withFormat);
			return;
		}
		SXSSFSheet sheet = workbook.createSheet(excelSheet.getFormattedName());
		ICellStyleWorkbook cellStyleWorkbook = new CellStyleWorkbookFactory(workbook);
		if (withFormat) {
			sheet.trackAllColumnsForAutoSizing();
		}
		excelSheet.getExcelBlockList().forEach(excelBlock -> {
			Optional<ICellStyleWorkbook> cellStyleWorkbookFactoryOptional = withFormat ? Optional.of(cellStyleWorkbook) : Optional.empty();
			createBlock(sheet, excelBlock, cellStyleWorkbookFactoryOptional);
			sheet.createRow(sheet.getLastRowNum() + 1);
		});
		if (withFormat) {
			for (int i = 0; i < excelSheet.getNbColumnMax(); i++) {
				sheet.autoSizeColumn(i);
			}
		}
	}

	public void createBlock(SXSSFSheet sheet, ExcelBlock excelBlock, Optional<ICellStyleWorkbook> cellStyleWorkbook) {
		for (ExcelLine line : excelBlock.getExcelLineLinkedList()) {
			if (treatmentIsCancelled()) {
				break;
			}
			SXSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			createLine(row, line, true, cellStyleWorkbook);
		}
	}

	public void createLine(SXSSFRow row, ExcelLine excelLine, boolean isTableStyle, Optional<ICellStyleWorkbook> cellStyleWorkbook) {
		for (ExcelCell l : excelLine.getExcelCellLinkedList()) {
			if (treatmentIsCancelled()) {
				break;
			}
			int cellNumber = row.getLastCellNum() < 0 ?  row.getLastCellNum()+1 : row.getLastCellNum();
			SXSSFCell cell = row.createCell(cellNumber);
			cellStyleWorkbook.ifPresent(c -> {
				if (isTableStyle) {
					cell.setCellStyle(l.isHeader() ? c.getHeaderTableStyle(BorderStyle.DOUBLE) : c.getTableStyle(BorderStyle.MEDIUM));
				}
			});
			if (l.getType().equals(String.class)) {
				String value = (String) l.getValue();
				cell.setCellValue(new XSSFRichTextString(value.replaceAll("\\R", " ")));
				cell.setCellType(CellType.STRING);
			} else if (l.getType().equals(Integer.class)) {
				Integer value = (Integer) l.getValue();
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(value);
			}
		}
	}
}
