package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;
import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 16.06.17.
 */

@SQLiteObject("dog_owner_table")
public class DogOwner {

    @SQLiteColumn(primaryKey = true, autoincrement = true)
    private long mId;

    @SQLiteColumn
    private int mAnotherColumn;

    @SQLiteColumn
    private boolean mIsPremium;

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setAnotherColumn(int anotherColumn) {
        mAnotherColumn = anotherColumn;
    }

    public int getAnotherColumn() {
        return mAnotherColumn;
    }

    public void setPremium(boolean premium) {
        mIsPremium = premium;
    }

    public boolean isPremium() {
        return mIsPremium;
    }
}
