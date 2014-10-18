package game.engine;

import android.graphics.*;

public class TextPrinter {
	private Canvas p_canvas;
	private Paint p_paint;
	private float p_x,p_y;
	private float p_spacing;
	private int p_align;
	public static final int ALIGN_LEFT = 0,ALIGN_MIDDLE=1,ALIGN_RIGHT=2;
	
	public TextPrinter() {
		this(null);
	}
	
	public TextPrinter(Canvas canvas) {
		p_canvas=canvas;
		p_paint=new Paint();
		p_x=p_y=0;
		p_spacing=22;
		p_align=ALIGN_LEFT;
	}
	
	public void setCanvas(Canvas canvas) {
		p_canvas=canvas;
	}
	public void setLineSpacing(float spacing) {
		p_spacing=spacing;
	}
	public void setTextSize(float size) {
		p_paint.setTextSize(size);
	}
	public void setColor(int color) {
		p_paint.setColor(color);
	}
	public void setAlign(int align) {
		if (align>=0 && align<=2) p_align=align;
	}
	public void draw(String text,float x,float y) {
		p_x=x;p_y=y;draw(text);
	}
	public void draw(String text) {
		int x=getStringSize(text);
		float ax=p_x;
		switch (p_align) {
			case ALIGN_LEFT:
				ax=p_x;break;
			case ALIGN_MIDDLE:
				ax=p_x-x/2;break;
			case ALIGN_RIGHT:
				ax=p_x-x;break;
		}
		p_canvas.drawText(text, ax, p_y, p_paint);		
		p_y+=p_spacing;
	}	
	public int getStringSize(String text) {
		Rect rect=new Rect();
		p_paint.getTextBounds(text, 0, text.length(), rect);
		return rect.width();
	}
}