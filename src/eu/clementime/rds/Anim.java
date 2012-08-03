package eu.clementime.rds;

import static eu.clementime.rds.Constants.MARGIN_Y;
import static eu.clementime.rds.Constants.SET_BACKGROUND_POSITION_Y;
import static eu.clementime.rds.Constants.SCALE_POSITION;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Anim extends AnimatedSprite {

	public int id;
	public int andEngineId;
	public int stopFrame;

	public float moveToX;
	public float moveToY;

	public boolean toChase;

	public float staticCenterX;

	public Anim(int id, int width, float x, float y, int stopFrame, float moveToX, float moveToY, boolean toChase, TiledTextureRegion tr) {

		super(x * SCALE_POSITION, y * SCALE_POSITION, tr);
		
		this.id = id;

		this.stopFrame = stopFrame;
		
		this.staticCenterX = x - width/2;
		
		this.moveToX = moveToX * SCALE_POSITION;
		this.moveToY = moveToY * SCALE_POSITION;

		this.toChase = toChase;

		this.setPosition(x, y - SET_BACKGROUND_POSITION_Y + MARGIN_Y);
		this.setVisible(false);
	}
}
