package com.example.myfirebasestoragepdf;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class View_PDF_Activity extends AppCompatActivity {

    ListView myListView;
    DatabaseReference databaseReference;
    List<Pdf_Info> pdfInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__p_d_f_);
        getSupportActionBar().setTitle("View_PDF_Activity");

        myListView = findViewById(R.id.myListView);
        pdfInfoList = new ArrayList<>();

        viewAllPdfFiles();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pdf_Info info = pdfInfoList.get(position);

                Intent intent = new Intent();
                intent.setDataAndType(Uri.parse(info.getUrl()), "application/pdf");
                startActivity(intent);
            }
        });
    }

    private void viewAllPdfFiles() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Pdf");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Pdf_Info info = postSnapshot.getValue(Pdf_Info.class);
                    pdfInfoList.add(info);
                }

                String[] uploads = new String[pdfInfoList.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = pdfInfoList.get(i).getName();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView textView = view.findViewById(android.R.id.text1);

                        /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLACK);

                        return view;
                    }
                };


                myListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}