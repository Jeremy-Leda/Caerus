package model.analyze.lexicometric.beans;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.LemmatizationHierarchicalEditEnum;

import java.util.Arrays;

/**
 * Enumeration pour la gestion de la hiérarchie concernant les lemmes
 */
public enum LemmatizationHierarchicalEnum {
    BASE(LemmatizationHierarchicalEditEnum.BASE),
    LEMME(LemmatizationHierarchicalEditEnum.LEMME);

    private final LemmatizationHierarchicalEditEnum lemmatizationHierarchicalEditEnum;

    LemmatizationHierarchicalEnum(LemmatizationHierarchicalEditEnum lemmatizationHierarchicalEditEnum) {
        this.lemmatizationHierarchicalEditEnum = lemmatizationHierarchicalEditEnum;
    }

    /**
     * Permet de se procurer la valeur de {@link LemmatizationHierarchicalEnum} en fonction de {@link LemmatizationHierarchicalEditEnum}
     * @param value valeur de {@link LemmatizationHierarchicalEditEnum}
     * @return la valeur de {@link LemmatizationHierarchicalEnum}
     */
    public static LemmatizationHierarchicalEnum getLemmatizationHierarchicalEnumFromLemmatizationHierarchicalEditEnum(LemmatizationHierarchicalEditEnum value) {
        return Arrays.stream(values())
                .filter(e -> e.lemmatizationHierarchicalEditEnum.equals(value))
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
