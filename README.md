# ObjectVille II

A text-based, **zero-player** city simulation in Java. You hand it a map and a
number of ticks; it runs the city forward and prints a complete event log of
everything that happens each tick — which zones get services, which get
utilities (and how much), which receive pooled resources, what each zone
produces, and every level change.

The engine has been **calibrated against the official reference run**
(`map00.txt`, 10 ticks): its output is **byte-for-byte identical** to the
provided `output.txt` (all 3,558 lines, including encoding).

---

## Build & run

JDK 21+ is required.

```bash
./build.sh                                  # compiles src/ and produces ObjectVilleGame.jar
java -jar ObjectVilleGame.jar <mapFile> <ticks>
```

Examples:

```bash
java -jar ObjectVilleGame.jar map00.txt 10        # the official reference run
java -jar ObjectVilleGame.jar sample_map.txt 5
```

`arg0` is the map file, `arg1` is the (non-negative) number of ticks to simulate.
There is also a convenience wrapper, `./run.sh <mapFile> <ticks>`.

To reproduce the reference verification:

```bash
java -jar ObjectVilleGame.jar map00.txt 10 > mine.txt
cmp mine.txt output.txt        # no output  ->  identical
```

---

## The map

A map is a rectangular grid of single-character cells. Cells may be written
contiguously (`EEESRHHHHRE`) or separated by spaces/commas. Blank lines and
lines beginning with `#` are ignored, short rows are padded with empty cells,
and a leading UTF-8 BOM is tolerated (the reference `map00.txt` has one).

| Symbol | Cell | Notes |
|:------:|------|-------|
| `H` | Housing zone | produces **population** |
| `I` | Industrial zone | produces **goods** |
| `C` | Commercial zone | produces **lifestyle** |
| `R` | Road | utilities flow through it; consumes nothing |
| `E` | Empty | a wall — nothing passes through it |
| `P` | Power plant | provides **electricity**, capacity 100 |
| `W` | Water station | provides **water**, capacity 100 |
| `T` | Internet hub | provides **internet**, capacity 100 |
| `F` | Police station | provides **security**, radius 5 |
| `D` | Hospital | provides **health**, radius 3 |
| `S` | School | provides **education**, radius 4 |

Roads and zones are **connectable** (utilities spread through them). Empty cells
and the provider buildings themselves block propagation.

---

## What a tick prints

Every tick emits, in this exact order:

1. **`Tick N`** header.
2. **Services.** For each service provider in grid row-major order, every zone
   that *uses* that service and lies within the provider's Euclidean radius is
   granted it and logged:
   `House at (r,c) received education service`.
   A zone covered by two providers of the same service is logged twice.
3. **Utilities.** Every utility provider, **in grid row-major order**, pushes its
   own utility outward by BFS. Each absorbing zone is logged:
   `Commercial at (r,c) received 3 water`.
4. **Resources.** The *previous* tick's pooled production is divided out (see
   below) and logged per recipient:
   `Industrial at (r,c) received 1 population`,
   `Commercial at (r,c) received 2 goods`,
   `House at (r,c) received 1 lifestyle`.
5. **Generate + level.** Each zone in row-major order logs what it produced this
   tick — `House at (r,c) generated 4 population` — and, only if its level
   changed, the move: `Industrial at (r,c) levels up from 2 to 3` or
   `... levels down from 3 to 0`.

Coordinates are zero-indexed `(row,col)`. Only positive amounts are logged.
Output is UTF-8 with a leading BOM, CRLF line endings and no trailing newline,
matching the reference exactly.

---

## Simulation rules

### Zones, levels and output

Each zone has a level 0-3. Let **m** = the minimum amount delivered across all
the utilities the zone requires this tick (e.g. 1 electricity + 2 water + 3
internet => m = 1).

| Zone | Requires (utilities) | Level 1 | Level 2 | Level 3 | Output by level |
|------|----------------------|---------|---------|---------|-----------------|
| **Housing** (`H`) | electricity, water, internet | utilities (m >= 1) | + security AND health AND education | + receives lifestyle >= 1 | L1 = m, L2 = 2m, L3 = 2m + lifestyle |
| **Industrial** (`I`) | electricity, water | utilities (m >= 1) | + security | + receives population >= 1 | L1 = m, L2 = 2m, L3 = 2m + population |
| **Commercial** (`C`) | electricity, water, internet | utilities (m >= 1) | + security | + receives population >= 1 AND goods >= 1 | L1 = m, L2 = 2m, L3 = 2m + min(population, goods) |

Notes confirmed against the reference:

* **Level 1 needs only the utilities.** Industrial and Commercial reach level 1
  from utilities alone — population/goods are *not* required to start; they are
  what push a zone to level 3 (and add the level-3 output bonus).
* Industrial and Commercial only ever use the **security** service; Housing uses
  all three. Only the services a zone uses are logged for it.

### Level transitions

* A zone rises or falls by **at most one level per tick**.
* **Losing utilities entirely** (m = 0 — it failed to receive at least one of
  every required utility) drops it straight to level 0 immediately; this shows as
  a single line such as `levels down from 3 to 0`. After collapsing it can revive
  from level 0 on later ticks.

### Demand

A zone's utility demand for the next tick is `max(output, 1)`. Zones start at
level 0, output 0, demand 1.

### Service distribution

Pure geometry: a provider covers a zone when the **Euclidean distance**
`sqrt(dr^2 + dc^2) <= radius` (inclusive). Walls and roads do not block services.

### Utility distribution

Providers are processed **in grid row-major order**, each running an independent
BFS from its own position:

* The BFS is **4-directional**; neighbours are enqueued in the order
  **North, South, East, West**, and the queue is FIFO. A zone absorbs when it is
  dequeued, then the wave continues through it (zones and roads are connectable).
* Each provider has **capacity 100**. Providers of the same utility type share a
  single per-type remaining-demand pool (reset once at the start of the step), so
  a zone can be partly served by one provider and finished by another, and the
  utility types interleave in the log (internet, water, power, internet, water,
  power ...).

### Resource distribution

The previous tick's city-wide production is pooled and split by **integer
division**:

* population -> split equally among **industrial + commercial** zones,
* goods -> split equally among **commercial** zones,
* lifestyle -> split equally among **housing** zones.

---

## Project layout

```
src/objectville/
  Main.java            CLI entry point (UTF-8 output stream)
  MapLoader.java       parses a map file (UTF-8/BOM, CRLF, contiguous or delimited)
  CellFactory.java     symbol -> Cell
  Cell.java            abstract base cell (indexInto + default no-op absorb)
  Connectable.java     capability interface: utilities propagate through it
  Provider.java        generic interface Provider<T> (provides a Utility or Service)
  EmptyCell.java       wall                RoadCell.java        road (Connectable)
  Zone.java            abstract zone (Connectable; level, demand, m, update, log hooks)
  Housing.java  Industrial.java  Commercial.java
  UtilityProvider.java + PowerPlant / WaterStation / InternetHub   (Provider<Utility>)
  ServiceProvider.java + PoliceStation / Hospital / School         (Provider<Service>)
  Utility.java         ELECTRICITY, WATER, INTERNET
  Service.java         SECURITY, HEALTH, EDUCATION
  City.java            the engine: tick loop, BFS, distribution, event log (final)
build.sh  run.sh
map00.txt  output.txt          official reference map + its expected output
sample_map.txt  sample_output.txt   a smaller demo (shows walls isolating a block)
```

The grid is built from `Cell`s; `City` caches row-major lists of zones and
providers and runs the five tick steps in order, appending every event to a log
that is written out at the end in the reference's exact byte format.

### OOP design notes

* **Interfaces over inheritance for cross-cutting traits.** `Connectable` is a
  *capability* shared by roads and zones — two unrelated branches of the `Cell`
  hierarchy — so the BFS asks `cell instanceof Connectable` rather than checking
  concrete types. `Provider<T>` is a *generic* contract implemented by the two
  separate provider sub-trees (`Provider<Utility>`, `Provider<Service>`).
* **Polymorphism instead of `instanceof` chains.** Cells register themselves into
  the city's indices (`indexInto`), zones take their own pooled resources and emit
  their own log lines (`receivePooled`), and any cell can `absorb` a utility
  (zones consume and log, roads/empty no-op). The engine never switches on a
  cell's concrete type or downcasts it.
* `Zone.resetTickState()` and the `City` class are `final`, so neither the
  constructor calls nor the indexing leak `this` to a not-yet-initialised
  subclass (no `this-escape`). The project compiles cleanly under `-Xlint:all`.

---

## Validation

`java -jar ObjectVilleGame.jar map00.txt 10` reproduces the official
`output.txt` **byte-for-byte** (145,557 bytes, `cmp` reports no difference).
