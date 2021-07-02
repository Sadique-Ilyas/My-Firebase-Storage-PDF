package com.example.myfirebasestoragepdf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PdfActivity extends AppCompatActivity {

    EditText myPdf;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    SimpleDateFormat simpleDateFormat;
    private Uri uriFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        getSupportActionBar().setTitle("PdfActivity");

        myPdf = findViewById(R.id.myPdf);

        storageReference = FirebaseStorage.getInstance().getReference("Pdf");
        databaseReference = FirebaseDatabase.getInstance().getReference("Pdf");
    }

    public void choosePdf(View view) {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFilePath = data.getData();
        }
    }

    public void uploadPdf(View view) {
        final String pdfName = myPdf.getText().toString().trim();
        if (pdfName.isEmpty()) {
            myPdf.setError("Enter PDF Name");
        } else {
            if (uriFilePath != null) {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd _ HH:mm:ss");
                String currentDateTime = simpleDateFormat.format(new Date());
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                StorageReference reference = storageReference.child("PDF " + currentDateTime + ".pdf");
                reference.putFile(uriFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url = uri.getResult();

                        Pdf_Info info = new Pdf_Info(pdfName, url.toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(info);
                        progressDialog.dismiss();
                        Toast.makeText(PdfActivity.this, "PDF Uploaded !!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PdfActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
            }
        }
    }

    public void viewPDF(View view) {
        startActivity(new Intent(PdfActivity.this, View_PDF_Activity.class));
    }
}