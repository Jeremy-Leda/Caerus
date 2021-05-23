package view.beans;

import model.analyze.lexicometric.interfaces.ILexicometricHierarchical;
import view.interfaces.IRootTable;
import view.utils.ConfigurationUtils;
import view.utils.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enumeration pour la hiérarchie des lemmes par catégorie
 * Utilise l'ordre naturel pour l'affichage (Haut -> Bas = Gauche -> Droite)
 */
public enum LemmatizationByGrammaticalCategoryHierarchicalEditEnum implements ILexicometricHierarchical<LemmatizationByGrammaticalCategoryHierarchicalEditEnum>, IRootTable {
    CATEGORY(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_TABLE_HEADER_LABEL), true, 0, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_CATEGORY_FILTER_LABEL)),
    BASE(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_TABLE_HEADER_LABEL), false, 1, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_FILTER_LABEL)),
    LEMME(ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_TABLE_HEADER_LABEL), false, 2, ConfigurationUtils.getInstance().getDisplayMessage(Constants.WINDOW_MANAGE_RADICALS_BY_CLASS_VARIATION_FILTER_LABEL));

    private final String label;
    private final Boolean isRoot;
    private final Integer hiearchicalOrder;
    private final String labelFilter;

    LemmatizationByGrammaticalCategoryHierarchicalEditEnum(String label, Boolean isRoot, Integer hiearchicalOrder, String labelFilter) {
        this.label = label;
        this.isRoot = isRoot;
        this.hiearchicalOrder = hiearchicalOrder;
        this.labelFilter = labelFilter;
    }

    @Override
    public Map<LemmatizationByGrammaticalCategoryHierarchicalEditEnum, Integer> getHierarchicalIntegerMap() {
        return Arrays.stream(values()).collect(Collectors.toMap(e -> e, e -> e.ordinal()));
    }

    @Override
    public String getHeaderLabel() {
        return this.label;
    }

    @Override
    public String getFilterLabel() {
        return this.labelFilter;
    }

    @Override
    public Boolean isRoot() {
        return this.isRoot;
    }

    @Override
    public Integer hierarchicalOrder() {
        return this.hiearchicalOrder;
    }

    @Override
    public Integer displayOrder() {
        return super.ordinal();
    }
}
