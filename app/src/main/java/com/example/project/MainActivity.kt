package com.example.project

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.project.adapter.DishAdapter
import com.example.project.adapter.UserAdapter
import com.example.project.data.entity.User
import kotlin.random.Random

import android.content.Intent
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var IsvDish: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IsvDish = findViewById(R.id.IsvDish)
        val arr = ArrayList<Dish>()

        arr.add(Dish(1, "Informasi"))
        arr.add(Dish(2, "Form Entry"))
        arr.add(Dish(3, "Lihat Data"))
        arr.add(Dish(4, "Keluar"))

        val adapter = DishAdapter(this, 0, arr)
        IsvDish.adapter = adapter

        IsvDish.setOnItemClickListener { _, _, position, _ ->
            val clickedDish = arr[position]
            if (clickedDish.id == 1) {
                val intent = Intent(this@MainActivity, InformasiActivity::class.java)
                startActivity(intent)
            }else if (clickedDish.id == 2) {
                val intent = Intent(this@MainActivity, EditorActivity::class.java)
                startActivity(intent)
            }else if (clickedDish.id == 3) {
                val intent = Intent(this@MainActivity, ViewActivity::class.java)
                startActivity(intent)
            }else if (clickedDish.id == 4) {
                finish()
            }
        }
    }
}