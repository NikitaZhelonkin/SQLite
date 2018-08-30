package ru.nikitazhelonkin.test;

import java.util.List;

import ru.nikitazhelonkin.sqlite.Dao;
import ru.nikitazhelonkin.sqlite.SQLiteDatabaseProvider;
import ru.nikitazhelonkin.sqlite.Selection;

class DogDao extends Dao<Dog> {

    DogDao(SQLiteDatabaseProvider sqLiteDatabaseProvider) {
        super(sqLiteDatabaseProvider, DogTable.INSTANCE);
    }

    public List<Dog> getByAge(int age){
        return query(Selection.create().equals(DogTable.AGE, age));
    }
}
