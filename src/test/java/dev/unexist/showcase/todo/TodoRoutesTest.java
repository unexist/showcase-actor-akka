/**
 * @package Showcase-Actor-Akka
 *
 * @file Todo route tests
 * @copyright 2024-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.typed.ActorRef;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import org.junit.*;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoRoutesTest extends JUnitRouteTest {

    @ClassRule
    public static TestKitJunitResource testkit = new TestKitJunitResource();

    //#test-top
    // shared registry for all tests
    private static ActorRef<TodoRegistry.Command> todoRegistry;
    private TestRoute appRoute;

    @BeforeClass
    public static void beforeClass() {
        todoRegistry = testkit.spawn(TodoRegistry.create());
    }

    @Before
    public void before() {
        TodoRoutes todoRoutes = new TodoRoutes(testkit.system(), todoRegistry);
        appRoute = testRoute(todoRoutes.todoRoutes());
    }

    @AfterClass
    public static void afterClass() {
        testkit.stop(todoRegistry);
    }

    @Test
    public void test1NoTodos() {
        appRoute.run(HttpRequest.GET("/todos"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"todos\":[]}");
    }

    @Test
    public void test2HandlePOST() {
        appRoute.run(HttpRequest.POST("/todos")
                .withEntity(MediaTypes.APPLICATION_JSON.toContentType(),
                        "{\"name\": \"Kapi\", \"age\": 42, \"countryOfResidence\": \"jp\"}"))
                .assertStatusCode(StatusCodes.CREATED)
                .assertMediaType("application/json")
                .assertEntity("{\"description\":\"Todo Kapi created.\"}");
    }

    @Test
    public void test3Remove() {
        appRoute.run(HttpRequest.DELETE("/todos/Kapi"))
                .assertStatusCode(StatusCodes.OK)
                .assertMediaType("application/json")
                .assertEntity("{\"description\":\"Todo Kapi deleted.\"}");

    }
}
