package org.valix85.utils;


import info.bliki.wiki.model.WikiModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.valix85.model.WikiItemResult;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.select;

public class WikiUtils {

    private static final Logger logger = LogManager.getLogger(WikiUtils.class);

    private static final String MAINSITE = "https://it.wikipedia.org/w/api.php";

    private ConnectionUtils conn = new ConnectionUtils();
    private URLEncoder encoder;
    private URLDecoder decoder;

    public List<WikiItemResult> searchByTitle(String title) {
        logger.debug("searchByTitle " + title);
        try {
            title = encoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String query = MAINSITE + "?action=query&format=json&list=search&srsearch=" + title + "&prop=info|extracts&inprop=url";
        logger.debug(query);
        String content = conn.getContentPage(query);
        //logger.debug(content);
        JSONParser parse = new JSONParser();
        JSONObject jobj = null;
        List<WikiItemResult> elenco = new ArrayList<>();
        try {
            jobj = (JSONObject) parse.parse(content);
            JSONArray risultati = (JSONArray) (((JSONObject) jobj.get("query")).get("search"));
            logger.debug("risultati trovati: " + risultati.size());
            for (Object item : risultati) {
                JSONObject tmp = ((JSONObject) item);
                elenco.add(
                        new WikiItemResult(
                                Long.parseLong("" + tmp.get("pageid")),
                                "" + tmp.get("title"),
                                "" + StringUtils.clearHtmlTag(""+tmp.get("snippet"))
                        )
                );
            }
            //elenco.forEach(System.out::println);
            return elenco;
        } catch (ParseException e) {
            logger.error("{}", e);
        }
        logger.info("Titolo non trovato: " + title);
        return elenco;
    }

    public String getWebpageFromPageId(Long pageId) {
        logger.debug("getWebpageFromPageId " + pageId);
        String query = MAINSITE + "?action=query&prop=info&pageids=" + pageId + "&inprop=url&format=json";
        logger.debug(query);
        String content = conn.getContentPage(query);
        //System.out.println(content);
        JSONParser parse = new JSONParser();
        JSONObject jobj = null;
        try {
            jobj = (JSONObject) parse.parse(content);
            JSONObject ids = (JSONObject) (((JSONObject) jobj.get("query")).get("pages"));
            JSONObject id = (JSONObject) ids.get("" + pageId);
            return "" + id.get("fullurl");
        } catch (ParseException e) {
            logger.error("{}", e);
        }
        return "";
    }

    public Map<String, Object> getDataFromPageId(Long pageId) {
        logger.debug("getDataFromPageId " + pageId);
        String query = MAINSITE + "?format=json&action=query&prop=extracts|revisions&pageids=" + pageId + "&rvprop=content&rvlimit=1&exlimit=1";
        logger.debug(query);
        String content = conn.getContentPage(query);
        //System.out.println(content);
        JSONParser parse = new JSONParser();
        JSONObject jobj = null;
        Map<String, Object> data = new HashMap<>();
        String htmlData = "";
        String wikiRevisions = "";
        try {
            jobj = (JSONObject) parse.parse(content);
            JSONObject ids = (JSONObject) (((JSONObject) jobj.get("query")).get("pages"));
            JSONObject id = (JSONObject) ids.get("" + pageId);
            wikiRevisions = "" + id.get("revisions");
            htmlData = "" + id.get("extract");
        } catch (ParseException e) {
            logger.error("Impossibile estrarre i dati per il pageId: " + pageId);
            logger.error(e);
            throw new RuntimeException("Errore nel recupero dei dati per il pageId " + pageId);
        }
        // provo ad estrarre ogni singolo campo che mi serve
        try {
            htmlData = decoder.decode(htmlData, "UTF-8");
            wikiRevisions = decoder.decode(wikiRevisions, "UTF-8");
            //System.out.println(htmlData);
            //System.out.println(wikiRevisions);
            data.put("trama", StringUtils.clearHtmlTag(getTrama(htmlData)));
            //parseWikiText(wikiRevisions); // abbandonato provo da HTML della pagina
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String getTrama(String htmlData) {
        String inizio = "<h2><span id=\"Trama\">Trama</span></h2>";
        if (htmlData.contains(inizio)) {
            htmlData = htmlData.substring(htmlData.indexOf(inizio) + inizio.length() + 1);
            if (htmlData.contains("<h2>"))
                return htmlData.substring(0, htmlData.indexOf("<h2>")).trim();
        } else {
            logger.info("Trama non presente");
        }
        return "undefined";
    }

    public void parseWikiText(String wikiText) {
        String title = "Lion";

        JSONParser parse = new JSONParser();
        try {
            JSONArray root = (JSONArray) parse.parse(wikiText);
            JSONObject arr = (JSONObject) root.get(0);
            wikiText = (String) arr.get("*");
            //System.out.println(wikiText);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String htmlText = WikiModel.toHtml(wikiText);
        System.out.println(htmlText);


        return;

    }

    public Map<String, Object> getDataFromWebPage(String webpage) {
        logger.debug("getDataFromWebPage " + webpage);
        logger.info("Inizio estrazione dati da " + webpage);
        String query = webpage;
        //logger.debug(query);
        String content = conn.getContentPage(query);
        /*
        try {
            content = new String(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */
        //logger.debug(content);
        Document doc = Jsoup.parse(content);
        Elements sinottico = doc.select("table.sinottico");
        //logger.debug(sinottico);
        Map<String, Object> data = new HashMap<>();
        data.put("titolo", getTitoloFromSinottico(sinottico));
        data.put("titoloOriginale", getDataFromSinottico(sinottico, "Titolo originale"));
        data.put("anno", getDataFromSinottico(sinottico, "Anno"));
        data.put("durata", getDataFromSinottico(sinottico, "Durata").replaceAll("min", "").trim());
        data.put("regia", getDataFromSinottico(sinottico, "Regia"));
        data.put("genere", getDataFromSinottico(sinottico, "Genere"));
        data.put("cast", getCast(sinottico));
        data.put("trama", getTramaFromPage(doc));
        data.put("webpage",webpage);
        data.put("paese",getDataFromSinottico(sinottico, "Paese di produzione"));
        //logger.debug(StringUtils.mapToString(data));
        if (data.get("paese").toString().toLowerCase().contains("italia") && data.get("titoloOriginale").toString().equals("undefined")){
            data.put("titoloOriginale",data.get("titolo"));
        }else if (data.get("titoloOriginale").toString().equals("undefined")){
            data.put("titoloOriginale",data.get("titolo"));
        }
        logger.info("Fine estrazione dati");
        return data;
    }

    private String getTitoloFromSinottico(Elements sinottico) {
        try {
            return sinottico.select(".sinottico_testata i").text();
        } catch (RuntimeException e) {
            logger.warn("Errore estrazione titolo da sinottico " + e);
        }
        return "undefined";
    }

    private String getTitoloOriginaleFromSinottico(Elements sinottico) {
        try {
            Elements th = sinottico.select("tr th");
            Element tmp = th.stream()
                    .filter(t -> t.text().contains("Titolo originale"))
                    .findFirst()
                    .get();
            String titoloOriginale = tmp.parent().select("td").text();
            return titoloOriginale;
        } catch (RuntimeException e) {
            logger.warn("Errore estrazione titolo originale da sinottico " + e);
        }
        return "undefined";
    }

    private String getCast(Elements sinottico) {
        try {
            Elements th = sinottico.select("tr th");
            Element tmp = th.stream()
                    //.peek(System.out::println)
                    .filter(t -> t.text().toLowerCase().contains("interpreti"))
                    .findFirst()
                    .get();
            Elements li = tmp.parent().nextElementSibling().select("li");
            String value = (String) li.stream()
                    .map(item -> item.text().trim())
                    .collect(Collectors.joining("\n"));
            return value;
        } catch (RuntimeException e) {
            logger.warn("Errore estrazione Interpreti e personaggi (cast) da sinottico " + e);
        }
        return "undefined";
    }

    private String getDataFromSinottico(Elements sinottico, String label) {
        try {
            Elements th = sinottico.select("tr th");
            Element tmp = th.stream()
                    //.peek(System.out::println)
                    .filter(t -> t.text().toLowerCase().contains(label.toLowerCase()))
                    .findFirst()
                    .get();
            String value = tmp.parent().select("td").text().trim();
            return value;
        } catch (RuntimeException e) {
            logger.warn("Errore estrazione " + label + " da sinottico " + e);
        }
        return "undefined";
    }

    private String getTramaFromPage(Document page) {
        try {
            Elements h2 = page.select("h2");
            Element tramaStart = h2.stream()
                    .filter(item -> item.text().contains("Trama"))
                    .findFirst()
                    .get();
            Element tmp = tramaStart.nextElementSibling();
            String trama = "";
            do {
                trama += tmp.text().trim() + "\n";
                tmp = tmp.nextElementSibling();
            } while (!tmp.tagName().equals("h2"));
            return trama;
        } catch (RuntimeException e) {
            logger.warn("Errore estrazione Trama dalla pagina " + e);
        }
        return "undefined";
    }


}//end class
