package dk.aau.cs.ds302e18.app.service;

import dk.aau.cs.ds302e18.app.domain.Logbook;
import dk.aau.cs.ds302e18.app.domain.LogbookModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LogbookService {

    private static final String LOGBOOK = "/logbook";
    private static final String SLASH = "/";
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ds.service.url}")
    private String logbookServiceUrl;

    public List<Logbook> getAllLogbooks() {
        String url = logbookServiceUrl + LOGBOOK;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Logbook>>() { }).getBody();
    }

    public Logbook getLogbook(long id) {
        String url = logbookServiceUrl + LOGBOOK + SLASH + id;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Logbook.class).getBody();
    }

    public Logbook addLogbook(LogbookModel lessonModel) {
        String url = logbookServiceUrl + LOGBOOK;
        HttpEntity<LogbookModel> request = new HttpEntity<>(lessonModel, null);
        return this.restTemplate.exchange(url, HttpMethod.POST, request, Logbook.class).getBody();
    }

    public Logbook deleteLogbook(long id) {
        String url = logbookServiceUrl + LOGBOOK + SLASH + id;
        HttpEntity<LogbookModel> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.DELETE, request, Logbook.class).getBody();
    }
}
