Feature: the user can create, retrieve, and reserve the books

  Scenario: user creates two books and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo"
    And the user creates the book "L'avare" written by "Molière"
    And the user get all books
    Then the list should contains the following books in the same order
      | name | author | id | reserved |
      | L'avare | Molière | 2 | false |
      | Les Misérables | Victor Hugo | 1 | false |

  Scenario: user reserves a book
    Given the user creates the book "1984" written by "George Orwell"
    When the user reserves the book with id 1
    Then the book "1984" should be marked as reserved
