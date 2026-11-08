package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.PageMetadataResource;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UserPageResource;

import java.util.Comparator;
import java.util.List;

public class UserPageResourceFromEntitiesAssembler {

    public static UserPageResource toResourceFromEntities(List<User> users, int page, int size, String sort) {
        var sorted = users.stream().sorted(comparatorFrom(sort)).toList();
        var from = Math.min(page * size, sorted.size());
        var to = Math.min(from + size, sorted.size());
        var content = sorted.subList(from, to).stream().map(UserResourceFromEntityAssembler::toResourceFromEntity).toList();
        var totalPages = (int) Math.ceil((double) sorted.size() / size);
        return new UserPageResource(content, new PageMetadataResource(page, size, sorted.size(), totalPages));
    }

    private static Comparator<User> comparatorFrom(String sort) {
        var parts = (sort == null || sort.isBlank() ? "lastName,asc" : sort).split(",");
        Comparator<User> comparator = switch (parts[0].strip()) {
            case "email" -> Comparator.comparing(user -> user.getEmail().value());
            case "createdAt" -> Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "lastName" -> Comparator.<User, String>comparing(user -> user.getName().lastName(), String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(user -> user.getName().firstName(), String.CASE_INSENSITIVE_ORDER);
            default -> throw new IllegalArgumentException("iam.user.error.sort.field");
        };
        return parts.length > 1 && parts[1].strip().equalsIgnoreCase("desc") ? comparator.reversed() : comparator;
    }
}
