Feature: the user can create, retrieve, and reserve the books

  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo"
    And the user creates the book "L'avare" written by "Molière"
    And the user get all books
    Then the list should contains the following books in the same order
      | name | author |
      | L'avare | Molière |
      | Les Misérables | Victor Hugo |

  Scenario: user reserves a book
    Given the user creates the book "1984" written by "George Orwell"
    When the user reserves the book "1984"
    Then the book "1984" should be marked as reserved
