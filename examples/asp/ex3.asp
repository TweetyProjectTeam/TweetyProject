obj(r).
obj(l).

usable(X) :- obj(X), not broken(X).
-usable(X) :- broken(X).
broken(r) ; broken(l).
