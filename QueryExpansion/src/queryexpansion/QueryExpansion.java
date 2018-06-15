/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryexpansion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.lemurproject.kstem.KrovetzStemmer;

/**
 *
 * @author srabonti chakraborty
 */
public class QueryExpansion {

    //private HashMap<String, Integer> originalQuery;
    private Set<String> originalQuery;
    private ArrayList<String> [] fileContents;  //stores stems of each documents with stopwords, used for counting
    Set<String> stopWords;
    
    QueryExpansion(Set<String> stopWords) {
        this.stopWords = stopWords;
    }
    //this method returns the result of matric cluster expansion
    //file contains the part of each document (1, 500) which are search result of the original query
    public String getMetricClusterExpansion(String oldQuery, ArrayList<String> documents){
        String newQuery = " ";
        int collectionSize = documents.size();
        int localStemSize =0;
        int count =0;
        fileContents = new ArrayList[collectionSize];
        originalQuery = getQuery(oldQuery); 
        
        //calculate the values of the local vocabulary from the files documents
        //local vocalbuary has <token, <docnumber, count>>
        HashMap<String, HashMap<Integer, Integer>> localVocab = getLocalVocabulary(documents);
        
        
        //stemmed local vocabulary contains <stem, <comma separated tokens>>
        HashMap<String, String> localStemmedVocab = getLocalStemmedVocabulary(localVocab);
        localStemSize = localStemmedVocab.size();   //set S
        
        ArrayList<Integer> newQueryTerms = new ArrayList<Integer>();     //to store expaned query
        //check if the local stem vocabulary contains at least any stem present in the original query
        //if not return the original query, there is no scope of calculating expaned query
        for(String key: originalQuery){
            newQuery += key+" ";
            if(localStemmedVocab.containsKey(key))
                count++;
        }
        if (count ==0)
            return oldQuery;
        
        //for each stem in originalquery calculate correlation between the word and every stem in local stem vocabulary
        for(String key: originalQuery){     //for each word in the original query calculate correlation
            String stem = key;
            KrovetzStemmer stemmer = new KrovetzStemmer();
            stem = stemmer.stem(stem);
            
            //calculate correlation matrix for each terms in the original query at a time
            double [][] correlationMatrix = getCorrelationMatrix(stem, localStemSize, localStemmedVocab, localStemSize, collectionSize, originalQuery );
            
            double top1Correl = 0.0;
            double top2Correl = 0.0;
            int top1Term =0;
            int top2Term =0;
            
            for(int i=0; i<localStemSize; i++){     //1
                double current = 0.0;
                for(int j=0;j <collectionSize; j++){
                    current+= correlationMatrix[i][j];      //calculate total correlation coeff for all the docs for one stem in vocab
                }
                
                //normalization of the correlation coefficient
                //store local vocab stems in sorted order
                Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab);
                int row = 0;
                String t = "";
                for(String term: sortedMap.keySet()){
                    if(row==i){
                        t = term;       //get the local stem vocab record for the current stem
                        break;
                    }
                    row++;
                }
                
                String [] stemSources = localStemmedVocab.get(t).split(",");    //store the words for the stems that appear in vocab
                int KjCount = stemSources.length;
                stemSources = localStemmedVocab.get(key).split(",");    //store words for the stem in query
                int KiCount = stemSources.length;
                int cross = KiCount * KjCount;
                current = current/cross; //normalizes value in current
                
                //get the maximum correl coeff for the query stem from matrix
                //going to consider the top 2 terms from local vocab to expand the query
                
                
                if(current > top1Correl){
                  double temp = top1Correl;
                  int tempRow = top1Term;
                  top1Correl = current;
                  top1Term = i;
                  
                  if(temp > top2Correl ){
                      top2Correl  = temp;
                      top2Term = tempRow;  
                  }
                }else if(current > top2Correl){
                    top2Correl = current;
                    top2Term = i;
                }
                  
            }   //1
            newQueryTerms.add(top1Term);
            newQueryTerms.add(top2Term);    
        }
        //get the new query
        newQuery = getNewQuery(newQueryTerms,localStemmedVocab, oldQuery);
        return newQuery;
    }
    
    private Set<String> getQuery(String line){
        Set<String> query = new HashSet<String>();
        line = line.replace("\n", " ").replace("\r", " ").toLowerCase().replaceAll("[^a-z]", " ");
        StringTokenizer tokens = new StringTokenizer(line);
        while (tokens.hasMoreTokens()) {
            String nextToken = tokens.nextToken();
            if (!stopWords.contains(nextToken) && nextToken.length() > 2) {
                String stem = nextToken;
                KrovetzStemmer stemmer = new KrovetzStemmer();
                stem = stemmer.stem(stem);
                
                if(!query.contains(stem)){
                    query.add(stem);
                }
            }
        }
        return query;
    }

    //this method calculates the local vocabulary
    private HashMap<String, HashMap<Integer, Integer>> getLocalVocabulary(ArrayList<String> documents) {
        KrovetzStemmer stemmer = new KrovetzStemmer();
        HashMap<String, HashMap<Integer, Integer>>localVocab = new HashMap<String, HashMap<Integer, Integer>>();
        
        for (int i = 0; i < documents.size(); i++) { //for each doc in the document array
            fileContents[i] =  new ArrayList<String>();     //store stem from each docemt as separate lists
            String line = documents.get(i);
            
            line = line.replace("\n", " ").replace("\r", " ").toLowerCase().replaceAll("[^a-z]", " ");
            StringTokenizer tokens = new StringTokenizer(line);
            
            while (tokens.hasMoreTokens()) {
                String nextToken = tokens.nextToken();
                String stem = nextToken;
                stem = stemmer.stem(stem);
                
                fileContents[i].add(stem);
                if (!stopWords.contains(nextToken) && nextToken.length() > 2) {
                    if (localVocab.containsKey(nextToken)) {
                        System.out.print(nextToken+ ", ");
                        HashMap<Integer, Integer> t = localVocab.get(nextToken);
                        if(t.containsKey(i))
                            t.put(i, t.get(i)+1);
                        else
                            t.put(i, 1);
                        localVocab.put(nextToken, t);    
                    }
                    else{
                        HashMap<Integer, Integer> t = new HashMap<Integer, Integer>();
                        t.put(i, 1);
                        localVocab.put(nextToken, t);
                    }
                }
                
            }
        }
        return localVocab;
    }

    //calculates the stems of words in local vocabulary and stores related tokens separted by comma
    private HashMap<String, String> getLocalStemmedVocabulary(HashMap<String, HashMap<Integer, Integer>> localVocab) {
        HashMap<String, String> stemmedVocab = new HashMap<String, String>();
        
        for (String key : localVocab.keySet()) {
            String stem = key;
            KrovetzStemmer stemmer = new KrovetzStemmer();
            stem = stemmer.stem(stem);
            
            if(stemmedVocab.containsKey(stem)){
                
                String [] lines = stemmedVocab.get(stem).split(",");
                boolean insert = true;
                for(int i =0; i <lines.length; i ++){
                    if(lines[i].compareTo(key)==0)
                        insert = false;
                }
            
                if(insert){
                    stemmedVocab.put(stem, stemmedVocab.get(stem)+","+key);
                    System.out.print(stem +", ");
                }
                
            }
            else{
                stemmedVocab.put(stem, key);
                System.out.print(stem +", ");
            }
        }
        return stemmedVocab;
    }

    private double[][] getCorrelationMatrix(String stem, int localStemSize, HashMap<String, String> localStemmedVocab, int localStemSize0, int collectionSize, Set<String> originalQuery) {
        
        double [][] matrix = new double[localStemSize][collectionSize];
        Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab); //store local stemmed vocab in sorted order
        KrovetzStemmer stemmer = new KrovetzStemmer();
        
        for(int j=0; j < fileContents.length; j++){     //process for each document
            int stemRow =0;
            String  Ki = stemmer.stem(stem);    //stem of query to be processed
            
            for(String key: sortedMap.keySet()){    //process for each stem in local vocab
                double correllation = 0.0;
                boolean flag = true;
                
                //check if the current word in local vocab is not equal to the word in original qury in hand
                for(String term: originalQuery){ 
                    if(term.compareTo(key)==0)
                        flag= false;
                }
                
                if (flag){  //if the local vocab stem is not same as stem of query
                    String Kj = stemmer.stem(key);  //consider the stem as Kj
                    
                    if(Kj.compareTo(Ki)!=0){
                        if(fileContents[j].contains(Ki)&& fileContents[j].contains(Kj)){    //if the current doc contains both Ki and Kj
                            
                            //store all possible occurrence positions of Ki and Kj
                            ArrayList<Integer> KiPositions = new ArrayList<Integer>();
                            ArrayList<Integer> KjPositions = new ArrayList<Integer>();
                            //store the gaps between all possible positions of Ki and Kj in the current doc
                            ArrayList<Integer> KijGaps = new ArrayList<Integer>();
                            
                            int KiCount = Collections.frequency(fileContents[j], Ki);
                            int KjCount = Collections.frequency(fileContents[j], Kj);
                            
                            //calculate and store all possible positions of Ki and Kj in the current doc
                            for(int g=0; g<fileContents[j].size(); g++){
                                String current = fileContents[j].get(g);
                                if(current.compareTo(Ki)==0){
                                    KiPositions.add(g);    
                                }
                                else if(current.compareTo(Kj)==0){
                                    KjPositions.add(g);    
                                }    
                            }
                            
                            //calculate gaps between all possible sets of Ki and Kj
                            for(int g=0; g<KiCount; g++){  
                                for(int h=0; h<KjCount; h++){
                                    
                                    if(KiPositions.get(g)<KjPositions.get(h)){
                                        int tempGap = KjPositions.get(h) - KiPositions.get(g);
                                        KijGaps.add(tempGap);             
                                    }else if(KiPositions.get(g)>KjPositions.get(h)){
                                        int tempGap = KiPositions.get(g) - KjPositions.get(h);
                                        KijGaps.add(tempGap);
                                    }   
                                }   
                            }
                            
                            //calculate correlation coeff for a document and for one word from the local vocab of stems
                            for(int g=0; g<KijGaps.size(); g++){
                                correllation+= (double)1/KijGaps.get(g);
                            }
                            matrix[stemRow][j]= correllation;   //correlation coeff for a stem in each doc   
                        }
                        stemRow++;
                    }
                }    
            }
   
        }
        return matrix;  
    }

    private String getNewQuery(ArrayList<Integer> newQueryTerms, HashMap<String, String> localStemmedVocab, String oldQuery) {
        String query =oldQuery+" ";
        
        Map<String, String> sortedMap = new TreeMap<String, String>(localStemmedVocab);
        
        //match the index value stored with the sorted stems index
        for(int i=0; i <newQueryTerms.size(); i++){
            int row = 0; 
            int rowNeeded = newQueryTerms.get(i);
        
            for(String key: sortedMap.keySet()){
                if(rowNeeded == row){
                   String word = key;
                   if(!query.contains(word))
                        query+= word+" ";
                 break;
                }
                row++;
            }
        }
        return query;
        
    }
}
