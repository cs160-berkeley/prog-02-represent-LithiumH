package org.haojun.represent;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;

/** This adapter will populate the GridViewPager of the watch
 * Created by Haojun on 2/29/16.
 */
public class RepresentGridViewPagerAdapter extends FragmentGridPagerAdapter {

    public RepresentGridViewPagerAdapter(FragmentManager fm, ArrayList<Bundle> bundles) {
        super(fm);
        for (int i = 0; i < 2; i ++)
            _fragments.add(new ArrayList<Fragment>());
        for (Bundle bundle : bundles) {
            if ("candidate".equals(bundle.getString("id"))) {
                Fragment fragment = CandidatesFragment.newInstance(
                        bundle.getString("name"), bundle.getString("party"),
                        bundle.getByteArray("pic"));
                _fragments.get(0).add(fragment);
            } else {
                for (String key : bundle.keySet()) {
                }
                Fragment fragment = VoteFragment.newInstance(bundle.getString("state"),
                        bundle.getString("county"), bundle.getString("obama"),
                        bundle.getString("romney"));
                _fragments.get(1).add(fragment);
            }
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        return _fragments.get(row).get(col);
    }

    @Override
    public int getRowCount() {
        return _fragments.size();
    }

    @Override
    public int getColumnCount(int i) {
        return _fragments.get(i).size();
    }

    private ArrayList<ArrayList<Fragment>> _fragments = new ArrayList<>();
}
