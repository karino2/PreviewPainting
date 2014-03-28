package com.livejournal.karino2.previewpainting.app;

import com.livejournal.karino2.previewpainting.app.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    View contentView;

    Uri uri;
    String mimeType;

    PreviewView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        hideNotification();

        setContentView(R.layout.activity_main);

        contentView = findViewById(R.id.fullscreen_content);
        preview = (PreviewView)contentView;

        Intent intent = getIntent();

        uri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(uri == null){
            showMessage("not supported. getParcelableExtra fail.");
            finish();
            return;
        }
        mimeType = intent.getType();

        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            preview.setImage(bitmap);
            setTitle(options.outWidth + "x" + options.outHeight);
        } catch (FileNotFoundException e) {
            showMessage("file not found: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                showMessage("is close fail. What situation!?");
            }
        }

        preview.setOnTapListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActionBar().isShowing())
                    hideActionBarAndNavigation();
                else
                    showActionBarAndNavigation();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(mimeType);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, "Share Image"));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showActionBarAndNavigation() {
        getActionBar().show();
        getWindow().setFlags(
                0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideActionBarAndNavigation() {
        getActionBar().hide();
        hideNotification();
        /*
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
               //  |View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        */
    }

    private void hideNotification() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


}
