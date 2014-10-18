package game.engine;

import java.io.*;

import android.content.*;
import android.graphics.*;

public class Texture {
	private Context p_context;
	private Bitmap p_bitmap;
	
	public Texture(Context context) {
		p_context=context;
		p_bitmap=null;
	}
	
	public Texture(Texture texture) {
		p_context=texture.p_context;
		p_bitmap=texture.p_bitmap;
	}
	public Bitmap getBitmap() {
		return p_bitmap;
	}
	
	public Bitmap getBitmap(int x,int y,int width, int height) {
		return Bitmap.createBitmap(p_bitmap, x, y, width, height);
	}
	
	public boolean loadFromAsset(String filename) {
		InputStream istream=null;
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inPreferredConfig=Bitmap.Config.ARGB_8888;
		try {
			istream=p_context.getAssets().open(filename);
			p_bitmap=BitmapFactory.decodeStream(istream);
			istream.close();
		} catch (IOException e) {return false;}
		return true;
	}
	
}