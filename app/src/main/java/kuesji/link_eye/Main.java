package kuesji.link_eye;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Main extends Activity {
	MainUI ui;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ui = new MainUI(this);
		ui.close.setOnClickListener((v) -> {
			finish();
		});
		setContentView(ui);

		Intent intent = getIntent();
		switch (intent.getAction()) {
			case Intent.ACTION_VIEW:
				ui.setURL(intent.getDataString());
				break;
			case Intent.ACTION_SEND:
				ui.setURL(intent.getExtras().getString(Intent.EXTRA_TEXT));
				break;
			default:
				ui.setURL("https://twitter.com/kuesji");
				break;
		}
	}
}
