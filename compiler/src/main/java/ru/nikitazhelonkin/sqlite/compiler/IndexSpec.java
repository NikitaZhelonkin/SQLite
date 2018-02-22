package ru.nikitazhelonkin.sqlite.compiler;


public class IndexSpec {

    private final String mName;
    private final String[] mValues;
    private final boolean mUnique;

    public IndexSpec(String name, String[] values, boolean unique){
        mName = name;
        mValues = values;
        mUnique = unique;
    }

    public String toSql(String tableName){
        StringBuilder builder = new StringBuilder("CREATE");
        String columns = String.join(",", mValues);
        if(mUnique){
            builder.append(" UNIQUE");
        }
        builder.append(" INDEX ").append(mName).append(" ON ").append(tableName);
        builder.append(" (").append(columns).append(");");
        return builder.toString();
    }
}
