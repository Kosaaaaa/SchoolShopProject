package com.example.shopproject.models;

import java.util.ArrayList;

public class OrderModel {
    final static private String tableName = "sale_order";
    private int id;
    private int user_id;
    private double sum_total;
    private ArrayList<OrderLineModel> orderLines;

    public boolean isEmpty() {
        return orderLines.isEmpty();
    }

    public int getId() {
        return id;
    }

    public double getSum_total() {
        return sum_total;
    }

    public int getUser_id() {
        return user_id;
    }

    public static String getTableName() {
        return tableName;
    }

    public OrderModel(int user_id) {
        this.user_id = user_id;
        orderLines = new ArrayList<>();
    }

    public void addOrderLine(ProductModel productId, double qty) {
        orderLines.add(new OrderLineModel(this, productId, qty));
        sum_total += productId.getPrice() * qty;
    }

    public ArrayList<OrderLineModel> getOrderLines() {
        return orderLines;
    }

    public static String getCreateTableQuery() {
        return String.format("CREATE TABLE %s (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    user_id INTEGER NOT NULL,\n" +
                "    sum_total REAL DEFAULT 0,\n" +
                "    order_date TEXT, \n" +
                "    FOREIGN KEY (user_id) REFERENCES %s(id) ON DELETE CASCADE\n" +
                ");", getTableName(), UserModel.getTableName());
    }

}
