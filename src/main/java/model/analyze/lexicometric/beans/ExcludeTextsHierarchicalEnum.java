package model.analyze.lexicometric.beans;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.ExcludeTextsHierarchicalEditEnum;

import java.util.Arrays;

/**
 * Enumeration pour la gestion de la hiérarchie concernant les textes à exclure
 */
public enum ExcludeTextsHierarchicalEnum {
    BASE(ExcludeTextsHierarchicalEditEnum.BASE);

    private final ExcludeTextsHierarchicalEditEnum excludeTextsHierarchicalEditEnum;

    ExcludeTextsHierarchicalEnum(ExcludeTextsHierarchicalEditEnum excludeTextsHierarchicalEditEnum) {
        this.excludeTextsHierarchicalEditEnum = excludeTextsHierarchicalEditEnum;
    }

    /**
     * Permet de se procurer la valeur de {@link ExcludeTextsHierarchicalEnum} en fonction de {@link ExcludeTextsHierarchicalEditEnum}
     * @param value valeur de {@link ExcludeTextsHierarchicalEditEnum}
     * @return la valeur de {@link ExcludeTextsHierarchicalEnum}
     */
    public static ExcludeTextsHierarchicalEnum getExcludeTextsHierarchicalEnumFromProperNounHierarchicalEditEnum(ExcludeTextsHierarchicalEditEnum value) {
        return Arrays.stream(values())
                .filter(e -> e.excludeTextsHierarchicalEditEnum.equals(value))
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
