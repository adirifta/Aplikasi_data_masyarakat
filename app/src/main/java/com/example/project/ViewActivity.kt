package com.example.project

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.project.adapter.UserAdapter
import com.example.project.data.AppDatabase
import com.example.project.data.entity.User

class ViewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var database: AppDatabase
    private var list = mutableListOf<User>()
    private lateinit var editSearch : EditText
    private lateinit var btnSearch : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        recyclerView = findViewById(R.id.recycler_view)
        editSearch = findViewById(R.id.edit_search)
        btnSearch = findViewById(R.id.btn_search)

        database = AppDatabase.getInstance(applicationContext)
        adapter = UserAdapter(list)
        adapter.setDialog(object : UserAdapter.Dialog{
            override fun onClick(position: Int) {
                //Membuat Dialog View
                val dialog = AlertDialog.Builder(this@ViewActivity)
                dialog.setTitle(list[position].nik)
                dialog.setItems(R.array.items_option, DialogInterface.OnClickListener{ dialog, which ->
                    if (which==0){
                        //Code Edit
//                        val intent = Intent(this@ViewActivity, EditorActivity::class.java)
//                        intent.putExtra("id", list[position].id)
//                        startActivity(intent)
//                    } else if (which==1){
//                        ///code Delete
                        database.userDao().delete(list[position])
                        getData()
                    } else{
                        //Code Batal
                        dialog.dismiss()
                    }
                })
                //Menampilkan Dialog
                val dialogView = dialog.create()
                dialogView.show()
            }

        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(applicationContext, VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL))

        btnSearch.setOnClickListener{
            if (editSearch.text.isNotEmpty()){
                serachData(editSearch.text.toString())
            }else{
                getData()
                Toast.makeText(applicationContext, "Silahkan isi terlebih dahulu!", LENGTH_SHORT).show()
            }
        }
        editSearch.setOnKeyListener{ v, keyCode, event ->
            if (editSearch.text.isNotEmpty()){
                serachData(editSearch.text.toString())
            }else{
                getData()
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getData(){
        list.clear()
        list.addAll(database.userDao().getAll())
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun serachData(search : String){
        list.clear()
        list.addAll(database.userDao().searchByName(search))
        adapter.notifyDataSetChanged()
    }
}