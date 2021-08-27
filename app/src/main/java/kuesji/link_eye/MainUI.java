package kuesji.link_eye;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainUI extends LinearLayout {

				TextView title;
				EditText url;
				Button query;
				TextView header_launch;
				ScrollView scroller_launch;
				LinearLayout content_launch;
				private LinearLayout container_buttons;
				Button copy, share, clean, close;
				TextView txtMark;

				private Main main;

				public MainUI(Context context) {
								super(context);

								if (context instanceof Main) {
												main = (Main) context;
												main.getWindow().setStatusBarColor(0xff1e1f21);
												main.getWindow().setNavigationBarColor(0xff1e1f21);
								}

								setBackgroundColor(0xff1e1f21);
								setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								setOrientation(VERTICAL);

								title = new TextView(context);
								title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(64)));
								title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
								title.setText("link eye");
								title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
								title.setPadding(dp2px(16), 0, dp2px(16), 0);
								addView(title);

								url = new EditText(context);
								url.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(96)));
								url.setPadding(dp2px(16), 0, dp2px(16), dp2px(16));
								url.setGravity(Gravity.START | Gravity.TOP);
								url.setBackgroundColor(0);
								url.setHint("type url here");
								url.setTextColor(0xffeeeeee);
								url.setHintTextColor(0xffeeeeee);
								url.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
								addView(url);

								MarginLayoutParams query_lp = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								query_lp.leftMargin = dp2px(16);
								query_lp.rightMargin = dp2px(16);

								query = new Button(context);
								query.setLayoutParams(query_lp);
								query.setPadding(dp2px(16), 0, dp2px(16), 0);
								query.setAllCaps(false);
								query.setText("query");
								query.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
								query.setBackgroundColor(0xff313335);
								query.setTextColor(0xffc0c9d3);
								query.setOnClickListener((v) -> {
												queryApps();
								});
								addView(query);

								header_launch = new TextView(context);
								header_launch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(64)));
								header_launch.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
								header_launch.setText("open with");
								header_launch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
								header_launch.setPadding(dp2px(16), 0, dp2px(16), 0);
								addView(header_launch);

								scroller_launch = new ScrollView(context);
								scroller_launch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(256)));
								scroller_launch.setFillViewport(true);
								scroller_launch.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
								scroller_launch.setHorizontalFadingEdgeEnabled(false);
								scroller_launch.setVerticalFadingEdgeEnabled(false);
								addView(scroller_launch);

								content_launch = new LinearLayout(context);
								content_launch.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								content_launch.setOrientation(LinearLayout.VERTICAL);
								scroller_launch.addView(content_launch);

								MarginLayoutParams buttons_lp = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								buttons_lp.rightMargin = dp2px(16);
								buttons_lp.leftMargin = dp2px(16);

								container_buttons = new LinearLayout(context);
								container_buttons.setLayoutParams(buttons_lp);
								container_buttons.setOrientation(LinearLayout.HORIZONTAL);
								container_buttons.setWeightSum(20);
								container_buttons.setGravity(Gravity.CENTER);
								addView(container_buttons);

								copy = new Button(context);
								copy.setText("copy");
								copy.setOnClickListener((v) -> {
												ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
												cm.setPrimaryClip(ClipData.newPlainText("text", url.getText().toString()));
												Toast.makeText(getContext(), "link copied to your clipboard", Toast.LENGTH_SHORT).show();
								});

								share = new Button(context);
								share.setText("share");
								share.setOnClickListener((v) -> {
												Intent intent = new Intent(Intent.ACTION_SEND);
												intent.setType("text/plain");
												intent.putExtra(Intent.EXTRA_TEXT, url.getText().toString());

												if (main != null) {
																try {
																				main.startActivity(intent);

																} catch (ActivityNotFoundException e){
																				Toast.makeText(getContext(),"error: activity not found",Toast.LENGTH_SHORT).show();
																				e.printStackTrace();
																}
												}
								});

								clean = new Button(context);
								clean.setOnClickListener((v) -> {
												showCleaner();
								});
								clean.setText("clean");
								close = new Button(context);
								close.setText("close");

								LinearLayout.LayoutParams blp = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
								blp.weight = 5;

								for (Button x : new Button[]{copy, share, clean, close}) {
												x.setLayoutParams(blp);
												x.setAllCaps(false);
												x.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
												x.setBackgroundColor(0xff313335);
												x.setTextColor(0xffc0c9d3);
												container_buttons.addView(x);
								}

								txtMark = new TextView(context);
								txtMark.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								txtMark.setText(String.format("v%s by kuesji (kuesji@koesnu.com)\nclick here to make donation", BuildConfig.VERSION_NAME));
								txtMark.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
								txtMark.setTextColor(0xffc0c9d3);
								txtMark.setGravity(Gravity.CENTER);
								txtMark.setOnClickListener((v) -> {
												url.setText("https://liberapay.com/kuesji");
												query.performClick();
												Toast.makeText(getContext(), "ready to go. pick your app and browse the link", Toast.LENGTH_LONG).show();
								});
								addView(txtMark);
				}

				private Uri removeQueryParameter(Uri uri, String key) {
								Uri.Builder builder = uri.buildUpon().clearQuery();
								for (String tmp : uri.getQueryParameterNames()) {
												if (tmp.equals(key)) continue;
												builder.appendQueryParameter(tmp, uri.getQueryParameter(tmp));
								}

								return builder.build();
				}

				AlertDialog cleanerDialog;
				Uri cleanerUri;

				private void showCleaner() {
								cleanerUri = Uri.parse(url.getText().toString());
								Set<String> keys = cleanerUri.getQueryParameterNames();
								if (keys.size() < 1) return;

								ScrollView s = new ScrollView(getContext());
								s.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								s.setFillViewport(true);

								LinearLayout c = new LinearLayout(getContext());
								c.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
								c.setGravity(Gravity.TOP | Gravity.START);
								s.addView(c);

								OnClickListener listener = (v) -> {
												ViewGroup parent = (ViewGroup) v.getParent();
												parent.removeView(v);

												cleanerUri = removeQueryParameter(cleanerUri, ((Button) v).getText().toString());
												url.setText(cleanerUri.toString());

												if (c.getChildCount() < 1 && cleanerDialog != null) {
																cleanerDialog.dismiss();
												}
								};

								for (String key : keys) {
												Button b = new Button(getContext());
												b.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
												b.setAllCaps(false);
												b.setText(key);
												b.setOnClickListener(listener);
												c.addView(b);
								}

								cleanerDialog = new AlertDialog.Builder(getContext())
									.setTitle("click to remove parameter from url")
									.setView(s)
									.show();
				}

				public void setURL(String url) {
								this.url.setText(url);
								queryApps();
				}

				private void queryApps() {
								PackageManager pm = getContext().getPackageManager();
								content_launch.removeAllViews();
								for (ResolveInfo resolve : queryResolves(url.getText().toString())) {
												LinearLayout entry = new LinearLayout(getContext());
												entry.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
												entry.setOrientation(LinearLayout.HORIZONTAL);
												entry.setPadding(dp2px(32), dp2px(32), 0, 0);

												entry.setOnClickListener((v) -> {
																if (main != null) {
																				main.intent.setData(Uri.parse(url.getText().toString()));
																				main.intent.setClassName(resolve.activityInfo.packageName, resolve.activityInfo.name);
																				main.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);

																				try {
																								main.startActivity(main.intent);
																				} catch (ActivityNotFoundException e) {
																								e.printStackTrace();
																								Toast.makeText(getContext(), "error: no activity found", Toast.LENGTH_LONG).show();
																				}
																} else {
																				Toast.makeText(getContext(), "error: but weird one", Toast.LENGTH_LONG).show();
																}
												});

												ImageView entry_logo = new ImageView(getContext());
												entry_logo.setLayoutParams(new LinearLayout.LayoutParams(dp2px(32), dp2px(32)));
												entry_logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
												entry_logo.setImageDrawable(resolve.loadIcon(pm));
												entry.addView(entry_logo);

												TextView entry_label = new TextView(getContext());
												entry_label.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(32)));
												entry_label.setPadding(dp2px(32), 0, 0, 0);
												entry_label.setText(resolve.loadLabel(pm));
												entry_label.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
												entry.addView(entry_label);

												content_launch.addView(entry);
								}

								View space = new View(getContext());
								space.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(32)));
								content_launch.addView(space);
				}

				private List<ResolveInfo> queryResolves(String url) {
								PackageManager pm = getContext().getPackageManager();

								Intent search = new Intent();
								search.setAction(Intent.ACTION_VIEW);
								search.setData(Uri.parse(url));

								List<ResolveInfo> resolves = pm.queryIntentActivities(search, PackageManager.MATCH_ALL | PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS);

								final Collator collator = Collator.getInstance(Locale.getDefault());
								Collections.sort(resolves, (o1, o2) -> {
												return collator.compare(
													o1.activityInfo.loadLabel(pm).toString().toLowerCase(Locale.getDefault()),
													o2.activityInfo.loadLabel(pm).toString().toLowerCase(Locale.getDefault())
												);
								});

								return resolves;
				}

				public int dp2px(int dp) {
								return (int) (getResources().getDisplayMetrics().density * dp);
				}
}
