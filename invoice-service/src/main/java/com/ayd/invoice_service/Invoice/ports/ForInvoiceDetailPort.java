package com.ayd.invoice_service.Invoice.ports;

import java.math.BigDecimal;
import java.util.List;

import com.ayd.invoice_service.Invoice.models.Invoice;
import com.ayd.invoice_service.Invoice.models.InvoiceDetail;
import com.ayd.shared.exceptions.NotFoundException;
import com.ayd.sharedInvoiceService.dtos.CreateInvoiceDetailRequestDTO;

public interface ForInvoiceDetailPort {
    public InvoiceDetail getInvoiceDetailById(String id) throws NotFoundException;

    public List<InvoiceDetail> createInvoiceDetail(CreateInvoiceDetailRequestDTO createInvoiceDetailRequestDTO,
            Invoice invoice) throws IllegalArgumentException, NotFoundException;

    public BigDecimal calcValuesInvoiceDetail(List<CreateInvoiceDetailRequestDTO> createInvoiceDetailRequestDTO)
            throws IllegalArgumentException;

    public List<InvoiceDetail> getInvoiceDetailsByInvoiceId(String invoiceId);
}
