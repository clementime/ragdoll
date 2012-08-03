package eu.clementime.rds;

import static eu.clementime.rds.Constants.SCALE_POSITION;

public class Area {

	public int id;
	public float x;
	public float y;
	public float width;
	public float height;
	public String desc;
	public int look;
	public int exit;
	
	public Area(int id, float x, float y, float width, float height) {
		
		this.id = id;
		this.x = x * SCALE_POSITION;
		this.y = y * SCALE_POSITION;
		this.width = width;
		this.height = height;
	}
}

