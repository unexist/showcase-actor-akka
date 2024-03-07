/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo service and domain service
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import java.util.List;
import java.util.Optional;

public class TodoService {
    private final TodoRepository todoRepository;

    /**
     * Constructor
     *
     * @param  todoRepository  A #{@link TodoRepository}
     **/

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Create new {@link Todo} entry and store it in repository
     *
     * @param  base  A {@link TodoBase} entry
     * @param  id    Id for the new entry
     *
     * @return Either {@code true} on success; otherwise {@code false}
     **/

    public boolean create(TodoBase base, String id) {
        Todo todo = new Todo(id, base);

        return this.todoRepository.add(todo);
    }

    /**
     * Update {@link Todo} at with given id
     *
     * @param  id    Id to update
     * @param  base  Values for the entry
     *
     * @return Either {@code true} on success; otherwise {@code false}
     **/

    public boolean update(String id, TodoBase base) {
        Optional<Todo> todo = this.findById(id);
        boolean ret = false;

        if (todo.isPresent()) {
            Todo updatedTodo = new Todo(id, base);

            ret = this.todoRepository.update(updatedTodo);
        }

        return ret;
    }

    /**
     * Delete {@link Todo} with given id
     *
     * @param  id  Id to delete
     *
     * @return Either {@code true} on success; otherwise {@code false}
     **/

    public boolean delete(String id) {
        return this.todoRepository.deleteById(id);
    }

    /**
     * Get all {@link Todo} entries
     *
     * @return List of all {@link Todo}; might be empty
     **/

    public List<Todo> getAll() {
        return this.todoRepository.getAll();
    }

    /**
     * Find {@link Todo} by given id
     *
     * @param  id  Id to look for
     *
     * @return A {@link Optional} of the entry
     **/

    public Optional<Todo> findById(String id) {
        return this.todoRepository.findById(id);
    }
}
