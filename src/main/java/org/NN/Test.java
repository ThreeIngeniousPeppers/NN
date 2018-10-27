package org.NN;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class Test {
    public static void main(String []args)throws MalformedURLException, IOException {
        String url = "https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=730&toTs=1535473419";
        // Get the contents of json as a string using commons IO IOUTils class.
        String genreJson = IOUtils.toString(new URL(url), "UTF-8");

        // create an ObjectMapper instance.
        ObjectMapper mapper = new ObjectMapper();
        // use the ObjectMapper to read the json string and create a tree
        JsonNode root = mapper.readTree(genreJson);
        // lets find out what fields it has

        JsonNode Data = root.get("Data");
        Iterator<JsonNode> DataElements = Data.iterator();
        while (DataElements.hasNext()) {
            JsonNode index = DataElements.next();
            System.out.println(index.get("time"));

//            Iterator<JsonNode> indexElements = index.iterator();
//            while (indexElements.hasNext()) {
//                String element = indexElements.next().asText();
//                System.out.println(element);
//            }
        }

    }
}
