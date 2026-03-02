Distributed Key-Value Store (Dynamo-Style)

Project Overview

This project implements a Dynamo-style distributed key-value store simulator in Java.
It is designed to provide a hands-on understanding of distributed systems concepts such as replication, quorum-based consistency, vector clocks, consistent hashing, and partitioning.

The project uses pure Java and Maven wrapper for building and running. No Spring dependencies are required.

⸻

Goals
	1.	Understand Dynamo principles:
	•	Data partitioning using consistent hashing
	•	Replication for availability
	•	Quorum-based reads and writes
	•	Eventual consistency
	•	Versioning with vector clocks
	2.	Build a production-style simulation of distributed key-value nodes
	3.	Enable experimenting with multiple nodes, replication factors, and quorum configurations
	4.	Serve as a foundation for learning Raft-style consensus later

⸻

Core Concepts Covered
	1.	Consistent Hashing
	•	Distributes keys across nodes evenly
	•	Handles node addition/removal with minimal data movement
	2.	Replication
	•	Writes replicated to multiple nodes
	•	Configurable replication factor
	3.	Quorum-based Reads/Writes
	•	writeQuorum: minimum number of nodes required to acknowledge a write
	•	readQuorum: minimum number of nodes required to satisfy a read
	4.	Versioned Values
	•	Stored values include vector clocks to track causality
	•	Optional timestamp as tie-breaker for concurrent writes
	5.	Vector Clocks
	•	Tracks updates across nodes to resolve conflicts
	•	Supports concurrent updates detection
	6.	Node Abstraction
	•	Each Node simulates an independent storage engine
	•	Nodes hold replicated key-value data
	7.	Cluster Simulation
	•	Cluster class manages nodes, replication, and quorum logic
	•	Routes put and get requests through consistent hash ring
	8.	Eventual Consistency
	•	Conflicting versions are resolved using vector clock comparison
	•	System converges over time
	9.	Persistence Simulation
	•	In-memory storage (for simplicity)
	•	Optional: PostgreSQL integration can be added for real persistence
	10.	MVP Load Balancing
	•	Initial Dynamo implementation supports routing and replication logic

⸻

Project Structure

src/main/java/com/example/dynamo_key_value/
├── cluster/
│   └── Cluster.java
├── node/
│   └── Node.java
├── storage/
│   └── VersionedValue.java
│   └── StorageEngine.java
├── versioning/
│   └── VectorClock.java
└── service/
    └── ClusterService.java

Key Classes
	1.	Cluster.java
	•	Manages the list of nodes and the consistent hash ring
	•	Handles put(key, value) and get(key) requests with replication and quorum logic
	2.	Node.java
	•	Simulates a distributed node
	•	Stores VersionedValue objects per key
	•	Handles in-memory storage and retrieval
	3.	StorageEngine.java
	•	Interface for pluggable storage engines (in-memory or DB-backed)
	4.	VersionedValue.java
	•	Encapsulates value, vector clock, and timestamp
	5.	VectorClock.java
	•	Implements vector clocks for versioning and conflict resolution
	6.	ClusterService.java
	•	Provides a higher-level interface for external access (simulates service layer)

⸻

Implementation Workflow
	1.	Project Setup
	•	Created with Spring Initializr for Maven wrapper
	•	Removed all Spring dependencies to create a pure Java project
	2.	Node Abstraction
	•	Each node stores a Map<String, List<VersionedValue>> for multi-versioned keys
	3.	Consistent Hashing
	•	Nodes are placed on a virtual hash ring
	•	Keys are mapped to primary and replica nodes
	4.	Versioning
	•	Writes produce VersionedValue with a new VectorClock for the node
	•	Timestamps used as tie-breakers for concurrent writes
	5.	Put Operation
	•	Routes key to replica nodes
	•	Writes value to all replicas
	•	Enforces write quorum
	6.	Get Operation
	•	Reads from replica nodes
	•	Merges versions using vector clocks
	•	Enforces read quorum
	7.	Conflict Resolution
	•	Uses VectorClock.compare() to detect concurrent updates
	•	Returns all conflicting versions or resolves via timestamp tie-breaker
	8.	Testing
	•	Multiple nodes simulated in a single JVM
	•	Quorum parameters configurable
	•	Tested with simple main class to insert and read keys

⸻

Key Design Decisions
	1.	Vector Clocks Instead of Timestamps
	•	Timestamps alone cannot guarantee causal ordering
	•	Vector clocks provide causal versioning
	2.	Cluster-Level Quorum Enforcement
	•	Cluster handles routing, replication, and quorum
	•	Nodes only store data locally
	3.	Optional Persistence
	•	StorageEngine can be swapped for PostgreSQL-backed storage
	•	Provides foundation for production-level persistence
	4.	Single JVM Simulation
	•	All nodes run in one JVM for simplicity
	•	Can be extended to networked nodes later

⸻

Next Steps / Extensions
	1.	Integrate PostgreSQL or Redis
	•	For persistent or distributed storage
	2.	Add Node Failure Simulation
	•	Simulate unavailable nodes and test read/write quorums
	3.	Add Eventual Consistency Checks
	•	Background repair threads to reconcile vector clocks
	4.	Move to Raft
	•	Implement strong consistency with leader election and log replication
	5.	Add Client API
	•	REST/gRPC endpoints for external access

⸻

Usage
	1.	Compile with Maven wrapper:

./mvnw clean compile

	2.	Run a main test class:

public static void main(String[] args) {
    Cluster cluster = new Cluster(3, 2, 2, 2); // 3 nodes, replication=2, quorum=2
    cluster.put("key1", "value1");
    System.out.println(cluster.get("key1"));
}

	3.	Adjust replication and quorum parameters to test different scenarios

⸻

Learning Outcomes
	•	How Dynamo-style replication works
	•	Quorum-based consistency enforcement
	•	Using vector clocks for causal versioning
	•	Implementing consistent hashing
	•	Building cluster simulations in a single JVM
	•	Preparing for Raft-based consensus implementation next

⸻

This project provides a full MVP for Dynamo-style key-value store, production-aware, interview-focused, and ready for extension into more complex distributed systems.
