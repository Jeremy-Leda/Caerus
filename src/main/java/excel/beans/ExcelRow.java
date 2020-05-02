package excel.beans;

import java.util.ArrayList;
import java.util.List;

public class ExcelRow {

	private final List<String> listElement = new ArrayList<String>();
	
	public void addCell(String value) {
		this.listElement.add(value);
	}
	
	public List<String> getCells() {
		return this.listElement;
	}
	
}
