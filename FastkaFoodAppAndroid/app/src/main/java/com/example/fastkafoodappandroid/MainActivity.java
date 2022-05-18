package com.example.fastkafoodappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fastkafoodappandroid.Adapter.CategoryAdapter;
import com.example.fastkafoodappandroid.Adapter.RecommendedAdapter;
import com.example.fastkafoodappandroid.Adapter.SliderAdapter;
import com.example.fastkafoodappandroid.Model.CategoryData;
import com.example.fastkafoodappandroid.Model.FoodData;
import com.example.fastkafoodappandroid.Model.SliderData;
import com.example.fastkafoodappandroid.UI.CartActivity;
import com.example.fastkafoodappandroid.account.LoginActivity;
import com.example.fastkafoodappandroid.account.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnSignOut;
    private FirebaseUser user;
    private DatabaseReference reference;

    private RecyclerView.Adapter adapter, adapter2;
    private RecyclerView recyclerViewCategotyList, recyclerViewPopularList;

    public static final String TAG = "GoogleSignIn";


    SliderView sliderView;

    String url1 = "https://s3-ap-southeast-1.amazonaws.com/storage.adpia.vn/affiliate_document/multi/giftpop-lotte-special-sale-2021.jpg";
    String url2 = "https://cdn-www.vinid.net/2020/02/20200218_Voucher-Lotteria_TopBannerWeb_1920x1080.jpg";
    String url3 = "https://blog.utop.vn/uploads/125060732118012021.jpg";
    String url4 = "https://i.ibb.co/Mnx6dmr/4095-detail-2.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        //
        recyclerViewCategoty();
        recyclerViewPopular();
        bottomNavigation();
        //
        reference = FirebaseDatabase.getInstance().getReference("Users");
        String userId = user.getUid();

        //

        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        sliderView =findViewById(R.id.image_slider);
        // adding the urls inside array list
        sliderDataArrayList.add(new SliderData(url1));
        sliderDataArrayList.add(new SliderData(url2));
        sliderDataArrayList.add(new SliderData(url3));
        sliderDataArrayList.add(new SliderData(url4));

        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);

        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
//
       final TextView txtWelcome = (TextView) findViewById(R.id.txt_Welcome);
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String name = userProfile.username;
                    String email = userProfile.email;

                    txtWelcome.setText("Hi, " + name + " !");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something wrong happened !", Toast.LENGTH_SHORT).show();
            }
        });

//
       DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id){

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        goToLoginActivity();

                    default:
                        return true;
                }

            }

        });
        //
      //  ImageView avatarUser = findViewById(R.id.avatarUser);
       // TextView txtName = findViewById(R.id.txtNameApp);
        SharedPreferences preferences = this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userName = preferences.getString("username","");
        String userEmail = preferences.getString("useremail", "");
        String userImageUrl = preferences.getString("userPhoto","");

        txtWelcome.setText(userName);
       // txtName.setText(userEmail);
       // Glide.with(this).load(userImageUrl).into(avatarUser);

    }

//
    private void goToLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    //
    private void recyclerViewPopular() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPopularList = findViewById(R.id.view2);
        recyclerViewPopularList.setLayoutManager(linearLayoutManager);

        ArrayList<FoodData> foodlist = new ArrayList<>();
        foodlist.add(new FoodData("Pepperoni pizza", "pizza1", "slices pepperoni ,mozzarella cheese, fresh oregano,  ground black pepper, pizza sauce", 13.0, 5, 20, 1000));
        foodlist.add(new FoodData("Chesse Burger", "burger", "beef, Gouda Cheese, Special sauce, Lettuce, tomato ", 15.20, 4, 18, 1500));
        foodlist.add(new FoodData("Vagetable pizza", "pizza3", " olive oil, Vegetable oil, pitted Kalamata, cherry tomatoes, fresh oregano, basil", 11.0, 3, 16, 800));

        adapter2 = new RecommendedAdapter(foodlist);
        recyclerViewPopularList.setAdapter(adapter2);
    }

    private void recyclerViewCategoty() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategotyList = findViewById(R.id.view1);
        recyclerViewCategotyList.setLayoutManager(linearLayoutManager);

        ArrayList<CategoryData> categoryList = new ArrayList<>();
        categoryList.add(new CategoryData("Pizza", "cat_1"));
        categoryList.add(new CategoryData("Burger", "cat_2"));
        categoryList.add(new CategoryData("Hotdog", "cat_3"));
        categoryList.add(new CategoryData("Drink", "cat_4"));
        categoryList.add(new CategoryData("Donut", "cat_5"));

        adapter = new CategoryAdapter(categoryList);
        recyclerViewCategotyList.setAdapter(adapter);

    }
    private void bottomNavigation() {
        LinearLayout homeBtn=findViewById(R.id.homeBtn1);
        LinearLayout cartBtn=findViewById(R.id.cartBtn1);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MainActivity.class));
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
    }
}


//        btnSignOut = findViewById(R.id.btnsignout);
//        btnSignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            }
//        });
//
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//      String  userId = user.getUid();
//
//
//        final TextView idUser = (TextView) findViewById(R.id.idUser);
//        final TextView nameUser = (TextView) findViewById(R.id.nameUser);
//        final TextView emailUser = (TextView) findViewById(R.id.emailUser);
//
//        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProfile = snapshot.getValue(User.class);
//                if (userProfile != null){
//                    String name = userProfile.username;
//                    String email = userProfile.email;
//
//                    nameUser.setText(name);
//                    emailUser.setText(email);
//                    idUser.setText("Welcome" + name +"!");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MainActivity.this,"Something wrong happened !",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}