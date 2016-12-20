package biz.davidnelson.music.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import biz.davidnelson.music.R;
import biz.davidnelson.music.VolleyRequestQueue;
import biz.davidnelson.music.activities.MainActivity;
import biz.davidnelson.music.activities.SongDetailActivity;

/**
 * A fragment representing a single Song detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link SongDetailActivity}
 * on handsets.
 */
public class SongDetailFragment extends Fragment {
    final static String LYRICS_URL = "http://lyrics.wikia.com/api.php?func=getSong&fmt=json&";

    String mArtist;
    String mTrack;
    String mAlbum;
    String mImageUrl;

    public SongDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();

        mArtist = args.getString("artist");
        mTrack = args.getString("track");
        mAlbum = args.getString("album");
        mImageUrl = args.getString("albumImageUrl");


        final Activity activity = this.getActivity();
        final CollapsingToolbarLayout appBarLayout =
            (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mTrack);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ImageLoader imageLoader =
            VolleyRequestQueue.getInstance(getActivity().getApplicationContext()).getImageLoader();

        View rootView = inflater.inflate(R.layout.song_detail, container, false);

        final TextView lyricsTextView = (TextView) rootView.findViewById(R.id.tv_lyrics);
        final TextView albumTextView = (TextView) rootView.findViewById(R.id.tv_albumName);
        final TextView artistTextView = (TextView) rootView.findViewById(R.id.tv_artistName);
        final TextView trackTextView = (TextView) rootView.findViewById(R.id.tv_songName);
        final ImageView albumImageView = (ImageView) rootView.findViewById(R.id.iv_albumImage);

        artistTextView.setText(mArtist);
        albumTextView.setText(mAlbum);
        trackTextView.setText(mTrack);

        if (!TextUtils.isEmpty(mImageUrl))
            imageLoader.get(mImageUrl, ImageLoader.getImageListener(albumImageView, 0, 0));

        String lyricsUrl = "";
        try {
            lyricsUrl = LYRICS_URL + "artist=" +
                URLEncoder.encode(mArtist, "UTF-8") + "&" + "song=" +
                URLEncoder.encode(mTrack, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = null;
        if (!TextUtils.isEmpty(lyricsUrl)) {

            request = new StringRequest(lyricsUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    // HACK, response is not valid json, but always seems
                    // to start with "song = " .... and then valid json
                    final String json = response.substring(7);

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        lyricsTextView.setText(jsonObject.getString("lyrics"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(SearchDialogFragment.class.getSimpleName(), "got error from JsonArray");
                        error.printStackTrace();
                    }
                });

        }

        if (request != null)
            VolleyRequestQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);

        return rootView;
    }
}
