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
import static eu.clementime.rds.Constants.SCALE;
import static eu.clementime.rds.Constants.LOG_ON;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_ANIM;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_AREA;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_CHAR;
//import static eu.clementime.rds.Constants.OBJECT_TYPE_ITEM;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class World {

	private DatabaseAccess db;
	
	public World(DatabaseHandler dbh, Context context, int startingScreen) {
		this.db = new DatabaseAccess(dbh);	
	}
		
	public int[] combineItems(int idItem1, int idItem2, int type) {
		return db.selectCombination(idItem1, idItem2, type);
	}
	
	public ArrayList<Integer> getTriggers(int id, String tableName) {
		return db.selectTriggers(id, tableName);
	}
	
	public boolean isItemTakeable(int itemId) {
		return db.selectTakeable(itemId);
	}	
	
	public int[] activateTrigger(int triggerId) {
		
		//********************************************************************************************************
		// results table:
		// results[0]: next trigger				- if there is another trigger launched following this one, id of the trigger, 0 if not 		
		// results[1]: animation on screen		- if the trigger is to animate a screenAnim, id of the screeAnim
		// results[2]: doll moving				- if the trigger is to animate the doll, x where the doll has to be moved
		// results[3]: doll is hidden			- if doll is moving, it can be off screen
		// results[4]: text activated			- if an animation text is activated, id of this text
		// results[5]: take item from screen	- id of item to add in inventory
		// results[6]: doll moving y velocity
		// results[7]: hide/show screen item
		// results[8]: hide/show animation	
		// results[9]: simultaneous trigger		- if there is another trigger to launch at the same time, id of the trigger
		//********************************************************************************************************

		int[] triggerResults = {0,0,0,0,0,0,0,0,0,0};
		
		try {
			
			Map<String, String> hm = db.selectTrigger(triggerId);
				
			int type = 0;
			int toTriggerId = 0;	
					
			type = Integer.parseInt(hm.get("type"));
			toTriggerId = Integer.parseInt(hm.get("to_trigger_id"));
			
			switch (type) {
						
				case DB_TRIGGER_TYPE_ANIM:			// an animation is launched
					
					triggerResults[1] = toTriggerId;				
					break;
				
				case DB_TRIGGER_TYPE_ACTION:	// OR a change on items (combination, action, take item on screen...)

					triggerResults[5] = activateModifier(toTriggerId);
				    break;

				case DB_TRIGGER_TYPE_DOLL:	// OR move/hide doll	
					
					Map<String, String> hmDoll = db.selectDollTrigger(toTriggerId);
					if (hmDoll.get("to_x") != null)				triggerResults[2] = Integer.parseInt(hmDoll.get("to_x"));
					if (hmDoll.get("doll_is_hidden") != null)	triggerResults[3] = Integer.parseInt(hmDoll.get("doll_is_hidden"));
					if (hmDoll.get("y_velocity") != null)		triggerResults[6] = Integer.parseInt(hmDoll.get("y_velocity"));
					
					if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 2 " + hmDoll.get("to_x"));
					if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 3 " + hmDoll.get("doll_is_hidden"));
					if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 6 " + hmDoll.get("y_velocity"));
					
					break;
					
				case DB_TRIGGER_TYPE_TEXT:	// show a bubble
					
					triggerResults[4] = toTriggerId;
					break;
					
				case DB_TRIGGER_TYPE_SV_ITEM:	// hide/show item
					
					triggerResults[7] = toTriggerId;
					break;
					
				case DB_TRIGGER_TYPE_SV_ANIM:	// hide/show anim
					
					triggerResults[8] = toTriggerId;					
					break;
			}
		
			if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 0 " + hm.get("next_trigger_id"));
			if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 5 " + triggerResults[5]);
			if (LOG_ON) Log.d("Clementime", "World/activateTrigger(): Trigger results 1, 4, 7 & 8: " + toTriggerId);
			
			if (hm.get("next_trigger_id") != null)	triggerResults[0] = Integer.parseInt(hm.get("next_trigger_id"));
			if (hm.get("simultaneous_trigger_id") != null)	triggerResults[9] = Integer.parseInt(hm.get("simultaneous_trigger_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return triggerResults;
	}
	
	private int activateModifier(int toTriggerId) {
		
		int itemId = 0;
		int type = 0;
		
		Map<String, String> hm = db.selectModifier(toTriggerId);

		// find what to modify in database
		String tableName = "";
		String fieldName = "";
		String idField = "";
		String where = "";
		int toModifyId = 0;

		toModifyId = Integer.parseInt(hm.get("to_modify_id"));
		
		// choose what table to modify
		type = Integer.parseInt(hm.get("type"));

		switch (type) {
		
			case DB_MODIFIER_TYPE_COMB: // (combination)
				
				tableName = DB_TABLE_COMBINATION;
				fieldName = DB_FIELD_STATE;
				idField = "_id";
			    
			    break;		    
				
			case DB_MODIFIER_TYPE_POS: // (change item position) 

				tableName = DB_TABLE_ITEM;
				fieldName = DB_FIELD_DISPLAY;
				idField = "_id";
				
				itemId = Integer.parseInt(hm.get("to_modify_id"));
				
			    break;					    
				
			case DB_MODIFIER_TYPE_TAKE: // (action take on item) 

				tableName = DB_TABLE_SCREEN_ITEM;
				fieldName = DB_FIELD_TAKE_STATE;
				idField = "item_id";
				
			    break;
				
			case DB_MODIFIER_TYPE_LOOK: // (look item on screen) 

				tableName = DB_TABLE_SCREEN_ITEM;
				fieldName = DB_FIELD_LOOK_STATE;
				idField = "item_id";
				
			    break;
			    
			case DB_MODIFIER_TYPE_TALK:
			
				tableName = DB_TABLE_CHARACTER;
				fieldName = DB_FIELD_TALK_STATE;
				idField = "_id";
				
				break;
			
			case DB_MODIFIER_TYPE_EXIT: // (enable/disable exit)
				
				tableName = DB_TABLE_SCREEN_AREA;
				fieldName = DB_FIELD_EXIT;
				idField = "_id";
				
				break;

			case DB_MODIFIER_TYPE_TAKEABLE: // (set item takeable) 

				tableName = DB_TABLE_SCREEN_ITEM;
				fieldName = DB_FIELD_TAKEABLE;
				idField = "item_id";
				
			    break;
								    
			case DB_MODIFIER_TYPE_SWITCH_COMB: // switch description on combination

				tableName = DB_TABLE_COMBINATION;
				fieldName = DB_FIELD_DESC_STATE;
				idField = "_id";
			
			case DB_MODIFIER_TYPE_AREA_LOOK: // (look area on screen) 

				tableName = DB_TABLE_SCREEN_AREA;
				fieldName = DB_FIELD_LOOK_STATE;
				idField = "_id";
				
			    break;
			    
			case DB_MODIFIER_TYPE_CHAR_LOOK: // (look area on screen) 

				tableName = DB_TABLE_CHARACTER;
				fieldName = DB_FIELD_LOOK_STATE;
				idField = "_id";
				
			    break;
			
			case DB_MODIFIER_TYPE_QUESTION_STATE: // (look area on screen) 

				tableName = DB_TABLE_QUESTION;
				fieldName = DB_FIELD_STATE;
				idField = "_id";
				
			    break;
			    
			case DB_MODIFIER_TYPE_SCREEN_TRIGGER: // (change starting trigger) 

				tableName = DB_TABLE_EXIT;
				fieldName = DB_FIELD_TRIGGER;
				idField = "_id";
				
			    break;
		}			
		
		where = " " + idField + " = " + toModifyId;	
		
		db.updateWithModifier(tableName, fieldName, Integer.parseInt(hm.get("new_state")), where);
		
	    return itemId;
	}
	
	public void save(int screenId, float x, float y) {
		db.updateWhenSaving(screenId, x, y);
	}	
	
	public int isItemDisplayed(int itemId) {
		return db.selectDisplayed(itemId);
	}
	
	public Map<String, Integer> getAnimFeatures(int animId) {
		return db.selectAnimFeatures(animId);		
	}

	// features[0] = id exit, features[1] = to trigger before leaving	
	public int[] getFirstScreenFeatures() {
		return db.selectFirstScreenFeatures();
	}
//	
//	public int[] getScreenFeatures(int screenId) {
//		
//		if (LOG_ON) Log.i("Clementime", "World/getFirstScreenFeatures() ");
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
//				if (LOG_ON) Log.v("Clementime", "World/getFirstScreenFeatures(): starting x: " + features[2] + " starting y: " + features[3] + " starting trigger: " + features[1]);
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
	public void calculateWalkArea(float dollFeetPosition) {	
			Constants.WALK_AREA_Y_POS = dollFeetPosition + Constants.WALK_AREA_UNDER_FEET;
			if (LOG_ON) Log.i("Clementime", "Constants/calculateWalkArea: walk area Y pos bottom " + Constants.WALK_AREA_Y_POS);
			if (LOG_ON) Log.i("Clementime", "Constants/calculateWalkArea: walk area Y pos top " + (Constants.WALK_AREA_Y_POS - Constants.WALK_AREA_SIZE));
	}
	
	public boolean checkWalkArea(int xMin, int xMax, float touchedX, float touchedY) {
		if (LOG_ON) Log.d("Clementime", "Constants/checkWalkArea: touch y " + touchedY);
		
		if (touchedY <= Constants.WALK_AREA_Y_POS
				&& touchedY >= Constants.WALK_AREA_Y_POS - Constants.WALK_AREA_SIZE
				&& touchedX >= xMin && touchedX <= xMax) return true;
		else return false;
	}

}
