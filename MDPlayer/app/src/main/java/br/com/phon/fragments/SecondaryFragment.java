package br.com.phon.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import br.com.phon.DeveloperKey;
import br.com.phon.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import android.util.Log;

public class SecondaryFragment extends VideoFragment {
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	private String url;
	private YouTubePlayer player;
	private final String TAG = "SecondaryFragment";

	@Override
	protected void openBrowser(String url, int which) {
		this.url = url;
		if (which == 0) {
			this.url = url.substring(url.lastIndexOf("/") +1);
			YouTubePlayerSupportFragment frag = new YouTubePlayerSupportFragment();
			getFragmentManager()
				.beginTransaction()
				.replace(R.id.secondaryLayout, frag, "YoutubePlayerFragment")
				.addToBackStack(null)
				.commit();
			frag.initialize(DeveloperKey.DEVELOPER_KEY, getOnInitializedListener());
		} else {
			BrowserFragment frag = new BrowserFragment();
			Bundle args = new Bundle();
			args.putString("url", url);
			frag.setArguments(args);
			getFragmentManager()
				.beginTransaction()
				.replace(R.id.secondaryLayout, frag)
				.addToBackStack(null)
				.commit();
		}
	}

	private YouTubePlayer.OnInitializedListener getOnInitializedListener() {
		return new YouTubePlayer.OnInitializedListener() {
			@Override
			public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
				if (!wasRestored) {
					player.cueVideo(url);
					player.setShowFullscreenButton(false);
					player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
					player.setPlaybackEventListener(getPlaybackEventListener());
					SecondaryFragment.this.player = player;
				}
			}

			@Override
			public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
				if (error.isUserRecoverableError()) {
					error.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
				} else {
					Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RECOVERY_DIALOG_REQUEST) {
			YouTubePlayerSupportFragment player = (YouTubePlayerSupportFragment) getFragmentManager()
				.findFragmentByTag("YoutubePlayerFragment");
			if(player != null) player.initialize(DeveloperKey.DEVELOPER_KEY, getOnInitializedListener());
		}
	}
	
	private YouTubePlayer.PlaybackEventListener getPlaybackEventListener() {
		return new YouTubePlayer.PlaybackEventListener(){
			@Override
			public void onPlaying() {}

			@Override
			public void onPaused() {
				try {
					if(player != null) player.play();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}

			@Override
			public void onStopped() {}

			@Override
			public void onBuffering(boolean p1) {}

			@Override
			public void onSeekTo(int p1) {}					
		};
	}
}
