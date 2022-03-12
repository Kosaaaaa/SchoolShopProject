package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopproject.models.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    Context context;
    ArrayList<ProductModel> products;
    LayoutInflater layoutInflater;
    ImageView imageView;
    TextView textView;
    TextView descriptionView;

    public ProductAdapter(Context context, ArrayList<ProductModel> products) {
        super();
        this.context = context;
        this.products = products;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        convertView = layoutInflater.inflate(R.layout.my_spinner_items, null);
        imageView = convertView.findViewById(R.id.imageView);
        textView = convertView.findViewById(R.id.textView);
        descriptionView = convertView.findViewById(R.id.descriptionView);

        ProductModel product = products.get(i);

        imageView.setImageBitmap(product.getImage());
        textView.setText(product.getName());
        descriptionView.setText(product.getDescription());
        return convertView;
    }
}
