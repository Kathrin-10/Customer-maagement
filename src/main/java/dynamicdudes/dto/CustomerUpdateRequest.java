package dynamicdudes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerUpdateRequest {
    @NotBlank @Size(min=3,max=20) @Pattern(regexp="^[A-Za-z0-9._-]+$")
    private String username;
    @NotBlank @Size(min=2,max=100) private String fullName;
    @NotBlank @Email @Size(max=150) private String email;
    @NotBlank @Pattern(regexp="^[0-9]{7,15}$") private String phoneNumber;
    @NotBlank @Pattern(regexp="^\\+[1-9][0-9]{0,3}$") private String countryCode;
    @NotBlank @Size(min=2,max=100) @Pattern(regexp="^[A-Za-z0-9 .,'-]+$") private String district;
    @Size(max=255) private String address;

    public String getUsername(){return username;}
    public void setUsername(String username){this.username=username;}
    public String getFullName(){return fullName;}
    public void setFullName(String fullName){this.fullName=fullName;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}
    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber=phoneNumber;}
    public String getCountryCode(){return countryCode;}
    public void setCountryCode(String countryCode){this.countryCode=countryCode;}
    public String getDistrict(){return district;}
    public void setDistrict(String district){this.district=district;}
    public String getAddress(){return address;}
    public void setAddress(String address){this.address=address;}
}
