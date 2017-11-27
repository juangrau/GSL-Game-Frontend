package juangrau.gslgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by juang_000 on 11/26/2017.
 */

public class Database extends SQLiteOpenHelper {
    public static final String USER_TABLE = "user";
    public static final String API_KEY = "api_key";
    public static final String POINTS = "points";


    public static final String DB_NAME = "game.db";
    public static int DB_VERSION = 1;

    private final String mDestPath;

    private SQLiteDatabase mDatabase;

    private final Context mContext;

    public Database(Context context)  throws IOException {
        super(context, DB_NAME, null, 1);

        File dbFile;
        InputStream input;
        OutputStream output;


        mContext = context;
        mDestPath = "/data/data/" + mContext.getPackageName() +  "/databases/"  + DB_NAME;
        dbFile = new File(mDestPath);

        if(!dbFile.exists()) {
            // By calling this method and empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            // Copies your database from your local assets-folder to the just created empty database in the
            // system folder, from where it can be accessed and handled.
            // This is done by transfering bytestream.
            //Open your local db as the input stream
            input = mContext.getAssets().open(DB_NAME);
            //Open the empty db as the output stream
            output = new FileOutputStream(mDestPath);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            //Close the streams
            output.flush();
            output.close();
            input.close();
        }
        //Open the database
        mDatabase = SQLiteDatabase.openDatabase(mDestPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    public void setApiKey(String api_key){
        Log.d("DB", "DB: Inserting new message");
        ContentValues contentValues = new ContentValues();
        contentValues.put(API_KEY,api_key);
        mDatabase.insert(USER_TABLE, null, contentValues);
    }

    public Cursor getApiKey(){
        String sqlString;
        Cursor cursor;

        sqlString = " SELECT api_key FROM " + USER_TABLE;

        System.out.println("sqlString " + sqlString);
        cursor = mDatabase.rawQuery(sqlString, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //---opens the database---
    public Database open() throws SQLException {
        mDatabase = this.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public synchronized void close() {
        if (mDatabase != null){
            mDatabase.close();
        }
    }
}
