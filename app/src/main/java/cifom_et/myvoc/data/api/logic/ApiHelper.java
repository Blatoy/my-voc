package cifom_et.myvoc.data.api.logic;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */
public abstract class ApiHelper extends AsyncTask<String, Void, String> {
    private Context ctx;

    /**
     * @param ctx -
     */
    public ApiHelper(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * @param result The result from the API
     * @return A JSONObject with the 2 objects: "data", "code"
     */
    // Parse result from the API
    public static JSONObject parseJson(String result) {
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a JSON string from the specified values
     *
     * @param values The key -> value array
     * @return An encoded JSON String
     */
    public static String getJSONStringFromValues(ContentValues values) {
        String url = "";
        JSONObject root = new JSONObject();

        // Based on  http://stackoverflow.com/questions/2390244/how-to-get-the-keys-from-contentvalues
        Set<Map.Entry<String, Object>> s = values.valueSet();

        // Add object to URL
        for (Object value : s) {
            Map.Entry me = (Map.Entry) value;
            try {
                root.put(me.getKey().toString(), me.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        url += root.toString();
        return url;
    }

    /**
     * @param result The raw string from the API
     */
    public abstract void onResponseReceived(String result);

    /**
     * Query the API in another thread
     *
     * @param params #1 param = target API, #2 param = action, #3 param = data
     * @return The raw result of the query
     */
    @Override
    protected String doInBackground(String... params) {
        // Check the connectivity state
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // If wifiSyncOnly and there's no connection, prevent
        if (!mWifi.isConnected() && SettingsHelper.getBoolean(ctx, SettingsStructure.WIFI_SYNC)) {
            return "";
        }

        // Based on http://stackoverflow.com/questions/10500775/parse-json-from-httpurlconnection-object
        HttpURLConnection c = null;

        // Retrive the base path
        String url = SettingsHelper.getString(ctx, SettingsStructure.API_BASE_PATH) + "/api/" + params[0];

        try {
            // Connect to the page
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(3000);
            c.setReadTimeout(5000);

            // Add params to the query
            List<AbstractMap.SimpleEntry> postData = new ArrayList<>();

            if (params.length > 1)
                postData.add(new AbstractMap.SimpleEntry("action", params[1]));
            if (params.length > 2)
                postData.add(new AbstractMap.SimpleEntry("data", params[2]));

            OutputStream os = c.getOutputStream();
            // Send utf8 data
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(postData));
            writer.flush();
            writer.close();

            c.connect();

            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    // Read data
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // Concat data
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    Log.i("API result", sb.toString());
                    return sb.toString();
            }

        } catch (Exception ex) {
            // Log any errors
            Logger.getLogger(ctx.getClass().getName()).log(Level.SEVERE, null, ex);
            return "";
        } finally {
            if (c != null) {
                try {
                    // Try to disconnect
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return "";
    }

    /**
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        onResponseReceived(result);
    }

    // Based on http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post
    private String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        // Add elements on the end of the URL
        for (AbstractMap.SimpleEntry pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey() + "", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue() + "", "UTF-8"));
        }

        return result.toString();
    }
}