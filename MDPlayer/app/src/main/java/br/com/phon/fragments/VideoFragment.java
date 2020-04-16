package br.com.phon.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import br.com.phon.R;
import android.widget.ImageButton;
import android.widget.VideoView;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.app.Activity;
import android.support.v4.provider.DocumentFile;
import android.net.Uri;
import java.util.List;
import java.util.ArrayList;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class VideoFragment extends android.support.v4.app.Fragment implements android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnInfoListener, View.OnLongClickListener{
	private ImageButton btnPlay, btnOpenFolder, btnRepeatOpt, btnVolume, btnAddFile, btnPrev, btnNext;
	private VideoView mVideoView;
	
	private static final int NOVO = 1;
	private static final int TOCANDO = 2;
	private static final int PAUSADO = 3;
	private int status = NOVO;
	private static final int REQUEST_OPEN_FOLDER = 4;
	private static final int REQUEST_ADD_FILE = 44;
	private List<Uri> videos;
	private MediaPlayer mp;
	private int idx;
	private static final int NO_REPEAT = 5;
	private static final int REPEAT_ALL = 6;
	private static final int REPEAT_ONE = 7;
	private int repeatMode = NO_REPEAT;
	private boolean isMuted;

	private static final String TAG = "VideoFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_video, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		btnPlay = getView().findViewById(R.id.btnPlay);
		btnOpenFolder = getView().findViewById(R.id.btnOpenFolder);
		btnRepeatOpt = getView().findViewById(R.id.btnRepeatOpt);
		btnVolume = getView().findViewById(R.id.btnVolume);
		btnAddFile = getView().findViewById(R.id.btnAddFile);
		btnPrev = getView().findViewById(R.id.btnSkipPrevious);
		btnNext = getView().findViewById(R.id.btnSkipNext);
		mVideoView = getView().findViewById(R.id.mVideoView);
		
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);
		
		btnPlay.setOnLongClickListener(this);
		btnOpenFolder.setOnLongClickListener(this);
		btnRepeatOpt.setOnLongClickListener(this);
		btnVolume.setOnLongClickListener(this);
		btnAddFile.setOnLongClickListener(this);
		btnPrev.setOnLongClickListener(this);
		btnNext.setOnLongClickListener(this);
		getView().findViewById(R.id.btnInternet).setOnLongClickListener(this);
		
		btnOpenFolder.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_OPEN_FOLDER);
			}
		});
		
		btnPlay.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (status == NOVO) {
					start();
				} else if (status == TOCANDO) {
					pause();
				} else if (status == PAUSADO) {
					play();
				}
			}
		});
		
		btnRepeatOpt.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if (repeatMode == NO_REPEAT) {
					repeatMode = REPEAT_ONE;
					btnRepeatOpt.setImageResource(R.drawable.repeat_one);
				} else if (repeatMode == REPEAT_ONE) {
					repeatMode = REPEAT_ALL;
					btnRepeatOpt.setImageResource(R.drawable.repeat);
				} else {
					repeatMode = NO_REPEAT;
					btnRepeatOpt.setImageResource(R.drawable.shuffle);
				}
			}
		});
		
		btnVolume.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				isMuted = !isMuted;
				setMuted(isMuted);
			}
		});
		
		getView().findViewById(R.id.btnInternet).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				InputDialogFragment.show(getChildFragmentManager(), new InputDialogFragment.Callback() {
					public void call(final String url) {
						SelectModeDialogFragment.show(getFragmentManager(), new SelectModeDialogFragment.Callback() {
							public void call(final int which) {
								openBrowser(url, which);
							}
						});
					}
				});
			}
		});
		
		btnAddFile.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("video/*");
				startActivityForResult(intent, REQUEST_ADD_FILE);
			}
		});
		
		btnPrev.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				if (videos != null && videos.size() >= 2) {
					if(idx == 0) {
						idx = videos.size() -1;
						status = NOVO;
						start();
					} else {
						idx--;
						status = NOVO;
						start();
					}
				}
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				if (videos != null && videos.size() >= 2)
					nextVideo();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OPEN_FOLDER && resultCode == Activity.RESULT_OK) {
			videos = new ArrayList<>();
			status = NOVO;
			DocumentFile[] files = DocumentFile.fromTreeUri(getActivity(), data.getData()).listFiles();
			for (DocumentFile file : files) {
				String type = file.getType();
				if(file.isDirectory() || (type != null && !type.matches("video.*")))
					continue;
				videos.add(file.getUri());
			}
			Toast.makeText(getActivity(), String.format("%d vídeo(s) encontrado.", videos.size()), Toast.LENGTH_SHORT).show();
		} else if (requestCode == REQUEST_ADD_FILE && resultCode == Activity.RESULT_OK) {
			if(videos == null) videos = new ArrayList<>();
			status = NOVO;
			videos.add(data.getData());
			idx = 0;
			String msg = String.format("Vídeo adicionado com sucesso. Total de %d vídeos", videos.size());
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void start() {
		if(videos == null || videos.size() <= 0) return;
		mVideoView.setVideoURI(videos.get(idx));
		mVideoView.start();
		btnPlay.setImageResource(R.drawable.pause);
		status = TOCANDO;
	}
	
	private void pause() {
		if(videos == null || videos.size() <= 0 && mVideoView.canPause())
			return;
		mVideoView.pause();
		btnPlay.setImageResource(R.drawable.play_arrow);
		status = PAUSADO;
	}
	
	private void play() {
		try {
			if(videos == null || videos.size() <= 0)
				return;
			mVideoView.start();
			btnPlay.setImageResource(R.drawable.pause);
			status = TOCANDO;
		}catch(Exception e){
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	public void setMuted(boolean b) {
		if (mp != null) {
			if (b) {
				mp.setVolume(0f, 0f);
				btnVolume.setImageResource(R.drawable.volume_off);
			} else {
				mp.setVolume(1f, 1f);
				btnVolume.setImageResource(R.drawable.volume_up);
			}
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int p2, int p3) {
		this.mp = mp;
		setMuted(isMuted);
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		nextVideo();
	}

	private void nextVideo() {
		switch (repeatMode) {
			case REPEAT_ONE:
				status = NOVO;
				start();
				break;
			case REPEAT_ALL:
				if (idx + 1 >= videos.size()) 
					idx = 0;
				else
					idx++;

				status = NOVO;
				start();
				break;
			case NO_REPEAT:
				if (idx + 1 < videos.size()) {
					idx++;
					status = NOVO;
					start();
				} else {
					status = NOVO;
					idx = 0;
					btnPlay.setImageResource(R.drawable.play_arrow);
				}
				break;
		}
	}

	@Override
	public boolean onLongClick(View btn) {
		int id = btn.getId();
		switch(id) {
			case R.id.btnPlay:
			Toast.makeText(getActivity(), "Iniciar/Parar reprodução.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnOpenFolder:
			Toast.makeText(getActivity(), "Abrir pasta", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnRepeatOpt:
			if(repeatMode == NO_REPEAT)
				Toast.makeText(getActivity(), "Não repete os vídeos.", Toast.LENGTH_SHORT).show();
			else if(repeatMode == REPEAT_ONE)
				Toast.makeText(getActivity(), "Repete este vídeo.", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getActivity(), "Repete todos vídeo.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnVolume:
			Toast.makeText(getActivity(), "Ativar/Desativar o som", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnAddFile:
			Toast.makeText(getActivity(), "Adicionar apenas um arquivo a lista de reprodução.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnSkipPrevious:
			Toast.makeText(getActivity(), "Voltar um vídeo.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnSkipNext:
			Toast.makeText(getActivity(), "Avançar um vídeo.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.btnInternet:
			Toast.makeText(getActivity(), "Abrir vídeo da web.", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;
	}

	
	protected abstract void openBrowser(String url, int which)
}
