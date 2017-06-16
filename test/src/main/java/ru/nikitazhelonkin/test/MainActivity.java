package ru.nikitazhelonkin.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import ru.nikitazhelonkin.sqlite.Selection;

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
        long id = databaseHelper.insert(DogTable.INSTANCE, new Dog("name1", 1, dogOwnerId));

        databaseHelper.delete(DogOwnerTable.INSTANCE, Selection.create().equals(DogOwnerTable.ID, dogOwnerId));

        List<Dog> dogs = databaseHelper.query(DogTable.INSTANCE, Selection.create()
                .columns(new String[]{"*", String.format("MAX(%s) as %s", "age", "age")})
                .groupBy(DogTable.NAME));

        Toast.makeText(this, dogs.size(), Toast.LENGTH_SHORT).show();
    }
}
