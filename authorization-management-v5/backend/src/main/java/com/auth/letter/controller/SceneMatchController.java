package com.auth.letter.controller;

import com.auth.letter.dto.*;
import com.auth.letter.service.SceneMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Scene Match Controller
 * REST API endpoints for scene matching
 */
@Component
@Path("/api/scene-match")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SceneMatchController {

    private final SceneMatchService sceneMatchService;

    /**
     * Match scenes based on input parameters
     * POST /api/scene-match
     */
    @POST
    public Response matchScenes(SceneMatchRequestDTO request) {
        if (request.getAuthLetterId() == null) {
            return Response.ok(ApiResponse.error(400, "授权书ID不能为空")).build();
        }

        List<SceneMatchResultVO> results = sceneMatchService.matchScenes(request);
        return Response.ok(ApiResponse.success(results)).build();
    }
}