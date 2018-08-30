# SQLite

Database library for Android based on SQLite

### Gradle:

```groovy
implementation 'ru.nikitazhelonkin:sqlite:2.1.2'
annotationProcessor 'ru.nikitazhelonkin:sqlite-compiler:2.1.2'
```

### Usage:

Just annotate you entity class as in the example below:

```java
@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn(primaryKey = true, autoincrement = true)
    private long mId;

    @SQLiteColumn(unique = true, notnull = true)
    private String mName;

    @SQLiteColumn
    private int mAge;

    // Getters and setters are ignored for brevity, but they're required
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
Create entity dao class that extends from ```Dao<T>```:

```java
public class DogDao extends Dao<Dog> {

    public DogDao(SQLiteDatabaseProvider provider) {
        super(provider, DogTable.INSTANCE);
    }
    
    //Custom methods here
    public List<Dog> getPuppies(){
        return query(Selection.create().lessThanOrEquals(DogTable.AGE, 1)); 
    }
}
```
### Query

```java
DogDao dogDao = new DogDao(helper);

Dog guffy = dogDao.queryFirst(Selection.create().equals(DogTable.NAME, "Guffy" ));

List<Dog> allDogs = dogDao.query();

List<Dog> puppies  = dogDao.getPuppies();
``````

### Insert
```java
Dog dog = new Dog(1, "Guffy", 1);
dogDao.insert(dog);
```

### Delete

```java
dogDao.delete(Selection.create().equals(DogTable.ID, dog.getId()));
```

### Update

```java
dogDao.update(Selection.create().equals(DogTable.ID, dog.getId()), dog);
```

### Indices declaration

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

### Foreign Keys declaration

```java
@SQLiteObject("dog_table")
public class Dog {

    @SQLiteColumn
    private String mName;

    @SQLiteColumn
    private int mAge;


    @SQLiteColumn(reference = @Reference(parentTable = "dog_owner_table", parentColumn = "id", onDelete = Reference.CASCADE))
    private long mDogOwnerId;

    //....
}
```