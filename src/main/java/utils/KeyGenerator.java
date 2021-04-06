package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

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
		    String myChecksum = Hex.encodeHexString(digest).toUpperCase();
		    return myChecksum;
		} catch (NoSuchAlgorithmException e) {
			// Impossible de trouvé l'algorithme. On retourne le hashcode
			return new StringBuilder("hash").append(object.hashCode()).toString();
		}
	}
	
}
