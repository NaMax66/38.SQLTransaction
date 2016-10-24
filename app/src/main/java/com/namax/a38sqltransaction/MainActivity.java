package com.namax.a38sqltransaction;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String DB_NAME = "MyDB" ;
    private static final String TABLE_NAME = "MyTable" ;
    private SQLiteDatabase database;
    TextView tvTime ;
    Button btnInsert, btnStat, btnCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_main);
        initDB();
        tvTime = (TextView) findViewById(R.id.tvTime );
        btnInsert = (Button) findViewById(R.id.btnInsert );
        btnStat = (Button) findViewById(R.id.btnStat);
        btnCV = (Button) findViewById(R.id.btnCV);
        btnInsert.setOnClickListener( this);
        btnStat.setOnClickListener(this);
        btnCV.setOnClickListener(this);

    }

    private void initDB(){
        database = this.openOrCreateDatabase( DB_NAME , MODE_PRIVATE , null );
        database.execSQL( "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(FirstNumber INT, SecondNumber INT, Result INT);" );
        database.delete( TABLE_NAME, null , null );
    }

    @Override
    public void onClick(View view) {
        database.delete( TABLE_NAME, null , null );
        long startTime = System. currentTimeMillis();
        if (view.getId() == R.id.btnInsert) insertRecords();
        if (view.getId() == R.id.btnStat) insertRecordsUsingStatements();
        if (view.getId() == R.id.btnCV) insertRecordsCV();
        insertRecords();
        long diff = System. currentTimeMillis() - startTime;
        tvTime.setText( "Time: " + Long. toString(diff) + " ms");

    }

    private void insertRecords(){ //используем транзакцию, это ускоряет вставку в 40 раз по сравнению с CV
        database.beginTransaction();
        try {
            for ( int i = 0; i < 1000 ; i++){
                ContentValues cv = new ContentValues();
                cv.put( "FirstNumber", i);
                cv.put( "SecondNumber", i);
                cv.put( "Result", i*i);
                database.insert( TABLE_NAME , null , cv);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
    private void insertRecordsCV(){ //без транзакции

            for ( int i = 0; i < 1000 ; i++){
                ContentValues cv = new ContentValues();
                cv.put( "FirstNumber", i);
                cv.put( "SecondNumber", i);
                cv.put( "Result", i*i);
                database.insert( TABLE_NAME , null , cv);
            }

    }
 private void insertRecordsUsingStatements(){ // используем класс SQLiteStatement
     String sql = "INSERT INTO " +TABLE_NAME + " VALUES(?, ?, ?);";
     SQLiteStatement statement = database.compileStatement(sql);

        database.beginTransaction();
        try {
            for ( int i = 0; i < 1000 ; i++){
                statement.clearBindings();
                statement.bindLong(1, i);
                statement.bindLong(2, i);
                statement.bindLong(3, i*i);
                statement.execute();
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}