package model.analyze.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * Bean utilisé pour effectuer la sauvegarder du texte en cours
 * 
 * @author jerem
 *
 */
public class SaveCurrentFixedText {
	private File path;
	private List<UserStructuredText> userStructuredTextList = new ArrayList<UserStructuredText>();
	private Set<String> keysStructuredTextErrorSet = new HashSet<>();
	private Set<String> keysBlankLineErrorSet = new HashSet<>();
	private Set<String> keysMetaBlankLineErrorSet = new HashSet<>();
	
	public List<UserStructuredText> getUserStructuredTextList() {
		return userStructuredTextList;
	}
	public void setUserStructuredTextList(List<UserStructuredText> userStructuredTextList) {
		this.userStructuredTextList = userStructuredTextList;
	}
	public Set<String> getKeysStructuredTextErrorSet() {
		return keysStructuredTextErrorSet;
	}
	public void setKeysStructuredTextErrorSet(Set<String> keysStructuredTextErrorSet) {
		this.keysStructuredTextErrorSet = keysStructuredTextErrorSet;
	}
	public File getPath() {
		return path;
	}
	public void setPath(File path) {
		this.path = path;
	}
	public Set<String> getKeysBlankLineErrorSet() {
		return keysBlankLineErrorSet;
	}
	public void setKeysBlankLineErrorSet(Set<String> keysBlankLineErrorSet) {
		this.keysBlankLineErrorSet = keysBlankLineErrorSet;
	}
	public Set<String> getKeysMetaBlankLineErrorSet() {
		return keysMetaBlankLineErrorSet;
	}
	public void setKeysMetaBlankLineErrorSet(Set<String> keysMetaBlankLineErrorSet) {
		this.keysMetaBlankLineErrorSet = keysMetaBlankLineErrorSet;
	}
	
}
