package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Anim extends AnimatedSprite {

	public int id;
	public int andEngineId;
//	public int frameDuration;
//	public int firstFrame;
//	public int lastFrame;
	public int stopFrame;
//	public int loop;

	public float x;
	public float y;
	public float moveToX;
	public float moveToY;
//	public int xVelocity;
//	public int yVelocity;
//	
//	public boolean dollIsHidden;
	public boolean toChase;
//	public int triggerId;
	
	public int width;

	public Anim(int id, int width, float x, float y, int stopFrame, float moveToX, float moveToY, boolean toChase, TiledTextureRegion tr) {
	//public Animation(int id, int frameDuration, int firstFrame, int lastFrame, int stopFrame, int loop, int width, float x, float y, float moveToX, int xVelocity, int yVelocity, boolean dollIsHidden, boolean toChase, int triggerId, TiledTextureRegion tr) {
		//super(x, y, tr.getWidth(), tr.getHeight(), tr);
		super(x, y, tr);
		
		this.id = id;

//		this.frameDuration = frameDuration;
//		this.firstFrame = firstFrame;
//		this.lastFrame = lastFrame;
		this.stopFrame = stopFrame;
//		
//		this.loop = loop;
		
		this.width = width;
		this.x = x - width/2;
		this.y = y;
		this.moveToX = moveToX;
		this.moveToY = moveToY;
//		this.xVelocity = xVelocity;
//		this.yVelocity = yVelocity;
//		
//		this.dollIsHidden = dollIsHidden;
		this.toChase = toChase;
//		this.triggerId = triggerId;
		this.setVisible(false);
	}
}
