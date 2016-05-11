Campaign Bid
============


Strategy
-----------

The campaign bid is based on its reach.

The amount of known competing campaigns, the size of its market segment, and its mobile and video coefficients are used to stir our bid up or down as an attempt to estimate the correct value of the campaign.

The amount of campaigns we currently have (how active is our agent), and a random factor, are used to mitigate issues with the previous calculations to ensure our agent will have reasonable performance even if its predefined behavior is less suited for the current game situation.

In addition, we are using custom KNN algorithm on previous campaigns results (from current game and past simulations) to find campaigns with similar characteristics and based on each bid and its results (profit and completion rate), we may estimate if we need to decrease/increase the bid. For example, if we had a similar campaign which was not profitable or not completed, it is likely to assume we should bid more than previously on the current campaign.

Overrides
---------
However, in order to leverage certain scenarios there are few overrides to the default behavior.

Maximal Bid:
* When our quality score is horribly low we always go with the maximal campaign bid in hope of a random win - we assume our opponents are competitive, and it is unlikely all of them will not give a good bid on a reachable campaign.

Minimal bid:
* When there are very few impressions per day we go with the minimal bid.
* When the campaign is long enough then it is always likely we will be able to complete it, and we will even have time at the beginning to try and get cheap impressions, so we can go with the minimal bid.
* Late in game we stop to care about our quality score always bid the minimal bid on the campaign - we should always profit, assuming we will never give a losing bid on an impression.
