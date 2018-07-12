bird(X) :- penguin(X).
flies(X) :- bird(X), not -flies(X).
-flies(X) :- penguin(X).
flies(X) :- bat(X).
:- bird(X), bat(X).

penguin(tweety).
bat(batman).
