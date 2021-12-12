package es.scrap.scrapTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scraping {
	
    public static final String urls = "https://organii.com/shop/rosto/madara-creme-protector-solar-spf30-rosto/";
    //public static final String urls = "https://organii.com/categoria-produtos/sugestoes-de-natal/,https://organii.com/categoria-produtos/vegan/,https://organii.com/categoria-produtos/zero-waste/,https://organii.com/categoria-produtos/oleos-essenciais/,https://organii.com/categoria-produtos/proteccao-solar/,https://organii.com/categoria-produtos/cabelo/,https://organii.com/categoria-produtos/rosto/";
    public static final int maxPages = 20;
	
	
	public static void main(String args[]) throws IOException {
		Set<Product> products = new HashSet<Product>();
		String contenido = new String(Files.readAllBytes(Paths.get("C:\\Users\\espet\\Desktop\\output.txt")));
		for (String url : contenido.split("\n")) {

			String urlPage = String.format(url);
			System.out.println("Página: " + urlPage);
			try {
				// Compruebo si me da un 200 al hacer la petición
				if (getStatusConnectionCode(urlPage) == 200) {

					// Obtengo el HTML de la web en un objeto Document2
					Document document = getHtmlDocument(urlPage);
//				Set<Product> products = new HashSet<Product>();
//				Elements entradas = document.select("div.b02__products").select("a[href]");

					Elements nombres = document.select("div.b06__product-content");
//				for(Element e : nombres) {
//					Product p = new Product();
//					
//				}
					// b02__heading
					// Paseo cada una de las entradas
					if (!nombres.isEmpty()) {
						Product p = new Product();
						Optional<String> name = nombres.stream().map(e -> e.getElementsByClass("b06__product-heading").text()).findFirst();
						Optional<String> desc = nombres.stream().map(e -> e.getElementsByClass("b06__product-content-text").text()).findFirst();
						Optional<String> inci = nombres.stream().map(e -> e.getElementsByClass("b06__ingredients--content").text()).findFirst();

						Optional<String> other = nombres.stream().map(e -> e.getElementsByClass("b06__info--row").text()).findFirst();
						Optional<String> precio = nombres.stream().map(e -> e.getElementsByClass("b06__product-price-price").text()).findFirst();
						p.setName(name.get());
						p.setDesc(desc.get() + "\n" + "INCI" + "\n" + inci.get());
						p.setOther(other.get());
						p.setPrice(precio.get());

						System.out.println(p);
						products.add(p);
					} else {
						continue;
					}
//                }

//                for (Element elem : entradas) {
//                	
//                	Product p = new Product();	
//                	Elements href = elem.select("a[href]").select("span.b06__product-name");
//                	
//                	p.setName(href.get(0).getElementsByClass("b06__product-name").text());
//                	href= elem.select("a[href]").select("span.b06__product-price");
//                	p.setPrice(href.get(0).getElementsByClass("b06__product-price").text());
//                    System.out.println(p);
//					
//                }

				} else {
					System.out.println("El Status Code no es OK es: " + getStatusConnectionCode(urlPage));

				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}

		}
		Set<String> out = products.stream().map(p -> p.toString()).collect(Collectors.toSet());
		
		Files.write(Paths.get("C:\\Users\\espet\\Desktop\\culo.csv"), out);
	}
	
	
    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
     * EJM:
     * 		200 OK			300 Multiple Choices
     * 		301 Moved Permanently	305 Use Proxy
     * 		400 Bad Request		403 Forbidden
     * 		404 Not Found		500 Internal Server Error
     * 		502 Bad Gateway		503 Service Unavailable
     * @param url
     * @return Status Code
     */
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
}