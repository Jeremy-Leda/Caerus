package view.renders;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import view.beans.DisplayText;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Render pour l'affichage du display text dans une liste
 * 
 * @author jerem
 *
 */
public class DisplayTextRenderer extends JLabel implements ListCellRenderer<DisplayText> {


	private final Integer MAX_LENGTH = 75;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7152590547291430291L;


	@Override
	public Component getListCellRendererComponent(JList<? extends DisplayText> list, DisplayText value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setIcon(value.getImageIcon());
		setText(constructInformartionText(value));
	    setOpaque(true);
	    if (isSelected) {
	        setBackground(list.getSelectionBackground());
	    } else {
	        setBackground(list.getBackground());
	    }
		return this;
	}

	/**
	 * Permet de construire le texte pour l'affichage des informations du texte
	 * 
	 * @param value valeur du texte
	 * @return le texte
	 */
	private String constructInformartionText(DisplayText value) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html><p><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_DISPLAY_TEXTS_CORPUS_LABEL)).append(value.getCorpusName());
		value.getMapKeyValueList().entrySet().stream().forEach((entry) -> {
			stringBuilder.append("<br/><font color='rgb(74, 86, 145)'>").append(entry.getKey()).append(" : </font>");
			String totalText = entry.getKey() + entry.getValue();
			if (totalText.length() > MAX_LENGTH) {
				stringBuilder.append("<br/>");
			}
			String textValue = entry.getValue();
			if (textValue.length() > MAX_LENGTH) {
				StringBuilder sbNewText = new StringBuilder();
				sbNewText.append(textValue.substring(0, MAX_LENGTH-3));
				sbNewText.append("...");
				textValue = sbNewText.toString();
			}
			stringBuilder.append(textValue);
		});
		stringBuilder.append("<br/></p></html>");
		return stringBuilder.toString();
	}

}
