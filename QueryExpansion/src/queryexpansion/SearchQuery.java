/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryexpansion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 *
 * @author srabonti chakraborty
 */
public class SearchQuery {
    
    public final static String indexDirectoryPath = "";
    public final static String stopwordFile = "C:\\all desktop\\Spring 2017\\IR\\HW2\\stopwords";
    public List<SearchEntity> finalList = new ArrayList<SearchEntity>();
    public ArrayList<String> documents = new ArrayList<String>();
    Set<String> stopWords;
    
    public JSONObject getQueryExpansion(String srchQuery) throws IOException, MalformedURLException, JSONException{
        
        //read stop words
        stopWords = readStopWords(stopwordFile);
        QueryExpansion q = new QueryExpansion(stopWords);
        //get top 50 ranked documents from the search result
        //finalList = searchIndex(optimiseQuery(srchQuery));
        //finalList = searchIndex(srchQuery);
        //documents = docContent(finalList);
        documents = searchIndex(srchQuery);
        String expandedQuery = q.getMetricClusterExpansion(srchQuery, documents);
        
        System.out.println(expandedQuery);
        
        JSONArray solrJSONObj = ExternalAPICalls.solrCallQueryExp(expandedQuery);
        
        
        JSONObject Jobj = new JSONObject();
        Jobj.put("eQuery", expandedQuery);
        Jobj.put("qExp", solrJSONObj);
        
        System.out.println(Jobj);
        return Jobj;
        //response.getWriter().print(Jobj.toString());
    }

    //this method reads the stopwords from the file and keeps those in a set
    private Set<String> readStopWords(String stopwordFile) {
        BufferedReader bufferedReader=null;
        this.stopWords = new HashSet<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(stopwordFile));
            
            String line="";
            try {
                while((line = bufferedReader.readLine())!= null && line.length()!= 0){
                    this.stopWords.add(line.trim());
                }
            }
            catch (FileNotFoundException ex1) {
                ex1.printStackTrace();
            } 
        } 
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
        return this.stopWords; 
    }

    //this method retuns top 50 ranked document locations and desccription for the given query
    private ArrayList<String> searchIndex(String srchQuery) throws  IOException, MalformedURLException, JSONException, FileNotFoundException{
        
        ArrayList<String> docs = new ArrayList<String>();
        //final String solrQuery = "http://ec2-54-191-183-57.us-west-2.compute.amazonaws.com:8983/solr/collection1/select?q=title" + URLEncoder.encode(":"+String.join("+", srchQuery.split(" ")), "UTF-8") + "~2&rows=50&wt=json&indent=true";
        final String solrQuery = "http://localhost:8983/solr/collection1/select?q=title:"+ URLEncoder.encode(String.join("+", srchQuery.split(" ")), "UTF-8")+ "&rows="+50+"&wt=json&indent=true";
                
                
        final URL url = new URL(solrQuery);
        final URLConnection connection = url.openConnection();
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        String parsed = response.toString();
        
        final JSONObject json = new JSONObject(parsed);
        final JSONArray arr = json.getJSONObject("response").getJSONArray("docs");  //doc contains the whole data like id, title, decsription, url
      
        for (int i = 0; i < 50; i++){
            //String s = arr.getJSONObject(i).getString("content");
            try{
            docs.add(arr.getJSONObject(i).getString("content"));
            }
            catch(Exception e){
                break;
            }
        }
        return docs;
        
    }
            
}
