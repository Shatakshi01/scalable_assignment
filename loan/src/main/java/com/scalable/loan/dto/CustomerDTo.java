package com.scalable.loan.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTo {
    private String firstName;
    private String secondName;
    private List<AddressDto> address;
    private Long loanId;
}
