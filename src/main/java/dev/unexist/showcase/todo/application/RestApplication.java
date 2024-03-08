/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo application
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.application;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import dev.unexist.showcase.todo.adapter.TodoRegistry;
import dev.unexist.showcase.todo.adapter.TodoRoutes;
import dev.unexist.showcase.todo.domain.todo.TodoRepository;
import dev.unexist.showcase.todo.domain.todo.TodoService;
import dev.unexist.showcase.todo.infrastructure.persistence.ListTodoRepository;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

public class RestApplication {

    /**
     * Start the HTTP server
     *
     * @param  route   A #{@link Route}
     * @param  system  A #{@link ActorSystem}
     **/

    static void startHttpServer(Route route, ActorSystem<?> system) {
        Config configFactory = ConfigFactory.load();

        CompletionStage<ServerBinding> futureBinding =
                Http.get(system).newServerAt(configFactory.getString("http.host"),
                        configFactory.getInt("http.port")).bind(route);

        futureBinding.whenComplete((binding, exception) -> {
            if (null != binding) {
                InetSocketAddress address = binding.localAddress();

                system.log().info("Server online at http://{}:{}/",
                    address.getHostString(),
                    address.getPort());
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception);

                system.terminate();
            }
        });
    }

    /**
     * Main class
     *
     * @param  args  Passed commandline arguments
     * @throws Exception
     **/

    public static void main(String[] args) throws Exception {
        Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {
            TodoRepository todoRepository = new ListTodoRepository();
            TodoService todoService = new TodoService(todoRepository);

            ActorRef<TodoRegistry.Command> todoRegistryActor =
                context.spawn(TodoRegistry.create(), "TodoRegistry");

            TodoRoutes todoRoutes = new TodoRoutes(context.getSystem(), todoRegistryActor);

            startHttpServer(todoRoutes.todoRoutes(), context.getSystem());

            return Behaviors.empty();
        });

        ActorSystem.create(rootBehavior, "TodoAkkaHttpServer");
    }
}
