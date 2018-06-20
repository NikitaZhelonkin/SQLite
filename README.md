# SQLite

Database library for Android based on SQLite

### Gradle:

```groovy
implementation 'ru.nikitazhelonkin:sqlite:2.0.4'
annotationProcessor 'ru.nikitazhelonkin:sqlite-compiler:2.0.4'
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

    @SQLiteColumn(references = "dog_owner_table(id) ON DELETE CASCADE")
    private long mDogOwnerId;

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

    public void setDogOwnerId(long dogOwnerId) {
        mDogOwnerId = dogOwnerId;
    }

    public long getDogOwnerId() {
        return mDogOwnerId;
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
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, DogTable.INSTANCE);
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

### Declare Indices

```java
@SQLiteObject("dog_table",  indices = @Index(name = "dog_table_unique_idx", value = {"name", "age"}, unique = true))
public class Dog {

    @SQLiteColumn
    private String mName;

    @SQLiteColumn
    private int mAge;
    //....
}
```