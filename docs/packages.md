Packages
========

`bob` package is divided to several sub-packages, when only the basic agent class, responsible for forwarding each message to the correct class, resides on the top-level package.

* `bid` - Includes logic related to the generation of the bid bundle.
* `campaign` - includes logic for generating the campaign bid, data structure for the campaign data, in-memory storage with all announced and acquired campaigns, and some useful queries on stored campaigns.
* `learn` - Includes dedicated data structures, in-memory storage and logic for storing and querying past campaign and bid bundles results. Provides custom k-Nearest-Neighbors implementation for getting similar campaigns and bid bundles used by the relevant bids generation.   
* `plumbing` - Used to setup the dependency injection and initialize all the required modules (like loading stored JSON data).
* `publisher` - Includes the basic provided code to ensure bid bundles will be sent to all publishers. Does not include usage of the publisher report or any publisher related optimizations (different bids for different publishers) - we considered it to be more complex and less effective than other optimizations, and it was continuously delayed.   
* `sim` - used to stored general simulation related data (e.g., the current day, the agent quality score), and perform the necessary operations on simulation start/end.
* `ucs` - Used to generate the UCS bid. Stores the current simulation UCS bids and their results for this purpose.   
* `utils` - Variety of useful functions for the agent operation. Mathematical calculations, serialization and handling files.


#### Internal Dependencies

![alt text](https://github.com/ranf/adx-bob/raw/master/docs/packages-dependency-graph.png "Dependency Graph")

#### Full Dependencies Graph

![alt text](https://github.com/ranf/adx-bob/raw/master/docs/full-packages-dependency-graph.png "Full Dependency Graph")
