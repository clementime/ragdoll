package eu.clementime.rds;

import static eu.clementime.rds.Constants.ANIMATION_IMAGE_PREFIX;
import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_0;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_1;
import static eu.clementime.rds.Constants.ZINDEX_FOREGROUND;
import static eu.clementime.rds.Constants.ZINDEX_ITEM;
import static eu.clementime.rds.Constants.ZINDEX_ANIM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;

public class Background {
	
	private final int CAMERA_WIDTH;
	private final int CAMERA_HEIGHT;
	
	public ArrayList<ScreenItem> items = new ArrayList<ScreenItem>();
//	public ArrayList<Area> areas = new ArrayList<Area>();
	public ArrayList<Anim> anims = new ArrayList<Anim>();
//	public ArrayList<Sprite> chars = new ArrayList<Sprite>();
//	public ArrayList<Exit> exits = new ArrayList<Exit>();

	public Sprite bgImage;
	public Sprite fgImage;
	public int xMin;
	public int xMax;
		
	private DatabaseAccess db;
	
	private Context context;
	public int screenId;
	public String screenPrefix = "";
	
	public BitmapTextureAtlas bgBTA;
	public BitmapTextureAtlas itemsBTA;
	public BitmapTextureAtlas animsBTA;
	public BitmapTextureAtlas charsBTA;
	
	public Background(DatabaseHandler dbh, Context context, int screenId, int cw, int ch) {

		this.db = new DatabaseAccess(dbh);
		this.context = context;
		this.screenId = screenId;

		this.screenPrefix = db.selectScreenPrefix(screenId);
		
		this.CAMERA_WIDTH = cw;
		this.CAMERA_HEIGHT = ch;
	}
	
	public void loadBackground(Engine engine, Scene scene, int MARGIN) {
		
		try {			
			// -height- x -max scale- x -background & foreground- => 320 x 1.5 x 2 =  960px 
			// -width-  x -max scale- x -max size screen-         => 1360 x 1.5    = 2040px 
			bgBTA = new BitmapTextureAtlas(2048, 1024, TextureOptions.DEFAULT);
		
			Map<String, String> hm = db.selectBackground(this.screenId);
			
			int bgFile = context.getResources().getIdentifier(this.screenPrefix + hm.get("background"), "drawable", context.getPackageName());
			if (bgFile == 0) {
				bgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
				Log.w("Clementime", "Background/loadBackground(): cannot find background screen " + screenId + " " + hm.get("background"));
			} else Log.d("Clementime", "Background/loadBackground(): load background " + this.screenPrefix + hm.get("background"));
			
			// load foreground only if available
			if (hm.get("foreground") != "" && hm.get("foreground") != null) {	
				int fgFile = context.getResources().getIdentifier(this.screenPrefix + hm.get("foreground"), "drawable", context.getPackageName());
				if (fgFile == 0) {
					fgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
					Log.w("Clementime", "Background/loadBackground(): cannot find foreground screen " + screenId + " " + hm.get("foreground"));
				} else Log.d("Clementime", "Background/loadBackground(): load foreground " + this.screenPrefix + hm.get("foreground"));

				TextureRegion TRFore = BitmapTextureAtlasTextureRegionFactory.createFromResource(bgBTA, context, fgFile, 0, 480);
				fgImage = new Sprite(0, 0, TRFore);
				
				scene.attachChild(fgImage);
				fgImage.setZIndex(ZINDEX_GROUND_1);
			}
			
			TextureRegion TRBack = BitmapTextureAtlasTextureRegionFactory.createFromResource(bgBTA, context, bgFile, 0, 0);
			
			bgImage = new Sprite(0, 0 + MARGIN, TRBack);
		
			xMin = Integer.parseInt(hm.get("x_min"));
			xMax = Integer.parseInt(hm.get("x_max"));
			
			engine.getTextureManager().loadTexture(bgBTA);
			
			scene.attachChild(bgImage);		
			bgImage.setZIndex(ZINDEX_GROUND_0);
			
		} catch (Exception e) {
			Log.e("Clementime", "Background/loadBackground():failed to load screen " + screenId);			
		}

	}
	
	public void loadItems(Engine engine, Scene scene, int MARGIN) {
		
		LinkedList<Map<String, String>> ll = db.selectscreenItems(this.screenId);
		
		// avoid errors if BTAs are empty
		if (!ll.isEmpty()) {
			
			ScreenItem spriteToAttach;
			
			int posOnAtlasX = 0;
			int posOnAtlasY = 0;
			int nextPosOnAtlasX = 0;
			int nextPosOnAtlasY = 0;
			
			itemsBTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
				
			ListIterator<Map<String, String>> it = ll.listIterator();
			
			Map<String, String> hm = new HashMap<String, String>();

			try {
	
				while (it.hasNext()) {
					
					hm = it.next();
					
					Log.d("Clementime", "Background/loadItems(): load item " + hm.get("image"));
	
					// manage position on AtlasBitmap
					nextPosOnAtlasX = posOnAtlasX + Integer.parseInt(hm.get("width"));
					if (Integer.parseInt(hm.get("height")) > nextPosOnAtlasY) nextPosOnAtlasY = Integer.parseInt(hm.get("height"));
					if (nextPosOnAtlasX >= 512) {
						posOnAtlasX = 0;
						posOnAtlasY = posOnAtlasY + nextPosOnAtlasY;
						nextPosOnAtlasY = 0;
					}
					
					createItem(hm, itemsBTA, posOnAtlasX, posOnAtlasY, MARGIN);
		
					// manage position on AtlasBitmap
					posOnAtlasX = posOnAtlasX + Integer.parseInt(hm.get("width"));
				}
				
			} catch (Exception e) {
				Log.e("Clementime", "Background/loadItems(): failed to load items - " + e.getMessage());
			}
			
			engine.getTextureManager().loadTexture(itemsBTA);
			
			ListIterator<ScreenItem> itItems = items.listIterator();
						
			while(itItems.hasNext()){
				spriteToAttach = itItems.next();
				scene.attachChild(spriteToAttach);
				
				Log.d("Clementime", "Background/loadItems(): *** display item " + spriteToAttach.id + " ***");
				
				if (spriteToAttach.foreground) spriteToAttach.setZIndex(ZINDEX_FOREGROUND);
				else spriteToAttach.setZIndex(ZINDEX_ITEM);
				scene.registerTouchArea(spriteToAttach);
				spriteToAttach.andEngineId = scene.getChildIndex(spriteToAttach);
			}
			
		} else Log.d("Clementime", "Background/loadItems(): ***screen " + screenId + " has no item***");
	}
		
	private void createItem(Map<String, String> hm, BitmapTextureAtlas BTA, int xPos, int yPos, int MARGIN) {

		int res = 0;
		
		int id;
		float x;
		float y;
		int take;
		int look;
		int talk;
		boolean takeable;
		boolean foreground;
		
		// retrieve image
		String file = hm.get(("image"));
		res = context.getResources().getIdentifier(file, "drawable", context.getPackageName());
		if (res == 0) {
			res = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
		}
		
		TextureRegion TR = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, res, xPos, yPos);

		// create item from database information	
		id = Integer.parseInt(hm.get("id"));
		x = Float.valueOf(hm.get("x"));
		y = Float.valueOf(hm.get("y"));

		take = Integer.parseInt(hm.get("take_state"));
		look = Integer.parseInt(hm.get("look_state"));
		talk = Integer.parseInt(hm.get("talk_state"));
		//exit = Integer.parseInt(hm.get("exit"));
		if (Integer.parseInt(hm.get("foreground")) == 1) takeable = true;
		else takeable = false;
		if (Integer.parseInt(hm.get("foreground")) == 1) foreground = true;
		else foreground = false;

		items.add(new ScreenItem(id, x, y + MARGIN, take, look, talk, takeable, foreground, TR));
		
		Log.d("Clementime", "Background/createItem(): create item " + file + " -id: " + id);
	}
	
	public void loadAnimations(Engine engine, Scene scene, int MARGIN) {
		
		LinkedList<Map<String, String>> ll = db.selectAnimations(this.screenId);
		
		if (!ll.isEmpty()) {
			
			Anim spriteToAttach;
				
			int posOnAtlasX = 0;
			int posOnAtlasY = 0;
			int nextPosOnAtlasY = 0;
			int nextPosOnAtlasX = 0;
			
			animsBTA = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			
			ListIterator<Map<String, String>> it = ll.listIterator();
			
			Map<String, String> hm = new HashMap<String, String>();
			
			try {

				while (it.hasNext()) {
					
					hm = it.next();
							
					Log.d("Clementime", "Background/loadAnimations(): load animation " +  Integer.parseInt(hm.get("id")));
	
					// manage position on AtlasBitmap
					nextPosOnAtlasX = posOnAtlasX + Integer.parseInt(hm.get("width"));
					if (Integer.parseInt(hm.get("height")) > nextPosOnAtlasY) nextPosOnAtlasY = Integer.parseInt(hm.get("height"));
					if (nextPosOnAtlasX >= 1024) {
						posOnAtlasX = 0;
						posOnAtlasY = posOnAtlasY + nextPosOnAtlasY;
						nextPosOnAtlasY = 0; 	
					}
	
					createAnimation(hm, animsBTA, posOnAtlasX, posOnAtlasY, MARGIN);
					
					// manage position on AtlasBitmap
					posOnAtlasX = posOnAtlasX + Integer.parseInt(hm.get("width"));
				}
				
				engine.getTextureManager().loadTexture(animsBTA);
				
				ListIterator<Anim> itAnims = anims.listIterator();
							
				while(itAnims.hasNext()){
					spriteToAttach = itAnims.next();
					scene.attachChild(spriteToAttach);
					
					Log.d("Clementime", "Background/loadItems(): *** display anim " + spriteToAttach.id + " ***");
					
					spriteToAttach.setZIndex(ZINDEX_ANIM);
					scene.registerTouchArea(spriteToAttach);
					spriteToAttach.andEngineId = scene.getChildIndex(spriteToAttach);
				}
				
			} catch (Exception e) {
				Log.w("Clementime", "Background/loadAnimations(): failed to load animations - " + e.getMessage());
			}
		}
	}
	
	private void createAnimation(Map<String, String> hm, BitmapTextureAtlas BTA, int xPos, int yPos, int MARGIN) {

		int res = 0;
		
		int id;

		int rows = 0;
		int columns = 0;
		
		int stopFrame;

		float x;
		float y;
		float moveToX;
		float moveToY;

		boolean toChase = false;
		
		int width = 0;

		// retrieve image
		String file = this.screenPrefix + ANIMATION_IMAGE_PREFIX + hm.get("image");
		res = context.getResources().getIdentifier(file, "drawable", context.getPackageName());
		if (res == 0) {
			res = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
		}
		
		rows = Integer.parseInt(hm.get("rows"));
		columns = Integer.parseInt(hm.get("columns"));
		width = Integer.parseInt(hm.get("width"))/Integer.parseInt(hm.get("columns"));
		
		TiledTextureRegion TR = BitmapTextureAtlasTextureRegionFactory.createTiledFromResource(BTA, context, res,  xPos, yPos, columns, rows);

		// create item from database information	
		id = Integer.parseInt(hm.get("id"));

		stopFrame = Integer.parseInt(hm.get("stop_frame"));
		
		x = Float.valueOf(hm.get("x"));
		y = Float.valueOf(hm.get("y"));
		moveToX = Float.valueOf(hm.get("move_to_x"));
		moveToY = Float.valueOf(hm.get("move_to_y"));

		if (Integer.parseInt(hm.get("to_chase")) == 1) toChase = true;
		
//		anims.add(new Animation(id, frameDuration, firstFrame, lastFrame, stopFrame, loop, width, x, y, moveToX, xVelocity, yVelocity, dollIsHidden, toChase, triggerId, TR));
		anims.add(new Anim(id, width, x, y, stopFrame, moveToX, moveToY, toChase, TR));
		Log.d("Clementime", "Background/createAnimation(): create animation " + id);

	}
	
//	public BitmapTextureAtlas loadItem(int itemId) {
//		
//		BitmapTextureAtlas BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//
//		//String locale = context.getResources().getConfiguration().locale.getLanguage(); //iso2 code
//		
//		String query = " select _id as id, image, height, width, desc_" + this.language + " as desc, " + DB_FIELD_DISPLAY + ", ";
//		query += " x, y, take_state, look_state, talk_state, exit, takeable ";	// select field
//		query += " from item left join screen_item on _id = item_id ";
//		query += " where _id = " + itemId;	// conditions
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//
//			c.moveToNext();
//				
//			createItem(c, BTA, 0, 0);	
//			
//			c.close();
//			
//		} catch (Exception e) {
//			Log.w("Clementime", "Background/loadItem(): failed to load item " + itemId + " - " + e.getMessage());
//		}
//		
//		return BTA;
//	}
//
//	public void loadAreas() {
//
//		int id;
//		int x;
//		int y;
//		int width;
//		int height;
//		//int look;
//		//int exit;
//		//String desc;
//
//		//String locale = context.getResources().getConfiguration().locale.getLanguage(); //iso2 code
//		
//		String query = " select _id as id, x, y, height, width ";
//		query += " from screen_area ";
//		query += " where screen_id = " + screenId + " order by id";	// conditions
////		query += " where screen_id = " + screenId + " and look_state = state ";	// conditions
//		
//		Log.d("Clementime", "Background/loadAreas(): ***load areas*** ");	
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//
//			while (c.moveToNext()) {
//				
//				id = Integer.parseInt(hm.get("id"));
//				x = Integer.parseInt(hm.get("x"));
//				y = Integer.parseInt(hm.get("y"));
//				width = Integer.parseInt(hm.get("width"));
//				height = Integer.parseInt(hm.get("height"));
//				
//				//exit = Integer.parseInt(hm.get("exit"));		
//				//look = Integer.parseInt(hm.get("look"));
//				//desc = hm.get("desc"));
//	
//				Log.d("Clementime", "Background/loadAreas(): load area " + id + " xMin: " + x + "-yMin: " + y + "-xMax: " + (x + width) + "-yMax: " + (y + height));
//				//areas.add(new ScreenArea(id, x, y, width, height, desc, look, exit));
//				areas.add(new Area(id, x, y, width, height));
//			}
//			
//			c.close();
//			
//		} catch (Exception e) {
//			Log.w("Clementime", "Background/loadAreas(): failed to load areas - " + e.getMessage());
//		}
//	}
//	
//	public void loadExits(TiledTextureRegion TRLeft, TiledTextureRegion TRRight) {
//
//		String query = " select _id as id, x, y, direction, display, starting_x, starting_y, to_screen_id, before_trigger_id, after_trigger_id ";
//		query += " from exit ";
//		query += " where screen_id = " + screenId + " order by id";	// conditions
//		
//		Log.i("Clementime", "Background/loadExits(): ***load exits*** ");	
//
//		int id;
//		int direction;
//		int x;
//		int y;
//		int display;
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//
//			while (c.moveToNext()) {
//				
//				id = Integer.parseInt(hm.get("id"));
//				direction = Integer.parseInt(hm.get("direction"));
//				x = Integer.parseInt(hm.get("x"));
//				y = Integer.parseInt(hm.get("y"));
//				display = Integer.parseInt(hm.get("display"));
//				
//				Log.v("Clementime", "Background/loadExits(): load exit " + id);
//				
//				if (Integer.parseInt(hm.get("direction")) == DIRECTION_LEFT) {
//					Exit exit = new Exit(id, direction, x, y, display, TRLeft);
//					
//					exit.beforeTrigger = Integer.parseInt(hm.get("before_trigger_id"));
//					exit.afterTrigger = Integer.parseInt(hm.get("after_trigger_id"));
//					exit.startingX = Integer.parseInt(hm.get("starting_x"));
//					exit.startingY = Integer.parseInt(hm.get("starting_y"));
//					exit.toScreen = Integer.parseInt(hm.get("to_screen_id"));
//
//					exits.add(exit);
//				} else if (Integer.parseInt(hm.get("direction")) == DIRECTION_RIGHT) {
//					Exit exit = new Exit(id, direction, x, y, display, TRRight);
//					
//					exit.beforeTrigger = Integer.parseInt(hm.get("before_trigger_id"));
//					exit.afterTrigger = Integer.parseInt(hm.get("after_trigger_id"));
//					exit.startingX = Integer.parseInt(hm.get("starting_x"));
//					exit.startingY = Integer.parseInt(hm.get("starting_y"));
//					exit.toScreen = Integer.parseInt(hm.get("to_screen_id"));
//
//					exits.add(exit);
//				}			
//			}
//			
//			c.close();
//			
//		} catch (Exception e) {
//			Log.w("Clementime", "Background/loadExits(): failed to load exits - " + e.getMessage());
//		}
//	}
//	
//	public boolean hasNoCharacter() {
//		
//		String query = " select c._id ";
//		query += " from character c left join playing_animation p on c.playing_anim_id = p._id ";
//		query += " left join animation a on p.anim_id = a._id ";
//		query += " where a.screen_id = " + screenId + " and " + DB_FIELD_DISPLAY + " = 1";	// conditions
//		
//		boolean empty = true;
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//
//			if (c.getCount() != 0) empty = false;
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return empty;
//	}
//	
//	public void loadChars() {
//		
//		Log.i("Clementime", "Background/loadChars()");
//		
//		int posX = 0;
//		int posY = 0;
//		int j = 1;
//		
//		charsBTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
//		TextureRegion TR;
//		
//		String query = " select distinct c._id as id, c.image as image ";
//		query += " from character c left join playing_animation p on c.playing_anim_id = p._id ";
//		query += " left join animation a on p.anim_id = a._id ";
//		query += " where a.screen_id = " + screenId + " and " + DB_FIELD_DISPLAY + " = 1";	// conditions	
//
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//	
//			while (c.moveToNext()) {
//				// retrieve images
//				String file = screenPrefix + CHAR_IMAGE_PREFIX + hm.get("image"));
//			
//				int res = context.getResources().getIdentifier(file, "drawable", context.getPackageName());
//				if (res == 0) {
//					res = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
//					Log.i("Clementime", "Background/loadChars() unable to find file " + file );
//				} else Log.i("Clementime", "Background/loadChars() load file " + file );
//				
//				TR = BitmapTextureAtlasTextureRegionFactory.createFromResource(charsBTA, context, res, posX, posY);
//
//				Sprite sprite = new Sprite(0, 0, TR);
//				sprite.setUserData(Integer.parseInt(hm.get("id")));
//				chars.add(sprite);
//
//				posX = posX + CHARS_MAX_SIZE;
//				
//				j++;
//				
//				if (j >= 4) {
//
//					j = 1;
//					posX = 0;
//					
//					posY = posY + CHARS_MAX_SIZE;
//				}
//
//			}
//			
//			c.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public ArrayList<Integer> showAnims() {
//		
//		ArrayList<Integer> displayedChars = new ArrayList<Integer>();
//		
//		String query = " select p._id as id ";
//		query += " from playing_animation p left join animation a on a._id = anim_id ";
//		query += " where screen_id = " + this.screenId;	// conditions
//		query += " and " + DB_FIELD_DISPLAY + " = 1 ";
//		
//		try {
//			Cursor c = dbh.db.rawQuery(query, new String [] {});
//
//			while (c.moveToNext()) displayedChars.add(Integer.parseInt(hm.get("id")));			
//			
//			c.close();
//			
//		} catch (Exception e) {
//			Log.w("Clementime", "Background/showAnims(): failed to load playing animations - " + e.getMessage());
//		}
//		
//		return displayedChars;
//	}
	
	public void hideShowItem(Scene scene, ScreenItem item) {
		
		Log.i("Clementime", "Background/hideShowItem()");	

		if (item.isVisible()) {
			item.setVisible(false);
			scene.unregisterTouchArea(item);					
		} else {
			item.setVisible(true);
			scene.registerTouchArea(item);				
		}
	}
}

