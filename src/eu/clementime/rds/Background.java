package eu.clementime.rds;

import static eu.clementime.rds.Constants.ANIMATION_IMAGE_PREFIX;
import static eu.clementime.rds.Constants.CAMERA_HEIGHT;
import static eu.clementime.rds.Constants.CAMERA_WIDTH;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_ON_SCREEN;
import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SCALE_POSITION;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;
import static eu.clementime.rds.Constants.ZINDEX_ACTION;
import static eu.clementime.rds.Constants.ZINDEX_ANIM;
import static eu.clementime.rds.Constants.ZINDEX_FOREGROUND;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_0;
import static eu.clementime.rds.Constants.ZINDEX_GROUND_1;
import static eu.clementime.rds.Constants.ZINDEX_ITEM;
import static eu.clementime.rds.Constants.LOG_ON;

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

/**
* Background class manages the whole background: background & foreground images but also screen items, animations and areas the doll is confronted with.
* @author Cl&eacute;ment
* @version 1.0
*/public class Background {
	
	/**
	 * Even if attached to scene, items are part of the background, and stored inside it (idem for areas, anims and exists).
	 */	
	public ArrayList<ScreenItem> items = new ArrayList<ScreenItem>();
	public ArrayList<Area> areas = new ArrayList<Area>();
	public ArrayList<Anim> anims = new ArrayList<Anim>();
	public ArrayList<Exit> exits = new ArrayList<Exit>();

	public Sprite bgImage;
	public Sprite fgImage;
	
	/**
	 * xMin and xMax are the boundaries outside the doll can't go (but if part of an animation).
	 */	
	public int xMin;
	public int xMax;
		
	private DatabaseAccess db;
	
	/**
	 * For logs only.
	 */	
	private String className = "Background";
	
	private Context context;
	public int screenId;
	
	public BitmapTextureAtlas bgBTA;
	public BitmapTextureAtlas itemsBTA;
	public BitmapTextureAtlas animsBTA;
	public BitmapTextureAtlas charsBTA;
	
	/**
	 * Everything is loaded and created directly in the constructor (background, items, animations, areas, exists).
	 * @param	dbh			database handler is stored for upcoming database calls
 	 * @param	context		Android context, to retrieve files
 	 * @param	screenId	id of the current screen
	 * @param	engine		AndEngine engine, to load textures
	 * @param	scene		AndEngine scene, to attach sprites to scene and register touch areas
	 * @param	elTR		texture region of the left exit, to create corresponding animated sprite
	 * @param	erTR		texture region of the right exit, to create corresponding animated sprite
	 */	
	public Background(DatabaseHandler dbh, Context context, int screenId, Engine engine, Scene scene, TiledTextureRegion elTR, TiledTextureRegion erTR) {

		this.db = new DatabaseAccess(dbh);
		this.context = context;
		this.screenId = screenId;
		
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
	/**
	 * Loads background and foreground (if existing) images and creates every sprites, attach them to the AndEngine scene,
	 * register touch areas and set ZIndexes, and set background boundaries.
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void load(Engine engine, Scene scene) {
		
		try {			
			// -height- x -max scale- x -background & foreground- => 320 x 1.5 x 2 =  960px 
			// -width-  x -max scale- x -max size screen-         => 1360 x 1.5    = 2040px 
			bgBTA = new BitmapTextureAtlas(4096, 1024, TextureOptions.DEFAULT);
		
			Map<String, String> hm = db.selectBackground(this.screenId);
			
			int bgFile = context.getResources().getIdentifier(hm.get("background"), "drawable", context.getPackageName());
			if (bgFile == 0) {
				bgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
				if (LOG_ON) Log.w("Clementime", className + "/loadBackground(): cannot find background screen " + screenId + " " + hm.get("background"));
			} else if (LOG_ON) Log.d("Clementime", className + "/loadBackground(): load background " + hm.get("background"));
			
			// load foreground only if available
			if (hm.get("foreground") != "" && hm.get("foreground") != null && hm.get("foreground").length() > 1) {	
				int fgFile = context.getResources().getIdentifier(hm.get("foreground"), "drawable", context.getPackageName());
				if (fgFile == 0) {
					fgFile = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
					if (LOG_ON) Log.w("Clementime", className + "/loadBackground(): cannot find foreground screen " + screenId + " " + hm.get("foreground"));
				} else if (LOG_ON) Log.d("Clementime", className + "/loadBackground(): load foreground " + hm.get("foreground"));

				TextureRegion TRFore = BitmapTextureAtlasTextureRegionFactory.createFromResource(bgBTA, context, fgFile, 0, 480);
				fgImage = new Sprite(0, 0 - SET_BACKGROUND_POSITION_Y + MARGIN_Y, TRFore);
				
				scene.attachChild(fgImage);
				fgImage.setZIndex(ZINDEX_GROUND_1);
			} else if (LOG_ON) Log.i("Clementime", className + "/loadBackground(): no foreground to load");
			
			TextureRegion TRBack = BitmapTextureAtlasTextureRegionFactory.createFromResource(bgBTA, context, bgFile, 0, 0);
			
			bgImage = new Sprite(0, 0 - SET_BACKGROUND_POSITION_Y + MARGIN_Y, TRBack);
			
			if (LOG_ON) Log.i("Clementime", className + "/load(): bg width " + bgImage.getWidth() + " - height " + bgImage.getHeight());
			//bgImage.setPosition(0,0);
		
			xMin = Integer.parseInt(hm.get("x_min"));
			xMax = Integer.parseInt(hm.get("x_max"));
			
			engine.getTextureManager().loadTexture(bgBTA);
			
			scene.attachChild(bgImage);		
			bgImage.setZIndex(ZINDEX_GROUND_0);
			//bgImage.setVisible(false); // used to test if a zindex is not correctly set
			
		} catch (Exception e) {
			Log.e("Clementime", className + "/loadBackground():failed to load screen " + screenId);		
		}
	}
	/**
	 * Loads images and creates every sprites, attach them to the AndEngine scene, register touch areas and set ZIndexes.
	 * Dynamically set images on the BitmapTextureAtlas with posX & posY.
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void loadItems(Engine engine, Scene scene) {
		
		LinkedList<Map<String, String>> ll = db.selectScreenItems(this.screenId);
		
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
					
					if (LOG_ON) Log.d("Clementime", className + "/loadItems(): load item " + hm.get("image"));
	
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
				Log.e("Clementime", className + "/loadItems(): failed to load items - " + e.getMessage());
			}
			
			engine.getTextureManager().loadTexture(itemsBTA);
			
			ListIterator<ScreenItem> itItems = items.listIterator();
						
			while(itItems.hasNext()){
				spriteToAttach = itItems.next();
				
				scene.attachChild(spriteToAttach);
				spriteToAttach.setPosition(spriteToAttach.x * SCALE_POSITION, (spriteToAttach.y - SET_BACKGROUND_POSITION_Y + MARGIN_Y) * SCALE_POSITION);	
				
				// some items have to be placed beyond the doll, on the foreground
				if (spriteToAttach.foreground) spriteToAttach.setZIndex(ZINDEX_FOREGROUND);
				else spriteToAttach.setZIndex(ZINDEX_ITEM);
				
				scene.registerTouchArea(spriteToAttach);
				spriteToAttach.andEngineId = scene.getChildIndex(spriteToAttach);
				
				if (LOG_ON) Log.d("Clementime", className + "/loadItems(): *** display item " + spriteToAttach.id + " ***");
			}
			
		} else if (LOG_ON) Log.d("Clementime", className + "/loadItems(): ***screen " + screenId + " has no item***");
	}
	//TODO: (maybe, have to think of it) change actions information at running time instead of calling the database)
	// currently information about actions in item objects aren't used
	/**
	 * Create an item object (sprite) with information about its position and allowed actions. 
	 * @param	hm		tab with database information
	 * @param	BTA		AndEngine BitmapTextureAtlas to store all item images
	 * @param	xPos	calculated position on BTA
	 * @param	yPos	calculated position on BTA
	 */	
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
		talk = Integer.parseInt(hm.get("talkk_state"));
		//exit = Integer.parseInt(hm.get("exit"));
		if (Integer.parseInt(hm.get("foreground")) == 1) takeable = true;
		else takeable = false;
		if (Integer.parseInt(hm.get("foreground")) == 1) foreground = true;
		else foreground = false;

		items.add(new ScreenItem(id, x, y, take, look, talk, takeable, foreground, TR));
		
		if (LOG_ON) Log.d("Clementime", className + "/createItem(): create item " + file + " -id: " + id);
	}
	/**
	 * Loads images and creates every animated sprites, attach them to the AndEngine scene, register touch areas and set ZIndexes.
	 * Dynamically set images on the BitmapTextureAtlas with posX & posY.
	 * @param	engine	AndEngine engine, to load textures
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
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
							
					if (LOG_ON) Log.d("Clementime", className + "/loadAnimations(): load animation " +  Integer.parseInt(hm.get("id")));
	
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
					
					if (LOG_ON) Log.d("Clementime", className + "/loadAnimations(): *** display anim " + spriteToAttach.id + " ***");
					
					spriteToAttach.setZIndex(ZINDEX_ANIM);
					scene.registerTouchArea(spriteToAttach);
					spriteToAttach.andEngineId = scene.getChildIndex(spriteToAttach);
				}
				
			} catch (Exception e) {
				Log.e("Clementime", className + "/loadAnimations(): failed to load animations - " + e.getMessage());
			}
		}
	}
	/**
	 * Create an animation object (animated sprite) with position and running information. 
	 * @param	hm		tab with database information
	 * @param	BTA		AndEngine BitmapTextureAtlas to store all item images
	 * @param	xPos	calculated position on BTA
	 * @param	yPos	calculated position on BTA
	 */
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
		String file = hm.get("image");
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
		if (LOG_ON) Log.d("Clementime", className + "/createAnimation(): create animation " + id);

	}
	/**
	 * Loads and creates a single item, after a combination of two existing items resulting in another one. 
	 * @param	itemId	database id of item to create
	 * @param	engine	AndEngine engine, to load textures
	 */	
	public void loadItem(int itemId, Engine engine) {
		
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		Map<String, String> hm = db.selectItem(itemId);
				
		createItem(hm, BTA, 0, 0);	
		
		engine.getTextureManager().loadTexture(BTA);
	}
	/**
	 * Stores screen areas the player can interact with. 
	 */	
	public void createAreas() {
		areas = db.selectAreas(screenId, MARGIN_Y);
	}
	/**
	 * Stores all the screen exists (even these which aren't displayed at first).
	 * @param	TRLeft		texture region of the left exit, to create corresponding animated sprite
	 * @param	TRRight		texture region of the right exit, to create corresponding animated sprite
	 */		
	public void createExits(TiledTextureRegion TRLeft, TiledTextureRegion TRRight) {
		exits = db.selectExits(screenId, TRLeft, TRRight, MARGIN_Y);
	}
	/**
	 * Launches character or landscape animations (looping and not moving animations). 
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void showStaticAnims(Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/showStaticAnims()");
		
		ArrayList<Integer> displayedAnims = db.selectDisplayedAnims(screenId);
		ListIterator<Integer> itAnims = displayedAnims.listIterator();
		
		while(itAnims.hasNext()) launchStaticAnim(itAnims.next(), scene);	
	}
	/**
	 * Launches character or landscape animations (looping and not moving animations). 
	 * @param	animId	database id of animation to be launched
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	private void launchStaticAnim(int animId, Scene scene) {
		
		//****************************************************************
		// features[0] = first_frame
		// features[1] = last_frame
		// features[2] = frame_duration
		//****************************************************************
		int[] animFeatures = db.selectStaticAnimFeatures(animId);
		
		if (LOG_ON) Log.i("Clementime","Screen/launchStaticAnim(): launch static animation " + animId);
		
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
	/**
	 * Displays exits the doll is allowed to use (others will be displayed at run time). 
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */	
	public void showExits(Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/showExits()");

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
				if (LOG_ON) Log.v("Clementime", "Screen/showExits(): exit " + exit.id + " on display");
			} else if (LOG_ON) Log.v("Clementime", "Screen/showExits(): exit " + exit.id + " hidden");
					
		}
	}
	
	/**************************************/
	/* DURING RUNTIME                     */
	/**************************************/
	
	/**
	 * When an item is taken by the doll, it disappears of the screen; when two items are combined, another can appear on the screen. 
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 * @param	item	item object to hide or display
	 */	
	public void hideShowItem(Scene scene, ScreenItem item) {
		
		if (LOG_ON) Log.i("Clementime", className + "/hideShowItem()");	

		if (item.isVisible()) {
			item.setVisible(false);
			scene.unregisterTouchArea(item);					
		} else {
			item.setVisible(true);
			scene.registerTouchArea(item);				
		}
	}
	/**
	 * When an item is taken by the doll, it disappears of the screen; when two items are combined, another can appear on the screen.
	 * @param	itemId	id of item to hide or display 
	 * @param	scene	AndEngine scene, to attach sprites to scene and register touch areas
	 */
	public void hideShowItemById(int itemId, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/hideShowScreenItem()");
		
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
	//TODO: find if touch area must be deactivated in this case (as for item ??)
	/**
	 * Animation can appear or disappear, depending on the scenario.
	 * @param	animId	id of animation to hide or display 
	 */
	public void hideShowAnimation(int animId) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/hideShowAnimation()");
		
		ListIterator<Anim> it = anims.listIterator();
		
		while(it.hasNext()){
			Anim anim = it.next();
			
			if (anim.id == animId) {
				if (anim.isVisible()) anim.setVisible(false);
				else anim.setVisible(true);

			}
		}
	}
	/**
	 * For action manager.
	 * @param	animId	id of concerned animation
	 * @return	states	states[0]=take; states[1]=look; states[2]=talk;  
	 */
	public int[] getAnimStates(int animId) {	
		return db.selectAnimStates(animId);
	}
	/**
	 * For action manager.
	 * @param	itemId	id of concerned item
	 * @return	states	states[0]=take; states[1]=look; states[2]=talk;  
	 */
	public int[] getItemStates(int itemId) {	
		return db.selectItemStates(itemId);
	}
	/**
	 * For action manager.
	 * @param	areaId	id of concerned area
	 * @return	states	states[0]=take; states[1]=look; states[2]=talk;  
	 */
	public int[] getAreaStates(int areaId) {	
		return db.selectAreaStates(areaId);
	}
	/**
	 * Checks if there is an existing area at the touched point.
	 * @param	x		touched x (on scene/background, not local screen x)
	 * @param	y		touched y (on scene/background, not local screen y)
	 * @return	area	touched area, if there is one, or null  
	 */
	public Area checkAreas(float x, float y) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/checkAreas()");
		
		Area touchedArea = null;
		
		ListIterator<Area> it = this.areas.listIterator();
		
		while(it.hasNext()){
			Area area = it.next();
					
			//if (LOG_ON) Log.d("Clementime", "Screen/checkAreas(): x: " + x + "-y: " + y + "-area xMin: " + area.x + "-area yMin: " + area.y + "-area xMax: " + (area.x + area.width) + "-area yMax: " + (area.y + area.height));
			
			if (x >= area.x && x <= (area.x + area.width) && y >= area.y && y <= (area.y + area.height)) touchedArea = area;		
		}
		
		return touchedArea;
	}
	/**
	 * Checks if there is an existing area at the touched point.
	 * @param	itemId		database id of item to add
	 * @param	engine		AndEngine engine, to load texture
	 * @param	scene		AndEngine scene, to attach sprite to scene and register touch area 
	 */
	public void addItemOnScreen(int itemId, Engine engine, Scene scene) {
//	public ScreenItem addItemOnScreen(int itemId, Engine engine, Scene scene) {
		
		if (LOG_ON) Log.i("Clementime", "Screen/addItemOnScreen()");

		int newValue = DB_INVENTORY_VALUE_ON_SCREEN;
		String where = " _id = " + itemId;
		db.updateInventoryField(where, newValue);

		loadItem(itemId, engine);

		ScreenItem item = items.get(items.size() - 1);  

		scene.attachChild(item);
		scene.registerTouchArea(item);
		
		//return item;
	}
}

