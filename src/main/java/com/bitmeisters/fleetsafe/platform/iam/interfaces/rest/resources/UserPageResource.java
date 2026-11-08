package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A page of users.")
public record UserPageResource(List<UserResource> content, PageMetadataResource page) {}
