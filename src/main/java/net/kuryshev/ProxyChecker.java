package net.kuryshev;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;


public class ProxyChecker {

    private String url = "http://yandex.ru";
    private int timeout = 2000;

    public ProxyChecker(String url, int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    public boolean isOk(String proxyString) throws IOException {
        proxyString = proxyString.trim();
        String host = proxyString.split(":")[0];
        int port = Integer.parseInt(proxyString.split(":")[1]);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        Document document;
        document = Jsoup.connect(url).
                timeout(timeout).
                proxy(proxy).
                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0").
                referrer("").
                get();
        return document != null;
    }
}
