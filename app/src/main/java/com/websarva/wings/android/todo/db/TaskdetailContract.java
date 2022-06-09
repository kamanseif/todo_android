package com.websarva.wings.android.todo.db;
import android.provider.BaseColumns;
public class TaskdetailContract {

    public static final String DB_NAME_D = "com.todolist.db";
    public static final int DB_VERSION_D = 2;

    public class TaskdetailEntry implements BaseColumns {
        public static final String TABLE = "taskdetail";
        public static final String COL_TASK_TITLE = "title_2";
    }
}
