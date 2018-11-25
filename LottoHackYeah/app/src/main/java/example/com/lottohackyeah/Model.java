package example.com.lottohackyeah;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Model {
    ArrayList<GeolocationRadius> GeolocationList= new ArrayList<>();
    String results;

    public String getResults() {
        return results;
    }

        public void setResults(String results) {
        this.results = results;
    }

    public Model()
    {
        try
        {

            // if your url can contain weird characters you will want to
            // encode it here, something like this:
            // myUrl = URLEncoder.encode(myUrl, "UTF-8");
            //String results = "{\"a\": [{\"La\": 21, \"Lo\": 51, \"Ci\": 100}, {\"La\": 22, \"Lo\": 52, \"Ci\": 101}]}";
            Thread thread = new Thread() {
            @Override
            public void run() {
                String myUrl = "http://192.168.137.1:8000/";
                try {
                    setResults(doHttpUrlConnectionAction(myUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
            thread.start();
            thread.join();


            try {
                JSONObject jo = new JSONObject(results);
                JSONArray ja = jo.getJSONArray("a");
                for(int i=0; i<ja.length(); i++) {
                    double valla = ja.getJSONObject(i).getDouble("La");
                    double vallo = ja.getJSONObject(i).getDouble("Lo");
                    double valci = 200;
                    GeolocationList.add(new GeolocationRadius(valla,vallo,valci));
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<GeolocationRadius> get()
    {
        return GeolocationList;
    }
    /**
     * Returns the output from the given URL.
     *
     * I tried to hide some of the ugliness of the exception-handling
     * in this method, and just return a high level Exception from here.
     * Modify this behavior as desired.
     *
     * @param desiredUrl
     * @return
     * @throws Exception
     */
    private String doHttpUrlConnectionAction(String desiredUrl)
            throws Exception
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            if(connection.getResponseCode() == 200)
            {
                reader = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));
            }
            else
            {
                reader = new BufferedReader(new
                        InputStreamReader(connection.getErrorStream()));
            }
            stringBuilder = new StringBuilder();


            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }
}