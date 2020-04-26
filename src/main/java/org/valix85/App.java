package org.valix85;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.valix85.model.WikiFilm;
import org.valix85.model.WikiItemResult;
import org.valix85.utils.InputUtils;
import org.valix85.utils.StringUtils;
import org.valix85.utils.WikiUtils;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {


    /*
    * per creare l'eseguibile a file unico eseguire:
    * mvn clean compile assembly:single
    * */

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Wikifilm");
        WikiUtils wu = new WikiUtils();
        //Map<String, Object> datas = wu.getDataFromWebPage("https://it.wikipedia.org/wiki/Lion_-_La_strada_verso_casa");
        //Map<String, Object> datas = wu.getDataFromWebPage("https://it.wikipedia.org/wiki/Fast_%26_Furious_8");
        //System.exit(-1);


        String titolo = InputUtils.readLine("Inserisci un titolo");
        List<WikiItemResult> elenco = wu.searchByTitle(titolo);
        for (int i = 0; i < elenco.size(); i++) {
            System.out.println((i+1) + " "+ elenco.get(i).getTitle());
            System.out.println("\t"+StringUtils.reduce(elenco.get(i).getSnippet(),70));
            System.out.println("\n");
        }
        int pos = Integer.parseInt(InputUtils.readLine("Seleziona il risultato corretto"))-1;


        WikiItemResult result = elenco.get(pos);
        String webpage = wu.getWebpageFromPageId(result.getPageId());
        //Map<String, Object> data = wu.getDataFromPageId(elenco.get(0).getPageId());
        Map<String, Object> data = wu.getDataFromWebPage(webpage);

        WikiFilm film = new WikiFilm();
        film.setPageId(result.getPageId());
        film.setTitolo(""+data.get("titolo"));
        film.setSnippet(result.getSnippet());
        film.setWebpage(webpage);
        film.setTitoloOriginale(""+data.get("titoloOriginale"));
        film.setAnno(""+data.get("anno"));
        film.setDurata(""+data.get("durata"));
        film.setRegia(""+data.get("regia"));
        film.setGenere(""+data.get("genere"));
        film.setCast(""+data.get("cast"));
        film.setTrama(""+data.get("trama"));
        //System.out.println(film);
        System.out.println(StringUtils.objectToJson(film));


        //elenco = wu.searchByTitle("lui è peggio di me");


    }

}
