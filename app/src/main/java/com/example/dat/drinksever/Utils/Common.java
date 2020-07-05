package com.example.dat.drinksever.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dat.drinksever.Adapter.DrinkListAdapter;
import com.example.dat.drinksever.Model.Category;
import com.example.dat.drinksever.Model.Drink;
import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Model.Order;
import com.example.dat.drinksever.Retrofit.FCMRetrofitClient;
import com.example.dat.drinksever.Retrofit.IDrinkShopAPI;
import com.example.dat.drinksever.Retrofit.IFCMService;
import com.example.dat.drinksever.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public static final String BASE_URL = "https://phandat123689.000webhostapp.com/";
    public static  final String FCM_URl="https://fcm.googleapis.com/";
    public static IDrinkShopAPI getAPI() {

        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static IFCMService getFCMService(){
        return FCMRetrofitClient.getClient(FCM_URl).create(IFCMService.class);
    }
    public static FileName fileName = null;
    public static Order currentOrder = null;
    public static Category category;
    public static Drink currenDrink;
    public static String curenuser = "Admin";
    public static List<Category> menuList = new ArrayList<>();


    public static String convertCodeToStatus(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Cacelled";
            default:
                return "Order Error";
        }
    }
}
