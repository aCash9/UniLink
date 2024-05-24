package com.example.unilink.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.R;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.recyclerAdapters.RecyclerMarketProductAdapter;
import com.example.unilink.recyclerAdapters.RecyclerPostAdapter;

public class MarketPlaceFragment extends Fragment {


    public MarketPlaceFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_market_place, container, false);
        controller = new FirebaseController();
        Toolbar toolbar = inflate.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        lottieAnimationView = inflate.findViewById(R.id.animationView);
        loading(false);

        controller.getMarketPlaceProducts(posts -> {
            RecyclerMarketProductAdapter adapter = new RecyclerMarketProductAdapter(getActivity(), posts);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        return inflate;
    }
    private void loading(boolean loading){
        if(loading) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        } else {
            lottieAnimationView.pauseAnimation();
            lottieAnimationView.setVisibility(View.GONE);
        }
    }
}