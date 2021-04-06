package model.analyze.beans;

import java.nio.file.Path;

/**
 * 
 * Bean permettant de stocker les lignes non interprétables
 * 
 * @author jerem
 *
 */
public class LineError {

	private Path path;
	private String lineWithError;
	private String lineFixed;
	private Integer index;
	
	public LineError(Path path, Integer index, String lineWithError) {
		this.path = path;
		this.index = index;
		this.lineWithError = lineWithError;
	}
	
	public String getLineWithError() {
		return lineWithError;
	}
	public String getLineFixed() {
		return lineFixed;
	}
	public void setLineFixed(String lineFixed) {
		this.lineFixed = lineFixed;
	}
	public Integer getIndex() {
		return index;
	}
	public Path getPath() {
		return path;
	}
	
}
