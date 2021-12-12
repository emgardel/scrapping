package es.scrap.scrapTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class GetAllLinks {

    public static Set<String> uniqueURL = new HashSet<String>();
    public static String my_site;

    public static void main(String[] args) throws IOException {

        GetAllLinks obj = new GetAllLinks();
        my_site = "organii.com/";
        obj.getLinks("https://organii.com/");
        uniqueURL = uniqueURL.stream().filter(s -> !s.contains("#")).collect(Collectors.toSet());
        Files.write(Paths.get("C:\\Users\\espet\\Desktop\\output.txt"), uniqueURL);
    }

//    private Set<String> get_links(String url) {
//        try {
//        	
//            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
//            Elements links = doc.select("a");
//
//            if (links.isEmpty()) {
//               return null;
//            }
//
//            links.parallelStream().map((link) -> link.attr("abs:href")).forEach((this_url) -> {
//                boolean add = uniqueURL.add(this_url);
//                if (add && this_url.contains(my_site)) {
//                    System.out.println(this_url);
//                    get_links(this_url);
//                }
//            });
//
//        } catch (IOException ex) {
//
//        }
//        
//        return uniqueURL.stream().filter(s -> !s.contains("#")).collect(Collectors.toSet());
//
//    }
    
    private void getLinks(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
            Elements links = doc.select("a");

            if (links.isEmpty()) {
               return;
            }

            links.parallelStream().map((link) -> link.attr("abs:href")).forEach((this_url) -> {
                boolean add = uniqueURL.add(this_url);
                if (add && this_url.contains(my_site)) {
                    System.out.println(this_url);
                    getLinks(this_url);
                }
            });

        } catch (IOException ex) {

        }

    }
    
    public static int getStatusConnectionCode(String url) {
		
        Response response = null;
		
        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
        }
        return response.statusCode();
    }
    
    /**
     * Con este método devuelvo un objeto de la clase Document con el contenido del
     * HTML de la web que me permitirá parsearlo con los métodos de la librelia JSoup
     * @param url
     * @return Documento con el HTML
     */
    public static Document getHtmlDocument(String url) {

        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
        }

        return doc;

    }
    
    public static Set<String> getAllLinks() {

        GetAllLinks obj = new GetAllLinks();
        my_site = "organii.com/";
        return null;
    }
}
