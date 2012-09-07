package eu.clementime.rds;

import static eu.clementime.rds.Constants.DB_FIELD_DISPLAY;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DB_TABLE_ITEM;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.LOG_ON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
* All queries are stored in DatabaseAccess class.
* @author Cl&eacute;ment
* @version 1.0
*/
public class DatabaseAccess {
	
	private DatabaseHandler dbh;
	
	/**
	 * For logs only.
	 */	
	private String className = "DatabaseAccess";
	
	//TODO: put as much as possible in classes to reduce database access during game (ex.: take, look, talk states should be updated in classes at runtime) 
	
	public DatabaseAccess(DatabaseHandler dbh) {
		this.dbh = dbh;
	}
	
	//***********************************
	// BACKGROUND
	//***********************************   
	
	/**
	 * Finds background & foreground images, and boundaries (x min & max out of which doll can't go).
	 * @param	screenId	id of concerned screen
	 * @return	a hash map of strings with:</br>
	 * > x min,</br>
	 * > x max,</br>
	 * > background image name,</br>
	 * > foreground image name 
	 */		
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
			Log.e("Clementime", className + "/selectBackground(): failed to access data " + e.getMessage());
		}
		
		return hm;	
	}
	/**
	 * Returns a list of items to be displayed on screen.
	 * @param	screenId	id of concerned screen
	 * @return	a linked list of hash map of strings with:</br>
	 * > item id,</br>
	 * > image,</br>
	 * > height of image (to fill BitmapTextureAtlas),</br>
	 * > width of image (to fill BitmapTextureAtlas),</br>
	 * > x position of item on screen,</br>
	 * > y position of item on screen,</br>
	 * > can the doll try to take this item,</br>
	 * > can the doll try to look at this item,</br>
	 * > is this item takeable,</br>
	 * > is this item on foreground or not 
	 */		
	public LinkedList<Map<String, String>> selectScreenItems(int screenId) {
		
		LinkedList<Map<String, String>> ll = new LinkedList<Map<String, String>>();
		
		String query = " select i._id as id, i.image, i.height, i.width, i." + DB_FIELD_DISPLAY + ", ";
		query += " s.x, s.y, s.take_state, s.look_state, s.talk_state, s.takeable, s.foreground ";	// select field
		query += " from item i left join screen_item s on i._id = s.item_id ";
		query += " where s.screen_id = " + screenId;	// conditions
		query += " and i." + DB_FIELD_DISPLAY + " = 1 ";
		query += " order by i.height desc ";

		if (LOG_ON) Log.d("Clementime", className + "/selectScreenItems(): query: " + query);
		
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
				hm.put("talk_state", c.getString(c.getColumnIndex("talk_state")));
				hm.put("takeable", c.getString(c.getColumnIndex("takeable")));
				hm.put("foreground", c.getString(c.getColumnIndex("foreground")));

				ll.add(hm);		
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", className + "/selectScreenItems(): failed to select screen items on screen " + screenId);
		}		

		return ll;
	}
	/**
	 * Returns a list of animations to be displayed on screen.
	 * @param	screenId	id of concerned screen
	 * @return	a linked list of hash map of strings with:</br>
	 * > animation id,</br>
	 * > image,</br>
	 * > number of rows in this image (to launch AndEngine animation),</br>
	 * > number of columns in this image (to launch AndEngine animation),</br>
	 * > height of image (to fill BitmapTextureAtlas),</br>
	 * > width of image (to fill BitmapTextureAtlas),</br>
	 * > x position of animation on screen,</br>
	 * > y position of animation on screen,</br>
	 * > the frame where animation should stop at end,</br>
	 * > x position where animation should go if it is a moving animation OR if the animation is run at different places,</br>
	 * > y position where animation should go if it is a moving animation OR if the animation is run at different places,</br>
	 * > is animation chased by camera or not 
	 */		
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
			Log.e("Clementime", className + "/selectAnimations(): failed to select screen items on screen " + screenId);
		}		

		return ll;
	}
	// TODO: scale position missing (should be retrieved to y) - idem for AREAS just after
	/**
	 * Returns a list of exits to be displayed on screen.
	 * @param	screenId	id of concerned screen
	 * @param	TRLeft	TiledTextureRegion, to create animated sprite of exit
	 * @param	TRRight	TiledTextureRegion, to create animated sprite of exit
	 * @param	MARGIN	y margin, to position animated sprite of exit
	 * @return	a array list of exit objects containing following information:</br>
	 * > exit id,</br>
	 * > exit direction (left or right),</br>
	 * > x position of exit on screen,</br>
	 * > y position of exit on screen,</br>
	 * > is exit displayed,</br>
	 * > is there a trigger to launch before leaving the screen by this exit,</br>
	 * > is there a trigger to launch after leaving the screen by this exit (in new screen),</br>
	 * > x position where doll should start in the next screen after leaving by this exit,</br>
	 * > y position where doll should start in the next screen after leaving by this exit,</br>
	 * > screen id of next screen (after exit) 
	 */		
	public ArrayList<Exit> selectExits(int screenId, TiledTextureRegion TRLeft, TiledTextureRegion TRRight,int MARGIN) {

		ArrayList<Exit> exits = new ArrayList<Exit>();
		
		String query = " select _id as id, x, y, direction, display, starting_x, starting_y, to_screen_id, before_trigger_id, after_trigger_id ";
		query += " from exit ";
		query += " where screen_id = " + screenId + " order by id";	// conditions
		
		if (LOG_ON) Log.i("Clementime", className + "/loadExits(): ***load exits*** ");	

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
				y = c.getInt(c.getColumnIndex("y")) + MARGIN;
				display = c.getInt(c.getColumnIndex("display"));
				
				if (LOG_ON) Log.v("Clementime", className + "/loadExits(): load exit " + id);
				
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
			Log.e("Clementime", className + "/loadExits(): failed to load exits - " + e.getMessage());
		}
		
		return exits;
	}
	/**
	 * Returns a list of areas to be stored in Background object.
	 * @param	screenId	id of concerned screen
	 * @param	MARGIN	y margin, to position area
	 * @return	a array list of areas containing following information:</br>
	 * > area id,</br>
	 * > x position of area on screen,</br>
	 * > y position of area on screen,</br>
	 * > width of this area,</br>
	 * > height of this area 
	 */	
	public ArrayList<Area> selectAreas(int screenId, int MARGIN) {

		ArrayList<Area> areas = new ArrayList<Area>();
		
		int id;
		int x;
		int y;
		int width;
		int height;
		
		String query = " select _id as id, x, y, height, width ";
		query += " from screen_area ";
		query += " where screen_id = " + screenId + " order by id";	// conditions
		
		if (LOG_ON) Log.d("Clementime", className + "/loadAreas(): ***load areas*** ");	
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			while (c.moveToNext()) {
				
				id = c.getInt(c.getColumnIndex("id"));
				x = c.getInt(c.getColumnIndex("x"));
				y = c.getInt(c.getColumnIndex("y")) + MARGIN;
				width = c.getInt(c.getColumnIndex("width"));
				height = c.getInt(c.getColumnIndex("height"));
	
				if (LOG_ON) Log.d("Clementime", className + "/loadAreas(): load area " + id + " xMin: " + x + "-yMin: " + y + "-xMax: " + (x + width) + "-yMax: " + (y + height));
				areas.add(new Area(id, x, y, width, height));
			}
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", className + "/loadAreas(): failed to load areas - " + e.getMessage());
		}
		
		return areas;
	}
	/**
	 * Returns a list of animation id of animations displayed on screen.
	 * @param	screenId	id of concerned screen
	 * @return	a array list of animation id
	 */	
	
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
			Log.e("Clementime", className + "/showAnims(): failed to load playing animations - " + e.getMessage());
		}
		
		return displayedAnims;
	}
	/**
	 * Returns features of a static animation (= an animation that doesn't move and loop infinitely, as characters or landscape animation).
	 * @param	animId	database id of animation
	 * @return	an integer array with 3 entries:</br>
	 * > first frame,</br>
	 * > last frame,</br>
	 * > frame duration (in ms)
	 */	

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
	/**
	 * Returns states of a static animation (= an animation that doesn't move and loop infinitely, as characters or landscape animation).
	 * @param	animId	database id of animation
	 * @return	an integer array with 3 entries:</br>
	 * > can the doll try to take this animation (=take state),</br>
	 * > can the doll look at this animation (=look state),</br>
	 * > can the doll talk to this animation (=talk state)
	 */	
	public int[] selectAnimStates(int animId) {	
		
		int[] stateResults = {0,0,0};

		String query  = " select take_state, look_state, talk_state ";
		query += " from playing_animation ";
		query += " where _id = " + animId;
		
		// check if every items needed for activating are in inventory
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				stateResults[0] = c.getInt(c.getColumnIndex("take_state"));
				stateResults[1] = c.getInt(c.getColumnIndex("look_state"));
				stateResults[2] = c.getInt(c.getColumnIndex("talk_state"));
				
				c.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stateResults;
	}
	/**
	 * Returns states of a screen item.
	 * @param	itemId	database id of item
	 * @return	an integer array with 3 entries:</br>
	 * > can the doll try to take this item (=take state),</br>
	 * > can the doll look at this item (=look state),</br>
	 * > can the doll talk to this item (=talk state)
	 */

	public int[] selectItemStates(int itemId) {	
		
		int[] stateResults = {0,0,0};

		String query  = " select item_id as id, take_state, look_state, talk_state ";
		query += " from screen_item ";
		query += " where id = " + itemId;
		
		if (LOG_ON) Log.d("Clementime", className + "/selectItemStates(): query: " + query);
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				stateResults[0] = c.getInt(c.getColumnIndex("take_state"));
				stateResults[1] = c.getInt(c.getColumnIndex("look_state"));
				stateResults[2] = c.getInt(c.getColumnIndex("talk_state"));
				
				c.close();
			}
		} catch (Exception e) {
			if (LOG_ON) Log.e("Clementime", className + "/selectItemStates(): failed");
		}
		
		return stateResults;
	}
	//TODO: check this method: exit isn't part of area anymore, stateResults[3] doesn't exist (?) and is talking allowed (should think of, maybe, or add after) 
	/**
	 * Returns states of a screen area.
	 * @param	areaId	database id of area
	 * @return	an integer array with 3 entries:</br>
	 * > can the doll try to take this area (=take state), always 0/impossible,</br>
	 * > can the doll look at this area (=look state),</br>
	 * > can the doll talk to this area (=talk state)
	 */
	
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
	/**
	 * Returns information about an item when one is added by combination of other items.
	 * @param	itemId	id of concerned item
	 * @return	a hash map of strings with:</br>
	 * > item id,</br>
	 * > image,</br>
	 * > height of image (to fill BitmapTextureAtlas),</br>
	 * > width of image (to fill BitmapTextureAtlas),</br>
	 * > is this item displayed,</br>
	 * > x position of item on screen,</br>
	 * > y position of item on screen,</br>
	 * > can the doll try to take this item,</br>
	 * > can the doll try to look at this item,</br>
	 * > is this item takeable,</br>
	 * > is this item on foreground or not 
	 */	
	public Map<String, String> selectItem(int itemId) {

		Map<String, String> hm = new HashMap<String, String>();

		String query = " select i._id as id, i.image, i.height, i.width, i." + DB_FIELD_DISPLAY + ", ";
		query += " s.x, s.y, s.take_state, s.look_state, s.talk_state, s.takeable ";	// select field
		query += " from item i left join screen_item s on i._id = s.item_id ";
		query += " where i._id = " + itemId;	// conditions
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});

			c.moveToNext();
				
			hm.put("id", c.getString(c.getColumnIndex("id")));
			hm.put("image", c.getString(c.getColumnIndex("image")));
			hm.put("height", c.getString(c.getColumnIndex("height")));
			hm.put("width", c.getString(c.getColumnIndex("width")));	
			hm.put(DB_FIELD_DISPLAY, c.getString(c.getColumnIndex(DB_FIELD_DISPLAY)));
			hm.put("x", c.getString(c.getColumnIndex("x")));
			hm.put("y", c.getString(c.getColumnIndex("y")));
			hm.put("take_state", c.getString(c.getColumnIndex("take_state")));
			hm.put("look_state", c.getString(c.getColumnIndex("look_state")));
			hm.put("talk_state", c.getString(c.getColumnIndex("talk_state")));
			hm.put("exit", c.getString(c.getColumnIndex("exit")));
			hm.put("takeable", c.getString(c.getColumnIndex("takeable")));
			
			c.close();
			
		} catch (Exception e) {
			Log.e("Clementime", className + "/loadItem(): failed to load item " + itemId + " - " + e.getMessage());
		}
		
		return hm;
	}

	
	//***********************************
	// INVENTORY
	//***********************************
	
	/**
	 * Returns list of items to be displayed in inventory.
	 * @return	a linked list of hash map of strings with:</br>
	 * > item id,</br>
	 * > image
	 */	
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
	/**
	 * Updates item table to set an item in inventory when taken by the doll or to remove it from inventory when used in a combination.
	 * @param	where	where clause
	 * @param	value	1=on screen; 2=in inventory; 0=not displayed anywhere
	 */
	public void updateInventoryField(String where, int value) {
		
		if (LOG_ON) Log.d("Clementime", "InventoryFrame/updateInventoryField()");

		try {	
		    ContentValues args = new ContentValues();
		    args.put(DB_FIELD_DISPLAY, value);
    
		    //int affectedRows = dbh.db.update(DB_TABLE_ITEM, args, where, null);
		    dbh.db.update(DB_TABLE_ITEM, args, where, null);

		} catch (Exception e) {
			Log.e("Clementime", "InventoryFrame/updateInventoryField(): failed to update display field " + where + " - " + e.getMessage());
		}
	}
	/**
	 * Returns information about a new item added in inventory.
	 * @param	itemId	database id item
	 * @return	a hash map of strings with:</br>
	 * > item id,</br>
	 * > image,</br> 	
	 * > height of image (to fill BitmapTextureAtlas),</br>
	 * > width of image (to fill BitmapTextureAtlas)
	 */	
	
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
	/**
	 * Returns information about a combination.
	 * @param	idItem1	database id of first item combined
	 * @param	idItem2	database id of second item combined
	 * @param	type	on screen or in inventory
	 * @return	an integer tab with 3 entries:</br>
	 * > id combination if combination ok, 0 if no combination allowed,</br>
	 * > id of resulting item if combination allowed,</br> 	
	 * > on screen creation: 0=combination create a new item in inventory, 1=new item has to be created on screen
	 */	

	public int[] selectCombination(int idItem1, int idItem2, int type) {

		//********************************************************************************************************
		// results table:
		// results[0]: combination        - id combination if combination ok, 0 if no combination		
		// results[1]: combination        - id of resulting item
		// results[2]: on screen creation - 0 if combination is in inventory, 1 if new item has to be created on screen
		//********************************************************************************************************
		int[] results = {0,0,0};
		
		String query = " select _id as id, resulting_item as item_id, result_on_screen, trigger_id ";
		query += " from combination ";
		query += " where ((item1_id = " + idItem1 + " and item2_id = " + idItem2 +") " ;	// conditions
		query += " or (item1_id = " + idItem2 + " and item2_id = " + idItem1 +")) " ;	// conditions
		query += " and state = " + type;
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				results[0] = c.getInt(c.getColumnIndex("id"));
				
				// retrieve item id for side screen display	
				results[1] = c.getInt(c.getColumnIndex("item_id"));

				if (c.getInt(c.getColumnIndex("result_on_screen")) == 1) results[2] = c.getInt(c.getColumnIndex("result_on_screen"));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}
	
	
	//***********************************
	// WORLD
	//*********************************** 
	
	/**
	 * Finds id of triggers to launch.
	 * @param	id			id of the concerned object (depending on the table, can be a combination id, another trigger id...)
	 * @param	tableName	name of the concerned table (combination, trigger...)
	 * @return	one or more trigger id in an ArrayList<Integer>
	 */	
	public ArrayList<Integer> selectTriggers(int id, String tableName) {

		ArrayList<Integer> triggers = new ArrayList<Integer>();
		
		String query = " select trigger_id ";
		query += " from " + tableName;
		query += " where _id = " + id;
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				if (c.getString(c.getColumnIndex("trigger_id")) != null) {
					String[] triggersId = c.getString(c.getColumnIndex("trigger_id")).split(";");
					for (int i = 0; i < triggersId.length; i++) {
						triggers.add(Integer.parseInt(triggersId[i]));
					}					
				}
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return triggers;
	}
	/**
	 * Finds information about a trigger.
	 * @param	triggerId	id of the trigger to launch
	 * @return	a hash map of strings with:</br>
	 * > trigger id,</br>
	 * > object to trigger id,</br>
	 * > type of triggering,</br>
	 * > next trigger id,</br>
	 * > simultaneous trigger id 
	 */	
	public Map<String, String> selectTrigger(int triggerId) {

		Map<String, String> hm = new HashMap<String, String>();
		
		String query  = " select _id as id, to_trigger_id, type, next_trigger_id, simultaneous_trigger_id ";
		query += " from trigger ";
		query += " where _id = " + triggerId;
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			c.moveToFirst();

			hm.put("id", c.getString(c.getColumnIndex("id")));
			hm.put("to_trigger_id", c.getString(c.getColumnIndex("to_trigger_id")));
			hm.put("type", c.getString(c.getColumnIndex("type")));
			hm.put("next_trigger_id", c.getString(c.getColumnIndex("next_trigger_id")));
			hm.put("simultaneous_trigger_id", c.getString(c.getColumnIndex("simultaneous_trigger_id")));
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hm;
	}
	/**
	 * Finds information about an automatic move of the doll (not coming from player action, but from a trigger).
	 * @param	toTriggerId	id of the move to trigger
	 * @return	a hash map of strings with:</br>
	 * > x position the doll should reach,</br>
	 * > if doll is hidden (1=hidden/0=is not),</br>
	 * > y velocity of the doll during this move,
	 */	
	public Map<String, String> selectDollTrigger(int toTriggerId) {

		Map<String, String> hm = new HashMap<String, String>();
		
		String query  = " select to_x, doll_is_hidden, y_velocity ";
		query += " from move_doll ";
		query += " where _id = " + toTriggerId;
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			c.moveToFirst();

			hm.put("to_x", c.getString(c.getColumnIndex("to_x")));
			hm.put("doll_is_hidden", c.getString(c.getColumnIndex("doll_is_hidden")));
			hm.put("y_velocity", c.getString(c.getColumnIndex("y_velocity")));
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hm;
	}
	/**
	 * Finds information about a modifier; a modifier automatically changes database and is launched by a trigger.
	 * @param	modifierId	id of the modifier to launch
	 * @return	a hash map of strings with:</br>
	 * > new state that should be written in database,</br>
	 * > type of modification (to select right table to modify),</br>
	 * > id of the object that should be modified (a screen starting trigger, a look state, a take state, displaying of an item, etc...)
	 * @see eu.clementime.rds.World#activateModifier
	 */		
	public Map<String, String> selectModifier(int modifierId) {

		Map<String, String> hm = new HashMap<String, String>();
		
		String query = " select new_state, type, to_modify_id ";
		query += " from modifier ";
		query += " where _id = " + modifierId;
	
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			c.moveToFirst();

			hm.put("new_state", c.getString(c.getColumnIndex("new_state")));
			hm.put("type", c.getString(c.getColumnIndex("type")));
			hm.put("to_modify_id", c.getString(c.getColumnIndex("to_modify_id")));
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hm;
	}
	/**
	 * Finds if an item is takeable or not.
	 * @param	itemId	database id of item
	 * @return	boolean
	 */	
	public boolean selectTakeable(int itemId) {
		
		boolean takeable = false;
		
		String query = " select takeable ";
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
	/**
	 * Finds if an item is displayed on screen or not.
	 * @param	itemId	database id of item
	 * @return	0=not displayed/1=displayed
	 */		
	public int selectDisplayed(int itemId) {
		
		int displayed = 0;
		
		String query = " select " + DB_FIELD_DISPLAY + " from item where _id = " + itemId;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();

				// retrieve combination description for side screen display				
				displayed = c.getInt(c.getColumnIndex(DB_FIELD_DISPLAY));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return displayed;
	}
	/**
	 * Updates a field with information coming from a modifier.
	 * @param	tableName	table name for query
	 * @param	fieldName	field to be updated in this table
	 * @param	newState	new value of field
	 * @param	where		where clause
	 * @see eu.clementime.rds.World#activateModifier
	 */		
	public void updateWithModifier(String tableName, String fieldName, int newState, String where) {
		
		if (LOG_ON) Log.d("Clementime", "InventoryFrame/updateInventoryField()");

		try {	
		    ContentValues args = new ContentValues();
			args.put(fieldName, newState);

		    dbh.db.update(tableName, args, where, null);

		} catch (Exception e) {
			Log.e("Clementime", "InventoryFrame/updateInventoryField(): failed to update display field " + where + " - " + e.getMessage());
		}
	}

	//***********************************
	// GAME TOOLS
	//***********************************
	
	/**
	 * Search if phone language is available (English is used if not).
	 * @param	context	Android context, to retrieve files
	 * @return	language isocode
	 */	
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
	/**
	 * Selects text from general text table, for menus, settings...
	 * @param	reference	reference of searched text in text table
	 * @param	language	isocode language
	 * @return	text
	 * @see #selectLanguage
	 */	
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
	/**
	 * Selects player hand (left or right handed player) in settings.
	 * @param	reference	0=left; 1=right
	 */	
	public int selectPlayingHand() {

		String query = " select value as playing_hand from player_saving where reference = 'playing_hand' ";
		
		int playingHand = 1;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();
				
				playingHand = c.getInt(c.getColumnIndex("playing_hand"));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return playingHand;
	}
	
	//***********************************
	// DOLL
	//***********************************  
	
	/**
	 * Selects y velocity of the doll in this screen (0=flat ground; <0=go up from left to right; >0=go down from left to right).
	 * @param	screenId	id of the current screen
	 */	
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

	/**
	 * Finds which screen the doll is in.
	 *  @return	 id of the screen
	 */	
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
	
	/**
	 * Finds where the doll is at launching time (x and y).
	 * @return	tab with 2 entries:</br>
	 * > [0] x position of doll,</br>
	 * > [1] y position
	 */	
	public int[] selectDollPosition() {
		
		if (LOG_ON) Log.d("Clementime", "World/getDollPosition()");
		
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
			Log.e("Clementime", "World/getDollPosition(): failed to get x position from player table.");
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
			Log.e("Clementime", "World/getDollPosition(): failed to get y position from player table.");
		}
		
		return dollPos;	
	}
	
	//***********************************
	// WORLD
	//*********************************** 
	
	/**
	 * Returns information about a screen from exit table, when doll leaves a screen for another (also used for first screen, with exit 0).
	 * @return	tab with 4 entries:</br>
	 * > [0] screen id,</br>
	 * > [1] x position of doll in this new screen,</br>
	 * > [2] y position,</br>
	 * > [3] trigger to launch when starting screen
	 */	
	public int[] selectFirstScreenFeatures() {
		
		if (LOG_ON) Log.i("Clementime", "World/getFirstScreenFeatures() ");

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
				if (LOG_ON) Log.v("Clementime", "World/getFirstScreenFeatures(): starting x: " + features[2] + " starting y: " + features[3] + " starting trigger: " + features[1]);
			}

			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return features;
	}
	/**
	 * Returns information about an animation.
	 * @param	animId	database animation id
	 * @return	tab with 7 entries:</br>
	 * > [0] number of the first frame,</br>
	 * > [1] number of the last frame,</br>
	 * > [2] duration of each frame (in ms),</br>
	 * > [3] number of loop of the animation,</br>
	 * > [4] x velocity of this animation (=animation is playing AND moving on screen),</br>
	 * > [5] y velocity of this animation (=animation is playing AND moving on screen),</br>
	 * > [6] is the doll hidden or not (1=hidden/0=is not)
	 */	
	public Map<String, Integer> selectAnimFeatures(int animId) {
		
		Map<String, Integer> hm = new HashMap<String, Integer>();
		
		String query = " select first_frame, last_frame, frame_duration, loop, x_velocity, y_velocity, doll_is_hidden ";
		query += " from animation a left join playing_animation p on a._id = p.anim_id ";
		query += " where p._id = " + animId;
		
		try {
			Cursor c = dbh.db.rawQuery(query, new String [] {});
			
			if (c.getCount() != 0) {
				c.moveToFirst();

				hm.put("first_frame", c.getInt(c.getColumnIndex("first_frame")));
				hm.put("last_frame", c.getInt(c.getColumnIndex("last_frame")));
				hm.put("frame_duration", c.getInt(c.getColumnIndex("frame_duration")));
				hm.put("loop", c.getInt(c.getColumnIndex("loop")));
				hm.put("x_velocity", c.getInt(c.getColumnIndex("x_velocity")));
				hm.put("y_velocity", c.getInt(c.getColumnIndex("y_velocity")));
				hm.put("doll_is_hidden", c.getInt(c.getColumnIndex("doll_is_hidden")));
			}
	
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hm;		
	}
	/**
	 * Updates doll position when leaving game.
	 * @param	screenId	id of the current screen
	 * @param	x			x position of doll in this screen
	 * @param	y			y position of doll in this screen
	 */	
	public void updateWhenSaving(int screenId, float x, float y) {
			
		    ContentValues args = new ContentValues();
		    
			args.put("value", screenId);
		    dbh.db.update("player_saving", args, " reference = 'screen_id' ", null);
		    
			args.put("value", x);
		    dbh.db.update("player_saving", args, " reference = 'x_doll' ", null);
		    
			args.put("value", y);
		    dbh.db.update("player_saving", args, " reference = 'y_doll' ", null);	
	}

	
	//***********************************
	// TALK
	//***********************************
	
	public void selectTalks(int screenId) {
		
	}
}
