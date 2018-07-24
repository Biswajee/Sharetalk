package sharetalks.biswajee.sharetalks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private TextView default_text;
    TextView open_val;
    TextView close_val;
    EditText stock_id;
    Button search;
    JSONObject latest_time_series;
    HttpURLConnection connection;
    BufferedReader br = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Sharetalk Mantra")
                        .setContentText("Plan your business today. Check out your stocks.");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        Toast.makeText(this, "Notification Published", Toast.LENGTH_SHORT).show();
    }


    protected void onStart() {
        stock_id = (EditText) findViewById(R.id.stock_id);
        search = (Button) findViewById(R.id.search_btn);
        //progress = (ProgressBar)findViewById(R.id.prog_bar);
        default_text = (TextView) findViewById(R.id.default_text);
        open_val = (TextView) findViewById(R.id.open_val);
        close_val = (TextView) findViewById(R.id.close_val);
        addNotification();

        super.onStart();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stock_data = stock_id.getText().toString();
                String url_path = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + stock_data.toUpperCase() + "&interval=1min&outputsize=compact&apikey=PJFWQSMODLRUL9F9";
                default_text.setText("Connecting to internet...");
                Toast.makeText(getApplicationContext(), "Searching for " + stock_data.toUpperCase(), Toast.LENGTH_SHORT).show();
                new JSONTask().execute(url_path);
            }
        });


    }


    public class JSONTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            URL url = null;
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                String json_data = buffer.toString();
                System.out.println(json_data);
                JSONObject root = new JSONObject(json_data);
                if(root.has("Time Series (1min)")) {
                    latest_time_series = root.getJSONObject("Time Series (1min)");
                }else{
                    return null;
                }

                /* finding last refreshed data */
                String lastRefreshed;

                JSONObject meta_array = root.getJSONObject("Meta Data");
                lastRefreshed = meta_array.getString("3. Last Refreshed");
                //System.out.println("Time Series - Output : " + lastRefreshed );


                // TODO Display data corresponding to lastRefreshed only...

                JSONObject finalJSON = latest_time_series.getJSONObject(lastRefreshed);
                return finalJSON.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "Error : Connecting to Internet !";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if(result == null) {
                    Toast.makeText(getApplicationContext(), "Enter valid stock ID", Toast.LENGTH_SHORT).show();
                    default_text.setText("No result fetched. Enter valid Stock ID !");
                    open_val.setText("Try Again");
                    close_val.setText("Try Again");
                    return;
                }
                JSONObject res = new JSONObject(result);
                String open = res.getString("1. open");
                String close = res.getString("4. close");
                String high = res.getString("2. high");
                String low = res.getString("3. low");
                String vol = res.getString("5. volume");

                open_val.setText("Rs. " + open);
                close_val.setText("Rs. " + close);

            } catch (JSONException e) {
                default_text.setText("Could not convert malformed JSON String to JSONObject !!!");
            }

            default_text.setText("Result Fetched !");
        }

    }
}