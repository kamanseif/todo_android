package com.websarva.wings.android.todo.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class TaskdetailHelper extends SQLiteOpenHelper {

    public TaskdetailHelper(Context context) {
        super(context, TaskdetailContract.DB_NAME_D, null, TaskdetailContract.DB_VERSION_D);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskdetailContract.TaskdetailEntry.TABLE + " ( " +
                TaskdetailContract.TaskdetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskdetailContract.TaskdetailEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskdetailContract.TaskdetailEntry.TABLE);
        onCreate(db);
    }

}
