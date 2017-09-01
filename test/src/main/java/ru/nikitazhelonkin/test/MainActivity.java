package ru.nikitazhelonkin.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by nikita on 03.04.17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.delete(DogTable.INSTANCE);
        databaseHelper.delete(DogOwnerTable.INSTANCE);

        long dogOwnerId = databaseHelper.insert(DogOwnerTable.INSTANCE, new DogOwner());

        Dog dog = new Dog();
        dog.setName("name");
        dog.setDogOwnerId(dogOwnerId);
        dog.setArray(null);
        dog.setId(databaseHelper.insert(DogTable.INSTANCE, dog));
    }
}
