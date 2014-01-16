about:
simuworld - a simple example how to use the asp library
as a back-end for an agent environment simulator.

note:
off maintenance, it is just an example.

how this works:
effects of all actions are simulated at once. for any action,
guess if it can by applied to the world state or not.
* guess fire action: if an action causes an error, a hard 
                     constraint discards the answer set.
* guess omit action: if an action does not cause an error,
                     a weak constraint is use to minimize
                     the overall non-conflicting, but not fired
                     actions.

the next world state is that with the lowest weight of all answer
sets. if multiple answer sets arise, they are all equal with respect
to the input alphabet.