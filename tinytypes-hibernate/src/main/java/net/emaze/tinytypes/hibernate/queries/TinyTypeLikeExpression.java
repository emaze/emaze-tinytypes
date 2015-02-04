package net.emaze.tinytypes.hibernate.queries;

import net.emaze.tinytypes.StringTinyType;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.StringType;

public class TinyTypeLikeExpression extends LikeExpression {
    private final Object value;
    private final String propertyName;
    private final boolean ignoreCase;

    protected TinyTypeLikeExpression(String propertyName, String value, Character escapeChar, boolean ignoreCase) {
        super(propertyName, value, escapeChar, ignoreCase);
        this.value = value;
        this.propertyName = propertyName;
        this.ignoreCase = ignoreCase;
    }

    protected TinyTypeLikeExpression(String propertyName, String value, MatchMode matchMode, Character escapeChar, boolean ignoreCase) {
        this(propertyName, matchMode.toMatchString(value), escapeChar, ignoreCase);
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        final String matchValue = ignoreCase ? value.toString().toLowerCase() : value.toString();
        final TypedValue trueTypedValue = criteriaQuery.getTypedValue(criteria, propertyName, matchValue);
        final Class<?> returnedClass = trueTypedValue.getType().getReturnedClass();
        if(!StringTinyType.class.isAssignableFrom(returnedClass)){
            throw new IllegalStateException(String.format("Criterion can only be applied to a StringTinyType, found type is: %s", returnedClass.getName()));
        }
        return new TypedValue[]{new TypedValue(new StringType(), matchValue)};
    }

}
