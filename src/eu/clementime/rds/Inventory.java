package eu.clementime.rds;

import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DEFAULT_IMAGE;
import static eu.clementime.rds.Constants.INVBOX_ALPHA_LAYER;
import static eu.clementime.rds.Constants.INVENTORY_BAG_POSX;
import static eu.clementime.rds.Constants.INVENTORY_BAG_POSY;
import static eu.clementime.rds.Constants.INVENTORY_IMAGE_PREFIX;
import static eu.clementime.rds.Constants.INVENTORY_IMAGE_PREFIX_ZOOM;
import static eu.clementime.rds.Constants.INVENTORY_MAX_SIZE_ITEM;
import static eu.clementime.rds.Constants.INVENTORY_MAX_SIZE_ZOOM;
import static eu.clementime.rds.Constants.INVENTORY_POSX_NORMALVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSX_ZOOMVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSX_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INVENTORY_POSY_NORMALVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSY_ZOOMVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSY_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INVENTORY_SIZE_BETWEEN_BOXES;
import static eu.clementime.rds.Constants.INVENTORY_SIZE_BOXES;
import static eu.clementime.rds.Constants.INV_ALPHA_LAYER;
import static eu.clementime.rds.Constants.MASK_ALPHA_LAYER;
import static eu.clementime.rds.Constants.POSX_ZOOMVIEW;
import static eu.clementime.rds.Constants.POSY_ZOOMVIEW;
import static eu.clementime.rds.Constants.ZINDEX_INVENTORY;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM;

import static eu.clementime.rds.Global.CAMERA_WIDTH;
import static eu.clementime.rds.Global.CAMERA_HEIGHT;
import static eu.clementime.rds.Global.MARGIN_Y;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.content.Context;
import android.util.Log;

public class Inventory {

	public ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
	//private ArrayList<ScreenItem> screenItems = new ArrayList<ScreenItem>();

	private TextureRegion TR1;
	private TextureRegion TR2;
	private TextureRegion TR3;
	private TextureRegion TR4;
	
//	public Sprite bag;
//	public Sprite openBag;
	
	private Font font;
	
	public Entity normalView;
	public Sprite zoomView;
	public Rectangle mask;

	//private float width;
	public float boxPlusInterval;
	
	private Context context;
	private DatabaseAccess db;
	
	Sprite touchedSprite; 	
		
	public Inventory(Camera camera, Context context, DatabaseHandler dbh, Engine engine, Scene scene) {

		this.context = context;
		this.db = new DatabaseAccess(dbh);
		
		load(engine, scene);
		loadItems(engine, scene);
	}
	
	//*********************************************
	//        LOADING IMAGES FROM DATABASE
	//*********************************************
	
	public void load(Engine engine, Scene scene) {
		
		Log.d("Clementime", "Inventory/load()");

		BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.TR1 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.inventory_zoom_box, 0, 0);
		this.TR3 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.inventory_box, 0, 240);
//		this.TR2 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.magali_bag_opened, 80, 240);
//		this.TR4 = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, R.drawable.magali_bag, 160, 240);

		engine.getTextureManager().loadTexture(BTA);
		
//		bag = new Sprite(INVENTORY_BAG_POSX, INVENTORY_BAG_POSY, TR4);
//		openBag = new Sprite(INVENTORY_BAG_POSX, INVENTORY_BAG_POSY, TR2);
//		bag.setAlpha(0.7f);
//		openBag.setAlpha(0.6f);
//		openBag.setVisible(false);
		
		// boxPlusInterval is used to set boxes in normal view
		boxPlusInterval = INVENTORY_SIZE_BOXES + INVENTORY_SIZE_BETWEEN_BOXES;
		
		// Zoom view: items are zoomed with description
		mask = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mask.setColor(1, 1, 1, MASK_ALPHA_LAYER);
		zoomView = new Sprite(INVENTORY_POSX_ZOOMVIEW, INVENTORY_POSY_ZOOMVIEW, TR1);
		
		// normal view: items are listed in boxes at the bottom of the screen
		normalView =  new Entity();
		normalView.setPosition(INVENTORY_POSX_NORMALVIEW, INVENTORY_POSY_NORMALVIEW + MARGIN_Y);

		drawInventory();
	
		normalView.setVisible(false);
		mask.setVisible(false);	
		zoomView.setVisible(false);
		
//		scene.attachChild(bag);
//		scene.attachChild(openBag);
		scene.attachChild(mask);
		scene.attachChild(zoomView);
		scene.attachChild(normalView);
		
//		bag.setZIndex(ZINDEX_INVENTORY);
//		openBag.setZIndex(ZINDEX_INVENTORY);
		mask.setZIndex(ZINDEX_INVENTORY);
		zoomView.setZIndex(ZINDEX_INVENTORY);
		normalView.setZIndex(ZINDEX_INVENTORY);	
	}
	
	public void loadItems(Engine engine, Scene scene) {

		LinkedList<Map<String, String>> ll = db.selectInventoryItems();

		if (!ll.isEmpty()) {
			/* be careful:
			 *   inventory items image must not be bigger than INVENTORY_MAX_SIZE_ITEM  (square)  
			 *   big items image       must not be bigger than INVENTORY_MAX_SIZE_BIG (square)  
			 *   zoom items image      must not be bigger than INVENTORY_MAX_SIZE_ZOOM (square)  
			 */
			int posXInv = 0;
			int posYInv = 0;
			int posXZoom = 0;
			int posYZoom = 0;
			int j = 1;
			
			BitmapTextureAtlas invBTA = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			BitmapTextureAtlas zoomBTA = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			TextureRegion invTR;
			TextureRegion zoomTR;
		
			int id;
			
			ListIterator<Map<String, String>> it = ll.listIterator();
			
			Map<String, String> hm = new HashMap<String, String>();
			
			try {
				while (it.hasNext()) {
					// retrieve images
					String invFile = hm.get("image") + INVENTORY_IMAGE_PREFIX;
					String zoomFile = hm.get("image") + INVENTORY_IMAGE_PREFIX_ZOOM;
					
					int invRes = context.getResources().getIdentifier(invFile, "drawable", context.getPackageName());
					if (invRes == 0) {
						Log.w("Clementime", "InventoryFrame/loadItems(): cannot find image " + invFile + " - try to find default image " + DEFAULT_IMAGE);
						invRes = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
						if (invRes == 0) Log.w("Clementime", "InventoryFrame/loadItems(): cannot find default image " + DEFAULT_IMAGE);
					}
					int zoomRes = context.getResources().getIdentifier(zoomFile, "drawable", context.getPackageName());
					if (zoomRes == 0) {
						Log.w("Clementime", "InventoryFrame/loadItems(): cannot find image " + zoomFile + " - try to find default image " + DEFAULT_IMAGE);
						zoomRes = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
						if (zoomRes == 0) Log.w("Clementime", "InventoryFrame/loadItems(): cannot find default image " + DEFAULT_IMAGE);
					}
	
					
					invTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(invBTA, context, invRes, posXInv, posYInv);
					zoomTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(zoomBTA, context, zoomRes, posXZoom, posYZoom);
		
					// create item from database information	
					id = Integer.parseInt(hm.get("id"));
		
					items.add(new InventoryItem(id, new Sprite(0, 0, zoomTR), new Sprite(0, 0, zoomTR), new Sprite(0, 0, invTR), TR3));
					Log.d("Clementime", "InventoryFrame/loadItems(): load item " + invFile + " id: " + id);
	
					posXInv = posXInv + INVENTORY_MAX_SIZE_ITEM;
					posXZoom = posXZoom + INVENTORY_MAX_SIZE_ZOOM;
	
					
					j++;
					
					if (j >= 5) {
	
						j = 1;
						posXInv = 0;
						posXZoom = 0;
						
						posYInv = posYInv + INVENTORY_MAX_SIZE_ITEM;
						posYZoom = posYZoom + INVENTORY_MAX_SIZE_ZOOM;					
					}
	
				}
				
				engine.getTextureManager().loadTextures(invBTA, zoomBTA);
				
				ListIterator<InventoryItem> itItems = items.listIterator();

				InventoryItem item;
				
				while(it.hasNext()){
					item = itItems.next();

					//scene.attachChild(item.zoom); // zoom must be drawn before big
					scene.attachChild(item.big);
					
					// item are attached to the scene, so they are sorted in SideScreen when displaying inventory to avoid zoom view going above dragged view
					item.setZIndex(ZINDEX_INVENTORY);
					item.small.setZIndex(ZINDEX_INV_ITEM);
					item.big.setZIndex(ZINDEX_INV_ITEM);
				}	
				
			} catch (Exception e) {
				Log.w("Clementime", "InventoryFrame/loadItems(): failed to load inventory items - " + e.getMessage());
			}	
		}
	}

	//*********************************************
	//        USING INVENTORY during game 
	//*********************************************
	public void display(float cameraMinX, Scene scene) {
		
		Log.d("Clementime", "InventoryFrame/display()");

		normalView.setPosition(cameraMinX + INVENTORY_POSX_NORMALVIEW, normalView.getY());
		
		ListIterator<InventoryItem> itItem = items.listIterator();
			
		while(itItem.hasNext()){
			Sprite sprite = itItem.next();
			scene.registerTouchArea(sprite);
		}			
		
		normalView.setVisible(true);
//		bag.setVisible(false);
//		openBag.setVisible(true);
	}
	
	public void hide() {
		
		Log.d("Clementime", "InventoryFrame/hide()");

		normalView.setVisible(false);
//		bag.setVisible(true);
//		openBag.setVisible(false);
	}
	
	public void displayZoomView(float xMin, InventoryItem touchedItem, Scene scene) {		
			
		Log.d("Clementime", "InventoryFrame/displayZoomView()");

		mask.setVisible(true);
		mask.setPosition(xMin, mask.getY());
		zoomView.setVisible(true);
 		scene.registerTouchArea(zoomView);
		
 		// zoom view is positioned depending on it is open when inventory is open or when an object is taken
		if (normalView.isVisible())	zoomView.setPosition(xMin + CAMERA_WIDTH/2 - zoomView.getWidth()/2, INVENTORY_POSY_ZOOMVIEW + MARGIN_Y);
		else zoomView.setPosition(xMin + CAMERA_WIDTH/2 - zoomView.getWidth()/2, CAMERA_HEIGHT/2 - zoomView.getHeight()/2);
//		if (normalView.isVisible())	zoomView.setPosition(xMin + INVENTORY_POSX_ZOOMVIEW, INVENTORY_POSY_ZOOMVIEW + MARGIN_Y);
//		else zoomView.setPosition(xMin + POSX_ZOOMVIEW, POSY_ZOOMVIEW + MARGIN_Y);
		
		displayZoomItem(xMin, touchedItem);
	}
	
	public void displayZoomItem (float xMin, InventoryItem touchedItem) {
		
		Log.d("Clementime", "InventoryFrame/displayZoomItem()");
		
		// xMin: x of the camera from the beginning of the screen
		float x = zoomView.getX() + INVENTORY_POSX_ZOOM_ITEM - (touchedItem.big.getWidth()/2);
		float y = zoomView.getY() + INVENTORY_POSY_ZOOM_ITEM - (touchedItem.big.getHeight()/2);
		
		touchedItem.big.setPosition(x, y);
		touchedItem.big.setVisible(true);
    	touchedItem.setAlpha(INVBOX_ALPHA_LAYER);
    	touchedItem.small.setAlpha(INV_ALPHA_LAYER); 
	}
	
	public void hideZoomView(InventoryItem touchedItem, Scene scene) {
		
		Log.d("Clementime", "InventoryFrame/hideZoomView()");

		touchedItem.big.setVisible(false);
    	touchedItem.setAlpha(1);
    	touchedItem.small.setAlpha(1);
		mask.setVisible(false);
		zoomView.setVisible(false);
		scene.unregisterTouchArea(zoomView);
		
		touchedItem.big.setPosition(-200, 0); // out of screen
	}
	
	public void drawInventory() {
		
		Log.d("Clementime", "InventoryFrame/drawInventory()");
		
		// xBox is used to give position of boxes/items inside inventory
		float xBox = 0;
		
		ListIterator<InventoryItem> itItem = items.listIterator();
		
		while(itItem.hasNext()){
			
			
			
			drawItem(itItem.next(), xBox, 0);
			xBox = xBox + boxPlusInterval;
		}	
	}

	public void redrawInventory() {
		
		Log.d("Clementime", "InventoryFrame/redrawInventory()");

		// xBox is used to give position of boxes/items inside inventory
		float xBox = 0;
		
		ListIterator<InventoryItem> itItem = items.listIterator();
		
		while(itItem.hasNext()){
			InventoryItem item = itItem.next();
			item.setPosition(xBox, 0);
			float xSprite = xBox + (INVENTORY_SIZE_BOXES - item.small.getWidth()) / 2;
			float ySprite = (INVENTORY_SIZE_BOXES - item.small.getHeight()) / 2;
			item.small.setPosition(xSprite, ySprite);
			xBox = xBox + boxPlusInterval;
		}	
	}

	public InventoryItem addItem(int itemId, Engine engine, Scene scene) {
		
		InventoryItem item;
		
		int newValue = DB_INVENTORY_VALUE_IN;
		String where = " _id = " + itemId;
		db.updateInventoryField(where, newValue);
		
		// sizes: inventory = 70px / big = 100px / zoom = 200px 
		BitmapTextureAtlas BTA = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegion invTR;
		TextureRegion zoomTR;
	
		int id = 0;

		Map<String, String> hm = db.selectInventoryItem(itemId);
		
		try {

			// retrieve images
			String invFile = hm.get("image") + INVENTORY_IMAGE_PREFIX;
			String zoomFile = hm.get("image") + INVENTORY_IMAGE_PREFIX_ZOOM;
			
			int invRes = context.getResources().getIdentifier(invFile, "drawable", context.getPackageName());
			if (invRes == 0) {
				invRes = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
			}
			int zoomRes = context.getResources().getIdentifier(zoomFile, "drawable", context.getPackageName());
			if (zoomRes == 0) {
				zoomRes = context.getResources().getIdentifier(DEFAULT_IMAGE, "drawable", context.getPackageName());
			}
			
			invTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, invRes, 0, 0);
			zoomTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(BTA, context, zoomRes, 101, 0);

			// create item from database information	
			id = Integer.parseInt(hm.get("id"));

			Log.d("Clementime", "InventoryFrame/addItem(): add item " + invFile + " id: " + id);
			item = new InventoryItem(id, new Sprite(0, 0, zoomTR), new Sprite(0, 0, zoomTR), new Sprite(0, 0, invTR), TR3);
			items.add(item);
			
			engine.getTextureManager().loadTexture(BTA);
			
			scene.attachChild(item.big);
			
			// item are attached to the scene, so they are sorted in SideScreen when displaying inventory to avoid zoom view going above dragged view
			item.setZIndex(ZINDEX_INVENTORY);
			item.small.setZIndex(ZINDEX_INV_ITEM);
			item.big.setZIndex(ZINDEX_INV_ITEM);
			
			addItemAtEnd(item);
			
			return item;
			
		} catch (Exception e) {
			Log.w("Clementime", "InventoryFrame/addItem(): failed to add inventory item " + id + " - " + e.getMessage());
			return null;
		}	
	}
	
	public void addItemAtEnd(InventoryItem item) {
		
		Log.d("Clementime", "InventoryFrame/addItemAtEnd()");

		drawItem(item, (items.size() - 1) * boxPlusInterval, 0);
	}

	// TODO: check if hide & remove item are different and if garbage collector take all sprites (because of itItem.remove()...
	public void hideItem(InventoryItem item) {
		
		Log.i("Clementime", "Screen/hideInventoryItem()");
		
		item.big.setVisible(false);
		item.big.setPosition(-200, 0);
	}
	
	public void removeItem(int itemId) {
		
		Log.d("Clementime", "InventoryFrame/removeItem()");

		// remove item from list
		ListIterator<InventoryItem> itItem = items.listIterator();
		
		while(itItem.hasNext()){
			InventoryItem item = itItem.next();
			
			if (item.id == itemId) {
				item.setVisible(false);
				item.small.setVisible(false);
				item.setPosition(-200, 0);
				item.small.setPosition(-200, 0);
				itItem.remove();
				break;
			}
		}	
	}

	public void drawItem(InventoryItem item, float xBox, float yBox) {
		
		Log.d("Clementime", "InventoryFrame/drawItem()");

		normalView.attachChild(item);
		normalView.attachChild(item.small);
		item.setPosition(xBox, yBox);

		float xSprite = xBox + (INVENTORY_SIZE_BOXES - item.small.getWidth()) / 2;
		float ySprite = yBox + (INVENTORY_SIZE_BOXES - item.small.getHeight()) / 2;
		item.small.setPosition(xSprite, ySprite);

		item.big.setVisible(false);
		item.big.setPosition(-200, 0); // out of screen, avoiding sprite to be seen anywhere when set visible
		
		final PhysicsHandler physicsHandler = new PhysicsHandler(item.big);
		item.big.registerUpdateHandler(physicsHandler);

	}
	
	public void moveItemList(float xMin, float x) {
		float movement = (float)Math.exp(Math.abs(x));

		if (x < -1 && normalView.getX() + items.size() * boxPlusInterval > xMin + CAMERA_WIDTH) {
			normalView.setPosition(normalView.getX()-movement, normalView.getY());	
		} else if (x > 1 && normalView.getX() < xMin) {
			normalView.setPosition(normalView.getX()+movement, normalView.getY());
		}
		
		// avoid going too far on screen at beginning of list
		if (normalView.getX() > xMin) normalView.setPosition(xMin, normalView.getY());

	}
}
