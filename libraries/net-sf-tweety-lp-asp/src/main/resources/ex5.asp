% Single-line comment
motive(harry).
motive(sally).
guilty(harry).

%* Multi-
line 
comment
*% 
innocent(Suspect) :- motive(Suspect), not guilty(Suspect).
#show guilty/1.
innocent(harry)?



