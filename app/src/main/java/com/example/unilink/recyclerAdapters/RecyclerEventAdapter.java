package com.example.unilink.recyclerAdapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unilink.R;
import com.example.unilink.activity.ProfileActivity;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Event;
import com.example.unilink.objects.UserPosts;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerEventAdapter extends RecyclerView.Adapter<RecyclerEventAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Event> list;
    private final FirebaseController controller;
    private final FirebaseUser user;

    public RecyclerEventAdapter(Context context, ArrayList<Event> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerEventAdapter.ViewHolder holder, int position) {
        Event event = list.get(position);
        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());


        holder.register.setOnClickListener(v -> {
            String url = event.getLink();
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });


        if(user.getUid().equals(event.getClubID()))
                holder.delete.setVisibility(View.VISIBLE);

        holder.delete.setOnClickListener(v -> {
            controller.deleteEvent(event);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageButton  delete;
        Button register;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            register = itemView.findViewById(R.id.register);
            delete = itemView.findViewById(R.id.delete);
        }

    }
}
