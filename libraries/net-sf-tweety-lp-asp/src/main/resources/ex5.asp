eagle(eddy).
penguin(tux).

 fly(X) :- bird(X), not -fly(X).
-fly(X) :- penguin(X).
bird(X) :- penguin(X).
bird(X) :- eagle(X).
