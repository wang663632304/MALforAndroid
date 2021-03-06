package com.riotopsys.MALforAndroid;

import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.util.Log;

public class MangaRecord extends MALRecord  {

	private static final long serialVersionUID = 6018807873534986692L;

	private final static String LOG_NAME = "MangaRecord";

	//public String title;
	//public String type;
	//public String imageUrl;
	
	public int chapters;
	public int volumes;
	
	//public String status;

	public int chaptersRead;
	public int volumesRead; 
	
	//public int score;
	//public String watchedStatus;
	//public String memberScore;
	//public String rank;
	//public String synopsis;
	//public int dirty;

	public MangaRecord() {
		dirty = UNSYNCED;
	}

	public MangaRecord(long id2, SQLiteDatabase db) throws Exception{
		pullFromDB(id2, db);
	}

	public MangaRecord(JSONObject raw) {
		super(raw);
		try {			
			
			if ( raw.isNull("chapters") ){
				chapters = 0;
			} else {
				chapters = raw.getInt("chapters");
			}
			
			if ( raw.isNull("volumes") ){
				volumes = 0;
			} else {
				volumes = raw.getInt("volumes");
			}
			
			if ( raw.isNull("chapters_read") ){
				chaptersRead = 0;
			} else {
				chaptersRead = raw.getInt("chapters_read");
			}
			
			if ( raw.isNull("volumes_read") ){
				volumesRead = 0;
			} else {
				volumesRead = raw.getInt("volumes_read");
			}
			
			watchedStatus = Html.fromHtml(raw.getString("read_status")).toString();
			
			
		} catch (Exception e) {
			Log.e(LOG_NAME, "JSON failed", e);
		}
	}

	// loads fields from table
	@Override
	public void pullFromDB(long id, SQLiteDatabase db) throws Exception {
		
		Cursor c = db.rawQuery("select * from mangaList where id = " + String.valueOf(id), null);
		if (c.moveToFirst()) {
			// c.getInt(c.getColumnIndex("watchedEpisodes"))
			this.id = c.getInt(c.getColumnIndex("id"));
			
			chapters = c.getInt(c.getColumnIndex("chapters"));
			volumes = c.getInt(c.getColumnIndex("volumes"));
			
			
			chaptersRead = c.getInt(c.getColumnIndex("chaptersRead"));
			volumesRead = c.getInt(c.getColumnIndex("volumesRead"));
			
			score = c.getInt(c.getColumnIndex("score"));

			title = c.getString(c.getColumnIndex("title"));
			type = c.getString(c.getColumnIndex("type"));
			imageUrl = c.getString(c.getColumnIndex("imageUrl"));
			status = c.getString(c.getColumnIndex("status"));
			watchedStatus = c.getString(c.getColumnIndex("watchedStatus"));
			
			if ( !c.isNull(c.getColumnIndex("memberScore")) ){
				memberScore = c.getString(c.getColumnIndex("memberScore"));
				rank = c.getString(c.getColumnIndex("rank"));
				synopsis = c.getString(c.getColumnIndex("synopsis"));
			} 
			
			dirty = c.getInt(c.getColumnIndex("dirty"));
			c.close();
		} else {
			c.close();
			throw new Exception( "item not found" );
		}
	}

	@Override
	public void pushToDB(SQLiteDatabase db) {
		try {
			db.execSQL(insertStatement());
		} catch (Exception e) {
			Log.i(LOG_NAME, "pushToDB", e);			
		}
		Log.i(LOG_NAME, "write");
	}

	private String insertStatement() {
		return "replace into `mangaList` values (" + 
			String.valueOf(id) + ", " + 
			addQuotes(title) + ", " + 
			addQuotes(type) + ", " + 
			addQuotes(imageUrl) + ", " + 
			String.valueOf(chapters) + ", " + 
			String.valueOf(volumes) + ", " +
			addQuotes(status) + ", " + 
			String.valueOf(chaptersRead) + ", " + 
			String.valueOf(volumesRead) + ", " +
			String.valueOf(score) + ", " + 
			addQuotes(watchedStatus) + ", " + 
			String.valueOf(dirty) + ", " + 
			addQuotes(memberScore) + ", " + 
			addQuotes(rank) + ", " +
			addQuotes(synopsis) + " )";
	}
	
	public boolean equals( Object o ){
		boolean result = super.equals(o); 
		result &= (o instanceof MangaRecord);
		if ( result ){
			result &= ( chapters == ((MangaRecord)o).chapters) ;
			result &= ( volumes == ((MangaRecord)o).volumes);
			result &= ( chaptersRead == ((MangaRecord)o).chaptersRead) ;
			result &= ( volumesRead == ((MangaRecord)o).volumesRead);			
		}		
		return result;
	}
	
}
