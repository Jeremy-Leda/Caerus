package view.renders;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import view.beans.BaseCodeError;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Render pour l'affichage du display text dans une liste
 * 
 * @author jerem
 *
 */
public class InconsistencyBaseCodeErrorRenderer extends JLabel implements ListCellRenderer<BaseCodeError> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7152590547291430291L;


	@Override
	public Component getListCellRendererComponent(JList<? extends BaseCodeError> list, BaseCodeError value, int index,
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
	private String constructFilterToDisplay(BaseCodeError value) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html><p><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_MISSING_BASE_CODE_NAME_FILE_LABEL));
		stringBuilder.append(" ");
		stringBuilder.append(value.getNameFile());
		stringBuilder.append("<br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_MISSING_BASE_CODE_FIELD_LABEL));
		stringBuilder.append(" <font color='red'>");
		stringBuilder.append(value.getFieldName());
		stringBuilder.append("</font><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_ERROR_INCONSISTENCY_NUMBER_LINE_LABEL));
		stringBuilder.append(" ");
		stringBuilder.append(value.getNumLine().toString());
		stringBuilder.append("<br/>");
		stringBuilder.append("<br/></p></html>");
		return stringBuilder.toString();
	}

}
