package com.scalable.customer.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerRequest {
    private String firstName;
    private String secondName;
    private List<AddressDto> address;
    private Long loanId;
}
