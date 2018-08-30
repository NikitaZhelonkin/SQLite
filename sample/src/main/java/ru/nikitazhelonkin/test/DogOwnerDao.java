package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.Dao;
import ru.nikitazhelonkin.sqlite.SQLiteDatabaseProvider;

class DogOwnerDao extends Dao<DogOwner> {

    DogOwnerDao(SQLiteDatabaseProvider sqLiteDatabaseProvider) {
        super(sqLiteDatabaseProvider, DogOwnerTable.INSTANCE);
    }
}
