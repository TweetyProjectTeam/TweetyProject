

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% initial facts
trap_recharge(2).
trap_effect_time(3).
trap_duration(5).
vertex(10).
vertex(9).
vertex(8).
vertex(7).
vertex(6).
vertex(5).
vertex(4).
vertex(3).
vertex(2).
vertex(1).
prev_at(6, bob).
prev_at(1, alice).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% initial rules
adj(A, B):- vertex(A), vertex(B), A !=B.


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
% snare action
apply_2_snare(X, Ag, Y, T, DUR):- intends_snare(X, Ag), prev_at(Y, Ag), adj(Y, X), not prev_trap_at(X, T), #int(T), trap_duration(DUR), not blocked(Ag).
take_2_snare(X, Ag, Y, T, DUR) | -take_2_snare(X, Ag, Y, T, DUR):- apply_2_snare(X, Ag, Y, T, DUR).
trap_at(X, DUR):- take_2_snare(X, Ag, Y, T, DUR).
maybe_trap_at(X, DUR):- -take_2_snare(X, Ag, Y, T, DUR).
fail_2_snare(X, Ag, Y, T, DUR):- -take_2_snare(X, Ag, Y, T, DUR), conflict_trap_at(X, DUR).
:~ -take_2_snare(X, Ag, Y, T, DUR), not fail_2_snare(X, Ag, Y, T, DUR).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% disarm action
apply_3_disarm(X, Ag, Y, T):- intends_disarm(X, Ag), prev_at(Y, Ag), adj(X, Y), prev_trap_at(X, T), not blocked(Ag).
take_3_disarm(X, Ag, Y, T) | -take_3_disarm(X, Ag, Y, T):- apply_3_disarm(X, Ag, Y, T).
:~ -take_3_disarm(X, Ag, Y, T), not fail_3_disarm(X, Ag, Y, T).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  constraint 1
:- at(X, Ag1), at(X, Ag2), Ag1 !=Ag2.
constraint_1(X, Ag1, Ag2):- maybe_at(X, Ag1), maybe_at(X, Ag2), Ag1 !=Ag2.
conflict_at(X, Ag1):- constraint_1(X, Ag1, Ag2).
conflict_at(X, Ag2):- constraint_1(X, Ag1, Ag2).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  constraint 2
:- at(X, Ag), at(Y, Ag), X !=Y.
constraint_2(X, Ag, Y):- maybe_at(X, Ag), maybe_at(Y, Ag), X !=Y.
conflict_at(X, Ag):- constraint_2(X, Ag, Y).
conflict_at(Y, Ag):- constraint_2(X, Ag, Y).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effects


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effect 1
effect_1(Ag, X, 1, Y):- prev_trapped(Ag, X), agent(Ag), X >1, Y = X - 1.
trapped(Ag, Y):- effect_1(Ag, X, 1, Y).
del_trapped(Ag, X):- effect_1(Ag, X, 1, Y).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effect 2
effect_2(Ag, X):- prev_trapped(Ag, X).
blocked(Ag):- intends_goto(_, Ag), effect_2(Ag, X).
blocked(Ag):- intends_disarm(_, Ag), effect_2(Ag, X).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effect 3
effect_3(X, T, 1, T2, Ag):- prev_trap_at(X, T), T2 = T - 1, T >1, not prev_at(X, Ag), agent(Ag).
trap_at(X, T2):- effect_3(X, T, 1, T2, Ag).
del_trap_at(X, T):- effect_3(X, T, 1, T2, Ag).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effect 4
effect_4(X, 1, Ag):- prev_trap_at(X, 1), not prev_at(X, Ag), agent(Ag).
del_trap_at(X, 1):- effect_4(X, 1, Ag).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  effect 5
effect_5(X, T, Ag, U):- prev_trap_at(X, T), agent(Ag), prev_at(X, Ag), trap_effect_time(U).
trapped(Ag, U):- effect_5(X, T, Ag, U).
del_trap_at(X, T):- effect_5(X, T, Ag, U).
blocked(Ag):- intends_goto(_, Ag), effect_5(X, T, Ag, U).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  fluents
trapped(A0, A1):- prev_trapped(A0, A1), not del_trapped(A0, A1).
trap_at(A0, A1):- prev_trap_at(A0, A1), not del_trap_at(A0, A1).
at(A0, A1):- prev_at(A0, A1), not del_at(A0, A1).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  alternate world fluents
maybe_trapped(A0, A1):- prev_trapped(A0, A1), not del_trapped(A0, A1), not not_trapped(A0, A1).
maybe_trap_at(A0, A1):- prev_trap_at(A0, A1), not del_trap_at(A0, A1), not not_trap_at(A0, A1).
maybe_at(A0, A1):- prev_at(A0, A1), not del_at(A0, A1), not not_at(A0, A1).
