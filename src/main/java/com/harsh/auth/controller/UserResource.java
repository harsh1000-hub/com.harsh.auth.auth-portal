package com.harsh.auth.controller;

import com.harsh.auth.model.User;
import com.harsh.auth.repository.UserRepository;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository userRepo;

    @GET
    public Response getProfile(@Context RoutingContext ctx) {
        Cookie cookie = ctx.request().getCookie("userId");

        if (cookie == null || cookie.getValue() == null || cookie.getValue().isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"You are not logged in\"}")
                    .header("Cache-Control", "no-store")
                    .build();
        }

        Long userId;
        try {
            userId = Long.parseLong(cookie.getValue());
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid session\"}")
                    .header("Cache-Control", "no-store")
                    .build();
        }

        User user = userRepo.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"User not found\"}")
                    .header("Cache-Control", "no-store")
                    .build();
        }

        // ✅ Return only the email (not the full user object with password hash)
        return Response.ok("{\"email\": \"" + user.email + "\"}")
                .header("Cache-Control", "no-store")
                .build();
    }
}
