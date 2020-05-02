package ihm.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;

import ihm.interfaces.IFilePickerPanel;
import ihm.utils.ConfigurationUtils;
import ihm.utils.Constants;

/**
 * 
 * Permet d'afficher un panel pour choisir le fichier
 * 
 * @author jerem
 *
 */
public class FilePickerPanel implements IFilePickerPanel {

	private final JPanel filePickerPanel;
    private JLabel label;
    private JTextField textField;
    private JButton button;
    private Consumer<?> consumerChooseFileOk;
     
    private JFileChooser fileChooser;
	
	public FilePickerPanel(String titlePanel) {
		this.filePickerPanel = new JPanel();
		this.filePickerPanel.setBorder(
				BorderFactory.createTitledBorder(titlePanel));
		this.label = new JLabel(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_PANEL_LABEL));
		this.button = new JButton(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_FILE_PICKER_PANEL_BUTTON));
		this.button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonActionPerformed();
			}
		});
		this.textField = new JTextField(30);
		this.textField.setEnabled(false);
		this.filePickerPanel.add(this.label);
		this.filePickerPanel.add(this.textField);
		this.filePickerPanel.add(this.button);
		this.fileChooser = new JFileChooser();
		this.fileChooser.setDialogTitle(titlePanel);
	}
	
	@Override
	public JComponent getJPanel() {
		return this.filePickerPanel;
	}

	@Override
	public String getFile() {
		return this.textField.getText();
	}

	@Override
	public void addConsumerOnChooseFileOk(Consumer<?> consumer) {
		this.consumerChooseFileOk = consumer;
	}
	
	/**
	 * Permet d'activer l'affichage de la fenêtre pour choisir le fichier
	 */
	private void buttonActionPerformed() {
        if (fileChooser.showOpenDialog(getJPanel()) == JFileChooser.APPROVE_OPTION) {
        	String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
        	String extension = FilenameUtils.getExtension(absolutePath);
        	if (!"xls".equals(extension)) {
        		StringBuilder sb = new StringBuilder(absolutePath);
        		sb.append(".xls");
        		absolutePath = sb.toString();
        	}
            textField.setText(absolutePath);
            if (null != this.consumerChooseFileOk) {
            	this.consumerChooseFileOk.accept(null);
            }
        }
    }


}
