package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import view.Main;

import javax.swing.filechooser.FileSystemView;

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
	 * @param folder dossier à créer
	 * @return le fichier
	 */
	public static File addFolderAndCreate(String root, String folder) {
		File file = new File(new File(root), folder);
		file.mkdirs();
		return file;
	}
	
	/**
	 * Effectue la suppression du fichier passé en paramètre
	 * @param fileToDelete fichier à supprimer
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
	 * @throws IOException Erreur d'entrée sortie
	 */
	public static void copyFile(File oldFile, File newFile) throws IOException {
		Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Permet de déplacer un fichier dans un nouveau repertoire
	 * @param fileToMove fichier à déplacer
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
		if (Platform.isWindows()) {
			char[] pszPath = new char[WinDef.MAX_PATH];
			Shell32.INSTANCE.SHGetFolderPath(null,
					ShlObj.CSIDL_MYDOCUMENTS, null, ShlObj.SHGFP_TYPE_CURRENT,
					pszPath);
			return Native.toString(pszPath) + File.separator + GLOBAL_FOLDER;
		}
		return System.getProperty("user.home") + File.separator + GLOBAL_FOLDER;
	}
	
	/**
	 * Permet de se procurer le dossier temporaire du système
	 * @return le dossier temporaire du système
	 */
	public static String getTempFolder() {
		return System.getProperty("java.io.tmpdir");
	}
}
