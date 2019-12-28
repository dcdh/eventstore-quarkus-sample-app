Feature: Todo IT

    Scenario: should create a note
        When I create a todo
        Then A todo is created
        Then A created todo mail notification is sent

    Scenario: should mark a note as completed
        Given A created todo
        When I mark the todo as completed
        Then The todo is marked as completed
        Then A marked todo mail notification is sent
