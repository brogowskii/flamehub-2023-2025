package io.github.flamehub.commons.database;

import dev.morphia.Datastore;
import dev.morphia.InsertManyOptions;
import dev.morphia.query.filters.Filters;
import io.smallrye.common.constraint.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatabaseRepository<E> {

    protected final Datastore datastore;
    protected final Class<E> entityClass;

    public DatabaseRepository(final Datastore datastore, final Class<E> entityClass) {
        this.datastore = datastore;
        this.entityClass = entityClass;
    }

    public E load(final String fieldName, final Object value) {
        return this.datastore.find(entityClass)
                .filter(Filters.eq(fieldName, value))
                .first();
    }

    public E load(final Object value) {
        return load("_id", value);
    }

    public E loadIgnoreCase(final String fieldName, final String value) {
        final String patternString = "(?i)^" + value + "$";
        final Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        return datastore.find(entityClass)
                .filter(Filters.regex(fieldName, pattern))
                .first();

    }

    public List<E> loadAll() {
        return this.datastore.find(entityClass)
                .stream()
                .collect(Collectors.toList());
    }

    public List<E> loadAll(final String fieldName, final Object value) {
        return this.datastore.find(entityClass)
                .filter(Filters.eq(fieldName, value))
                .stream()
                .collect(Collectors.toList());
    }

    public List<E> loadAll(final Object value) {
        return loadAll("_id", value);
    }

    public List<E> loadAllIgnoreCase(final String fieldName, final String value) {
        return datastore.find(entityClass)
                .filter(Filters.regex(fieldName, value).caseInsensitive())
                .stream()
                .collect(Collectors.toList());
    }

    public E save(final E entity) {
        return this.datastore.save(entity);
    }

    public void insert(final E entity) {
        this.datastore.insert(entity);
    }

    public List<E> saveMany(final List<E> entities) {
        return this.datastore.save(entities);
    }

    public void delete(final E entity) {
        this.datastore.delete(entity);
    }


}