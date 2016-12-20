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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

    TextView mLyricsTextView;
    TextView mArtistTextView;
    TextView mAlbumTextView;
    TextView mTrackTextView;

    String mArtist;
    String mTrack;
    String mAlbum;

    public SongDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mArtist = args.getString("artist");
        mTrack = args.getString("track");
        mAlbum = args.getString("album");

        final Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mTrack);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_detail2, container, false);

        mLyricsTextView = (TextView) rootView.findViewById(R.id.tv_lyrics);
        mAlbumTextView = (TextView) rootView.findViewById(R.id.tv_albumName);
        mArtistTextView = (TextView) rootView.findViewById(R.id.tv_artistName);
        mTrackTextView = (TextView) rootView.findViewById(R.id.tv_songName);

        mArtistTextView.setText(mArtist);
        mAlbumTextView.setText(mAlbum);
        mTrackTextView.setText(mTrack);

        String lyricsUrl = "";
        try {
            lyricsUrl = LYRICS_URL + "artist=" +
                URLEncoder.encode(mArtist, "UTF-8") + "&" + "song=" +
                URLEncoder.encode(mTrack, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
// had some problem using standard lib to parse the wikia response as json, so will just put the
// whole response as the lyrics
//        JsonObjectRequest request = null;
        StringRequest request = null;
        if (!TextUtils.isEmpty(lyricsUrl)) {

            request = new StringRequest(lyricsUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("debug", "response is " + response);
                    mLyricsTextView.setText(response);
                }
            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(SearchDialogFragment.class.getSimpleName(), "got error from JsonArray");
                        error.printStackTrace();
                    }
                });

//            request = new JsonObjectRequest(lyricsUrl, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        Log.d("debug", response.toString());
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(SearchDialogFragment.class.getSimpleName(), "got error from JsonArray");
//                        error.printStackTrace();
//                    }
//                }
//            );
        }

        if (request != null)
            VolleyRequestQueue.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);

        return rootView;
    }
}
