package biz.davidnelson.music.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import biz.davidnelson.music.R;
import biz.davidnelson.music.VolleyRequestQueue;
import biz.davidnelson.music.adapters.SongListAdapter;
import biz.davidnelson.music.fragments.SearchDialogFragment;
import biz.davidnelson.music.fragments.SongDetailFragment;

/**
 * An activity representing a list of Songs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SongDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity
                        implements SearchDialogFragment.SearchDialogListener,
                                   SongListAdapter.SongListClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private final static String URL = "https://itunes.apple.com/search?term=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(android.R.drawable.ic_menu_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    final DialogFragment fragment = new SearchDialogFragment();
                    fragment.show(getFragmentManager(), "search");
            }
        });

        if (findViewById(R.id.song_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onSearchCriteriaEntered(String searchString) {
        String searchUrl;
        try {
            searchUrl = URL + URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(searchUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.song_list);

                    JSONArray results = null;
                    try {
                        results = response.getJSONArray("results");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (results != null) recyclerView.setAdapter(
                        new SongListAdapter(getApplicationContext(), results, MainActivity.this));
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(SearchDialogFragment.class.getSimpleName(), "got error from JsonArray");
                    error.printStackTrace();
                }
            }
        );

        VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public void onSongClicked(String artist, String track, String album) {

        final Bundle args = new Bundle();
        args.putString("artist", artist);
        args.putString("track", track);
        args.putString("album", album);

        if (mTwoPane) {
            final SongDetailFragment fragment = new SongDetailFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                .replace(R.id.song_detail_container, fragment)
                .commit();
        } else {
            final Intent intent = new Intent(getApplicationContext(), SongDetailActivity.class);
            intent.putExtra("info", args);
            startActivity(intent);
        }


    }
}
