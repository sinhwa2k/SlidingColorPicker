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

	Paint paintBg;
	Paint paintCenter;
	ArrayList<Paint> arPaintColor;
	ArrayList<Paint> arPaintColorAlpha10;
	int colorCount = 15;
	int showColorCount = 9;
	int centerIdx = 0; //센터 칼라

	int startIdx = 6;

	float dX = 0f; //현재 스크롤 dx
	float colorDx = 0f; //현재 색상 추출용
	float centerDx = 0f; //멈췄을 시 센터로 이동용
	float addDx = 0f; //센터 이동 dx

	float moveLeft;
	float colorW;
	float colorHalfW;
	int colorH;
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
		onColorChange.changeColor(arPaintColor.get(centerIdx).getColor());
	}

	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );

		width = right - left;
		height = bottom - top;

		colorW = width / (float)showColorCount;
		colorHalfW = colorW / 2;
		colorH = height;

		colorDx = colorHalfW;

		colorRect.set(0, 0, (int)colorW+1, colorH);

        leftRect.set(0, height /2 - 44, 26, height /2 + 44);
        rightRect.set(width - 26, height /2 - 44, width, height /2 + 44);

		moveLeft = (colorW) * 5;
		centerX = width/2;
	}

	public void setInitColor(int colorIdx) {
		centerIdx = colorIdx;
		startIdx = centerIdx - 9;

		if(startIdx < 0 ) startIdx = colorCount + startIdx;
		else startIdx = startIdx % colorCount;
        invalidate();
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {

		colorAlpha =  context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getInt( R.styleable.ColorPickerView_color_alpha, 255);
		visibleArrow = context.obtainStyledAttributes( attrs, R.styleable.ColorPickerView ).getBoolean( R.styleable.ColorPickerView_visible_arrow, true);
        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_left);
        bitmapRight = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_right);
        leftRect = new Rect();
        rightRect = new Rect();

		paintBg = new Paint();
		paintBg.setColor(getResources().getColor(android.R.color.white));

		paintCenter = new Paint();
		paintCenter.setColor(getResources().getColor(android.R.color.black));
		paintCenter.setStrokeWidth(4);

		arPaintColor = new ArrayList<Paint>();
		arPaintColorAlpha10 = new ArrayList<Paint>();
	
		addColor("#f9552e");
		addColor("#ff7800");
		addColor("#ffb400");
		addColor("#6cd128");
		addColor("#07b017");
		addColor("#00b37e");
		addColor("#33cccc");
		addColor("#0066ff");
		addColor("#0c74a5");
		addColor("#6075de");
		addColor("#ce5bf5");
		addColor("#9e37e3");
		addColor("#fa53a7");
		addColor("#9a8170");
		addColor("#996600");

		colorRect =  new Rect();

		mGestureDetector = new GestureDetector( context, this );
		mGestureDetector.setIsLongpressEnabled( false );
		setFocusable( true );
		setFocusableInTouchMode( true );

	}

	private void addColor(String color) {
		Paint p = new Paint();
		p.setColor(Color.parseColor(color));
		arPaintColor.add(p);

		p = new Paint();
		p.setColor(Color.parseColor(color));
		p.setAlpha(colorAlpha);
		arPaintColorAlpha10.add(p);
	}

	private void calculate() {

		//칼라체크
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
			if(startIdx < 0 ) startIdx = colorCount - 1;
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

			//마지막
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
			if(centerIdx < 0) centerIdx = colorCount -1;
			break;
		}

		if(onColorChange != null)
			onColorChange.changeColor(arPaintColor.get(centerIdx).getColor());
	}

	@Override
	protected void onDraw( Canvas canvas ) {
		super.onDraw( canvas );

		canvas.drawPaint( paintBg );

		calculate();


		canvas.save();
		//스크롤 이동
		canvas.translate(-dX - moveLeft, 0);

		canvas.drawRect(colorRect, arPaintColorAlpha10.get(startIdx));

		for(int i = 0; i < colorCount - 1; i ++) {
			int idx = (startIdx + 1 + i) % colorCount;
			canvas.translate(colorW, 0);
			canvas.drawRect(colorRect, arPaintColorAlpha10.get(idx));
		}

		canvas.restore();
		canvas.drawLine(centerX, 0, centerX, height, paintCenter);

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
		// TODO Auto-generated method stub

	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						   float velocityY) {
		return false;
	}




}
