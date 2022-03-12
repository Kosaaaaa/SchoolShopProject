package com.example.shopproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.shopproject.models.OrderLineModel;
import com.example.shopproject.models.OrderModel;
import com.example.shopproject.models.ProductModel;
import com.example.shopproject.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class DbConnector extends SQLiteOpenHelper {
    Context context;
    private static final String DATABASE_NAME = "shop.db";

    public DbConnector(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 8);
        this.context = context;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // user table
        db.execSQL(UserModel.getCreateTableQuery());

        // product table
        db.execSQL(ProductModel.getCreateTableQuery());

        // order table
        db.execSQL(OrderModel.getCreateTableQuery());

        // order line table
        db.execSQL(OrderLineModel.getCreateTableQuery());
    }

    public Pair<Integer, String> loginUser(String email, String password) {
        email = email.toLowerCase();
        String hashedPassword = sha256String(password);

        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("SELECT id FROM %s WHERE email = ? AND password = ?",
                UserModel.getTableName());
        Cursor cursor = db.rawQuery(query, new String[]{email, hashedPassword});
        int userId = -1;
        String msg;
        try {
            if (cursor.moveToFirst()) {
                Log.i(BaseActivity.BASE_LOG_TAG, cursor.toString());
                userId = cursor.getInt(0);
                msg = "login successfully";
            } else {
                msg = "invalid login credentials";
            }
        } catch (Exception e) {
            msg = "Error while trying to login: " + e;
            Log.d(BaseActivity.BASE_LOG_TAG, msg);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return Pair.create(userId, msg);
    }

    public Pair<Integer, String> registerUser(UserModel rawUserModel) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;
        String msg;
        db.beginTransaction();
        try {
            ContentValues values = rawUserModel.getContentValues();
            userId = db.insertOrThrow(UserModel.getTableName(), null, values);
            db.setTransactionSuccessful();
            msg = "Created new user: #" + userId;
            Log.i(BaseActivity.BASE_LOG_TAG, msg);
        } catch (SQLiteConstraintException e) {
            msg = "Error while trying to add user to database: email is not unique";
            Log.w(BaseActivity.BASE_LOG_TAG, msg);
        } catch (Exception e) {
            msg = "Error while trying to add user to database: " + e;
            Log.w(BaseActivity.BASE_LOG_TAG, msg);
        } finally {
            db.endTransaction();
        }
        return Pair.create((int) userId, msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + OrderLineModel.getTableName());
            db.execSQL("DROP TABLE IF EXISTS " + OrderModel.getTableName());
            db.execSQL("DROP TABLE IF EXISTS " + UserModel.getTableName());
            db.execSQL("DROP TABLE IF EXISTS " + ProductModel.getTableName());
            onCreate(db);
        }
    }

    public static String sha256String(@NonNull String source) {
        byte[] hash = null;
        String hashCode = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(source.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.e(BaseActivity.BASE_LOG_TAG, "Can't calculate SHA-256");
        }

        if (hash != null) {
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(b);
                if (hex.length() == 1) {
                    hashBuilder.append("0");
                    hashBuilder.append(hex.charAt(0));
                } else {
                    hashBuilder.append(hex.substring(hex.length() - 2));
                }
            }
            hashCode = hashBuilder.toString();
        }
        return hashCode;
    }

    public ProductModel[] getAllProducts() {
        List<ProductModel> products = new ArrayList<>();

        String PRODUCT_SELECT_QUERY = String.format("SELECT * FROM %s;",
                ProductModel.getTableName());

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PRODUCT_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") ProductModel newProduct = new ProductModel(
                            cursor.getInt(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("description")),
                            cursor.getDouble(cursor.getColumnIndex("price")),
                            cursor.getBlob(cursor.getColumnIndex("image"))
                    );
                    products.add(newProduct);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(BaseActivity.BASE_LOG_TAG, "Error while trying to get products from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return products.toArray(new ProductModel[0]);
    }

    public Pair<Integer, String> addOrder(OrderModel rawOrderModel) {
        SQLiteDatabase db = getWritableDatabase();
        long orderId = -1;
        String msg;
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("user_id", rawOrderModel.getUser_id());
            values.put("sum_total", rawOrderModel.getSum_total());

            ZonedDateTime d = LocalDateTime.now().atZone(ZoneId.of("UTC"));
            String datetimeStr = DateTimeFormatter.ISO_DATE_TIME.format(d);
            // put current date time as String in ISO format
            values.put("order_date", datetimeStr);

            orderId = db.insertOrThrow(OrderModel.getTableName(), null, values);

            for (OrderLineModel orderLine : rawOrderModel.getOrderLines()) {
                values.clear();
                values.put("order_id", orderId);
                values.put("product_id", orderLine.getProduct_id().getId());
                values.put("qty", orderLine.getQty());
                db.insertOrThrow(OrderLineModel.getTableName(), null, values);
            }
            db.setTransactionSuccessful();

            msg = "Created new order: #" + orderId;
            Log.i(BaseActivity.BASE_LOG_TAG, msg);
        } catch (Exception e) {
            msg = "Error while trying to add order to database: " + e;
            Log.w(BaseActivity.BASE_LOG_TAG, msg);
        } finally {
            db.endTransaction();
        }
        return Pair.create((int) orderId, msg);
    }

    public void createSampleProducts() {
        Resources res = context.getResources();
        ArrayList<BitmapDrawable> images = new ArrayList<>();
        for (int imgId : new int[]{R.drawable.set_1, R.drawable.set_2, R.drawable.set_3}) {
            images.add((BitmapDrawable) ResourcesCompat.getDrawable(res, imgId, null));
        }

        ArrayList<ProductModel> sampleProducts = new ArrayList<>();
        int i = 1;
        for (BitmapDrawable img : images) {
            Bitmap imgBitmap = Objects.requireNonNull(img).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            sampleProducts.add(new ProductModel("Zestaw " + i, "To Opis Zestaw " + i, 2137.00 * i, imgBytes));
            i++;
        }


        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ProductModel product : sampleProducts) {
                ContentValues values = new ContentValues();
                values.put("name", product.getName());
                values.put("description", product.getDescription());
                values.put("price", product.getPrice());
                values.put("image", product.getImageBytes());
                db.insertOrThrow(ProductModel.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.w(BaseActivity.BASE_LOG_TAG, "Error while trying to sample products to database: " + e);
        } finally {
            db.endTransaction();
        }
    }
}
