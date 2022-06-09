package com.websarva.wings.android.todo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDbHelper extends SQLiteOpenHelper {


    //コンストラクタ
    public TaskDbHelper(Context context){
        //親クラスのコンストラクタの呼出し
        super(context,TaskContract.DB_NAME,null,TaskContract.DB_VERSION);
    }

    //TaskContract.javaで定義したデータベース名のデータベースが存在しないとき、実行される
    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " +
                TaskContract.TaskEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

        //データベースの構造や構成を定義するためのSQL文を実行するためのメソッド
        db.execSQL(createTable);
    }


    //抽象メソッドなので定義は必要
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //テーブルを削除する
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }

}
