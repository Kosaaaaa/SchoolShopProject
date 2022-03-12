package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopproject.models.OrderModel;
import com.example.shopproject.models.ProductModel;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private DbConnector dbConnector;
    private double priceTotal = 0;
    private CartAdapter cartAdapter;
    private OrderModel currentOrder;
    private TextView priceTotalEt;

    private ProductModel currentProduct;
    private Slider qtySlider;

    private int uid = -1;
    private final String CURRENCY = "PLN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.uid = extras.getInt(LOGGED_USER_ID);
        } else {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            uid = sharedPref.getInt(LOGGED_USER_ID, -1);
        }

        if (uid <= 0) {
            log_info("user not logged, switching to login activity");
            switchActivity(LoginUserActivity.class);
        }

        currentOrder = new OrderModel(uid);
        dbConnector = new DbConnector(this);

        Spinner spinner = findViewById(R.id.spinner);
        ListView cartListView = findViewById(R.id.cart_list_view);
        qtySlider = findViewById(R.id.qty_slider);
        priceTotalEt = findViewById(R.id.priceTotal);
        Button orderAddBtn = findViewById(R.id.order_add_btn);
        Button orderBtn = findViewById(R.id.orderBtn);

        orderAddBtn.setOnClickListener(this::onProductAdded);
        orderBtn.setOnClickListener(this::onCreateOrder);

        ArrayList<ProductModel> allProducts = new ArrayList<>(Arrays.asList(dbConnector.getAllProducts()));

        if (allProducts.isEmpty()) {
            dbConnector.createSampleProducts();
        }

        allProducts.add(0, new ProductModel());

        ProductAdapter spinnerAdapter = new ProductAdapter(getApplicationContext(), allProducts);
        cartAdapter = new CartAdapter(getApplicationContext(), currentOrder.getOrderLines());

        spinner.setAdapter(spinnerAdapter);
        cartListView.setAdapter(cartAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentProduct = allProducts.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    @SuppressLint("SetTextI18n")
    private void onProductAdded(View view) {
        if (currentProduct.getId() <= 0) {
            return;
        }

        log_info("product added to cart: " + currentProduct.toString());

        currentOrder.addOrderLine(currentProduct, qtySlider.getValue());
        cartAdapter.notifyDataSetChanged();
        log_info(String.valueOf(cartAdapter.getCount()));

        priceTotal += currentProduct.getPrice() * qtySlider.getValue();
        priceTotalEt.setText(priceTotal + " " + CURRENCY);
    }

    private void onCreateOrder(View view) {
        if (!currentOrder.isEmpty()) {
            Pair<Integer, String> responsePair = dbConnector.addOrder(currentOrder);
            Toast.makeText(view.getContext(), responsePair.second, Toast.LENGTH_SHORT).show();
            clearOrder();
        }
    }

    private void clearOrder() {
        currentOrder = new OrderModel(uid);
        cartAdapter.orderLines = currentOrder.getOrderLines();
        cartAdapter.notifyDataSetChanged();
        priceTotalEt.setText("0");
    }
}