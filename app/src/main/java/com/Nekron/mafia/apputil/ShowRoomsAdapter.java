package com.Nekron.mafia.apputil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Nekron.mafia.MainActivity;
import com.Nekron.mafia.R;

import java.util.List;

public class ShowRoomsAdapter extends RecyclerView.Adapter<ShowRoomsAdapter.ShowRoomsAdapterViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<String> rooms;

    public ShowRoomsAdapter(MainActivity context, List<String> rooms){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.rooms = rooms;
    }


    @Override
    public ShowRoomsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = inflater.inflate(R.layout.row_item_rooms, parent, false);
        ShowRoomsAdapterViewHolder viewHolder = new ShowRoomsAdapterViewHolder(v);
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(ShowRoomsAdapterViewHolder holder, int position){
        holder.personRooms.setText(rooms.get(position));
    }
    public void update(List<String> list){
        rooms = list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return rooms.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class ShowRoomsAdapterViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personRooms;


        ShowRoomsAdapterViewHolder(View itemView) {
            super(itemView);
            cv =  itemView.findViewById(R.id.card_rooms);
            personRooms =  itemView.findViewById(R.id.rooms);

        }


    }
}
