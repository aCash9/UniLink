package com.example.unilink;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }

    TextView akash, suraj;
    Button account, notes, help, contribute, myPosts;
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_account, container, false);

        akash = inflate.findViewById(R.id.akash);
        suraj = inflate.findViewById(R.id.suraj);

        account = inflate.findViewById(R.id.account);
        notes = inflate.findViewById(R.id.notes);
        help = inflate.findViewById(R.id.help);
        contribute = inflate.findViewById(R.id.contribute);
        logout = inflate.findViewById(R.id.logout);
        myPosts = inflate.findViewById(R.id.myPosts);

        myPosts.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MyPosts.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });

        account.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AccountActivity.class);
            startActivity(intent);
        });

        notes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotesActivity.class);
            startActivity(intent);
        });

        help.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HelpActivity.class);
            startActivity(intent);
        });

        contribute.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContributeActivity.class);
            startActivity(intent);
        });

        akash.setOnClickListener(v -> {
            String url = "https://www.linkedin.com/in/srivastava-akash-hello";

            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        suraj.setOnClickListener(v -> {
            String url = "https://www.linkedin.com/in/suraj-g-meharwade-408b492a8";

            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(myIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No application can handle this request."
                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
        return inflate;
    }
}