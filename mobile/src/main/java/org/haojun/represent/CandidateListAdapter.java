package org.haojun.represent;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** This list adapter will populate the AllCandidates view
 * Created by Haojun on 2/29/16.
 */
public class CandidateListAdapter extends ArrayAdapter<Map<String,String>> {
    public CandidateListAdapter(Context context, int resource,
                                List<Map<String, String>> candidateList) {
        super(context, resource, candidateList);
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        Context context = getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.individual_candidate_list_item, parent, false);
        }
        Map<String, String> candidate = getItem(pos);
        if (candidate != null) {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.candidate);
            ImageView pic = (ImageView) convertView.findViewById(R.id.candidatePic);
            TextView name = (TextView) convertView.findViewById(R.id.candidateName);
            TextView email = (TextView) convertView.findViewById(R.id.candidateEmail);
            TextView website = (TextView) convertView.findViewById(R.id.candidateWebsite);
            TextView tweet = (TextView) convertView.findViewById(R.id.candidateTweet);

            if (layout != null && pic != null && name != null && email != null && website != null
                    && tweet != null) {
                layout.setBackgroundColor(
                        candidate.get("party").equals("Democrat") ? context.getResources().getColor(R.color.democratBlue)
                        : candidate.get("party").equals("Republican") ? context.getResources().getColor(R.color.republicanRed)
                        : context.getResources().getColor(R.color.independentGreen));
                Drawable imgSrc = null;
                try {
                    URL profile = new URL(candidate.get("picuri"));
                    imgSrc= Drawable.createFromStream(profile.openStream(), "src");
                } catch (MalformedInputException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pic.setImageDrawable(imgSrc);
                name.setText(candidate.get("name"));
                email.setText(candidate.get("email"));
                website.setText(candidate.get("website"));
                tweet.setText(candidate.get("tweet"));
            }
        }
        return convertView;
    }
}
