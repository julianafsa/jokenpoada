# language: pt

Funcionalidade: Gerenciamento de jogadas

  Cenario: Listando as jogadas
    Dado Uma lista vazia de jogadas
    Quando Realiza uma busca de todas as jogadas
    Entao A lista de jogadas eh exibida

  Cenario: Exibindo erro se nao ha jogadas cadastradas
    Dado Uma lista vazia de jogadas
    Quando Realiza uma busca de todas as jogadas invalidas
    Entao Um erro eh lancado

  Esquema do Cenario: Realizando a busca de jogada pelo nome da jogada
    Dado O NOME <move> da jogada
    Quando Realiza uma busca da jogada pelo NOME
    Entao A jogada eh exibida

    Exemplos:
      | move      |
      | "Lagarto" |
      | "Papel"   |
      | "Pedra"   |
      | "Spock"   |
      | "Tesoura" |

  Esquema do Cenario: Realizando a busca de jogada pelo nome errado da jogada
    Dado O NOME errado <move> da jogada
    Quando Realiza uma busca da jogada pelo NOME errado
    Entao Um erro eh exibido

    Exemplos:
      | move |
      | "A" |
      | "B" |
      | "C" |
      | "D" |
      | "E" |
