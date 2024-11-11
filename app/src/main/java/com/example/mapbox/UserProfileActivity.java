package com.example.mapbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {
    private TextView nameTextView, trashCountTextView, levelTextView, pointsTextView;
    private ImageView profileImageView;
    private Button editProfileButton, redeemPointsButton;
    private Switch achievementsSwitch, promotionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ініціалізація елементів інтерфейсу
        nameTextView = findViewById(R.id.nameTextView);
        trashCountTextView = findViewById(R.id.trashCountTextView);
        levelTextView = findViewById(R.id.levelTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        profileImageView = findViewById(R.id.profileImageView);
        editProfileButton = findViewById(R.id.editProfileButton);
        redeemPointsButton = findViewById(R.id.redeemPointsButton);
        achievementsSwitch = findViewById(R.id.achievementsSwitch);
        promotionSwitch = findViewById(R.id.promotionSwitch);

        // Завантаження даних користувача (це можна реалізувати через базу даних або API)
        loadUserProfile();

        // Обробник кнопки "Редагувати профіль"
        editProfileButton.setOnClickListener(v -> openEditProfile());

        // Обробник кнопки "Обміняти бали"
        redeemPointsButton.setOnClickListener(v -> redeemPoints());
    }

    private void loadUserProfile() {
        // Завантаження даних користувача (можна витягнути з бази даних)
        // nameTextView.setText(user.getName());
        // trashCountTextView.setText(String.valueOf(user.getTrashCount()));
        // levelTextView.setText(user.getLevel());
        // pointsTextView.setText(String.valueOf(user.getPoints()));
    }

    private void openEditProfile() {
        Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    private void redeemPoints() {
        // Логіка обміну балів на винагороди
        Toast.makeText(this, "Бали обміняно успішно!", Toast.LENGTH_SHORT).show();
    }
}