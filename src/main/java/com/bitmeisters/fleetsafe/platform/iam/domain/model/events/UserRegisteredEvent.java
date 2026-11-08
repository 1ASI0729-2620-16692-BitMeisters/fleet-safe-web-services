package com.bitmeisters.fleetsafe.platform.iam.domain.model.events;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

public record UserRegisteredEvent(EmailAddress email, PersonName name, Role role) {
}
