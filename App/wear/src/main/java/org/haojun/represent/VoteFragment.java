package org.haojun.represent;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoteFragment extends Fragment {
    private static final String ARG_PARAM1 = "state";
    private static final String ARG_PARAM2 = "county";
    private static final String ARG_PARAM3 = "obama";
    private static final String ARG_PARAM4 = "romney";

    private String _state;
    private String _county;
    private String _obama;
    private String _romney;

    public VoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param state The current state.
     * @param county The current county.
     * @param obama Obama's vote.
     * @param romney Romney's vote.
     * @return A new instance of fragment VoteFragment.
     */
    public static VoteFragment newInstance(String state, String county, String obama,
                                           String romney) {
        VoteFragment fragment = new VoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, state);
        args.putString(ARG_PARAM2, county);
        args.putString(ARG_PARAM3, obama);
        args.putString(ARG_PARAM4, romney);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _state = getArguments().getString(ARG_PARAM1);
            _county = getArguments().getString(ARG_PARAM2);
            _obama = getArguments().getString(ARG_PARAM3);
            _romney = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vote, container, false);

        XTextView countyState = (XTextView) view.findViewById(R.id.county_state);
        XTextView obama = (XTextView) view.findViewById(R.id.obama);
        XTextView romney = (XTextView) view.findViewById(R.id.romney);

        countyState.setText(String.format("%s, %s", _county, _state));
        obama.setText(_obama);
        romney.setText(_romney);

        return view;
    }

}
