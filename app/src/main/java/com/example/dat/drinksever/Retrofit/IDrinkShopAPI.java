package com.example.dat.drinksever.Retrofit;

import com.example.dat.drinksever.Model.Category;
import com.example.dat.drinksever.Model.Drink;
import com.example.dat.drinksever.Model.FileName;
import com.example.dat.drinksever.Model.Order;
import com.example.dat.drinksever.Model.Token;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    @Headers("Content-Type: application/json")
    @GET("getMenu.php")
    Observable<List<Category>> getMenu();

    @FormUrlEncoded
    @POST("Sever/add_category.php")
    Observable<String> addNewCategory(@Field("name") String name,
                                      @Field("imgPath") String imgPath);

    @Multipart
    @POST("Sever/upload_category_img.php")
    Call<FileName> uploadCategoryFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("Sever/update_category.php")
    Observable<String> updateNewCategory(@Field("id") String id,
                                         @Field("name") String name,
                                         @Field("imgPath") String imgPath);

    @FormUrlEncoded
    @POST("Sever/detele_category.php")
    Observable<String> deleteCategory(@Field("id") String id);

    @FormUrlEncoded
    @POST("getDrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuID);

    @FormUrlEncoded
    @POST("Sever/add_product.php")
    Observable<String> addNewProduct(@Field("name") String name,
                                     @Field("imgPath") String imgPath,
                                     @Field("price") String price,
                                     @Field("menuId") String menuId);
    @Multipart
    @POST("Sever/upload_product_img.php")
    Call<FileName> uploadProductFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("Sever/update_product.php")
    Observable<String> updateProduct(@Field("id") String id,
                                     @Field("name") String name,
                                     @Field("imgPath") String imgPath,
                                     @Field("price") String price,
                                     @Field("menuId") String menuId
    );

    @FormUrlEncoded
    @POST("Sever/delete_product.php")
    Observable<String> deleteProduct(@Field("id") String id);

    @FormUrlEncoded
    @POST("Sever/getOrder.php")
    Observable<List<Order>> getAllOrder(
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("Sever/updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken
    );

    @FormUrlEncoded
    @POST("Sever/update_status.php")
    Observable<String> updateOrderStatus(@Field("phone") String phone,
                                         @Field("order_id") long orderId,
                                         @Field("status") int status
    );

    @FormUrlEncoded
    @POST("getToken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken
    );
}
