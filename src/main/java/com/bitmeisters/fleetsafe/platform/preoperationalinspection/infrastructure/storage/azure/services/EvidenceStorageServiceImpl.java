package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.storage.azure.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.storage.azure.AzureEvidenceStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class EvidenceStorageServiceImpl implements AzureEvidenceStorageService {

    private static final Duration READ_URL_LIFETIME = Duration.ofHours(1);

    private final BlobContainerClient container;

    public EvidenceStorageServiceImpl(@Value("${azure.storage.connection-string:}") String connectionString,
                                      @Value("${azure.storage.container:evidences}") String containerName) {
        this.container = StringUtils.hasText(connectionString)
                ? new BlobContainerClientBuilder().connectionString(connectionString).containerName(containerName).buildClient()
                : null;
    }

    @Override
    public Optional<FileUrl> store(String contentType, byte[] content) {
        if (container == null) {
            log.warn("Evidence not stored: AZURE_STORAGE_CONNECTION_STRING is not set");
            return Optional.empty();
        }
        try {
            var blob = container.getBlobClient(UUID.randomUUID() + extensionOf(contentType));
            blob.uploadWithResponse(new BlobParallelUploadOptions(BinaryData.fromBytes(content))
                    .setHeaders(new BlobHttpHeaders().setContentType(contentType)), null, null);
            log.info("Evidence stored in Azure Blob Storage");
            return Optional.of(new FileUrl(blob.getBlobUrl()));
        } catch (RuntimeException exception) {
            log.warn("Evidence could not be stored in Azure Blob Storage: {}", exception.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String temporaryReadUrl(FileUrl fileUrl) {
        var containerUrl = container == null ? null : container.getBlobContainerUrl() + "/";
        if (containerUrl == null || !fileUrl.value().startsWith(containerUrl)) return fileUrl.value();
        var blob = container.getBlobClient(fileUrl.value().substring(containerUrl.length()));
        var permission = new BlobSasPermission().setReadPermission(true);
        var sas = blob.generateSas(new BlobServiceSasSignatureValues(OffsetDateTime.now().plus(READ_URL_LIFETIME), permission));
        return blob.getBlobUrl() + "?" + sas;
    }

    private static String extensionOf(String contentType) {
        return "image/png".equalsIgnoreCase(contentType) ? ".png" : ".jpg";
    }
}
