/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryexpansion;

import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author srabo
 */
public class QueryExpansionMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, MalformedURLException, JSONException {
        // TODO code application logic here
        SearchQuery s = new SearchQuery();
        JSONObject jj = s.getQueryExpansion("elephant");
        /*
        JSONArray ansLinks = null;
        JSONArray ansObj = null;
        if (jj != null) {
            JSONObject obj = new JSONObject(jj.toString());
            ansObj = obj.getJSONArray("eQuery");
            //ansLinks = ansObj.getJSONArray("qExp");
        }
*/
        
        //System.out.println(ansObj);
    }
    
}
