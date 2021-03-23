package cs108.stanford.edu.bunnyworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private String pageName;
    private ArrayList<Shape> shapes;
    private Shape currentSelected = null;
    private Shape currentCopied = null;
    private boolean starterPage = false;
    private Paint selectionPaint;
    private Paint dragBubbleBorder, dragBubbleFill;

    public Page() {
        shapes = new ArrayList<Shape>();
        this.selectionPaint = new Paint();
        selectionPaint.setColor(Color.BLUE);
        selectionPaint.setStrokeWidth(15.0f);
        selectionPaint.setStyle(Paint.Style.STROKE);
    }

    public Page(String pageName) {
        this.pageName = pageName;
        shapes = new ArrayList<Shape>();
        this.selectionPaint = new Paint();
        selectionPaint.setColor(Color.BLUE);
        selectionPaint.setStrokeWidth(15.0f);
        selectionPaint.setStyle(Paint.Style.STROKE);
    }

    public boolean equals(Page b) {
        return this.pageName.equals(b.curName());
    }

    public String curName() {
        return pageName;
    }

    public void renameCurrentPage(String newName) {
        this.pageName = newName;
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public void playerDrawPage(Canvas canvas) {
        for (Shape cur : shapes) {
            cur.playerDrawShape(canvas);
        }
    }


    public void draw(Canvas canvas) {
        Paint hiddenPaint = null;
        for (Shape cur : shapes) {
            hiddenPaint = null; //ts added
            if(cur == currentSelected) {
                RectF box = new RectF(cur.getLeft() - 8, cur.getTop() - 8, cur.getRight() + 8, cur.getBottom() + 8);
                canvas.drawRect(box, selectionPaint);
            }
            if(!cur.getVisible()) {
                hiddenPaint = new Paint();
                hiddenPaint.setAlpha(70);
            }
            if(!cur.getMovable()) {
                cur.setMovable(false);
            }

            if(cur.isText()) {
                if(hiddenPaint == null) hiddenPaint = new Paint();
                hiddenPaint.setColor(Color.BLUE);
                hiddenPaint.setTextSize(cur.getFontSize());
                canvas.drawText(cur.getText(), cur.getLeft(), cur.getBottom(), hiddenPaint);

            } else if(cur.getImage().equals("greybox")){
                Paint greyPaint = new Paint();
                //greyPaint.setColor(Color.rgb(1, 1, 128));//light grey
                greyPaint.setColor(Color.rgb(128, 128, 128));//light grey
                canvas.drawRect(cur.getLeft(), cur.getTop(), cur.getRight(), cur.getBottom(), greyPaint);
            } else {
                canvas.drawBitmap(cur.getBitmap(), cur.getX(), cur.getY(), hiddenPaint);
            }
        }
    }

    public void addShape(Shape shape) {
        if(shapes.contains(shape)) deleteShape(shape);
        shapes.add(shape);
    }

    public void deleteShape(Shape shape) {
        shapes.remove(shape);
        currentSelected = null;
    }

    public void copyShape(Shape shape) {
        currentCopied = shape;
    }

    public void pasteShape() {
        if (currentCopied != null) {
            addShape(currentCopied);
        }
    }

    public void cutShape(Shape shape) {
        copyShape(shape);
        deleteShape(shape);
    }

    public Shape selectShape(float x, float y) {
        for (int i = shapes.size() - 1; i >= 0; i--) {
            if (shapes.get(i).cover(x, y)) {
                currentSelected = shapes.get(i);
                return currentSelected;
            }
        }
        return null;
    }

    public Shape getSelected() {return currentSelected;}
    public void setSelected(Shape shape) { currentSelected = shape; }
    public void setStarterPage() { this.starterPage = true; }
    public boolean isStarterPage() { return this.starterPage; }
}