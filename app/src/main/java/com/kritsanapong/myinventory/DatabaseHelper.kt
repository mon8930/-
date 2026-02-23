package com.kritsanapong.myinventory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME    = "Inventory.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_PRODUCTS  = "products"
        const val COLUMN_ID       = "id"
        const val COLUMN_NAME     = "name"
        const val COLUMN_PRICE    = "price"
        const val COLUMN_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_QUANTITY INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    // CREATE
    fun insertProduct(name: String, price: Double, quantity: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PRICE, price)
            put(COLUMN_QUANTITY, quantity)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    // READ ALL
    fun getAllProducts(): MutableList<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, "$COLUMN_NAME ASC")
        if (cursor.moveToFirst()) {
            do {
                products.add(
                    Product(
                        id       = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        price    = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return products
    }

    // UPDATE
    fun updateProduct(id: Int, name: String, price: Double, quantity: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PRICE, price)
            put(COLUMN_QUANTITY, quantity)
        }
        return db.update(TABLE_PRODUCTS, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    // DELETE
    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_PRODUCTS, "$COLUMN_ID=?", arrayOf(id.toString()))
    }
}