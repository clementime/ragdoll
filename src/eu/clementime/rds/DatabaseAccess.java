package eu.clementime.rds;

import static eu.clementime.rds.Constants.DB_FIELD_DISPLAY;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DB_TABLE_ITEM;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DatabaseAccess {
	
	private DatabaseHandler dbh;
	
	public DatabaseAccess(DatabaseHandler dbh) {
		this.dbh = dbh;
	}
	
	//***********************************
	// BACKGROUND
	//***********************************   
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
	
	public ArrayList<Exit> selectExits(int screenId, TiledTextureRegion TRLeft, TiledTextureRegion TRRight) {

		ArrayList<Exit> exits = new ArrayList<Exit>();
		
		String query = " select _id as id, x, y, direction, display, starting_x, starting_y, to_screen_id, before_trigger_id, after_trigger_id ";
		query += " from exit ";
		query += " where screen_id = " + screenId + " order by id";	// conditions
		
		Log.i("Clementime", "Background/loadExits(): ***load exits*** ");	

		int id;
		int direction;
		int x;
		int y;
		int display;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			while (c.moveToNext()) {
				
				id = c.getInt(c.getColumnIndex("id"));
				direction = c.getInt(c.getColumnIndex("direction"));
				x = c.getInt(c.getColumnIndex("x"));
				y = c.getInt(c.getColumnIndex("y"));
				display = c.getInt(c.getColumnIndex("display"));
				
				Log.v("Clementime", "Background/loadExits(): load exit " + id);
				
				if (c.getInt(c.getColumnIndex("direction")) == DIRECTION_LEFT) {
					Exit exit = new Exit(id, direction, x, y, display, TRLeft);
					
					exit.beforeTrigger = c.getInt(c.getColumnIndex("before_trigger_id"));
					exit.afterTrigger = c.getInt(c.getColumnIndex("after_trigger_id"));
					exit.startingX = c.getInt(c.getColumnIndex("starting_x"));
					exit.startingY = c.getInt(c.getColumnIndex("starting_y"));
					exit.toScreen = c.getInt(c.getColumnIndex("to_screen_id"));

					exits.add(exit);
				} else if (c.getInt(c.getColumnIndex("direction")) == DIRECTION_RIGHT) {
					Exit exit = new Exit(id, direction, x, y, display, TRRight);
					
					exit.beforeTrigger = c.getInt(c.getColumnIndex("before_trigger_id"));
					exit.afterTrigger = c.getInt(c.getColumnIndex("after_trigger_id"));
					exit.startingX = c.getInt(c.getColumnIndex("starting_x"));	
					exit.startingY = c.getInt(c.getColumnIndex("starting_y"));
					exit.toScreen = c.getInt(c.getColumnIndex("to_screen_id"));

					exits.add(exit);
				}			
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.w("Clementime", "Background/loadExits(): failed to load exits - " + e.getMessage());
		}
		
		return exits;
	}

	public ArrayList<Area> selectAreas(int screenId) {

		ArrayList<Area> areas = new ArrayList<Area>();
		
		int id;
		int x;
		int y;
		int width;
		int height;
		
		String query = " select _id as id, x, y, height, width ";
		query += " from screen_area ";
		query += " where screen_id = " + screenId + " order by id";	// conditions
		
		Log.d("Clementime", "Background/loadAreas(): ***load areas*** ");	
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			while (c.moveToNext()) {
				
				id = c.getInt(c.getColumnIndex("id"));
				x = c.getInt(c.getColumnIndex("x"));
				y = c.getInt(c.getColumnIndex("y"));
				width = c.getInt(c.getColumnIndex("width"));
				height = c.getInt(c.getColumnIndex("height"));
	
				Log.d("Clementime", "Background/loadAreas(): load area " + id + " xMin: " + x + "-yMin: " + y + "-xMax: " + (x + width) + "-yMax: " + (y + height));
				areas.add(new Area(id, x, y, width, height));
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.w("Clementime", "Background/loadAreas(): failed to load areas - " + e.getMessage());
		}
		
		return areas;
	}
	
	public ArrayList<Integer> selectDisplayedAnims(int screenId) {
		
		ArrayList<Integer> displayedAnims = new ArrayList<Integer>();
		
		String query = " select p._id as id ";
		query += " from playing_animation p left join animation a on a._id = anim_id ";
		query += " where screen_id = " + screenId;	// conditions
		query += " and " + DB_FIELD_DISPLAY + " = 1 ";
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			while (c.moveToNext()) displayedAnims.add(c.getInt(c.getColumnIndex("id")));			
			
			c.close();
			
		} catch (Exception e) {
			Log.w("Clementime", "Background/showAnims(): failed to load playing animations - " + e.getMessage());
		}
		
		return displayedAnims;
	}

	public int[] selectStaticAnimFeatures(int animId) {
	
		String query;
		int[] features = {0,0,0};	
		
		query = " select first_frame, last_frame, frame_duration ";
		query += " from playing_animation p ";
		query += " where p._id = " + animId;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
	
				features[0] = c.getInt(c.getColumnIndex("first_frame"));
				features[1] = c.getInt(c.getColumnIndex("last_frame"));
				features[2] = c.getInt(c.getColumnIndex("frame_duration"));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return features;		
	}

	public int[] selectAnimStates(int animId) {	
		
		int[] stateResults = {0,0,0,0};

		String query  = " select look_state, talk_state ";
		query += " from character ";
		query += " where playing_anim_id = " + animId;
		
		// check if every items needed for activating are in inventory
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				stateResults[1] = c.getInt(c.getColumnIndex("look_state"));
				stateResults[2] = c.getInt(c.getColumnIndex("talk_state"));
				
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stateResults;
	}

	public int[] selectItemStates(int itemId) {	
		
		int[] stateResults = {0,0,0,0};

		String query  = " select item_id as id, take_state, look_state, talk_state, exit ";
		query += " from screen_item ";
		query += " where id = " + itemId;
		
		// check if every items needed for activating are in inventory
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				stateResults[0] = c.getInt(c.getColumnIndex("take_state"));
				stateResults[1] = c.getInt(c.getColumnIndex("look_state"));
				stateResults[2] = c.getInt(c.getColumnIndex("talk_state"));
				stateResults[3] = c.getInt(c.getColumnIndex("exit"));
				
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stateResults;
	}
	
	public int[] selectAreaStates(int areaId) {	
		
		int[] stateResults = {0,0,0,0};

		String query  = " select a._id as id, look_state, a.exit as exit_id, direction ";
		query += " from screen_area a left join exit e on e._id = exit_id ";
		query += " where id = " + areaId;
		
		// check if every items needed for activating are in inventory
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				stateResults[1] = c.getInt(c.getColumnIndex("look_state"));
				stateResults[3] = c.getInt(c.getColumnIndex("direction"));
				
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stateResults;
	}	

	//***********************************
	// INVENTORY
	//*********************************** 
	public LinkedList<Map<String, String>> selectInventoryItems() {
		
		LinkedList<Map<String, String>> ll = new LinkedList<Map<String, String>>();
		
		String query = " select _id as id, image " ;
		query += " from item ";
		query += " where " + DB_FIELD_DISPLAY + " = " + DB_INVENTORY_VALUE_IN;	// conditions
		query += " order by height desc ";
			
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			//int i = 0;

			while (c.moveToNext()) {
				Map<String, String> hm = new HashMap<String, String>();
				
				hm.put("id", c.getString(c.getColumnIndex("id")));
				hm.put("image", c.getString(c.getColumnIndex("image")));

				ll.add(hm);		
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "DatabaseAccess/selectInventoryItems(): failed to select inventory items");
		}		

		return ll;
	}

	public void updateInventoryField(String where, String value) {
		
		Log.d("Clementime", "InventoryFrame/updateInventoryField()");

		try {	
		    ContentValues args = new ContentValues();
		    args.put(DB_FIELD_DISPLAY, value);
    
		    //int affectedRows = dbh.db.update(DB_TABLE_ITEM, args, where, null);
		    dbh.db.update(DB_TABLE_ITEM, args, where, null);

		} catch (Exception e) {
			Log.e("Clementime", "InventoryFrame/updateInventoryField(): failed to update display field " + where + " - " + e.getMessage());
		}
	}
	
	public Map<String, String> selectInventoryItem(int itemId) {
		
		Map<String, String> hm = new HashMap<String, String>();
		
		String query = " select _id as id, image, height, width ";
		query += " from item ";
		query += " where id = " + itemId;	// conditions
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			c.moveToFirst();

			hm.put("id", c.getString(c.getColumnIndex("id")));
			hm.put("image", c.getString(c.getColumnIndex("image")));
			hm.put("height", c.getString(c.getColumnIndex("height")));
			hm.put("width", c.getString(c.getColumnIndex("width")));
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", "DatabaseAccess/selectInventoryItem(): failed to access data " + e.getMessage());
		}
		
		return hm;	
	}

	//***********************************
	// WORLD
	//***********************************  
	public boolean selectTakeable(int itemId) {
		
		String query;
		boolean takeable = false;
		
		query = " select takeable ";
		query += " from screen_item where item_id = " + itemId;		
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();

				if (c.getInt(c.getColumnIndex("takeable")) == 1) takeable = true;
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return takeable;
	}	
	
	//***********************************
	// GAME TOOLS
	//***********************************  
	// search if phone language is available (English is used if not)
	public String selectLanguage(Context context) {

		String locale = context.getResources().getConfiguration().locale.getLanguage(); //iso2 code	
		String query = " select value as language_list from parameter where reference = 'available_language' ";
		String[] languages;
		
		String language = "en";
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				languages = c.getString(c.getColumnIndex("language_list")).split(";");
				for (int i = 0; i < languages.length; i++) {

					if (locale.equals(languages[i])) language = locale;
				}
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return language;
	}

	public String selectText(String reference, String language) {
		
		String query;
		String text = "";
		
		query = " select text_" + language + " as text ";
		query += " from text where reference = '" + reference + "'";
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();

				// retrieve combination description for side screen display				
				text = c.getString(c.getColumnIndex("text"));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return text;
	}

	//***********************************
	// DOLL
	//***********************************  
	public float selectYVelocity(int screenId) {
		
		float yVelocity = 0;
		
		String query = " select y_velocity ";
		query += " from screen ";
		query += " where _id = " + screenId;	// conditions
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				yVelocity = c.getFloat(c.getColumnIndex("y_velocity"));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return yVelocity;
	}

	// find which screen the doll is in
	public int selectDollScreen() {

		String query = "";

		int screenId = 1;

		try {
			query = " select value as screen_id from player_saving where reference = 'screen_id' ";
		
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				screenId = c.getInt(c.getColumnIndex("screen_id"));

			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return screenId;	
	}
	
	// find where the doll is at launching time
	public int[] selectDollPosition() {
		
		Log.d("Clementime", "World/getDollPosition()");
		
		String query = "";
		int[] dollPos = {0,0};
		
		try {
			query = " select value as x from player_saving where reference = 'x_doll' ";
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				dollPos[0] = c.getInt(c.getColumnIndex("x"));
			}

			c.close();
			
		} catch (Exception e) {
			Log.w("Clementime", "World/getDollPosition(): failed to get x position from player table.");
		}
		
		try {		
			query = " select value as y from player_saving where reference = 'y_doll' ";
			Cursor c1 = dbh.db.rawQuery(query, new String [] {});
			
			if (c1.getCount() != 0) {
				c1.moveToFirst();
				
				dollPos[1] = c1.getInt(c1.getColumnIndex("y"));
			}

			c1.close();
			
		} catch (Exception e) {
			Log.w("Clementime", "World/getDollPosition(): failed to get y position from player table.");
		}
		
		return dollPos;	
	}

	//***********************************
	// WORLD
	//*********************************** 
	public int[] selectFirstScreenFeatures() {
		
		Log.i("Clementime", "World/getFirstScreenFeatures() ");

		// features[0] = id exit, features[1] = to trigger before leaving	
		int[] features = {0,0,0,0};
		
		String query = " select to_screen_id, ";
		query += " starting_x, starting_y, after_trigger_id ";
		query += " from exit e ";
		query += " where _id = 0 ";	// conditions
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				features[0] = c.getInt(c.getColumnIndex("after_trigger_id"));
				features[1] = c.getInt(c.getColumnIndex("starting_x"));
				features[2] = c.getInt(c.getColumnIndex("starting_y"));
				Log.v("Clementime", "World/getFirstScreenFeatures(): starting x: " + features[2] + " starting y: " + features[3] + " starting trigger: " + features[1]);
			}

			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return features;
	}
}
