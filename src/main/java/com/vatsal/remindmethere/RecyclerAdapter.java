package com.vatsal.remindmethere;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private static final String TAG = "RecyclerAdapter";

    private Context context;
    List<geofences> List;
    DatabaseHelper db;
    private static onClickListner onclicklistner;


    public RecyclerAdapter(java.util.List<geofences> List) {
        this.List = List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public RecyclerAdapter(Context context, List<geofences> List) {
        this.context = context;
        this.List = List;
        db = new DatabaseHelper(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        geofences currentItem = List.get(position);
         String unitDistance;
        String unitMax;
        int distance = currentItem.getRadius();
        int max = holder.seekbar.getMax();
        if(distance >= 1000){ unitDistance = " kM";
            distance = distance /1000;}
        else{
            unitDistance = " M";}
        if(max >= 1000){ unitMax = " kM";
            max = max/1000;}
        else{unitMax = " M";}


        holder.textView.setText(currentItem.getLocation());
         //   holder.imageButton.setOnClickListener();
        holder.rowCountTextView.setText(String.valueOf(position));
        //  holder.rowCountTextView.setText(currentItem.getId());
//toogle button
        if(currentItem.getToggle()==1){holder.toggle.setChecked(true); }
        if(currentItem.getToggle()==0){holder.toggle.setChecked(false);}

        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
               onclicklistner.onSwitchClick(currentItem.getID(),bChecked,position);

            }
        });
//seelbar
        holder.seekbar.setProgress(currentItem.getRadius());
        holder.SeekBarTextView.setText(distance + unitDistance +" / "+ max+unitMax);
//seekbar change listener
        holder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;
                int dis,max=holder.seekbar.getMax();
                String unitDis;
                String unitMax;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;

                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    max=holder.seekbar.getMax();
                   dis = progressChangedValue;
                    if(dis >= 1000){ unitDis = " kM";
                        dis = dis /1000;}
                    else{
                        unitDis = " M";}

                    if(max >= 1000){ unitMax = " kM";
                        max = max /1000;}
                    else{
                        unitMax = " M";}
                   onclicklistner.onSeekbarChange(currentItem.getID(),progressChangedValue,position);
                    holder.SeekBarTextView.setText(dis + unitDis +" / "+ max +unitMax);
                    //currentItem.setRadius(progressChangedValue);
               }
          });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        SeekBar seekbar;
        ImageButton imageButton;
        TextView textView, rowCountTextView, SeekBarTextView;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch toggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

               imageButton = itemView.findViewById(R.id.imageButton);
            toggle = (Switch) itemView.findViewById(R.id.switch1);
            textView = itemView.findViewById(R.id.textView);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);
            SeekBarTextView = itemView.findViewById(R.id.SeekBarTextView);
            seekbar = (SeekBar) itemView.findViewById(R.id.seekBar);

            imageButton.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {

          //  onclicklistner.onClick(getAdapterPosition(), v);
            return true;
        }


        @Override
        public void onClick(View v) {

            onclicklistner.onClick(getAdapterPosition(),v);
         }
    }


    public interface onClickListner {

        void onSeekbarChange(String id, int radius, int pos);
        void onSwitchClick(String id,boolean b,int pos);
        void onClick(int position, View v);
    }

    public void setOnItemClickListener(onClickListner onclicklistner) {
        RecyclerAdapter.onclicklistner = onclicklistner;
    }

}