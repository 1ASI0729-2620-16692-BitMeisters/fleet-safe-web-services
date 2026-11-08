package com.bitmeisters.fleetsafe.platform.iam.infrastructure.hashing.bcrypt;

import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
