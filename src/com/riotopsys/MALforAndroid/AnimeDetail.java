package com.riotopsys.MALforAndroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AnimeDetail extends Activity {

	private TextView title;
	private TextView progress;
	private TextView score;
	private TextView status;
	private TextView type;
	private TextView watchedStatus;
	private ImageView image;
	private TextView memberScore;
	private TextView rank;
	private TextView synopsis;
	private IntentFilter intentFilter;

	private long id;
	private Reciever rec;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		intentFilter = new IntentFilter("com.riotopsys.MALForAndroid.FETCH_COMPLETE");
		intentFilter.addAction("com.riotopsys.MALForAndroid.DELETE_COMPLETE");

		rec = new Reciever();

		registerReceiver(rec, intentFilter);

		title = (TextView) this.findViewById(R.id.detailTitle);
		progress = (TextView) this.findViewById(R.id.detailProgress);
		score = (TextView) this.findViewById(R.id.detailScore);
		status = (TextView) this.findViewById(R.id.detailStatus);
		type = (TextView) this.findViewById(R.id.detailType);
		watchedStatus = (TextView) this.findViewById(R.id.detailWatchedStatus);

		image = (ImageView) this.findViewById(R.id.detailImage);
		memberScore = (TextView) this.findViewById(R.id.detailMemberScore);
		rank = (TextView) this.findViewById(R.id.detailRank);
		synopsis = (TextView) this.findViewById(R.id.detailSynopsis);

		Bundle b = getIntent().getExtras();

		id = b.getLong("id", 0);

		display();

	}

	private void display() {
		MALSqlHelper openHelper = new MALSqlHelper(this.getBaseContext());
		SQLiteDatabase db = openHelper.getReadableDatabase();

		String s = "select * from animeList where id = " + String.valueOf(id);

		Cursor c = db.rawQuery(s, null);

		if (c != null) {
			if (c.moveToFirst()) {

				title.setText(c.getString(1));
				progress.setText(c.getString(6) + " of " + c.getString(4));
				score.setText("Score: " + c.getString(7));
				status.setText(c.getString(5));
				type.setText(c.getString(2));
				watchedStatus.setText(c.getString(8));

				if (!c.isNull(10)) {
					memberScore.setText("Member Score: " + c.getString(10));
					rank.setText("Rank: " + c.getString(11));
					synopsis.setText(new String(c.getBlob(12)));

				} else {
					memberScore.setText("");
					rank.setText("");
					synopsis.setText("");

					Intent i = new Intent(this, MALManager.class);
					i.setAction("com.riotopsys.MALForAndroid.FETCH_EXTRAS");
					Bundle b = new Bundle();
					b.putLong("id", id);
					i.putExtras(b);
					startService(i);
				}

				File root = Environment.getExternalStorageDirectory();
				File file = new File(root, "Android/data/com.riotopsys.MALForAndroid/images/" + String.valueOf(id));

				if (file.exists()) {

					try {
						FileInputStream fis = new FileInputStream(file);
						Bitmap bmImg = BitmapFactory.decodeStream(fis);
						if (bmImg != null) {
							image.setImageBitmap(bmImg);
						} else {
							Intent i = new Intent(this, MALManager.class);
							i.setAction("com.riotopsys.MALForAndroid.IMAGE");
							Bundle b = new Bundle();
							b.putLong("id", id);
							i.putExtras(b);
							startService(i);
						}
					} catch (FileNotFoundException e) {
						Log.e("AnimeDetail", "image error", e);
					}

				} else {
					Intent i = new Intent(this, MALManager.class);
					i.setAction("com.riotopsys.MALForAndroid.IMAGE");
					Bundle b = new Bundle();
					b.putLong("id", id);
					i.putExtras(b);
					startService(i);
				}

			}
			

		}
		c.close();

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		Intent i = new Intent(this, MALManager.class);
		//
		Bundle b = new Bundle();
		b.putLong("id", id);
		i.putExtras(b);

		switch (itemId) {
			case R.id.detailMenuDelete:
				i.setAction("com.riotopsys.MALForAndroid.DELETE");
				//finish();
				Toast.makeText(this, "Deleting Item...", Toast.LENGTH_SHORT).show();
				break;
			case R.id.detailMenuSync:
				i.setAction("com.riotopsys.MALForAndroid.FETCH_EXTRAS");
				break;
		}

		startService(i);
		return true;
	}

	@Override
	public void onPause() {
		unregisterReceiver(rec);
		super.onPause();
	}

	@Override
	public void onResume() {
		registerReceiver(rec, intentFilter);
		super.onPause();
	}

	private class Reciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if ( arg1.getAction().equals("com.riotopsys.MALForAndroid.DELETE_COMPLETE")){
				finish();
			} else {
				display();
			}
		}

	}

}
