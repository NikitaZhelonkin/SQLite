package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;
import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 29.03.17.
 */



@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn(primaryKey = true)
    private long mId;

    @SQLiteColumn
    private String mName;

    @SQLiteColumn
    private int mAge;

    @SQLiteColumn
    private boolean mBoolean;

    public Dog(){

    }

    public Dog(long id){
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public void setBoolean(boolean aBoolean) {
        mBoolean = aBoolean;
    }

    public boolean getBoolean() {
        return mBoolean;
    }
}
