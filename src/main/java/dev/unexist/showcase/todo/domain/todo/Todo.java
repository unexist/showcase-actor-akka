/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo class and aggregate root
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import java.util.Objects;

public record Todo(String id, String title, String description, Boolean done, DueDate dueDate) {

    /**
     * Constructor
     *
     * @param id
     * @param title
     * @param description
     * @param done
     * @param dueDate
     **/

    public Todo {
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
    }

    /**
     * Constructor
     *
     * @param  base  Base entry
     **/

    public Todo(final String id, final TodoBase base) {
        this(id, base.title(), base.description(), base.done(), base.dueDate());
    }
}
