package com.android.rupali.myapplication;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText titleEditText, msgEditText;
    TextView toolbarTitle;
    Button saveNoteButton , getNoteButton;
    ConstraintLayout contentMainLayout;
    ProgressBar progressBar;

    FirebaseFirestore fireStoreDb;

    private static final String MY_TAG = "FirebaseTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle=findViewById(R.id.mainActivityToolbarTitle);
        toolbarTitle.setText("Main Acivity");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
        setListeners();
    }

    private void setListeners() {
        saveNoteButton.setOnClickListener(this);
        getNoteButton.setOnClickListener(this);
    }

    private void initUI() {
        titleEditText = findViewById(R.id.noteTitle);
        msgEditText = findViewById(R.id.noteMsg);
        saveNoteButton = findViewById(R.id.saveNote);
        contentMainLayout = findViewById(R.id.layoutContentMain);
        progressBar = findViewById(R.id.mainActivityProgressBar);
        getNoteButton = findViewById(R.id.getNote);

        fireStoreDb = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.saveNote:
                saveNote();
                break;

            case R.id.getNote:
                getNote();
                break;

            default:
                break;
        }
    }

    private void saveNote() {
        showProgressBar();
        String title = titleEditText.getText().toString();
        String msg = msgEditText.getText().toString();
        Note note = new Note(title, msg);
        DocumentReference newNoteRef = fireStoreDb.collection("notes").document();
        Log.d(MY_TAG,"inside save note, noteID: "+newNoteRef.getId());
        note.setId(newNoteRef.getId());

        newNoteRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hideProgressBar();
                Log.d(MY_TAG,"note added successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Log.d(MY_TAG,"note add failed , error: "+e.getMessage());
            }
        });

    }

    private void getNote(){
        showProgressBar();
        CollectionReference notesCollectionReference = fireStoreDb.collection("notes");
//        DocumentReference noteDocReference = notesCollectionReference.document("I9Ui7KfTIPBt0rzvZkZf");
        Query notesQuery = notesCollectionReference.whereEqualTo("id","I9Ui7KfTIPBt0rzvZkZf");
        notesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hideProgressBar();
                Log.d(MY_TAG,"note retreived successfully");
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                for(int i = 0; i < documentSnapshots.size(); i++){
                    DocumentSnapshot documentSnapshot =documentSnapshots.get(i);
                    Note note =documentSnapshot.toObject(Note.class);
                    titleEditText.setText(note.getTitle());
                    msgEditText.setText(note.getMsg());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressBar();
                Log.d(MY_TAG,"note get failed , error: "+e.getMessage());
            }
        });
    }
    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        contentMainLayout.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        contentMainLayout.setVisibility(View.INVISIBLE);
    }
}
