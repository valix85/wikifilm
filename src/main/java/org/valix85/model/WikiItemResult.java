package org.valix85.model;

public class WikiItemResult {

    Long    pageId;
    String  title;
    String  snippet;

    public WikiItemResult(Long pageId, String title, String snippet) {
        this.pageId = pageId;
        this.title = title;
        this.snippet = snippet;
    }

    public WikiItemResult() {
        this.pageId = 0L;
        this.title = "undefined";
        this.snippet = "undefined";
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "WikiItemResult{" +
                "pageId=" + pageId +
                ", title='" + title + '\'' +
                ", snippet='" + snippet.length() + '\'' +
                '}';
    }
}
