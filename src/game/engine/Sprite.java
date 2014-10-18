package game.engine;

import android.graphics.*;

public class Sprite {
	private Canvas p_canvas;
	private Paint p_paint;
	private Engine p_engine;
	private Texture p_texture;
	private Point p_point;
	private int tx,ty,width,height;
	
	public Sprite() {
		this(null);
	}
	
	public Sprite(Engine engine) {
		p_engine=engine;
		p_canvas=null;
		p_texture=new Texture(engine);
		p_paint=new Paint();
		p_paint.setColor(Color.WHITE);
		tx=-1;ty=-1;width=0;height=0;
		p_point=new Point(0, 0);
	}
	
	public void setTexture(Texture texture) {
		p_texture=texture;
	}
	
	public void draw() {
		p_canvas=p_engine.getCavas();
		if (tx==-1 || ty==-1) {
			p_canvas.drawBitmap(p_texture.getBitmap(), p_point.x, p_point.y, p_paint);
		}
		else {
			p_canvas.drawBitmap(p_texture.getBitmap(tx, ty, width, height), 
					p_point.x, p_point.y, p_paint);
		}
	}
	
	public void setPoint(Point point) {
		p_point=point;
	}
	
	public void setPlaceInBitmap(int _tx,int _ty,int _width,int _height) {
		tx=_tx;ty=_ty;width=_width;height=_height;
	}
	
	public void setColor(int color) {
		p_paint.setColor(color);
	}
	
	public void setPaint(Paint paint) {
		p_paint=paint;
	}
	
	public Texture getTexture() {
		return p_texture;
	}
	
	public Point getPosition() {
		return p_point;
	}
}