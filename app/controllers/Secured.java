package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.*;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    private static final String TOKEN = "token";
    private static final String INSTANCE_URL = "instance_url";

    @Override
    public String getUsername(Http.Context ctx) {
        if ((ctx.session().get(INSTANCE_URL) == null) || (ctx.session().get(TOKEN) == null)) {
            return null;
        }
        else {
            return "";
        }
    }

    public static String getToken() {
        return Http.Context.current().session().get(TOKEN);
    }

    public static String getInstanceUrl() {
        return Http.Context.current().session().get(INSTANCE_URL);
    }

    public static String getConsumerKey() {
        return Play.application().configuration().getString("salesforce.oauth.consumer-key");
    }

    public static String getConsumerSecret() {
        return Play.application().configuration().getString("salesforce.oauth.consumer-secret");
    }

    public static Boolean isSecure(Http.Request request) {
        return "https".equals(request.getHeader("X-FORWARDED-PROTO"));
    }

    public static String getOauthCallbackUrl(Http.Request request) {
        String url = routes.Secured.oauthCallback(null).absoluteURL(Http.Context.current().request(), isSecure(request));
        return url.replace("?code=", ""); // chop off the empty param
    }


    public static F.Promise<Result> oauthCallback(String code) {
        String url = "https://login.salesforce.com/services/oauth2/token";

        F.Promise<WSResponse> ws = WS.url(url)
                .setQueryParameter("grant_type", "authorization_code")
                .setQueryParameter("code", code)
                .setQueryParameter("client_id", getConsumerKey())
                .setQueryParameter("client_secret", getConsumerSecret())
                .setQueryParameter("redirect_uri", getOauthCallbackUrl(Http.Context.current().request()))
                .post("");

        return ws.map(new F.Function<WSResponse, Result>() {
            @Override
            public Result apply(WSResponse response) throws Throwable {
                JsonNode json = response.asJson();
                String accessToken = json.get("access_token").asText();
                String instanceUrl = json.get("instance_url").asText();

                Http.Context.current().session().put(TOKEN, accessToken);
                Http.Context.current().session().put(INSTANCE_URL, instanceUrl);

                return redirect(routes.Application.index());
            }
        });

    }

    public static Result login() {

        String url = "https://login.salesforce.com/services/oauth2/authorize?response_type=code" +
                "&client_id=" + getConsumerKey() +
                "&redirect_uri=" + getOauthCallbackUrl(Http.Context.current().request());
        return redirect(url);
    }

    public static Result logout() {
        Http.Context.current().session().clear();
        return ok();
    }

}