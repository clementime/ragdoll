package eu.clementime.rds;

public final class Constants  {

	/*         */
	/* GENERAL */
	/*         */
	public static final boolean DEVELOPMENT = true;	//TODO: TO INACTIVATE IN PRODUCTION
//	public static final float LOOP_LOG_INTERVAL = 2;
	
	public static final int BACKGROUND_MAX_HEIGHT = 320;
	
	public static final float MASK_ALPHA_LAYER = 0.4f;
	public static final float INV_ALPHA_LAYER = 0.8f;
	public static final float INVBOX_ALPHA_LAYER = 0.3f;
	public static final String DEFAULT_IMAGE = "default_img";
	public static final String ANIMATION_IMAGE_PREFIX = "anim_";
	public static final int NO_END_LOOP = 1000;
	
	/*             */
	/* SIDE SCREEN */
	/*             */
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = 2;
//
//	public static final int SIDE_SCREEN_AREA = 80; // when you press side doll keep moving
//	
	public static final int ACTION_TAKE = 1;
	public static final int ACTION_LOOK = 2;	
	public static final int ACTION_TALK = 3;	
	public static final int ACTION_EXIT = 4;
	
	public static final int ACTION_POINTER_SIZE = 60;
	public static final int ACTION_POINTER_OUT = -1000;
//	
//	public static final int LEFT_HANDED = 75;
//	public static final int RIGHT_HANDED = -75;
//	
//	//*******************
//	// STATUS & MODES
//	//*******************
	public static final int STATUS_ACTION = 0;		// walk, try to look, take, talk, exit, do whatever you want
	public static final int STATUS_INVENTORY = 1;		// inventory is open, walk and actions not allowed
	public static final int STATUS_ANIM = 2;			// an animation is running, or talking is activated
	public static final int STATUS_MAP = 3;
	
	// status action modes
	public static final int MODE_ACTION_WALK = 0;		// player walk and search for objects
	public static final int MODE_ACTION_WAIT = 1;		// action manager activated, waiting for player action
//	public static final int MODE_ACTION_RUNNING = 2;	// an action (look, moving doll to talk, take or exit) is running
	// inventory different modes
	public static final int MODE_INVENTORY_OPEN = 3;	// inventory is open
	public static final int MODE_INVENTORY_ZOOM = 4;	// zoom is open
	public static final int MODE_INVENTORY_DROP = 5;	// player is dragging an object from inventory
	// status anim modes
	public static final int MODE_ANIM_ACTION = 6;		// an action (look, moving doll to talk, take or exit) is running 
	public static final int MODE_ANIM_TALK = 7;		// doll is talking
	public static final int MODE_ANIM_RUNNING = 8;	// an animation is running
	
	//*********
	// CLICKS
	//*********
	public static final int CLICK_OFF = 0;
	public static final int CLICK_INVENTORY = 1;
	public static final int CLICK_ZOOM = 2;	
	public static final int CLICK_ACTION = 3;	
	public static final int CLICK_BAG = 4;
	public static final int CLICK_TALK = 5;
	public static final int CLICK_INFO = 6;
	public static final int CLICK_AGAIN_INVENTORY = 7;
	public static final int CLICK_DOLL = 8;
	
	public static final int MOVE_LEFT_ARROW_POSX = 10;
	public static final int MOVE_RIGHT_ARROW_POSX = -45; // from right (to remove from screen width)
	public static final int MOVE_ARROWS_POSY = -80; // from screen bottom (to remove from screen height)

	/*                  */
	/* INVENTORY SCREEN */
	/*                  */
	public static final int INVENTORY_BAG_POSX = 20;
	public static final int INVENTORY_BAG_POSY = 20;

	public static final int INVENTORY_POSX_NORMALVIEW = 0;
	public static final int INVENTORY_POSY_NORMALVIEW = 320-85;
	public static final int POSX_ZOOMVIEW = 2;
	public static final int POSY_ZOOMVIEW = 46;
	public static final int INVENTORY_POSX_ZOOMVIEW = 2;
	public static final int INVENTORY_POSY_ZOOMVIEW = 6;
	public static final int INVENTORY_POSX_ZOOM_ITEM = 238;
	public static final int INVENTORY_POSY_ZOOM_ITEM = 94;
	public static final int INVENTORY_POSX_ZOOM_TEXT = 30;
	public static final int INVENTORY_POSY_ZOOM_TEXT = 190;
	public static final int INVENTORY_ZOOM_SIZE_TEXT = 40;
	
	public static final int INVENTORY_SIZE_BOXES = 70;
	public static final int INVENTORY_SIZE_BETWEEN_BOXES = 2;
	
	public static final String INVENTORY_IMAGE_PREFIX = "_inv";
	//public static final String INVENTORY_IMAGE_PREFIX_BIG = "big_";
	public static final String INVENTORY_IMAGE_PREFIX_ZOOM = "_zoom";

	public static final int INVENTORY_ADD_ITEM = 1;
	public static final int INVENTORY_REMOVE_ITEM = 0;
	
	/*  */
	/* be careful: if you change the following sizes, don't forget to increase
	/* BTAs size in method loadItems of Inventory class
	/*  */
	public static final int INVENTORY_MAX_SIZE_ITEM = 70;
	public static final int INVENTORY_MAX_SIZE_BIG = 100;
	public static final int INVENTORY_MAX_SIZE_ZOOM = 200;
	
	/*                  */
	/* TALK SCREEN      */
	/*                  */
//	public static final int TALKSCREEN_POSX = 4;
//	public static final int TALKSCREEN_POSY = 4;
//	
//	public static final int TALKSCREEN_DOLL_TEXT_POSX = 85;
//	public static final int TALKSCREEN_DOLL_TEXT_POSY = 180;
//	public static final int TALKSCREEN_BUBBLE_CLEAR_HEIGHT = 10;
//	public static final int TALKSCREEN_BUBBLE_HOOK_HEIGHT = 19;  //11
//	public static final int TALKSCREEN_BUBBLE_MIDDLE_HEIGHT = 18;
//	
//	public static final int TALKSCREEN_CHAR_TEXT_POSX = 120;
//	public static final int TALKSCREEN_CHAR_TEXT_POSY = 290;
//	
//	public static final int TALKSCREEN_DOLL_TALK = 0;
//	public static final int TALKSCREEN_CHAR_TALK = 1;
//	
//	public static final int TALKSCREEN_LENGTH_TEXT_DOLL = 32;	
//	public static final int TALKSCREEN_LENGTH_TEXT_CHAR = 22;	
//	
//	public static final int CHARS_MAX_SIZE = 150;
//	public static final String CHAR_IMAGE_PREFIX = "char_";
//
//	/*                     */
//	/* INFORMATION FRAME   */
//	/*                     */
//	public static final int INFORMATION_POSX = 0;
//	public static final int INFORMATION_POSY = 0;
//	public static final int INFORMATION_TEXT_MAX_SIZE = 32;
//	public static final int INFORMATION_TEXT_POSX = 85;
//	public static final int INFORMATION_TEXT_POSY = 6;
//	public static final int INFORMATION_TEXT_HEIGHT = 20;
//	public static final int INFORMATION_TEXT_WIDTH = 300;
//	public static final int INFORMATION_MAX_LINE = 5;
//	public static final float INFORMATION_AVERAGE_LETTER_WIDTH = 8.7f;
//	public static final int INFORMATION_CLOSING_HEIGHT = 140;
//	
//	public static final int OBJECT_TYPE_ITEM = 1;
//	public static final int OBJECT_TYPE_AREA = 2;
//	public static final int OBJECT_TYPE_ANIM = 3;
//	public static final int OBJECT_TYPE_CHAR = 4;
//	
//	/*                     */
//	/*         MAP         */
//	/*                     */
//	public static final int MAP_MAX_SIZE_ITEM = 150;
//	
//	/*                     */
//	/* DATABASE            */
//	/*                     */
//	
//	// ---- BE CAREFUL: These constants have to matched the database ---- 
	public static final String DB_TABLE_ITEM = "item";
	public static final String DB_TABLE_ANIMATION = "animation";
	public static final String DB_TABLE_MOVE_DOLL = "move_doll";
	public static final String DB_TABLE_MODIFIER = "modifier";
	public static final String DB_TABLE_TEXT_ANIM = "text_animation";
	public static final String DB_TABLE_COMBINATION = "combination";
	public static final String DB_TABLE_TRIGGER = "trigger";
	public static final String DB_TABLE_SCREEN_ITEM = "screen_item";
	public static final String DB_TABLE_SCREEN_AREA = "screen_area";
	public static final String DB_TABLE_CHARACTER = "character";
	public static final String DB_TABLE_QUESTION = "text_question";
	public static final String DB_TABLE_EXIT = "exit";
//	
	public static final String DB_FIELD_DISPLAY = "display";
	public static final String DB_FIELD_STATE = "state";
	public static final String DB_FIELD_DESC_STATE = "desc_state";
	public static final String DB_FIELD_TAKE_STATE = "take_state";
	public static final String DB_FIELD_LOOK_STATE = "look_state";
	public static final String DB_FIELD_TALK_STATE = "talk_state";
	public static final String DB_FIELD_EXIT = "exit";
	public static final String DB_FIELD_TAKEABLE = "takeable";
	public static final String DB_FIELD_TRIGGER = "after_trigger_id";

	public static final int DB_INVENTORY_VALUE_ON_SCREEN = 1;
	public static final int DB_INVENTORY_VALUE_IN = 2;
	public static final int DB_INVENTORY_VALUE_OUT = 0;
	
	public static final int DB_COMBINATION_VALUE_ON_SCREEN = 1;
	public static final int DB_COMBINATION_VALUE_IN_INVENTORY = 2;
//
//	public static final int DB_DESCRIPTION_ACTION_TAKE = 1;
//	public static final int DB_DESCRIPTION_ACTION_LOOK = 2;
//	public static final int DB_DESCRIPTION_ACTION_TALK = 3;
//	public static final int DB_DESCRIPTION_ACTION_EXIT = 4;
//	public static final int DB_DESCRIPTION_ACTION_AREA_LOOK = 5;
//	public static final int DB_DESCRIPTION_ANIMATION = 6;
//	public static final int DB_DESCRIPTION_COMBINATION = 7;
//	public static final int DB_DESCRIPTION_ACTION_CHAR_LOOK = 8;
//	
	public static final int DB_TRIGGER_TYPE_ANIM = 1;
	public static final int DB_TRIGGER_TYPE_ACTION = 2;
	public static final int DB_TRIGGER_TYPE_DOLL = 3;
	public static final int DB_TRIGGER_TYPE_SV_ITEM = 4; // Switch Visibility
	public static final int DB_TRIGGER_TYPE_TEXT = 5;
	public static final int DB_TRIGGER_TYPE_SV_ANIM = 6; // Switch Visibility
	
	public static final int DB_MODIFIER_TYPE_COMB = 1;
	public static final int DB_MODIFIER_TYPE_POS = 2;
	public static final int DB_MODIFIER_TYPE_TAKE = 3;
	public static final int DB_MODIFIER_TYPE_LOOK = 4;
	public static final int DB_MODIFIER_TYPE_TALK = 5;
	public static final int DB_MODIFIER_TYPE_EXIT = 6;
	public static final int DB_MODIFIER_TYPE_TAKEABLE = 7;
	public static final int DB_MODIFIER_TYPE_SWITCH_COMB = 8;
	public static final int DB_MODIFIER_TYPE_AREA_LOOK = 9;
	public static final int DB_MODIFIER_TYPE_CHAR_LOOK = 10;
	public static final int DB_MODIFIER_TYPE_QUESTION_STATE = 11;
	public static final int DB_MODIFIER_TYPE_SCREEN_TRIGGER = 12;
	
	/*                     */
	/*      ZINDEXES       */
	/*                     */
	public static final int ZINDEX_GROUND_0 = 0;
	public static final int ZINDEX_ITEM = 10;			
	public static final int ZINDEX_ANIM = 60;			
	public static final int ZINDEX_DOLL = 90;			
	public static final int ZINDEX_GROUND_1 = 100;
	public static final int ZINDEX_FOREGROUND = 101;			
	public static final int ZINDEX_ACTION = 102;			
	public static final int ZINDEX_ARROW = 110;			
	public static final int ZINDEX_CIRCLE = 120;					
	public static final int ZINDEX_INVENTORY = 150;
	public static final int ZINDEX_INV_ITEM = 170;			
	public static final int ZINDEX_INV_ITEM_IN_USE = 230;	
	public static final int ZINDEX_INFO_FRAME = 270;
	public static final int ZINDEX_SETTINGS = 300;			
	
	private Constants(){
		throw new AssertionError();
	}	  
}
