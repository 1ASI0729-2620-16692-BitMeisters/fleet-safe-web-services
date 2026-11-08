package com.bitmeisters.fleetsafe.platform.shared.infrastructure.storage.azure.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Duration READ_URL_LIFETIME = Duration.ofHours(1);
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "application/pdf", ".pdf");

    private final BlobContainerClient container;

    public FileStorageServiceImpl(@Value("${azure.storage.connection-string:}") String connectionString,
                                  @Value("${azure.storage.container:evidences}") String containerName) {
        this.container = StringUtils.hasText(connectionString)
                ? new BlobContainerClientBuilder().connectionString(connectionString).containerName(containerName).buildClient()
                : null;
    }

    @Override
    public Optional<String> store(String contentType, byte[] content) {
        if (container == null) {
            log.warn("File not stored: AZURE_STORAGE_CONNECTION_STRING is not set");
            return Optional.empty();
        }
        try {
            var blob = container.getBlobClient(UUID.randomUUID() + EXTENSIONS.getOrDefault(contentType, ""));
            blob.uploadWithResponse(new BlobParallelUploadOptions(BinaryData.fromBytes(content))
                    .setHeaders(new BlobHttpHeaders().setContentType(contentType)), null, null);
            log.info("File stored in Azure Blob Storage");
            return Optional.of(blob.getBlobUrl());
        } catch (RuntimeException exception) {
            log.warn("File could not be stored in Azure Blob Storage: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String temporaryReadUrl(String fileUrl) {
        if (fileUrl == null || container == null) return fileUrl;
        var containerUrl = container.getBlobContainerUrl() + "/";
        if (!fileUrl.startsWith(containerUrl)) return fileUrl;
        var blob = container.getBlobClient(fileUrl.substring(containerUrl.length()));
        var permission = new BlobSasPermission().setReadPermission(true);
        var sas = blob.generateSas(new BlobServiceSasSignatureValues(OffsetDateTime.now().plus(READ_URL_LIFETIME), permission));
        return blob.getBlobUrl() + "?" + sas;
    }
}
