package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.BaseDao;
import ru.nikitazhelonkin.sqlite.SQLiteHelper;

public class DogOwnerDao extends BaseDao<DogOwner> {

    public DogOwnerDao(SQLiteHelper helper) {
        super(helper, DogOwnerTable.INSTANCE);
    }
}
