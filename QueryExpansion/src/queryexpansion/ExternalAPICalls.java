package queryexpansion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author srabo
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExternalAPICalls {
	

	public static JSONArray solrCallQueryExp(String searchText) throws IOException, JSONException {

            final String solrQuery = "http://localhost:8983/solr/collection1/select?q=title"
                            + URLEncoder.encode(":"+searchText, "UTF-8")
                            + "~2&rows=100&wt=json&indent=true";

            System.out.println(solrQuery);
            final URL url = new URL(solrQuery);
            final URLConnection connection = url.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            JSONObject items = null;
            JSONArray dataArray = null;
            if (response != null) {
                    JSONObject obj = new JSONObject(response.toString());
                    // System.out.println(response.toString());
                    items = obj.getJSONObject("response");
                    dataArray = items.getJSONArray("docs");
            }

            return dataArray;
        }
}
