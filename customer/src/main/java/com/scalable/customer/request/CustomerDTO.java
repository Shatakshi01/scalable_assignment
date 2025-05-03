package com.scalable.customer.request;

import com.scalable.customer.entity.AddressDetails;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDTO {
    private String firstName;
    private String secondName;
    private List<AddressDto> addresses;
}
