package br.com.phon.fragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import br.com.phon.R;
import android.widget.TextView;
import android.util.Log;
import java.util.Calendar;

public class InfoFragment extends Fragment {
	
	private TextView tvHour;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		tvHour = getView().findViewById(R.id.tvHour);
		new Thread() {
			@Override
			public void run() {
				try {
					while(true)
						printHours();
				} catch (Exception e){
					Log.e("InfoFragment", e.getMessage(), e);
				}
			}

			private void printHours() throws InterruptedException {
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR);
				int minute = c.get(Calendar.MINUTE);
				int second = c.get(Calendar.SECOND);
				final String time = (String.format("%02d:%02d:%02d", hour, minute, second));
				getActivity().runOnUiThread(new Runnable(){
					public void run() {
						tvHour.setText(time);
					}
				});
				Thread.sleep(1000);
			}
		}.start();
	}
}
