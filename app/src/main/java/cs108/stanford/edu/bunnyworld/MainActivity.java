package cs108.stanford.edu.bunnyworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.startGame_button);
        popupWindow(view);
    }

    public void showPopupWindow(View view) {
        popupWindow(view);
    }

    public void goEditor(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivity(intent);
    }

    public void goPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    // References:
    // https://stackoverflow.com/questions/5944987/how-to-create-a-popup-window-popupwindow-in-android
    private void popupWindow(View view) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.welcome, null);
            final PopupWindow pw = new PopupWindow(layout, 800, 1000, true);
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);

            Button playLoadedGameBtn = layout.findViewById(R.id.playLoadedGame_button);
            Button createNewGameBtn = layout.findViewById(R.id.createNewGame_button);

            playLoadedGameBtn.setOnClickListener(v -> {
                goPlayer(v);
                pw.dismiss();
            });

            createNewGameBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goEditor(v);
                    pw.dismiss();

                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
