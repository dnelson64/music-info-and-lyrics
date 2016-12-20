package biz.davidnelson.music.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biz.davidnelson.music.R;
import biz.davidnelson.music.VolleyRequestQueue;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {

    private Context mContext;
    private JSONArray mSongs;
    private final ImageLoader imageLoader = VolleyRequestQueue.getInstance(mContext).getImageLoader();
    private static SongListClickListener mSongClickListener;

    public interface SongListClickListener {
        void onSongClicked(String artist, String track, String album, String albumImageUrl);
    }

    public SongListAdapter(@NonNull Context context, @NonNull JSONArray songs, SongListClickListener songClickListener) {
        mContext = context;
        mSongs = songs;
        mSongClickListener = songClickListener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        final JSONObject item;

        try {
            item = mSongs.getJSONObject(position);

            holder.mTrackName.setText(item.getString("trackName"));
            holder.mArtistName.setText(item.getString("artistName"));
            holder.mAlbumName.setText(item.getString("collectionName"));

            final String imageUrl = item.getString("artworkUrl100");

            holder.mAlbumImageUrl = imageUrl;

            if (!TextUtils.isEmpty(imageUrl))
                imageLoader.get(imageUrl, ImageLoader.getImageListener(holder.mAlbumImage, 0, 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mSongs.length();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {

        TextView mTrackName;
        TextView mArtistName;
        TextView mAlbumName;
        ImageView mAlbumImage;

        String mAlbumImageUrl;

        SongViewHolder(View v) {
            super(v);

            mAlbumImage = (ImageView) v.findViewById(R.id.iv_albumImage);
            mTrackName = (TextView) v.findViewById(R.id.tv_songName);
            mArtistName = (TextView) v.findViewById(R.id.tv_artistName);
            mAlbumName = (TextView) v.findViewById(R.id.tv_albumName);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSongClickListener.onSongClicked(mArtistName.getText().toString(),
                        mTrackName.getText().toString(), mAlbumName.getText().toString(),
                        mAlbumImageUrl);

                }
            });
        }

    }
}
