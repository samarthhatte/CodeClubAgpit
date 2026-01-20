package com.agpitcodeclub.codeclubagpit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etDesc;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        db = FirebaseFirestore.getInstance();
        etTitle = findViewById(R.id.etEventTitle);
        etDate = findViewById(R.id.etEventDate);
        etDesc = findViewById(R.id.etEventDesc);
        Button btnSubmit = findViewById(R.id.btnSubmitEvent);

        btnSubmit.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String desc = etDesc.getText().toString();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Title and Date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("description", desc);
        event.put("timestamp", FieldValue.serverTimestamp());

        db.collection("events").add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Event Posted Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to dashboard
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}