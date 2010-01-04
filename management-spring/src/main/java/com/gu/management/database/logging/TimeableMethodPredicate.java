package com.gu.management.database.logging;

import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Method;
import java.util.Set;

import com.google.common.base.Predicate;


public class TimeableMethodPredicate implements Predicate<Method> {

    private final Set<String> timeableMethodNames = newHashSet("execute", "executeQuery", "executeUpdate");

    @Override
    public boolean apply(Method method) {
        return timeableMethodNames.contains(method.getName());
    }
}
