package com.example.dat.drinksever;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Utils.Common;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCategory extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1111;
    ImageView img_browser;
    EditText edt_name;
    Button btn_update, btn_delete;

    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable;

    Uri selectUri = null;
    String uploaded_img_path = "";
    final int REQUEST_PERMISSION_CODE = 444;
    String repath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        edt_name = findViewById(R.id.edt_name);
        img_browser = findViewById(R.id.img_browser);

        mService = Common.getAPI();

        compositeDisposable = new CompositeDisposable();
        displayData();


        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisstion();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FILE_REQUEST);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCategory();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCategory();
            }
        });
    }




    private void deleteCategory() {
        if (!edt_name.getText().toString().isEmpty()) {
            compositeDisposable.add(mService.deleteCategory(Common.category.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Toast.makeText(UpdateCategory.this, s, Toast.LENGTH_SHORT).show();

                            uploaded_img_path = "";
                            selectUri = null;
                            Common.category = null;
                            finish();
                        }
                    }));
        }
    }

    private void updateCategory() {
        if (!edt_name.getText().toString().isEmpty()) {
            compositeDisposable.add(mService.updateNewCategory(Common.category.getId(),
                    edt_name.getText().toString(),
                    uploaded_img_path)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Toast.makeText(UpdateCategory.this, s, Toast.LENGTH_SHORT).show();

                            uploaded_img_path = "";
                            selectUri = null;
                            Common.category = null;
                            finish();
                        }
                    }));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data != null) {
                    selectUri = data.getData();
                    if (selectUri != null && !selectUri.getPath().isEmpty()) {
                        repath = getRealPathFromURI(selectUri);
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            img_browser.setImageBitmap(bitmap);
                            uploadFileToServer();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(this, "can't upload file to server", Toast.LENGTH_SHORT).show();
                    }
                }



            }
        }
    }

    private void uploadFileToServer() {
        if (selectUri != null) {

            File file = new File(repath);
            String file_path = file.getAbsolutePath();
            String [] mangtenfile = file_path.split("\\.");
            file_path = mangtenfile[0] + System.currentTimeMillis() + "." +mangtenfile[1];

            final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/from-data"),file);// xác nhận lại kiểu dữ liệu

            final MultipartBody.Part body = MultipartBody.Part.createFormData("upload",file_path,requestBody);
            mService.uploadCategoryFile(body)
                    .enqueue(new Callback<FileName>() {
                        @Override
                        public void onResponse(Call<FileName> call, Response<FileName> response) {
                            Common.fileName = response.body();

                            uploaded_img_path = new StringBuilder(Common.BASE_URL)
                                    .append("Sever/category_img/")
                                    .append(Common.fileName.getFile()).toString();


                        }

                        @Override
                        public void onFailure(Call<FileName> call, Throwable t) {
                            Log.d("sss",t.getMessage());
                        }
                    });


        }
    }

    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    private void permisstion(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        compositeDisposable.clear();
        super.onStart();
    }

    private void displayData() {
        if (Common.category != null) {
            Picasso.with(this).load(Common.category.getLink())
                    .into(img_browser);

            edt_name.setText(Common.category.getName());

            uploaded_img_path = Common.category.getLink();
        }
    }




}
