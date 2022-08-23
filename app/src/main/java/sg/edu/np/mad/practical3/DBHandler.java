package sg.edu.np.mad.practical3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "users.db";
    public static String USERS = "Users";
    public static String COLUMN_NAME = "Name";
    public static String COLUMN_DESCRIPTION = "Description";
    public static String COLUMN_ID = "Id";
    public static String COLUMN_FOLLOWED = "Followed";

    public static int DATABASE_VERSION = 1;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + USERS +
                "(" + COLUMN_NAME + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_ID + " INT," +
                COLUMN_FOLLOWED + " INT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS);
        onCreate(db);
    }

    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_DESCRIPTION, user.getDescription());
        values.put(COLUMN_ID, user.getId());
        values.put(COLUMN_FOLLOWED, user.isFollowed() ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(USERS, null, values);
        db.close();
    }

    public User findUser(int id) {
        String query = "SELECT * FROM " + USERS + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        User queryData = new User();

        if (cursor.moveToFirst()){
            queryData.setName(cursor.getString(0));
            queryData.setDescription(cursor.getString(1));
            queryData.setId(cursor.getInt(2));
            queryData.setFollowed(cursor.getInt(3) == 1);
            cursor.close();
        }
        else {
            queryData = null;
        }
        db.close();
        return queryData;
    }

    public ArrayList<User> getUsers() {
        String query = "SELECT * FROM " + USERS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<User> userList = new ArrayList<>();
        User queryData;

        if (cursor.getCount() == 0) return userList;
        for (int i = 0; i < 20; i++) {
            if (i == 0) cursor.moveToFirst();
            else cursor.moveToNext();
            queryData = new User(cursor.getString(0), cursor.getString(1), cursor.getInt(2),cursor.getInt(3) == 1);
            userList.add(queryData);
        }
        db.close();
        return userList;
    }

    public void updateUser(User user) {
        int followFlag = user.isFollowed() ? 1 : 0;
        String query = "UPDATE " + USERS + " SET " + COLUMN_FOLLOWED + " = " + followFlag +
                " WHERE " + COLUMN_ID + " = " + user.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }
}
