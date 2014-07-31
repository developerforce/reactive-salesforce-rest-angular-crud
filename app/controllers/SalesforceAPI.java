package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

// todo: error handling

public class SalesforceAPI {

    private static final String API_VERSION = "31.0";

    private static final String BASE_URL = "/services/data/v" + API_VERSION + "/";

    private static final String QUERY_URL = BASE_URL + "query/";

    private static final String SOBJECTS_URL = BASE_URL + "sobjects/";

    public static F.Promise<JsonNode> query(String apiKey, String instanceUrl, String query) {
        return WS.url(instanceUrl + QUERY_URL)
                .setHeader("Authorization", "Bearer " + apiKey)
                .setQueryParameter("q", query)
                .get()
                .map(new F.Function<WSResponse, JsonNode>() {
            @Override
            public JsonNode apply(WSResponse wsResponse) throws Throwable {
                return wsResponse.asJson();
            }
        });
    }

    public static F.Promise<JsonNode> create(String apiKey, String instanceUrl, String sobject, JsonNode json) {
        return WS.url(instanceUrl + SOBJECTS_URL + sobject)
                .setHeader("Authorization", "Bearer " + apiKey)
                .post(json)
                .map(new F.Function<WSResponse, JsonNode>() {
                    @Override
                    public JsonNode apply(WSResponse wsResponse) throws Throwable {
                        return wsResponse.asJson();
                    }
                });
    }

    public static F.Promise<JsonNode> update(String apiKey, String instanceUrl, String sobject, String id, JsonNode json) {
        return WS.url(instanceUrl + SOBJECTS_URL + sobject + "/" + id)
                .setHeader("Authorization", "Bearer " + apiKey)
                .patch(json)
                .map(new F.Function<WSResponse, JsonNode>() {
                    @Override
                    public JsonNode apply(WSResponse wsResponse) throws Throwable {
                        return Json.newObject();
                    }
                });
    }

    public static F.Promise<JsonNode> delete(String apiKey, String instanceUrl, String sobject, String id) {
        return WS.url(instanceUrl + SOBJECTS_URL + sobject + "/" + id)
                .setHeader("Authorization", "Bearer " + apiKey)
                .delete()
                .map(new F.Function<WSResponse, JsonNode>() {
                    @Override
                    public JsonNode apply(WSResponse wsResponse) throws Throwable {
                        return Json.newObject();
                    }
                });
    }

}
