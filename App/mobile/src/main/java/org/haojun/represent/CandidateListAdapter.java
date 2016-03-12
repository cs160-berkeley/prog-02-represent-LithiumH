package org.haojun.represent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetView;

import java.io.ByteArrayInputStream;
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
        final Map<String, String> candidate = getItem(pos);
        Context context = getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.individual_candidate_list_item, parent, false);
        }
        if (candidate != null) {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.candidate);
            ImageView pic = (ImageView) convertView.findViewById(R.id.candidatePic);
            TextView name = (TextView) convertView.findViewById(R.id.candidateName);
            TextView email = (TextView) convertView.findViewById(R.id.candidateEmail);
            TextView website = (TextView) convertView.findViewById(R.id.candidateWebsite);
            TextView party = (TextView) convertView.findViewById(R.id.candidateParty);

            final ViewGroup parentView = (ViewGroup) convertView.findViewById(R.id.candidate);
            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override
                public void success(Result<AppSession> result) {
                    AppSession session = result.data;
                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                    StatusesService service = twitterApiClient.getStatusesService();
                    service.userTimeline(null, candidate.get("twitter_id"), 1, null, null, null, null, null, null,
                            new Callback<List<Tweet>>() {
                                @Override
                                public void success(Result<List<Tweet>> result) {
                                    for (int i = parentView.getChildCount() - 1; i >= 0; i--) {
                                        if (parentView.getChildAt(i) instanceof TweetView)
                                            return;
                                    }
                                    TweetView tweetView = new TweetView(getContext(), result.data.get(0));
                                    parentView.addView(tweetView);
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    Log.e("Twitter", e.getMessage());
                                }
                            });
                }

                @Override
                public void failure(TwitterException e) {

                }
            });


            if (layout != null && pic != null && name != null && email != null && website != null) {
                layout.setBackgroundColor(
                        candidate.get("party").equals("Democrat") ? context.getResources().getColor(R.color.democratBlue)
                                : candidate.get("party").equals("Republican") ? context.getResources().getColor(R.color.republicanRed)
                                : context.getResources().getColor(R.color.independentGreen));
                String bytes = candidate.get("pic");
                String[] byteValues = bytes.substring(1, bytes.length() - 1).split(",");
                byte[] buffer = new byte[byteValues.length];
                for (int i = 0; i < byteValues.length; i ++) {
                    buffer[i] = Byte.parseByte(byteValues[i].trim());
                }
                Drawable imgSrc = Drawable.createFromStream(new ByteArrayInputStream(buffer), "src");
                pic.setImageDrawable(imgSrc);
                name.setText(candidate.get("name"));
                email.setText(candidate.get("email"));
                website.setText(candidate.get("website"));
                party.setText(candidate.get("party"));
            }
        }
        return convertView;
    }
}
