package dynamicdudes.dto;

import dynamicdudes.model.Customer;

public record CustomerResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phoneNumber,
        String countryCode,
        String district,
        String address,
        String role,
        String status) {

    public static CustomerResponse from(Customer customer, String status) {
        return new CustomerResponse(
                customer.getId(), customer.getUsername(), customer.getFullName(),
                customer.getEmail(), customer.getPhoneNumber(), customer.getCountryCode(),
                customer.getDistrict(), customer.getAddress(), customer.getRole(), status);
    }

    public static CustomerResponse from(Customer customer) {
        return from(customer, "ACTIVE");
    }
}
