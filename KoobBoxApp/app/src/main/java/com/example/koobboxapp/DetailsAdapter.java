package com.example.koobboxapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

class DetailsHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
    public TextView title,subtitle,date,priority,type;
    public CheckBox selection;
    private ItemClickListener itemClickListener;
    public DetailsHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.title);
        subtitle=itemView.findViewById(R.id.subtitle);
        date=itemView.findViewById(R.id.date);
        priority=itemView.findViewById(R.id.priority);
        selection=itemView.findViewById(R.id.selection);
        type=itemView.findViewById(R.id.type);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }

}
public class DetailsAdapter extends RecyclerView.Adapter<DetailsHolder>{
    private ArrayList<String> data_title;
    private ArrayList<String> data_subtitle;
    private ArrayList<String> data_date;
    private ArrayList<String> datta_priority;
    private ArrayList<String> data_selection;
    private ArrayList<String> data_type;
    private Context context;

    public DetailsAdapter(ArrayList<String> data_title, ArrayList<String> data_subtitle, ArrayList<String> data_date, ArrayList<String> datta_priority, ArrayList<String> data_selection, ArrayList<String> data_type, Context context) {
        this.data_title = data_title;
        this.data_subtitle = data_subtitle;
        this.data_date = data_date;
        this.datta_priority = datta_priority;
        this.data_selection = data_selection;
        this.data_type = data_type;
        this.context = context;
    }

    @NonNull
    @Override
    public DetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.layout_list,parent,false);
        return new DetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsHolder holder, int position) {
        final String list1=data_title.get(position);
        final String list2=data_subtitle.get(position);
        final String list3=data_date.get(position);
        final String list4=datta_priority.get(position);
        final String list5=data_selection.get(position);
        final String list6=data_type.get(position);
        holder.title.setText(list1);
        holder.subtitle.setText(list2);
        holder.date.setText(list3);
        holder.priority.setText(list4);
        holder.selection.setText(list5);
        holder.type.setText(list6);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick)
                {
                    deleteData(list1,list2,list3,list4,list5,list6);

                }
            }
        });
    }

    private void deleteData(final String list1, final String list2, final String list3, final String list4, final String list5, final String list6) {
        final DetailsDatabase db=new DetailsDatabase(context);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this data??");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int d=db.deleteData(list1,list2,list3,list4,list5,list6);
                if(d!=-1)
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "Could Not Delete", Toast.LENGTH_SHORT).show();
                Intent i1=new Intent(context,MainActivity.class);
                ((Activity)context).finish();
                context.startActivity(i1);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
       builder.show();

    }

    @Override
    public int getItemCount() {
        return data_title.size();
    }
}