import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;


public class Main {
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=nCN56QNaJIeBcLbxsFxsgh9syi5JvxMaz16U6Ou1");

        CloseableHttpResponse response = httpClient.execute(request);

        Nasa nasa = mapper.readValue( // не дает сделать List, т.к. в файле только один объект
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );
        System.out.println(nasa);

        response.close();

        HttpGet request2 = new HttpGet(nasa.getUrl());
        CloseableHttpResponse response2 = httpClient.execute(request2);

        FileOutputStream fos = new FileOutputStream("C:/Games/" + makeFileName(nasa.getUrl()));
        fos.write(response2.getEntity().getContent().readAllBytes());

        response2.close(); // имеет значение очередность закрытия потоков?
        httpClient.close();
        fos.close();
    }

    public static String makeFileName(String url) {
        String[] urlSplit = url.split("/");
        return urlSplit[urlSplit.length - 1];
    }
}
