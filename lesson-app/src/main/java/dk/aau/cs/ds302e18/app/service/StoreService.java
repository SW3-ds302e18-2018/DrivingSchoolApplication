package dk.aau.cs.ds302e18.app.service;

import dk.aau.cs.ds302e18.app.domain.Store;
import dk.aau.cs.ds302e18.app.domain.StoreModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/* Class responsible for reading lesson related data from the 8200 server. */
@Service
public class StoreService
{
    private static final String REQUESTS = "/storeadmin";
    private static final String SLASH = "/";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ds.service.url}")
    private String storeServiceUrl;

    /**
     * Retrieves an list of store from the 8200 server and returns it as list of lessons in the format specified in the Lesson class.
     * @return
     */
    public List<Store> getAllStoreRequests() {
        String url = storeServiceUrl + REQUESTS;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Store>>() { }).getBody();
    }

    /**
     * Returns an store object from the 8100 server that has just been added.
     * @param storeModel
     * @return
     */
    public Store addStoreRequest(StoreModel storeModel) {
        String url = storeServiceUrl + REQUESTS;
        HttpEntity<StoreModel> request = new HttpEntity<>(storeModel, null);
        return this.restTemplate.exchange(url, HttpMethod.POST, request, Store.class).getBody();
    }

    /**
     *
     * @param id
     * @return
     */
    public Store getStoreRequest(long id) {
        String url = storeServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Store.class).getBody();
    }

    /**
     *
     * @param id
     * @param storeModel
     * @return
     */
    public Store acceptStoreRequest(long id, StoreModel storeModel) {
        String url = storeServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<StoreModel> request = new HttpEntity<>(storeModel, null);
        return this.restTemplate.exchange(url, HttpMethod.PUT, request, Store.class).getBody();
    }
}
