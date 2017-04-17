package ru.nikitazhelonkin.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 03.04.17.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        for (int i = 0; i < 15; i++) {
            long id = databaseHelper.insert(DogTable.INSTANCE, new Dog());
            Log.e("TAG", String.valueOf(id));
        }
    }
}
