package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.BaseDao;
import ru.nikitazhelonkin.sqlite.SQLiteHelper;

public class DogDao extends BaseDao<Dog> {

    public DogDao(SQLiteHelper helper) {
        super(helper, DogTable.INSTANCE);
    }
}
