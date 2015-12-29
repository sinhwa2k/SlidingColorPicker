package com.sinhwa2k.slidingcolorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;


import com.sinhwa2k.slidingcolorpicker.R;

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
	int hegiht;
	int centerX;

	boolean startGoCenter = false;
	boolean doGoCenter = false;

    Bitmap bitmapLeft;
    Bitmap bitmapRight;

	Rect colorRect;
	//Rect colorRectTop;
    Rect leftRect;
    Rect rightRect;

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
		onColorChange.changeColor(centerIdx);
	}

	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );

		width = right - left;
		hegiht = bottom - top;

		colorW = width / (float)showColorCount;
		colorHalfW = colorW / 2;
		colorH = hegiht;

		colorDx = colorHalfW;


		colorRect.set(0, 0, (int)colorW+1, colorH);
		//colorRectTop.set(0, 0, (int)colorW+1, colorH/15);
        //26 88

        leftRect.set(0, hegiht/2 - 44, 26, hegiht/2 + 44);
        rightRect.set(width - 26, hegiht/2 - 44, width, hegiht/2 + 44);

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

        bitmapLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_left);
        bitmapRight = BitmapFactory.decodeResource(getResources(), R.mipmap.picker_ic_right);
        leftRect = new Rect();
        rightRect = new Rect();

		paintBg = new Paint();
		paintBg.setColor(getResources().getColor(android.R.color.white));

		paintCenter = new Paint();
		paintCenter.setColor(getResources().getColor(android.R.color.black));
		paintCenter.setStrokeWidth(4);
		//paintCenter.setAlpha(50);

		arPaintColor = new ArrayList<Paint>();
		arPaintColorAlpha10 = new ArrayList<Paint>();
		addColor(R.color.Timetable_Color0);
		addColor(R.color.Timetable_Color1);
		addColor(R.color.Timetable_Color2);
		addColor(R.color.Timetable_Color3);
		addColor(R.color.Timetable_Color4);
		addColor(R.color.Timetable_Color5);
		addColor(R.color.Timetable_Color6);
		addColor(R.color.Timetable_Color7);
		addColor(R.color.Timetable_Color8);
		addColor(R.color.Timetable_Color9);
		addColor(R.color.Timetable_Color10);
		addColor(R.color.Timetable_Color11);
		addColor(R.color.Timetable_Color12);
		addColor(R.color.Timetable_Color13);
		addColor(R.color.Timetable_Color14);

		colorRect =  new Rect();
		//colorRectTop =  new Rect();

		mGestureDetector = new GestureDetector( context, this );
		mGestureDetector.setIsLongpressEnabled( false );
		setFocusable( true );
		setFocusableInTouchMode( true );

	}

	private void addColor(int id) {
		Paint p = new Paint();
		p.setColor(getResources().getColor(id));
		arPaintColor.add(p);

		p = new Paint();
		p.setColor(getResources().getColor(id));
		p.setAlpha(25);
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
			onColorChange.changeColor(centerIdx);
	}

	@Override
	protected void onDraw( Canvas canvas ) {
		super.onDraw( canvas );

		canvas.drawPaint( paintBg );

		calculate();


		canvas.save();
		//스크롤 이동
		canvas.translate(-dX - moveLeft, 0);

		//canvas.drawRect(colorRectTop, arPaintColor.get(startIdx));
		canvas.drawRect(colorRect, arPaintColorAlpha10.get(startIdx));

		for(int i = 0; i < colorCount - 1; i ++) {
			int idx = (startIdx + 1 + i) % colorCount;
			canvas.translate(colorW, 0);
			//canvas.drawRect(colorRectTop, arPaintColor.get(idx));
			canvas.drawRect(colorRect, arPaintColorAlpha10.get(idx));
		}

		canvas.restore();
		canvas.drawLine(centerX, 0, centerX, hegiht, paintCenter);
        canvas.drawBitmap(bitmapLeft, null, leftRect, null);
        canvas.drawBitmap(bitmapRight, null, rightRect, null);



		if(startGoCenter || doGoCenter) invalidate();
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