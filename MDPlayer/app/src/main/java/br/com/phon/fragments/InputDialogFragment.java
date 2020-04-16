package br.com.phon.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.support.v4.app.FragmentManager;
import java.io.Serializable;
import android.content.DialogInterface;
import android.view.ViewGroup;

public class InputDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{
	
	private EditText edUrl;
	private Callback callback;
	
	public static void show(FragmentManager fm, Callback callback) {
		InputDialogFragment frag = new InputDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable("callback", callback);
		frag.setArguments(args);
		frag.show(fm, "input");
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		edUrl = new EditText(getActivity());
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		edUrl.setLayoutParams(lp);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Digite a url...");
		builder.setView(edUrl);
		builder.setPositiveButton("Ok", this);
		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		callback = (InputDialogFragment.Callback) getArguments().getSerializable("callback");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(callback != null && edUrl != null)
			callback.call(edUrl.getText().toString());
	}
	
	public interface Callback extends Serializable{
		public void call(String url)
	}
}
