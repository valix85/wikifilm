package org.valix85.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
// import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionUtils {
    private static final String newLine  = System.getProperty("line.separator");
    private static final int TIMEOUT = 8000;

    //final static Logger logger = Logger.getLogger(ConnectionUtils.class);
    private static final Logger logger = LogManager.getLogger(ConnectionUtils.class);

    public String getContentPage(String url){
        URL pageUrl = null;
        try {
            pageUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) pageUrl.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Content-length", "0");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.setInstanceFollowRedirects(true);
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.addRequestProperty("Accept-Language", "it-IT,it,en-US,en;q=0.8");
            con.addRequestProperty("User-Agent", "Mozilla");
            con.connect();
            int status = con.getResponseCode();
            logger.debug("Status code: " + status);
            String content = readStream(con.getInputStream());
            con.disconnect();
            //System.out.println(content);
            return content;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.error("Errore scaricamento dati dalla pagina: {}", url);
        throw new RuntimeException("Errore scaricamento dati dalla pagina: "+url);
    }




    private static String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine + newLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
