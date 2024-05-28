package com.siddhant.llamalingua;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    DBHelper dbHelper;
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.actvLanguage);
        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });
        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String[] userInfo = getUserInfo(etEmail.getText().toString(), etPassword.getText().toString());
            if (userInfo == null || userInfo.length == 0) {
                Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this, Dashboard.class);
                i.putExtra("user", userInfo);
                startActivity(i);
            }
        });
    }

    public String[] getUserInfo(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) return null;

        System.out.println("Email: " + email + " Pass: " + password);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "email = ? AND password = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(
                "my_table",
                null, // Select all columns
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String[] userInfo = null;
        if (cursor.moveToFirst()) {
            int columnCount = cursor.getColumnCount();
            userInfo = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                userInfo[i] = cursor.getString(i);
            }
        }
        cursor.close();
        db.close();

        return userInfo;
    }
}