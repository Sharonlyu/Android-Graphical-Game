package cs108.stanford.edu.bunnyworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PlayerView extends View {
    float xOld, yOld, xStart, yStart;
    boolean moveMode;
    PlayerInventory inventory;

    static float inventoryTop;
    static float screenHeight;
    static float screenWidth;

    Page currPage;
    Shape selectedShape;
    Paint selectedPaint;
    float ySelectedStart;
    Paint droppablePaint;
    TextPaint textPaint;

    ArrayList<Shape> droppableShapes;
    HashMap<String, Page> currGame;
    HashMap<String, BitmapDrawable> shapeLibrary;
    HashMap<String, Integer> soundLibrary;
    HashMap<Shape, String[]> scripts;
    ArrayList<Shape> shapeList;
    MediaPlayer backgroundMusic;
    static ArrayList<String> actions = new ArrayList<String>(Arrays.asList("goto", "play", "hide", "show"));

    Bitmap animationPic;
    int animationSpeedX;
    int animationSpeedY;
    int animationY;
    int animationX;
    int animationWidth;
    int animationHeight;
    Paint animationPaint;
    MediaPlayer animationSound;

    Bitmap transitionPic;
    int transitionY;
    int transitionSpeed;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        animationPic = BitmapFactory.decodeResource(getResources(), R.drawable.player_animation);
        animationSpeedX = 5;
        animationSpeedY = 5;
        animationWidth = 300;
        animationHeight = 300;
        animationPaint = new Paint();
        animationPaint.setAlpha(150);
        animationSound = MediaPlayer.create(getContext(), R.raw.animation_sound);

        transitionY = 0;
        transitionSpeed = 100;

        selectedPaint = new Paint();
        selectedPaint.setColor(Color.BLUE);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeWidth(10.0f);

        droppablePaint = new Paint();
        droppablePaint.setColor(Color.GREEN);
        droppablePaint.setStyle(Paint.Style.STROKE);
        droppablePaint.setStrokeWidth(15f);
        droppableShapes = new ArrayList<>();

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);

        loadShapes();
        loadSounds();
        playBGM();
        loadGame(testGame());

        // Stops background music when quitting player view (pressing the back button)
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                try {
                    if (backgroundMusic != null) {
                        if (backgroundMusic.isPlaying())
                            backgroundMusic.stop();
                        backgroundMusic.release();
                        backgroundMusic = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        });
    }

    // Positioning for inventory and animation
    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenHeight = h;
        screenWidth = w;
        float inventoryLeft = 0f;
        inventoryTop = screenHeight - PlayerInventory.INVENTORY_HEIGHT;
        float inventoryRight = screenWidth;
        float inventoryBottom = screenHeight;
        BitmapDrawable invBackground = (BitmapDrawable)  getResources().getDrawable(R.drawable.inventory_background);
        inventory = new PlayerInventory(inventoryLeft, inventoryTop, inventoryRight, inventoryBottom, invBackground);

        animationX = (int)screenWidth - 500;
        animationY = 0;
    }

    public ArrayList<Page> testGame() { // Example game
        ArrayList<Page> testGame = new ArrayList<>();

        Page startPage = new Page("Page1");
        Shape title = new Shape("title", "Page1", "", 0f, 50f, 0f, 100f, "", false, true);
        title.setisText(true);
        title.setText("Bunny World!");
        title.setFontSize(100);

        Shape description = new Shape("description", "Page1", "", 0f, 10f, 100f, 200f, "", false, true);
        description.setisText(true);
        description.setText("You are in a maze of twisty little passages, all alike");
        description.setFontSize(70);

        Shape door1 = new Shape("door1", "Page1", "", 100f, 300f, 300f, 500f, "on click goto page2;", false, true);
        Shape door2 = new Shape("door2", "Page1", "", 700f, 900f, 300f, 500f, "on click goto page3;", false, false);
        Shape door3 = new Shape("door3", "Page1", "", 1300f, 1500f, 300f, 500f, "on click goto page4;", false, true);
        startPage.addShape(title);
        startPage.addShape(description);
        startPage.addShape(door1);
        startPage.addShape(door2);
        startPage.addShape(door3);

        Page page2 = new Page("Page2");
        Shape rabbit1 = new Shape("rabbit1", "Page2", "mystic", 700f, 900f, 0f, 300f, "on click hide carrot1 play munching;on enter show door2", false, true);
        Shape carrot = new Shape("carrot1", "Page2", "carrot_", 1100f, 1400f, 0f, 400f, "", false, true);
        Shape door4 = new Shape("door4", "Page2", "", 0f, 200f, 200f, 400f, "on click goto page1;", false, true);
        Shape mysticText = new Shape("mysticText", "Page2", "", 500f, 600f, 400f, 450f, "", false, true);
        mysticText.setisText(true);
        mysticText.setText("Mystic Bunny - ");
        mysticText.setFontSize(70);
        Shape mysticText2 = new Shape("mysticText2", "Page2", "", 500f, 600f, 500f, 550f, "", false, true);
        mysticText2.setisText(true);
        mysticText2.setText("Rub my tummy for a big surprise!");
        mysticText2.setFontSize(70);

        page2.addShape(rabbit1);
        page2.addShape(carrot);
        page2.addShape(door4);
        page2.addShape(mysticText);
        page2.addShape(mysticText2);

        Page page3 = new Page("Page3");
        Shape fire = new Shape("fire", "Page3", "fire", 0f, 600f, 0f, 300f, "on enter play fire;", false, true);
        Shape carrot2 = new Shape("carrot2", "Page3", "carrot", 1200f, 1500f, 0f, 500f, "", true, true);
        Shape door5 = new Shape("door5", "Page3", "", 700f, 900f, 300f, 500f, "on click goto page2;", false, true);
        Shape fireText = new Shape("fireText", "Page3", "", 0f, 200f, 300f, 400f, "", false, true);
        fireText.setisText(true);
        fireText.setText("Eek! Fire-Room");
        fireText.setFontSize(80);
        Shape fireText2 = new Shape("fireText2", "Page3", "", 0f, 200f, 400f, 500f, "", false, true);
        fireText2.setisText(true);
        fireText2.setText("Run away!");
        fireText2.setFontSize(90);

        page3.addShape(fire);
        page3.addShape(carrot2);
        page3.addShape(door5);
        page3.addShape(fireText);
        page3.addShape(fireText2);

        Page page4 = new Page("Page4");
        Shape evil = new Shape("evil", "Page4", "death", 700f, 1000f, 0f, 400f, "on enter play evillaugh;on drop carrot2 hide carrot2 play munch hide evil show exit;on click play evillaugh;", false, true);
        Shape exit = new Shape("exit", "Page4", "", 1400f, 1600f, 200f, 400f, "on click goto page5;", false, false);
        Shape evilText = new Shape("evilText", "Page4", "", 50f, 200f, 450f, 550f, "", false, true);
        evilText.setisText(true);
        evilText.setText("You must appease the Bunny of Death!");
        evilText.setFontSize(100);

        page4.addShape(evil);
        page4.addShape(exit);
        page4.addShape(evilText);

        Page page5 = new Page("Page5");
        Shape carrot3 = new Shape("carrot3", "Page5", "carrot", 0f, 200f, 0f, 300f, "", true, true);
        Shape carrot4 = new Shape("carrot4", "Page5", "carrot2", 700f, 900f, 100f, 400f, "", true, true);
        Shape carrot5 = new Shape("carrot5", "Page5", "carrot_", 1400f, 1600f, 0f, 300f, "", true, true);
        Shape win = new Shape("winText", "Page5", "", 400f, 500f, 400f, 550f, "on enter play hooray;", false, true);
        win.setisText(true);
        win.setText("You Win! Yay!");
        win.setFontSize(100);

        page5.addShape(carrot3);
        page5.addShape(carrot4);
        page5.addShape(carrot5);
        page5.addShape(win);

        testGame.add(startPage);
        testGame.add(page2);
        testGame.add(page3);
        testGame.add(page4);
        testGame.add(page5);
        return testGame;
    }

    // Loads game from outside source
    // All strings converted to lower case for case insensitivity
    public void loadGame(ArrayList<Page> game) {
        currPage = null;
        currGame = new HashMap<>();
        scripts = new HashMap<>();
        shapeList = new ArrayList<>();
        ArrayList<String> shapeNames = new ArrayList<>();
        for (Page page : game) {
            if (currGame.containsKey(page.curName().toLowerCase()) ||
                    page.curName().contains(" ")) { // Check for duplicate page names and spaces
                currPage = null;
                Toast toast = Toast.makeText(getContext(),
                        "Error: invalid page name '" + page.curName() + "'\nNames are not case-sensitive\nPlease check your game",
                        Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            currGame.put(page.curName().toLowerCase(), page);
            if (page.curName().toLowerCase().equals("page1")) currPage = page; // Set first page for the game
            for (Shape shape : page.getShapes()) {
                if (shapeNames.contains(shape.getName().toLowerCase()) ||
                        shape.getName().contains(" ")) { // Check for duplicate shape names and spaces
                    currPage = null;
                    Toast toast = Toast.makeText(getContext(),
                            "Error: invalid shape name '" + shape.getName() +"'\nNames are not case-sensitive\nPlease check your game",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                shapeNames.add(shape.getName().toLowerCase());
                shapeList.add(shape);
                if (shapeLibrary.containsKey(shape.getImage()))
                    shape.setShapeRes(shapeLibrary.get(shape.getImage()));  // Link shape to bitmap
                if (shape.getScript().contains(";")) {
                    String[] scriptArray = shape.getScript().toLowerCase().split(";");
                    scripts.put(shape, scriptArray);
                }
            }
        }
        if (currPage == null) {
            Toast toast = Toast.makeText(getContext(),
                    "Error: no starting page\nPlease name your starting page 'page1'",
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        onEnter();
        invalidate();
    }

    // Loads all drawables a game needs
    private void loadShapes() {
        shapeLibrary = new HashMap<>();
        shapeLibrary.put("carrot", (BitmapDrawable) getResources().getDrawable(R.drawable.carrot));
        shapeLibrary.put("carrot2", (BitmapDrawable) getResources().getDrawable(R.drawable.carrot2));
        shapeLibrary.put("death", (BitmapDrawable) getResources().getDrawable(R.drawable.death));
        shapeLibrary.put("duck", (BitmapDrawable) getResources().getDrawable(R.drawable.duck));
        shapeLibrary.put("fire", (BitmapDrawable) getResources().getDrawable(R.drawable.fire));
        shapeLibrary.put("mystic", (BitmapDrawable) getResources().getDrawable(R.drawable.mystic));
        shapeLibrary.put("carrot_", (BitmapDrawable) getResources().getDrawable(R.drawable.carrot_));
        shapeLibrary.put("rabbit", (BitmapDrawable) getResources().getDrawable(R.drawable.rabbit));
    }

    // Loads all sounds a game needs
    private void loadSounds() {
        soundLibrary = new HashMap<>();
        soundLibrary.put("carrotcarrotcarrot", R.raw.carrotcarrotcarrot);
        soundLibrary.put("evillaugh", R.raw.evillaugh);
        soundLibrary.put("fire", R.raw.fire);
        soundLibrary.put("hooray", R.raw.hooray);
        soundLibrary.put("munch", R.raw.munch);
        soundLibrary.put("munching", R.raw.munching);
        soundLibrary.put("woof", R.raw.woof);
    }

    // Plays a pre-determined background music
    private void playBGM() {
        backgroundMusic = MediaPlayer.create(getContext(), R.raw.game_bgm);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
    }

    private boolean inInvBounds(Shape shape) {
        return shape != null && shape.getY() + shape.getHeight() / 2 > inventoryTop;
    }

    private void onEnter() {
        for (Shape shape : currPage.getShapes()) {
            if (scripts.containsKey(shape)) {
                for (int i = 0; i < scripts.get(shape).length; i++) {
                    String[] line = scripts.get(shape)[i].split(" ");
                    if (line[0].equals("on") && line[1].equals("enter"))
                        doActions(line);
                }
            }
        }
    }

    private void onClick(Shape shape) {
        if (scripts.containsKey(shape)) {
            for (int i = 0; i < scripts.get(shape).length; i++) {
                String[] line = scripts.get(shape)[i].split(" ");
                if (line[0].equals("on") && line[1].equals("click")) {
                    doActions(line);
                    return;
                }
            }
        }
    }

    private void onDrop(Shape shape) {
        ArrayList<Shape> shapes = currPage.getShapes();
        boolean wasInInventory = false; // Checks if the shape was dragged from inventory
        float left = shape.getX();
        float top = shape.getY();
        float right = left + shape.getWidth();
        float bottom = top + shape.getHeight();
        for (Shape otherShape : shapes) {
            if (otherShape != shape && otherShape.getVisible()) {
                float otherLeft = otherShape.getX();
                float otherTop = otherShape.getY();
                float otherRight = otherLeft + otherShape.getWidth();
                float otherBottom = otherTop + otherShape.getHeight();
                if (!(left > otherRight || otherLeft > right) && !(top > otherBottom || otherTop > bottom)) { // if two shapes overlap
                    if (droppableShapes.contains(otherShape) && !inventory.contains(otherShape)) {
                        for (int i = 0; i < scripts.get(otherShape).length; i++) {
                            String[] line = scripts.get(otherShape)[i].split(" ");
                            if (line[0].equals("on") && line[1].equals("drop") && line[2].equals(shape.getName().toLowerCase()))
                                doActions(line);
                        }
                    } else {
                        if (ySelectedStart < inventoryTop) {
                            float deltaX = xStart - xOld;
                            float deltaY = yStart - yOld;
                            shape.playerMove(deltaX, deltaY);
                        }
                        else {
                            wasInInventory = true;
                            inventory.addShape(selectedShape);
                        }
                    }
                    break;  // Only performs on drop on one shape
                }
            }
        }
        if (wasInInventory) {
            currPage.selectShape(xOld, yOld);
            currPage.deleteShape(selectedShape);
        }
    }

    private void doActions(String[] line) {
        for (int i = 2; i < line.length; i++) {
            if (actions.contains(line[i]) && i != line.length-1) {
                switch (line[i]) {
                    case "play":
                        if (soundLibrary.containsKey(line[i + 1])) {
                            MediaPlayer mp = MediaPlayer.create(getContext(), soundLibrary.get(line[i + 1]));
                            mp.start();
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(),
                                    "Play: sound '" + line[i+1] + "' not found", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case "hide":
                        boolean operatedHide = false;
                        String hideName = line[i + 1];
                        for (Shape shape : shapeList) {
                            if (shape.getName().toLowerCase().equals(hideName)) {
                                shape.setVisible(false);
                                operatedHide = true;
                            }
                        }
                        if (!operatedHide) {
                            Toast toast = Toast.makeText(getContext(),
                                    "Hide: shape name '" + line[i+1] + "' not found", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case "show":
                        boolean operatedShow = false;
                        String showName = line[i + 1];
                        for (Shape shape : shapeList) {
                            if (shape.getName().toLowerCase().equals(showName)) {
                                shape.setVisible(true);
                                operatedShow = true;
                            }
                        }
                        if (!operatedShow) {
                            Toast toast = Toast.makeText(getContext(),
                                    "Show: shape name '" + line[i+1] + "' not found", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case "goto":
                        String pageName = line[i + 1];
                        if (currGame.containsKey(pageName)) {
                            this.setDrawingCacheEnabled(true);
                            transitionPic = Bitmap.createBitmap(this.getDrawingCache());    // Take a screenshot of current canvas
                            this.setDrawingCacheEnabled(false);
                            transitionY = 1;    // Start page transition

                            currPage = currGame.get(pageName);
                            onEnter();
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(),
                                    "Goto: page name '" + line[i+1] + "' not found", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        inventory.drawInventory(canvas);

        if (currPage != null)
            currPage.playerDrawPage(canvas);
        else {  // Game does not load correctly
            textPaint.setTextSize(150f);
            canvas.drawText("INVALID GAME", 0f, screenHeight/2, textPaint);
        }

        if (!droppableShapes.isEmpty()) {
            for (Shape shape : droppableShapes) {
                float left = shape.getX();
                float top = shape.getY();
                float right = shape.getWidth() + left;
                float bottom = shape.getHeight() + top;
                canvas.drawRect(left, top, right, bottom, droppablePaint);
            }
        }

        if (selectedShape != null) {
            selectedShape.playerDrawShape(canvas);
            float left = selectedShape.getX();
            float top = selectedShape.getY();
            float right = selectedShape.getWidth() + left;
            float bottom = selectedShape.getHeight() + top;
            canvas.drawRect(left, top, right, bottom, selectedPaint);
        }

        if (transitionY > inventoryTop) transitionY = 0;
        else if (transitionY != 0) transitionY += transitionSpeed;
        if (transitionY != 0 && transitionPic != null) {
            canvas.drawBitmap(transitionPic, 0, transitionY, null);
        }

        animationY += animationSpeedY;
        animationX += animationSpeedX;
        if (animationY + animationHeight > screenHeight || animationY < 0) {
            animationSpeedY *= -1;
        }
        if (animationX + animationWidth > screenWidth || animationX < 0) {
            animationSpeedX *= -1;
        }
        canvas.drawBitmap(animationPic, null,
                new RectF(animationX, animationY, animationX + animationWidth, animationY + animationHeight),
                animationPaint);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currPage == null) return false; // Not register onTouchEvent when invalid game

                droppableShapes.clear();
                xStart = event.getX();
                yStart = event.getY();
                xOld = xStart;
                yOld = yStart;
                for (Shape shape : currPage.getShapes()) {
                    if (shape.cover(xOld, yOld) && shape.getVisible()) {
                        selectedShape = shape;
                        break;
                    }
                    else selectedShape = null;
                }
                if (selectedShape == null) {
                    for (Shape shape : inventory.getShapes()) {
                        if (shape.cover(xOld, yOld)) {
                            selectedShape = shape;
                            break;
                        }
                        else selectedShape = null;
                    }
                }
                if (selectedShape != null) {
                    ySelectedStart = selectedShape.getY();  // Get top of selected shape for onDrop checks
                    for (Shape otherShape : currPage.getShapes()) {
                        if (scripts.containsKey(otherShape) && otherShape != selectedShape &&
                                otherShape.getVisible()) {
                            for (int i = 0; i < scripts.get(otherShape).length; i++) {
                                String[] line = scripts.get(otherShape)[i].split(" ");
                                if (line[0].equals("on") && line[1].equals("drop") && line[2].equals(selectedShape.getName().toLowerCase()))
                                    droppableShapes.add(otherShape);
                            }
                        }
                    }
                }

                if (xStart >= animationX && xStart <= animationX + animationWidth &&
                        yStart >= animationY && yStart <= animationY + animationHeight)
                    animationSound.start();

                break;

            case MotionEvent.ACTION_MOVE:
                moveMode = true;
                if (selectedShape != null && selectedShape.getMovable()) {
                    float xMove = event.getX();
                    float yMove = event.getY();
                    float deltaX = xMove - xOld;
                    float deltaY = yMove - yOld;
                    selectedShape.playerMove(deltaX, deltaY);
                    xOld = xMove;
                    yOld = yMove;
                }
                break;


            case MotionEvent.ACTION_UP:
                if (selectedShape != null) {
                    if (!moveMode) {    // Nothing has moved
                        if (!inventory.contains(selectedShape)) {   // Selected shape not in inventory
                            onClick(selectedShape);
                        }
                    }
                    else {  // Finger has moved
                        if (!inventory.contains(selectedShape)) {   // Selected shape not in inventory
                            if (inInvBounds(selectedShape)) {   // Selected shape middle crossed inventory boundary
                                inventory.addShape(selectedShape);
                                currPage.selectShape(xOld, yOld);
                                currPage.deleteShape(selectedShape);
                            }
                            else {
                                onDrop(selectedShape);
                            }
                        }
                        else {
                            if (inInvBounds(selectedShape)) {   // Selected shape not moved out of inventory
                                float deltaX = xStart - xOld;
                                float deltaY = yStart - yOld;
                                selectedShape.playerMove(deltaX, deltaY);
                            }
                            else {
                                inventory.removeShape(selectedShape);
                                currPage.addShape(selectedShape);
                                onDrop(selectedShape);
                            }
                        }
                    }
                }
                moveMode = false;
                selectedShape = null;
                droppableShapes.clear();
                break;
        }

        invalidate();
        return true;
    }
}