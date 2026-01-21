package com.agpitcodeclub.codeclubagpit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        RecyclerView rvFaculty = findViewById(R.id.rvFaculty);
        rvFaculty.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<FacultyModel> facultyList = new ArrayList<>();
        FacultyAdapter adapter = new FacultyAdapter(facultyList);
        rvFaculty.setAdapter(adapter);

        facultyList.add(new FacultyModel(
                "S. A. Patil",
                "Secretary - AGPIT",
                "https://codeclubagpit.vercel.app/SecretaryDesk.png"
        ));

        facultyList.add(new FacultyModel(
                "Dr. M. A. Chougule",
                "Campus Director",
                "https://codeclubagpit.vercel.app/campus_director_chougule_sir.jpg"
        ));

        facultyList.add(new FacultyModel(
                "Dr. V. V. Potdar",
                "Principal",
                "https://codeclubagpit.vercel.app/Principal-desk.jpeg.jpg"
        ));

        facultyList.add(new FacultyModel(
                "Mr. S. V. Kulkarni",
                "HOD - CSE",
                "https://codeclubagpit.vercel.app/Kulkarni.jpg"
        ));



        adapter.notifyDataSetChanged();



        TextView tvWebsite = findViewById(R.id.tvWebsite);
        tvWebsite.setOnClickListener(v -> {
            String url = "https://codeclubagpit.vercel.app";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}
