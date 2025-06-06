package com.ayd.invoice_service.Invoice.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.ayd.invoice_service.Invoice.models.InvoiceDetail;
import com.ayd.sharedInvoiceService.dtos.InvoiceDetailResponseDTO;

@Mapper(componentModel = "spring")
public interface InvoiceDetailMapper {
    public InvoiceDetailResponseDTO fromInvoiceDetailToInvoiceDetailResponseDTO(InvoiceDetail invoiceDetail);
    public List<InvoiceDetailResponseDTO> fromInvoiceDetailsToInvoiceDetailResponseDTOs(List<InvoiceDetail> invoiceDetails);
}
