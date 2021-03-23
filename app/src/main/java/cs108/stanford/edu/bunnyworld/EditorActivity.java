package cs108.stanford.edu.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {

    private List<Page> pages = new ArrayList<Page>();
    private Page page1;
    private Page curPage;
    private Shape currentSelection, copyShape;
    private EditorView editorView;
    private View view;
    private Toast addToast;
    private Dialog addShapeDialog;
    private ArrayList<ShapeRes> shapeResList;
    private SQLiteDatabase db;
    private String gameNameStr = "game1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        editorView = (EditorView) findViewById(R.id.editorView);
        this.pages = editorView.getPages();
        page1 = pages.get(0);
        curPage = page1;
        this.view = editorView;
        initializeRes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.out_most_menu, menu);
        return true;
    }
    private void initializeRes() {
        int[] imgIds = {R.drawable.carrot,
                R.drawable.carrot2,
                R.drawable.death,
                R.drawable.duck,
                R.drawable.fire,
                R.drawable.mystic,
                R.drawable.rabbit,
                R.drawable.text_bubble,
                R.drawable.carrot_,
                R.drawable.greybox,};
        String[] resourceFiles = {"carrot", "carrot2", "death", "duck",
                "fire", "mystic", "rabbit", "text_bubble", "carrot_", "greybox"};
        shapeResList = new ArrayList<>();
        for(int i = 0; i < imgIds.length; i++) {
            ShapeRes s = new ShapeRes(resourceFiles[i], imgIds[i]);
            shapeResList.add(s);
        }

    }
    public void updatePageArray(Page newPage) {
        pages.add(newPage);
    }

    //pop up window for adding a shape
    private void addShapeWindow(View viewInput) {
        try{
            addShapeDialog = new Dialog(EditorActivity.this);
            addShapeDialog.setContentView(R.layout.add_shape_window);

            GridView grid = (GridView) addShapeDialog.findViewById(R.id.grid_view);
            final GridViewAdapter adapter = new GridViewAdapter(EditorActivity.this, shapeResList);
            grid.setAdapter(adapter);

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ShapeRes res = (ShapeRes) adapter.getItem(position);
                    if(res != null) {
                        Toast addToast = Toast.makeText(getApplicationContext(),res.getResName() + " Added",Toast.LENGTH_SHORT);
                        addToast.show();
                        addShapeToPage(res.getResName());
                        //use res name as the name the shape for now
                        //show the shape on up left
                        addShapeDialog.dismiss();
                    }
                }
            });
            addShapeDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pasteShapeToPage(Shape copyShape) {
        Shape shape = new Shape(copyShape.getName(), copyShape.getPage(), copyShape.getImage(),copyShape.getLeft() + 50, copyShape.getRight() + 50, copyShape.getTop() + 50, copyShape.getBottom() + 50, copyShape.getScript(), copyShape.getMovable(), copyShape.getVisible());
        if (copyShape.getImage().equals("text_bubble")) {
            System.out.println("here");
            shape.setFontSize(copyShape.getFontSize());
            shape.setisText(true);
            shape.setText(copyShape.getText());
        }
        curPage.addShape(shape);
        currentSelection = shape;
        curPage.setSelected(currentSelection);
        editorView.drawAddedShape(shape);
    }

    private void addShapeToPage(String name) {
        Shape shape;
        if (name.equals("text_bubble")) {
            shape = new Shape(name, "Page1", name,20, 270, 20, 100, "", true, true);
            shape.setFontSize((int)shape.getHeight());
            shape.setisText(true);
            shape.setText("Text");
        }

        else {
            shape = new Shape(name, "Page1", name,20, 270, 20, 250, "", true, true);
        }
        curPage.addShape(shape);
        currentSelection = shape;
        curPage.setSelected(shape);
        editorView.drawAddedShape(shape);

    }

    private void createPageWindow(View curView) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View createPageView = inflater.inflate(R.layout.create_page,null);
            final PopupWindow pw = new PopupWindow(createPageView, 800, 400, true);
            EditText pageName = (EditText) createPageView.findViewById(R.id.PageName);
            int pageNum = pages.size() + 1;
            String assignedName = "Page" + pageNum;
            pageName.setText(assignedName);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) createPageView.findViewById(R.id.confirmName);
            Button cancelBtn = (Button) createPageView.findViewById(R.id.cancelNewPage);
            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String name = pageName.getText().toString();
                    for (Page temp : pages) {
                        if (temp.curName().equals(name)) {
                            addToast = Toast.makeText(getApplicationContext(),"Duplicated page name! Please retry!",Toast.LENGTH_SHORT);
                            addToast.show();
                            pw.dismiss();
                            return;
                        }
                    }
                    Page newPage = new Page(name);
                    pages.add(newPage);
                    if (pages.size() == 1) {page1 = newPage;}
                    editorView.updatePages(pages);
                    addToast = Toast.makeText(getApplicationContext(),"Warning! Page name must be one word without space. Please rename if it has space.",Toast.LENGTH_LONG);
                    addToast.show();
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renamePage(View curView) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View renamePageView = inflater.inflate(R.layout.rename_page,null);
            final PopupWindow pw = new PopupWindow(renamePageView, 800, 400, true);
            EditText pageName = (EditText) renamePageView.findViewById(R.id.newPageName);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) renamePageView.findViewById(R.id.confirmRename);
            Button cancelBtn = (Button) renamePageView.findViewById(R.id.cancelRename);
            TextView displayed = (TextView) findViewById(R.id.displayedPageName);
            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String name = pageName.getText().toString();
                    for (Page temp : pages) {
                        if (temp.curName().equals(name)) {
                            addToast = Toast.makeText(getApplicationContext(),"Duplicated page name! Please retry!",Toast.LENGTH_SHORT);
                            addToast.show();
                            pw.dismiss();
                            return;
                        }
                    }
                    curPage.renameCurrentPage(name);
                    editorView.updatePages(pages);
                    displayed.setText(curPage.curName());
                    addToast = Toast.makeText(getApplicationContext(),"Warning! Page name must be one word without space. Please rename if it has space.",Toast.LENGTH_LONG);
                    addToast.show();
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePage(View curView) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View deletePageView = inflater.inflate(R.layout.delete_page,null);
            final PopupWindow pw = new PopupWindow(deletePageView, 800, 200, true);

            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) deletePageView.findViewById(R.id.confirmDelete);
            Button cancelBtn = (Button) deletePageView.findViewById(R.id.cancelDeletePage);
            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!curPage.isStarterPage()) {
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Page Deleted",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        pages.remove(curPage);
                        curPage = page1;
                        editorView.setCurrentPage(curPage);
                        TextView displayed = (TextView) findViewById(R.id.displayedPageName);
                        displayed.setText(curPage.curName());
                    } else {
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Can't delete the first page",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    editorView.updatePages(pages);
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchPage(View curView) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View switchPageView = inflater.inflate(R.layout.switch_page,null);
            final PopupWindow pw = new PopupWindow(switchPageView, 1000, 1000, true);
            TextView displayedOption = (TextView) switchPageView.findViewById(R.id.allPages);
            TextView displayed = (TextView) findViewById(R.id.displayedPageName);
            String allPages = "";
            for (int i = 0; i < pages.size(); i++) {
                allPages += (i+1) + ". " + pages.get(i).curName() + "\n";
            }
            displayedOption.setText(allPages);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) switchPageView.findViewById(R.id.confirmSelect);
            Button cancelBtn = (Button) switchPageView.findViewById(R.id.cancelSwitchPage);
            EditText inputNum = (EditText) switchPageView.findViewById(R.id.pageNumSelected);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pageNum = Integer.valueOf(inputNum.getText().toString());
                    if (pageNum > pages.size() || pageNum <= 0) {
                        addToast = Toast.makeText(getApplicationContext(),"Page doesn't exist!",Toast.LENGTH_LONG);
                        addToast.show();
                        pw.dismiss();
                        return;
                    }
                    curPage = pages.get(pageNum - 1);
                    displayed.setText(curPage.curName());
                    editorView.setCurrentPage(curPage);
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showScriptPop(View curView) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View showScriptView = inflater.inflate(R.layout.show_script,null);
            final PopupWindow pw = new PopupWindow(showScriptView, 800, 1000, true);
            TextView script = (TextView) showScriptView.findViewById(R.id.curScript);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            script.setText(currentSelection.getScript());
            Button confirmBtn = (Button) showScriptView.findViewById(R.id.okBtn);
            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    pw.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseGoToPop(View curView, String trigger) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View chooseView = inflater.inflate(R.layout.choose_goto_page,null);
            final PopupWindow pw = new PopupWindow(chooseView, 800, 1000, true);
            TextView displayedOption = (TextView) chooseView.findViewById(R.id.allPages);
            String allPages = "";
            for (int i = 0; i < pages.size(); i++) {
                allPages += (i+1) + ". " + pages.get(i).curName() + "\n";
            }
            displayedOption.setText(allPages);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) chooseView.findViewById(R.id.confirmSelect);
            Button cancelBtn = (Button) chooseView.findViewById(R.id.cancelSwitchPage);
            EditText inputNum = (EditText) chooseView.findViewById(R.id.pageNumSelected);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pageNum = Integer.valueOf(inputNum.getText().toString());
                    if (pageNum > pages.size() || pageNum <= 0) {
                        addToast = Toast.makeText(getApplicationContext(),"Page doesn't exist!",Toast.LENGTH_LONG);
                        addToast.show();
                        pw.dismiss();
                        return;
                    }
                    Page goToPage = pages.get(pageNum - 1);
                    String temp = goToPage.curName();
                    if (trigger.equals("onClick")) {
                        currentSelection.setOnClick("goto " + temp);
                    } else if (trigger.equals("onDrop")) {
                        currentSelection.setOnDrop("goto " + temp);
                    }

                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseSound(View curView, String trigger) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View chooseSoundView = inflater.inflate(R.layout.choose_sound,null);
            final PopupWindow pw = new PopupWindow(chooseSoundView, 800, 1000, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            RadioButton carrot = (RadioButton) chooseSoundView.findViewById(R.id.carrotSound);
            RadioButton evil = (RadioButton) chooseSoundView.findViewById(R.id.evillaugh);
            RadioButton fire = (RadioButton) chooseSoundView.findViewById(R.id.fire);
            RadioButton hooray = (RadioButton) chooseSoundView.findViewById(R.id.hooray);
            RadioButton munch = (RadioButton) chooseSoundView.findViewById(R.id.munch);
            RadioButton munching = (RadioButton) chooseSoundView.findViewById(R.id.munching);
            RadioButton woof = (RadioButton) chooseSoundView.findViewById(R.id.woof);
            Button confirmBtn = (Button) chooseSoundView.findViewById(R.id.confirmSound);
            Button cancelBtn = (Button) chooseSoundView.findViewById(R.id.cancelSound);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String script = "play ";
                    if (carrot.isChecked()){
                        script += "carrotcarrotcarrot";
                    } else if (evil.isChecked()){
                        script += "evillaugh";
                    } else if (fire.isChecked()){
                        script += "fire";
                    } else if (hooray.isChecked()){
                        script += "hooray";
                    } else if (munch.isChecked()){
                        script += "munch";
                    } else if (munching.isChecked()){
                        script += "munching";
                    } else if (woof.isChecked()){
                        script += "woof";
                    }
                    if (trigger.equals("onClick")) {
                        currentSelection.setOnClick(script);
                    } else if (trigger.equals("onDrop")) {
                        currentSelection.setOnDrop(script);
                    } else if (trigger.equals("onEnter")) {
                        currentSelection.setOnEnter(script);
                    }
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseDropShape(View curView, String trigger, String action) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View chooseShapeView = inflater.inflate(R.layout.choose_current_shapes,null);
            final PopupWindow pw = new PopupWindow(chooseShapeView, 800, 1000, true);
            TextView displayedOption = (TextView) chooseShapeView.findViewById(R.id.allShapes);
            TextView instrution = chooseShapeView.findViewById(R.id.displayedInstruction);
            instrution.setText("Choose the dropping shape number ");
            String allShapes = "";
            ArrayList<Shape> allShapesOnAllPages = new ArrayList<Shape>();
            int count = 0;
            for (Page temp : pages) {
                allShapes += temp.curName() + ": \n";
                for (int i = 0; i < temp.getShapes().size(); i++) {
                    if ((i >= 1 && (i+1) % 2 == 0) || i == temp.getShapes().size() - 1) {
                        allShapes += (count+1) + ". " + temp.getShapes().get(i).getName() + "\n";
                    } else {
                        allShapes += (count+1) + ". " + temp.getShapes().get(i).getName() + " ";
                    }
                    count++;
                    allShapesOnAllPages.add(temp.getShapes().get(i));
                }
            }
            displayedOption.setText(allShapes);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) chooseShapeView.findViewById(R.id.confirmChooseShape);
            Button cancelBtn = (Button) chooseShapeView.findViewById(R.id.cancelChooseShape);
            EditText inputNum = (EditText) chooseShapeView.findViewById(R.id.shapeNumSelected);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int shapeNum = Integer.valueOf(inputNum.getText().toString());
                    if (shapeNum > curPage.getShapes().size() || shapeNum <= 0) {
                        addToast = Toast.makeText(getApplicationContext(),"Shape doesn't exist!",Toast.LENGTH_LONG);
                        addToast.show();
                        pw.dismiss();
                        return;
                    }

                    Shape cur = allShapesOnAllPages.get(shapeNum - 1);
                    String temp = cur.getName();
                    if (trigger.equals("onDrop")) {
                        if(!currentSelection.checkIfDropEntered()) {
                            currentSelection.setOnDrop(temp);
                        } else {
                            addToast = Toast.makeText(getApplicationContext(),"Warning : Selected dropping shape must be the same as" +
                                    "the first dropped shape",Toast.LENGTH_LONG);
                            addToast.show();
                        }
                        if (action.equals("play")) {
                            chooseSound(curView, trigger);
                        } else if (action.equals("goto")) {
                            chooseGoToPop(curView, trigger);
                        } else {
                            chooseShape(curView, trigger, action);
                        }
                    }
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseShape(View curView, String trigger, String action) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View chooseShapeView = inflater.inflate(R.layout.choose_current_shapes,null);
            final PopupWindow pw = new PopupWindow(chooseShapeView, 1200, 1000, true);
            TextView displayedOption = (TextView) chooseShapeView.findViewById(R.id.allShapes);
            TextView instrution = chooseShapeView.findViewById(R.id.displayedInstruction);
            instrution.setText("Choose the shape number being hiden/shown");
            String allShapes = "";
            ArrayList<Shape> allShapesOnAllPages = new ArrayList<Shape>();
            int count = 0;
            for (Page temp : pages) {
                allShapes += temp.curName() + ": \n";
                for (int i = 0; i < temp.getShapes().size(); i++) {
                    if ((i >= 1 && (i+1) % 2 == 0) || i == temp.getShapes().size() - 1) {
                        allShapes += (count+1) + ". " + temp.getShapes().get(i).getName() + "\n";
                    } else {
                        allShapes += (count+1) + ". " + temp.getShapes().get(i).getName() + " ";
                    }
                    count++;
                    allShapesOnAllPages.add(temp.getShapes().get(i));
                }
            }
            displayedOption.setText(allShapes);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) chooseShapeView.findViewById(R.id.confirmChooseShape);
            Button cancelBtn = (Button) chooseShapeView.findViewById(R.id.cancelChooseShape);
            EditText inputNum = (EditText) chooseShapeView.findViewById(R.id.shapeNumSelected);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int shapeNum = Integer.valueOf(inputNum.getText().toString());
                    if (shapeNum > allShapesOnAllPages.size() || shapeNum <= 0) {
                        addToast = Toast.makeText(getApplicationContext(),"Shape doesn't exist!",Toast.LENGTH_LONG);
                        addToast.show();
                        pw.dismiss();
                        return;
                    }
                    Shape cur = allShapesOnAllPages.get(shapeNum - 1);
                    String temp = cur.getName();
                    if (trigger.equals("onClick")) {
                        currentSelection.setOnClick( action + " " + temp);
                    } else if (trigger.equals("onEnter")) {
                        currentSelection.setOnDrop( action + " " + temp);
                    } else if (trigger.equals("onDrop")) {
                        currentSelection.setOnDrop(action + " " + temp);
                    }
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeProperties (View viewInput) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View propertyView = inflater.inflate(R.layout.change_property_window,null);
            currentSelection = curPage.getSelected();//ts added
            if (currentSelection == null) {
                addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                addToast.show();
                return;
            }

            String prevName = currentSelection.getName();
            String prevX = Float.toString(currentSelection.getX());
            String prevY = Float.toString(currentSelection.getY());
            String prevWidth = Float.toString(currentSelection.getWidth());
            String prevHeight = Float.toString(currentSelection.getHeight());

            EditText name = propertyView.findViewById(R.id.editName);
            EditText X = propertyView.findViewById(R.id.editX);
            EditText Y = propertyView.findViewById(R.id.editY);
            EditText width = propertyView.findViewById(R.id.editWidth);
            EditText height = propertyView.findViewById(R.id.editHeight);
            CheckBox checkMove = (CheckBox) propertyView.findViewById(R.id.moveCheck);
            CheckBox checkVisable = (CheckBox) propertyView.findViewById(R.id.visableCheck);
            TextView scriptText = propertyView.findViewById(R.id.shapeScript);

            name.setText(prevName);
            X.setText(prevX);
            Y.setText(prevY);
            width.setText(prevWidth);
            height.setText(prevHeight);
            if (currentSelection.getScript() != "") scriptText.setText(currentSelection.getScript());
            else scriptText.setText("No script at this moment");
            //ts added
            checkMove.setChecked(currentSelection.getMovable());
            checkVisable.setChecked(currentSelection.getVisible());

            final PopupWindow pw = new PopupWindow(propertyView, 1000, 1000, true);
            pw.showAtLocation(viewInput, Gravity.CENTER, 0, 0);

            Button applyBtn = propertyView.findViewById(R.id.apply_btn);
            Button cancelBtn = propertyView.findViewById(R.id.cancel_btn);
            applyBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (currentSelection == null) return;
                    curPage.deleteShape(currentSelection);//ts added
                    //set name
                    currentSelection.setName(name.getText().toString());
                    //set geometry
                    currentSelection.setLeft(Float.valueOf(X.getText().toString()));
                    currentSelection.setTop(Float.valueOf(Y.getText().toString()));
                    float newRight = Float.valueOf(X.getText().toString()) + Float.valueOf(width.getText().toString());
                    float newBottom = Float.valueOf(Y.getText().toString()) + Float.valueOf(height.getText().toString());
                    currentSelection.setRight(newRight);
                    currentSelection.setBottom(newBottom);
                    //set visable and movable
                    currentSelection.setMovable(checkMove.isChecked());
                    currentSelection.setVisible(checkVisable.isChecked());
                    //ts added
                    curPage.addShape(currentSelection);
                    curPage.setSelected(currentSelection);
                    pw.dismiss();
                    editorView.drawPage(curPage);
                }
            });

            //dismiss the window
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeText (View viewInput) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View textView = inflater.inflate(R.layout.change_text_window,null);
            currentSelection = curPage.getSelected();//ts added
            if (currentSelection == null && !currentSelection.isText()) {
                addToast = Toast.makeText(getApplicationContext(),"No selection or not a valid text!",Toast.LENGTH_SHORT);
                addToast.show();
                return;
            }
            String prevText = currentSelection.getText();
            String prevFont = Integer.toString(currentSelection.getFontSize());

            EditText text = textView.findViewById(R.id.editText);
            EditText font = textView.findViewById(R.id.editFont);

            text.setText(prevText);
            font.setText(prevFont);

            final PopupWindow pw = new PopupWindow(textView, 800, 500, true);
            pw.showAtLocation(viewInput, Gravity.CENTER, 0, 0);

            Button applyBtn = textView.findViewById(R.id.apply_btn);
            Button cancelBtn = textView.findViewById(R.id.cancel_btn);

            applyBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (currentSelection == null) return;
                    curPage.deleteShape(currentSelection);//ts added
                    //set name
                    currentSelection.setText(text.getText().toString());
                    currentSelection.setFontSize(Integer.parseInt(font.getText().toString()));
                    curPage.addShape(currentSelection);
                    curPage.setSelected(currentSelection);
                    pw.dismiss();
                    editorView.drawPage(curPage);
                }
            });

            //dismiss the window
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pw.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearDBItem:
                onClear(view);
                return true;
            case R.id.importGameItem:
                onImport(view);
                return true;
            case R.id.exportGameItem:
                onExport(view);
                return true;

            case R.id.addShape:
                addShapeWindow(view);
                return true;

            case R.id.changeText:
                currentSelection = curPage.getSelected();//ts added
                if (currentSelection != null && currentSelection.isText()) changeText(view);
                return true;

            case R.id.properties:
                currentSelection = curPage.getSelected();//ts added
                if (currentSelection != null) changeProperties(view);
                return true;

            case R.id.deleteShape:
                currentSelection = curPage.getSelected();//ts added
                if (currentSelection != null) {
                    editorView.delete(currentSelection);
                    addToast = Toast.makeText(getApplicationContext(),"Shape deleted!",Toast.LENGTH_SHORT);
                    //ts add
                    currentSelection = null;
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                }
                addToast.show();
                return true;

            case R.id.copyShape:
                currentSelection = curPage.getSelected();//ts added
                if (currentSelection != null) {
                    addToast = Toast.makeText(getApplicationContext(),"Shape copied!",Toast.LENGTH_SHORT);
                    copyShape = currentSelection;
                }
                else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                }
                addToast.show();
                return true;

            case R.id.pasteShape:
                if (copyShape != null) {
                    pasteShapeToPage(copyShape);
                    addToast = Toast.makeText(getApplicationContext(),"Shape Pasted!",Toast.LENGTH_SHORT);
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                }
                addToast.show();
                return true;

            case R.id.cutShape:
                currentSelection = curPage.getSelected();//ts added
                if (currentSelection != null) {
                    addToast = Toast.makeText(getApplicationContext(),"Shape cutted!",Toast.LENGTH_SHORT);
                    copyShape = currentSelection;
                    editorView.delete(currentSelection);
                }
                else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                }
                addToast.show();
                return true;

            case R.id.createPage:
                createPageWindow(view);
                return true;

            case R.id.renamePage:
                renamePage(view);
                return true;

            case R.id.deletePage:
                deletePage(view);
                return true;

            case R.id.switchPage:
                switchPage(view);
                return true;

            case R.id.showScript:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    showScriptPop(view);
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;
            case R.id.onClickGoTo:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseGoToPop(view, "onClick");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onClickShow:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseShape(view, "onClick", "show");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onClickHide:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseShape(view, "onClick", "hide");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;


            case R.id.onClickPlaySound:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseSound(view, "onClick");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onEnterShow:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseShape(view, "onEnter", "show");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onEnterHide:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseShape(view, "onEnter", "hide");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onEnterPlaySound:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseSound(view, "onEnter");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onDropGoTo:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseDropShape(view, "onDrop", "goto");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onDropShow:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseDropShape(view, "onDrop", "show");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onDropHide:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseDropShape(view, "onDrop", "hide");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            case R.id.onDropPlaySound:
                currentSelection = curPage.getSelected();
                if (currentSelection != null) {
                    chooseDropShape(view, "onDrop", "play");
                } else {
                    addToast = Toast.makeText(getApplicationContext(),"No shape selected!",Toast.LENGTH_SHORT);
                    addToast.show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    //database
    //clear all records in the database
    public void onClear(View view) {
        db = openOrCreateDatabase("bunnyWorldDB", MODE_PRIVATE, null);
        Cursor tableCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='bunnyWorld';", null);
        if(tableCursor.getCount() != 0) {
            db.execSQL("DROP TABLE IF EXISTS bunnyWorld;");//clear before setup
            Toast addToast = Toast.makeText(getApplicationContext(), "Clear!" ,Toast.LENGTH_SHORT);
            addToast.show();
        }else{
            Toast addToast = Toast.makeText(getApplicationContext(), "The database is empty!" ,Toast.LENGTH_SHORT);
            addToast.show();
        }

    }
    private boolean fromOutside = false;
    private String jsonStr;
    //import from json string
    public void onImport(View view) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View importGameView = inflater.inflate(R.layout.import_game_str,null);
            final PopupWindow pw = new PopupWindow(importGameView, 1000, 1000, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
            Button confirmBtn = (Button) importGameView.findViewById(R.id.confirm_btn);
            Button cancelBtn = (Button) importGameView.findViewById(R.id.cancel_btn);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    fromOutside = true;
                    EditText jsonStrText = (EditText) importGameView.findViewById(R.id.body);
                    jsonStr = jsonStrText.getText().toString();
                    saveToDatabase();
                    Toast addToast = Toast.makeText(getApplicationContext(), "import game json string" ,Toast.LENGTH_SHORT);
                    addToast.show();
                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String gameInfo;
    private void saveToDatabase() {
        db = openOrCreateDatabase("bunnyWorldDB", MODE_PRIVATE, null);

        //check if the table exists in my database
        Cursor tableCursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='bunnyWorld';", null);
        if(tableCursor.getCount() == 0) {
            db.execSQL("DROP TABLE IF EXISTS bunnyWorld;");//clear before setup
            String setupStr = "CREATE TABLE bunnyWorld ("
                    + "pageName TEXT, pageInfo TEXT,"
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ");";
            db.execSQL(setupStr);//setup db
            System.out.println(setupStr);

        }
        //if the game exists, delete and insert the new one; otherwise, insert the new one.
        if(fromOutside){
            gameInfo = jsonStr;
            gameNameStr = "importedGame";
        }else{
            gameInfo = pageToJson();//get the gameinfo string
        }

        String replaceStr = "REPLACE INTO bunnyWorld VALUES " + "('" + gameNameStr+ "'" + ",'" + gameInfo + "',NULL);";
        if(gameInfo.isEmpty()){System.out.println("gameInfo is  empty");}
        System.out.println("export game: "+ gameNameStr +" "+ gameInfo);
        db.execSQL(replaceStr);//populate db
    }

    //The gameInfo Json object stores a Json array containing all the page json object. Each page object stores the shape json array
    private String pageToJson() {
        JSONObject gameInfoObj = new JSONObject();
        try{

            JSONArray pageArray = new JSONArray();
            for(Page page: pages){
                JSONObject pageObj = new JSONObject();
                pageObj.put("pageName", page.curName());//store pageName
                pageObj.put("shapes", shapesToJson(page.getShapes()));//store shapes
                pageArray.put(pageObj);
            }
            gameInfoObj.put("gameInfo", pageArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("pageString: "+ gameInfoObj.toString());
        Log.i("pageString: "+ gameInfoObj.toString(),"");
        return gameInfoObj.toString();//return gameInfo string
    }
    //page obj stores a shape array
    private String shapesToJson(List<Shape> shapeList) {
        JSONObject pageObj = new JSONObject();
        try{
            JSONArray shapeArray = new JSONArray();
            for(Shape shape : shapeList){
                JSONObject shapeObj = new JSONObject();

                shapeObj.put("shapeName", shape.getName());
                shapeObj.put("whichPage", shape.getPage());
                shapeObj.put("whichImage", shape.getImage());
                shapeObj.put("leftCoordinate", shape.getLeft());
                shapeObj.put("rightCoordinate", shape.getRight());
                shapeObj.put("topCoordinate", shape.getTop());
                shapeObj.put("bottomCoordinate", shape.getBottom());
                shapeObj.put("XCoordinate", shape.getX());
                shapeObj.put("YCoordinate", shape.getY());
                shapeObj.put("width", shape.getWidth());
                shapeObj.put("height", shape.getHeight());
                shapeObj.put("script", shape.getScript());
                shapeObj.put("isMovable", shape.getMovable());
                shapeObj.put("isVisible", shape.getVisible());
                shapeObj.put("text", shape.getText());
                shapeObj.put("fontSize", shape.getFontSize());
                shapeObj.put("isText", shape.isText());

                shapeArray.put(shapeObj);
            }
            pageObj.put("shapeInfo", shapeArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("shapesString: "+ pageObj.toString(),"");
        //System.out.println("shapes on this page: " + pageObj.toString());
        return pageObj.toString();
    }

    public void onSave(View view) {//when clicking save button, save the game in json file, pop up a window for rename the game
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View renameGameView = inflater.inflate(R.layout.rename_game,null);
            final PopupWindow pw = new PopupWindow(renameGameView, 800, 400, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            EditText gameName = (EditText) renameGameView.findViewById(R.id.newGameName);
            Button confirmBtn = (Button) renameGameView.findViewById(R.id.confirmRename);
            Button cancelBtn = (Button) renameGameView.findViewById(R.id.cancelRename);
            TextView displayed = (TextView) findViewById(R.id.displayedGameName);

            confirmBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String name = gameName.getText().toString();
                    if(name.isEmpty()){
                        displayed.setText(gameNameStr);//use default name

                    }else{
                        displayed.setText(name);
                        gameNameStr = name;
                    }

                    saveToDatabase();

                    try {
                        String filename = gameNameStr + ".json";
                        File rootFolder = getApplicationContext().getExternalFilesDir(null);
                        File jsonFile = new File(rootFolder, filename);
                        FileWriter writer = new FileWriter(jsonFile);
                        writer.write(gameInfo);
                        writer.close();

                        Toast addToast = Toast.makeText(getApplicationContext(),filename + " Created on " + rootFolder.toString() ,Toast.LENGTH_SHORT);
                        addToast.show();
                        System.out.println(rootFolder.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });
            //System.out.println("saved!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onExport(View view) {

        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sendMailView = inflater.inflate(R.layout.send_mail,null);
            final PopupWindow pw = new PopupWindow(sendMailView, 1000, 1200, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            EditText toEmailText = (EditText) sendMailView.findViewById(R.id.to_email);
            EditText ccEmailText = (EditText) sendMailView.findViewById(R.id.cc_email);
            EditText subjectText = (EditText) sendMailView.findViewById(R.id.subject);
            EditText bodyText = (EditText) sendMailView.findViewById(R.id.body);
            String[] toEmails = toEmailText.getText().toString().split(";");
            String[] ccEmails = ccEmailText.getText().toString().split(";");
            bodyText.setText(gameInfo);

            Button sendBtn = (Button) sendMailView.findViewById(R.id.send_btn);
            Button cancelBtn = (Button) sendMailView.findViewById(R.id.cancel_btn);

            sendBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
                    intent.putExtra(Intent.EXTRA_CC, ccEmails);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subjectText.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, bodyText.getText().toString());
                    try{
                        startActivity(Intent.createChooser(intent, "How to send email?"));
                    } catch (android.content.ActivityNotFoundException ex){

                    }

                    pw.dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) { pw.dismiss(); }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
