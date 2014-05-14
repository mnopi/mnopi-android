package com.mnopi.mnopi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mnopi.adapters.PageAdapter;
import com.mnopi.data.DataProvider;
import com.mnopi.dialogs.PageDialog;
import com.mnopi.models.PageVisited;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Josko on 13/05/2014.
 */
public class ViewPagesVisitedDeviceActivity extends Activity {

    private ArrayList<PageVisited> pages;
    private ListView listPages;
    private PageAdapter pAdapter;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpages);

        pages = new ArrayList<PageVisited>();
        listPages = (ListView) findViewById(R.id.listPages);
        pAdapter = new PageAdapter(this, R.layout.page_item, pages);
        listPages.setAdapter(pAdapter);
        mContext = this;

        // Get data from content provider
        String[] projection = new String[]{
                DataProvider.PageVisited._ID,
                DataProvider.PageVisited.COL_URL,
                DataProvider.PageVisited.COL_DATE,
                DataProvider.PageVisited.COL_HTML_CODE
        };

        Uri pageVisitedUri = DataProvider.PAGE_VISITED_URI;
        ContentResolver cr = mContext.getContentResolver();

        Cursor cursor = cr.query(pageVisitedUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                String id = cursor.getString(cursor.getColumnIndex(DataProvider.PageVisited._ID));
                String url = cursor.getString(cursor.getColumnIndex(DataProvider.PageVisited.COL_URL));
                String date = cursor.getString(cursor.getColumnIndex(DataProvider.PageVisited.COL_DATE));
                String dateFormated = date.substring(0, 10);
                String hour = date.substring(11, 19);
                String uri = url;
                if(!url.startsWith("http") && !url.startsWith("https")){
                    uri = "http://" + url;
                }
                URL netUrl = null;
                try {
                    netUrl = new URL(uri);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String host = netUrl.getHost();
                if(host.startsWith("www")){
                    host = host.substring("www".length()+1);
                }
                final String domain = host;
                PageVisited pageAux = new PageVisited(url, domain, dateFormated, hour
                        , id, null );
                pages.add(pageAux);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
