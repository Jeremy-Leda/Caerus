package model.validation;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Validateur générique pour la validation des Pojo
 *
 */
public class GenericPojoValidator {

    private final ValidatorFactory factory;
    private final Validator validator;

    /**
     * Constructeur
     */
    public GenericPojoValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    /**
     *
     * Validation d'un pojo
     *
     * @param objectToValidate pojo à valider
     * @param <T> Type du pojo
     */
    public <T extends Object> void validate(T objectToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        if (violations.isEmpty()) {
            return;
        }
        InformationException informationException = new InformationExceptionBuilder()
                .errorCode(ErrorCode.TECHNICAL_ERROR)
                .objectInError(objectToValidate)
                .parameters(violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet()))
                .build();
        throw new ServerException().addInformationException(informationException);
    }

}
