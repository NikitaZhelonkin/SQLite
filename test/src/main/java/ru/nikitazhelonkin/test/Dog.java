package ru.nikitazhelonkin.test;

import ru.nikitazhelonkin.sqlite.annotation.SQLiteColumn;
import ru.nikitazhelonkin.sqlite.annotation.SQLiteObject;

/**
 * Created by nikita on 29.03.17.
 */



@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn(primaryKey = true, autoincrement = true)
    private long mId;

    @SQLiteColumn(notnull = true)
    private String mName;

    @SQLiteColumn
    private int mAge;

    public Dog() {

    }

    public Dog(String name, int age) {
        mName = name;
        mAge = age;
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

}
