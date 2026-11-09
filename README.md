# Batalha Naval - FATEC

## Objetivo

Jogo individual de Batalha Naval desenvolvido em Java para a disciplina Linguagem de Programação I.

## Integrantes

- Integrante 1: a definir
- Integrante 2: a definir

## Tecnologias

- Java 26 (versao configurada no projeto)
- Java Swing
- Maven

## Regras

O jogador escolhe entre exatamente dois niveis:

| Nível | Tabuleiro | Frota | Tiros |
|---|---:|---|---:|
| Fácil | 6x6 | 4 submarinos, 2 destroieres, 1 cruzador, 1 encouraçado e 1 porta-aviões | 30 |
| Difícil | 8x8 | 6 submarinos, 2 destroieres, 2 cruzadores, 2 encouraçados e 1 porta-aviões | 40 |

As embarcações são posicionadas aleatoriamente na horizontal ou vertical, sem sobreposição e dentro do tabuleiro. Cada casa pode receber apenas um disparo. O jogador vence ao acertar todas as casas da frota e perde quando usa todos os tiros antes disso.

## Como executar

Abra o projeto no IntelliJ IDEA com o JDK 26 configurado e execute `org.example.Inicio`. Também é possível importar o projeto pelo `pom.xml` e executar a classe `Inicio` pela IDE.

## Pacotes

- `org.example`: inicializacao da aplicacao.
- `org.example.enums`: dificuldade, embarcacoes e estados/resultados dos disparos.
- `org.example.model`: tabuleiro, casas, embarcacoes e resultado do disparo.
- `org.example.service`: regras da partida e posicionamento aleatorio da frota.
- `org.example.controller`: integracao entre eventos da interface e regras.
- `org.example.view`: telas Swing e representacoes visuais de acerto/agua.
- `src/test/java/org/example/model`: verificacoes executaveis das regras do jogo.

## Funcionalidades

- Menu inicial com opcoes para jogar, consultar instrucoes ou sair.
- Selecao de nivel facil ou dificil.
- Tabuleiro clicavel com identificacao de linhas e colunas.
- Indicador de tiros restantes e mensagens com o resultado de cada disparo.
- Icones visuais distintos para embarcacoes atingidas e tiros na agua.
- Telas de vitoria/derrota com total de tiros e opcoes de continuar.
- Testes das configuracoes, posicionamento, disparos, repeticao, vitoria e derrota.

## Divisão sugerida para a dupla

- **Integrante 1:** enums, modelos, servicos e testes das regras.
- **Integrante 2:** telas Swing, icones e apresentacao da interface.
- **Integracao:** `ControladorJogo` conecta as telas ao `ServicoJogo`.

Para executar as verificações das regras, execute `org.example.model.TesteRegrasJogo` pela IDE.
