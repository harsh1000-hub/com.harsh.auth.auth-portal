package com.harsh.auth.controller;

import com.harsh.auth.dto.LoginRequest;
import com.harsh.auth.model.User;
import com.harsh.auth.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;
import java.time.Duration;

@Path("/login")
@Consumes("application/json")
@Produces("text/plain")
public class LoginResource {

    @Inject
    UserRepository userRepo;

    @POST
    @Transactional
    public Response login(LoginRequest request, @Context RoutingContext ctx) {
        User user = userRepo.findByEmail(request.getEmail());

        if (request.getEmail() == null || request.getEmail().isBlank() || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        if (user == null || !org.mindrot.jbcrypt.BCrypt.checkpw(request.getPassword(), user.passwordHash)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid email or password").build();
        }

        Cookie userCookie = Cookie.cookie("userId", String.valueOf(user.id));
        userCookie.setHttpOnly(true);
        userCookie.setPath("/");
        userCookie.setMaxAge((int) Duration.ofMinutes(30).getSeconds());

        ctx.response().addCookie(userCookie);

        if (!user.isEmailVerified) {
            return Response.ok("You need to validate your email to access the portal").build();
        }

        return Response.ok("Your email is validated. You can access the portal").build();
    }
}
