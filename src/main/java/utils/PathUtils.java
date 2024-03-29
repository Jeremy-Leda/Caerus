package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import view.Main;

/**
 * 
 * Classe utilitaire pour les chemin
 * 
 * @author jerem
 *
 */
public class PathUtils {

	private static final String GLOBAL_FOLDER = "Caerus";
	
	/**
	 * Permet de se procurer le chemin root
	 * @return le chemin root
	 */
	public static String getRootPath() {
		File currentJavaJarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());   
		String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath().replaceAll("%20", " ");		
		String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");
		return currentRootDirectoryPath;
	}
	
	/**
	 * Permet de cr�er un repertoire s'il n'existe pas
	 * @param root chemin root
	 * @param folder dossier � cr��er
	 * @return le fichier
	 */
	public static File addFolderAndCreate(String root, String folder) {
		File file = new File(new File(root), folder);
		file.mkdirs();
		return file;
	}
	
	/**
	 * Effectue la suppression du fichier pass� en param�tre
	 * @param fileToDelete fichier � supprimer
	 */
	public static void deleteFile(File fileToDelete) {
		if (fileToDelete.exists()) {
			fileToDelete.delete();
		}
	}
	
	/**
	 * Permet de copier un fichier
	 * @param oldFile ancien fichier
	 * @param newFile nouveau fichier
	 * @throws IOException Erreur d'entr�e sortie
	 */
	public static void copyFile(File oldFile, File newFile) throws IOException {
		Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Permet de d�placer un fichier dans un nouveau repertoire
	 * @param fileToMove fichier � d�placer
	 * @param newDirectory nouveau repertoire
	 * @throws IOException
	 */
	public static Path moveFile(File fileToMove, File newDirectory) throws IOException {
		File newFile = new File(newDirectory.getAbsolutePath(), fileToMove.getName());
		return Files.move(fileToMove.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Permet de se procurer le chemin du dossier Caerus dans les documents de l'utilisateur
	 * @return le dossier Caerus dans les Documents
	 */
	public static String getCaerusFolder() {
		return System.getProperty("user.home") + File.separator + "Documents" + File.separator + GLOBAL_FOLDER;
	}
}
