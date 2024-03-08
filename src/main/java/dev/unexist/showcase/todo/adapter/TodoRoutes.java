/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo routes
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import dev.unexist.showcase.todo.domain.todo.TodoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.complete;
import static akka.http.javadsl.server.Directives.concat;
import static akka.http.javadsl.server.Directives.delete;
import static akka.http.javadsl.server.Directives.entity;
import static akka.http.javadsl.server.Directives.get;
import static akka.http.javadsl.server.Directives.onSuccess;
import static akka.http.javadsl.server.Directives.path;
import static akka.http.javadsl.server.Directives.pathEnd;
import static akka.http.javadsl.server.Directives.pathPrefix;
import static akka.http.javadsl.server.Directives.post;
import static akka.http.javadsl.server.Directives.rejectEmptyResponse;

public class TodoRoutes {
    private final static Logger log = LoggerFactory.getLogger(TodoRoutes.class);

    private final ActorRef<TodoRegistry.Command> todoRegistryActor;
    private final Duration askTimeout;
    private final Scheduler scheduler;

    public TodoRoutes(ActorSystem<?> system,
                      ActorRef<TodoRegistry.Command> todoRegistryActor)
    {
        this.todoRegistryActor = todoRegistryActor;
        this.scheduler = system.scheduler();
        this.askTimeout = system.settings().config().getDuration("http.ask-timeout");
    }

    private CompletionStage<TodoRegistry.GetTodoResponse> getTodo(String name) {
        return AskPattern.ask(this.todoRegistryActor,
                ref -> new TodoRegistry.GetTodo(name, ref), this.askTimeout, this.scheduler);
    }

    private CompletionStage<TodoRegistry.ActionPerformed> deleteTodo(String name) {
        return AskPattern.ask(this.todoRegistryActor,
                ref -> new TodoRegistry.DeleteTodo(name, ref), this.askTimeout, this.scheduler);
    }

    private CompletionStage<TodoRegistry.Todos> getTodos() {
        return AskPattern.ask(this.todoRegistryActor,
                TodoRegistry.GetTodos::new, this.askTimeout, this.scheduler);
    }

    private CompletionStage<TodoRegistry.ActionPerformed> createTodo(TodoBase todoBase) {
        return AskPattern.ask(this.todoRegistryActor,
                ref -> new TodoRegistry.CreateTodo(todoBase, ref), this.askTimeout, this.scheduler);
    }

    /**
     * This method creates one route (of possibly many more that will be part of your Web App)
     */
    public Route todoRoutes() {
        return pathPrefix("todos", () ->
                concat(
                        pathEnd(() ->
                                concat(
                                        get(() ->
                                                onSuccess(getTodos(),
                                                        todos -> complete(StatusCodes.OK, todos, Jackson.marshaller())
                                                )
                                        ),
                                        post(() ->
                                                entity(
                                                        Jackson.unmarshaller(TodoBase.class),
                                                        todoBase ->
                                                                onSuccess(createTodo(todoBase), performed -> {
                                                                    log.info("Create result: {}",
                                                                            performed.description());
                                                                    return complete(StatusCodes.CREATED, performed,
                                                                            Jackson.marshaller());
                                                                })
                                                )
                                        )
                                )
                        ),
                        path(PathMatchers.segment(), (String name) ->
                                concat(
                                        get(() ->
                                                rejectEmptyResponse(() ->
                                                        onSuccess(getTodo(name), performed ->
                                                                complete(StatusCodes.OK,
                                                                        performed.maybeTodo(),
                                                                        Jackson.marshaller())
                                                        )
                                                )
                                        ),
                                        delete(() ->
                                                onSuccess(deleteTodo(name), performed -> {
                                                            log.info("Delete result: {}",
                                                                    performed.description());
                                                            return complete(StatusCodes.OK, performed,
                                                                    Jackson.marshaller());
                                                        }
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
