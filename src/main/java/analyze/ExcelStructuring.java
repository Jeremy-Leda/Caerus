package analyze;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import analyze.beans.Configuration;
import analyze.beans.StructuredField;
import analyze.beans.StructuredFile;
import analyze.beans.StructuredText;
import excel.beans.ExcelGenerateConfigurationCmd;

public class ExcelStructuring {

	private List<StructuredField> listStructuredFieldOrdered;

	private final List<List<String>> rows = new ArrayList<List<String>>();

	public List<List<String>> getStructuringRows(List<StructuredFile> files, Configuration configuration) {
		this.listStructuredFieldOrdered = configuration.getStructuredFieldList().stream().sorted(Comparator.comparingInt(StructuredField::getOrder))
				.collect(Collectors.toList());
		rows.add(excelHeader(configuration));
		files.stream().forEach(sf -> excelValues(sf).stream().forEach(st -> this.rows.add(st)));
		return this.rows;
	}

	public List<List<String>> getStructuringRows(List<StructuredFile> files, Configuration configuration, ExcelGenerateConfigurationCmd cmd) {
		if (null != cmd.getFieldToGenerateList() && !cmd.getFieldToGenerateList().isEmpty()) {
			this.listStructuredFieldOrdered = configuration.getStructuredFieldList().stream()
					.filter(f -> cmd.getFieldToGenerateList().contains(f.getFieldName())).sorted(Comparator.comparingInt(StructuredField::getOrder))
					.collect(Collectors.toList());
		} else {
			this.listStructuredFieldOrdered = configuration.getStructuredFieldList().stream()
					.sorted(Comparator.comparingInt(StructuredField::getOrder)).collect(Collectors.toList());
		}
		if (cmd.getWithHeader()) {
			rows.add(excelHeader(configuration));
		}
		files.stream().forEach(sf -> excelValues(sf).stream().forEach(st -> this.rows.add(st)));
		return this.rows;
	}

	private List<String> excelHeader(Configuration configuration) {
		return listStructuredFieldOrdered.stream().map(s -> s.getLabel()).collect(Collectors.toList());
	}

	private List<String> excelValues(StructuredText st) {
		List<String> collect = listStructuredFieldOrdered.stream().map(s -> s.getFieldName()).collect(Collectors.toList());
		return collect.stream().map(fieldName -> st.getContent(fieldName)).collect(Collectors.toList());
	}

	private List<List<String>> excelValues(StructuredFile sf) {
		return sf.getListStructuredText().stream().map(st -> excelValues(st)).collect(Collectors.toList());
	}
}
