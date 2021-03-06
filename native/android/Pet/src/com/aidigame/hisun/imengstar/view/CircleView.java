package com.aidigame.hisun.imengstar.view;

import com.aidigame.hisun.imengstar.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CircleView extends ImageView {
    int w;
	public CircleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleView(Context context) {
		super(context);
		init();
	}

	private Bitmap bitmap;
	private Rect bitmapRect = new Rect();
	private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
	private Paint paint = new Paint();
	{
		paint.setStyle(Paint.Style.STROKE);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setAntiAlias(true);// 反锯齿效果
	}
	private Bitmap mDstB = null;
	private PorterDuffXfermode xfermode = new PorterDuffXfermode(
			PorterDuff.Mode.MULTIPLY);

	private void init() {
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				setLayerType(LAYER_TYPE_SOFTWARE, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mDstB = makeDst(getMeasuredWidth(), getMeasuredHeight());
	}

	public void setImageBitmap(Bitmap path) {
		this.bitmap=path;
		invalidate();
	}

	private Bitmap makeDst(int w, int h) {
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.parseColor("#ffffffff"));
		c.drawOval(new RectF(0, 0, w, h), p);
		return bm;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=8;

		if (null == bitmap) {
			options.inSampleSize=1;
			bitmap = BitmapFactory
					.decodeResource(getResources(), R.drawable.pet_icon,options);
		}
		if (null == mDstB) {
			mDstB = makeDst(getMeasuredWidth(), getMeasuredHeight());
		}

		bitmapRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		canvas.save();
		canvas.setDrawFilter(pdf);
		canvas.drawBitmap(mDstB, 0, 0, paint);
		this.w=w;
		paint.setXfermode(xfermode);
		if(bitmap!=null)
		canvas.drawBitmap(bitmap, null, bitmapRect, paint);
		paint.setXfermode(null);
		canvas.restore();
		
	}
	/**
	 * 获取裁剪后的圆形图片
	 * 
	 * @param radius
	 *            半径
	 */
	public Bitmap getCroppedRoundBitmap(Bitmap bmp) {
		if(bmp==null||w<=0)return null;
		int radius=w/2;
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);

		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();
		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

}
