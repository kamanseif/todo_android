package com.websarva.wings.android.todo.db;

import android.provider.BaseColumns;

public class TaskContract {
    //データベースファイル名の定数フィールド
    public static final String DB_NAME = "todo.db";
    //バージョン情報の定数フィールド
    public static final int DB_VERSION = 2;



    public class TaskEntry implements BaseColumns{
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
    }

}
