package br.com.phon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import br.com.phon.fragments.VideoFragment;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;
import br.com.phon.fragments.InputDialogFragment;
import android.widget.Toast;
import br.com.phon.fragments.MainFragment;
import br.com.phon.fragments.SecondaryFragment;
import br.com.phon.fragments.InfoFragment;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.firstLayout, new MainFragment());
			ft.add(R.id.secondaryLayout, new SecondaryFragment());
			ft.add(R.id.infoLayout, new InfoFragment());
			ft.commit();
		}
	}
}
