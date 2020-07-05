package com.example.dat.drinksever;

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

import com.example.dat.drinksever.Adapter.MenuAdapter;
import com.example.dat.drinksever.Model.Category;
import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Utils.Common;
import com.example.dat.drinksever.Utils.UploadCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack {

    RecyclerView recycler_menu;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IDrinkShopAPI mService;

    final int REQUEST_PERMISSION_CODE = 123;

    EditText edt_name;
    ImageView img_brower;
    Uri select_uri = null;
    String uploaded_img_path = "";
    final int PICK_FROM_GALLERY =123;
    String repath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });
        FloatingActionButton fabChat = findViewById(R.id.fabChat);
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this,ListChatActivity.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mService = Common.getAPI();
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_menu.setHasFixedSize(true);

        getMenu();
        updateTokenServer();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                   // Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else{

                }
                  //  Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_show_order) {
            startActivity(new Intent(this,ShowOrderActivity.class));
        }else if (id == R.id.nav_shipper) {
            startActivity(new Intent(this,ShiperManagementActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getMenu() {
        compositeDisposable.add(mService.getMenu().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenuList(categories);
                    }
                }));
    }

    private void displayMenuList(List<Category> categories) {
        Common.menuList = categories;
        MenuAdapter adapter = new MenuAdapter(this, categories);
        recycler_menu.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        getMenu();
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

    private void showAddCategoryDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        View view = LayoutInflater.from(this).inflate(R.layout.add_category_layout, null);
        edt_name = view.findViewById(R.id.edt_name);
        img_brower = view.findViewById(R.id.img_browser);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);

        img_brower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
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


                if (edt_name.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter name of category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uploaded_img_path.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select image of category", Toast.LENGTH_SHORT).show();
                    return;
                }

                compositeDisposable.add(mService.addNewCategory(edt_name.getText().toString()
                        , uploaded_img_path)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                getMenu();
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
            if (requestCode == PICK_FROM_GALLERY) {
                if (data != null) {
                    select_uri = data.getData();
                    if (select_uri != null && !select_uri.getPath().isEmpty()) {
                        repath = getRealPathFromURI(select_uri);
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(select_uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            img_brower.setImageBitmap(bitmap);
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
            mService.uploadCategoryFile(body)
                    .enqueue(new Callback<FileName>() {
                        @Override
                        public void onResponse(Call<FileName> call, Response<FileName> response) {
                            Common.fileName = response.body();

                                    uploaded_img_path = new StringBuilder(Common.BASE_URL)
                                            .append("Sever/category_img/")
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
    public void onProgressUpdate(int pertantage) {

    }


    private void updateTokenServer() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        mService.updateToken("server_app_01", instanceIdResult.getToken(), "1")
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.d("DEBUG", response.body());
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.e("DEBUG", t.getMessage());
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
