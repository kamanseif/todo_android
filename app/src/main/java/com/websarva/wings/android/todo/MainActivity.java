package com.websarva.wings.android.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.websarva.wings.android.todo.db.TaskContract;
import com.websarva.wings.android.todo.db.TaskDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final Object Tag = "MainActivity";

    //Helperオブジェクトをフィールドとして用意
    private TaskDbHelper mHelper;

    //データベースヘルパーオブジェクト
    private DatabaseHelper _helper;

    //ListViewオブジェクトをフィールドとして用紙
    private ListView mTaskListView;

    //データベースからそれぞれの要素をリストに入れるためのもの
    private ArrayAdapter<String> mAdapter;

    //選択されたリストの主キーIDを表すフィールド
   private int _task_chooseID = -1;

    //選択されたタスク名を表すフィールド
    private String _task_choose_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DBヘルパーオブジェクトを生成
       // _helper=new DatabaseHelper(MainActivity.this);
        //Helperオブジェクトを生成
        mHelper = new TaskDbHelper(this);

        //ListViewオブジェクトを取得
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        //mataskListViewにリスナを登録

        updateUI();

        mTaskListView.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    protected void onDestroy(){
        //DBヘルパーオブジェクトの解放
        _helper.close();
        super.onDestroy();
    }

    private void updateUI(){
        //大きさが決まっていない配列(リストデータに相当)
        ArrayList<String> taskList = new ArrayList<>();

        //ヘルパーオブジェクトからデータベース接続オブジェクトを受け取る（読み取り専用のデータベース）
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //結果のリスト（データベース）とそのリストを指すカーソルがあって、カーソルを動かしながら結果を取り出す
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID,TaskContract.TaskEntry.COL_TASK_TITLE},
                null,null,null,null,null);

                //カーソルオブジェクトをループさせてデータベース内のデータを取得
                while(cursor.moveToNext()){
                    int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
                    //データベースからのデータを配列に追加
                    taskList.add(cursor.getString(idx));
                }
        //リストが空だったら
        if(mAdapter == null){
            /*リストデータ(taskList)を元にアダプタオブジェクト(mAdapter)を生成
              アダプタとはリストビュー(mTaskListView)に表示するリストデータ(taskList)を管理し、リストビューの
              各行にそのリストデータを当てはめていく働きをするオブジェクト
            */

            //元データとして配列（taskList）を利用するアダプタクラス
            mAdapter = new ArrayAdapter<>(this,R.layout.item_todo,R.id.task_title,taskList);

            //ListView(mTaskListView)にアダプタオブジェクトをセットする
            mTaskListView.setAdapter(mAdapter);
        }

        //リストが空でないとき
        else{

            //リストから全てのデータを除去する
            mAdapter.clear();

            //配列(taskList)の要素を全てアダプタ(mAdapter)に追加する
            mAdapter.addAll(taskList);

            //基になるデータが変更され、反映されたデータセットが自動的に更新される必要があることを監視者に知らせる
            mAdapter.notifyDataSetChanged();
        }

        //カーソルを閉じて、全てのリソースを解放し、完全に無効にする
        cursor.close();

        //データベースの解放処理
        db.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //ボタンを有効にするクラス
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_add_task:
                Log.d((String) Tag,"ToDo App 項目の追加");

                //ボタンを押した後のイベント
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("追加")
                        .setMessage("何する?")
                        .setView(taskEditText)
                        .setPositiveButton("追加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                Log.d((String) Tag,"追加された項目： " + task);


                                //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
                                SQLiteDatabase db = mHelper.getWritableDatabase();

                                //データベースにデータを追加するためのもの（オブジェクトの作成）
                                ContentValues values = new ContentValues();
                                //キーと値のペアをセットに追加
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE,task);
                                //データベースに行を挿入する　データベースに衝突が発生した場合の処理を行う
                                // CONFLICT_REPLACE 制約に違反するデータは元データを上書きする
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,null,values, SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                //ボタンを押したときに呼び出される関数
                                updateUI();
                            }
                        })
                        .setNegativeButton("キャンセル",null)
                        .create();
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //保存ボタンがタップされた時の処理メソッド
    public void onSaveButtonClick(View view){
        //詳細欄を取得
        EditText etNote = findViewById(R.id.etNote);
/*
        String note = etNote.getText().toString();

        //データヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db = _helper.getWritableDatabase();

        //まず、リストで選択されたカクテルのメモデータを削除。その後インサートを行う。削除用SQL文字列を用意
        String sqlDelete = "DELETE FROM taskdetailmemos WHERE _id = ?";

        //SQL文字列を元にプリペア度ステートメントを取得
        SQLiteStatement stmt = db.compileStatement(sqlDelete);

        //変数のバインド
        stmt.bindLong(1,_task_chooseID);

        //削除SQLの実行
        stmt.executeUpdateDelete();

        //インサート用SQL文字列を用意
        String sqlInsert = "INSERT INTO taskdetailmemos (_id,name,note) VALUES (?,?,?)";

        //SQL文字列を元にプリペアドステートメントを取得
        stmt=db.compileStatement(sqlInsert);

        //変数のバインド
        stmt.bindLong(1,_task_chooseID);
        stmt.bindString(2,_task_choose_name);
        stmt.bindString(3,note);

        //インサートSQLの実行
        stmt.executeInsert();

*/
        //タスク欄の入力値を消去
        etNote.setText("");
        //タスク名を未選択に変更
        TextView tvTaskName = findViewById(R.id.tvTaskName);
        tvTaskName.setText(getString(R.string.task_name));
        //保存ボタンをタップできないように変更
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setEnabled(false);
    }

    //リストがタップされた時の処理が記述されたメンバクラス
    public class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //タップされた行番号をフィールドの主キーIDに代入
            _task_chooseID = position;
            //タップされた行のデータを取得　これがタスクとなるのでフィールドに代入
            _task_choose_name = (String) parent.getItemAtPosition(position);

            Log.d((String) Tag, "表示するタスク" + _task_choose_name);
            //タスク名を表示するTextViewに表示タスク名を設定
            TextView tvTaskName = findViewById(R.id.tvTaskName);
            tvTaskName.setText(_task_choose_name);
            //保存ボタンをタップできるように設定
            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setEnabled(true);
/*
            //データヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db = _helper.getWritableDatabase();

            //主キーによる検索SQL文字列の用意
            String sql = "SELECT * FROM taskdetailmemos WHERE _id = " + _task_chooseID;

            //SQLの実行
            Cursor cursor = db.rawQuery(sql, null);
            //データベースから取得した値を格納する変数の用意。データがなかった時のための初期値も用意
            String note = "";
            //SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得
            while (cursor.moveToNext()) {
                //カラムのインデックス値を取得
                int idxNote = cursor.getColumnIndex("note");
                //カラムのインデックス値を元に実際のデータを取得
                note = cursor.getString(idxNote);

            }

            //感想のEditTextの各画面部品を取得し、データベースの値を反映
            EditText etNote = findViewById(R.id.etNote);
            etNote.setText(note);
*/

        }

    }

        public void deleteTask(View view) {
            View parent = (View) view.getParent();
            TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
            String task = String.valueOf(taskTextView.getText());
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{task});
            db.close();
            //SQLiteDatabase db2 = _helper.getWritableDatabase();
            //db2.delete("taskdetailmemos","name=?",new String[]{task});
            //db2.close();
            updateUI();
        }
}

