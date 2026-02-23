package com.kritsanapong.myinventory

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ProductAdapter.OnProductActionListener {

    private lateinit var dbHelper: DatabaseHelper
    private var adapter: ProductAdapter? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener { showProductDialog(null) }

        loadProducts()
    }

    private fun loadProducts() {
        val products = dbHelper.getAllProducts()
        if (adapter == null) {
            adapter = ProductAdapter(products, this)
            recyclerView.adapter = adapter
        } else {
            adapter!!.updateList(products)
        }
    }

    override fun onEdit(product: Product) {
        showProductDialog(product)
    }

    override fun onDelete(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("ลบสินค้า")
            .setMessage("ต้องการลบ \"${product.name}\" ใช่หรือไม่?")
            .setPositiveButton("ลบ") { _, _ ->
                dbHelper.deleteProduct(product.id)
                loadProducts()
                Toast.makeText(this, "ลบสินค้าแล้ว", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun showProductDialog(product: Product?) {
        val isEdit = product != null
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product, null)

        val etName     = dialogView.findViewById<EditText>(R.id.etName)
        val etPrice    = dialogView.findViewById<EditText>(R.id.etPrice)
        val etQuantity = dialogView.findViewById<EditText>(R.id.etQuantity)

        if (isEdit) {
            etName.setText(product!!.name)
            etPrice.setText(product.price.toString())
            etQuantity.setText(product.quantity.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEdit) "แก้ไขสินค้า" else "เพิ่มสินค้า")
            .setView(dialogView)
            .setPositiveButton(if (isEdit) "บันทึก" else "เพิ่ม") { _, _ ->
                val name     = etName.text.toString().trim()
                val priceStr = etPrice.text.toString().trim()
                val qtyStr   = etQuantity.text.toString().trim()

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(qtyStr)) {
                    Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price    = priceStr.toDouble()
                val quantity = qtyStr.toInt()

                if (isEdit) {
                    dbHelper.updateProduct(product!!.id, name, price, quantity)
                    Toast.makeText(this, "แก้ไขสินค้าแล้ว", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertProduct(name, price, quantity)
                    Toast.makeText(this, "เพิ่มสินค้าแล้ว", Toast.LENGTH_SHORT).show()
                }
                loadProducts()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }
}