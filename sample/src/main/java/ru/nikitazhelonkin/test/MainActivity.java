package ru.nikitazhelonkin.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nikita on 03.04.17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        DogDao dogDao = new DogDao(databaseHelper);
        DogOwnerDao dogOwnerDao = new DogOwnerDao(databaseHelper);

        long dogOwnerId = dogOwnerDao.insert(new DogOwner());

        Dog dog = new Dog();
        dog.setName("name");
        dog.setDogOwnerId(dogOwnerId);
        dog.setArray(null);
        dog.setId(dogDao.insert(dog));
    }
}
