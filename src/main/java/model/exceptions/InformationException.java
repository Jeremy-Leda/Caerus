package model.exceptions;

import model.PojoBuilder;
import net.karneim.pojobuilder.GeneratePojoBuilder;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Classe contenant les informations pour une exceptions
 */
@PojoBuilder
public class InformationException {

    @NotNull(message = "ERROR_CODE_NULL")
    private ErrorCode errorCode;
    private Set<String> parameters;
    private Object objectInError;
    private StackTraceElement[] stackTraceElements;
    private Exception exceptionParent;
    private List<String> messageParameters;

    /**
     * Permet de se procurer le code de l'erreur
     * @return le code de l'erreur
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Permet de définir le code de l'erreur
     * @param errorCode code de l'erreur
     */
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Permet se procurer les paramètres de l'erreur
     * @return Les paramètres de l'erreur
     */
    public Set<String> getParameters() {
        return parameters;
    }

    /**
     * Permet de définir les paramètres de l'erreur
     * @param parameters les paramètres de l'erreur
     */
    public void setParameters(Set<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Permet de se procurer l'objet en erreur
     * @return l'objet en erreur
     */
    public Object getObjectInError() {
        return objectInError;
    }

    /**
     * Permet de définir l'objet en erreur
     * @param objectInError l'objet en erreur
     */
    public void setObjectInError(Object objectInError) {
        this.objectInError = objectInError;
    }

    /**
     * Permet de se procurer la stack trace de l'erreur d'origine
     * @return la stack trace de l'erreur d'origine
     */
    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    /**
     * Permet de définir la stack trace de l'erreur d'origine
     * @param stackTraceElements la stack trace de l'erreur d'origine
     */
    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    /**
     * Permet de se procurer l'exception parente si présente
     * @return l'exception parente si présente
     */
    public Exception getExceptionParent() {
        return exceptionParent;
    }

    /**
     * Permet de définir l'exception parente
     * @param exceptionParent l'exception parente
     */
    public void setExceptionParent(Exception exceptionParent) {
        this.exceptionParent = exceptionParent;
    }

    /**
     * Permet de se procurer les paramètres pour le message
     * @return les paramètres
     */
    public List<String> getMessageParameters() {
        return messageParameters;
    }

    /**
     * Permet de définir les paramètres pour le message
     * @param messageParameters les paramètres pour le message
     */
    public void setMessageParameters(List<String> messageParameters) {
        this.messageParameters = messageParameters;
    }

    @Override
    public String toString() {
        return "InformationException{" +
                "errorCode=" + errorCode +
                ", parameters=" + parameters +
                ", objectInError=" + objectInError +
                ", stackTraceElements=" + Arrays.toString(stackTraceElements) +
                ", exceptionParent=" + exceptionParent +
                ", messageParameters=" + messageParameters +
                '}';
    }
}
