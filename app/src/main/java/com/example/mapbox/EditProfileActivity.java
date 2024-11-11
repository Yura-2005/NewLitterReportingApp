package com.example.mapbox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapbox.database.AppDatabase;

public class EditProfileActivity extends AppCompatActivity {
    private EditText nameEditText;
    private ImageView profileImageView;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void saveProfileChanges() {
        String name = nameEditText.getText().toString();
        int points = 100; // Приклад значення
        String level = "Новачок"; // Приклад рівня

        AppDatabase db = AppDatabase.getInstance(this);
        db.saveUserProfile(name, points, level);

        Toast.makeText(this, "Зміни збережено успішно!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
