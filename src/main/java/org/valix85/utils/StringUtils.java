package org.valix85.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    private static final Logger logger = LogManager.getLogger(StringUtils.class);

    public static String clearHtmlTag(String html) {
        return html.replaceAll("\\<.*?\\>", "");
    }

    public static String mapToString(Map map){
        String result = (String) map.entrySet()
                .stream()
                .map(entry -> ""+entry)
                .collect(Collectors.joining(", "));
        return "Map["+result+"]";
    }

    public static String objectToJson(Object obj){
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Errore di conversione in JSON dell'oggetto "+obj+" ex: "+e);
            e.printStackTrace();
        }
        return "{\"error\":\"Errore durante la conversione dell'oggetto\"}";
    }

    public static String reduce(String text, int size){
        return text.length()>size ? text.substring(0,size)+"..." : text;
    }
}
