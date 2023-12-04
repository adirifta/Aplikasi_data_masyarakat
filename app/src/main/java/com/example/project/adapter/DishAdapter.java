package com.example.project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.Dish;
import com.example.project.EditorActivity;
import com.example.project.InformasiActivity;
import com.example.project.R;
import com.example.project.ViewActivity;

import java.util.ArrayList;
import java.util.List;

public class DishAdapter extends ArrayAdapter<Dish> {
    private Context ct;
    private ArrayList<Dish> arr;

    public DishAdapter(@NonNull Context context, int resource, @NonNull List<Dish> objects) {
        super(context, resource, objects);
        this.ct = context;
        this.arr = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(ct);
            convertView = inflater.inflate(R.layout.button_item_layout, parent, false);
        }

        if (arr.size() > 0) {
            Dish d = arr.get(position);
            Button itemButton = convertView.findViewById(R.id.itemButton);

            // Customize the Button's text and behavior here
            itemButton.setText(d.name);
            itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle button click action here
                    if (d.id == 1) {
                        Intent intent = new Intent(ct, InformasiActivity.class);
                        ct.startActivity(intent);
                    }else if (d.id == 2) {
                        Intent intent = new Intent(ct, EditorActivity.class);
                        ct.startActivity(intent);
                    }else if (d.id == 3) {
                        Intent intent = new Intent(ct, ViewActivity.class);
                        ct.startActivity(intent);
                    } else if (d.id == 4) {
                        showExitConfirmationDialog();
                    }
                }
            });
        }
        return convertView;
    }
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setTitle("Konfirmasi Keluar");
        builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Keluar dari aplikasi
                System.exit(0);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Batal keluar, tutup dialog
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
