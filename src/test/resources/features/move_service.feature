# language: en

Feature: Managing moves

  #1
  Scenario Outline: Create moves
    Given A <move> move
    When Create a move
    Then Move is created
    Examples:
      | move      |
      | 'Lagarto' |
      | 'Papel'   |
      | 'Pedra'   |
      | 'Spock'   |
      | 'Tesoura' |
  #2
  Scenario: Create move already created
    Given A move "Lagarto" already created
    When Create a move already created
    Then Move is not created

  #3
  Scenario: Create invalid move
    Given A "Invalid" name move
    When Trying to create a invalid move
    Then Move is not created and a message error is returned

  #4
  Scenario: Listing all moves
    Given A empty list of moves
    When Search all moves
    Then Moves list is displayed

  #5
  Scenario: Displaying error if there are no registered moves
    Given A empty list of moves
    When Search all empty moves
    Then An error message is displayed

  #6
  Scenario Outline: Searching for a move by name
    Given A move named <move>
    When Search for a move by name
    Then The move is displayed
    Examples:
      | move      |
      | "Lagarto" |
      | "Papel"   |
      | "Pedra"   |
      | "Spock"   |
      | "Tesoura" |

  #7
  Scenario: Searching for an invalid move
    Given A "Invalid" move to be searched
    When Searching for an invalide move
    Then Move is not returned and an error message is displayed
