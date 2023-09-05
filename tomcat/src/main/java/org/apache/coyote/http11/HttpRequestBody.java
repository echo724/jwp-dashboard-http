package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBody {
    private static final String QUERY_DELIMITER = "&";
    private static final String QUERY_SEPARATOR = "=";
    private final Map<String, String> parameters;

    private HttpRequestBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static HttpRequestBody parseBody(final HttpRequestHeader httpRequestHeader, final BufferedReader bufferedReader) throws IOException {
        if (httpRequestHeader.getContentLength() != 0) {
            final int contentLength = httpRequestHeader.getContentLength();
            final char[] body = new char[contentLength];
            bufferedReader.read(body, 0, contentLength);
            return HttpRequestBody.of(httpRequestHeader.getContentType(), new String(body));
        }
        return HttpRequestBody.empty();
    }

    public static HttpRequestBody empty() {
        return new HttpRequestBody(Map.of());
    }

    public static HttpRequestBody of(final ContentType contentType, final String body) {
        if (contentType == ContentType.FORM_URLENCODED) {
            final HashMap<String, String> parameters = new HashMap<>();
            final String[] tokens = body.split(QUERY_DELIMITER);
            for (String token : tokens) {
                final List<String> keyValues = List.of(token.split(QUERY_SEPARATOR));
                if (keyValues.size() == 2) {
                    parameters.put(keyValues.get(0), keyValues.get(1));
                } else {
                    parameters.put(keyValues.get(0), "");
                }
            }
            return new HttpRequestBody(parameters);
        }
        throw new IllegalArgumentException("지원하지 않는 Content-Type입니다.");
    }

    public String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
    }

    @Override
    public String toString() {
        return "HttpRequestBody{" +
                "parameters=" + parameters +
                '}';
    }
}
