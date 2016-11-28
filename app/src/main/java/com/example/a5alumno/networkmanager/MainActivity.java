package com.example.a5alumno.networkmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = MyAlternativeThread.class.getSimpleName();

    private MyAlternativeThread myParsingThread;

    private ImageView downImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton downBtn = (ImageButton) this.findViewById(R.id.imgBtnDownload);
        downBtn.setOnClickListener(this);

        final Button reedBtn = (Button) this.findViewById(R.id.btnReadFeed);
        reedBtn.setOnClickListener(this);

        this.downImg = (ImageView) this.findViewById(R.id.imageViewMain);

        final TextView donwTxt = (TextView) this.findViewById(R.id.textViewMain);

    }

    @Override
    public void onClick(View view) {

        ConnectivityManager mConnectionManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectionManager.getActiveNetworkInfo();

        if (view.getId() == R.id.imgBtnDownload) {
            //Descargar Imagen
            Picasso.with(this).load("http://www.tutorialspoint.com/green/images/logo.png").into(this.downImg);
        } else if (view.getId() == R.id.btnReadFeed) {
            //Leer Feed
            final String urlFeed = "https://www.theguardian.com/international/rss";
            new MyAlternativeThread(this).execute(urlFeed);
        }
    }

    private class MyAlternativeThread extends AsyncTask<String, Void, String> {

        private Context mThreadContext;

        public MyAlternativeThread(Context context) {
            this.mThreadContext = context;
        }

        @Override
        protected @Nullable String doInBackground(String... params) {

            URL myUrl = null;
            try {

                myUrl = new URL(params[0]);
                HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();
                myConnection.setRequestMethod("GET");
                myConnection.setDoInput(true);

                myConnection.connect();
                int respCode = myConnection.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_OK) {
                    InputStream myInstream = myConnection.getInputStream();
                    XmlPullParser myXmlParser = Xml.newPullParser();

                    myXmlParser.setInput(myInstream, null);

                    StringBuilder stringBuilder = new StringBuilder("");
                    int event = myXmlParser.nextTag();

                    while (myXmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (event == XmlPullParser.START_TAG && myXmlParser.getName().equals("item")) {
                            myXmlParser.nextTag();
                            myXmlParser.next();
                            stringBuilder.append(myXmlParser.getText()).append("\n");
                        }
                        event = myXmlParser.next();
                    }
                    myInstream.close();
//                    Toast.makeText(this.mThreadContext,stringBuilder.toString(),Toast.LENGTH_SHORT); ARREGLAR
                    Log.i(TAG, stringBuilder.toString());
                    return stringBuilder.toString();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(@Nullable String s) {
            super.onPostExecute(s);

            if (s != null) {
                Toast.makeText(this.mThreadContext, s, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this.mThreadContext, "Can't read feed", Toast.LENGTH_SHORT);
            }
        }
    }
}
