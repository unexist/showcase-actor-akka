/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo registry
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import dev.unexist.showcase.todo.domain.todo.Todo;
import dev.unexist.showcase.todo.domain.todo.TodoBase;
import dev.unexist.showcase.todo.domain.todo.TodoService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TodoRegistry extends AbstractBehavior<TodoRegistry.Command> {
    sealed public interface Command {}

    public record GetTodos(ActorRef<Todos> replyTo) implements Command {}
    public record CreateTodo(TodoBase todoBase, ActorRef<ActionPerformed> replyTo) implements Command {}
    public record GetTodoResponse(Optional<Todo> maybeTodo) {}
    public record GetTodo(String id, ActorRef<GetTodoResponse> replyTo) implements Command {}
    public record DeleteTodo(String id, ActorRef<ActionPerformed> replyTo) implements Command {}
    public record ActionPerformed(String description) implements Command {}

    public record Todos(List<Todo> todos) {}

    private TodoService todoService;

    private TodoRegistry(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(TodoRegistry::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetTodos.class, this::onGetTodos)
                .onMessage(CreateTodo.class, this::onCreateTodo)
                .onMessage(GetTodo.class, this::onGetTodo)
                .onMessage(DeleteTodo.class, this::onDeleteTodo)
                .build();
    }

    private Behavior<Command> onGetTodos(GetTodos command) {
        command.replyTo().tell(
                new Todos(this.todoService.getAll()));

        return this;
    }

    private Behavior<Command> onCreateTodo(CreateTodo command) {
        this.todoService.create(command.todoBase(), String.valueOf(UUID.randomUUID()));

        command.replyTo().tell(
                new ActionPerformed(String.format("Todo %s created.", command.todoBase().title())));

        return this;
    }

    private Behavior<Command> onGetTodo(GetTodo command) {
        command.replyTo().tell(
                new GetTodoResponse(this.todoService.findById(command.id())));

        return this;
    }

    private Behavior<Command> onDeleteTodo(DeleteTodo command) {
        this.todoService.delete(command.id());

        command.replyTo().tell(
                new ActionPerformed(String.format("Todo %s deleted.", command.id)));

        return this;
    }

}
