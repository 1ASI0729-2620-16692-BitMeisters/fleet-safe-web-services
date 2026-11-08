package com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage;

import java.util.Optional;
import java.util.Set;

public interface FileStorageService {

    Set<String> SUPPORTED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "application/pdf");

    Optional<String> store(String contentType, byte[] content);

    String temporaryReadUrl(String fileUrl);
}
