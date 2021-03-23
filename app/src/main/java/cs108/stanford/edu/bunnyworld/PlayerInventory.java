package cs108.stanford.edu.bunnyworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerInventory {
    float left;
    float right;
    float top;
    float bottom;
    float invBoundary;  // Left edge for newly added shape
    Paint inventoryPaint;
    BitmapDrawable backgroundResource;
    public static final float INVENTORY_HEIGHT = 300f;
    public static final float SHAPE_DIST = 20f; // Distance between shapes in inventory

    ArrayList<Shape> invShapes;
    HashMap<Shape, Float> resizeMap;

    public PlayerInventory(float left, float top, float right, float bottom, BitmapDrawable backgroundResource) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.backgroundResource = backgroundResource;
        inventoryPaint = new Paint();
        inventoryPaint.setColor(Color.WHITE);
        invShapes = new ArrayList<>();
        resizeMap = new HashMap<>();
        invBoundary = 0f;
    }

    public void drawInventory(Canvas canvas) {
        if (backgroundResource == null)
            canvas.drawRect(left, top, right, bottom, inventoryPaint);
        else {
            Bitmap bitmap = backgroundResource.getBitmap();
            canvas.drawBitmap(bitmap, null, new RectF(left, top, right, bottom), null);
        }
        for (Shape elem : invShapes) {
            elem.playerDrawShape(canvas);
        }
    }

    public void addShape(Shape shape) {
        if (shape.getHeight() > INVENTORY_HEIGHT) {
            float resizeFactor = INVENTORY_HEIGHT / shape.getHeight();
            float invertSize = 1.0f / resizeFactor;
            shape.resize(resizeFactor);
            resizeMap.put(shape, invertSize);
        }

        float oldX = shape.getX();
        float oldY = shape.getY();
        float newX = invBoundary;
        float newY = PlayerView.inventoryTop;
        float deltaX = newX - oldX;
        float deltaY = newY - oldY;
        shape.playerMove(deltaX, deltaY);
        invShapes.add(shape);
        invBoundary = invBoundary + shape.getWidth() + SHAPE_DIST;
    }

    public void removeShape(Shape shape) {
        invBoundary = invBoundary - shape.getWidth() - SHAPE_DIST;
        int removeIndex = invShapes.indexOf(shape);
        if (removeIndex != invShapes.size()-1 && !invShapes.isEmpty()) {
            for (int i = removeIndex + 1; i < invShapes.size(); i++) {
                invShapes.get(i).playerMove((shape.getWidth() + SHAPE_DIST) * (-1), 0f);
            }
        }
        invShapes.remove(shape);

        if (resizeMap.containsKey(shape)) {
            shape.resize(resizeMap.get(shape));
            resizeMap.remove(shape);
        }
    }

    public boolean contains(Shape shape) {
        return invShapes.contains(shape);
    }

    public ArrayList<Shape> getShapes() {
        return invShapes;
    }
}
