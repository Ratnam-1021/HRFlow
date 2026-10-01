# HRFlow — Intelligent HR Automation System
> **A high-performance Java + JavaFX desktop platform where real enterprise HR workflows are powered by custom Data Structures and Algorithms.**

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-FF6F00?logo=oracle&logoColor=white)](https://openjfx.io/)
[![Database](https://img.shields.io/badge/SQLite-Embedded-003B57?logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## 🌟 Overview

HRFlow is **not a generic CRUD application**. Instead, its automation engine is driven by self-implemented Data Structures and Algorithms solving real-world enterprise bottlenecks:

* **Resume Screening & Skill Autocomplete**: Handcrafted **Trie (Prefix Tree)** for sub-millisecond keyword lookup across hundreds of applicant resumes.
* **Applicant Ranking & Shortlisting**: Generic **Max-Heap** extracting top-$K$ candidates in $O(N + K \log N)$ time based on dynamic job weighting.
* **Zero-Collision Interview Scheduling**: **Graph Conflict Modeling & Greedy Vertex Coloring** eliminating interviewer and room overlaps.
* **Organizational Hierarchy**: **$N$-ary Tree** supporting level-order BFS and DFS traversals for reporting lines and subtree headcount rollups.
* **Leave Overlap & Quorum Auditing**: **Sweep-Line Interval Algorithm** detecting simultaneous team absences before project delays occur.
* **Workforce Fast Search & Live Benchmarks**: **$O(\log N)$ Binary Search** with an interactive microsecond benchmark visualizer comparing Binary Search vs $O(N)$ Linear Search.
* **Operational Task Management**: **Priority Queue** sorting HR dispatch items by multi-factor urgency weights and target deadlines.
* **HR Automation Center**: A one-click automated pipeline executing the complete multi-threaded algorithmic workflow in background threads.

---

## 🏗️ Architecture & DSA Engine

```text
                                 HRFlow
                                    │
               ┌────────────────────┴────────────────────┐
               │                                         │
        JavaFX Desktop UI                         Data Layer (SQLite)
               │                                         │
               └────────────────────┬────────────────────┘
                                    │
                              DSA Core Engine
                                    │
       ┌───────────────┬────────────┴───┬───────────────┬───────────────┐
       │               │                │               │               │
    Max-Heap          Trie            Graph           Tree          Interval
       │               │                │               │               │
 Candidate Ranking   Skill          Interview      Organization    Leave Overlap
   & Top-K Query   Autocomplete     Scheduler       Hierarchy      Sweep-Line
  O(K log N)         O(L)          Coloring O(V+E)  DFS/BFS O(N)   O(N log N)
```

---

## 🧠 DSA Workflows Breakdown

| HR Workflow | Custom DSA Component | Theoretical Complexity | Business Impact |
| :--- | :--- | :--- | :--- |
| **Candidate Ranking** | `MaxHeap<Candidate>` | Build: $O(N)$, Extract: $O(K \log N)$ | Instantly ranks 250+ applicants based on customizable criteria vectors. |
| **Skill Search & Autocomplete** | `SkillTrie` & `TrieNode` | Query: $O(L)$ where $L$ = keyword length | As HR types skills (`Java`, `Spring`), matching suggestions and applicant counts pop up immediately. |
| **Skill Intersection Filter** | Inverted Index `Map<String, Set<Candidate>>` | $O(\min(S_1, S_2))$ | Instant intersection queries for complex requirements (e.g. `Java` + `SQL` + `DSA`). |
| **Interview Scheduling** | `InterviewScheduler` (Graph Coloring) | $O(V^2)$ Welsh-Powell Heuristic | Automatically schedules panel interviews into physical rooms without double-booking. |
| **Organization Hierarchy** | `OrgHierarchyTree` ($N$-ary Tree) | $O(N)$ Traversal | Instantly traces reporting chain up to the CEO and computes total departmental headcounts. |
| **Fast Employee Search** | `BinarySearchEngine` | $O(\log N)$ | Microsecond retrieval on sorted rosters with live comparison against linear scanning. |
| **Department Absence Overlaps** | `LeaveOverlapDetector` (Sweep-Line) | $O(N \log N)$ Event Sorting | Proactively alerts HR when team concurrency drops below minimum required operating thresholds. |
| **Operational Task Prioritization** | `TaskPriorityQueue` (Max-Heap) | Peek: $O(1)$, Poll: $O(\log N)$ | Dispatches urgent HR actions (offer rollouts, bottleneck mitigations) by deadline and impact. |

---

## 🚀 One-Click "HR Automation Center"

Instead of manually coordinating between recruiting, interviews, and team schedules, HR clicks:

> **⚡ Run Daily Automation**

The multi-threaded background pipeline automatically executes:
```text
✓ Scanned 250 applicant pool and indexed skills via Trie
✓ Ranked candidates and extracted top-15 shortlist into Max-Heap
✓ Scheduled 8 collision-free interview sessions using Graph Vertex Coloring
✓ Detected departmental absence overlaps using Sweep-Line intervals
✓ Created and prioritized critical HR action items in Task Max-Heap queue
✓ Daily Automation Complete! (Processed in < 150 ms)
```

---

## 💻 Tech Stack & Design

* **Language**: Java 21+ (Modern pattern matching, records, streams, CompletableFuture)
* **Frontend**: JavaFX 21 (Modern Obsidian Dark Theme, Glassmorphism, Material Cards)
* **Persistence**: SQLite (Embedded zero-configuration database with automatic schema seeding)
* **Build Tool**: Apache Maven
* **Testing**: JUnit 5 (Unit tests for all DSA modules + End-to-end integration tests)

---

## 🛠️ Getting Started

### Prerequisites
* JDK 21 or higher (`java -version`)
* Apache Maven (`mvn -version`)

### Quick Launch
```bash
# Clone the repository
git clone <repo-url>
cd HRM

# Run the test suite (100% passing)
mvn test

# Launch the JavaFX Application
./run.sh
# OR
mvn javafx:run
```

---

## 🧪 Testing

Run all unit tests and end-to-end integration tests:
```bash
mvn test
```
The test suite validates:
1. `MaxHeapTest`: Heap property maintenance, $O(N)$ bottom-up heapify, top-$K$ extraction.
2. `SkillTrieTest`: Prefix autocomplete, candidate inverted index mappings, multi-tag intersection.
3. `LeaveOverlapTest`: Sweep-line event sorting and peak concurrency detection.
4. `OrgHierarchyTest`: Tree building, DFS subtree headcount, and BFS level order.
5. `InterviewSchedulerTest`: Graph coloring conflict elimination on shared interviewers and rooms.
6. `BinarySearchTest`: Search precision and logarithmic comparison counts.
7. `EndToEndIntegrationTest`: Full system integration with SQLite persistence and daily automation.
