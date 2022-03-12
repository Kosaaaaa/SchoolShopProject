package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopproject.models.OrderLineModel;
import com.example.shopproject.models.ProductModel;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    Context context;
    ArrayList<OrderLineModel> orderLines;
    LayoutInflater layoutInflater;
    ImageView imageView;
    TextView textView;
    TextView qtyView;
    TextView priceView;

    public CartAdapter(Context context, ArrayList<OrderLineModel> orderLines) {
        super();
        this.context = context;
        this.orderLines = orderLines;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderLines.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public void clear() {
        this.orderLines.clear();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"SetTextI18n", "InflateParams", "ViewHolder"})
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        convertView = layoutInflater.inflate(R.layout.cart_spinner_items, null);
        imageView = convertView.findViewById(R.id.cartImageView);
        textView = convertView.findViewById(R.id.cartNameView);
        qtyView = convertView.findViewById(R.id.cartQtyView);
        priceView = convertView.findViewById(R.id.cartPriceView);

        OrderLineModel orderLine = orderLines.get(i);
        ProductModel product = orderLine.getProduct_id();


        imageView.setImageBitmap(product.getImage());
        textView.setText(product.getName());
        qtyView.setText(Double.toString(orderLine.getQty()));
        priceView.setText(Double.toString(orderLine.getProduct_id().getPrice()));

        Button deleteBtn = convertView.findViewById(R.id.cartDeleteBtn);

        deleteBtn.setOnClickListener(view -> {
            orderLines.remove(i);
            this.notifyDataSetChanged();
        });

        return convertView;
    }
}
