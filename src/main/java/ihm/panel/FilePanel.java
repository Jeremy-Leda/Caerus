package ihm.panel;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ihm.interfaces.IFilePanel;

/**
 * 
 * Permet de créer le panel pour le fichier
 * 
 * @author jerem
 *
 */
public class FilePanel implements IFilePanel {

	private final JPanel filePanel;
	private final JLabel labelFile;
	private final JLabel valueFile;

	/**
	 * Constructeur
	 */
	public FilePanel() {
		filePanel = new JPanel();
		labelFile = new JLabel();
		valueFile = new JLabel();
		filePanel.add(labelFile);
		filePanel.add(valueFile);
	}
	
	@Override
	public void refresh(String titlePanel, String labelFile, String valueFile) {
		filePanel.setBorder(
				BorderFactory.createTitledBorder(titlePanel));
		this.labelFile.setText(labelFile);
		this.valueFile.setText(valueFile);		
	}

	@Override
	public JComponent getJPanel() {
		return this.filePanel;
	}
	
}
