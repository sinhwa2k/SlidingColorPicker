package com.sinhwa2k.slidingcolorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;


public class ColorPickerView extends View implements OnGestureListener {

    public final static int COLOR_IDX_UP = 0;
    public final static int COLOR_IDX_DOWN = 1;

	public final static float ADD_DX = 2f;

	OnColorChange onColorChange = null;
	GestureDetector mGestureDetector;

	Paint paintCenter;
	ArrayList<String> arColor;
	ArrayList<Paint> arPaintColor;
	int colorCount = 0;
	int centerIdx = 0; //센터 칼라

	int startIdx;

	float dX = 0f; //현재 스크롤 dx
	float colorDx = 0f; //현재 색상 추출용
	float centerDx = 0f; //멈췄을 시 센터로 이동용
	float addDx = 0f; //센터 이동 dx

	float moveLeft;
	float colorW;
	float colorHalfW;
	int width;
	int height;
	int centerX;

	boolean startGoCenter = false;
	boolean doGoCenter = false;

    Bitmap bitmapLeft;
    Bitmap bitmapRight;

	Rect colorRect;
    Rect leftRect;
    Rect rightRect;

	int colorAlpha;
	boolean visibleArrow;
	boolean visibleCenterLine;
	int showColorCount = 0;

	public ColorPickerView(Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		init( context, attrs, defStyle );
	}

	public ColorPickerView(Context context, AttributeSet attrs ) {
		this( context, attrs, 0 );
	}

	public ColorPickerView(Context context ) {
		this( context, null );
	}

	public void setOnColorChange(OnColorChange onColorChange) {
		this.onColorChange = onColorChange;
		onColorChange.changeColor(arColor.get(centerIdx));
	}

	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );

		width = right - left;
		height = bottom - top;

		colorW = width / (float)showColorCount;
		colorHalfW = colorW / 2;

		colorDx = colorHalfW;

		colorRect.set(0, 0, (int)colorW+1, height);

        leftRect.set(0, height /2 - 44, 26, height /2 + 44);
        rightRect.set(width - 26, height /2 - 44, width, height /2 + 44);

		moveLeft = (colorW) * ((showColorCount / 2) + 1);
		centerX = width/2;
	}



	private void init(Context context, AttributeSet attrs, int defStyle) {

		colorAlpha =  context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getInt( R.styleable.ColorPickerView_color_alpha, 255);
		visibleArrow = context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getBoolean( R.styleable.ColorPickerView_visible_arrow, true);
		visibleCenterLine = context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getBoolean( R.styleable.ColorPickerView_visible_center_line, true);
		showColorCount = context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getInt( R.styleable.ColorPickerView_show_color_count, 9);

        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_left);
        bitmapRight = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_right);
        leftRect = new Rect();
        rightRect = new Rect();

		paintCenter = new Paint();
		paintCenter.setColor(getResources().getColor(android.R.color.black));
		paintCenter.setStrokeWidth(4);

		arColor = new ArrayList<>();
		arPaintColor = new ArrayList<>();

		colorRect =  new Rect();

		mGestureDetector = new GestureDetector( context, this );
		mGestureDetector.setIsLongpressEnabled( false );
		setFocusable( true );
		setFocusableInTouchMode( true );

	}

	public void addColor(String color[]) {
		for(String c : color) {
			addColor(c);
		}
	}

	public void addColor(String color) {

		arColor.add(color);
		colorCount = arColor.size();

		Paint p = new Paint();
		p.setColor(Color.parseColor(color));
		p.setAlpha(colorAlpha);
		arPaintColor.add(p);

		//TODO: calculate start idx
		startIdx = colorCount - showColorCount;
	}

	private void calculate() {

		if(colorDx > colorW) {
			colorDx -= colorW;
			changeColor(COLOR_IDX_UP);
		} else if(colorDx < 0) {
			colorDx += colorW;
			changeColor(COLOR_IDX_DOWN);
		}

		if(dX > colorW) {
			startIdx++;
			dX -= colorW;
			startIdx = startIdx % colorCount;
		} else if(dX < -colorW) {
			startIdx--;
			dX += colorW;
			if(startIdx < 0 ) {
				startIdx = colorCount - 1;
			}
		}

		if(startGoCenter) {
			if(dX > colorHalfW) {
				centerDx = colorW - dX;
			} else if(dX < -colorHalfW) {
				centerDx = -(colorW + dX);
			} else {
				centerDx = -dX;
			}

			if(centerDx < 0) {
				addDx = -ADD_DX;
				centerDx = -centerDx; //양수 화
			} else {
				addDx = ADD_DX;
			}

			doGoCenter = true;
			startGoCenter = false;

		} else if (doGoCenter) {
			if(centerDx <= ADD_DX ) {
				if(addDx < 0) addDx = -centerDx;
				else addDx = centerDx;
				doGoCenter = false;
			}

			dX += addDx;
			colorDx += addDx;
			centerDx -= ADD_DX;
		}

	}



	private void changeColor(int cmd) {
		switch (cmd) {
		case COLOR_IDX_UP:
			centerIdx ++;
			centerIdx = centerIdx % colorCount;
			break;
		case COLOR_IDX_DOWN:
			centerIdx --;
			if(centerIdx < 0) {
				centerIdx = colorCount - 1;
			}
			break;
		}

		if(onColorChange != null) {
			onColorChange.changeColor(arColor.get(centerIdx));
		}
	}

	@Override
	protected void onDraw( Canvas canvas ) {
		super.onDraw( canvas );

		calculate();

		canvas.save();
		canvas.translate(-dX - moveLeft, 0);
		canvas.drawRect(colorRect, arPaintColor.get(startIdx));

		for(int i = 0; i < colorCount - 1; i ++) {
			int idx = (startIdx + 1 + i) % colorCount;
			canvas.translate(colorW, 0);
			canvas.drawRect(colorRect, arPaintColor.get(idx));
		}

		canvas.restore();

		if(visibleCenterLine) {
			canvas.drawLine(centerX, 0, centerX, height, paintCenter);
		}

		if(visibleArrow) {
			canvas.drawBitmap(bitmapLeft, null, leftRect, null);
			canvas.drawBitmap(bitmapRight, null, rightRect, null);
		}

		if(startGoCenter || doGoCenter) {
			invalidate();
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retValue = mGestureDetector.onTouchEvent( event );

		int action = event.getAction();
		if ( action == MotionEvent.ACTION_UP ) {
			onUp();
		}
		return retValue;

	}
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	public void onUp() {
		startGoCenter = true;
		invalidate();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
		getParent().requestDisallowInterceptTouchEvent( true );
		dX += distanceX;
		colorDx += distanceX;
		invalidate();
		return true;
	}


	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY) {
		return false;
	}

	public ArrayList<String> getColorList() {
		return arColor;
	}

	public boolean isVisibleCenterLine() {
		return visibleCenterLine;
	}

	public void setVisibleCenterLine(boolean visibleCenterLine) {
		this.visibleCenterLine = visibleCenterLine;
		invalidate();
	}

	/**
	 * get the visibility of arrow
	 *
	 *	@return visibility of arrow
	 */
	public boolean isVisibleArrow() {
		return visibleArrow;
	}

	/**
	 * set the visibility of arrow
	 *
	 * @param a visibility of arrow
	 */
	public void setVisibleArrow(boolean visibleArrow) {
		this.visibleArrow = visibleArrow;
		invalidate();
	}

	/**
	 * get the display color alpha
	 *
	 *	@return value of the display color alpha of the range [0..255]
	*/
	public int getColorAlpha() {
		return colorAlpha;
	}

	/**
	 * set the display color alpha
	 * value is outside of the range [0..255]
	 *
	 * @param a set the alpha component [0..255] of the display color.
	 */
	public void setColorAlpha(int colorAlpha) {
		this.colorAlpha = colorAlpha;

		for(Paint p : arPaintColor) {
			p.setAlpha(colorAlpha);
		}
	}

	/**
	 * get the value of display color at center
	 *
	 * @return the value of display color at center
	 */
	public String getCenterColor() {
		return arColor.get(centerIdx);
	}

}
