package com.example.mobilehomework;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class NewsFragment extends Fragment {

    String baseUrl;
    String newsUrl;
    View mainView;
    Document response;
    private static ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initProgressBar();
        baseUrl = getResources().getString(R.string.base_url);
        newsUrl = baseUrl + "/muhendislik/bilgisayar";
        mainView = inflater.inflate(R.layout.news_fragment, container, false);
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
        requireContext();
        final LinearLayout layout = mainView.findViewById(R.id.layout);
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        switchProgressBar(true);
                        Document doc = Jsoup.connect(newsUrl).get();
                        if (response != null && doc.toString().equals(response.toString()) && layout.getChildCount() > 0) {
                            switchProgressBar(false);
                            return;
                        }
                        response = doc;
                        clearView();

                        Element announcements = doc.select(".cnContent").first();

                        for (Element div : announcements.children()) {
                            if (div.className().equals("cncItem")) {
                                final Element a = div.child(0).child(1);
                                ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                final Button tv = new Button(mainView.getContext());
                                tv.setLayoutParams(lparams);
                                tv.setText(a.text());
                                if (getContext() != null)
                                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_item_border_bottom));
                                tv.setTextSize(14);
                                tv.setPadding(32, 32, 16, 32);
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sendToDetail(a.text(), a.attr("href"));
                                    }
                                });
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

    private void sendToDetail(String title, String url) {


        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);

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