package view.renders;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import view.beans.InconsistencyError;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Render pour l'affichage du display text dans une liste
 * 
 * @author jerem
 *
 */
public class InconsistencyErrorRenderer extends JLabel implements ListCellRenderer<InconsistencyError> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7152590547291430291L;


	@Override
	public Component getListCellRendererComponent(JList<? extends InconsistencyError> list, InconsistencyError value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setIcon(value.getImageIcon());
		setText(constructFilterToDisplay(value));
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
	private String constructFilterToDisplay(InconsistencyError value) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html><p><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_NAME_FILE_LABEL));
		stringBuilder.append(" ");
		stringBuilder.append(value.getNameFile());
		stringBuilder.append("<br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_FIELD_LABEL));
		stringBuilder.append(" <font color='red'>");
		if (value.getOldFieldIsMetaFile()) {
			stringBuilder.append(value.getOldFieldName());
		} else {
			stringBuilder.append(value.getNewFieldName());	
		}
		stringBuilder.append("</font><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_NUMBER_LINE_LABEL));
		stringBuilder.append(" ");
		if (value.getOldFieldIsMetaFile()) {
			stringBuilder.append(value.getOldNumLine());
		} else {
			stringBuilder.append(value.getNewNumLine());
		}
		stringBuilder.append("<br/>");
		stringBuilder.append("<br/></p></html>");
		return stringBuilder.toString();
	}

}
