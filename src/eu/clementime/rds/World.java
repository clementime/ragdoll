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
import static eu.clementime.rds.Constants.LOG_ON;
import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SCALE_POSITION;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;

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

/**
* This class contains playing rules (for actions, triggering, database modifiers, saving, settings, walking...) and controls database access.
* @author Cl&eacute;ment
* @version 1.0
*/
public class World {

	private DatabaseAccess db;
	
	/**
	 * For logs only.
	 */	
	private String className = "World";
	
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
	/**
	 * Triggers 6 kinds of actions: launch an animation, change database with a modifier, move or hide doll, show a bubble, hide/show an item or hide/show an animation.
	 * @param	dbh		database handler is stored for upcoming database calls
	 * @return	integer array with 10 entries:</br>
	 * next trigger				- if there is another trigger launched following this one, id of the trigger, 0 if not,</br>
	 * animation on screen		- if the trigger is to animate a screenAnim, id of the screeAnim,</br>
	 * doll moving				- if the trigger is to animate the doll, x where the doll has to be moved,</br>
	 * doll is hidden			- if doll is moving, it can be off screen,</br>
	 * text activated			- if an animation text is activated, id of this text,</br>
	 * take item from screen	- id of item to add in inventory,</br>
	 * doll moving y velocity,</br>
	 * hide/show screen item,</br>
	 * hide/show animation,</br>
	 * simultaneous trigger		- if there is another trigger to launch at the same time, id of the trigger
	 */
	public int[] activateTrigger(int triggerId) {

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
					
					if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 2 " + hmDoll.get("to_x"));
					if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 3 " + hmDoll.get("doll_is_hidden"));
					if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 6 " + hmDoll.get("y_velocity"));
					
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
		
			if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 0 " + hm.get("next_trigger_id"));
			if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 5 " + triggerResults[5]);
			if (LOG_ON) Log.d("Clementime", className + "/activateTrigger(): Trigger results 1, 4, 7 & 8: " + toTriggerId);
			
			if (hm.get("next_trigger_id") != null)	triggerResults[0] = Integer.parseInt(hm.get("next_trigger_id"));
			if (hm.get("simultaneous_trigger_id") != null)	triggerResults[9] = Integer.parseInt(hm.get("simultaneous_trigger_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return triggerResults;
	}
	// TODO: this method should be seriously reworked
	/**
	 * Triggers +/-10 kinds of modifier, that is to say changes in database: combination (switch allowed/not allowed),
	 * change item position (switch screen/inventory/nowhere), take, look, talk, takeable (yes/no),
	 * exit (visible/not visible), modify starting trigger in screen table.
	 * @param	toTriggerId	modifier to trigger
	 * @return	id of item (for change item position only) 
	 */
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
				
			case DB_MODIFIER_TYPE_POS: // (change item position, screen/inventory/nowhere) 

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

			// TODO: description on combination doesn't exist anymore. Simply remove it. 
			case DB_MODIFIER_TYPE_SWITCH_COMB: // switch description on combination

				tableName = DB_TABLE_COMBINATION;
				fieldName = DB_FIELD_DESC_STATE;
				idField = "_id";
			
			case DB_MODIFIER_TYPE_AREA_LOOK: // (look area on screen) 

				tableName = DB_TABLE_SCREEN_AREA;
				fieldName = DB_FIELD_LOOK_STATE;
				idField = "_id";
				
			    break;
			    
			// TODO: character table doesn't exist anymore. Should switch to anim table    
			case DB_MODIFIER_TYPE_CHAR_LOOK: // (look anim on screen) 

				tableName = DB_TABLE_CHARACTER;
				fieldName = DB_FIELD_LOOK_STATE;
				idField = "_id";
				
			    break;

			// TODO: question table doesn't exist anymore. Should switch to bubble/talk table    
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
	/**
	 * When saving, x and y doll position are saved as for larger allowed screen density.
	 */	
	public void save(int screenId, float x, float y) {
		x = x / SCALE_POSITION;
		y = (y + SET_BACKGROUND_POSITION_Y - MARGIN_Y) / SCALE_POSITION;
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
	// TODO: currently calculates only rectangular area, for flat walking. Has to be trapezoidal, for flat and inclined walking.
	/**
	 * Calculates a trapezoidal area where player activates walking when touching it.
	 * @param	dollFeetPosition	y at start (why, I have no idea... oh yes, I remember: to calculate y max and min of walking area)
	 */	
	public void calculateWalkArea(float dollFeetPosition) {	
			Constants.WALK_AREA_Y_POS = dollFeetPosition + Constants.WALK_AREA_UNDER_FEET;
			if (LOG_ON) Log.i("Clementime", className + "/calculateWalkArea: walk area Y pos bottom " + Constants.WALK_AREA_Y_POS);
			if (LOG_ON) Log.i("Clementime", className + "/calculateWalkArea: walk area Y pos top " + (Constants.WALK_AREA_Y_POS - Constants.WALK_AREA_SIZE));
	}
	/**
	 * Checks if the player touched inside walk area.
	 * @see #calculateWalkArea(float)
	 */
	public boolean checkWalkArea(int xMin, int xMax, float touchedX, float touchedY) {
		if (LOG_ON) Log.d("Clementime", className + "/checkWalkArea: touch y " + touchedY);
		
		if (touchedY <= Constants.WALK_AREA_Y_POS
				&& touchedY >= Constants.WALK_AREA_Y_POS - Constants.WALK_AREA_SIZE
				&& touchedX >= xMin && touchedX <= xMax) return true;
		else return false;
	}

}
