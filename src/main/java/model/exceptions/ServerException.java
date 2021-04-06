package model.exceptions;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Exception en provenance du server
 *
 */
public class ServerException extends RuntimeException {

    private Set<InformationException> informationExceptionSet;

    /**
     * Constructeur
     */
    public ServerException() {
        super("Server Error");
        this.informationExceptionSet = new HashSet<>();
    }

    /**
     * Permet d'ajouter une information sur la cause de l'exception
     * @param informationException une information sur la cause de l'exception
     * @return L'exception en cours
     */
    public ServerException addInformationException(InformationException informationException) {
        this.informationExceptionSet.add(informationException);
        return this;
    }

    /**
     * Permet de se procurer la liste des informations sur l'exception
     * @return la liste des informations sur l'exception
     */
    public Set<InformationException> getInformationExceptionSet() {
        return informationExceptionSet;
    }

    @Override
    public String toString() {
        return "ServerException{" +
                "informationExceptionSet=" + informationExceptionSet +
                '}';
    }
}
