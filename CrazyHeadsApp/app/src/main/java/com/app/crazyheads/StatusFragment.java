package com.app.crazyheads;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Messi10 on 18-Jan-15.
 */
public class StatusFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    TextView textView;

    private int position;

    public static StatusFragment newInstance(int position) {
        StatusFragment f = new StatusFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        ViewCompat.setElevation(rootView, 50);
        textView= (TextView) rootView.findViewById(R.id.ticket_fragment_textView);
        textView.setText("CARD "+position);
        return rootView;
    }
}
