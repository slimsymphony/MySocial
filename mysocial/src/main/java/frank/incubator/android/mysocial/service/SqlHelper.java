package frank.incubator.android.mysocial.service;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import frank.incubator.android.mysocial.common.CommonUtils;
import frank.incubator.android.mysocial.common.Constants;

/**
 * Sqlite data base operation helper class.
 * Created by f78wang on 8/20/14.
 */
public class SqlHelper extends SQLiteOpenHelper {
    private SQLiteOpenHelper helper;

    private final static String DATABASE_NAME = "mysocial.db";
    private final static int DATABASE_VERSION = 1;
    private final String INIT_FEEDS = "create table feeds( id INTEGER PRIMARY KEY AUTOINCREMENT, topic VARCHAR(200), content VARCHAR(4000), timestamp DATETIME default current_timestamp, status INTEGER )";
    private final String INIT_ATTACHMENTS = "create table attachments( id INTEGER PRIMARY KEY AUTOINCREMENT, feedId INTEGER, desc VARCHAR(1000), originalName VARCHAR(500), format VARCHAR(10), link VARCHAR(500) )";
    private final String DROP_FEEDS = "DROP TABLE IF EXISTS feeds";
    private final String DROP_ATTACHMENTS = "DROP TABLE IF EXISTS attachments";

    public SqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    public SQLiteDatabase getDb(){
        return this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(INIT_FEEDS);
            db.execSQL(INIT_ATTACHMENTS);
        }catch( SQLException ex ){
            Log.e(Constants.LOG_TAG, "Create db failed." + CommonUtils.getStack(ex));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        try{
            db.execSQL(DROP_FEEDS);
            db.execSQL(DROP_ATTACHMENTS);
            onCreate(db);
        }catch( SQLException ex ){
            Log.e(Constants.LOG_TAG, "Upgrade db failed." + CommonUtils.getStack(ex));
        }
    }
}
