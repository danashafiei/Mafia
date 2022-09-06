package com.Nekron.mafia.apputil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.Nekron.mafia.R;
import com.Nekron.mafia.RoomActivity;

import java.util.List;


public class GetRoleAdapter extends RecyclerView.Adapter<GetRoleAdapter.GetRoleAdapterViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    List<String> userRole;


    public GetRoleAdapter(RoomActivity context, List<String> userRole){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.userRole = userRole;

    }

    @Override
    public GetRoleAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = inflater.inflate(R.layout.row_item2, parent, false);
        GetRoleAdapterViewHolder viewHolder = new GetRoleAdapterViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(GetRoleAdapterViewHolder holder, int position) {
        holder.personUserRole.setText(userRole.get(position));
    }
    public void update(List<String> list){
        userRole = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userRole.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class GetRoleAdapterViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personUserRole;


        GetRoleAdapterViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_user_role);
            personUserRole = (TextView) itemView.findViewById(R.id.user_role);

        }


    }
}


