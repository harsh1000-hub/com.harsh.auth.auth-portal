package com.harsh.auth.controller;

import com.harsh.auth.dto.SignupRequest;
import com.harsh.auth.model.User;
import com.harsh.auth.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/signup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignupResource {

    @Inject
    UserService userService;

    @POST
    @Transactional
    public Response register(@Valid SignupRequest request) {
        if (userService.isEmailRegistered(request.email)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email already registered").build();
        }
        if (request.email == null || request.email.isBlank() || request.password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid input").build();
        }

        User user = userService.registerUser(request.email, request.password);
        String token = userService.sendEmailVerificationToken(user);

        return Response.ok("{\"message\": \"Signup successful!\", \"token\": \"" + token + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}