package ru.nikitazhelonkin.sqlite;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private String mName;
    private List<Column> mColumns;

    private TableBuilder(String name) {
        mName = name;
        mColumns = new ArrayList<>();
    }

    public static TableBuilder create(Table table) {
        return new TableBuilder(table.getName());
    }

    public TableBuilder add(Column column) {
        mColumns.add(column);
        return this;
    }

    public String toSql() {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(mName).append(" ");
        for (int i = 0; i < mColumns.size(); i++) {
            if (i == 0) builder.append(" (");
            builder.append(mColumns.get(i).toSql());
            if (i == mColumns.size() - 1) builder.append(");");
            else builder.append(", ");
        }
        return builder.toString();
    }


}
