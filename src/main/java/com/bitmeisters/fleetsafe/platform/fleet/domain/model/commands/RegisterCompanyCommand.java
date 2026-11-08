package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;

import java.util.regex.Pattern;

/**
 * Registers the company of the signed-in administrator. The identifier is the one the users of the
 * company already carry in their token, so it is never chosen in the request.
 */
public record RegisterCompanyCommand(
        CompanyId companyId,
        CompanyName name,
        TaxId taxId,
        String address,
        String phone,
        String email) {

    public static final int ADDRESS_MAX_LENGTH = 200;
    public static final int PHONE_MAX_LENGTH = 30;
    public static final int EMAIL_MAX_LENGTH = 120;

    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+$");

    public RegisterCompanyCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (name == null) throw new IllegalArgumentException("fleet.company.error.name.required");
        if (taxId == null) throw new IllegalArgumentException("fleet.company.error.tax-id.required");
        address = optional(address, ADDRESS_MAX_LENGTH, "fleet.company.error.address.size");
        phone = optional(phone, PHONE_MAX_LENGTH, "fleet.company.error.phone.size");
        email = optional(email, EMAIL_MAX_LENGTH, "fleet.company.error.email.size");
        if (email != null && !EMAIL_FORMAT.matcher(email).matches()) {
            throw new IllegalArgumentException("fleet.company.error.email.invalid");
        }
    }

    private static String optional(String value, int maxLength, String sizeKey) {
        if (value == null || value.isBlank()) return null;
        var stripped = value.strip();
        if (stripped.length() > maxLength) throw new IllegalArgumentException(sizeKey);
        return stripped;
    }
}
