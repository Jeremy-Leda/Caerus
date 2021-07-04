package model.analyze.lexicometric.beans;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.ProperNounHierarchicalEditEnum;
import view.beans.TokenizationHierarchicalEditEnum;

import java.util.Arrays;

/**
 * Enumeration pour la gestion de la hiérarchie concernant les noms propres
 */
public enum ProperNounHierarchicalEnum {
    BASE(ProperNounHierarchicalEditEnum.BASE);

    private final ProperNounHierarchicalEditEnum properNounHierarchicalEditEnum;

    ProperNounHierarchicalEnum(ProperNounHierarchicalEditEnum tokenizationHierarchicalEditEnum) {
        this.properNounHierarchicalEditEnum = tokenizationHierarchicalEditEnum;
    }

    /**
     * Permet de se procurer la valeur de {@link ProperNounHierarchicalEnum} en fonction de {@link ProperNounHierarchicalEditEnum}
     * @param value valeur de {@link ProperNounHierarchicalEditEnum}
     * @return la valeur de {@link ProperNounHierarchicalEnum}
     */
    public static ProperNounHierarchicalEnum getProperNounHierarchicalEnumFromProperNounHierarchicalEditEnum(ProperNounHierarchicalEditEnum value) {
        return Arrays.stream(values())
                .filter(e -> e.properNounHierarchicalEditEnum.equals(value))
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
