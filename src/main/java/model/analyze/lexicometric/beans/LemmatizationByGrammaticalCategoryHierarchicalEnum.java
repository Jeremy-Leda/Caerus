package model.analyze.lexicometric.beans;

import model.exceptions.ErrorCode;
import model.exceptions.InformationException;
import model.exceptions.InformationExceptionBuilder;
import model.exceptions.ServerException;
import view.beans.LemmatizationByGrammaticalCategoryHierarchicalEditEnum;
import view.beans.LemmatizationHierarchicalEditEnum;

import java.util.Arrays;

/**
 * Enumeration pour la gestion de la hiérarchie concernant les lemmes
 */
public enum LemmatizationByGrammaticalCategoryHierarchicalEnum {
    CATEGORY(LemmatizationByGrammaticalCategoryHierarchicalEditEnum.CATEGORY),
    BASE(LemmatizationByGrammaticalCategoryHierarchicalEditEnum.BASE),
    LEMME(LemmatizationByGrammaticalCategoryHierarchicalEditEnum.LEMME);

    private final LemmatizationByGrammaticalCategoryHierarchicalEditEnum lemmatizationByGrammaticalCategoryHierarchicalEditEnum;

    LemmatizationByGrammaticalCategoryHierarchicalEnum(LemmatizationByGrammaticalCategoryHierarchicalEditEnum lemmatizationByGrammaticalCategoryHierarchicalEditEnum) {
        this.lemmatizationByGrammaticalCategoryHierarchicalEditEnum = lemmatizationByGrammaticalCategoryHierarchicalEditEnum;
    }

    /**
     * Permet de se procurer la valeur de {@link LemmatizationByGrammaticalCategoryHierarchicalEnum} en fonction de {@link LemmatizationByGrammaticalCategoryHierarchicalEditEnum}
     * @param value valeur de {@link LemmatizationByGrammaticalCategoryHierarchicalEditEnum}
     * @return la valeur de {@link LemmatizationByGrammaticalCategoryHierarchicalEnum}
     */
    public static LemmatizationByGrammaticalCategoryHierarchicalEnum getLemmatizationByGrammaticalCategoryHierarchicalEnumFromLemmatizationByGrammaticalCategoryHierarchicalEditEnum(LemmatizationByGrammaticalCategoryHierarchicalEditEnum value) {
        return Arrays.stream(values())
                .filter(e -> e.lemmatizationByGrammaticalCategoryHierarchicalEditEnum.equals(value))
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
