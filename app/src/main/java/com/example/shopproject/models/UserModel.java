package com.example.shopproject.models;

import android.content.ContentValues;

public class UserModel {
    private int id;
    private String email;
    private String password;
    private String name;
    private String city;
    private String zip_code;
    private String address_1;
    private String address_2;

    final static private String tableName = "user";

    public UserModel(String email, String password, String name, String city, String zip_code, String address_1, String address_2) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.city = city;
        this.zip_code = zip_code;
        this.address_1 = address_1;
        this.address_2 = address_2;
    }

    public static String getTableName() {
        return tableName;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }


    public static String getCreateTableQuery() {
        return String.format("CREATE TABLE %s (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    email VARCHAR(255) NOT NULL UNIQUE,\n" +
                "    password VARCHAR(255) NOT NULL,\n" +
                "    name VARCHAR(255),\n" +
                "    city VARCHAR(255),\n" +
                "    zip_code VARCHAR(6),\n" +
                "    address_1 VARCHAR(255),\n" +
                "    address_2 VARCHAR(255)\n" +
                ");", getTableName());
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", getEmail());
        contentValues.put("password", getPassword());
        contentValues.put("name", getName());
        contentValues.put("city", getCity());
        contentValues.put("zip_code", getZip_code());
        contentValues.put("address_1", getAddress_1());
        contentValues.put("address_2", getAddress_2());
        return contentValues;
    }


}
