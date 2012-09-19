package eu.clementime.rds;

import static eu.clementime.rds.Constants.ACTION_EXIT;
import static eu.clementime.rds.Constants.ACTION_LOOK;
import static eu.clementime.rds.Constants.ACTION_TAKE;
import static eu.clementime.rds.Constants.ACTION_TALK;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT_MDPI;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT_HDPI;
import static eu.clementime.rds.Constants.BACKGROUND_MAX_HEIGHT_XHDPI;
import static eu.clementime.rds.Constants.BIG_ITEM_POSITION;
import static eu.clementime.rds.Constants.CAMERA_HEIGHT;
import static eu.clementime.rds.Constants.CAMERA_WIDTH;
import static eu.clementime.rds.Constants.CLICK_AGAIN_INVENTORY;
import static eu.clementime.rds.Constants.CLICK_BAG;
import static eu.clementime.rds.Constants.CLICK_DOLL;
import static eu.clementime.rds.Constants.CLICK_INVENTORY;
import static eu.clementime.rds.Constants.CLICK_OFF;
import static eu.clementime.rds.Constants.CLICK_ZOOM;
import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_IN_INVENTORY;
import static eu.clementime.rds.Constants.DB_COMBINATION_VALUE_ON_SCREEN;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_IN;
import static eu.clementime.rds.Constants.DB_INVENTORY_VALUE_OUT;
import static eu.clementime.rds.Constants.DB_TABLE_COMBINATION;
import static eu.clementime.rds.Constants.DEVELOPMENT;
import static eu.clementime.rds.Constants.DIRECTION_LEFT;
import static eu.clementime.rds.Constants.DIRECTION_RIGHT;
import static eu.clementime.rds.Constants.INVBOX_ALPHA_LAYER;
import static eu.clementime.rds.Constants.INVENTORY_POSX_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INVENTORY_POSY_NORMALVIEW;
import static eu.clementime.rds.Constants.INVENTORY_POSY_ZOOM_ITEM;
import static eu.clementime.rds.Constants.INV_ALPHA_LAYER;
import static eu.clementime.rds.Constants.LOG_ON;
import static eu.clementime.rds.Constants.LOOP_LOG_INTERVAL;
import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.MODE_ACTION_WAIT;
import static eu.clementime.rds.Constants.MODE_ACTION_WALK;
import static eu.clementime.rds.Constants.MODE_ANIM_ACTION;
import static eu.clementime.rds.Constants.MODE_ANIM_RUNNING;
import static eu.clementime.rds.Constants.MODE_ANIM_TALK;
import static eu.clementime.rds.Constants.MODE_INVENTORY_DROP;
import static eu.clementime.rds.Constants.MODE_INVENTORY_OPEN;
import static eu.clementime.rds.Constants.MODE_INVENTORY_ZOOM;
import static eu.clementime.rds.Constants.NO_END_LOOP;
import static eu.clementime.rds.Constants.PLAYING_HAND;
import static eu.clementime.rds.Constants.POINTER_WALK;
import static eu.clementime.rds.Constants.POINTER_DOLL;
import static eu.clementime.rds.Constants.SCALE_POSITION;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;
import static eu.clementime.rds.Constants.STATUS_ACTION;
import static eu.clementime.rds.Constants.STATUS_ANIM;
import static eu.clementime.rds.Constants.STATUS_INVENTORY;
import static eu.clementime.rds.Constants.STATUS_MAP;
import static eu.clementime.rds.Constants.TALK_DISTANCE;
import static eu.clementime.rds.Constants.TALK_POSX;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM;
import static eu.clementime.rds.Constants.ZINDEX_INV_ITEM_IN_USE;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import org.anddev.andengine.collision.RectangularShapeCollisionChecker;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.input.touch.detector.ClickDetector;
import org.anddev.andengine.input.touch.detector.ClickDetector.IClickDetectorListener;
import org.anddev.andengine.opengl.buffer.BufferObjectManager;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
* Screen class is the main class of the game, and manages starting/closing game, player touch, loop game and running animations. 
* @author Cl&eacute;ment
* @version 1.0
*/
public class Screen extends BaseGameActivity implements IOnAreaTouchListener,
IOnSceneTouchListener, IClickDetectorListener, IAccelerometerListener {

	//*
	//* Android & General objects
	//*******************************************************/
	/**
	 * Used to keep database handler over the whole game.
	 */	
	private RdsGame app;
	private DatabaseHandler dbh;
	private Context context;
	
	private CharSequence text = "An error occurred during the game\nPlease contact xxx\nPlease send logs to zzz";
	private int duration = Toast.LENGTH_LONG;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
	//*
	//* AndEngine objects (loaded at game start)
	//*******************************************************/
	private final ClickDetector clickDetector = new ClickDetector(this);
	private Camera camera;
	private Scene scene = new Scene();
	private PhysicsHandler phAnimRunning;
	private Doll doll;

	//*
	//* changeable objects & screen variables (loaded at each screen)
	//*******************************************************/
	private Background currentBg;
	/**
	 * Loaded background, waiting to replace current background when switching screen for another. 
	 */	
	private Background nextBg;
	
	private float minChasingX = 0;
	private float maxChasingX = 0;
	
	//*
	//* persistent objects (loaded at game start)
	//*******************************************************/
	private Inventory inventory;
	private Talk talk;
	
	/**
	 * Moving arrows, pointers, icons, action manager. 
	 */	
	private GameTools gameTools;
//	private Map map;
	
	private World world;
	
	//*
	//* touched objects, variables & booleans => keep trace at runtime
	//*****************************************************************/	
//	private AnimatedSprite touchedAction = null;
	private int touchedAction = 0;
	private Exit touchedExit = null;
	private InventoryItem touchedInventoryItem = null;
	private InventoryItem touchedZoomItem = null;
	private ScreenItem touchedItem = null;
	private ScreenItem itemToBeRemoved = null;
//	private Sprite touchedMapItem = null;
	private Area touchedArea = null;
//	private PlayingAnimation touchedAnimation = null;
	private Anim touchedAnimation = null;

	private boolean movingArrowPressed = false;
	
	/**
	 * Avoid doll trying to go until action manager if it is out of reach for her. 
	 */	
	private boolean actionsManagerOutsideBorders = false;

	// doll moving
	private float touchedX = 0;
	//private int scaledBgWidth;

	// status
	/**
	 * The 3 status, associated with one mode, indicate what sort of things the doll is allowed to do.<p>
	 * - Action (= can walk, take, talk, look, exit),</br>
	 * - Inventory (= inventory is open),</br>
	 * - Animation (doll is doing something, as talking, or an animation is running)
	 * @see #mode 
	 */	
	private int status = STATUS_ACTION;
	
	/**
	 * The mode specifies the status and depends on it; it indicates precisely what doll is doing.<p>
	 * Action status allows 2 modes:</br>
	 *   > Walk,</br>
	 *   > Wait (for an action: action manager is visible),<p>
	 * Inventory status allows 3 modes:</br>
	 *   > Open (zoom is closed, click to open zoom or drag an item),</br>
	 *   > Zoom (zoom is open, click to close or drag an item),</br>
	 *   > Drop (player is dragging an item onto something else, zoomed item or screen item),<p>
	 * Animation status allows 2 modes:</br>
	 *   > Action (doll is looking, moving toward an item or an exit)</br>
	 *   > Talk (doll is talking),</br>
	 *   > Animation is running (player must wait until this animation is finished).
	 * @see #status 
	 */	
	private int mode = MODE_ACTION_WALK;
	
	/**
	 * Used to know when and where a click happened.<p>Completes status and mode.
	 */	
	private int clickCheck = CLICK_OFF;

	// animation sequences
	/**
	 * Used to know if there is another animation to be triggered after the running animation. 
	 */	
	private int pendingTriggerId = 0;

	/**
	 * Used to know if there is another animation to be triggered during the running animation. 
	 */	
	private int simultaneousTriggerId = 0;
	
	/**
	 * Used to know if there is an animation running. 
	 */	
	private Anim runningAnim = null;
	
	// switch screen
	/**
	 * Used to know if there is an animation to be triggered at the beginning of the first screen. 
	 */	
	private int firstScreenTriggerId = 0;
	
	/**
	 * startingPosition[0] = x Doll / startingPosition[1] = y Doll. 
	 */	
	private int[] startingPosition;
	private boolean deactivateTouchEvents = false;
	
	private float downTime;
	private ChangeableText errorMessage;

//	private Sprite mapSprite;
	
	//***************************************
	// DEV TOOLS - IN DEVELOPMENT ONLY
	//***************************************
	private DevTools devTools;
	private Backup backup;
	private int load = 0;
	// DELAY log displayed in loop
	//*****************************
	boolean displayLog = false;
	private float lastLog = 0;
	private float nextLog = 0;
	private String className = "Screen";

	/* ************************************ */
	/*  FIRST LOAD - ONE-RUN METHODS        */
	/* ************************************ */
	
	/**
	 * AndEngine one-run method, at start.
	 */
    @Override
    public Engine onLoadEngine() {   	
		
		if (LOG_ON) Log.i("Clementime", className + "/onLoadEngine()");
		
		try {
	    	context = getApplicationContext();
	    	getWindowManager().getDefaultDisplay().getMetrics(dm);
	    	
	       	CAMERA_WIDTH = dm.widthPixels;
	    	CAMERA_HEIGHT = dm.heightPixels;
	    	
	    	if (dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM)		BACKGROUND_MAX_HEIGHT = BACKGROUND_MAX_HEIGHT_MDPI;
	    	else if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH)	BACKGROUND_MAX_HEIGHT = BACKGROUND_MAX_HEIGHT_HDPI;
	    	else if (dm.densityDpi >= 320)							BACKGROUND_MAX_HEIGHT = BACKGROUND_MAX_HEIGHT_XHDPI;
	    	
	    	Constants.setDependingScreenConstants();	

	    	camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    	
			if (LOG_ON) Log.i("Clementime", className + "/onLoadEngine(): screen width " + CAMERA_WIDTH + " - screen height " + CAMERA_HEIGHT);
	
		} catch (Exception e){
			Log.e("Clementime", className + "/onLoadEngine(): cannot set camera - " + e.getMessage());			
		}

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    	engineOptions.getTouchOptions().setRunOnUpdateThread(true);		

		return new Engine(engineOptions);
    } 
	/**
	 * AndEngine one-run method, at start.
	 */
	@Override
	public void onLoadResources() {	
		
		if (LOG_ON) Log.i("Clementime", className + "/onLoadResources()");
		
		try {
	//		loadingBTA = new BitmapTextureAtlas(512, 512, TextureOptions.DEFAULT);
	//		loadingTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(loadingBTA, this, R.drawable.loading, 0, 0);
	//
	//		this.mEngine.getTextureManager().loadTexture(loadingBTA);
			loadResources();			

		} catch (Exception e){
			Log.e("Clementime", className + "/onLoadResources(): cannot load resources - " + e.getMessage());			
		}
	}
	/**
	 * Persistent objects (= not depending on screen: doll, game tools, inventory, fonts, talk bubbles, etc...) are created and corresponding sprites loaded.
	 * This is a one-run method, at start.
	 */
	private void loadResources() {
		
		/*
		/* starting game, persistent objects are set up
		/**************************************************/
		
		if (LOG_ON) Log.i("Clementime", className + "/loadResources()");

        //**********************	
		// DOLL
        //**********************
		doll = new Doll(dbh, context, mEngine, scene);	

        //**********************
		// SCREEN
        //**********************	
		world = new World(dbh, context, doll.getScreen());
		gameTools = new GameTools(dbh, context, mEngine, scene);
		inventory = new Inventory(dbh, context, mEngine, scene);
		
		PLAYING_HAND = gameTools.getPlayingHand();
		
		if (LOG_ON) {
			if (PLAYING_HAND == -1) Log.d("Clementime", className + "/loadResources(): left handed player");
			else Log.d("Clementime", className + "/loadResources(): right handed player");
		}
		
        //**********************
		// FONTS
        //**********************
		getFontManager().loadFont(gameTools.font);
		getFontManager().loadFont(gameTools.defaultFont);
		getFontManager().loadFont(gameTools.defaultFont2);
		
		talk = new Talk(dbh, context,  mEngine, scene);
//		talk = new Talk(dbh, context, doll.getScreen(), mEngine, scene);
//		map = new Map(dbh, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		// map
//		mapBTA = new BitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
//		mapTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(mapBTA, this, R.drawable.settings, 0, 0);
		
		//***************************************
		// DEV TOOLS - IN DEVELOPMENT ONLY
		//***************************************

		if (DEVELOPMENT) {
	    	devTools = new DevTools(camera, gameTools.font, dbh, backup, context, mEngine, scene);    	
			getFontManager().loadFont(devTools.devFont);
			devTools.setup(scene);
		}
	}	
	/**
	 * AndEngine one-run method, at start.
	 */
	@Override
	public Scene onLoadScene() {
			
		this.mEngine.registerUpdateHandler(new FPSLogger());	
		
		try {
			
			setLoop();
			
			/*
			/* prepare background here, because items are needed in inventory
			/******************************************************************************/
			loadNewScreen();
	
			/*
			/* setup inventory, talk screen, information frame, game tools (arrows, pointer...)
			/******************************************************************************/

	//        inventory.setup(nextBg.items); // TODO: inventory shouldn't be part of next scene
	//        map.setup();

			/*
			/* then set up background, items, animations and foreground of current screen
			/* (changed when new screen is loaded)
			/******************************************************************************/
			currentBg = nextBg;
			initNewScreen();	
			
			if (LOG_ON) Log.i("Clementime", className + "/onLoadScene()");
	
	//		scene.attachChild(new Sprite(0, 0, loadingTR));

		} catch (Exception e){
			Log.e("Clementime", className + "/onLoadScene(): cannot load scene - " + e.getMessage());
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		return scene;
	}
	/**
	 * This is the loop of the game (into onUpdate() method).
	 * Manages running animations and doll chasing (= moving camera & game tools as doll is walking).</br> 
	 * Be careful not putting more that necessary inside the loop.</br>
	 * This is a one-run method, at start.
	 */
	private void setLoop() {
		
		if (LOG_ON) Log.i("Clementime", className + "/setLoop()");

	
		// checks during runtime
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }
	
			@Override
			public void onUpdate(final float pSecondsElapsed) {
	
				float pointToChase = -1;
				boolean chaseAnim = false;
				
				// DELAY log displayed in loop
				//*****************************
				if (LOG_ON) {
					if (lastLog >= nextLog) {
						displayLog = true;
						nextLog = nextLog + LOOP_LOG_INTERVAL;
					} else {
						lastLog += pSecondsElapsed;
						displayLog = false;					
					}
				}
				
				if (displayLog) Log.d("Clementime", className + "/onUpdate(): status " + status + " - mode " + mode);
				
	    		//************************************
	    		//     PLAY ANIMATIONS IF NEEDED
	    		//************************************  				
				// in animation mode, loop between triggers/animations until sequence is finished
				if (status == STATUS_ANIM && mode == MODE_ANIM_RUNNING) {
					
					if (simultaneousTriggerId != 0) {
						launchTrigger(simultaneousTriggerId, true);						
					}
	
					// chase and stop running animation when needed
					if (runningAnim != null) {

						if (phAnimRunning != null) {
							if (displayLog) Log.d("Clementime", className + "/onUpdate(): running animation " + runningAnim.id + " moving");

							if (phAnimRunning.getVelocityX() != 0 || phAnimRunning.getVelocityY() != 0)
								checkStopMovingAnimation(runningAnim);
							
						} else if (displayLog) Log.d("Clementime", className + "/onUpdate(): running animation " + runningAnim.id + " stopped or static");
						
						if (runningAnim.isAnimationRunning()) {
							if (runningAnim.toChase) {
								chaseAnim = true;
								pointToChase = runningAnim.getX() + runningAnim.staticCenterX;
								
								if (displayLog) Log.d("Clementime", className + "/onUpdate(): running animation is chased");
							} else if (displayLog) Log.d("Clementime", className + "/onUpdate(): running animation isn't chased");
							
						} else runningAnim = null;

					} else {							
					
						if (pendingTriggerId != 0 && !talk.background.isVisible() && doll.ph.getVelocityX() == 0) {
							if (!inventory.zoomView.isVisible()) launchTrigger(pendingTriggerId, false);						
						} else if (pendingTriggerId == 0 && !talk.background.isVisible() && doll.ph.getVelocityX() == 0) {
							status = STATUS_ACTION;
							mode = MODE_ACTION_WALK;
						}
					}
				}
				
				// TODO: find why mode is leaving anim_running during animation		
				if (touchedExit != null) {
//					if (touchedExit.beforeTrigger != 0) {
//						if (pendingTriggerId != 0) launchTrigger(pendingTriggerId, false);
//						else if (simultaneousTriggerId != 0) launchTrigger(simultaneousTriggerId, true);
//						else goToNextScreen();									
//					}
				}
				
				if (displayLog) Log.d("Clementime", className + "/onUpdate(): walking " + doll.walking.getX() + " - idle " + doll.idle.getX());
				
				//***************************************************************
				//     CHASE ANIM or DOLL with camera, move everything needed
				//***************************************************************
				if ((doll.ph.getVelocityX() != 0 && !checkStopDoll(pSecondsElapsed)) || chaseAnim) {	
					
					if (doll.isChased) pointToChase = doll.walking.getX() + doll.staticCenterX; // chase doll
						
		    		//************************************
		    		//    CHASE DOLL or ANIM
		    		//************************************			
					if (pointToChase != -1 && pointToChase > minChasingX && pointToChase < maxChasingX) {
					
						camera.setCenter(pointToChase, CAMERA_HEIGHT / 2);
						
						//****************************************
						//     MOVE SPRITES WITH CAMERA
						//****************************************
						setToolsInPosition();					
					}		
				}				
			}
		});	
	}
	/**
	 * When the doll is moving, checkStopDoll() checks if the doll must stop or not, depending on the current action (simple walking, continuous walking, taking, etc...).
	 * When the doll stops, actions are done, if needed, as taking, talking or going to next screen by an exit.
	 * Be careful, this method is called inside the loop. That is why the displaying of the log is delayed in this method. 
	 * @param	pSecondsElapsed	time since last update on screen 
	 */
	private boolean checkStopDoll(float pSecondsElapsed) {
		
		// if doll is moving, it can be stopped at runtime without player interaction if:
		// it reaches borders or it reaches touchedX (if moving arrows aren't activated)
		
		boolean stop = false;
		float stopX = 0;
		
		// DELAY log displayed in loop
		//*****************************
		// TODO: put it after if (LOG_ON)
		if (lastLog >= nextLog) {
			displayLog = true;
			nextLog = nextLog + LOOP_LOG_INTERVAL;
		} else {
			lastLog += pSecondsElapsed;
			displayLog = false;					
		}
		
		if (displayLog) Log.d("Clementime", className + "/checkStopDoll(): status " + status + " - mode " + mode + " - click " + clickCheck);

		//************************************
		//    CHECK IF DOLL HAS TO STOP
		//************************************
		
		// if moving arrows are playing, stop doll when reaching borders (if not during an animation that allows going outside borders)
    	if ((doll.walking.getX() < currentBg.xMin || doll.walking.getX() > currentBg.xMax) && mode != MODE_ANIM_RUNNING) {	
			
    		if (LOG_ON) Log.d("Clementime", className + "/checkStopDoll(): doll reached one border");
    		
	    	doll.stop();
	    	gameTools.leftArrow.stopAnimation(4);
	    	gameTools.rightArrow.stopAnimation(4);
	    	
	    	// avoid doll being stuck on one border
	    	if (doll.walking.getX() < currentBg.xMin)			doll.setPosition(currentBg.xMin, doll.walking.getY());
	    	else if (doll.walking.getX() > currentBg.xMax)	doll.setPosition(currentBg.xMax, doll.walking.getY());

	    	// avoid that an exit doesn't work because set a little bit outside borders (it happens, yes...)
	    	if (touchedExit != null && touchedExit.beforeTrigger == 0) goToNextScreen();
	    	
	    // otherwise (>> moving arrows aren't playing)
		} else if (!gameTools.leftArrow.isAnimationRunning() && !gameTools.rightArrow.isAnimationRunning()) {
						   		
			stopX = touchedX - doll.staticCenterX;		

			// if looking or talking to an item towards which doll can't move, do action anyway
			if (mode == MODE_ANIM_ACTION && actionsManagerOutsideBorders) {

	    		if (LOG_ON) Log.d("Clementime", className + "/checkStopDoll(): doll does action (look/talk) outside borders");
				
				//TODO: where is looking? Does it disappear in the big black hole?
		    	else if (touchedAction == ACTION_TALK)	talk();
				
				gameTools.am.deactivate();
								
				touchedItem = null;
				touchedAnimation = null;
				touchedAction = 0;
				touchedArea = null;	
				
				actionsManagerOutsideBorders = false;
			}
			
			//************************************
			//    DOLL REACHED ACTION POINT
			//************************************

			// then stop it at touchedX and please do action
			if (doll.walkDirection == DIRECTION_RIGHT && doll.walking.getX() >= stopX || doll.walkDirection == DIRECTION_LEFT && doll.walking.getX() <= stopX) {   	

	    		if (LOG_ON) Log.d("Clementime", className + "/checkStopDoll(): doll reached action point and stops");
	    		
		    	doll.stop();
		    	gameTools.leftArrow.stopAnimation(4);
		    	gameTools.rightArrow.stopAnimation(4);
	    		
		    	// reasons why doll is stopping (other than simple moving on screen)
		    	if (mode == MODE_ANIM_ACTION) {
		    		
		    		if (LOG_ON) Log.d("Clementime", className + "/checkStopDoll(): doll reached action point in mode anim/action, now does an action");
					
			    	if (touchedExit != null && touchedExit.beforeTrigger == 0) goToNextScreen();
			    	else if (touchedAction == ACTION_TAKE) 	take();
					else if (touchedAction == ACTION_TALK)	talk();
					
					gameTools.am.deactivate();
					
					if (itemToBeRemoved != null) {								
						currentBg.hideShowItem(scene, itemToBeRemoved);
						touchedZoomItem = inventory.addItem(itemToBeRemoved.id, mEngine, scene);
						
						status = STATUS_INVENTORY;
						mode = MODE_INVENTORY_ZOOM;
						inventory.displayZoomView(camera.getMinX(), touchedZoomItem, scene);
						
						itemToBeRemoved = null;
					}
					
					touchedItem = null;
					touchedAnimation = null;
					touchedAction = 0;
					touchedArea = null;		    	
				}
				
			}
		}		
		
		if (doll.ph.getVelocityX() == 0 && doll.ph.getVelocityY() == 0) stop = true;
		
		return stop;
	}		
	/**
	 * AndEngine one-run method, at start.
	 */
	@Override
	public void onLoadComplete() {
		
	}
	
	/* ************************************ */
	/*  LOAD NEW SCREEN                     */
	/* ************************************ */	
	
	/**
	 * Load a new screen when starting game or leaving screen for another by an exit.<p> 
	 * 
	 * This method retrieve doll starting position from database
	 * and create a new background (screen, items, animations...)
	 * without displaying it yet. <p>
	 * 
	 * First, whatever the case, it looks for:</br>
	 * - player last position (player carry on playing a previous game),<p>
	 * 
	 * But if doll comes from another screen by an exit, this starting position is updated by:</br>
	 * - new screen starting position,<p>
	 * 
	 * If not, and if there is no previous position (= x < -300), this is a new game, and method retrieve:</br>
	 * - first screen starting position.
	 */	
	private void loadNewScreen() {
		
		if (LOG_ON) Log.i("Clementime", className + "/loadNewScreen()");

		//******************************************************************************
		// items, areas, animations and talk screen chars (scene depending objects)
		//*****************************************************************************
		//screenFeatures = world.getScreenFeatures(lastExitId);
		
		// get position of doll from player table - on x beginning position is set to -1
		startingPosition = doll.getStartingPosition();

		if (LOG_ON) Log.i("Clementime", className + "************ START: " + startingPosition[0]);
		
		// get new position from database if:
		// 1. doll comes from another exit (exit >= 1)
		// 2. game begins (exit=0 & x beginning position isn't set by anterior playing => -1)
		if (touchedExit != null) {
			startingPosition[0] = touchedExit.startingX; 
			startingPosition[1] = touchedExit.startingY; 
			createNewBackground(touchedExit.toScreen);
			
			if (LOG_ON) Log.i("Clementime", className + "/loadNewScreen(): enter a new screen from 'last' exit: " + touchedExit.id);
		} else if (startingPosition[0] <= -300) {
			int[] features = world.getFirstScreenFeatures();
			startingPosition[0] = features[1]; 
			startingPosition[1] = features[2];
			firstScreenTriggerId = features[0];
			createNewBackground(1);
			
			if (LOG_ON) Log.i("Clementime", className + "/loadNewScreen(): starting new game ");
//		} else if(touchedMapItem != null) {
//			int[] features = world.getScreenFeatures(world.screenId);
//			startingPosition[0] = features[1]; 
//			startingPosition[1] = features[2]; 
//			firstScreenTriggerId = features[0];
//			
//			touchedMapItem = null;
//			createNewBackground(Integer.parseInt(touchedMapItem.getUserData().toString()));
			
		} else {
			createNewBackground(doll.getScreen());
			
			if (LOG_ON) Log.i("Clementime", className + "/loadNewScreen(): player in screen: " + nextBg.screenId);
		}

		// do not remove float conversion. if you do you'll obtain a zero result
		startingPosition[0] = (int)((float)startingPosition[0] * SCALE_POSITION); 
		startingPosition[1] = (int)((float)startingPosition[1] * SCALE_POSITION);
	}
	/**
	 * Creates a new background object.
	 * @param	screenId	id of the newly created screen 
	 */
	private void createNewBackground(int screenId) {	
		nextBg = new Background(dbh, context, screenId, mEngine, scene, gameTools.am.exitLeftTR, gameTools.am.exitRightTR);	
	}
	/**
	 * Display a new screen. Everything was loaded previously in loadNewScreen() method.
	 */
	public void initNewScreen() {
		
		if (LOG_ON) Log.i("Clementime", className + "/initNewScreen()");
		
		deactivateTouchEvents = true; // do not do anything during scene initialisation
		
		try {
			// hide objects when changing screen
			showPersistentObjects(false);

			doll.getYVelocity(currentBg.screenId);	// set standard velocity for current screen
			
			scene.setOnSceneTouchListener(this);
			scene.setOnAreaTouchListener(this);
			
			// variables to chase doll or animation with the camera
			minChasingX = CAMERA_WIDTH / 2;
			maxChasingX = currentBg.bgImage.getWidth() - CAMERA_WIDTH / 2;
			
			/*
			/* set persistent objects in position
			/********************************************/	
			doll.setPosition(startingPosition[0], startingPosition[1] - SET_BACKGROUND_POSITION_Y + MARGIN_Y);
			
	    	// avoid doll starting outside borders if database information is not completely right
	    	if (doll.walking.getX() < currentBg.xMin)			doll.setPosition(currentBg.xMin, doll.walking.getY());
	    	else if (doll.walking.getX() > currentBg.xMax)		doll.setPosition(currentBg.xMax, doll.walking.getY());
			
			world.calculateWalkArea(startingPosition[1] - SET_BACKGROUND_POSITION_Y + doll.walking.getHeight() + MARGIN_Y);
			
			// set camera focus on doll at start
			if (startingPosition[0] >= currentBg.bgImage.getWidth() - CAMERA_WIDTH/2)	camera.setCenter(currentBg.bgImage.getWidth() - CAMERA_WIDTH/2, CAMERA_HEIGHT/2);
			else if (startingPosition[0] <= CAMERA_WIDTH/2)								camera.setCenter(CAMERA_WIDTH/2, CAMERA_HEIGHT/2);			
			else			 															camera.setCenter(startingPosition[0] + doll.staticCenterX, CAMERA_HEIGHT/2);

			setToolsInPosition();

			showPersistentObjects(true);
			currentBg.showStaticAnims(scene);
			currentBg.showExits(scene);
			
			// launch starting trigger if needed at the beginning of new screen
			if (touchedExit != null) {
				if (touchedExit.afterTrigger != 0) launchTrigger(touchedExit.afterTrigger, false);
				touchedExit = null;				
			} else if (firstScreenTriggerId != 0) {
				launchTrigger(firstScreenTriggerId, false);
				firstScreenTriggerId = 0;
			} else {
				status = STATUS_ACTION;
				mode = MODE_ACTION_WALK;
			}

			// sort ZIndex in order to display sprites in back, middle, or foreground.
			scene.sortChildren();	
			
			// !!!! these 3 lines avoid a strange behaviour of AndEngine: latency when first setVisible(true) of walking image		
			doll.walking.setVisible(true);
			doll.walking.setPosition(-500, -1000);
			doll.firstRun = true;
			// TODO: maybe better to change method showPersistentObjects() instead

		} catch (Exception e) {
			Log.e("Clementime", className + "/initNewScreen(): raise error: " + e.getMessage());
		}
		
		deactivateTouchEvents = false;
	}	
	/**
	 * Set game tools on screen depending on the position of the doll & camera.
	 */
	private void setToolsInPosition() {

//		// move info frame with the doll
//		if (talk.background.isVisible()) talk.background.setPosition(camera.getMinX() + INFORMATION_POSX, talk.background.getY());

		// moving arrows are shown if there is a possibility to travel within the screen
		gameTools.checkBorders(camera.getMinX(), camera.getMaxX(), currentBg.xMin, currentBg.xMax);			

//		mapSprite.setPosition(camera.getMinX() + 400, mapSprite.getY());
		
		if (DEVELOPMENT) {
			devTools.settings.setPosition(camera.getMinX() + CAMERA_WIDTH - devTools.settings.getWidth(), devTools.settings.getY());
			devTools.openX.setPosition(camera.getMinX() + CAMERA_WIDTH/2-devTools.openX.getWidth()/2, devTools.openX.getY());
		}
	}
	/**
	 * Hide or show doll & game tools.
	 */
	private void showPersistentObjects(boolean choice) {
		
		if (LOG_ON) Log.i("Clementime", className + "/showPersistentObjects(): " + choice);
		
		doll.setVisible(choice);
		gameTools.setVisible(choice);
	}

	/* ************************************ */
	/*  TOUCH DETECTION && GESTURE METHODS  */
	/* ************************************ */
	
	//*** EVENT ORDER REMINDER ***//
	// AreaTouched DOWN
	// SceneTouchEvent DOWN
	// AreaTouched UP
	// OnClick
	// SceneTouchEvent UP
	//
	// if no area is touched, AreaTouched isn't called
	// if no click happens, OnClick isn't called
	//****************************//
	
	/**
	 * onAreaTouched activates following sprites when touched: inventory items, talk bubbles, action manager, doll, moving arrows, some animations, exits, dev or setting tools, zoom.
	 */
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if (deactivateTouchEvents && LOG_ON) Log.i("Clementime", className + "/onAreaTouched(): scene initialisation - touchArea desactivated"); 
		else {
			if (LOG_ON) Log.i("Clementime", className + "/onAreaTouched(): status " + status + " - mode " + mode + " - click " + clickCheck + " - touch " + pTouchArea.toString());
			
			try {	
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					
					//**************************************** !!! DO NOT remove from ACTION_DOWN !!!
					//   INVENTORY (to zoom or move an item)
					//****************************************
					if (pTouchArea instanceof InventoryItem) selectInventoryItem((InventoryItem)pTouchArea);					

				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		    		//************************************
		    		//     CLOSE MAP
		    		//************************************
					//TODO: remove map
					if (status == STATUS_MAP && pTouchArea instanceof Sprite) closeMap();

		    		//************************************
		    		//     CLOSE TALK
		    		//************************************   
					if (status == STATUS_ANIM && pTouchArea == talk.background) closeTalk(pSceneTouchEvent.getY());
		
		    		//****************************************************************************************
		    		//     STATUS ACTION		>> doll can walk & try to take/look/talk, or open inventory
					//****************************************************************************************   
					else if (status == STATUS_ACTION) {
		
						if (pTouchArea instanceof AnimatedSprite) {		

							//**************************************************************************************
							//     ACTION MANAGER	>> move doll towards item/anim if talk/take and inside borders
							//**************************************************************************************
							if (pTouchArea == gameTools.am.take) doSingleAction(ACTION_TAKE);
							else if (pTouchArea == gameTools.am.look) doSingleAction(ACTION_LOOK);
							else if (pTouchArea == gameTools.am.talk) doSingleAction(ACTION_TALK);
							else if (pTouchArea == gameTools.am.exitLeft) doSingleAction(ACTION_EXIT);
							else if (pTouchArea == gameTools.am.exitRight) doSingleAction(ACTION_EXIT);
						
							//************************************
				    		//     DOLL		>> OPEN INVENTORY
				    		//************************************
							else if (pTouchArea == doll.walking) 	openInventory();

							//***********************************
							//		MOVING ARROWS
							//***********************************						
							else if (pTouchArea == gameTools.leftArrow)		activateMovingArrow(DIRECTION_LEFT);
							else if (pTouchArea == gameTools.rightArrow)	activateMovingArrow(DIRECTION_RIGHT);
							
							//**********************************************
				    		//     ACTION MANAGER on CHARACTER or ANIM
							//**********************************************							
							else if (pTouchArea instanceof Anim) activateActionOnAnim((Anim)pTouchArea);
	
							//***********************************
							//     EXIT
							//***********************************
							else if (pTouchArea instanceof Exit) {
									mode = MODE_ANIM_ACTION;
									touchedExit = (Exit)pTouchArea;
									touchedX = touchedExit.getX() + touchedExit.getWidth()/2;
							}					
						//******************************************************
			    		//     SCREEN ITEM >> activate ACTION MANAGER on ITEM
						//******************************************************
						} else if (pTouchArea instanceof ScreenItem) activateActionOnItem((ScreenItem)pTouchArea);
							
						else if (pTouchArea instanceof Sprite) {						

				    		//************************************
				    		//     OPEN DEV
				    		//************************************
						    if (pTouchArea == devTools.settings) {
						    	dbh.deleteDatabase();
						    	this.finish();
						    }
				    		//************************************
				    		//     OPEN MAP
				    		//************************************
//						    else if (pTouchArea == mapSprite) {
//					    		status = STATUS_MAP;
//					    		map.background.setPosition(camera.getMinX(), map.background.getY());
//					    		map.display();
//							} 
						}
					}
		    		//************************************
		    		//     CLOSE ZOOM
		    		//************************************
					else if (pTouchArea == inventory.zoomView && inventory.zoomView.isVisible()) clickCheck = CLICK_ZOOM;

		    		//**********************************************
		    		//     OPEN/CLOSE DEV TOOLS (only in dev mode)
		    		//**********************************************
					if (pTouchArea == devTools.openX) openCloseDevTools();
				}
			} catch (Exception e) {
				Log.e("Clementime", className + "/onAreaTouched(): raise error: " + e.getMessage());
			}
		}
		return false;
	}
	/**
	 * Used for player actions that do not involved a sprite, for things happening after a TouchArea event, or for cleaning variables after an event.
	 * <p>
	 *  > Player actions that do not involved a sprite: dragging and dropping inventory items, looking an area on screen or walking,</br>
	 *  > Things happening after a TouchArea event: doing actions with action manager or closing inventory,</br>
	 *  > cleaning variables if needed: changing status and mode, deactivating game tools or stopping doll.<p>
	 * onSceneTouchEvent() is activated after onAreaTouched().
	 */
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		clickDetector.onSceneTouchEvent(null, pSceneTouchEvent);
		
		if (deactivateTouchEvents && LOG_ON) Log.i("Clementime", className + "/onSceneTouchEvent(): scene initialisation - sceneTouchEvent desactivated"); 
		else {
			
			if (LOG_ON) Log.d("Clementime", className + "/onSceneTouchEvent(): status " + status + " - mode " + mode + " - click " + clickCheck);
			
			try {
				
				//**********************************************
				//     INVENTORY ITEM zooming and dropping
				//********************************************** 
				if (status == STATUS_INVENTORY && touchedInventoryItem != null && touchedInventoryItem != touchedZoomItem) {
					
					inventoryDragAndDrop(pSceneTouchEvent);			
 
				} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
					
					//******************************************
					//     TOUCH SCREEN IN ACTION MODE
					//******************************************
					if (status == STATUS_ACTION)	manageAction(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());

					//******************************************
					//     CLOSE INVENTORY
					//******************************************
					else if (status == STATUS_INVENTORY && mode == MODE_INVENTORY_OPEN) {
						// zoom just closed, come back to normal inventory mode, switch clickCheck from ZOOM to OFF
						if (clickCheck == CLICK_ZOOM && inventory.normalView.isVisible()) clickCheck = CLICK_OFF;
						else closeInventory();
					}

					
		    		// CLOSE MAP
//					else if (status == STATUS_MAP && touchedMapItem != null) {
//						goToNextScreen();
//					}
					else { 
						gameTools.am.deactivate();
				    	gameTools.leftArrow.stopAnimation(4);
				    	gameTools.rightArrow.stopAnimation(4);
		
						if (status == STATUS_ANIM) {
					    	// --------------- IF an animation is not running ---------------
					    	// talk has been recently closed, come back to mode screen here
					    	// (not before to avoid touch reacting to areas when closing talk) 
							if (mode != MODE_ANIM_RUNNING && mode != MODE_ANIM_ACTION && !talk.background.isVisible() && !inventory.zoomView.isVisible()) {
								status = STATUS_ACTION;
								mode = MODE_ACTION_WALK;						
							}					
						} else doll.stop();					
					}
				}
			} catch (Exception e) {
				Log.e("Clementime", className + "/onSceneTouchEvent(): raise error: " + e.getMessage());
			}
		}
		return true;
	}
	/**
	 * Manages click i.e. zooming on item in inventory and close zoom.
	 */
	@Override
	public void onClick(final ClickDetector pClickDetector, final TouchEvent pTouchEvent) {

		if (deactivateTouchEvents && LOG_ON) Log.i("Clementime", className + "/onClick(): scene initialisation - click desactivated"); 
		else {
			try {
				
				if (clickCheck == CLICK_INVENTORY)				zoomOnItem();
				else if (clickCheck == CLICK_AGAIN_INVENTORY)	switchZoomedItem();
				else if (clickCheck == CLICK_ZOOM)				closeZoom();
				
				else if (LOG_ON) Log.i("Clementime", className + "/onClick(): clic outside inventory");
				
			} catch (Exception e) {
				Log.e("Clementime", className + "/onClick(): raise error: " + e.getMessage());
			}
		}
	}
	/**
	 * Manages accelerometer i.e. looking for inventory items when there are too many to fit screen. 
	 */
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		inventory.moveItemList(camera.getMinX(), pAccelerometerData.getX());
	}
	
	
	/**************************************/
	/* ON AREA/ON SCENE TOUCH SUB METHODS */
	/* ************************************ */
	/*  TOUCH SUBMETHODS                    */
	/* ************************************ */
	
	/**
	 * When there is only one action, don't show action manager (to remove, probably).
	 */
	private void doSingleAction(int action) {
	
		status = STATUS_ANIM;
		mode = MODE_ANIM_ACTION;

		if (LOG_ON) {
			Log.i("Clementime", className + "/doSingleAction(): status " + status + " - mode " + mode + " - click " + clickCheck);
			Log.d("Clementime", className + "/doSingleAction(): action " + action);
		}
		
		touchedAction = action;
		if (touchedX > currentBg.xMin && touchedX < currentBg.xMax && touchedAction != ACTION_LOOK) doll.move(status, touchedX);
		else if (touchedAction == ACTION_LOOK) 	look();
		else {
			actionsManagerOutsideBorders = true;	
			if (touchedAction == ACTION_TAKE) doll.sayNo();
			if (touchedAction == ACTION_LOOK) talk();
		}
	}
	/**
	 * SubMethod of onSceneTouchEvent() method. Do one of the following:</br>
	 *  > freeze action manager and move doll towards item if doll is going to take an item, towards character if doll is going to talk to somebody, or towards exit,</br>
	 *  > check if there is something to look in the background (screen area),</br>
	 *  > let doll starting walking (to a point or continuously, it will be checked after by checkStopDoll() method),</br>
	 *  > show pointer.
	 * @param	x	x touched by the player (on the whole AndEngine scene, which means on the whole background image, not local x on screen)
	 * @param	y	y touched by the player (for this application = local y because there is no way to scroll vertically)
	 * @see #onSceneTouchEvent
	 */
	private void manageAction(float x, float y) {

		if (LOG_ON) Log.d("Clementime", className + "/manageAction(): status " + status + " - mode " + mode);
		
		//**************************
		//     ACTION
		//**************************
		// player activate an action or exit
		if (mode == MODE_ANIM_ACTION) {
			if (touchedAction == ACTION_LOOK)	{
				gameTools.am.freeze(ACTION_LOOK);
				look();
			}
			else if (touchedAction == ACTION_TAKE)		gameTools.am.freeze(ACTION_TAKE);
			else if (touchedAction == ACTION_TALK)		gameTools.am.freeze(ACTION_TALK);
			else { // exit
				
				touchedExit.stopAnimation();
		    	gameTools.leftArrow.stopAnimation(4);
		    	gameTools.rightArrow.stopAnimation(4);

				if (touchedExit.beforeTrigger == 0) doll.move(status, touchedX); // no trigger on exit, move doll until 
				else launchTrigger(touchedExit.beforeTrigger, false);
			}
			
			if (touchedAction == ACTION_TALK) touchedX = touchedAnimation.getX() - TALK_DISTANCE;
			else touchedX = x;
		//*********************************
		//     SPECIAL AREAS ON SCREEN
		//*********************************
		// player touch the screen - check if an area is touched
		} else if (clickCheck != CLICK_DOLL) {

			Area area = null;
			
			area = currentBg.checkAreas(x, y);

			if (area != null) {
				if (LOG_ON) Log.d("Clementime", className + "/onSceneTouchEvent(): activated area: " + area.id);
				
				mode = MODE_ACTION_WAIT;
				touchedX = area.x + area.width/2;
				
				int actions = gameTools.am.activate(area.x, area.y, area.width, area.height, currentBg.getAreaStates(area.id), true);
				
				if (actions == ACTION_TAKE) doSingleAction(ACTION_TAKE);
				if (actions == ACTION_LOOK) doSingleAction(ACTION_LOOK);
				if (actions == ACTION_TALK) doSingleAction(ACTION_TALK);
				else if (actions > 0) mode = MODE_ACTION_WAIT;
				
				touchedItem = null;
				touchedAnimation = null;
				touchedArea = area;
			}
		}

		//************************************
		//     SIMPLE MOVE DOLL
		//************************************
		// player touch the screen - walk is activated if in touch is in walk area
		if (mode == MODE_ACTION_WALK) {

			gameTools.am.deactivate();
				
			// avoid doll stopping if moving arrow just pressed
			if (!movingArrowPressed) {
		    	gameTools.leftArrow.stopAnimation(4);
		    	gameTools.rightArrow.stopAnimation(4);
				touchedX = x;
				
				// avoid doll moving when touching it
				if (clickCheck == CLICK_DOLL)	clickCheck = CLICK_OFF;
				
				// if not, check if walk area was touched and if yes, move doll until touched point
				else if (world.checkWalkArea(currentBg.xMin, currentBg.xMax, x, y)) {
					gameTools.pointer = POINTER_WALK;
					doll.move(status, touchedX);						
				}
				
			} else movingArrowPressed = false;		
			
		} else {
	    	gameTools.leftArrow.stopAnimation(4);
	    	gameTools.rightArrow.stopAnimation(4);
			
			if (mode == MODE_ACTION_WAIT) mode = MODE_ACTION_WALK;					
		}	
		
		//************************************
		//     POINTER
		//************************************ 
		// show pointer when status is ACTION only, walking pointer in walking area, 
		// doll pointer when touching doll, and default pointer in all other cases
		gameTools.showPointer(x, y);
	}
	/**
	 * SubMethod of onSceneTouchEvent() method, allows drag and drop of inventory items and combination with another inventory item or with a screen item. 
	 * @param	pSceneTouchEvent	Android/AndEngine touchEvent (up, down, touched x & y, etc...) 
	 * @see		#onSceneTouchEvent
	 */
	private void inventoryDragAndDrop(TouchEvent pSceneTouchEvent) {
		
		if (LOG_ON) Log.i("Clementime", className + "/inventoryDragAndDrop()"); 
		
		//**********************************************
		//     INVENTORY ITEM zooming and dropping
		//********************************************** 
    	// coordinates are set from top left to centre of the sprite
		float x = pSceneTouchEvent.getX() - touchedInventoryItem.big.getWidth() / 2  - BIG_ITEM_POSITION * PLAYING_HAND;
    	float y = pSceneTouchEvent.getY() - touchedInventoryItem.big.getHeight() / 2;
    	       	
    	//TODO: avoid mode drop when click zoom & dropping
    	
    	// mode DROP launched when an item is dragged outside the "list" of inventory items
    	if (mode == MODE_INVENTORY_OPEN && (pSceneTouchEvent.getY() < INVENTORY_POSY_NORMALVIEW
    								|| pSceneTouchEvent.getX() > camera.getMinX() + inventory.items.size() * inventory.boxPlusInterval)) {
    		mode = MODE_INVENTORY_DROP;
    		hideInventory();
   		}
    	
		switch(pSceneTouchEvent.getAction()) {
		
        case TouchEvent.ACTION_DOWN:   
        	downTime = pSceneTouchEvent.getMotionEvent().getDownTime();
        	
        	touchedInventoryItem.setAlpha(INVBOX_ALPHA_LAYER);
        	touchedInventoryItem.small.setAlpha(INV_ALPHA_LAYER);       		
        	touchedInventoryItem.big.setVisible(true);

        	break;
        case TouchEvent.ACTION_MOVE:
        	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200 && pSceneTouchEvent.getMotionEvent().getEventTime() < downTime + 400) {
            	touchedInventoryItem.big.setZIndex(ZINDEX_INV_ITEM_IN_USE);
        		scene.sortChildren();
        	}
        	if (pSceneTouchEvent.getMotionEvent().getEventTime() > downTime + 200) touchedInventoryItem.big.setPosition(x, y);
        	
            break;
            
		//********************************************************
		//     COMBINATIONS: item dropped on something else
		//********************************************************  
        case TouchEvent.ACTION_UP:
        	
    		int[] combinationResult = {0,0,0,0,0};	        	
        	
    		//************************************
    		//     ITEM DROPPED ON SCREEN
    		//************************************        		
    		if (mode == MODE_INVENTORY_DROP) {

	        	ListIterator<ScreenItem> it = currentBg.items.listIterator();
	        	
	        	boolean lookForCombination = true;
				
	    		while(it.hasNext() && lookForCombination) {
	    			
	    			ScreenItem item = it.next();
	    			
	    			if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, item)) {   	    		

	    				//********************************************************************************************************
	    				// results table:
	    				// results[0]: combination        - id combination if combination ok, 0 if no combination		
	    				// results[1]: combination        - id of resulting item
	    				// results[2]: on screen creation - 0 if combination is in inventory, 1 if new item has to be created on screen
	    				//********************************************************************************************************
	    				combinationResult = world.combineItems(touchedInventoryItem.id, item.id, DB_COMBINATION_VALUE_ON_SCREEN);
	    				
		    			if (combinationResult[0] != 0) {
		    				
		    				lookForCombination = false;
		    				
		    				//******************************************** 
		    				//   LAUNCH TRIGGERS  if needed
		    				//********************************************
	    					// before removing item from inventory, in case trigger use item from inventory
		    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);

	    					ListIterator<Integer> itTriggers = triggers.listIterator();
	    					
	    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(), false);
		    			}		    			

		    			if (combinationResult[1] != 0)	{
		    				//************************************** 
		    				//   REMOVE ITEMS if needed
		    				//**************************************		    				
	    					inventory.hideItem(touchedInventoryItem);

		    				//************************************** 
		    				//   CREATE NEW ITEM if needed
		    				//************************************** 			    				
		    				// a combination on screen can change an item on screen or inventory (or just launch a trigger, if combinationResult[1] == 0)
		    				if (combinationResult[2] == DB_COMBINATION_VALUE_ON_SCREEN) {
		    					//TODO: doesn't work
			    				currentBg.addItemOnScreen(combinationResult[1], mEngine, scene);			    					
		    				} else {
			    				touchedZoomItem  = inventory.addItem(combinationResult[1], mEngine, scene); // should be done after redrawing
				    			inventory.displayZoomView(camera.getMinX(), touchedZoomItem, scene);
		    				}			    					
			    		}
		    		}  			
	    		}

    			
	        	clickCheck = CLICK_OFF;
	        	if (status != STATUS_ANIM) {
	        		status = STATUS_ACTION;
	    			mode = MODE_ACTION_WALK;
	        	}
	        	
	       	//************************************
        	//   ITEM COMBINATION (in inventory)
        	//************************************        		    	 
        	} else if (mode == MODE_INVENTORY_ZOOM) {

	    		if (RectangularShapeCollisionChecker.checkCollision(touchedInventoryItem.big, touchedZoomItem.big)) {
	    				
	    			combinationResult = world.combineItems(touchedInventoryItem.id, touchedZoomItem.id, DB_COMBINATION_VALUE_IN_INVENTORY);
	    			if (combinationResult[0] != 0)	{
	    				
	    				// clean zoom view, detach all sprites, update db and remove items
	    				// don't detach sprites because it cause andEngine to crash
	    				inventory.zoomView.detachChildren();
    					inventory.hideItem(touchedInventoryItem);
    					inventory.hideItem(touchedZoomItem);
    												
	    				//******************************************** 
	    				//   LAUNCHING TRIGGERS  if needed
	    				//********************************************
						//TODO: check if change is working
	    				ArrayList<Integer> triggers = world.getTriggers(combinationResult[0], DB_TABLE_COMBINATION);

    					ListIterator<Integer> itTriggers = triggers.listIterator();
    					
    					while(itTriggers.hasNext())	launchTrigger(itTriggers.next(),false);							
	    				
	    				inventory.redrawInventory(); // change position of boxes							

	    				InventoryItem newItem = inventory.addItem(combinationResult[1], mEngine, scene); // should be done after redrawing
	    				// TODO: probably better inside inventory ??
						float xPos = camera.getMinX() + INVENTORY_POSX_ZOOM_ITEM - newItem.big.getWidth()/2;
						float yPos = INVENTORY_POSY_ZOOM_ITEM - newItem.big.getHeight()/2;
						newItem.big.setPosition(xPos, yPos);
						newItem.big.setVisible(true);
						
						touchedZoomItem = newItem;
	    			}
    			}
    		}

        	hideBigItem();

        	// if still positioned in CLICK_INVENTORY, clickCheck has to be reinitialised
        	// because it means that an item was moved but not clicked or used
        	if (clickCheck == CLICK_INVENTORY) clickCheck = CLICK_OFF;   
	        
        	break;
    	}
	}
	/**
	 * Activates moving arrows, i.e. launch left or right moving arrow animation and launch doll moving (will stop moving if reach a border or if player do another action only).
	 * SubMethod of onAreaTouched() method.
	 * @param	direction	left arrow or right arrow
	 */
	private void activateMovingArrow(int direction) {
		
		if (direction == DIRECTION_LEFT) {
			
			if (doll.ph.getVelocityX() != 0 && doll.walkDirection == DIRECTION_RIGHT) {
		    	doll.stop();
				gameTools.rightArrow.stopAnimation(4);
			}
			touchedX = currentBg.xMin;
			gameTools.leftArrow.animate(120, true);
			
		} else {
			
			if (doll.ph.getVelocityX() != 0 && doll.walkDirection == DIRECTION_LEFT) {
		    	doll.stop();
				gameTools.leftArrow.stopAnimation(4);
			}
			touchedX = currentBg.xMax;
			gameTools.rightArrow.animate(120, true);
		}
		doll.move(status, touchedX);
		movingArrowPressed = true;
	}
	/**
	 * To remove(?)
	 */
	private void closeMap() {

//		touchedMapItem = (Sprite)pTouchArea;
//
//		if (map.items.contains(touchedMapItem)) {					
//			map.hide();
//		}

	}
	/**
	 * Hide talking bubble at the end of a dialog.
	 * SubMethod of onAreaTouched() method.
	 */
	private void closeTalk(float x) {
		
		// avoid closing frame when touching the decorated part
//		if (x <= TALK_CLOSING_HEIGHT) {
			talk.hide();
			talk.background.detachChildren();
	        scene.unregisterTouchArea(talk.background);
//			if (touchedZoomItem != null) inventory.displayZoomView(camera.getMinX(), touchedZoomItem, scene);
//		}	
		
//		if (mode == MODE_ANIM_TALK) {
//			// check if there are relies
//			if (true) displayTalk(0);
//			else {
//				status = STATUS_ACTION;
//				mode = MODE_ACTION_WALK;
//			}
//		}
	}
	/**
	 * Stop doll then either open inventory (set corresponding inventory status and mode) or launch "say no" animation if inventory is empty.
	 * SubMethod of onAreaTouched() method.
	 */
	private void openInventory() {
		
		gameTools.pointer = POINTER_DOLL;
		clickCheck = CLICK_DOLL;
    	doll.stop();
    	gameTools.leftArrow.stopAnimation(4);
    	gameTools.rightArrow.stopAnimation(4);
    	gameTools.am.deactivate();
		
		if (!inventory.items.isEmpty()) {
			status = STATUS_INVENTORY;
			mode = MODE_INVENTORY_OPEN;
			displayInventory();
		} else doll.sayNo();
	}
	/**
	 * Close inventory and come back to walk status and mode.
	 * SubMethod of onAreaTouched() method.
	 */
	private void closeInventory() {
		
		// avoid closing inventory right after opening it
		if (clickCheck != CLICK_DOLL) {
			status = STATUS_ACTION;
			mode = MODE_ACTION_WALK;
			hideInventory();								
		} else clickCheck = CLICK_OFF;
	}
	/**
	 * When an animation is touched, open action manager with actions matching this animation (if there are).
	 * SubMethod of onAreaTouched() method.
	 */
	private void activateActionOnAnim(Anim anim) {
		
		if (LOG_ON) Log.d("Clementime", className + "/activateActionOnAnim(): anim " + anim.toString()); 
		
		touchedItem = null;
		touchedArea = null;
		touchedAnimation = anim;
		touchedX = touchedAnimation.getX() + touchedAnimation.staticCenterX;
		
		int actions = gameTools.am.activate(touchedAnimation.getX(), touchedAnimation.getY(), touchedAnimation.getWidth(), touchedAnimation.getHeight(),
				               currentBg.getAnimStates(touchedAnimation.id), false);
		
		if (LOG_ON) Log.i("Clementime", className + "/activateActionOnAnim(): actions = " + actions);
		
		if (actions == ACTION_TAKE) doSingleAction(ACTION_TAKE);
		if (actions == ACTION_LOOK) doSingleAction(ACTION_LOOK);
		if (actions == ACTION_TALK) doSingleAction(ACTION_TALK);
		else if (actions > 0) mode = MODE_ACTION_WAIT;
	}
	/**
	 * When an item is touched, open action manager with actions matching this animation (if there are).
	 * SubMethod of onAreaTouched() method.
	 */
	private void activateActionOnItem(ScreenItem screenItem) {
		
		touchedAnimation = null;
		touchedArea = null;
		touchedItem = screenItem;
		touchedX = touchedItem.getX() + touchedItem.getWidth()/2;

		int actions = gameTools.am.activate(touchedItem.getX(), touchedItem.getY(), touchedItem.getWidth(), touchedItem.getHeight(),
				               currentBg.getItemStates(touchedItem.id), false);
		
		if (LOG_ON) Log.i("Clementime", className + "/activateActionOnItem(): actions = " + actions);
		
		if (actions == ACTION_TAKE) doSingleAction(ACTION_TAKE);
		if (actions == ACTION_LOOK) doSingleAction(ACTION_LOOK);
		if (actions == ACTION_TALK) doSingleAction(ACTION_TALK);
		else if (actions > 0) mode = MODE_ACTION_WAIT;
	}
	/**
	 * In inventory, mark item as selected for zooming or dragging and dropping.
	 * SubMethod of onAreaTouched() method.
	 */
	private void selectInventoryItem(InventoryItem item) {
		if (mode == MODE_INVENTORY_OPEN || mode == MODE_INVENTORY_ZOOM) {
			touchedInventoryItem = item;

			if (mode == MODE_INVENTORY_OPEN) clickCheck = CLICK_INVENTORY;
			// accept consecutive clicks
			else clickCheck = CLICK_AGAIN_INVENTORY;
		}	
	}
	/**
	 * Display development screen (loading and saving).
	 * SubMethod of onAreaTouched() method.
	 */
	private void openCloseDevTools() {
				
		if (LOG_ON) Log.i("Clementime", className + "/openCloseDevTools()");
		
    	if (!devTools.mask.isVisible()) {
	        scene.setChildScene(devTools);
	        devTools.display();	    		
    	} else {
			world.save(currentBg.screenId, doll.walking.getX(), doll.walking.getY());
    		load = devTools.hide();
    		if (load > 0) {
    			backup.setLoad(load);
		    	Intent refresh = new Intent(this, Screen.class);
		    	startActivity(refresh);
		    	this.finish();
    		}
    	}
	}

	/* ************************************ */
	/*  ON CLICK SUB METHODS & INVENTORY    */
	/* ************************************ */
	
	/**
	 * Displays inventory and enables accelerometer.
	 */
	public void displayInventory() {

		if (LOG_ON) Log.i("Clementime", className + "/displayInventory()");
		
		this.enableAccelerometerSensor(this);
		inventory.display(camera.getMinX(), scene);
	}
	/**
	 * Hides inventory and disables accelerometer.
	 */
	public void hideInventory() {
		
		if (LOG_ON) Log.i("Clementime", className + "/hideInventory()");
		
		this.disableAccelerometerSensor();
		inventory.hide();	
	}
	/**
	 * Displays zoom in inventory when player clicks on an item.
	 */
	private void zoomOnItem() {
		
		if (LOG_ON) Log.i("Clementime", className + "/ZoomOnItem()");
		
		mode = MODE_INVENTORY_ZOOM;
		touchedZoomItem = touchedInventoryItem;
		inventory.displayZoomView(camera.getMinX(), touchedZoomItem, scene);
		
		clickCheck = CLICK_OFF;	
	}
	/**
	 * Displays another item in zoom when zoom is already open and player clicks on another item.
	 */
	private void switchZoomedItem() {
		// accept consecutive clicks (= zoom again when already in zoom mode)
		
		if (LOG_ON) Log.i("Clementime", className + "/onClick(): clic again on inventory");

		// clean zoom image before drawing another
		touchedZoomItem.big.setPosition(-200, 0);
		touchedZoomItem.setAlpha(1);
		touchedZoomItem.small.setAlpha(1);
		touchedZoomItem = touchedInventoryItem;
		
		inventory.displayZoomItem(camera.getMinX(), touchedZoomItem);

		clickCheck = CLICK_OFF;
		
	}
	/**
	 * Hides zoom in inventory when player clicks on zoom area.
	 */
	private void closeZoom() {
		
		if (LOG_ON) Log.i("Clementime", className + "/closeZoom()");

    	if (status == STATUS_INVENTORY) mode = MODE_INVENTORY_OPEN;	
		inventory.hideZoomView(touchedZoomItem, scene);
		touchedInventoryItem = null;
		touchedZoomItem = null;
		//clickCheck = CLICK_OFF;
	
	}
	/**
	 * Hides big item when player releases dragging.
	 */
	private void hideBigItem() {
		
		if (LOG_ON) Log.i("Clementime", className + "/hideBigItem()");
		
    	touchedInventoryItem.big.setVisible(false);
       	touchedInventoryItem.big.setPosition(-200, 0); // out of screen
       	if (touchedInventoryItem != touchedZoomItem) {
       		touchedInventoryItem.setAlpha(1);
       		touchedInventoryItem.small.setAlpha(1);
       	}
    	touchedInventoryItem.big.setZIndex(ZINDEX_INV_ITEM);
		scene.sortChildren();
       	touchedInventoryItem = null;
	}
	
	/* ************************************ */
	/*  ACTIONS (including exit)            */
	/* ************************************ */
	
	/**
	 * Launches take action (hide screen item, put it in inventory) if an item is marked as "takeable".
	 */
	private void take() {
		
		if (LOG_ON) Log.i("Clementime", className + "/take()");
		
		if (world.isItemTakeable(touchedItem.id)) itemToBeRemoved = touchedItem;
		
		mode = MODE_ACTION_WALK;
	}
	/**
	 * Stops doll and launch look action.
	 */
	private void look() {

		if (LOG_ON) Log.i("Clementime", className + "/look()");
		
//		if (touchedItem != null)
//			text = world.getDesc(touchedItem.id, OBJECT_TYPE_ITEM, DB_DESCRIPTION_ACTION_LOOK);
//		else if (touchedArea != null)
//			text = world.getDesc(touchedArea.id, OBJECT_TYPE_AREA, DB_DESCRIPTION_ACTION_AREA_LOOK);
//		else if (touchedAnimation != null)
//			text = world.getDesc(touchedAnimation.id, OBJECT_TYPE_CHAR, DB_DESCRIPTION_ACTION_CHAR_LOOK);
    	doll.stop();
		displayTalk(0);
	}
	/**
	 * Launches talk action.
	 */
	private void talk() {

		if (LOG_ON) Log.i("Clementime", className + "/talk()");

		status = STATUS_ANIM;
		mode = MODE_ANIM_TALK;
		displayTalk(0);
	}
	/**
	 * Displays talk bubble with first text of a dialog.
	 */
	private void displayTalk(int pictureId) {
		
		if (LOG_ON) Log.i("Clementime", className + "/displayTalk()");
		
		talk.background.setPosition(camera.getMinX() + TALK_POSX, talk.background.getY());
		//talk.display(pictureId, gameTools.font);
		talk.display(scene, pictureId, gameTools.font);
        scene.registerTouchArea(talk.background);
		status = STATUS_ANIM;
		//mode = MODE_ANIM_OFF;
	}
	/**
	 * Remove old sprites and load new screen objects and sprites.
	 */
	private void goToNextScreen() {
		
		if (touchedExit != null && LOG_ON) Log.i("Clementime", className + "/goToNextScreen(): from exit " + touchedExit.id);
//		else if (touchedMapItem != null) Log.i("Clementime", className + "/goToNextScreen(): from map " + touchedMapItem);
		else if (LOG_ON) Log.i("Clementime", className + "/goToNextScreen()");
		
		garbageCollector();
		
		loadNewScreen();
		
		currentBg = nextBg;
		
		gameTools.am.deactivate();
		gameTools.hidePointer();
		status = STATUS_ACTION;
		mode = MODE_ACTION_WALK;
		
		initNewScreen();

	}
	
	/* ************************************ */
	/*  ANIMS & GENERAL                     */
	/* ************************************ */
	
	/**
	 * Activates a trigger which can launch an animation, move the doll, hide the doll, launch a text, etc...<p>
	 * All information about what to do is retrieved into the results tab described below:</br>
	 * > results[0]: next trigger			- if there is another trigger launched following this one, id of the trigger, 0 if not</br> 		
	 * > results[1]: animation on screen	- if the trigger is to animate a screenAnim, id of the screeAnim</br>
	 * > results[2]: doll moving			- if the trigger is to animate the doll, x where the doll has to be moved</br>
	 * > results[3]: doll is hidden			- if doll is moving, it can be off screen</br>
	 * > results[4]: text activated			- if an animation text is activated, id of this text</br>
	 * > results[5]: add item to inventory	- id of item to add in inventory</br>
	 * > results[6]: doll moving y velocity</br>
	 * > results[7]: hide/show screen item</br>
	 * > results[8]: hide/show animation</br>
	 * > results[9]: simultaneous trigger	- if there is another trigger to launch at the same time, id of the trigger
	 * @param	triggerId		is the trigger to launch
	 * @param	simultaneous	if this trigger has to be launch simultaneously another trigger, no pendingTriggerId is set
	 */
	private void launchTrigger(int triggerId, boolean simultaneous) {

		//********************************************************************************************************

		//********************************************************************************************************
		if (LOG_ON) {
			if (simultaneous) Log.i("Clementime", className + "/launchTrigger(): launch simultaneous trigger " + triggerId);
			else Log.i("Clementime", className + "/launchTrigger(): launch pending trigger " + triggerId);
		}
		
		simultaneousTriggerId = 0;
		
		// check and launch any trigger or trigger sequence
		int[] triggerResult = world.activateTrigger(triggerId);

		if (LOG_ON) {
			Log.d("Clementime", className + "/launchTrigger(): Trigger results (trigger " + triggerId + ")");
			for (int i=0; i<triggerResult.length; i++) Log.d("Clementime", className + "/launchTrigger(): " + i + ": " + triggerResult[i]);
		}
		
		// the trigger launch an animation
		if (triggerResult[1] != 0) {
			launchAnim(triggerResult[1]);
			//animationToWaitForId = triggerResult[1];
			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
		}
			
		// the trigger move the doll (special animation)
		if (triggerResult[2] != 0) {
			if (LOG_ON) Log.d("Clementime", className + "/launchTrigger(): move Doll ");
			
			touchedX = triggerResult[2] + doll.staticCenterX; // doll.getWidth()/2 is removed when check stop doll 

			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
			
			doll.YVelocity = triggerResult[6];
			doll.move(status, touchedX);
			doll.isChased = true; // to come back to chase doll you have to move it
		}

		// the trigger display a text
		if (triggerResult[4] != 0) {
			if (LOG_ON) Log.d("Clementime", className + "/launchTrigger(): display text ");
			
			displayTalk(0);
			status = STATUS_ANIM;
			mode = MODE_ANIM_RUNNING;
		}
			
		if (triggerResult[7] != 0) currentBg.hideShowItemById(triggerResult[7], scene);
		if (triggerResult[8] != 0) currentBg.hideShowAnimation(triggerResult[8]);

		// TODO: [5] = change inventory
		if (triggerResult[5] != 0) {
			
			int pos = world.isItemDisplayed(triggerResult[5]);

			if (pos == DB_INVENTORY_VALUE_IN) {
				inventory.redrawInventory(); // change position of boxes							
				touchedZoomItem = inventory.addItem(triggerResult[5], mEngine, scene); // should be done after redrawing
			} else if (pos == DB_INVENTORY_VALUE_OUT) {
				inventory.removeItem(triggerResult[5]);
				inventory.redrawInventory();
			}
		}
		
		if (triggerResult[3] != 0)		doll.setVisible(false);
		else if (triggerResult[2] != 0)	doll.setVisible(true); // to display doll when hidden you have to move it
		
		// be careful: you can't have a pending trigger on a simultaneous trigger
		// because you can't know which pending trigger has to be triggered first
		if (!simultaneous) pendingTriggerId = triggerResult[0];

		simultaneousTriggerId = triggerResult[9];
	}
	/**
	 * Retrieves all information about an animation (number of frames, duration of frames, moving animation or not, etc...) and launches it.<p>
	 * All information about what to do is retrieved into the features tab described below:</br>
	 * features[0] = first_frame</br>
	 * features[1] = last_frame</br>
	 * features[2] = frame_duration</br>
	 * features[3] = loop</br>
	 * features[4] = x_velocity (for a moving animation)</br>
	 * features[5] = y_velocity (for a moving animation)</br>
	 * features[6] = doll_is_hidden</br>
	 * @param	animId	id of the animation to launch and display
	 */
	private void launchAnim(int animId) {
		Map<String, Integer> animFeatures = world.getAnimFeatures(animId);
		
		if (LOG_ON) Log.i("Clementime","Screen/launchanim(): launch animation " + animId);
		
		ListIterator<Anim> it = currentBg.anims.listIterator();
		
		while(it.hasNext()){
			Anim animation = it.next();

			if (animId == animation.id) {

				// to use animate function with first/last frame, you have to initialise a long[] sized by the number of frames 
				long[] frameDuration = new long[animFeatures.get("last_frame")-animFeatures.get("first_frame")+1];
				for (int i = 0; i <= animFeatures.get("last_frame") - animFeatures.get("first_frame"); i++) frameDuration[i] = animFeatures.get("frame_duration");

				if (animFeatures.get("loop") == NO_END_LOOP)	animation.animate(frameDuration, animFeatures.get("first_frame"), animFeatures.get("last_frame"), true);
				else animation.animate(frameDuration, animFeatures.get("first_frame"), animFeatures.get("last_frame"), animFeatures.get("loop"));
				
				// if static (no x velocity and no y velocity), check if x/y position has to change, stored in move_to fields
				if (animFeatures.get("x_velocity") == 0 &&  animFeatures.get("y_velocity") == 0) {
					if (animation.moveToX != 0 || animation.moveToY != 0) {
						animation.setPosition(animation.moveToX, animation.moveToY);					
					}
				// move animation on screen
				} else {
					phAnimRunning = new PhysicsHandler(animation);
					animation.registerUpdateHandler(phAnimRunning);
					phAnimRunning.setVelocity(animFeatures.get("x_velocity"), animFeatures.get("y_velocity"));
					if (animation.toChase) doll.isChased = false;			
				}

				if (animFeatures.get("doll_is_hidden") == 1) doll.setVisible(false);
				animation.setVisible(true);
								
				runningAnim = animation;
			}
		}
	}
	/**
	 * Checks if a moving animation (>> an animation getting around the screen, not only playing/running) is finished and has to be stopped (!! >> into the loop).
	 * @param	animation	animation (object, not only id) to be stopped
	 */
	
	private void checkStopMovingAnimation(Anim animation) {
		
		if (displayLog) Log.i("Clementime", className + "/checkStopMovingAnimation()");

		if ((animation.moveToX - animation.staticCenterX > 0 && animation.getX() >= animation.moveToX)
		 || (animation.moveToX - animation.staticCenterX <= 0 && animation.getX() <= animation.moveToX)) {
			
			phAnimRunning.setVelocity(0,0);
			animation.stopAnimation(animation.stopFrame);
		}
	}
	/**
	 * When changing screen, all sprites, touch areas and updatehandlers about this screen have to be removed properly.
	 * AndEngine seems not to do the cleaning completely, so to avoid problems, this is better use a clear/systematic garbage collector.
	 */
	
	private void garbageCollector() {
		
		if (LOG_ON) {
			Log.i("Clementime", className + "/garbageCollector(): List all scene children:");

			for (int i=0;i<scene.getChildCount();i++) {
				Log.i("Clementime", className + "/garbageCollector(): " + scene.getChild(i).toString());
			}
		}
		
		try {
            if (LOG_ON) Log.v("Clementime", className + "/garbageCollector(): background " + currentBg.bgImage.toString() + " removed.");
	        scene.detachChild(currentBg.bgImage);
	        mEngine.getTextureManager().unloadTexture(currentBg.bgBTA);
			
			if (currentBg.fgImage != null) {
	            if (LOG_ON) Log.v("Clementime", className + "/garbageCollector(): foreground " + currentBg.fgImage.toString() + " removed.");
	        	scene.detachChild(currentBg.fgImage);
			}
			
			
			if (!currentBg.items.isEmpty()) {
				ListIterator<ScreenItem> itItems = currentBg.items.listIterator();
				while(itItems.hasNext()){
					try {
						ScreenItem toRemove = itItems.next();
						if (LOG_ON) Log.v("Clementime", className + "/garbageCollector(): item " + toRemove.toString() + " removed.");
						scene.unregisterTouchArea(toRemove);
						scene.detachChild(toRemove);
					} catch (Exception e) {
						if (LOG_ON) Log.w("Clementime", className + "/garbageCollector(): unable to destroy sprite: " + e);
					}
				}
				currentBg.items.clear();
				mEngine.getTextureManager().unloadTexture(currentBg.itemsBTA);
			}
			
			if (!currentBg.anims.isEmpty()) {
				ListIterator<Anim> itAnims = currentBg.anims.listIterator();
				
				while(itAnims.hasNext()){
					try {
						Anim toRemove = itAnims.next();
						
						if (LOG_ON) Log.v("Clementime", className + "/garbageCollector(): item " + toRemove.toString() + " removed.");

						scene.unregisterTouchArea(toRemove);
						scene.detachChild(toRemove);
						toRemove.clearUpdateHandlers();
						
					} catch (Exception e) {
						Log.e("Clementime", className + "/garbageCollector(): unable to destroy sprite: " + e);
					}
				}
				currentBg.anims.clear();
				mEngine.getTextureManager().unloadTexture(currentBg.animsBTA);
			}

	        
//			if (!currentBg.hasNoCharacter()) {
//				ListIterator<Sprite> itChars = currentBg.chars.listIterator();
//				while(itChars.hasNext()){
//					try {
//						Sprite toRemove = itChars.next();
//						Log.v("Clementime", className + "/garbageCollector(): item " + toRemove.toString() + " removed.");
//						//BufferObjectManager.getActiveInstance().unloadBufferObject(toRemove.getVertexBuffer());
//						scene.unregisterTouchArea(toRemove);
//						scene.detachChild(toRemove);
//					} catch (Exception e) {
//						Log.w("Clementime", className + "/garbageCollector(): unable to destroy sprite: " + e);
//					}
//				}
//		        currentBg.chars.clear();
//		        mEngine.getTextureManager().unloadTexture(currentBg.charsBTA);
//			}
	        
			ListIterator<Exit> itExits = currentBg.exits.listIterator();
			
			while(itExits.hasNext()){
				try {
					Exit toRemove = itExits.next();
					if (LOG_ON) Log.v("Clementime", className + "/garbageCollector(): exit " + toRemove.toString() + " removed.");
					scene.unregisterTouchArea(toRemove);
					scene.detachChild(toRemove);
				} catch (Exception e) {
					Log.e("Clementime", className + "/garbageCollector(): unable to destroy exit: " + e);
				}
			}
	        currentBg.exits.clear(); 
	
			BufferObjectManager.getActiveInstance().clear();

			System.gc();
        } catch (Exception e) {
            Log.e("Clementime", className + "/garbageCollector(): problem during cleaning: " + e);
        }
	}

	/* ************************************ */
	/*  ANDROID GENERAL METHODS             */
	/* ************************************ */

	/**
	 * Opens the database, reloads new game or old game if necessary (backup exists only in dev).
	 */
	@Override
	public void onResume() {
		
		if (LOG_ON) Log.i("Clementime","Screen/onResume()");
		
		super.onResume();
		
		app = (RdsGame)getApplicationContext();
		dbh = app.dbHandler;
		dbh.checkNewGame();

		backup = new Backup(dbh, context);
		
		try {
			if (dbh.checkDatabase()) dbh.open();
			else {
	  			Log.e("Clementime", className + "/onResume(): cannot open database. Close RDS"); 
				this.finish();
			}		
		} catch (Exception e) {
  			Log.e("Clementime", className + "/onResume(): cannot open database. Close RDS"); 
			this.finish();		
		}
		
		//TODO: load
		if (DEVELOPMENT) {
			// information about old game to load is stored into the database. This is the only way not to erase it when launching a new game.
			if (backup.getLoad() == 2) {
				Log.i("Clementime","Screen/onResume(): --- DEV ON --- load saved version");
				backup.loadStateFiles("");
				backup.loadPlayerData("");				
			} else if (!dbh.newGame)  {
				Log.i("Clementime","Screen/onResume(): --- DEV ON --- load current version");
				backup.loadStateFiles("_current");
				backup.loadPlayerData("_current");
			} else Log.i("Clementime","Screen/onResume(): --- DEV ON ---  start a new game");	
		}
		else if (LOG_ON) { 		
			if (!dbh.newGame)	Log.i("Clementime","Screen/onResume(): keep current version");					
			else				Log.i("Clementime","Screen/onResume(): start a new game");
		}
	}
	/**
	 * Closes database, save player information or game (backup exists only in dev).
	 */
	
	@Override
	public void onPause() {
		
		if (LOG_ON) Log.i("Clementime", className + "/onPause()");
		
		super.onPause();
		if(dbh.db.isOpen()) {
			// if screen is loaded, don't change player savings info as it will cancel loading 
			if (load == 0) world.save(currentBg.screenId, doll.walking.getX(), doll.walking.getY());
			
			if (DEVELOPMENT && load == 0 && touchedExit == null) {
				backup.createStateFile("_current");
				backup.createStateFiles("_current");
				backup.savePlayerData("_current");
				Log.i("Clementime","Screen/onPause(): --- DEV ON --- save current version");
			}
			
			dbh.close();
		}
	}
}
