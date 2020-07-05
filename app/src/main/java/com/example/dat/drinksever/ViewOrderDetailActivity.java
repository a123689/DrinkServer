package com.example.dat.drinksever;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dat.drinksever.Adapter.OrderDetailAdapter;
import com.example.dat.drinksever.Model.DataMessage;
import com.example.dat.drinksever.Model.MyResponse;
import com.example.dat.drinksever.Model.Order;
import com.example.dat.drinksever.Model.Token;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Retrofit.IFCMService;
import com.example.dat.drinksever.Utils.Common;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderDetailActivity extends AppCompatActivity {


    TextView txt_order_id, txt_order_price, txt_order_comment, txt_order_address,txt_order_phone;
    Spinner spinner_order_status;
    RecyclerView recycler_order_detail;
    Toolbar toolbar;

    IFCMService ifcmService;

    String[] spinner_source = new String[]{
            "Cancelled",//index 0
            "Placed",//index 1
            "Processed",//index 2
            "Shipping",//index 3
            "Shipped",//index 4
    };

    IDrinkShopAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_detail);


        mService = Common.getAPI();
        ifcmService = Common.getFCMService();
       // toolbar = findViewById(R.id.toolbar_detail);
     //   setSupportActionBar(toolbar);
        txt_order_id = findViewById(R.id.txt_order_id);
        txt_order_price = findViewById(R.id.txt_order_price);
        txt_order_comment = findViewById(R.id.txt_order_comment);
        txt_order_address = findViewById(R.id.txt_order_address);
        txt_order_phone = findViewById(R.id.txt_order_phone);

        spinner_order_status = findViewById(R.id.spinner_order_status);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                spinner_source
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_order_status.setAdapter(arrayAdapter);

        spinner_order_status = findViewById(R.id.spinner_order_status);
        recycler_order_detail = findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setLayoutManager(new LinearLayoutManager(this));
        recycler_order_detail.setHasFixedSize(true);

        txt_order_id.setText(new StringBuilder("#")
                .append(Common.currentOrder.getOrderId()));

        txt_order_price.setText(new StringBuilder("$")
                .append(Common.currentOrder.getOrderPrice()));

        txt_order_address.setText(Common.currentOrder.getOrderAddress());
        txt_order_comment.setText(Common.currentOrder.getOrderComment());
        txt_order_phone.setText(Common.currentOrder.getUserPhone());


        OrderDetailAdapter adapter = new OrderDetailAdapter(this);
        recycler_order_detail.setAdapter(adapter);

        setSpinnerSelectBaseOnStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_order_detail){
            saveUpdateOrder();
        }

        return true;

    }

    private void setSpinnerSelectBaseOnStatus() {
        switch (Common.currentOrder.getOrderStatus()) {
            case -1:
                spinner_order_status.setSelection(0);//Cancelled
                break;
            case 0:
                spinner_order_status.setSelection(1);//Placed
                break;
            case 1:
                spinner_order_status.setSelection(2);//Processed
                break;
            case 2:
                spinner_order_status.setSelection(3);//Shipping
                break;
            case 3:
                spinner_order_status.setSelection(4);//Shipped
                break;
        }
    }
    private void saveUpdateOrder() {

        final int orderStatus = spinner_order_status.getSelectedItemPosition() - 1;

        compositeDisposable.add(mService.updateOrderStatus(Common.currentOrder.getUserPhone(),
                Common.currentOrder.getOrderId(),
                orderStatus
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        sendOrderUpdateNotification(Common.currentOrder, orderStatus);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", throwable.getMessage());
                    }
                }));

    }

    public void sendOrderUpdateNotification(final Order order, final int order_status){

        finish();

        mService.getToken(order.getUserPhone(),"0")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                        Token token = response.body();
                        DataMessage dataMessage = new DataMessage();
                        Map<String,String> datasend = new HashMap<>();
                        datasend.put("title","Your order has been update");
                        datasend.put("message","Order #"+order.getOrderId()+" has been updated to "+Common.convertCodeToStatus(order_status));
                        dataMessage.to = token.getToken();
                        dataMessage.setData(datasend);
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if(response.body().success ==  1){
                                            Toast.makeText(ViewOrderDetailActivity.this, "Order  updated!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });

                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {
                        Log.d("dat123",t.getMessage());
                    }
                });
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
