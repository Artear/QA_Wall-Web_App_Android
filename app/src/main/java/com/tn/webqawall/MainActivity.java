package com.tn.webqawall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.gson.Gson;
import com.tn.webqawall.socket.event.Page;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class MainActivity extends FragmentActivity
{

    private final static String URL_FROM_INTENT = "URL_FROM_INTENT";
    private String url;
    private ViewPager viewPager;
    private MyPagerAdapter adapterViewPager;
    private WebViewFragment webViewFragment;
    private InfoFragment infoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("WQWLog", "Action Value: " + getIntent().getAction());
        Log.d("WQWLog", "Has Extras: " + (getIntent().getExtras() != null));

        if (getIntent().getAction().equals("android.intent.action.MAIN") &&
                getIntent().getExtras() != null)
        {

            Log.d("WQWLog", "Extra Value on URL_FROM_INTENT: " + getIntent().getExtras().get(URL_FROM_INTENT));
            url = getIntent().getExtras().getString(URL_FROM_INTENT);
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_activity);

        webViewFragment = new WebViewFragment();
        infoFragment = new InfoFragment();

        viewPager = (ViewPager) findViewById(R.id.view_pager_qa_wall);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), "http://tn.com.ar");
        viewPager.setAdapter(adapterViewPager);

        App.getSocket().on(Page.EVENT_NAME, new Emitter.Listener()
        {
            @Override
            public void call(final Object... args)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Page pageEvent = new Gson().fromJson(String.valueOf(args[0]), Page.class);

                        webViewFragment.setUrl(pageEvent.getUrl());
                    }
                });
            }
        });

    }

    public class MyPagerAdapter extends FragmentPagerAdapter
    {
        private final int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fm, String url)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return webViewFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return infoFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return NUM_ITEMS;
        }
    }

    @Override
    public void onBackPressed()
    {
        Fragment webview = getSupportFragmentManager().findFragmentByTag("webview");

        if (webview instanceof WebViewFragment)
        {
            if (((WebViewFragment) webview).canGoBack())
            {
                ((WebViewFragment) webview).goBack();
            } else
            {
                super.onBackPressed();
            }
        }
    }

}
