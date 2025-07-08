package com.harsh.auth.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.Cookie;

@Path("/logout")
public class LogoutResource {

    @GET
    public Response logout(@Context RoutingContext ctx) {
        // Expire the cookie
        Cookie expired = Cookie.cookie("userId", "");
        expired.setMaxAge(0);
        expired.setPath("/");
        ctx.response().addCookie(expired);

        return Response.ok().build();
    }
}
