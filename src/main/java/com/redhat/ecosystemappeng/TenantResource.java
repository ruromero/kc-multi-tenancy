package com.redhat.ecosystemappeng;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.oidc.IdToken;

@Path("/app/{tenant}")
public class TenantResource {

    /**
     * Injection point for the ID Token issued by the OpenID Connect Provider
     */
    @Inject
    @IdToken
    JsonWebToken idToken;

    /**
     * Injection point for the Access Token issued by the OpenID Connect Provider
     */
    @Inject
    JsonWebToken accessToken;

    /**
     * Returns the ID Token info. This endpoint exists only for demonstration purposes, you should not
     * expose this token in a real application.
     *
     * @return ID Token info
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIdTokenInfo(@PathParam("tenant") String tenant) {
        StringBuilder response = new StringBuilder().append("<html>")
                .append("<body>");

        response.append("<h2>Welcome, ").append(this.idToken.getClaim("email").toString()).append("</h2>\n");
        response.append("<h3>You are accessing the application within tenant <b>").append(idToken.getIssuer())
                .append(" boundaries</b></h3>\n")
                .append("<a href=\"")
                .append(tenant).append("/logout\">Logout</a>");

        return response.append("</body>").append("</html>").toString();
    }

        /**
     * Returns the Access Token info. This endpoint exists only for demonstration purposes, you should not
     * expose this token in a real application.
     *
     * @return Access Token info
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("bearer")
    public String getAccessTokenInfo() {
        StringBuilder response = new StringBuilder().append("<html>")
                .append("<body>");

        response.append("<h2>Welcome, ").append(this.accessToken.getClaim("email").toString()).append("</h2>\n");
        response.append("<h3>You are accessing the application within tenant <b>").append(accessToken.getIssuer()).append(" boundaries</b></h3>");

        return response.append("</body>").append("</html>").toString();
    }
}