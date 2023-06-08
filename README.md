# Macblut

Macblut is an intelligent
[Tablut](https://en.wikipedia.org/wiki/Tafl_games#Tablut) player written in Java
and based on space state search. It follows a variation of the rules proposed in
the work of [John C. Ashton](https://www.heroicage.org/issues/13/ashton.php).

Specifically, Macblut uses a minimax algorithm with alpha-beta cuts, in
particular the implementation provided by the
[AIMA](https://github.com/aimacode/aima-java) library. The quality of cutoff
states is determined through a custom heuristic, which differs based on whether
the player is playing as black (attacker) or white (defender).

Further information (in Italian) can be found in “presentation.pdf”.

## White Heuristic

* Percentage of yet non-captured white pawns
* Percentage of captured black pawns
* Chances of the king escaping
* Distance of black pawns from the king
* King in castle
* King in danger

## Black Heuristic

* Percentage of yet non-captured black pawns
* Percentage of captured white pawns
* “Rhombus formation”
* Distance of black pawns from the king
* King in danger
