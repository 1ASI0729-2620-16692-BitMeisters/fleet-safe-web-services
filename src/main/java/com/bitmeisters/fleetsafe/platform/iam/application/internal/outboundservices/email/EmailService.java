package com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.email;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

public interface EmailService {

    void sendAccountOpenedEmail(EmailAddress recipient, PersonName name, Role role);
}
