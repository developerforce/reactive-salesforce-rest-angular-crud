package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        if ((Secured.getConsumerKey() == null) || (Secured.getConsumerSecret() == null)) {
            return internalServerError(setup.render(Secured.isSecure(request()), Secured.getOauthCallbackUrl(request())));
        } else {
            return ok(index.render());
        }
    }

    @Security.Authenticated(Secured.class)
    public static F.Promise<Result> createContact() {
        F.Promise<JsonNode> createResult = SalesforceAPI.create(Secured.getToken(), Secured.getInstanceUrl(), "Contact", request().body().asJson());
        return createResult.map(new F.Function<JsonNode, Result>() {
            @Override
            public Result apply(JsonNode jsonNode) throws Throwable {
                return ok(jsonNode);
            }
        });
    }

    @Security.Authenticated(Secured.class)
    public static F.Promise<Result> getContact(String id) {
        return F.Promise.pure((Result)new Todo());
    }

    @Security.Authenticated(Secured.class)
     public static F.Promise<Result> getContacts() {
        F.Promise<JsonNode> contacts = SalesforceAPI.query(Secured.getToken(), Secured.getInstanceUrl(), "select Id, FirstName, LastName from Contact");
        return contacts.map(new F.Function<JsonNode, Result>() {
            @Override
            public Result apply(JsonNode jsonNode) throws Throwable {
                return ok(jsonNode.get("records"));
            }
        });
    }

    @Security.Authenticated(Secured.class)
    public static F.Promise<Result> updateContact(String id) {
        final ObjectNode json = (ObjectNode) request().body().asJson();
        JsonNode withoutId = json.deepCopy().without("Id");
        F.Promise<JsonNode> updateResult = SalesforceAPI.update(Secured.getToken(), Secured.getInstanceUrl(), "Contact", id, withoutId);
        return updateResult.map(new F.Function<JsonNode, Result>() {
            @Override
            public Result apply(JsonNode jsonNode) throws Throwable {
                // return the original request json
                return ok(json);
            }
        });
    }

    @Security.Authenticated(Secured.class)
    public static F.Promise<Result> deleteContact(String id) {
        F.Promise<JsonNode> deleteResult = SalesforceAPI.delete(Secured.getToken(), Secured.getInstanceUrl(), "Contact", id);
        return deleteResult.map(new F.Function<JsonNode, Result>() {
            @Override
            public Result apply(JsonNode jsonNode) throws Throwable {
                return ok(jsonNode);
            }
        });
    }

}
