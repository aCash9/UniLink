package com.example.unilink.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.activity.EventsActivity;
import com.example.unilink.activity.ProfileActivity;
import com.example.unilink.callback.IntegerCallback;

public class SearchFragment extends Fragment {
    public SearchFragment() {
        // Required empty public constructor
    }


    private FirebaseController controller;
    private androidx.appcompat.widget.SearchView searchView;
    private TextView number_of_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = inflate.findViewById(R.id.search_view);
        controller = new FirebaseController();
        number_of_users = inflate.findViewById(R.id.number_of_users);

        controller.getNumberOfUser(count -> {
            number_of_users.setText(number_of_users.getText() + " " + String.valueOf(count));
        });

        Button events = inflate.findViewById(R.id.events);
        events.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            startActivity(intent);
        });

        Toast toast = new Toast(getContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast_box, container.findViewById(R.id.viewContainer));
        toast.setView(view);
        TextView promptTextview = view.findViewById(R.id.promptText);
        ImageView imageView = view.findViewById(R.id.action_image);
        toast.setDuration(Toast.LENGTH_LONG);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.findUser(query, userProfile -> {
                    if(userProfile != null && userProfile.getUserUID() != null) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("user", userProfile.getUserUID());
                        startActivity(intent);
                    } else {
                        promptTextview.setText("User not found");
                        imageView.setImageResource(R.drawable.baseline_error_24);
                        toast.show();
                        searchView.setQuery("", false);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return inflate;
    }
}