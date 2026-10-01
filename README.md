# HRFlow — Intelligent Recruitment & Hiring Subsystem
> **A high-performance Java + JavaFX desktop platform dedicated exclusively to automating end-to-end Recruitment & Hiring using custom Data Structures and Algorithms.**

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-EA580C?logo=oracle&logoColor=white)](https://openjfx.io/)
[![Database](https://img.shields.io/badge/SQLite-Embedded-D97706?logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Build](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## 🌟 Overview

**HRFlow — Recruitment & Hiring Subsystem** automates the entire recruitment journey—from applicant intake and candidate screening to ranking, interview scheduling, and state history rollback.

Instead of relying on generic frameworks or standard CRUD abstractions, every core recruitment decision and workflow is driven by **fundamental Data Structures and Algorithms**:

* **Candidate Storage & Fast Retrieval**: Instant $O(1)$ candidate indexing via **HashMap** paired with indexed **Binary Search** and multi-field **Linear Search**.
* **Algorithmic Candidate Ranking**: Tailored **Selection Sort (Top-$K$)** and **Insertion Sort** algorithms comparing candidates across experience, skill match, and interview scores.
* **Intelligent Interview Scheduling**: Fair **FIFO Queue** and urgency-weighted **Priority Queue** engines with automated interval-based **Availability Checking** to prevent room and interviewer double-booking.
* **Dual-Stack Undo / Redo System**: Command-pattern historical tracking using paired **LIFO Stacks** for instant rollback of status promotions, rejections, and scheduling appointments.

---

## 🏗️ Architecture & Subsystem Flow

```text
                   Recruitment & Hiring Subsystem                         
                                 │
             ┌───────────────────┼───────────────────┐
             │                   │                   │
         Recruitment          Scheduling          History
             │                   │                   │
             ↓                   ↓                   ↓
          HashMap              Queue              Stack
             │                   │                   │
             ↓                   ↓                   ↓
       Linear Search       Priority Queue      Undo / Redo
             │                   │
             ↓                   ↓
       Binary Search      Availability Check
             │
             ↓
          Sorting
             │
       ┌─────┴─────┐
       ↓           ↓
   Insertion   Selection
     Sort        Sort
       │
       ↓
 Candidate Ranking
```

---

## 🧠 DSA Workflow Breakdown

| Subsystem Module | Data Structure / Algorithm | Time Complexity | Real-World Recruitment Role |
| :--- | :--- | :--- | :--- |
| **Candidate Registry** | `CustomHashMap<K, V>` | Get/Put: $O(1)$ average | High-speed primary candidate lookup by UUID and email index. |
| **Multi-field Filtering** | `LinearSearchEngine` | $O(N)$ scan | Flexible full-text search across applicant skills, department, and role tags. |
| **Ranked Record Retrieval** | `BinarySearchEngine` | $O(\log N)$ | Microsecond logarithmic lookup by ID or target score on presorted candidate indices. |
| **Top-K Shortlisting** | `SelectionSortEngine` | $O(K \cdot N)$ | Optimally extracts the highest-ranked candidates for interview rounds without sorting the entire pool. |
| **Streaming Candidate Rank** | `InsertionSortEngine` | $O(N)$ best, $O(N^2)$ worst | Efficiently maintains sorted candidate pools when processing new incoming applicant streams. |
| **Standard Interview Queue** | `Queue (FIFO)` | Enqueue/Dequeue: $O(1)$ | Manages standard interview appointments in strict chronological arrival order. |
| **Urgent Interview Dispatch** | `PriorityQueue` | Enqueue/Dequeue: $O(\log N)$ | Dispatches critical executive or high-scoring candidates ahead in the interview queue. |
| **Collision Prevention** | `Interval Availability Check` | $O(M)$ where $M$ is active slots | Detects overlapping room bookings and panel interviewer conflicts before slots are confirmed. |
| **Audit & State Rollback** | `DualStackHistory<Command>` | Push/Pop: $O(1)$ | Complete multi-level Undo/Redo tracking across candidate status changes and interview dispatches. |

---

## 🎨 Executive Warm UI Palette & Modern Layout

HRFlow features a **soft, smooth, light yellowish-orange premium aesthetic** designed for executive clarity, comfortable all-day usage, and warm visual harmony:

* **Canvas & Backdrop**: Soft Warm Sandstone / Ivory (`#FAF5ED`)
* **Top Navigation Bar**: Floating white card with orange brand icon, title, rounded rectangle card selection buttons, notification indicator, and executive user profile pill
* **Cards & Surfaces**: Warm White (`#FFFFFF`) with subtle warm borders (`#EFE4D6`) and gentle amber drop shadows
* **Primary Accents**: Rich Warm Amber to Burnt Orange (`#EA580C` to `#D97706`)
* **Headings & Hierarchy**: Deep Warm Espresso (`#261B12`)
* **Secondary Typography**: Warm Cedar Charcoal (`#5C4D3E` & `#786958`)
* **Status Badges**:
  * *Shortlisted / Hired*: Mint Sage Pastel (`#DCFCE7` / `#15803D`)
  * *Interview Scheduled*: Soft Butter Amber (`#FEF3C7` / `#B45309`)
  * *Rejected / Withdrawn*: Soft Rose Blush (`#FFE4E6` / `#BE123C`)

---

## 💻 Tech Stack

* **Platform**: Java 21+ (Records, modern pattern matching, clean modular architecture)
* **UI Framework**: JavaFX 21 (Custom CSS design system, responsive layouts)
* **Data Storage**: SQLite (Embedded zero-configuration local database with automatic schema seeding)
* **Build System**: Apache Maven 3.8+
* **Testing**: JUnit 5 unit testing

---

## 🛠️ Quick Launch

### Prerequisites
* **Java Development Kit (JDK)**: Version 21 or newer (`java -version`)
* **Apache Maven**: Version 3.8 or newer (`mvn -version`)

### Running the Application

```bash
# Clone the repository
git clone <repo-url>
cd HRM

# Execute the test suite
mvn clean test

# Launch the JavaFX Application
./run.sh
# OR via Maven directly
mvn javafx:run
```

---

## 🧪 Testing & Verification

Run the test suite to verify the algorithmic integrity of the Recruitment & Hiring subsystem:
```bash
mvn test
```
The test suite validates:
1. `BinarySearchTest`: Confirms logarithmic lookups, boundary handling, and comparison counts.
2. `SortingTest`: Validates Selection Sort for Top-$K$ ranking and Insertion Sort stability.
3. `QueueTest`: Verifies FIFO and PriorityQueue interview ordering and scheduling.
4. `HistoryTest`: Validates multi-level Undo/Redo dual-stack invariant preservation.
