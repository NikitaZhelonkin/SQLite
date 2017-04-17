# SQLite

Database library for Android based on SQLite

### Gradle:

```groovy
compile 'ru.nikitazhelonkin:sqlite:1.5'
compile 'ru.nikitazhelonkin:sqlite-compiler:1.5'
```

### Usage:

Just annotate you model class as in the example below:

```java
@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn(primaryKey = true, autoincrement = true)
    private long mId;

    @SQLiteColumn(unique = true, notnull = true)
    private String mName;

    @SQLiteColumn
    private int mAge;

    public Dog(String name) {
        mName = name;
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
```
Annotation Processor will generate ```DogTable``` class;

Then override SQLiteHelper and register all tables:

```java
public class MySQLiteHelper extends SQLiteHelper {

    private static final String DATABASE_NAME = "database_name";

    private static final int VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);

        registerTable(DogTable.INSTANCE);
    }
}
```

Then create instance of your SQLiteHelper using dependency injection or make it singleton:

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MySQLiteHelper helper = MySQLiteHelper.get(this);
    }
}
```

### Query

```java
Dog guffy = helper.queryFirst(DogTable.INSTANCE, Selection.create().equals(DogTable.NAME, "Guffy" ));

List<Dog> allDogs = helper.query(DogTable.INSTANCE);

List<Dog> puppies  = helper.query(DogTable.INSTANCE, Selection.create().lessThanOrEquals(DogTable.AGE, 1));
``````

### Insert

```java
Dog dog = new Dog(1, "Guffy", 1);
helper.insert(DogTable.INSTANCE, dog);
```

### Delete

```java
helper.delete(DogTable.INSTANCE, Selection.create().equals(DogTable.ID, dog.getId()));
```

### Update

```java
helper.update(DogTable.INSTANCE, Selection.create().equals(DogTable.ID, dog.getId()), dog);
```
