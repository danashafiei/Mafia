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




    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostAdapterViewHolder>{
        private Context context;
        private LayoutInflater inflater;
        List<String> userInRoom;


         public PostAdapter(RoomActivity context, List<String> userInRoom){
             this.context = context;
             this.inflater = LayoutInflater.from(context);
             this.userInRoom = userInRoom;


        }

        @Override
        public PostAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = inflater.inflate(R.layout.row_item, parent, false);
            PostAdapterViewHolder viewHolder = new PostAdapterViewHolder(v);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(PostAdapterViewHolder holder, int position) {
            holder.personUserName.setText(userInRoom.get(position));


        }
        public void update(List<String> list){
             userInRoom = list;
             notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return userInRoom.size();

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
        public static class PostAdapterViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView personUserName;



            PostAdapterViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.card);
                personUserName = (TextView) itemView.findViewById(R.id.userInRoom);



            }


        }
    }

