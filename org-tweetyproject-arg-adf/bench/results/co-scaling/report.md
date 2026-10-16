# ADF evaluation: old implementation vs. new simple reasoners

- Semantics / task: **CO**, enumerate all models (EE)
- Machine: AMD Ryzen 7 7800X3D 8-Core Processor, Linux 7.2.9-2-cachyos, OpenJDK 64-Bit Server VM 25.0.4.1
- Commit: `ffcfe16c91baefcca6ef110723adc47df4d8de05`
- JVM flags: `-Xms512m -Xmx4g -XX:+UseSerialGC -Xss64m`, one JVM per run, pinned to one physical core
- Runner flags: `--task EE --sem CO --warmup 1 --reps 3 --budget-ms 10000`
- Per-solve timeout: 60.0 s; parsing is excluded from solve times

Times are geometric means over the solved instances of each row of the per-instance median solve time (ms). PAR-2 is the mean over all instances of the row in seconds, with unsolved instances counted as 2 × timeout. Speedup = old / new on instances solved by both (> 1: new is faster). Agree = instances solved by both with equal model count and answer hash.

## Family `af`

| n | new solved | new time ms | new PAR-2 s | old solved | old time ms | old PAR-2 s | speedup | agree | models (max) |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 4 | 5/5 | 0.19 | 0.00 | 5/5 | 2.40 | 0.00 | 12.3× | 5/5 | 3 |
| 6 | 5/5 | 0.32 | 0.00 | 5/5 | 5.00 | 0.01 | 15.7× | 5/5 | 2 |
| 8 | 5/5 | 1.06 | 0.00 | 5/5 | 8.18 | 0.01 | 7.73× | 5/5 | 1 |
| 10 | 5/5 | 3.49 | 0.00 | 5/5 | 18.4 | 0.02 | 5.26× | 5/5 | 4 |
| 12 | 5/5 | 13.3 | 0.01 | 5/5 | 43.3 | 0.04 | 3.27× | 5/5 | 3 |
| 14 | 5/5 | 114 | 0.12 | 5/5 | 60.7 | 0.07 | 0.53× | 5/5 | 1 |
| 16 | 5/5 | 1206 | 1.24 | 5/5 | 132 | 0.21 | 0.11× | 5/5 | 6 |
| 18 | 5/5 | 9745 | 9.96 | 5/5 | 552 | 0.61 | 0.06× | 5/5 | 3 |
| 20 | 4/5 | 33628 | 51.9 | 5/5 | 1279 | 1.90 | 0.04× | 4/4 | 3 |

## Family `bipolar`

| n | new solved | new time ms | new PAR-2 s | old solved | old time ms | old PAR-2 s | speedup | agree | models (max) |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 4 | 5/5 | 0.27 | 0.00 | 5/5 | 3.91 | 0.00 | 14.6× | 5/5 | 8 |
| 6 | 5/5 | 0.37 | 0.00 | 5/5 | 4.44 | 0.00 | 12.2× | 5/5 | 4 |
| 8 | 5/5 | 1.38 | 0.00 | 5/5 | 5.09 | 0.01 | 3.68× | 5/5 | 18 |
| 10 | 5/5 | 5.03 | 0.01 | 5/5 | 9.56 | 0.01 | 1.90× | 5/5 | 27 |
| 12 | 5/5 | 20.0 | 0.02 | 5/5 | 11.1 | 0.01 | 0.55× | 5/5 | 7 |
| 14 | 5/5 | 196 | 0.20 | 5/5 | 23.1 | 0.02 | 0.12× | 5/5 | 5 |
| 16 | 5/5 | 1487 | 1.51 | 5/5 | 23.7 | 0.04 | 0.02× | 5/5 | 9 |
| 18 | 5/5 | 10401 | 11.1 | 5/5 | 38.6 | 0.08 | 0.00× | 5/5 | 14 |
| 20 | 0/5 | – | 120 | 5/5 | 68.6 | 0.20 | – | 0/0 | 11 |

## Family `general`

| n | new solved | new time ms | new PAR-2 s | old solved | old time ms | old PAR-2 s | speedup | agree | models (max) |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 4 | 5/5 | 0.25 | 0.00 | 4/5 | 3.16 | 24.0 | 13.4× | 4/4 | 9 |
| 6 | 5/5 | 0.42 | 0.00 | 2/5 | 3.03 | 72.0 | 7.45× | 2/2 | 9 |
| 8 | 5/5 | 1.78 | 0.00 | 4/5 | 7.08 | 24.0 | 4.19× | 4/4 | 6 |
| 10 | 5/5 | 5.46 | 0.01 | 3/5 | 7.66 | 48.0 | 1.30× | 3/3 | 3 |
| 12 | 5/5 | 24.1 | 0.03 | 4/5 | 13.1 | 24.0 | 0.48× | 4/4 | 6 |
| 14 | 5/5 | 241 | 0.26 | 2/5 | 34.6 | 72.0 | 0.18× | 2/2 | 3 |
| 16 | 5/5 | 1680 | 1.72 | 4/5 | 268 | 24.2 | 0.17× | 4/4 | 18 |
| 18 | 5/5 | 14047 | 14.4 | 4/5 | 107 | 24.3 | 0.01× | 4/4 | 6 |
| 20 | 0/5 | – | 120 | 2/5 | 560 | 72.2 | – | 0/0 | 1 |

## Crashes, errors and disagreements

- `general-n04-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a1 | java.lang.IllegalArgumentException: Duplicate element: a1 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
- `general-n06-i0.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a3 | java.lang.IllegalArgumentException: Duplicate element: a3 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
- `general-n06-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a0 | java.lang.IllegalArgumentException: Duplicate element: a0 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
- `general-n06-i4.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a1 | java.lang.IllegalArgumentException: Duplicate element: a1 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n08-i4.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a0 | java.lang.IllegalArgumentException: Duplicate element: a0 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
- `general-n10-i1.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a2 | java.lang.IllegalArgumentException: Duplicate element: a2 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n10-i2.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a1 | java.lang.IllegalArgumentException: Duplicate element: a1 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
- `general-n12-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a6 | java.lang.IllegalArgumentException: Duplicate element: a6 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n14-i1.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a2 | java.lang.IllegalArgumentException: Duplicate element: a2 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n14-i2.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a10 | java.lang.IllegalArgumentException: Duplicate element: a10 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweet…
- `general-n14-i4.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a6 | java.lang.IllegalArgumentException: Duplicate element: a6 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n16-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: neg(a12) | java.lang.IllegalArgumentException: Duplicate element: neg(a12) / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at…
- `general-n18-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a6 | java.lang.IllegalArgumentException: Duplicate element: a6 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n20-i0.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: neg(a14) | java.lang.IllegalArgumentException: Duplicate element: neg(a14) / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at…
- `general-n20-i2.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a9 | java.lang.IllegalArgumentException: Duplicate element: a9 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.ConjunctionAcceptanceCondition.<init>(ConjunctionAcceptanceCondition.java:33) / at org.tweetyp…
- `general-n20-i3.kpp` old: **error** java.lang.IllegalArgumentException: Duplicate element: a1 | java.lang.IllegalArgumentException: Duplicate element: a1 / at org.tweetyproject.arg.adf.syntax.acc.BinaryAcceptanceCondition.<init>(BinaryAcceptanceCondition.java:28) / at org.tweetyproject.arg.adf.syntax.acc.DisjunctionAcceptanceCondition.<init>(DisjunctionAcceptanceCondition.java:35) / at org.tweetyp…
