package tn.iset.m2glnt.client.service.dao.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * JsonBodyHandler — BodyHandler générique pour parser du JSON avec Jackson.
 * Compatible avec les classes simples et les collections (List, Map...).
 */
public class JsonBodyHandler<W> implements HttpResponse.BodyHandler<W> {

    private final ObjectMapper objectMapper;
    private final TypeReference<W> typeReference;

    /**
     * Constructeur pour les types génériques.
     * Exemple : new JsonBodyHandler<>(new TypeReference<List<SlotDTO>>() {})
     */
    public JsonBodyHandler(TypeReference<W> typeReference) {
        this.typeReference = typeReference;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public HttpResponse.BodySubscriber<W> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> upstream =
                HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);

        return HttpResponse.BodySubscribers.mapping(upstream, this::convert);
    }

    private W convert(String body) {
        try {
            return objectMapper.readValue(body, typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur lors du parsing JSON", e);
        }
    }
}
