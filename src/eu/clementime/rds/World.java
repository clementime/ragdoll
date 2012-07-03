package eu.clementime.rds;

//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_AREA_LOOK;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_CHAR_LOOK;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_EXIT;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_LOOK;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_TAKE;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_ACTION_TALK;
//import static eu.clementime.rds.Constants.DB_DESCRIPTION_COMBINATION;
import static eu.clementime.rds.Constants.DB_FIELD_DESC_STATE;
import static eu.clementime.rds.Constants.DB_FIELD_DISPLAY;
import static eu.clementime.rds.Constants.DB_FIELD_EXIT;
import static eu.clementime.rds.Constants.DB_FIELD_LOOK_STATE;
import static eu.clementime.rds.Constants.DB_FIELD_STATE;
import static eu.clementime.rds.Constants.DB_FIELD_TAKEABLE;
import static eu.clementime.rds.Constants.DB_FIELD_TAKE_STATE;
import static eu.clementime.rds.Constants.DB_FIELD_TALK_STATE;
import static eu.clementime.rds.Constants.DB_FIELD_TRIGGER;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_AREA_LOOK;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_CHAR_LOOK;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_COMB;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_EXIT;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_LOOK;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_POS;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_QUESTION_STATE;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_SCREEN_TRIGGER;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_SWITCH_COMB;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_TAKE;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_TAKEABLE;
import static eu.clementime.rds.Constants.DB_MODIFIER_TYPE_TALK;
import static eu.clementime.rds.Constants.DB_TABLE_ANIMATION;
import static eu.clementime.rds.Constants.DB_TABLE_CHARACTER;
import static eu.clementime.rds.Constants.DB_TABLE_COMBINATION;
import static eu.clementime.rds.Constants.DB_TABLE_EXIT;
import static eu.clementime.rds.Constants.DB_TABLE_ITEM;
import static eu.clementime.rds.Constants.DB_TABLE_MODIFIER;
import static eu.clementime.rds.Constants.DB_TABLE_MOVE_DOLL;
import static eu.clementime.rds.Constants.DB_TABLE_QUESTION;
import static eu.clementime.rds.Constants.DB_TABLE_SCREEN_AREA;
import static eu.clementime.rds.Constants.DB_TABLE_SCREEN_ITEM;
import static eu.clementime.rds.Constants.DB_TABLE_TEXT_ANIM;
import static eu.clementime.rds.Constants.DB_TABLE_TRIGGER;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_ACTION;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_ANIM;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_DOLL;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_SV_ANIM;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_SV_ITEM;
import static eu.clementime.rds.Constants.DB_TRIGGER_TYPE_TEXT;
import static eu.clementime.rds.Constants.DEVELOPMENT;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_ANIM;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_AREA;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_CHAR;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_ITEM;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

//import eu.clementime.rds.object.InventoryItem;
//import eu.clementime.rds.util.DatabaseHandler;

public class World {
	
	private Context context;
	private DatabaseAccess db;

//	public int screenId;
	
	public World(DatabaseHandler dbh, Context context, int startingScreen) {
		this.context = context;
		this.db = new DatabaseAccess(dbh);		

//		this.screenId = startingScreen;
	}
		
//	public int[] combineItems(int idItem1, int idItem2, int type) {
//
//		//********************************************************************************************************
//		// results table:
//		// results[0]: combination        - id combination if combination ok, 0 if no combination		
//		// results[1]: combination        - id of resulting item
//		// results[2]: item 1 discarded   - item id if 1st item has to be discarded, 0 if item is to keep 
//		// results[3]: item 2 discarded   - item id if 2nd item has to be discarded, 0 if item is to keep 
//		// results[4]: on screen creation - 0 if combination is in inventory, 1 if new item has to be created on screen
//		//********************************************************************************************************
//
//		int[] results = {0,0,0};
//		
//		String query = " select _id as id, resulting_item as item_id, result_on_screen, trigger_id ";
//		query += " from combination ";
//		query += " where ((item1_id = " + idItem1 + " and item2_id = " + idItem2 +") " ;	// conditions
//		query += " or (item1_id = " + idItem2 + " and item2_id = " + idItem1 +")) " ;	// conditions
//		query += " and state = " + type;
//	
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//				
//				results[0] = c.getInt(c.getColumnIndex("id"));
//				
//				// retrieve item id for side screen display	
//				results[1] = c.getInt(c.getColumnIndex("item_id"));
//
//				if (c.getInt(c.getColumnIndex("result_on_screen")) == 1) results[2] = c.getInt(c.getColumnIndex("result_on_screen"));
//			}
//	
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return results;
//	}
//	
//	public ArrayList<Integer> getTriggers(int id, String tableName) {
//
//		ArrayList<Integer> triggers = new ArrayList<Integer>();
//		
//		String query = " select trigger_id ";
//		query += " from " + tableName;
//		query += " where _id = " + id;
//	
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//				
//				if (c.getString(c.getColumnIndex("trigger_id")) != null) {
//					String[] triggersId = c.getString(c.getColumnIndex("trigger_id")).split(";");
//					for (int i = 0; i < triggersId.length; i++) {
//						triggers.add(Integer.parseInt(triggersId[i]));
//					}					
//				}
//			}
//	
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return triggers;
//	}
//	
	public boolean isItemTakeable(int itemId) {
		return db.selectTakeable(itemId);
	}	
//	
//	public int[] activateTrigger(int triggerId) {
//		
//		//********************************************************************************************************
//		// results table:
//		// results[0]: next trigger				- if there is another trigger launched following this one, id of the trigger, 0 if not 		
//		// results[1]: animation on screen		- if the trigger is to animate a screenAnim, id of the screeAnim
//		// results[2]: doll moving				- if the trigger is to animate the doll, x where the doll has to be moved
//		// results[3]: doll is hidden			- if doll is moving, it can be off screen
//		// results[4]: text activated			- if an animation text is activated, id of this text
//		// results[5]: take item from screen	- id of item to add in inventory
//		// results[6]: doll moving y velocity
//		// results[7]: hide/show screen item
//		// results[8]: hide/show animation	
//		// results[9]: simultaneous trigger		- if there is another trigger to launch at the same time, id of the trigger
//		//********************************************************************************************************
//
//		int[] triggerResults = {0,0,0,0,0,0,0,0,0,0};
//		//boolean ok = true;
//		
//		//String query  = " select to_trigger_id, type, items_needed ";
//		String query  = " select _id as id, to_trigger_id, type, next_trigger_id, simultaneous_trigger_id ";
//		query += " from trigger ";
//		query += " where _id = " + triggerId;
//		
//		// check if every items needed for activating are in inventory
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//				
//				int type = 0;
//				int toTriggerId = 0;
//				//String nextTriggerTable = "";					
//						
//				type = c.getInt(c.getColumnIndex("type"));
//				toTriggerId = c.getInt(c.getColumnIndex("to_trigger_id"));
//				
//				switch (type) {
//							
//					case DB_TRIGGER_TYPE_ANIM:			// an animation is launched
//						
//						//nextTriggerTable = DB_TABLE_ANIMATION;
//						triggerResults[1] = toTriggerId;
//						
//						break;
//					
//					case DB_TRIGGER_TYPE_ACTION:	// OR a change on items (combination, action, take item on screen...)
//	
//						//nextTriggerTable = DB_TABLE_MODIFIER;
//						triggerResults[5] = activateModifier(toTriggerId);
//					    
//					    break;
//	
//					case DB_TRIGGER_TYPE_DOLL:	// OR move/hide doll	
//	
//						//nextTriggerTable = DB_TABLE_MOVE_DOLL;
//						
//						query  = " select to_x, doll_is_hidden, y_velocity ";
//						query += " from move_doll ";
//						query += " where _id = " + toTriggerId;
//						
//						try {
//							Cursor dollCursor = dbh.db.rawQuery(query, new String [] {});
//							
//							if (dollCursor.getCount() != 0) {
//								dollCursor.moveToFirst();
//								triggerResults[2] = dollCursor.getInt(dollCursor.getColumnIndex("to_x"));
//								triggerResults[3] = dollCursor.getInt(dollCursor.getColumnIndex("doll_is_hidden"));
//								triggerResults[6] = dollCursor.getInt(dollCursor.getColumnIndex("y_velocity"));
//							}
//							
//							dollCursor.close();
//							
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						
//						break;
//						
//					case DB_TRIGGER_TYPE_TEXT:	// show a bubble
//						
//						//nextTriggerTable = DB_TABLE_TEXT_ANIM;
//						triggerResults[4] = toTriggerId;
//						
//						break;
//						
//					case DB_TRIGGER_TYPE_SV_ITEM:	// hide/show item
//						
//						//nextTriggerTable = DB_TABLE_TRIGGER;
//						triggerResults[7] = toTriggerId;
//						//toTriggerId = c.getInt(c.getColumnIndex("id"));
//						
//						break;
//						
//					case DB_TRIGGER_TYPE_SV_ANIM:	// hide/show anim
//						
//						//nextTriggerTable = DB_TABLE_TRIGGER;
//						triggerResults[8] = toTriggerId;
//						//toTriggerId = c.getInt(c.getColumnIndex("id"));
//						
//						break;
//				}
//				
//				triggerResults[0] = c.getInt(c.getColumnIndex("next_trigger_id"));
//				triggerResults[9] = c.getInt(c.getColumnIndex("simultaneous_trigger_id"));
//				//triggerResults[0] = nextTrigger(nextTriggerTable, toTriggerId);
//			}
//			
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return triggerResults;
//	}
//	
//	private int activateModifier(int toTriggerId) {
//		
//		int itemId = 0;
//		int type = 0;
//		
//		String query = " select new_state, type, to_modify_id ";
//		query += " from modifier ";
//		query += " where _id = " + toTriggerId;
//			
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//
//				// find what to modify in database
//				String tableName = "";
//				String fieldName = "";
//				String idField = "";
//				String where = "";
//				ContentValues args;
//				int toModifyId = 0;
//
//				toModifyId = c.getInt(c.getColumnIndex("to_modify_id"));
//				
//				// choose what table to modify
//				type = c.getInt(c.getColumnIndex("type"));
//
//				switch (type) {
//				
//					case DB_MODIFIER_TYPE_COMB: // (combination)
//						
//						tableName = DB_TABLE_COMBINATION;
//						fieldName = DB_FIELD_STATE;
//						idField = "_id";
//					    
//					    break;		    
//						
//					case DB_MODIFIER_TYPE_POS: // (change item position) 
//
//						tableName = DB_TABLE_ITEM;
//						fieldName = DB_FIELD_DISPLAY;
//						idField = "_id";
//						
//						itemId = c.getInt(c.getColumnIndex("to_modify_id"));
//						
//					    break;					    
//						
//					case DB_MODIFIER_TYPE_TAKE: // (action take on item) 
//
//						tableName = DB_TABLE_SCREEN_ITEM;
//						fieldName = DB_FIELD_TAKE_STATE;
//						idField = "item_id";
//						
//					    break;
//						
//					case DB_MODIFIER_TYPE_LOOK: // (look item on screen) 
//
//						tableName = DB_TABLE_SCREEN_ITEM;
//						fieldName = DB_FIELD_LOOK_STATE;
//						idField = "item_id";
//						
//					    break;
//					    
//					case DB_MODIFIER_TYPE_TALK:
//					
//						tableName = DB_TABLE_CHARACTER;
//						fieldName = DB_FIELD_TALK_STATE;
//						idField = "_id";
//						
//						break;
//					
//					case DB_MODIFIER_TYPE_EXIT: // (enable/disable exit)
//						
//						tableName = DB_TABLE_SCREEN_AREA;
//						fieldName = DB_FIELD_EXIT;
//						idField = "_id";
//						
//						break;
//
//					case DB_MODIFIER_TYPE_TAKEABLE: // (set item takeable) 
//
//						tableName = DB_TABLE_SCREEN_ITEM;
//						fieldName = DB_FIELD_TAKEABLE;
//						idField = "item_id";
//						
//					    break;
//										    
//					case DB_MODIFIER_TYPE_SWITCH_COMB: // switch description on combination
//
//						tableName = DB_TABLE_COMBINATION;
//						fieldName = DB_FIELD_DESC_STATE;
//						idField = "_id";
//					
//					case DB_MODIFIER_TYPE_AREA_LOOK: // (look area on screen) 
//
//						tableName = DB_TABLE_SCREEN_AREA;
//						fieldName = DB_FIELD_LOOK_STATE;
//						idField = "_id";
//						
//					    break;
//					    
//					case DB_MODIFIER_TYPE_CHAR_LOOK: // (look area on screen) 
//
//						tableName = DB_TABLE_CHARACTER;
//						fieldName = DB_FIELD_LOOK_STATE;
//						idField = "_id";
//						
//					    break;
//					
//					case DB_MODIFIER_TYPE_QUESTION_STATE: // (look area on screen) 
//
//						tableName = DB_TABLE_QUESTION;
//						fieldName = DB_FIELD_STATE;
//						idField = "_id";
//						
//					    break;
//					    
//					case DB_MODIFIER_TYPE_SCREEN_TRIGGER: // (change starting trigger) 
//
//						tableName = DB_TABLE_EXIT;
//						fieldName = DB_FIELD_TRIGGER;
//						idField = "_id";
//						
//					    break;
//				}			
//				
//				where = " " + idField + " = " + toModifyId;	
//				
//			    args = new ContentValues();
//				args.put(fieldName, c.getInt(c.getColumnIndex("new_state")));
//	
//			    dbh.db.update(tableName, args, where, null);
//			}
//				
//			c.close();
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	    
//	    return itemId;
//	}
//	
//	public void save(float x, float y) {
//		
//	    ContentValues args = new ContentValues();
//	    
//		args.put("value", this.screenId);
//	    dbh.db.update("player_saving", args, " reference = 'screen_id' ", null);
//	    
//		args.put("value", x);
//	    dbh.db.update("player_saving", args, " reference = 'x_doll' ", null);
//	    
//		args.put("value", y);
//	    dbh.db.update("player_saving", args, " reference = 'y_doll' ", null);	
//	}
//	



//	
//	public String isItemDisplayed(int itemId) {
//		
//		String query;
//		String pos = "";
//		
//		query = " select " + DB_FIELD_DISPLAY + " from item where _id = " + itemId;
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//
//				// retrieve combination description for side screen display				
//				pos = c.getString(c.getColumnIndex(DB_FIELD_DISPLAY));
//			}
//	
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return pos;
//	}
//	
//	public int[] getAnimFeatures(int animId) {
//		
//		String query;
//		int[] features = {0,0,0,0,0,0,0};	
//		
//		query = " select first_frame, last_frame, frame_duration, loop, x_velocity, y_velocity, doll_is_hidden ";
//		query += " from animation a left join playing_animation p on a._id = p.anim_id ";
//		query += " where p._id = " + animId;
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//
//				features[0] = c.getInt(c.getColumnIndex("first_frame"));
//				features[1] = c.getInt(c.getColumnIndex("last_frame"));
//				features[2] = c.getInt(c.getColumnIndex("frame_duration"));
//				features[3] = c.getInt(c.getColumnIndex("loop"));
//				features[4] = c.getInt(c.getColumnIndex("x_velocity"));
//				features[5] = c.getInt(c.getColumnIndex("y_velocity"));
//				features[6] = c.getInt(c.getColumnIndex("doll_is_hidden"));
//			}
//	
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return features;		
//	}
//


	// features[0] = id exit, features[1] = to trigger before leaving	
	public int[] getFirstScreenFeatures() {
		return db.selectFirstScreenFeatures();
	}
//	
//	public int[] getScreenFeatures(int screenId) {
//		
//		Log.i("Clementime", "World/getFirstScreenFeatures() ");
//
//		// features[0] = to trigger when arriving - features[1] = x - features[2] = y	
//		int[] features = {0,0,0,0};
//		
//		String query = " select starting_x, starting_y, after_trigger_id ";
//		query += " from exit e where to_screen_id = " + screenId;
//		query += " order by starting_x ASC ";
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//			
//			if (c.getCount() != 0) {
//				c.moveToFirst();
//				features[0] = c.getInt(c.getColumnIndex("after_trigger_id"));
//				features[1] = c.getInt(c.getColumnIndex("starting_x"));
//				features[2] = c.getInt(c.getColumnIndex("starting_y"));
//				Log.v("Clementime", "World/getFirstScreenFeatures(): starting x: " + features[2] + " starting y: " + features[3] + " starting trigger: " + features[1]);
//			}
//
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return features;
//	}
}
