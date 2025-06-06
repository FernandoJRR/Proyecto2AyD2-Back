package com.ayd.reservation_service.reservation.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayd.reservation_service.reservation.dtos.CreateReservationOnlineRequestDTO;
import com.ayd.reservation_service.reservation.dtos.CreateReservationPresentialRequestDTO;
import com.ayd.reservation_service.reservation.dtos.PayReservationRequestDTO;
import com.ayd.reservation_service.reservation.mappers.ReservationMapper;
import com.ayd.reservation_service.reservation.models.Reservation;
import com.ayd.reservation_service.reservation.ports.ForReservationPort;
import com.ayd.shared.dtos.PeriodRequestDTO;
import com.ayd.shared.exceptions.DuplicatedEntryException;
import com.ayd.shared.exceptions.NotFoundException;
import com.ayd.sharedReservationService.dto.ReservationResponseDTO;
import com.ayd.sharedReservationService.dto.ReservationSpecificationRequestDTO;
import com.ayd.sharedReservationService.dto.ReservationTimeStatsDTO;
import com.google.zxing.WriterException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ForReservationPort forReservationPort;
    private final ReservationMapper reservationMapper;

    @Operation(summary = "Crear una nueva reservación", description = "Registra una nueva reservación en el sistema. Valida duplicados y existencia de entidades relacionadas como paquetes, horarios o usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Entidad relacionada no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una reservación con los mismos datos"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PostMapping("/presential")
    @PreAuthorize("hasAnyAuthority('CREATE_PRESENTIAL_RESERVATION')")
    public ResponseEntity<byte[]> createPresentialReservation(
            @Valid @RequestBody CreateReservationPresentialRequestDTO createReservationRequestDTO)
            throws DuplicatedEntryException, NotFoundException, WriterException, IOException {
        byte[] pdf = forReservationPort.createPresentialReservation(createReservationRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "reservacion_qr.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @Operation(summary = "Crear una nueva reservación", description = "Registra una nueva reservación en el sistema. Valida duplicados y existencia de entidades relacionadas como paquetes, horarios o usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "404", description = "Entidad relacionada no encontrada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una reservación con los mismos datos"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PostMapping("/online")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<byte[]> createOnlineReservation(
            @Valid @RequestBody CreateReservationOnlineRequestDTO createReservationRequestDTO)
            throws DuplicatedEntryException, NotFoundException {
        byte[] pdf = forReservationPort.createOnlineReservation(createReservationRequestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "reservacion_qr.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @Operation(summary = "Cancelar una reservación", description = "Cancela una reservación existente utilizando su ID. Valida que la reservación exista y que sea posible cancelarla según su estado actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede cancelar la reservación por su estado actual"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PatchMapping("/cancel/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('CANCEL_RESERVATION')")
    public ReservationResponseDTO cancelReservation(@PathVariable String reservationId)
            throws IllegalStateException, NotFoundException {
        Reservation reservation = forReservationPort.cancelReservation(reservationId);
        return reservationMapper.fromReservationToReservationResponseDTO(reservation);
    }

    @Operation(summary = "Marcar una reservación como pagada", description = "Actualiza el estado de una reservación para indicar que ha sido pagada. Requiere el ID de la reservación y valida que pueda realizarse el cambio de estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación marcada como pagada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede marcar como pagada por su estado actual"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PatchMapping("/pay")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('PAY_RESERVATION')")
    public ResponseEntity<byte[]> setPaymentReservation(
            @Valid @RequestBody PayReservationRequestDTO payReservationRequestDTO)
            throws IllegalStateException, NotFoundException, WriterException, IOException {

        byte[] pdf = forReservationPort.payReservation(payReservationRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "reservacion_qr.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @Operation(summary = "Obtener reservación por ID", description = "Devuelve la información detallada de una reservación específica utilizando su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @GetMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponseDTO getReservation(@PathVariable String reservationId) throws NotFoundException {
        Reservation reservation = forReservationPort.getReservation(reservationId);
        return reservationMapper.fromReservationToReservationResponseDTO(reservation);
    }

    @Operation(summary = "Obtener el qr de la reservacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @GetMapping("/get-reservation-qr/{reservationId}")
    public ResponseEntity<byte[]> getReservationQr(@PathVariable String reservationId)
            throws NotFoundException, WriterException, IOException {

        byte[] qr = forReservationPort.getReservationInvoice(reservationId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDispositionFormData("inline", "reservacion_qr.pdf");
        return new ResponseEntity<>(qr, headers, HttpStatus.OK);
    }

    @Operation(summary = "Obtener lista de reservaciones", description = "Devuelve una lista de reservaciones. Se pueden aplicar filtros opcionales mediante el cuerpo de la solicitud.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los filtros"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDTO> getReservations(
            @ModelAttribute ReservationSpecificationRequestDTO reservationSpecificationRequestDTO) {
        List<Reservation> reservations = forReservationPort.getReservations(reservationSpecificationRequestDTO);
        return reservationMapper.fromReservationsToReservationResponseDTOs(reservations);
    }

    @Operation(summary = "Obtener lista de reservaciones ne dos fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los filtros"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @GetMapping("/getReservationsBetweenDates")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationResponseDTO> getReservationsBetweenDates(
            @RequestBody PeriodRequestDTO periodRequestDTO) {
        List<Reservation> reservations = forReservationPort.getReservationsBetweenDates(periodRequestDTO);
        return reservationMapper.fromReservationsToReservationResponseDTOs(reservations);
    }

    @Operation(summary = "Obtener lisa de reservaciones por horas en un rango de fechas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación en los filtros"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @PostMapping("/get-popular-hours-between-dates")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservationTimeStatsDTO> getPopularHoursBetweenDates(
            @RequestBody PeriodRequestDTO periodRequestDTO) {
        List<ReservationTimeStatsDTO> reservations = forReservationPort.getPopularHoursBetweenDates(periodRequestDTO);
        return reservations;
    }

    @Operation(summary = "Eliminar una reservación", description = "Elimina una reservación identificada por su ID. Valida que exista y que pueda ser eliminada según su estado actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservación eliminada exitosamente (sin contenido)"),
            @ApiResponse(responseCode = "404", description = "Reservación no encontrada"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar la reservación por su estado actual"),
            @ApiResponse(responseCode = "500", description = "Error inesperado del servidor")
    })
    @DeleteMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('DELETE_RESERVATION')")
    public void deleteReservation(@PathVariable String reservationId)
            throws IllegalStateException, NotFoundException {
        forReservationPort.deleteReservation(reservationId);
    }
}
