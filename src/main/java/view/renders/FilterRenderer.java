package view.renders;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import view.beans.Filter;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

/**
 * 
 * Render pour l'affichage du display text dans une liste
 * 
 * @author jerem
 *
 */
public class FilterRenderer extends JLabel implements ListCellRenderer<Filter> {


	private final Integer MAX_LENGTH = 75;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7152590547291430291L;


	@Override
	public Component getListCellRendererComponent(JList<? extends Filter> list, Filter value, int index,
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
	private String constructFilterToDisplay(Filter value) {
		StringBuilder filterBuilder = new StringBuilder(Constants.WINDOW_FILTER_TYPE_PREFIX);
		filterBuilder.append(value.getType().name());
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html><p><br/><font color='green'>");
		stringBuilder.append(value.getLabel());
		stringBuilder.append("</font><br/>");
		stringBuilder.append(ConfigurationUtils.getInstance().getDisplayMessage(filterBuilder.toString()));
		stringBuilder.append("<br/>");
		if (value.getValue().length() > MAX_LENGTH) {
			stringBuilder.append(value.getValue().substring(0, MAX_LENGTH-3));
		} else {
			stringBuilder.append(value.getValue());
		}
		stringBuilder.append("<br/></p></html>");
		return stringBuilder.toString();
	}

}
