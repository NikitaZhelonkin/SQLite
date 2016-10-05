# SQLite

Database library for Android based on SQLite

### Gradle:

```groovy
compile 'ru.nikitazhelonkin:sqlite:1.0'
```

### Usage:

Create tables by implementing ```Table``` interface:

```java
public class DogTable implements Table<Dog> {

    public static final DogTable TABLE = new DogTable();

    private static final String TABLE_NAME = "dog_table";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE = "age";

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableBuilder.create(this)
                .add(Column.integer(ID).primaryKey())
                .add(Column.text(NAME))
                .add(Column.integer(AGE))
                .toSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + getName());
        onCreate(db);
    }

    @Override
    public ContentValues toContentValues(Dog dog) {
        ContentValues values = new ContentValues();
        values.put(ID, dog.getId());
        values.put(NAME, dog.getName());
        values.put(AGE, dog.getAge());
        return values;
    }

    @Override
    public Dog fromCursor(Cursor cursor) {
        Dog dog = new Dog();
        dog.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        dog.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        dog.setAge(cursor.getInt(cursor.getColumnIndex(AGE)));
        return dog;
    }
}
```

Then override SQLiteHelper and register all tables:

```java
public class MySQLiteHelper extends SQLiteHelper {

    private static final String DATABASE_NAME = "database_name";

    private static final int VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

        registerTable(DogTable.TABLE);
    }
}
```

Then create instance of your SQLiteHelper using dependency injection or make it singleton:

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MySQLiteHelper sqLiteHelper = MySQLiteHelper.get(this);
    }
}
```

### Query

```java
Dog guffy = sqLiteHelper.queryFirst(DogTable.TABLE, Selection.create().equals(DogTable.NAME, "Guffy" ));

List<Dog> allDogs = sqLiteHelper.query(DogTable.TABLE);

List<Dog> puppies  = sqLiteHelper.query(DogTable.TABLE, Selection.create().lessThanOrEquals(DogTable.AGE, 1));
``````

### Insert

```java
Dog dog = new Dog(1, "Guffy", 1);
sqLiteHelper.insert(DogTable.TABLE, dog);
```

### Delete

```java
sqLiteHelper.delete(DogTable.TABLE, Selection.create().equals(DogTable.ID, dog.getId()));
```

### Update

```java
sqLiteHelper.update(DogTable.TABLE, Selection.create().equals(DogTable.ID, dog.getId()), dog);
```
