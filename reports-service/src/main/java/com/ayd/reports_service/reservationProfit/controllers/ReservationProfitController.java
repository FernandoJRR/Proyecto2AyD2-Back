package com.ayd.reports_service.reservationProfit.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayd.reports_service.reservationProfit.dtos.ReservationProfitDTO;
import com.ayd.reports_service.shared.ports.ReportServicePort;
import com.ayd.shared.dtos.PeriodRequestDTO;
import com.ayd.shared.exceptions.ReportGenerationExeption;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservation-profit")
@RequiredArgsConstructor
public class ReservationProfitController {

    private final ReportServicePort<ReservationProfitDTO, PeriodRequestDTO> reservationReportPort;

    @Operation(summary = "Crear un reporte de ganancias por reservas reservas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte creado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ReservationProfitDTO createReservationProfitReport(
            @Valid @RequestBody PeriodRequestDTO filters) {
        ReservationProfitDTO reservations = (ReservationProfitDTO) reservationReportPort.generateReport(filters);
        return reservations;
    }

    @Operation(summary = "Exporta un reporte de ingresos de reservas a pdf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte exportado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportReservationProfitReport(@Valid @RequestBody PeriodRequestDTO filters)
            throws ReportGenerationExeption {
        byte[] pdfBytes = reservationReportPort.generateReportAsPdf(filters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "reporte_reservas_profit.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
