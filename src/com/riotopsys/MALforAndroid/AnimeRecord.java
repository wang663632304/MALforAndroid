package com.riotopsys.MALforAndroid;

import java.io.Serializable;

import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;

public class AnimeRecord extends MALRecord implements Serializable {

	private final static long serialVersionUID = 2091730735738808331L;

	private final static String LOG_NAME = "AnimeRecord.java";

	public long id;
	public String title;
	public String type;
	public String imageUrl;
	public int episodes;
	public String status;
	public int watchedEpisodes;
	public int score;
	public String watchedStatus;
	public String memberScore;
	public String rank;
	public String synopsis;
	public int dirty;

	public AnimeRecord() {
		dirty = UNSYNCED;
	}

	public AnimeRecord(long id2, SQLiteDatabase db) throws Exception{
		pullFromDB(id2, db);
	}

	public AnimeRecord(JSONObject raw) {
		try {			
			id = raw.getInt("id");
			
			if ( raw.isNull("episodes") ){
				episodes = 0;
			} else {
				episodes = raw.getInt("episodes");
			}
			
			if ( raw.isNull("score") ){
				score = 0;
			} else {
				score = raw.getInt("score");
			}
			
			if ( raw.isNull("watched_episodes") ){
				watchedEpisodes = 0;
			} else {
				watchedEpisodes = raw.getInt("watched_episodes");
			}
			
			title = Html.fromHtml(raw.getString("title")).toString();
			type = Html.fromHtml(raw.getString("type")).toString();
			imageUrl = Html.fromHtml(raw.getString("image_url")).toString();
			status = Html.fromHtml(raw.getString("status")).toString();
			watchedStatus = Html.fromHtml(raw.getString("watched_status")).toString();
			memberScore = Html.fromHtml(raw.getString("members_score")).toString();
			rank = Html.fromHtml(raw.getString("rank")).toString();
			synopsis = Html.fromHtml(raw.getString("synopsis")).toString();

			dirty = CLEAN;
		} catch (Exception e) {
			Log.e(LOG_NAME, "JSON failed", e);
		}
	}

	// loads fields from table
	public void pullFromDB(long id, SQLiteDatabase db) throws Exception {
		
		Cursor c = db.rawQuery("select * from animeList where id = " + String.valueOf(id), null);
		if (c.moveToFirst()) {
			// c.getInt(c.getColumnIndex("watchedEpisodes"))
			this.id = c.getInt(c.getColumnIndex("id"));
			episodes = c.getInt(c.getColumnIndex("episodes"));
			watchedEpisodes = c.getInt(c.getColumnIndex("watchedEpisodes"));
			score = c.getInt(c.getColumnIndex("score"));

			title = c.getString(c.getColumnIndex("title"));
			type = c.getString(c.getColumnIndex("type"));
			imageUrl = c.getString(c.getColumnIndex("imageUrl"));
			status = c.getString(c.getColumnIndex("status"));
			watchedStatus = c.getString(c.getColumnIndex("watchedStatus"));
			memberScore = c.getString(c.getColumnIndex("memberScore"));
			rank = c.getString(c.getColumnIndex("rank"));
			synopsis = c.getString(c.getColumnIndex("synopsis"));

			dirty = c.getInt(c.getColumnIndex("dirty"));
			c.close();
		} else {
			c.close();
			throw new Exception( "item not found" );
		}
	}

	public void pushToDB(SQLiteDatabase db) {
		try {
			db.execSQL(insertStatement());
		} catch (Exception e) {
			Log.i(LOG_NAME, "pushToDB", e);
		}
	}

	private String insertStatement() {
		return "replace into `animeList` values (" + 
			String.valueOf(id) + ", " + 
			addQuotes(title) + ", " + 
			addQuotes(type) + ", " + 
			addQuotes(imageUrl) + ", " + 
			String.valueOf(episodes) + ", " + 
			addQuotes(status) + ", " + 
			String.valueOf(watchedEpisodes) + ", " + 
			String.valueOf(score) + ", " + 
			addQuotes(watchedStatus) + ", " + 
			String.valueOf(dirty) + ", " + 
			addQuotes(memberScore) + ", " + 
			addQuotes(rank) + ", " +
			addQuotes(synopsis) + " )";
	}

}
