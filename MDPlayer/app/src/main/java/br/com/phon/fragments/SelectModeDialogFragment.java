package br.com.phon.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import java.io.Serializable;

public class SelectModeDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

	private Callback callback;
	
	public static void show(FragmentManager fm, SelectModeDialogFragment.Callback callback) {
		SelectModeDialogFragment frag = new SelectModeDialogFragment();
		Bundle args = new Bundle();
		args.putSerializable("callback", callback);
		frag.setArguments(args);
		frag.show(fm, "SelectModeFrag");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		callback = (SelectModeDialogFragment.Callback) getArguments().getSerializable("callback");
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setTitle("Selecione um método abaixo.")
			.setItems(new String[]{"Youtube", "Navegador"}, this)
			.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		callback.call(which);
	}
	
	public interface Callback extends Serializable {
		public void call(int opt)
	}
}
