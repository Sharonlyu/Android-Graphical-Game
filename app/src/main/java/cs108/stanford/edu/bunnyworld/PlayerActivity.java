package cs108.stanford.edu.bunnyworld;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    SQLiteDatabase db;
    List<String> gameNames = new ArrayList<>();;
    String nameOfGameToLoad = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_view);
        playerView = (PlayerView) findViewById(R.id.playerView_layout);
    }

    // References:
    // https://www.codota.com/code/java/methods/android.app.Activity/onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.load_game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // References:
    // https://www.codota.com/code/java/methods/android.app.Activity/onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loadGamesFromDB_item:
                if (gameNames.isEmpty()) {
                    loadGameNamesFromDB();
                }
                popupWindowOfLoadGame(playerView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadGameNamesFromDB() {
        db = openOrCreateDatabase("bunnyWorldDB", MODE_PRIVATE, null);
        Cursor tableCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='bunnyWorld';", null);
        if (tableCursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "There're no games in database, please create a new one.", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM bunnyWorld", null);
        while (cursor.moveToNext()) {
            String gameName = cursor.getString(0);
            gameNames.add(gameName);
        }
        System.out.println("Loading from DB...gameNames = " + gameNames);
    }


    public ArrayList<Page> jsonToPages(String json) {
        ArrayList<Page> pages = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("gameInfo");
            for (int i = 0; i < jsonArray.length(); i++) {
                pages.add(jsonToPage(jsonArray.getJSONObject(i)));
            }
            System.out.println("Loading from DB...jsonToPages done");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return pages;
    }


    public Page jsonToPage(JSONObject jsonObject) {
        Page page = new Page();
        try {
            JSONObject pageObject = jsonObject;
            String pageName = pageObject.getString("pageName");
            page.renameCurrentPage(pageName);
            String shapes = pageObject.getString("shapes");
            JSONObject shapeList = new JSONObject(shapes);
            JSONArray shapeArray = shapeList.getJSONArray("shapeInfo");
            for (int i = 0; i < shapeArray.length(); i++){
                page.addShape(jsonToShape(shapeArray.getJSONObject(i)));
            }
            System.out.println("Loading from DB...jsonToPage done");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return page;
    }


    public Shape jsonToShape(JSONObject shapeObject) {
        Shape shape = new Shape();
        try {
            String name = shapeObject.getString("shapeName");
            String page = shapeObject.getString("whichPage");
            String image = shapeObject.getString("whichImage");
            String left = shapeObject.getString("leftCoordinate");
            String right = shapeObject.getString("rightCoordinate");
            String top = shapeObject.getString("topCoordinate");
            String bottom = shapeObject.getString("bottomCoordinate");
            String script = shapeObject.getString("script");
            String movable = shapeObject.getString("isMovable");
            String visible = shapeObject.getString("isVisible");
            String fontSize = shapeObject.getString("fontSize");
            String isText = shapeObject.getString("isText");
            String text = shapeObject.getString("text");

            shape.setName(name);
            shape.setPage(page);
            shape.setImage(image);
            shape.setLeft(Float.parseFloat(left));
            shape.setRight(Float.parseFloat(right));
            shape.setTop(Float.parseFloat(top));
            shape.setBottom(Float.parseFloat(bottom));
            shape.setScript(script);
            shape.setMovable(Boolean.parseBoolean(movable));
            shape.setVisible(Boolean.parseBoolean(visible));
            shape.setFontSize(Integer.parseInt(fontSize));
            shape.setisText(Boolean.parseBoolean(isText));
            shape.setText(text);

            System.out.println("Loading from DB...jsonToShape done");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return shape;
    }

    // References:
    // https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android
    private void popupWindowOfLoadGame(View view) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.load_game, null);

            RadioGroup gameChoices = layout.findViewById(R.id.gameChoices_radioGroup);
            RadioButton radioButton;

            int i = 0;
            while (i < 4) {
                switch(i) {
                    case 0: radioButton = layout.findViewById(R.id.game1_radioButton);
                        if (gameNames.size() > i) {
                            radioButton.setText(gameNames.get(i));
                        }
                        break;
                    case 1: radioButton = layout.findViewById(R.id.game2_radioButton);
                        if (gameNames.size() > i) {
                            radioButton.setText(gameNames.get(i));
                        }
                        break;
                    case 2: radioButton = layout.findViewById(R.id.game3_radioButton);
                        if (gameNames.size() > i) {
                            radioButton.setText(gameNames.get(i));
                        }
                        break;
                    case 3: radioButton = layout.findViewById(R.id.game4_radioButton);
                        if (gameNames.size() > i) {
                            radioButton.setText(gameNames.get(i));
                        }
                        break;
                    default:
                }
                i++;
            }


            final PopupWindow pw = new PopupWindow(layout, 800, 1000, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            Button loadBtn = layout.findViewById(R.id.load_button);
            Button cancelBtn = layout.findViewById(R.id.cancel_button);

            loadBtn.setOnClickListener(view1 -> {
                int selectedGame = gameChoices.getCheckedRadioButtonId();
                switch(selectedGame) {
                    case R.id.game1_radioButton:
                        nameOfGameToLoad = gameNames.get(0);
                        break;
                    case R.id.game2_radioButton:
                        nameOfGameToLoad = gameNames.get(1);
                        break;
                    case R.id.game3_radioButton:
                        nameOfGameToLoad = gameNames.get(2);
                        break;
                    case R.id.game4_radioButton:
                        nameOfGameToLoad = gameNames.get(3);
                        break;
                    default:
                }
                pw.dismiss();
                System.out.println("Loading from DB...nameOfGameToLoad = " + nameOfGameToLoad);
                loadGameFromDB();
            });

            cancelBtn.setOnClickListener(v -> pw.dismiss());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void loadGameFromDB() {
        db = openOrCreateDatabase("bunnyWorldDB", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery(
                "SELECT pageInfo FROM bunnyWorld WHERE pageName = '" + nameOfGameToLoad + "'",
                null
        );
        while (cursor.moveToNext()){
            String gameInfo = cursor.getString(0);
            ArrayList<Page> game = jsonToPages(gameInfo);
            playerView.loadGame(game);
            System.out.println("Loading from DB...playerView.loadGame done");
        }
    }

}



