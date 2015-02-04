package net.emaze.tinytypes.hibernate.queries;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;

public class TinyTypesRestrictions {

    public static Criterion ilike(String propertyName, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return ilike(propertyName, value.toString(), MatchMode.EXACT);
    }

    public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
        if (value == null) {
            throw new IllegalArgumentException("Comparison value passed to ilike cannot be null");
        }
        return new TinyTypeLikeExpression(propertyName, value, matchMode, null, true);
    }

}
