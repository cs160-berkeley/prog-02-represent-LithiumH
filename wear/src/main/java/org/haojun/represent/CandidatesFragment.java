package org.haojun.represent;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CandidatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CandidatesFragment extends Fragment {
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "party";
    private static final String ARG_PARAM3 = "pic";


    private String _name;
    private String _party;
    private byte[] _pic;


    public CandidatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Name of the candidate.
     * @param party party of the candidate.
     * @param pic bitmap of the candidate.
     * @return A new instance of fragment CandidatesFragment.
     */
    public static CandidatesFragment newInstance(String name, String party, byte[] pic) {
        Log.d("T", String.format("initiating new fragment for %s, %s", name, party));

        CandidatesFragment fragment = new CandidatesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, party);
        args.putByteArray(ARG_PARAM3, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _name = getArguments().getString(ARG_PARAM1);
            _party = getArguments().getString(ARG_PARAM2);
            _pic = getArguments().getByteArray(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_candidates, container, false);
        view.setBackgroundColor(_party.equals("Democrat") ? getResources().getColor(R.color.democratBlue)
                : _party.equals("Republican") ? getResources().getColor(R.color.republicanRed)
                : getResources().getColor(R.color.independentGreen));

        ImageView pic = (ImageView) view.findViewById(R.id.pic);
        XTextView name = (XTextView) view.findViewById(R.id.name);
        XTextView party = (XTextView) view.findViewById(R.id.party);

        Drawable picDrawable = Drawable.createFromStream(new ByteArrayInputStream(_pic), "src");
        pic.setImageDrawable(picDrawable);
        name.setText(_name);
        party.setText(_party);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(getActivity().getApplicationContext(),
                        WatchToMobileService.class);
                sendIntent.putExtra("type", "candidate");
                sendIntent.putExtra("name", _name);
                getActivity().startService(sendIntent);
            }
        });
        return view;
    }

}
