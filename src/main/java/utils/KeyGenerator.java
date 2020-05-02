package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

/**
 * 
 * Classe permettant de généré une clé unique pour un objet
 * 
 * @author jerem
 *
 */
public final class KeyGenerator {

	/**
	 * Permet de généré uen clé unique
	 */
	public static <T> String generateKey(T object) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(object.toString().getBytes());
			byte[] digest = md.digest();
		    String myChecksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
		    return myChecksum;
		} catch (NoSuchAlgorithmException e) {
			// Impossible de trouvé l'algorithme. On retourne le hashcode
			return new StringBuilder("hash").append(object.hashCode()).toString();
		}
	}
	
}
