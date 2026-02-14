Bonus Task — Simple In-Memory Caching Layer

Objective
The goal of this task is to improve application performance by implementing a simple in-memory caching mechanism for frequently accessed data.

Implemented Solution
A simple in-memory cache was implemented and integrated into the service layer of the application. The cache stores the result of frequently used read operations and prevents repeated database queries for the same data.

Cache Implementation
The cache is stored entirely in memory using a Map-based data structure. It is implemented as a Singleton to ensure that only one cache instance exists across the application lifecycle.

Cached Method
The result of the getAll() method is cached. This method is frequently used to retrieve all activity records.

Cache Behavior
On the first call, data is retrieved from the database and stored in the cache.
On subsequent calls, data is returned directly from the cache without querying the database again.

Cache Invalidation
To maintain data consistency, the cache is automatically invalidated after any data modification operation, including create, update, and delete.
Additionally, a manual cache clearing mechanism is provided through a dedicated endpoint.

Design Constraints and Principles
The cache is stored in memory and does not rely on external systems.
Only one cache instance exists due to the Singleton pattern.
Cache logic follows SOLID principles by separating caching responsibility from business logic and data access logic.
The layered architecture of the application is preserved, and the repository layer remains unaware of caching.

Requirements Fulfilled
A simple in-memory cache is implemented.
A frequently used method is cached.
Repeated calls return cached data.
The Singleton pattern is properly applied.
Cache invalidation after update and delete operations is implemented.
Manual cache clearing is supported.
The caching mechanism does not break layered architecture.

Status
The bonus task is fully implemented and ready for evaluation.
