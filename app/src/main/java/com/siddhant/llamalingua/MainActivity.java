package com.siddhant.llamalingua;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etUserName, etEmail, etPassword;
    private AutoCompleteTextView actvLanguage;
    private Spinner spinnerDifficulty;
    private Button btnSubmit;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        actvLanguage = findViewById(R.id.actvLanguage);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        btnSubmit = findViewById(R.id.btnSubmit);
        String[] languages = getResources().getStringArray(R.array.languages_array);
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        actvLanguage.setAdapter(languageAdapter);
        actvLanguage.setThreshold(1);

        actvLanguage.setOnClickListener(v -> {
            if (!actvLanguage.getText().toString().isEmpty()) {
                actvLanguage.showDropDown();
            }
        });

        actvLanguage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && actvLanguage.getText().toString().isEmpty()) {
                actvLanguage.showDropDown();
            }
        });
        String[] difficultyLevels = {"Basic Conversation", "Professional Communication", "Fluency"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, difficultyLevels);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(difficultyAdapter);

        HashMap<String, String> map = new HashMap<>();
        map.put(difficultyLevels[0], "beginner");
        map.put(difficultyLevels[1], "professional");
        map.put(difficultyLevels[2], "fluent");

        btnSubmit.setOnClickListener(v -> {
            String userName = etUserName.getText().toString();
            String email = etEmail.getText().toString();
            String selectedLanguage = actvLanguage.getText().toString();
            String password = etPassword.getText().toString();
            String selectedDifficulty = spinnerDifficulty.getSelectedItem().toString();

            if (userName.isEmpty() || selectedLanguage.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String[] userInfo = insertUserData(userName, email, password, selectedLanguage, selectedDifficulty);
                if(userInfo == null || userInfo.length == 0) {
                    Toast.makeText(this, "Failed to register.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, Dashboard.class);
                    i.putExtra("user", userInfo);
                    startActivity(i);
                }
            }
        });
    }

    public String[] insertUserData(String name, String email, String password, String language, String difficulty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("language", language);
        values.put("difficulty", difficulty);
        long id = db.insert("my_table", null, values);
        db.close();
        return new String[] {String.valueOf(id), name, email, password, language, difficulty};
    }
}