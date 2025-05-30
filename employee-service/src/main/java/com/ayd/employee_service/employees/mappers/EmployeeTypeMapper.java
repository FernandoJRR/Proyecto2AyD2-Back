package com.ayd.employee_service.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.ayd.employee_service.employees.dtos.SaveEmployeeTypeRequestDTO;
import com.ayd.employee_service.employees.models.EmployeeType;
import com.ayd.shared.dtos.IdRequestDTO;
import com.ayd.sharedEmployeeService.dto.EmployeeTypeResponseDTO;

@Mapper(componentModel = "spring")
public interface EmployeeTypeMapper {

    public EmployeeType fromIdRequestDtoToEmployeeType(IdRequestDTO idRequestDTO);

    public EmployeeType fromCreateEmployeeTypeDtoToEmployeeType(SaveEmployeeTypeRequestDTO dto);

    public EmployeeTypeResponseDTO fromEmployeeTypeToEmployeeTypeResponseDto(EmployeeType employeeType);

    public List<EmployeeTypeResponseDTO> fromEmployeeTypeListToEmployeeTypeResponseDtoList(List<EmployeeType> types);
}
