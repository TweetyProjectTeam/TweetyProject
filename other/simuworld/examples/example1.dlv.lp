

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% initial facts
vertex(4).
vertex(3).
vertex(2).
vertex(1).
prev_at(3, bob).
prev_at(1, alice).
adj(4, 1).
adj(3, 4).
adj(2, 3).
adj(1, 2).
intends_goto(2, bob).
intends_goto(2, alice).
agent(bob).
agent(alice).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% initial rules
adj(X, Y):- adj(Y, X).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  agents intentions


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% goto action
apply_1_goto(X, Ag, Y):- intends_goto(X, Ag), prev_at(Y, Ag), adj(Y, X), not blocked(Ag).
take_1_goto(X, Ag, Y) | -take_1_goto(X, Ag, Y):- apply_1_goto(X, Ag, Y).
at(X, Ag):- take_1_goto(X, Ag, Y).
del_at(Y, Ag):- take_1_goto(X, Ag, Y).
maybe_at(X, Ag):- -take_1_goto(X, Ag, Y).
not_at(Y, Ag):- -take_1_goto(X, Ag, Y).
fail_1_goto(X, Ag, Y):- -take_1_goto(X, Ag, Y), conflict_at(X, Ag).
:~ -take_1_goto(X, Ag, Y), not fail_1_goto(X, Ag, Y).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  constraint 1
:- at(X, Ag1), at(X, Ag2), Ag1 !=Ag2.
constraint_1(X, Ag1, Ag2):- maybe_at(X, Ag1), maybe_at(X, Ag2), Ag1 !=Ag2.
conflict_at(X, Ag1):- constraint_1(X, Ag1, Ag2).
conflict_at(X, Ag2):- constraint_1(X, Ag1, Ag2).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effects


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  fluents
at(A0, A1):- prev_at(A0, A1), not del_at(A0, A1).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  alternate world fluents
maybe_at(A0, A1):- prev_at(A0, A1), not del_at(A0, A1), not not_at(A0, A1).
