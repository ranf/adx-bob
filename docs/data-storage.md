Data Storage
============

All the data used by the agent is stored in-memory as fields on singleton objects, and queried using Java 8 Streams API.  
This allows for fast and simple data manipulation, and even tough it takes some assumptions (single thread, data is small enough to fit in memory), its interface is standard enough to be ported to SQL/NoSQL if necessary.

We split the data to general Game data, Campaign related data, and data stored for learning purpose.

Data used across multiple simulations (for learning - UCS, campaign history, bid bundle history) is loaded to memory when the agent starts, and persisted back to disk as JSON or Properties files at the end of every simulation.
