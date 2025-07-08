package com.harsh.auth.controller;

import com.harsh.auth.model.VerificationToken;
import com.harsh.auth.repository.VerificationTokenRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import com.harsh.auth.service.UserService;

@Path("/verify")
public class VerificationResource {

    @Inject
    UserService userService;

    @GET
    public Response verifyEmail(@QueryParam("token") String token) {
        try {
            userService.verifyEmail(token);
            return Response.ok("Email verified successfully! You can now login.").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        }
    }
}
