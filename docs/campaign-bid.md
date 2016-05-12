Campaign Bid
============

Strategy
-----------
The campaign bid is based on the campaign reach.

##### Value
The amount of known `competing campaigns`, the size of the campaign `market segment`, and its `mobile and video coefficients` are used to stir our bid up or down as an attempt to estimate the correct value of the campaign.

The `market segment` size is based on the table in the specification.  
The `competing campaigns` is a rough estimation on the amount of impressions this campaign will have to compete on with other known campaigns, based on their dates and target segments.   

##### Learning
We are using a custom (and naïve) KNN algorithm on previous campaigns results (from current game and past simulations) to find campaigns with similar characteristics and based on each bid and its results (profit and completion rate), we may estimate if we need to decrease/increase the bid. For example, if we had a similar campaign which was not profitable or not completed, it is likely to assume we should bid more than previously on the current campaign.

The algorithm only search for the `k=8` nearest campaigns we won (from current and previous simulations, we thought about improving it to prefer recent simulations, but did not have enough results yet anyway), which are at least closer than a predefined epsilon. For the distance function we are taking weighted average of the difference in campaign `reach`, `market segment` size, `start day`, and campaign `length` - we assume the nearest campaigns will need to be close enough to all of these, and that these properties are enough to give a crude estimation to the value of the campaign.

The returned results are handled with care. Once the k nearest campaign are found, we slightly stir our previously calculated bid toward the average of these bids. Before the average calculation we increase each neighbor bid if it was not profitable enough, or the campaign was not completed. Otherwise, by the nature of the calculation there is some deviation down, as we only consider winning bids.

The completion of each campaign is calculated when it ends, using the ERR function with the campaign budget, and our quality rating when it ends. The profit is calculated similarly with the ERR function and costs provided by sum the AdNet Report related to the campaign.

KNN is a rather expensive algorithm to run in real-time. In our simulation we expect no issues since there is plenty of time to respond to the server messages, our data is small, and resides fully in memory. On real AdX agent we expect such calculations to still be possible, but require smart filtering of the data, and an efficient in-memory distributed KNN approximation (or something smarter, anyway this approach should still be applicable).  

##### Defenses
The amount of campaigns we currently have (relatively to the total number of active campaigns and the number of active agents) and a random factor, are used to mitigate issues with the previous calculations to ensure our agent will have reasonable performance even if its predefined behavior is less suited for the current game situation.

In general, no single calculation should have dramatic impact on the resulting bid. Together they should give the best results.


Overrides
---------
Despite the statements above, in order to leverage certain scenarios there are some overrides to the default behavior, in which we bid the maximal or minimal allowed bid.

##### Maximal Bid
* When our quality score is horribly low we always go with the maximal campaign bid in hope of a random win - we assume our opponents are competitive, and it is unlikely all of them will not give a good bid on a reachable campaign.

##### Minimal bid
* When there are very few impressions per day we go with the minimal bid.
* When the campaign is long enough then it is always likely we will be able to complete it, and we will even have time at the beginning to try and get cheap impressions, so we can go with the minimal bid.
* Late in game we stop to care about our quality score and always bid the minimal bid on the campaign - we should always profit, assuming we will never give a losing bid on an impression.
