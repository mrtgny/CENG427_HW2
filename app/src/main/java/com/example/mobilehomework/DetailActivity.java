package com.example.mobilehomework;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    String baseUrl;
    String detailUrl;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        baseUrl = getResources().getString(R.string.base_url);
        initProgressBar();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        detailUrl = baseUrl + "/muhendislik/bilgisayar/" + intent.getStringExtra("url");
        setTitle(title);

        initFab(detailUrl);

        loadDetails(detailUrl);
    }

    private void initFab(final String detailUrl) {
        FloatingActionButton floatingActionButton = new FloatingActionButton(this);

        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rel.setMargins(15, 15, 15, 15);
        rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        floatingActionButton.setLayoutParams(rel);
        floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);

        floatingActionButton.setImageResource(R.drawable.baseline_open_in_browser_white);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We are showing only toast message. However, you can do anything you need.
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(detailUrl));
                startActivity(i);
            }
        });

        RelativeLayout linearLayout = findViewById(R.id.rlayout);
        if (linearLayout != null) {
            linearLayout.addView(floatingActionButton);
        }

    }

    private void initProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }


    private void switchProgressBar(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void loadDetails(final String url) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    switchProgressBar(true);
                    Document doc = Jsoup.connect(url).get();
                    Elements details = doc.select("#content_left");
                    for (Element p : details.first().children()) {
                        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        final TextView tv = new TextView(getBaseContext());
                        tv.setLayoutParams(lparams);
                        setText(tv, p);
                        tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_border_bottom));
                        tv.setTextSize(14);
                        tv.setTextColor(getResources().getColor(R.color.black));
                        tv.setPadding(32, 32, 0, 32);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        if (tv.getText().length() > 0)
                            addView(tv);

                    }
                    switchProgressBar(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void setText(TextView tv, Element p) {
        if (p.children().size() > 0) {
            setText(tv, p.child(0));
        } else if (p.text().length() > 0) {
            tv.setText(p.text());
        }
    }


    private void addView(final View tv) {
        final LinearLayout layout = findViewById(R.id.layout);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //method which was problematic and was casing a problem
                layout.addView(tv);
            }
        });

    }

}