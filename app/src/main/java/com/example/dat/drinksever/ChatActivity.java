package com.example.dat.drinksever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dat.drinksever.Adapter.MessageAdapter;
import com.example.dat.drinksever.Interface.ILoadTimeFromFirebaseListener;
import com.example.dat.drinksever.Model.ChatMessage;
import com.example.dat.drinksever.Utils.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChatActivity extends AppCompatActivity implements ILoadTimeFromFirebaseListener {


    private static final int MY_CAMERA = 123;
    private static final int MY_RESULT_LOAD_IMG = 345;
    Toolbar toolbar;
    ImageView imCamera,imImage,imSend,imPreview;
    AppCompatEditText edChat;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference chatRef,offsetRef;
    ILoadTimeFromFirebaseListener iLoadTimeFromFirebaseListener;

    FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder> adapter;
    FirebaseRecyclerOptions<ChatMessage> options;
    Uri fileUri;
    StorageReference storageReference;
    LinearLayoutManager linearLayoutManager;

    DatabaseReference reference;
    MessageAdapter messageAdapter;
    List<ChatMessage> mchat;
    Bitmap bitmap;
    FirebaseStorage firebaseStorage;
    String userid;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findView();
        userid = getIntent().getStringExtra("user");
        name = getIntent().getStringExtra("name");
        initViews();

        chatRef = FirebaseDatabase.getInstance().getReference().child("ChatMessage").child(userid);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,MY_RESULT_LOAD_IMG);
            }
        });

        imCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                fileUri = getOutputMediaFileUri();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                startActivityForResult(cameraIntent, MY_CAMERA);
            }
        });

        imSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long estimate = System.currentTimeMillis();

                iLoadTimeFromFirebaseListener.onLoadOnlyTimeTimeSuccess(estimate);
            }
        });



        readMesagges("3NP8PNkVqYOmQhqKUOyB9b30sJv2",userid);
    }


    private Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }
    private void findView(){
        toolbar = findViewById(R.id.toolbarlayout);
        imCamera = findViewById(R.id.imCamera);
        imImage = findViewById(R.id.imImage);
        imSend = findViewById(R.id.imSend);
        imPreview = findViewById(R.id.imPreview);
        edChat = findViewById(R.id.edChat);
        recyclerView = findViewById(R.id.recyleviewChat);
    }



    public static String getFileName(ContentResolver contentResolver, Uri fileUri){
        String result = null;
        if(fileUri.getScheme().equals("content")){
            Cursor cursor = contentResolver.query(fileUri,null,null,null,null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }

        if(result == null){
            result = fileUri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }

    private File getOutputMediaFile(){
        File file = new File(Environment.getExternalStorageDirectory(),"drinkshop");
        if(!file.exists()){
            file.mkdir();
        }
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = new File(new StringBuilder(file.getPath())
                .append(File.separator)
                .append("IMG_")
                .append(time)
                .append("_")
                .append(new Random().nextInt())
                .append(".jpg")
                .toString());

        return mediaFile;
    }

    private void initViews(){

        iLoadTimeFromFirebaseListener =  this;
        database = FirebaseDatabase.getInstance();

        offsetRef = database.getReference(".info/serverTimeOffset");


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        toolbar.setTitle("Message");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    private void readMesagges(final String myid, final String userid){
        mchat = new ArrayList<>();

        // reference = FirebaseDatabase.getInstance().getReference("ChatMessage");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatMessage chat = snapshot.getValue(ChatMessage.class);
                    if(chat!= null){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, mchat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_CAMERA){
            if(resultCode == RESULT_OK){

                upLoadImage(data);
            }
        }else if(requestCode == MY_RESULT_LOAD_IMG){
            if(resultCode == RESULT_OK){
                upLoadImage(data);
            }
        }
    }


    private void upLoadImage(Intent data){
        Uri filePath;

        if(fileUri == null){
            filePath = data.getData();
        }else {

            filePath = fileUri;
        }

        try {


            final long currentTimeMillis = System.currentTimeMillis();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("images/"+ currentTimeMillis+".png");
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ChatMessage chatMessage = new ChatMessage();
                                    chatMessage.setName(Common.curenuser);
                                    chatMessage.setReceiver(userid);
                                    chatMessage.setSender("3NP8PNkVqYOmQhqKUOyB9b30sJv2");
                                    chatMessage.setTimeStamp(System.currentTimeMillis());
                                    chatMessage.setPictureLink(uri.toString());
                                    chatMessage.setPicture(true);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    reference.child("ChatMessage").child(userid)
                                            .push()
                                            .setValue(chatMessage)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        edChat.requestFocus();

                                                    }
                                                }
                                            });
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    @Override
    public void onLoadOnlyTimeTimeSuccess(long time) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setName(Common.curenuser);
        chatMessage.setContent(edChat.getText().toString());
        chatMessage.setReceiver(userid);
        chatMessage.setSender("3NP8PNkVqYOmQhqKUOyB9b30sJv2");
        chatMessage.setTimeStamp(time);
        chatMessage.setPicture(false);
        submitChatToFirebase(chatMessage,chatMessage.isPicture());

    }




    private void submitChatToFirebase(ChatMessage chatMessage, final boolean picture){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("ChatMessage").child(userid)
                .push()
                .setValue(chatMessage)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            edChat.setText("");
                            edChat.requestFocus();

                        }
                    }
                });
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }
}