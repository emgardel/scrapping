package es.scrap.scrapTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ParallelScraping {
	
    public static final String urls = "https://organii.com/shop/rosto/madara-creme-protector-solar-spf30-rosto/";
    //public static final String urls = "https://organii.com/categoria-produtos/sugestoes-de-natal/,https://organii.com/categoria-produtos/vegan/,https://organii.com/categoria-produtos/zero-waste/,https://organii.com/categoria-produtos/oleos-essenciais/,https://organii.com/categoria-produtos/proteccao-solar/,https://organii.com/categoria-produtos/cabelo/,https://organii.com/categoria-produtos/rosto/";
    public static final int maxPages = 20;
	
	
	public static void main(String args[]) throws Exception {
		Set<Product> products = new HashSet<Product>();
		String contenido = new String(Files.readAllBytes(Paths.get("C:\\Users\\espet\\Desktop\\output.txt")));
		Arrays.asList(contenido.split("\n")).parallelStream().forEach(url -> generate(products, url));
				
		writeCountryListToFile("C:\\Users\\espet\\Desktop\\test2.xls", products);
		
//		Set<String> out = products.stream().map(p -> p.toString()).collect(Collectors.toSet());
//		
//		Files.write(Paths.get("C:\\Users\\espet\\Desktop\\culo.csv"), out);
	}


	private static void generate(Set<Product> products, String url) {

		try {
			String urlPage = String.format(url);
			System.out.println("Página: " + urlPage);
			// Compruebo si me da un 200 al hacer la petición
			if (getStatusConnectionCode(urlPage) == 200) {
				// Obtengo el HTML de la web en un objeto Document2
				Document document = getHtmlDocument(urlPage);

				Elements nombres = document.select("div.b06__product-content");

				if (!nombres.isEmpty()) {
					Product p = new Product();
					Optional<String> name = nombres.stream().map(e -> e.getElementsByClass("b06__product-heading").text()).findFirst();
					Optional<String> desc = nombres.stream().map(e -> e.getElementsByClass("b06__product-content-text").text()).findFirst();
					Optional<String> inci = nombres.stream().map(e -> e.getElementsByClass("b06__ingredients--content").text()).findFirst();

					Optional<String> other = nombres.stream().map(e -> e.getElementsByClass("b06__info--row").text()).findFirst();
					Optional<String> precio = nombres.stream().map(e -> e.getElementsByClass("b06__product-price-price").text()).findFirst();
					p.setName(name.get());
					if (!desc.isEmpty()) {
						p.setDesc((desc.get() + "\n" + "INCI" + "\n" + inci.get()));
					}
					p.setOther(other.get());
					p.setPrice(precio.get());

					System.out.println(p);
					products.add(p);
				} else {
					return;
				}

			} else {
				System.out.println("El Status Code no es OK es: " + getStatusConnectionCode(urlPage));

			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}
	
	public static void writeCountryListToFile(String fileName, Set<Product> countryList) throws Exception{
		FileInputStream fis = new FileInputStream(fileName);
		Workbook workbook = new HSSFWorkbook(fis);
	
		Sheet sheet = workbook.createSheet("Productos");
		
		Iterator<Product> iterator = countryList.iterator();
		
		int rowIndex = 0;
		while(iterator.hasNext()){
			Product country = iterator.next();
			Row row = sheet.createRow(rowIndex++);
			Cell cell0 = row.createCell(0);
			cell0.setCellValue(country.getName());
			Cell cell1 = row.createCell(1);
			cell1.setCellValue(country.getPrice());
			Cell cell2 = row.createCell(2);
			cell2.setCellValue(country.getOther());
			Cell cell3 = row.createCell(3);
			cell3.setCellValue(country.getDesc());
		}
		
		//lets write the excel data to file now
		FileOutputStream fos = new FileOutputStream(fileName);
		workbook.write(fos);
		fos.close();
		workbook.close();
		System.out.println(fileName + " written successfully");
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
            //Response resultImageResponse = Jsoup.connect(imageLocation).cookies(cookies).ignoreContentType(true).execute();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
        }

        return doc;

    }
}