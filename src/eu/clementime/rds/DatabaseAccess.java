package eu.clementime.rds;

import static eu.clementime.rds.Constants.DB_FIELD_DISPLAY;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;


import android.database.Cursor;
import android.util.Log;

public class DatabaseAccess {
	
	private DatabaseHandler dbh;
	
	public DatabaseAccess(DatabaseHandler dbh) {
		this.dbh = dbh;
	}
	
	public String selectScreenPrefix(int screenId) {

		String screenPrefix = "";
		
		String query = " select image_prefix ";
		query += " from screen where _id = " + screenId;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			c.moveToFirst();		
			screenPrefix = c.getString(c.getColumnIndex("image_prefix"));
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "DatabaseAccess/selectScreenPrefix(): failed to access data " + e.getMessage());
		}
		
		return screenPrefix;
		
	}
	
	public Map<String, String> selectBackground(int screenId) {
		
		Map<String, String> hm = new HashMap<String, String>();
		
		String query = " select x_min, x_max, background, foreground ";
		query += " from screen where _id = " + screenId;	// conditions
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			c.moveToFirst();

			hm.put("x_min", c.getString(c.getColumnIndex("x_min")));
			hm.put("x_max", c.getString(c.getColumnIndex("x_max")));
			hm.put("background", c.getString(c.getColumnIndex("background")));
			hm.put("foreground", c.getString(c.getColumnIndex("foreground")));
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "DatabaseAccess/selectBackground(): failed to access data " + e.getMessage());
		}
		
		return hm;	
	}
	
	public LinkedList<Map<String, String>> selectscreenItems(int screenId) {
		
		LinkedList<Map<String, String>> ll = new LinkedList<Map<String, String>>();
		
		String query = " select _id as id, image, height, width, " + DB_FIELD_DISPLAY + ", ";
		query += " x, y, take_state, look_state, talk_state, takeable, foreground ";	// select field
		query += " from item left join screen_item on _id = item_id ";
		query += " where screen_id = " + screenId;	// conditions
		query += " and " + DB_FIELD_DISPLAY + " = 1 ";
		query += " order by height desc ";
			
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			//int i = 0;

			while (c.moveToNext()) {
				Map<String, String> hm = new HashMap<String, String>();
				
				hm.put("id", c.getString(c.getColumnIndex("id")));
				hm.put("image", c.getString(c.getColumnIndex("image")));
				hm.put("height", c.getString(c.getColumnIndex("height")));
				hm.put("width", c.getString(c.getColumnIndex("width")));
				hm.put("x", c.getString(c.getColumnIndex("x")));
				hm.put("y", c.getString(c.getColumnIndex("y")));
				hm.put("take_state", c.getString(c.getColumnIndex("take_state")));
				hm.put("look_state", c.getString(c.getColumnIndex("look_state")));
				hm.put("take_state", c.getString(c.getColumnIndex("take_state")));
				hm.put("talk_state", c.getString(c.getColumnIndex("talk_state")));
				hm.put("takeable", c.getString(c.getColumnIndex("takeable")));
				hm.put("foreground", c.getString(c.getColumnIndex("foreground")));

				ll.add(hm);		
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "Background/countScreenItems(): failed to select screen items on screen " + screenId);
		}		

		return ll;
	}
	
	
	public LinkedList<Map<String, String>> selectAnimations(int screenId) {
		
		LinkedList<Map<String, String>> ll = new LinkedList<Map<String, String>>();
		
		String query = " select p._id as id, image, rows, columns, height, width, x, y, stop_frame, move_to_x, move_to_y, to_chase ";
		query += " from animation a left join playing_animation p on a._id = p.anim_id ";
		query += " where screen_id = " + screenId;	// conditions
		query += " order by height desc ";
			
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			//int i = 0;

			while (c.moveToNext()) {
				Map<String, String> hm = new HashMap<String, String>();
				
				hm.put("id", c.getString(c.getColumnIndex("id")));
				hm.put("image", c.getString(c.getColumnIndex("image")));
				hm.put("rows", c.getString(c.getColumnIndex("rows")));
				hm.put("columns", c.getString(c.getColumnIndex("columns")));
				hm.put("height", c.getString(c.getColumnIndex("height")));
				hm.put("width", c.getString(c.getColumnIndex("width")));
				hm.put("x", c.getString(c.getColumnIndex("x")));
				hm.put("y", c.getString(c.getColumnIndex("y")));
				hm.put("stop_frame", c.getString(c.getColumnIndex("stop_frame")));
				hm.put("move_to_x", c.getString(c.getColumnIndex("move_to_x")));
				hm.put("move_to_y", c.getString(c.getColumnIndex("move_to_y")));
				hm.put("to_chase", c.getString(c.getColumnIndex("to_chase")));

				ll.add(hm);		
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "Background/countScreenItems(): failed to select screen items on screen " + screenId);
		}		

		return ll;
	}
}
