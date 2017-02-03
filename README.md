# SQLite

Database library for Android based on SQLite

### Gradle:

```groovy
compile 'ru.nikitazhelonkin:sqlite:1.1'
```

### Usage:

Just annotate you model class as in the example below:

```java
@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn(type = SQLiteColumn.INTEGER, primaryKey = true)
    private long mId;

    @SQLiteColumn(type = SQLiteColumn.TEXT)
    private String mName;

    @SQLiteColumn(type = SQLiteColumn.INTEGER)
    private int mAge;

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
```
Annotation Processor will generate ```DogTable``` class;

Then override SQLiteHelper and register all tables:

```java
public class MySQLiteHelper extends SQLiteHelper {

    private static final String DATABASE_NAME = "database_name";

    private static final int VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);

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
