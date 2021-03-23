package cs108.stanford.edu.bunnyworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;

public class Shape {
    private static final int DEFAULT_SIZE = 30;

    private String name;
    private String page;
    private String image;
    private String script;
    private String onClick = "";
    private String onDrop = "";
    private String onEnter = "";
    private String text;
    private int fontSize;

    private float left, right, top, bottom;
    private boolean movable;
    private boolean visible;
    private boolean isText;
    private boolean ifDropEntered = false;
    private BitmapDrawable shapeRes;
    Canvas canvas;
    private Bitmap shapeBitmap;

    public Shape() {
        this.name = null;
        this.page = null;
        this.image = null;

        this.left = (float) 0;
        this.right = (float )0;
        this.top = (float) 0;
        this.bottom = (float) 0;

        this.script = null;
        this.movable = false;
        this.visible = false;
    }

    public Shape(String name, String page, String image, float left, float right, float top, float bottom, String script, boolean movable, boolean visible) {

        this.name = name;
        this.page = page;
        this.image = image;

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;

        this.script = script;
        this.movable = movable;
        this.visible = visible;
        this.isText = false;
        this.text = "";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setOnClick(String str) {
        if (onClick.equals("")) {
            onClick += "on click " + str + ";";
        } else {
            onClick = onClick.substring(0, onClick.length()- 1) + " ";
            onClick += str + ";";
        }
        this.script = onClick + onDrop + onEnter;
    }

    public void setOnDrop(String str) {
        if (onDrop.equals("")) {
            onDrop += "on drop " + str + ";";
        } else {
            onDrop = onDrop.substring(0, onDrop.length()- 1) + " ";
            onDrop += str + ";";
        }
        this.script = onClick + onDrop + onEnter;
        ifDropEntered = true;
    }
    public boolean checkIfDropEntered() {
        return this.ifDropEntered;
    }
    public void setOnEnter(String str) {
        if (onEnter.equals("")) {
            onEnter += "on enter " + str + ";";
        } else {
            onEnter = onEnter.substring(0, onEnter.length()- 1) + " ";
            onEnter += str + ";";
        }
        this.script = onClick + onDrop + onEnter;
    }


    public float getX() {
        return left;
    }

    public float getY() {
        return top;
    }

    public float getWidth() {
        return (right - left);
    }

    public float getHeight() {
        return (bottom - top);
    }


    public Float getLeft( ) { return this.left; }

    public Float getRight() {
        return this.right;
    }

    public Float getTop() {
        return this.top;
    }

    public Float getBottom() {
        return this.bottom;
    }



    public void setLeft(float value) { left = value; }

    public void setRight(float value) {
        right = value;
    }

    public void setTop(float value) {
        top = value;
    }

    public void setBottom(float value) {
        bottom = value;
    }


    public String getImage() {
        return this.image;
    }

    public void setImage(String imgName) {
        this.image = imgName;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }


    public Boolean getMovable() {
        return this.movable;
    }

    public void setMovable(Boolean movable) {
        this.movable = movable;
    }


    public BitmapDrawable getShapeRes() {
        return this.shapeRes;
    }

    public void setShapeRes(BitmapDrawable shapeRes) {
        this.shapeRes = shapeRes;
    }


    public String getText() {
        return this.text;
    }
    public void setisText(boolean input) { isText = input; }
    public boolean isText() { return isText; }

    public void setText(String text) {
        this.text = text;
    }


    public int getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean cover(float x, float y) {
        return (x <= right && x >= left && y <= bottom && y >= top);
    }

    public void setBitmap(Bitmap bitmap) { shapeBitmap = bitmap; }
    public Bitmap getBitmap() { return shapeBitmap; }

    /**
     * This function is used to move the shape by xDist horizontally and yDist vertically
     */
    public void move(float xDist, float yDist) {
        float width = right - left;
        float height = bottom - top;
        this.left = xDist - (width / 2);
        this.top = yDist - (height / 2);
        this.right = width + left;
        bottom = top + height;
    }

    public void playerMove(float xDist, float yDist) {
        this.left += xDist;
        this.right += xDist;
        this.top += yDist;
        this.bottom += yDist;
    }
    /**
     * This function is used to resize the shape by the resizeFactor
     */
    public void resize(float resizeFactor) {
        float width = right - left;
        float height = bottom - top;
        this.top += (height - height * resizeFactor) / 2;
        this.bottom -= (height - height * resizeFactor) / 2;
        this.left += (width - width * resizeFactor) / 2;
        this.right -= (width - width * resizeFactor) / 2;

    }


// if image name is not empty, then draw the shape and scale to fit;
    //if image name is empty, or image is null, draw rect

    public void playerDrawShape(Canvas canvas) {
        if (visible) {
            this.canvas = canvas;
            RectF bounds = new RectF(left, top, right, bottom);

            if (isText) {//draw a single line of the text, ignore the shape size
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(Color.BLACK);//text color

                textPaint.setTextSize(fontSize);
                //textPaint.setTextAlign(Paint.Align.CENTER);
                //float textHeight = textPaint.descent() - textPaint.ascent();
                //float textOffset = (textHeight / 2) - textPaint.descent();
                //canvas.drawOval(bounds, null);
                canvas.drawText(text, left, bottom, textPaint);

            } else if (!image.isEmpty() && shapeRes != null) {
                canvas.drawBitmap(shapeRes.getBitmap(), null, bounds, null);

            } else {
                Paint greyPaint = new Paint();
                greyPaint.setColor(Color.rgb(128, 128, 128));//light grey
                canvas.drawRect(left, top, right, bottom, greyPaint);
            }
        }
    }
}
