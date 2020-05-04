package ihm.view;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import ihm.controler.IConfigurationControler;

public class SaveConfiguration extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2933572236620130426L;
	private final IConfigurationControler controler;

	public SaveConfiguration(IConfigurationControler configurationControler) {
		this.controler = configurationControler;
		if (isLoaded()) {
			fileChoose();
		}
	}
	
	public Boolean isLoaded() {
		return !this.controler.getListOfStructuredFile().isEmpty();
	}

	private void fileChoose() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Fichier excel à créer");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
		chooser.addChoosableFileFilter(filter);
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getSelectedFile(): " + chooser.getSelectedFile());
			//this.controler.analyzePath(chooser.getCurrentDirectory());
			this.controler.createExcel(chooser.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}
	}

}
