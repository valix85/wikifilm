package org.valix85.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WikiFilm {
    Long    pageId;
    String  webpage;
    String  titolo;
    String  titoloOriginale;
    String  snippet;
    String  trama;
    String  anno;
    String  genere;
    String  cast;
    String  regia;
    String  durata;

    @Override
    public String toString() {
        return "WikiFilm{" +
                "pageId=" + pageId +
                ", webpage='" + webpage + '\'' +
                ", titolo='" + titolo + '\'' +
                ", titoloOriginale='" + titoloOriginale + '\'' +
                ", snippet='" + snippet.length() + '\'' +
                ", trama='" + trama.length() + '\'' +
                ", anno='" + anno + '\'' +
                ", genere='" + genere + '\'' +
                ", cast='" + cast + '\'' +
                ", regia='" + regia + '\'' +
                ", durata='" + durata + '\'' +
                '}';
    }
}
