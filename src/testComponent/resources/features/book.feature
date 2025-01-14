Feature: the user can create and retrieve the books
  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo" booked by "Maxime Mourgues"
    And the user creates the book "L'avare" written by "Molière" booked by "Sandra Heraud"
    And the user get all books
    Then the list should contains the following books in the same order
      | name | author | booked |
      | L'avare | Molière | Sandra Heraud |
      | Les Misérables | Victor Hugo | Maxime Mourgues |