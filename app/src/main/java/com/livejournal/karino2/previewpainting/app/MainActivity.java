package com.livejournal.karino2.previewpainting.app;

import com.livejournal.karino2.previewpainting.app.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
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

    PointF downPos = null;
    float maxDistance = 0;
    Uri uri;
    String mimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();


        setContentView(R.layout.activity_main);

        // final View controlsView = findViewById(R.id.fullscreen_content_controls);
        contentView = findViewById(R.id.fullscreen_content);

        Intent intent = getIntent();

        uri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(uri == null){
            showMessage("not supported. getParcelableExtra fail.");
            finish();
            return;
        }
        mimeType = intent.getType();

        File file = new File(uri.getPath());
        if(!file.exists())
        {
            showMessage("Unknown file source.");
            finish();
            return;
        }

        WebView webView = (WebView)contentView;
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);


        List<String> pathSegments = uri.getPathSegments();
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(uri.getScheme());
        pathBuilder.append(":///");
        for(int i = 0; i < pathSegments.size()-1; i++) {
            pathBuilder.append(pathSegments.get(i));
            pathBuilder.append("/");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><img src=\"");
        builder.append(file.getName().replace("\"", "&quote;"));
        builder.append("\"></body></html>");


        webView.loadDataWithBaseURL(pathBuilder.toString(), builder.toString(), "text/html; charset=UTF-8", null, null);


        ((FixedWebView)contentView).setOnTouchListener2(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        maxDistance = 0;
                        downPos = new PointF(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (getActionBar().isShowing())
                            hideActionBarAndNavigation();
                        maxDistance = Math.max(distance(downPos, event.getX(), event.getY()), maxDistance);
                        break;
                    case MotionEvent.ACTION_UP:
                        maxDistance = Math.max(distance(downPos, event.getX(), event.getY()), maxDistance);
                        boolean nearEnough = maxDistance < 3.0;
                        maxDistance = 0.0f;
                        downPos = null;
                        if(nearEnough) {
                            // showMessage("tap");
                            if (getActionBar().isShowing())
                                hideActionBarAndNavigation();
                            else
                                showActionBarAndNavigation();
                            return true;
                        }
                        break;
                }
                return false;
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

    private float distance(PointF from, float x, float y) {
        if(from == null)
            return 0.0f;
        return (float)(Math.pow(from.x - x, 2)*Math.pow(from.y-y, 2));
    }

    private void showActionBarAndNavigation() {
        getActionBar().show();
        getWindow().setFlags(
                0,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideActionBarAndNavigation() {
        getActionBar().hide();
        contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
               //  |View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

}
