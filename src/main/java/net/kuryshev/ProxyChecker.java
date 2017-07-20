package net.kuryshev;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;


public class ProxyChecker {

    private static final String URL = "http://yandex.ru";
    private static final int TIMEOUT = 2000;

    public static boolean isOk(String proxyString) throws IOException {
        proxyString = proxyString.trim();
        String host = proxyString.split(":")[0];
        int port = Integer.parseInt(proxyString.split(":")[1]);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        Document document;
        document = Jsoup.connect(URL).
                timeout(TIMEOUT).
                proxy(proxy).
                userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0").
                referrer("").
                get();
        System.out.println(proxy + " is checked");
        return document != null;
    }
}
