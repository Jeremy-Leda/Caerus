package model.analyze;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.analyze.interfaces.IWriteText;

/**
 * 
 * Classe permettant de fournir les méthodes nécessaire à l'écriture d'un fichier
 * 
 * @author jerem
 *
 */
public class Writer implements IWriteText {

	private FileChannel channel;
	private FileOutputStream fout;
	private static Logger logger = LoggerFactory.getLogger(Writer.class);
	
	
	
	/**
	 * Créer un nouveau channel d'écriture
	 * @param path chemin de stockage
	 * @param fileName nom du fichier avec extension
	 * @throws FileNotFoundException 
	 */
	public Writer(File path, String fileName) throws FileNotFoundException {
		this(new File(path, fileName));
	}
	
	/**
	 * Créer un nouveau channel d'écriture
	 * @param path chemin complet
	 * @throws FileNotFoundException
	 */
	public Writer(Path path) throws FileNotFoundException {
		this(path.toFile());
	}
	
	/**
	 * Créer un nouveau channel d'écriture
	 * @param file fichier
	 * @throws FileNotFoundException
	 */
	public Writer(File file) throws FileNotFoundException {
		logger.debug("[DEBUT] Writer");
		if (file.exists()) {
			file.delete();
		}
		fout = new FileOutputStream(file);
		channel = fout.getChannel();
		logger.debug("[FIN] Writer");
	}

	
	@Override
	public void writeLine(String line) throws IOException {
		ByteBuffer buff = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
		channel.write(buff);
	}

	@Override
	public void writeLineWithBreakLineAfter(String line) throws IOException {
		writeLine(line);
		addBreakLine();
	}

	@Override
	public void addBreakLine() throws IOException {
		writeLine(System.lineSeparator());
	}

	
	
	/**
	 * Permet de fermer le fichier
	 * @throws IOException Erreur d'entrée sortie
	 */
	@Override
	public void close() throws IOException {
		logger.debug("[DEBUT] close");
		channel.close();
		fout.close();
		logger.debug("[FIN] close");
	}
	
}
