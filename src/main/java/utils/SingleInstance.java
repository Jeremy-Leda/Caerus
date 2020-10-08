package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Permet de v�rifier la pr�sence d'une seule instance de l'application
 * Utilise un lock sur un fichier dans un r�pertoire temporaire
 * 
 * @author jerem
 *
 */
public class SingleInstance {
	private static Logger log = LoggerFactory.getLogger(SingleInstance.class);
	private static final String LOCK_FILE = "caerus_lock";
	private static RandomAccessFile randomAccessFileLock;
	private static FileChannel fileChannelLock;
	private static FileLock fileLock;

	/**
	 * Permet de cr�er l'instance
	 * @return Vrai si l'instance est unique, Faux si une instance existe d�j�
	 */
	public static Boolean createInstance() {
		try {
			File lockFile = getAndCreateIfNecessaryLockFile();
			return acquireLockOnFile(lockFile);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Permet de cr�er le fichier � locker
	 * @return le fichier
	 * @throws IOException erreur � la cr�ation du fichier
	 */
	private static File getAndCreateIfNecessaryLockFile() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(PathUtils.getTempFolder());
		sb.append("//").append(LOCK_FILE);
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * Permet de se procurer le lock sur le fichier
	 * @param fileToLock le fichier a lock�
	 * @return Vrai si le lock a �t� acquis, Faux sinon
	 */
	private static Boolean acquireLockOnFile(File fileToLock) {
		try {
			randomAccessFileLock = new RandomAccessFile(fileToLock.getAbsolutePath(), "rw");
			fileChannelLock = randomAccessFileLock.getChannel();
			fileLock = fileChannelLock.tryLock();
			return Objects.nonNull(fileLock);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Permet de fermer et d�truire le fichier lock�
	 */
	public static void closeAndDeleteLockFile() {
		try {
			fileLock.close();
			fileChannelLock.close();
			randomAccessFileLock.close();
			getAndCreateIfNecessaryLockFile().delete();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
	}

}
