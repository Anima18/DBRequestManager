# DBRequestManager
封装SqlBrite，更方便操作Sqlite数据，提供通用的增删改查接口.   
 
1. [创建实体类](https://github.com/Anima18/DBRequestManager/blob/master/README.md#createBean)  
2. [创建表结构](#createTable)  
3. [创建SQLOpenHelper](#createHelper)  
4. [初始化DBManager](#initDB)  
5. [DBRequest介绍](#DBRequest)  
6. [add](#add)  
7. [getList](#getList)  
8. [update](#update)  
9. [delete](#delete)  

# Simple Project
你可以在下面链接下载最新的APK。[https://github.com/Anima18/DBRequestManager/tree/master/simple](https://github.com/Anima18/DBRequestManager/tree/master/simple)

# How to use

### <span id = "createBean">创建实体类</span>
    public class People{
    private int id;
    private String name;
    private int age;

    ......
	}

### <span id = "createTable">创建表结构</span>  
这里创建一个people的表，包括表名、表字段、创建表SQL语句、数据库记录转换对象和对象转数据库记录方法。  

    public class PeopleTable {
    // 表名
    public static final String TABLE_NAME = "people";

    // 表字段
    public static final String ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUME_AGE = "age";

    // 建表语句
    public static final String CREATE =
            "CREATE TABLE "
                    + TABLE_NAME
                    + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUME_AGE + " INT,"
                    + "UNIQUE (" + COLUMN_NAME + ")  ON CONFLICT REPLACE"
                    + " ); ";

    // 根据表中的row生成一个对象
    public static Func1<Cursor, People> PERSON_MAPPER = new Func1<Cursor, People>() {
        @Override
        public People call(Cursor cursor) {
            People people = new People();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            people.setId(id);
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            people.setName(name);
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUME_AGE));
            people.setAge(age);
            return people;
        }
    };

    // 根据对象生成一条数据库记录
    public static ContentValues toContentValues(People person) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUME_AGE, person.getAge());
        return values;
    }
	}  

### <span id = "createHelper">创建SQLOpenHelper</span>  
    public class SQLOpenHelper extends SQLiteOpenHelper {
	  public static final String DB_NAME = "sqlbrite.db";
	  public static final int DB_VERSION = 1;
	
	  private static SQLOpenHelper instance;
	
	  public static SQLOpenHelper getInstance(Context context) {
	    if (null == instance) {
	      instance = new SQLOpenHelper(context);
	    }
	    return instance;
	  }
	
	  private SQLOpenHelper(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	  }
	
	  @Override
	  public void onCreate(SQLiteDatabase db) {
	    db.execSQL(PeopleTable.CREATE);
	
	  }
	
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
	  }
	}

### <span id = "initDB">初始化DBManager</span> 
DBManager提供操作数据库的接口。  

    public class MyApplication extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        DBManager.initialize(SQLOpenHelper.getInstance(this));
	
	    }
	}

### <span id = "DBRequest">DBRequest介绍</span> 
DBRequest对DBManager进行封装，因为操作数据需要的参数多，所以使用Builder模式。提供了add、getList、update和delete接口。


### <span id = "add">插入数据</span>
    new DBRequest.Builder()
                .tableName(PeopleTable.TABLE_NAME)
                .contentValues(PeopleTable.toContentValues(people))
                .add();

### <span id = "getList">查询数据</span>
    new DBRequest.Builder()
                    .tableName(PeopleTable.TABLE_NAME)
                    .querySql("SELECT * FROM " + PeopleTable.TABLE_NAME+ " where name = ?")
                    .whereArgs(new String[]{name})
                    .mapper(PeopleTable.PERSON_MAPPER)
                    .getList(new CollectionCallBack<People>() {
                        @Override
                        public void onFailure(int code, String message) {}

                        @Override
                        public void onCompleted() { }

                        @Override
                        public void onSuccess(List<People> datas) {}
                    });

### <span id = "update">更新数据</span>  
    new DBRequest.Builder()
                .tableName(PeopleTable.TABLE_NAME)
                .contentValues(PeopleTable.toContentValues(people))
                .whereClause("_id = ?")
                .whereArgs(new String[] {people.getId()+""})
                .update();

### <span id = "delete">删除数据</span>
    new DBRequest.Builder()
                    .tableName(PeopleTable.TABLE_NAME)
                    .whereClause("_id = ?")
                    .whereArgs(new String[] {people.getId()+""})
                    .delete();

