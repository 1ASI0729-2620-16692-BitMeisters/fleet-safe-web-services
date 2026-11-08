package com.bitmeisters.fleetsafe.platform.documentation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandFailure;
import com.bitmeisters.fleetsafe.platform.documentation.application.internal.outboundservices.calendar.DocumentationCalendar;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RecalculateDocumentStatusCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentStatus;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.DocumentTypeRepository;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.VehicleDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleDocumentCommandServiceImplTest {

    @Mock
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private DocumentationCalendar documentationCalendar;

    @InjectMocks
    private VehicleDocumentCommandServiceImpl vehicleDocumentCommandService;

    private final LocalDate today = LocalDate.of(2026, 10, 28);
    private final VehicleId truck = new VehicleId(UUID.randomUUID());
    private final DocumentTypeId soat = new DocumentTypeId(UUID.randomUUID());

    private RegisterVehicleDocumentCommand registerSoatExpiringIn(int days) {
        return new RegisterVehicleDocumentCommand(truck, soat, new DocumentNumber("SOAT-2026-004512"),
                new ValidityPeriod(today.minusYears(1), today.plusDays(days)), null);
    }

    @BeforeEach
    void setToday() {
        lenient().when(documentationCalendar.today()).thenReturn(today);
    }

    @Test
    void registersADocumentWithTheStatusOfToday() {
        when(documentTypeRepository.existsById(soat)).thenReturn(true);
        when(vehicleDocumentRepository.save(any(VehicleDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = vehicleDocumentCommandService.handle(registerSoatExpiringIn(10));

        assertThat(result.success()).get().extracting(VehicleDocument::getStatus).isEqualTo(DocumentStatus.EXPIRING);
    }

    @Test
    void rejectsADocumentOfAnUnknownType() {
        when(documentTypeRepository.existsById(soat)).thenReturn(false);

        var result = vehicleDocumentCommandService.handle(registerSoatExpiringIn(10));

        assertThat(result.failure()).get().isInstanceOf(VehicleDocumentCommandFailure.UnknownDocumentType.class);
        verify(vehicleDocumentRepository, never()).save(any());
    }

    @Test
    void doesNotUpdateADocumentThroughAnotherVehicle() {
        var id = UUID.randomUUID();
        when(vehicleDocumentRepository.findById(id)).thenReturn(Optional.of(new VehicleDocument(registerSoatExpiringIn(10), today)));

        var result = vehicleDocumentCommandService.handle(new UpdateVehicleDocumentCommand(id, new VehicleId(UUID.randomUUID()), soat,
                new DocumentNumber("SOAT-2027-000031"), new ValidityPeriod(today, today.plusYears(1)), null));

        assertThat(result.failure()).get().isInstanceOf(VehicleDocumentCommandFailure.NotFound.class);
        verify(vehicleDocumentRepository, never()).save(any());
    }

    @Test
    void recalculationSavesOnlyTheDocumentsWhoseStatusChanged() {
        var stillValid = new VehicleDocument(registerSoatExpiringIn(200), today.minusDays(1));
        var nowExpired = new VehicleDocument(registerSoatExpiringIn(-1), today.minusDays(1));
        when(vehicleDocumentRepository.findAll()).thenReturn(List.of(stillValid, nowExpired));

        var changed = vehicleDocumentCommandService.handle(new RecalculateDocumentStatusCommand());

        assertThat(changed).isEqualTo(1);
        assertThat(nowExpired.getStatus()).isEqualTo(DocumentStatus.EXPIRED);
        verify(vehicleDocumentRepository, times(1)).save(nowExpired);
        verify(vehicleDocumentRepository, never()).save(stillValid);
    }
}
