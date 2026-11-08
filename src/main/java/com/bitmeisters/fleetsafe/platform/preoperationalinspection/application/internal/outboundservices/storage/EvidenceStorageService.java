package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.outboundservices.storage;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;

import java.util.Optional;

public interface EvidenceStorageService {

    Optional<FileUrl> store(String contentType, byte[] content);

    String temporaryReadUrl(FileUrl fileUrl);
}
