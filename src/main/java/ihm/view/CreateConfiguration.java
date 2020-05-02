package ihm.view;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ihm.controler.IConfigurationControler;

public class CreateConfiguration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2509686641214780066L;
	private final IConfigurationControler controler;

	public CreateConfiguration(IConfigurationControler configurationControler) {
		this.controler = configurationControler;
		directoryChoose();
	}
	
	public Boolean isLoaded() {
		return null != this.controler.getListOfStructuredFile();
	}
	
	public Boolean errorProcessing() {
		try {
			return this.controler.errorProcessing();
		} catch (IOException e) {
			System.err.println(e);
			JOptionPane.showMessageDialog(null, "Une erreur s'est produite pendant le traitement des erreurs", "Erreur", JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}

	private void directoryChoose() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choisissez le dossier à analyser");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getSelectedFile(): " + chooser.getSelectedFile());
			//this.controler.analyzePath(chooser.getCurrentDirectory());
			//this.controler.analyzePath(chooser.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}
	}

}
