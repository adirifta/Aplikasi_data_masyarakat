package com.example.project.adapter

import android.app.Dialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.data.entity.User
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class UserAdapter(var list: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private lateinit var dialog: Dialog

    fun setDialog(dialog: Dialog){
        this.dialog = dialog
    }

    interface Dialog {
        fun onClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var nik: TextView
        var fullName: TextView
        var phone: TextView
        var jenis_kelamin: TextView
        var tanggal: TextView
        var alamat: TextView
        var gambar: ShapeableImageView

        init {
            nik = view.findViewById(R.id.nik)
            fullName = view.findViewById(R.id.full_name)
            phone = view.findViewById(R.id.phone)
            jenis_kelamin = view.findViewById(R.id.jenis_kelamin)
            tanggal = view.findViewById(R.id.tanggal)
            alamat = view.findViewById(R.id.alamat)
            gambar = view.findViewById(R.id.gambar)
            view.setOnClickListener{
                dialog.onClick(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.nik.text = user.nik
        holder.fullName.text = user.fullName
        holder.phone.text = user.phone
        holder.jenis_kelamin.text = user.jeniskelamin
        holder.tanggal.text = user.tanggal
        holder.alamat.text = user.alamat

        // Memuat gambar dari ByteArray ke ShapeableImageView menggunakan Picasso
        user.gambarUri?.let { imageByteArray ->
            val dishBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            holder.gambar.setImageBitmap(dishBitmap)
        }
    }
}