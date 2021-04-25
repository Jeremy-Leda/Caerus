package model.analyze.lexicometric.beans;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.TokenizationHierarchicalEditEnum;

import java.util.Arrays;

/**
 * Enumeration pour la gestion de la hiérarchie concernant les token
 */
public enum TokenizationHierarchicalEnum {
    BASE(TokenizationHierarchicalEditEnum.BASE);

    private final TokenizationHierarchicalEditEnum tokenizationHierarchicalEditEnum;

    TokenizationHierarchicalEnum(TokenizationHierarchicalEditEnum tokenizationHierarchicalEditEnum) {
        this.tokenizationHierarchicalEditEnum = tokenizationHierarchicalEditEnum;
    }

    /**
     * Permet de se procurer la valeur de {@link TokenizationHierarchicalEnum} en fonction de {@link TokenizationHierarchicalEditEnum}
     * @param value valeur de {@link TokenizationHierarchicalEditEnum}
     * @return la valeur de {@link TokenizationHierarchicalEnum}
     */
    public static TokenizationHierarchicalEnum getTokenizationHierarchicalEnumFromTokenizationHierarchicalEditEnum(TokenizationHierarchicalEditEnum value) {
        return Arrays.stream(values())
                .filter(e -> e.tokenizationHierarchicalEditEnum.equals(value))
                .findFirst()
                .orElseThrow(() -> {
                    InformationException informationException = new InformationExceptionBuilder()
                            .errorCode(ErrorCode.TECHNICAL_ERROR)
                            .objectInError(value)
                            .build();
                    return new ServerException().addInformationException(informationException);
                });
    }
}
