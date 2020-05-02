package utils;

import java.io.File;

import ihm.Main;

/**
 * 
 * Classe utilitaire pour les chemin
 * 
 * @author jerem
 *
 */
public class PathUtils {

	/**
	 * Permet de se procurer le chemin root
	 * @return le chemin root
	 */
	public static String getRootPath() {
		File currentJavaJarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());   
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();
		String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
		return currentRootDirectoryPath;
	}
	
	/**
	 * Permet de créer un repertoire s'il n'existe pas
	 * @param root chemin root
	 * @param folder dossier à crééer
	 * @return le fichier
	 */
	public static File addFolderAndCreate(String root, String folder) {
		File file = new File(new File(root), folder);
		file.mkdirs();
		return file;
	}
	
	/**
	 * Effectue la suppression du fichier passé en paramétre
	 * @param fileToDelete fichier à supprimer
	 */
	public static void deleteFile(File fileToDelete) {
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}
	}
	
}
