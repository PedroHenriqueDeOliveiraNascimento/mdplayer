package br.com.phon.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import br.com.phon.R;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.graphics.Bitmap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;

public class BrowserFragment extends Fragment{
	
	private String url;
	private WebView wb;

	private ProgressBar progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_browser, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		url = getArguments().getString("url");
		progress = getView().findViewById(R.id.progress);
		wb = getView().findViewById(R.id.wb);
		wb.requestFocus();
		WebSettings ws = wb.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setSupportMultipleWindows(true);
		wb.setWebViewClient(getWbClient());
		wb.loadUrl(url);
		
		getView().findViewById(R.id.btnBack)
		.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				getFragmentManager()
				.popBackStack();
			}
		});
	}

	private WebViewClient getWbClient() {
		return new WebViewClient() {
			@Override
			public void onPageStarted(WebView wb, String url, Bitmap favicon) {
				progress.setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.tvStatus)).setText(wb.getTitle());
			}
		};
	}
	
}
