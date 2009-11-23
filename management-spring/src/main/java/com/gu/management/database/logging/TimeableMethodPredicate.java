package com.gu.management.database.logging;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;


public class TimeableMethodPredicate implements Predicate<Method> {

    private final Set<String> timeableMethodNames = newHashSet("execute", "executeQuery", "executeUpdate");

    @Override
    public boolean apply(Method method) {
        return timeableMethodNames.contains(method.getName());
    }
}
