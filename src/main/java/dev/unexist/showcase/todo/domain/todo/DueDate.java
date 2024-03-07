/**
 * @package Showcase-Actor-Akka
 *
 * @file DueDate class
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.unexist.showcase.todo.infrastructure.serde.DateDeserializer;
import dev.unexist.showcase.todo.infrastructure.serde.DateSerializer;

import java.time.LocalDate;
import java.util.Objects;

public record DueDate(
        @JsonSerialize(using = DateSerializer.class)
        @JsonDeserialize(using = DateDeserializer.class)
        LocalDate start,

        @JsonSerialize(using = DateSerializer.class)
        @JsonDeserialize(using = DateDeserializer.class)
        LocalDate due)
{
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Constructor
     *
     * @param start
     * @param due
     **/

    public DueDate {
        Objects.requireNonNull(start);
        Objects.requireNonNull(due);
    }
}
