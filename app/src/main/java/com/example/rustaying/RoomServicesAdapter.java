package com.example.rustaying;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RoomServicesAdapter extends RecyclerView.Adapter<RoomServicesAdapter.ViewHolder> {
    private static final String TAG = "RoomServicesAdapter";

    private ArrayList<Service> serviceList;
    private Context mContext;

    public RoomServicesAdapter(Context mContext, ArrayList<Service> serviceList){
        this.serviceList = serviceList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_room_services,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i){
        Log.d(TAG, "onBindViewHolder: Called");

        Service info = serviceList.get(i);

        viewHolder.requestType.setText(info.getRequestType());
        viewHolder.requestDate.setText(info.getRequestDate());
        viewHolder.requestTime.setText(info.getRequestedTimeRoomService());
        viewHolder.checkbox.setText(info.getCheckboxes());
        viewHolder.input.setText(info.getInputs());
    }

    @Override
    public int getItemCount(){
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView requestType, requestDate, requestTime,  checkbox, input;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            requestType = itemView.findViewById(R.id.requestTypeR);
            requestDate = itemView.findViewById(R.id.requestDateR);
            requestTime = itemView.findViewById(R.id.requestTimeR);
            checkbox = itemView.findViewById(R.id.checkboxesR);
            input = itemView.findViewById(R.id.inputsR);

        }
    }
}