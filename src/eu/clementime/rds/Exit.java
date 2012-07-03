package eu.clementime.rds;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Exit extends AnimatedSprite {

	public int id;
	public int display;
	
	public int startingX;
	public int startingY;
	public int beforeTrigger;
	public int afterTrigger;
	public int toScreen;
	
	public Exit(int id, int direction, int x, int y, int display, TiledTextureRegion tr) {
		
		super(x, y, tr.getTileWidth(), tr.getTileHeight(), tr);
		
		this.id = id;
		this.display = display;
		this.setVisible(false);	
	}
}