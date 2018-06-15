obj(a).
obj(b).

p(D) :- obj(D), not q(D).
q(D) :- r(D), not p(D).
r(a).
