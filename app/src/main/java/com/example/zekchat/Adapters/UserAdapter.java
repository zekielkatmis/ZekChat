package com.example.zekchat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zekchat.Fragments.OtherUserFragment;
import com.example.zekchat.Models.Users;
import com.example.zekchat.R;
import com.example.zekchat.Utils.ChangeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<String> userKeyList;
    Activity activity;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public UserAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        this.activity = activity;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    // Define layout
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false);
        return new ViewHolder(view);
    }

    // Setting to views
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //holder.user_name.setText(userKeyList.get(position).toString());

        //We can get user information from here.
        reference.child("Users").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                Picasso.get().load(users.getProfilePhoto()).into(holder.user_image);
                holder.user_name.setText(users.getProfileUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.userMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeFragment fragment = new ChangeFragment(context);
                fragment.changeWithParameter(new OtherUserFragment(), userKeyList.get(position));

            }
        });
    }

    // List size of Adapter
    @Override
    public int getItemCount() {
        return userKeyList.size();
    }

    // Define of views.
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CircleImageView user_image;
        LinearLayout userMainLayout;

        ViewHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            user_image = itemView.findViewById(R.id.user_image);
            userMainLayout = itemView.findViewById(R.id.user_main_layout);
        }
    }
}
