package cs108.stanford.edu.bunnyworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class EditorView extends View {
    private ArrayList<Shape> shapeList = new ArrayList<Shape>();
    private float x1, left, right, y1, top, bottom;
    private Page currentPage;
    private List<Page> pages = new ArrayList<Page>();
    private boolean resizable;

    public EditorView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init() {
        Page page1 = new Page("Page1");
        this.currentPage = page1;
        currentPage.setStarterPage();
        pages.add(page1);
    }

    public void copy(Shape input) {
        currentPage.setSelected(input);
    }
    public void paste() {
        invalidate();
    }

    public boolean delete(Shape shape) {
        if (currentPage != null) {
            currentPage.deleteShape(shape);
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: { //select shape
                helperDown(event);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                helperMove(event);
                break;
            }
            case MotionEvent.ACTION_UP: { //scale shape
                helperUp(event);
                break;
            }

        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(currentPage != null) currentPage.draw(canvas);
    }

    public void drawPage(Page page) {
        for(Shape cur: page.getShapes()) {
            drawAddedShape(cur);
        }
        invalidate();
    }

    public void drawAddedShape(Shape shape) {
        String name = shape.getImage();
        int addedShape = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
        BitmapDrawable drawableAdded = (BitmapDrawable) getResources().getDrawable(addedShape);
        Bitmap bitmapAdded = drawableAdded.getBitmap();
        Bitmap bitmapScale = Bitmap.createScaledBitmap(bitmapAdded, (int)shape.getWidth(), (int)shape.getHeight(), false);
        shape.setBitmap(bitmapScale);
        shape.setRight(bitmapScale.getWidth() + shape.getX());
        shape.setBottom(bitmapScale.getHeight() + shape.getY());
        invalidate();
    }

    private Shape findShape(float x, float y) {
        if (currentPage == null) return null;
        shapeList = currentPage.getShapes();
        for(int i = shapeList.size() - 1; i >= 0; i--) {
            if (shapeList.get(i).cover(x, y)) return shapeList.get(i);
        }
        return null;
    }

    private void helperDown(MotionEvent event) { //select shape
        x1 = event.getX();
        y1 = event.getY();
        Shape cur = findShape(x1, y1);
        if (currentPage != null) {
            Shape currentPageSelected = currentPage.getSelected();
            if(currentPageSelected != null && inResizableRegion(currentPageSelected, x1, y1)) {
                currentPage.setSelected(cur);
                resizable = true;
            } else {
                currentPage.setSelected(cur);
                resizable = false;
            }
        }
    }

    private boolean inResizableRegion(Shape shape, float x1, float y1) {
        return ((x1 >= shape.getRight() - 100) && (x1 <= shape.getRight() + 100)
                && (y1 >= shape.getBottom() - 100) && (y1 <= shape.getBottom() + 100));
    }

    private void helperUp(MotionEvent event) {
        float x2 = event.getX();
        float y2 = event.getY();

        if (x1 > x2) {
            left = x2;
            right = x1;
        } else {
            left = x1;
            right = x2;
        }

        if (y1 > y2) {
            top = y2;
            bottom = y1;
        } else {
            top = y1;
            bottom = y2;
        }
        resizable = false;
    }


    private void helperMove(MotionEvent event) {
        float width = event.getX();
        float height = event.getY();
        Shape currentSelection = currentPage.getSelected();
        if(currentPage != null && resizable && currentSelection != null) {
            if(width > 1 && height > 1) {
                currentSelection.setRight(width + currentSelection.getX());
                currentSelection.setBottom(height + currentSelection.getY());
            }
            drawAddedShape(currentPage.getSelected());
        } else if(currentPage != null && currentSelection != null && !resizable && currentSelection.getMovable()) {
            currentSelection.move(width, height);
            drawAddedShape(currentPage.getSelected());
        }
    }

    public List<Page> getPages() {
        return this.pages;
    }

    public void updatePages(List<Page> pages) { this.pages = pages; }

    public void setCurrentPage(Page cur) {
        this.currentPage = cur;
        invalidate();
    }
}