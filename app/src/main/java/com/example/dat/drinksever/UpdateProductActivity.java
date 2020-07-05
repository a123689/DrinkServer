package com.example.dat.drinksever;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dat.drinksever.Model.Category;
import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Utils.Common;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

public class UpdateProductActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1111;
    private ImageView img_browser;
    private EditText edt_drink_price, edt_drink_name;
    private MaterialSpinner spinner_menu;

    private Button btn_update, btn_delete;

    //getMenu
    HashMap<String, String> menu_data_for_get_key = new HashMap<>();
    HashMap<String, String> menu_data_for_get_value = new HashMap<>();

    //get List
    List<String> menu_data = new ArrayList<>();

    //API
    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable;

    Uri select_uri = null;
    String repath = "";
    String uploaded_img_path = "", selected_category = "";
    final int REQUEST_PERMISSION_CODE =657;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);


        if (Common.currenDrink != null) {
            uploaded_img_path = Common.currenDrink.getLink();
            selected_category = Common.currenDrink.getMenuId();
        }

        //getAPI
        mService = Common.getAPI();
        compositeDisposable = new CompositeDisposable();
        //init layout
        init();


        //getImage
        img_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                permisstion();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FILE_REQUEST);
            }
        });

        spinner_menu.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selected_category = menu_data_for_get_key.get(menu_data.get(position));
            }
        });



        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct();
            }
        });

        setSpinnerMenu();
        setProductInfo();
    }

    private void init() {
        img_browser = findViewById(R.id.img_browser);
        edt_drink_name = findViewById(R.id.edt_drink_name);
        edt_drink_price = findViewById(R.id.edt_drink_price);
        spinner_menu = findViewById(R.id.spinner_menu_id);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);

    }

    private void permisstion(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
    }



    private void deleteProduct() {
        compositeDisposable.add(mService.deleteProduct(
                Common.currenDrink.getId()
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(UpdateProductActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(UpdateProductActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }));
    }

    private void updateProduct() {

        compositeDisposable.add(mService.updateProduct(
                Common.currenDrink.getId(),
                edt_drink_name.getText().toString().trim(),
                uploaded_img_path,
                edt_drink_price.getText().toString().trim(),
                selected_category
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(UpdateProductActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(UpdateProductActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }));
    }

    private void setProductInfo() {
        if (Common.currenDrink != null) {
            edt_drink_name.setText(Common.currenDrink.getName());
            edt_drink_price.setText(Common.currenDrink.getPrice());

            Picasso.with(this).load(Common.currenDrink.getLink()).into(img_browser);
            spinner_menu.setSelectedIndex(menu_data.indexOf(menu_data_for_get_value.get(Common.category.getId())));

        }
    }

    private void setSpinnerMenu() {

        for (Category category : Common.menuList) {
            menu_data_for_get_key.put(category.getName(), category.getId());
            menu_data_for_get_value.put(category.getId(), category.getName());

            menu_data.add(category.getName());
        }
        spinner_menu.setItems(menu_data);

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

}
