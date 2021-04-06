package model.analyze;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import model.analyze.beans.Configuration;
import model.analyze.beans.StructuredField;
import model.analyze.beans.StructuredFile;
import model.analyze.beans.StructuredText;
import model.excel.beans.ExcelGenerateConfigurationCmd;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

public class ExcelStructuring {

	private List<StructuredField> listStructuredFieldOrdered;

	private final List<List<String>> rows = new ArrayList<List<String>>();
	private final String headerNumber = ConfigurationUtils.getInstance()
			.getDisplayMessage(Constants.EXCEL_HEADER_NUMBER);
	private Boolean isSpecificConfiguration;
	private Integer specificOrder;
	private String currentUniqueKey;
	private Integer specificRowNumber;

	public List<List<String>> getStructuringRows(List<StructuredFile> files, Configuration configuration, ExcelGenerateConfigurationCmd cmd) {
		this.isSpecificConfiguration = cmd.getSpecificConfiguration();
		this.specificOrder = cmd.getConfigurationSpecificOrder();
		if (null != cmd.getFieldToGenerateList() && !cmd.getFieldToGenerateList().isEmpty()) {
			this.listStructuredFieldOrdered = configuration.getStructuredFieldList().stream()
					.filter(f -> cmd.getFieldToGenerateList().contains(f.getFieldName())).sorted(Comparator.comparingInt(StructuredField::getOrder))
					.collect(Collectors.toList());
		} else {
			this.listStructuredFieldOrdered = configuration.getStructuredFieldList().stream()
					.sorted(Comparator.comparingInt(StructuredField::getOrder)).collect(Collectors.toList());
		}
		if (cmd.getWithHeader()) {
			rows.add(excelHeader(cmd));
		}
		files.stream().sorted(Comparator.comparing(StructuredFile::getNumber)).forEach(sf -> excelValues(sf, cmd.getUniqueKeyList(), cmd).stream().forEach(st -> this.rows.add(st)));
		return this.rows;
	}

	private List<String> excelHeader(ExcelGenerateConfigurationCmd cmd) {
		List<String> resultList = new ArrayList<>();
		if (cmd.getAddUniqueKey()) {
			resultList.add("key");
		}
		if (cmd.getAddNumber()) {
			resultList.add(headerNumber);
		}
		resultList.addAll(listStructuredFieldOrdered.stream().map(s -> s.getLabel()).collect(Collectors.toList()));
		return resultList;
	}

	private List<String> excelValues(StructuredText st, ExcelGenerateConfigurationCmd cmd, int numberFile) {
		List<String> resultList = new ArrayList<>();
		if (cmd.getAddUniqueKey()) {
			resultList.add(st.getUniqueKey());
		}
		if (cmd.getAddNumber()) {
			resultList.add(createNumber(st, numberFile));
		}
		List<String> collect = listStructuredFieldOrdered.stream().map(s -> s.getFieldName()).collect(Collectors.toList());
		resultList.addAll(collect.stream().map(fieldName -> st.getContent(fieldName)).collect(Collectors.toList()));
		return resultList;
	}

	private List<List<String>> excelValues(StructuredFile sf, List<String> uniqueKeyList, ExcelGenerateConfigurationCmd cmd) {
		if (uniqueKeyList.isEmpty()) {
			return sf.getListStructuredText().stream().sorted(Comparator.comparing(StructuredText::getNumber)).map(st -> excelValues(st, cmd, sf.getNumber())).collect(Collectors.toList());
		} else {
			return sf.getListStructuredText().stream().sorted(Comparator.comparing(StructuredText::getNumber)).filter(st -> uniqueKeyList.contains(st.getUniqueKey())).map(st -> excelValues(st, cmd, sf.getNumber())).collect(Collectors.toList());
		}
	}

	/**
	 * Permet de créer le numéro pour le fichier excel
	 * @param st Texte structuré
	 * @param numberFile Numéro du fichier
	 * @return le numéro
	 */
	private String createNumber(StructuredText st, int numberFile) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(numberFile)
				.append(".")
				.append(st.getNumber());
		if (isSpecificConfiguration) {
			if (!st.getUniqueKey().equals(currentUniqueKey)) {
				specificRowNumber = 0;
				currentUniqueKey = st.getUniqueKey();
			}
			stringBuilder.append(".")
				.append(specificOrder)
				.append(".")
				.append(++specificRowNumber);
		}
		return stringBuilder.toString();
	}
}
