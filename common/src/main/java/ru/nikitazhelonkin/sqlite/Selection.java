package ru.nikitazhelonkin.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Selection {

    private static final String EQ = "=?";
    private static final String PAREN_OPEN = "(";
    private static final String PAREN_CLOSE = ")";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String IS_NULL = " IS NULL";
    private static final String IS_NOT_NULL = " IS NOT NULL";
    private static final String IN = " IN (";
    private static final String NOT_IN = " NOT IN (";
    private static final String COMMA = ",";
    private static final String GT = ">?";
    private static final String LT = "<?";
    private static final String GT_EQ = ">=?";
    private static final String LT_EQ = "<=?";
    private static final String NOT_EQ = "<>?";
    private static final String LIKE = " LIKE ?";
    private static final String CONTAINS = " LIKE '%' || ? || '%'";
    private static final String STARTS = " LIKE ? || '%'";
    private static final String ENDS = " LIKE '%' || ?";
    private static final String DESC = " DESC";

    private StringBuilder mSelection ;
    private List<String> mSelectionArgs;
    private StringBuilder mOrderBy;
    private String mGroupBy;
    private String mHaving;
    private String mLimit;
    private String[] mColumns;

    public static Selection create(){
        return new Selection();
    }

    private Selection(){
        mSelection = new StringBuilder();
        mSelectionArgs = new ArrayList<>();
        mOrderBy = new StringBuilder();

    }

    String selection() {
        return mSelection.toString();
    }

    String[] args() {
        int size = mSelectionArgs.size();
        if (size == 0) return null;
        return mSelectionArgs.toArray(new String[size]);
    }

    String groupBy() {
        return mGroupBy;
    }

    String having() {
        return mHaving;
    }

    String orderBy() {
        return mOrderBy.toString();
    }

    String limit() {
        return mLimit;
    }

    String[] columns(){
        return mColumns;
    }

    public Selection columns(String[] columns){
        mColumns = columns;
        return this;
    }

    public Selection equals(String column, Object... value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NULL);
            } else {
                // Single not null value
                mSelection.append(EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
        return this;
    }

    public Selection notEquals(String column, Object... value) {
        mSelection.append(column);

        if (value == null) {
            // Single null value
            mSelection.append(IS_NOT_NULL);
        } else if (value.length > 1) {
            // Multiple values ('in' clause)
            mSelection.append(NOT_IN);
            for (int i = 0; i < value.length; i++) {
                mSelection.append("?");
                if (i < value.length - 1) {
                    mSelection.append(COMMA);
                }
                mSelectionArgs.add(valueOf(value[i]));
            }
            mSelection.append(PAREN_CLOSE);
        } else {
            // Single value
            if (value[0] == null) {
                // Single null value
                mSelection.append(IS_NOT_NULL);
            } else {
                // Single not null value
                mSelection.append(NOT_EQ);
                mSelectionArgs.add(valueOf(value[0]));
            }
        }
        return this;
    }

    public Selection like(String column, String... values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(LIKE);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
        return this;
    }

    public Selection contains(String column, String... values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(CONTAINS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
        return this;
    }

    public Selection startsWith(String column, String... values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(STARTS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
        return this;
    }

    public Selection endsWith(String column, String... values) {
        mSelection.append(PAREN_OPEN);
        for (int i = 0; i < values.length; i++) {
            mSelection.append(column);
            mSelection.append(ENDS);
            mSelectionArgs.add(values[i]);
            if (i < values.length - 1) {
                mSelection.append(OR);
            }
        }
        mSelection.append(PAREN_CLOSE);
        return this;
    }

    public Selection greaterThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT);
        mSelectionArgs.add(valueOf(value));
        return this;
    }

    public Selection greaterThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(GT_EQ);
        mSelectionArgs.add(valueOf(value));
        return this;
    }

    public Selection lessThan(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT);
        mSelectionArgs.add(valueOf(value));
        return this;
    }

    public Selection lessThanOrEquals(String column, Object value) {
        mSelection.append(column);
        mSelection.append(LT_EQ);
        mSelectionArgs.add(valueOf(value));
        return this;
    }

    public Selection addRaw(String raw, Object... args) {
        mSelection.append(" ");
        mSelection.append(raw);
        mSelection.append(" ");
        for (Object arg : args) {
            mSelectionArgs.add(valueOf(arg));
        }
        return this;
    }

    public Selection addRaw(String raw) {
        mSelection.append(" ");
        mSelection.append(raw);
        mSelection.append(" ");
        return this;
    }

    public Selection and() {
        mSelection.append(AND);
        return this;
    }

    public Selection or() {
        mSelection.append(OR);
        return this;
    }

    public Selection openGroup() {
        mSelection.append(PAREN_OPEN);
        return this;
    }

    public Selection closeGroup() {
        mSelection.append(PAREN_CLOSE);
        return this;
    }

    public Selection groupBy(String... groupBy) {
        if (groupBy != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < groupBy.length; i++) {
                if (i > 0) {
                    stringBuilder.append(COMMA);
                }
                stringBuilder.append(groupBy[i]);
            }
            mGroupBy = stringBuilder.toString();
        }
        return this;
    }

    public Selection having(String having) {
        mHaving = having;
        return this;
    }

    public Selection limit(String limit) {
        mLimit = limit;
        return this;
    }

    public Selection orderBy(String order, boolean desc) {
        if (mOrderBy.length() > 0) mOrderBy.append(COMMA);
        mOrderBy.append(order);
        if (desc) mOrderBy.append(DESC);
        return this;
    }

    public Selection orderBy(String order) {
        return orderBy(order, false);
    }

    public Selection orderBy(String... orders) {
        for (String order : orders) {
            orderBy(order, false);
        }
        return this;
    }


    private String valueOf(Object obj) {
        if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        } else if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        } else if (obj instanceof Enum) {
            return String.valueOf(((Enum<?>) obj).ordinal());
        }
        return String.valueOf(obj);
    }

}

