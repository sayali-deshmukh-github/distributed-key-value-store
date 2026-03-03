# Distributed Key-Value Store (Dynamo-Style)

A Dynamo-style distributed key-value store simulator built in pure Java with Maven. Designed for hands-on understanding of distributed systems concepts including replication, quorum-based consistency, vector clocks, consistent hashing, and partitioning.

---

## 🎯 Goals

- Understand core Dynamo principles: data partitioning, replication, quorum consistency, eventual consistency, and vector clock versioning
- Build a production-style simulation of distributed key-value nodes
- Enable experimenting with multiple nodes, replication factors, and quorum configurations
- Serve as a foundation for learning Raft-style consensus

---

## 📐 Project Structure

```
src/main/java/com/example/dynamo_key_value/
├── cluster/
│   └── Cluster.java
├── node/
│   └── Node.java
├── storage/
│   ├── VersionedValue.java
│   └── StorageEngine.java
├── versioning/
│   └── VectorClock.java
└── service/
    └── ClusterService.java
```

---

## 🔑 Key Classes

| Class | Responsibility |
|---|---|
| `Cluster.java` | Manages nodes and the consistent hash ring; handles `put`/`get` with replication and quorum logic |
| `Node.java` | Simulates a distributed node; stores `VersionedValue` objects per key |
| `StorageEngine.java` | Interface for pluggable storage engines (in-memory or DB-backed) |
| `VersionedValue.java` | Encapsulates value, vector clock, and timestamp |
| `VectorClock.java` | Implements vector clocks for versioning and conflict resolution |
| `ClusterService.java` | Higher-level interface for external access (simulates service layer) |

---

## 🧠 Core Concepts

### Consistent Hashing
Distributes keys across nodes evenly and handles node addition/removal with minimal data movement.

### Replication
Writes are replicated to multiple nodes with a configurable replication factor.

### Quorum-Based Reads/Writes
- `writeQuorum` — minimum nodes required to acknowledge a write
- `readQuorum` — minimum nodes required to satisfy a read

### Vector Clocks
Tracks updates across nodes to resolve conflicts and detect concurrent writes. Timestamps serve as tie-breakers.

### Eventual Consistency
Conflicting versions are resolved using `VectorClock.compare()`. The system converges over time.

---

## 🚀 Usage

**1. Compile with Maven wrapper:**
```bash
./mvnw clean compile
```

**2. Run a test scenario:**
```java
public static void main(String[] args) {
    // 3 nodes, replication factor=2, read quorum=2, write quorum=2
    Cluster cluster = new Cluster(3, 2, 2, 2);
    cluster.put("key1", "value1");
    System.out.println(cluster.get("key1"));
}
```

**3.** Adjust replication and quorum parameters to experiment with different consistency scenarios.

---

## ⚙️ Design Decisions

- **Vector Clocks over Timestamps** — Timestamps alone cannot guarantee causal ordering; vector clocks provide true causal versioning.
- **Cluster-Level Quorum Enforcement** — The `Cluster` handles routing, replication, and quorum; nodes only store data locally.
- **Pluggable Storage** — `StorageEngine` can be swapped for a PostgreSQL-backed implementation for production-level persistence.
- **Single JVM Simulation** — All nodes run in one JVM for simplicity, with a clear path to extend to networked nodes.

---

## 🔭 Next Steps & Extensions

- [ ] **PostgreSQL / Redis integration** — persistent or distributed storage backend
- [ ] **Node failure simulation** — test read/write quorum behavior under partial failures
- [ ] **Eventual consistency repair** — background threads to reconcile vector clocks across nodes
- [ ] **Raft consensus** — move to strong consistency with leader election and log replication
- [ ] **Client API** — expose REST or gRPC endpoints for external access

---

## 📚 Learning Outcomes

- How Dynamo-style replication works in practice
- Quorum-based consistency enforcement
- Using vector clocks for causal versioning
- Implementing consistent hashing
- Building cluster simulations in a single JVM
- Groundwork for Raft-based consensus systems
