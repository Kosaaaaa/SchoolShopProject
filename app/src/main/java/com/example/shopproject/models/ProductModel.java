package com.example.shopproject.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.shopproject.BaseActivity;

import java.sql.Blob;
import java.sql.SQLException;

public class ProductModel {
    private int id;
    private String name;
    private String description;
    private Double price = 0.0;
    private byte[] image;
    final static private String tableName = "product";


    public static String getTableName() {
        return tableName;
    }


    public Bitmap getImage() {
        return convertBlob2Bitmap(this.image);
    }

    public byte[] getImageBytes() {
        return this.image;
    }

    private Bitmap convertBlob2Bitmap(byte[] blobImage) {
        if (blobImage != null) {
            return BitmapFactory.decodeByteArray(blobImage, 0, blobImage.length);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public ProductModel() {
    }

    public ProductModel(int id, String name, String description, byte[] image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public ProductModel(int id, String name, String description, Double price, byte[] image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public ProductModel(String name, String description, Double price, byte[] image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public static String getCreateTableQuery() {
        return String.format("CREATE TABLE %s (\n " +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name VARCHAR(255) NOT NULL,\n" +
                "    description TEXT,\n" +
                "    price REAL DEFAULT 0,\n" +
                "    image BLOB\n" +
                ");", getTableName());
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
