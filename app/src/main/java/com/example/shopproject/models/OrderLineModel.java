package com.example.shopproject.models;

public class OrderLineModel {
    final static private String tableName = "sale_order_line";

    private int id;
    private OrderModel order_id;
    private ProductModel product_id;
    private double qty;


    public OrderLineModel(OrderModel order_id, ProductModel product_id, double qty) {
        this.order_id = order_id;
        this.product_id = product_id;
        this.qty = qty;
    }

    public ProductModel getProduct_id() {
        return product_id;
    }

    public OrderModel getOrder_id() {
        return order_id;
    }

    public double getQty() {
        return qty;
    }

    public static String getTableName() {
        return tableName;
    }

    public Double getPriceTotal() {
        return this.qty * this.product_id.getPrice();
    }

    public static String getCreateTableQuery() {
        return String.format("CREATE TABLE %s (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    order_id INTEGER NOT NULL,\n" +
                "    product_id INTEGER NOT NULL,\n" +
                "    qty REAL NOT NULL,\n" +
                "    FOREIGN KEY (order_id) REFERENCES %s(id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (product_id) REFERENCES %s(id) ON DELETE RESTRICT\n" +
                ");", getTableName(), OrderModel.getTableName(), ProductModel.getTableName());
    }

}
