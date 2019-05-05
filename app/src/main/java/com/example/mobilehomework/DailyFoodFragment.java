package com.example.mobilehomework;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DailyFoodFragment extends Fragment {

    String baseUrl;
    String sksUrl;
    View mainView;
    Document response;
    private static ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initProgressBar();

        baseUrl = getResources().getString(R.string.base_url);
        sksUrl = baseUrl + "/sks/";
        mainView = inflater.inflate(R.layout.daily_food_fragment, container, false);
        return mainView;
    }

    private void initProgressBar() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }


    private void switchProgressBar(final boolean show) {
        getActivity().runOnUiThread(new Runnable() {
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

    @Override
    public void onStart() {
        super.onStart();


        final LinearLayout layout = mainView.findViewById(R.id.layout);
        Activity activity = getActivity();

        if (activity != null && isAdded()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        switchProgressBar(true);
                        Document doc = Jsoup.connect(sksUrl).get();
                        if (response != null && doc.toString().equals(response.toString()) && layout.getChildCount() > 0) {
                            switchProgressBar(false);
                            return;
                        }
                        response = doc;
                        clearView();

                        Elements tables = doc.select("table");
                        Element tbody = tables.get(1).child(0);
                        for (int i = 0; i < tbody.children().size(); i++) {
                            Element td = tbody.child(i);
                            if (i == 0) {
                                Element image = td.child(0).child(0).child(0).child(0);
                                String src = image.attr("src");
                                URL url = new URL(baseUrl + src);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setDoInput(true);
                                connection.connect();
                                InputStream input = connection.getInputStream();
                                Bitmap imageBitmap = BitmapFactory.decodeStream(input);
                                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                final ImageView iv = new ImageView(mainView.getContext());
                                iv.setImageBitmap(imageBitmap);
                                iv.setPadding(8, 64, 8, 64);
                                iv.setBackgroundColor(getResources().getColor(R.color.white));
                                addView(iv);
                            } else if (i == 1) {
                                Element date = td.child(0).child(0).child(0);
                                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                final TextView tv = new TextView(mainView.getContext());
                                tv.setTypeface(Typeface.DEFAULT_BOLD);
                                tv.setTextSize(24);
                                tv.setLayoutParams(lparams);
                                tv.setGravity(Gravity.CENTER);
                                tv.setText(date.text());
                                tv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                tv.setTextColor(getResources().getColor(R.color.white));
                                tv.setPadding(8, 32, 8, 32);

                                addView(tv);
                            } else {
                                Element food = td.child(0).child(0).child(0);
                                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                final TextView tv = new TextView(mainView.getContext());
                                tv.setLayoutParams(lparams);
                                tv.setText(food.text());
                                tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_border_bottom));
                                tv.setGravity(Gravity.CENTER);
                                tv.setTextSize(18);
                                tv.setPadding(0, 32, 0, 32);
                                addView(tv);

                            }

                        }
                        switchProgressBar(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }


    private void clearView() {
        final LinearLayout layout = mainView.findViewById(R.id.layout);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //method which was problematic and was casing a problem
                    layout.removeAllViews();
                }
            });
    }


    private void addView(final View tv) {
        final LinearLayout layout = mainView.findViewById(R.id.layout);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //method which was problematic and was casing a problem
                    layout.addView(tv);
                }
            });

    }


}