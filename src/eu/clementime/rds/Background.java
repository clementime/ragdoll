package eu.clementime.rds;

import static eu.clementime.rds.Constants.ANIMATION_IMAGE_PREFIX;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_ON_SCREEN;
import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SCALE;
import static eu.clementime.rds.Constants.ZINDEX_ACTION;
import static eu.clementime.rds.Constants.ZINDEX_ANIM;
import static eu.clementime.rds.Constants.ZINDEX_FOREGROUND;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_0;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_1;
import static eu.clementime.rds.Constants.ZINDEX_ITEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;

public class Background {
	
	public ArrayList<ScreenItem> items = new ArrayList<ScreenItem>();
	public ArrayList<Area> areas = new ArrayList<Area>();
	public ArrayList<Anim> anims = new ArrayList<Anim>();
//	public ArrayList<Sprite> chars = new ArrayList<Sprite>();
	public ArrayList<Exit> exits = new ArrayList<Exit>();

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
	
	public Background(DatabaseHandler dbh, Context context, int screenId, Engine engine, Scene scene, TiledTextureRegion elTR, TiledTextureRegion erTR) {

		this.db = new DatabaseAccess(dbh);
		this.context = context;
		this.screenId = screenId;

		this.screenPrefix = db.selectScreenPrefix(screenId);
		
		// load images & data
		load(engine, scene);
		loadItems(engine, scene);
		loadAnimations(engine, scene);
		createAreas();
		createExits(elTR, erTR);
		
//		nextBg.loadChars();
//		talk.chars = nextBg.chars;
	}
	
	/**************************************/
	/* LOAD NEW SCREEN                    */
	/**************************************/
	
	public void load(Engine engine, Scene scene) {
		
		try {			
			// -height- x -max scale- x -background & foreground- => 320 x 1.5 x 2 =  960px 
			// -width-  x -max scale- x -max size screen-         => 1360 x 1.5    = 2040px 
			bgBTA = new BitmapTextureAtlas(4096, 1024, TextureOptions.DEFAULT);
		
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
				fgImage = new Sprite(0, 0 + MARGIN_Y, TRFore);
				
				scene.attachChild(fgImage);
				fgImage.setZIndex(ZINDEX_GROUND_1);
			}
			
			TextureRegion TRBack = BitmapTextureAtlasTextureRegionFactory.createFromResource(bgBTA, context, bgFile, 0, 0);
			
			bgImage = new Sprite(0, 0 + MARGIN_Y, TRBack);
			bgImage.setScaleCenter(0,0);
			bgImage.setScale(SCALE);
			//bgImage.setPosition(0,0);
		
			xMin = Integer.parseInt(hm.get("x_min"));
			xMax = Integer.parseInt(hm.get("x_max"));
			
			engine.getTextureManager().loadTexture(bgBTA);
			
			scene.attachChild(bgImage);		
			bgImage.setZIndex(ZINDEX_GROUND_0);
			//bgImage.setVisible(false); // used to test if a zindex is not correctly set
			
		} catch (Exception e) {
			Log.e("Clementime", "Background/loadBackground():failed to load screen " + screenId);			
		}
	}
	
	public void loadItems(Engine engine, Scene scene) {
		
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
					
					createItem(hm, itemsBTA, posOnAtlasX, posOnAtlasY);
		
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
		
	private void createItem(Map<String, String> hm, BitmapTextureAtlas BTA, int xPos, int yPos) {

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

		items.add(new ScreenItem(id, x, y + MARGIN_Y, take, look, talk, takeable, foreground, TR));
		
		Log.d("Clementime", "Background/createItem(): create item " + file + " -id: " + id);
	}
	
	public void loadAnimations(Engine engine, Scene scene) {
		
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
	
					createAnimation(hm, animsBTA, posOnAtlasX, posOnAtlasY);
					
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
	
	private void createAnimation(Map<String, String> hm, BitmapTextureAtlas BTA, int xPos, int yPos) {

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
		anims.add(new Anim(id, width, x, y + MARGIN_Y, stopFrame, moveToX, moveToY, toChase, TR));
		Log.d("Clementime", "Background/createAnimation(): create animation " + id);

	}
	
	public void loadItem(int itemId, Engine engine) {
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		Map<String, String> hm = db.selectItem(itemId);
				
		createItem(hm, BTA, 0, 0);	
		
		engine.getTextureManager().loadTexture(BTA);
	}

	public void createAreas() {
		areas = db.selectAreas(screenId, MARGIN_Y);
	}
	
	public void createExits(TiledTextureRegion TRLeft, TiledTextureRegion TRRight) {
		exits = db.selectExits(screenId, TRLeft, TRRight, MARGIN_Y);
	}

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
	
	public void showStaticAnims(Scene scene) {
		
		Log.i("Clementime", "Screen/showStaticAnims()");
		
		ArrayList<Integer> displayedAnims = db.selectDisplayedAnims(screenId);
		ListIterator<Integer> itAnims = displayedAnims.listIterator();
		
		while(itAnims.hasNext()) launchStaticAnim(itAnims.next(), scene);	
	}
	
	private void launchStaticAnim(int animId, Scene scene) {
		
		//****************************************************************
		// features[0] = first_frame
		// features[1] = last_frame
		// features[2] = frame_duration
		//****************************************************************
		int[] animFeatures = db.selectStaticAnimFeatures(animId);
		
		Log.i("Clementime","Screen/launchStaticAnim(): launch static animation " + animId);
		
		ListIterator<Anim> it = this.anims.listIterator();
		
		while(it.hasNext()){
			Anim animation = it.next();
			
			if (animId == animation.id) {

				// to use animate function with first/last frame, you have to initialise a long[] sized by the number of frames 
				long[] frameDuration = new long[animFeatures[1]-animFeatures[0]+1];
				for (int i = 0; i <= animFeatures[1] - animFeatures[0]; i++) frameDuration[i] = animFeatures[2];
				
				animation.animate(frameDuration, animFeatures[0], animFeatures[1], true);

				if (animation.moveToX != 0 || animation.moveToY != 0) animation.setPosition(animation.moveToX, animation.moveToY);					

				animation.setVisible(true);
				scene.registerTouchArea(animation);
			}
		}
	}
	
	public void showExits(Scene scene) {
		
		Log.i("Clementime", "Screen/showExits()");

		Exit exit;
		
		ListIterator<Exit> itExits = this.exits.listIterator();
		
		while(itExits.hasNext()) {
			exit = itExits.next();
			
			scene.attachChild(exit);
			exit.setZIndex(ZINDEX_ACTION);
			
			if (exit.display == 1) {
				exit.setVisible(true);
				exit.animate(150, true);
				scene.registerTouchArea(exit);
				Log.v("Clementime", "Screen/showExits(): exit " + exit.id + " on display");
			} else Log.v("Clementime", "Screen/showExits(): exit " + exit.id + " hidden");
					
		}
	}
	
	/**************************************/
	/* DURING RUNTIME                     */
	/**************************************/
	
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
	
	public void hideShowItemById(int itemId, Scene scene) {
		
		Log.i("Clementime", "Screen/hideShowScreenItem()");
		
		ListIterator<ScreenItem> it = items.listIterator();
		
		while(it.hasNext()){
			ScreenItem item = it.next();
			
			if (item.id == itemId) {
				if (item.isVisible()) {
					item.setVisible(false);
					scene.unregisterTouchArea(item);					
				} else {
					item.setVisible(true);
					scene.registerTouchArea(item);				
				}
			}
		}
	}

	public void hideShowAnimation(int animId) {
		
		Log.i("Clementime", "Screen/hideShowAnimation()");
		
		ListIterator<Anim> it = anims.listIterator();
		
		while(it.hasNext()){
			Anim anim = it.next();
			
			if (anim.id == animId) {
				if (anim.isVisible()) anim.setVisible(false);
				else anim.setVisible(true);

			}
		}
	}

	public int[] getAnimStates(int animId) {	
		return db.selectAnimStates(animId);
	}
	
	public int[] getItemStates(int itemId) {	
		return db.selectItemStates(itemId);
	}
	
	public int[] getAreaStates(int areaId) {	
		return db.selectAreaStates(areaId);
	}

	public Area checkAreas(float x, float y) {
		
		Log.i("Clementime", "Screen/checkAreas()");
		
		Area touchedArea = null;
		
		ListIterator<Area> it = this.areas.listIterator();
		
		while(it.hasNext()){
			Area area = it.next();
					
			//Log.d("Clementime", "Screen/checkAreas(): x: " + x + "-y: " + y + "-area xMin: " + area.x + "-area yMin: " + area.y + "-area xMax: " + (area.x + area.width) + "-area yMax: " + (area.y + area.height));
			
			if (x >= area.x && x <= (area.x + area.width) && y >= area.y && y <= (area.y + area.height)) touchedArea = area;		
		}
		
		return touchedArea;
	}
	
	public ScreenItem addItemOnScreen(int itemId, Engine engine, Scene scene) {
		
		Log.i("Clementime", "Screen/addItemOnScreen()");

		int newValue = DB_INVENTORY_VALUE_ON_SCREEN;
		String where = " _id = " + itemId;
		db.updateInventoryField(where, newValue);

		loadItem(itemId, engine);

		ScreenItem item = items.get(items.size() - 1);  

		scene.attachChild(item);
		
		return item;
	}
}

