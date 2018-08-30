package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.Dao;
import ru.nikitazhelonkin.sqlite.SQLiteDatabaseProvider;

class DogDao extends Dao<Dog> {

    DogDao(SQLiteDatabaseProvider sqLiteDatabaseProvider) {
        super(sqLiteDatabaseProvider, DogTable.INSTANCE);
    }
}
