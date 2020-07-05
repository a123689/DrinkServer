package com.example.dat.drinksever;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dat.drinksever.Adapter.DrinkListAdapter;
import com.example.dat.drinksever.Model.Drink;
import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Utils.Common;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

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

public class DrinkListActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1111;
    final int REQUEST_PERMISSION_CODE = 232;
    IDrinkShopAPI mService;
    RecyclerView recycler_drinks;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    FloatingActionButton btn_add;
    ImageView img_browser;
    EditText edt_drink_name, edt_drink_price;

    Uri select_uri = null;
    String uploaded_img_path = "";
    String repath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_list);

        mService = Common.getAPI();

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAddCategoryDialog();
            }
        });


        recycler_drinks = findViewById(R.id.recycler_drinks);
        recycler_drinks.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_drinks.setHasFixedSize(true);

        loadListDrink(Common.category.getId());
    }

    private void loadListDrink(String id) {
        compositeDisposable.add(mService.getDrink(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrinkList(drinks);
                    }
                }));
    }

    private void displayDrinkList(List<Drink> drinks) {
        DrinkListAdapter adapter = new DrinkListAdapter(this, drinks);
        recycler_drinks.setAdapter(adapter);
    }

    private void showAddCategoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Product");

        View view = LayoutInflater.from(this).inflate(R.layout.add_new_product_layout, null);
        edt_drink_name = view.findViewById(R.id.edt_drink_name);
        edt_drink_price = view.findViewById(R.id.edt_drink_price);
        img_browser = view.findViewById(R.id.img_browser);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);

        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FILE_REQUEST);
            }
        });



        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                repath =null;
                uploaded_img_path =null;
                select_uri = null;
            }
        }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (edt_drink_name.getText().toString().isEmpty()) {
                    Toast.makeText(DrinkListActivity.this, "Please enter name of product", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edt_drink_price.getText().toString().isEmpty()) {
                    Toast.makeText(DrinkListActivity.this, "Please enter price of product", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uploaded_img_path.isEmpty()) {
                    Toast.makeText(DrinkListActivity.this, "Please select image of product", Toast.LENGTH_SHORT).show();
                    return;
                }

                compositeDisposable.add(mService.addNewProduct(edt_drink_name.getText().toString()
                        , uploaded_img_path,edt_drink_price.getText().toString(),Common.category.Id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                loadListDrink(Common.category.getId());
                                uploaded_img_path = "";
                                select_uri = null;

                            }
                        }));


            }
        }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data != null) {
                    select_uri = data.getData();
                    if (select_uri != null && !select_uri.getPath().isEmpty()) {
                        repath = getRealPathFromURI(select_uri);
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(select_uri);
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

    private void uploadFileToServer() {
        if (select_uri != null) {

            File file = new File(repath);
            String file_path = file.getAbsolutePath();
            String [] mangtenfile = file_path.split("\\.");
            file_path = mangtenfile[0] + System.currentTimeMillis() + "." +mangtenfile[1];

            final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/from-data"),file);// xác nhận lại kiểu dữ liệu

            final MultipartBody.Part body = MultipartBody.Part.createFormData("upload",file_path,requestBody);
            mService.uploadProductFile(body)
                    .enqueue(new Callback<FileName>() {
                        @Override
                        public void onResponse(Call<FileName> call, Response<FileName> response) {
                            Common.fileName = response.body();

                            uploaded_img_path = new StringBuilder(Common.BASE_URL)
                                    .append("Sever/product_img/")
                                    .append(Common.fileName.getFile()).toString();

                            Common.fileName =null;


                        }

                        @Override
                        public void onFailure(Call<FileName> call, Throwable t) {
                            Log.d("sss",t.getMessage());
                        }
                    });


        }
    }

    @Override
    protected void onResume() {
        loadListDrink(Common.category.getId());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

}

