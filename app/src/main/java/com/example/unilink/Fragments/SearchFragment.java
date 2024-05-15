package com.example.unilink.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.activity.ProfileActivity;
import com.example.unilink.callback.userProfileCallback;
import com.example.unilink.objects.UserProfile;

public class SearchFragment extends Fragment {
    public SearchFragment() {
        // Required empty public constructor
    }


    private FirebaseController controller;
    private SearchView searchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = inflate.findViewById(R.id.search_view);
        controller = new FirebaseController();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.findUser(query, userProfile -> {
                    if(userProfile != null && userProfile.getUserUID() != null) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("user", userProfile.getUserUID());
                        startActivity(intent);
                    } else {
                        searchView.setQuery("", false);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return inflate;
    }
}