package view.utils;

import java.awt.Component;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * 
 * Classe permettant de déterminer la taille des colonnes automatiquement
 * 
 * @author jerem
 *
 */
public class ColumnsAutoSize {

	public static void sizeColumnsToFit(JTable table) {
		sizeColumnsToFit(table, 0);
	}

	public static void sizeColumnsToFit(JTable table, int columnIndex) {
		sizeColumnsToFit(table, 5, -1, columnIndex);
	}

	public static void sizeColumnsToFitForUpdate(JTable table, int rowIndex, int columnIndex) {
		sizeColumnsToFit(table, 5, rowIndex, columnIndex);
	}

	public static void sizeColumnsToFit(JTable table, int columnMargin, int rowIndex, int columnIndex) {
		JTableHeader tableHeader = table.getTableHeader();

		if (tableHeader == null) {
			// can't auto size a table without a header
			return;
		}
		

		FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());

		int[] minWidths = new int[table.getColumnCount()];
		int[] maxWidths = new int[table.getColumnCount()];

		int headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));

		if (rowIndex > -1) {
			minWidths[0] = table.getColumnModel().getColumn(columnIndex).getMinWidth();
		} else {
			minWidths[0] = headerWidth + columnMargin;
		}
		int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth, rowIndex);

		maxWidths[0] = Math.max(maxWidth, minWidths[0]) + columnMargin;

		adjustMaximumWidths(table, minWidths, maxWidths);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setVerticalAlignment(JLabel.CENTER);
		centerRenderer.setHorizontalAlignment(JLabel.LEFT);
		for (int i = 0; i < minWidths.length; i++) {

			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		if (minWidths[0] > 0) {
			table.getColumnModel().getColumn(columnIndex).setMinWidth(minWidths[0]);
		}

		if (maxWidths[0] > 0) {
			table.getColumnModel().getColumn(columnIndex).setMaxWidth(maxWidths[0]);

			table.getColumnModel().getColumn(columnIndex).setWidth(maxWidths[0]);
		}
		table.setRowHeight(30);
		table.setShowGrid(false);
	}

	private static void adjustMaximumWidths(JTable table, int[] minWidths, int[] maxWidths) {
		if (table.getWidth() > 0) {
			// to prevent infinite loops in exceptional situations
			int breaker = 0;

			// keep stealing one pixel of the maximum width of the highest column until we
			// can fit in the width of the table
			while (sum(maxWidths) > table.getWidth() && breaker < 10000) {
				int highestWidthIndex = findLargestIndex(maxWidths);

				maxWidths[highestWidthIndex] -= 1;

				maxWidths[highestWidthIndex] = Math.max(maxWidths[highestWidthIndex], minWidths[highestWidthIndex]);

				breaker++;
			}
		}
	}

	private static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth, int rowIndex) {
		int maxWidth = headerWidth;

		TableColumn column = table.getColumnModel().getColumn(columnIndex);

		TableCellRenderer cellRenderer = column.getCellRenderer();

		if (cellRenderer == null) {
			cellRenderer = new DefaultTableCellRenderer();
		}
		if (rowIndex > -1) {
			Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
					table.getModel().getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);
			double valueWidth = rendererComponent.getPreferredSize().getWidth();

			maxWidth = (int) Math.max(maxWidth, valueWidth);
		} else {
			for (int row = 0; row < table.getModel().getRowCount(); row++) {
				Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
						table.getModel().getValueAt(row, columnIndex), false, false, row, columnIndex);

				double valueWidth = rendererComponent.getPreferredSize().getWidth();

				maxWidth = (int) Math.max(maxWidth, valueWidth);
			}
		}

		return maxWidth;
	}

	private static int findLargestIndex(int[] widths) {
		int largestIndex = 0;
		int largestValue = 0;

		for (int i = 0; i < widths.length; i++) {
			if (widths[i] > largestValue) {
				largestIndex = i;
				largestValue = widths[i];
			}
		}

		return largestIndex;
	}

	private static int sum(int[] widths) {
		int sum = 0;

		for (int width : widths) {
			sum += width;
		}

		return sum;
	}
}