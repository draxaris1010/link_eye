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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Main extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AlertDialog dialog;
		switch (getIntent().getAction()){
			case Intent.ACTION_MAIN:
				String message = "link eye is small program to view or copy links before open in the your preferred app." +
				"you can start using via changing default browser to link eye in settings -> apps -> defaults or you can click any link and select link eye and click always if you don't know how to do with settings. \n" +
				"you can click browse to see my other apps on my website or click donate to go to liberapay and be my donator on liberapay.com.";

				dialog = new AlertDialog.Builder(this)
					.setTitle("link eye")
					.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("donate",(d,w)->{
						showOpen("https://liberapay.com/kuesji");
					})
					.setNeutralButton("browse",(d,w)->{
						showOpen("https://koesnu.com");
					})
					.setNegativeButton("close",(d,w)->{
						finishAndRemoveTask();
					}).show();
				break;
			case Intent.ACTION_SEND:
				String subject = getIntent().getExtras().getString(Intent.EXTRA_SUBJECT);
				String text = getIntent().getExtras().getString(Intent.EXTRA_TEXT);

				dialog = new AlertDialog.Builder(this)
					.setTitle("link eye")
					.setMessage(subject+"\n"+text)
					.setCancelable(false)
					.setPositiveButton("copy",(d,w)->{
						ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						cm.setPrimaryClip(ClipData.newPlainText("text",subject+"\n"+text));
						Toast.makeText(this,"copied to clipboard",Toast.LENGTH_LONG).show();
						finishAndRemoveTask();
					})
					.setNegativeButton("close",(d,w)->{
						finishAndRemoveTask();
					}).show();
				break;

			case Intent.ACTION_VIEW:
				dialog = new AlertDialog.Builder(this)
					.setTitle("link eye")
					.setMessage(getIntent().getDataString())
					.setCancelable(false)
					.setNegativeButton("close",(d,w)->{
						finishAndRemoveTask();
					})
					.setPositiveButton("open",(d,w)->{
						showOpen(getIntent().getDataString());
					})
					.setNeutralButton("copy",(d,w)->{
						ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData data = ClipData.newPlainText("text",getIntent().getDataString());
						cm.setPrimaryClip(data);
						Toast.makeText(this,"copied to clipboard",Toast.LENGTH_LONG).show();
						finishAndRemoveTask();
					})
				.show();

				break;
			default:
				finishAndRemoveTask();
				break;
		}

	}

	protected void onPause() {
		super.onPause();
		finishAndRemoveTask();
	}

	protected void onDestroy() {
		super.onDestroy();
		finishAndRemoveTask();
	}

	private void showOpen(String url){
		ScrollView scroller = new ScrollView(this);
		LinearLayout content = new LinearLayout(this);
		content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		content.setOrientation(LinearLayout.VERTICAL);
		scroller.addView(content);

		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("link eye")
		.setCancelable(false)
		.setView(scroller)
		.setNegativeButton("close",(d,w)->{
			finishAndRemoveTask();
		})
		.show();

		PackageManager pm = getPackageManager();
		for( ResolveInfo resolve : queryResolves(url)){
			LinearLayout entry = new LinearLayout(this);
			entry.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			entry.setOrientation(LinearLayout.HORIZONTAL);
			entry.setPadding(dp2px(32),dp2px(32),0,0);

			entry.setOnClickListener((v)->{
				dialog.dismiss();

				Intent x = new Intent();
				x.setAction(Intent.ACTION_VIEW);
				x.setData(Uri.parse(url));
				x.setClassName(resolve.activityInfo.packageName, resolve.activityInfo.name);
				x.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_NEW_TASK);

				getBaseContext().startActivity(x);
				finishAndRemoveTask();
			});

			ImageView entry_logo = new ImageView(this);
			entry_logo.setLayoutParams(new LinearLayout.LayoutParams(dp2px(32),dp2px(32)));
			entry_logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			entry_logo.setImageDrawable(resolve.loadIcon(pm));
			entry.addView(entry_logo);

			TextView entry_label = new TextView(this);
			entry_label.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp2px(32)));
			entry_label.setPadding(dp2px(32),0,0,0);
			entry_label.setText(resolve.loadLabel(pm));
			entry_label.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
			entry.addView(entry_label);

			content.addView(entry);
		}
	}

	private int dp2px(int dp){
		return (int)(getResources().getDisplayMetrics().density * dp);
	}

	private List<ResolveInfo> queryResolves(String url){
		Intent search = new Intent();
		search.setAction(Intent.ACTION_VIEW);
		search.setData(Uri.parse(url));

		List<ResolveInfo> resolves = getPackageManager().queryIntentActivities(search, PackageManager.MATCH_ALL | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

		final Collator collator = Collator.getInstance(Locale.getDefault());
		Collections.sort(resolves,(o1,o2)->{
			return collator.compare(
			o1.activityInfo.loadLabel(getPackageManager()).toString().toLowerCase(Locale.getDefault()),
			o2.activityInfo.loadLabel(getPackageManager()).toString().toLowerCase(Locale.getDefault())
			);
		});

		/*for (final ResolveInfo resolve : resolves) {
			if (resolve.activityInfo.packageName.equals(getPackageName()))
				continue;

			if (!resolve.activityInfo.exported)
				continue;

			ui.addItem(
			resolve.loadIcon(getPackageManager()),
			resolve.loadLabel(getPackageManager()).toString(),
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					i.setClassName(resolve.activityInfo.packageName, resolve.activityInfo.name);
					i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
					startActivity(i);
					finish();
				}
			}
			);
		}*/

		return resolves;
	}
}
