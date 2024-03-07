/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo base class
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TodoBase(String title, String description, Boolean done, DueDate dueDate) {

    public TodoBase {
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
    }
}
