package screen;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import eu.clementime.rds.R;

public class Screen extends BaseGameActivity  {

	private Context context;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
	private static int CAMERA_WIDTH = 0;
	private static int CAMERA_HEIGHT = 0;
	
	private Camera camera;
	private Scene scene = new Scene();
	
    @Override
    public Engine onLoadEngine() {   	
		
		Log.i("Clementime", "Screen/onLoadEngine()");

    	context = getApplicationContext();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);

       	CAMERA_WIDTH = dm.widthPixels;
    	CAMERA_HEIGHT = dm.heightPixels;

    	camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    	EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FixedResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    	engineOptions.getTouchOptions().setRunOnUpdateThread(true);

    	
		return new Engine(engineOptions);
    }
    
	@Override
	public void onLoadResources() {	
		
		Log.i("Clementime", "Screen/onLoadResources()");
		
//		loadingBTA = new BitmapTextureAtlas(512, 512, TextureOptions.DEFAULT);
//		loadingTR = BitmapTextureAtlasTextureRegionFactory.createFromResource(loadingBTA, this, R.drawable.loading, 0, 0);
//
//		this.mEngine.getTextureManager().loadTexture(loadingBTA);	
	}	
	
	@Override
	public Scene onLoadScene() {
			
		Log.i("Clementime", "Screen/onLoadScene()");

//		scene.attachChild(new Sprite(0, 0, loadingTR));
		
		return scene;
	}
	
	@Override
	public void onLoadComplete() {
		
	}
}
