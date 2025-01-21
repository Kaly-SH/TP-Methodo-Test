Feature: the user can create and retrieve the books
  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo" booked by "Maxime Mourgues"
    And the user creates the book "L'avare" written by "Molière" booked by "Sandra Heraud"
    And the user get all books
    Then the list should contains the following books in the same order
      | name | author | booked_by |
      | L'avare | Molière | Sandra Heraud |
      | Les Misérables | Victor Hugo | Maxime Mourgues |

  Scenario: user reserves a book successfully
    Given the user creates the book "1984" written by "George Orwell"
    When the user reserves the book with id 3 for "Sandra Heraud"
    Then the reservation should be successful

  Scenario: user tries to reserve a book that is already reserved
    Given the user creates the book "Hunger Games" written by "Suzan Collins" booked by "Sandra Heraud"
    When the user reserves the book with id 4 for "Maxime Mourgues"
    Then the reservation should fail with status 409

  Scenario: user tries to reserve a non-existent book
    When the user reserves the book with id 999 for "Maxime Mourgues"
    Then the reservation should fail with status 404