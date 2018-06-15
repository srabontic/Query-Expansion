Rocchio Algorithm:
Rocchio Algorithm reformulates a query in such a way that:
It gets closer to the neighborhood of the relevant document and gets away from the
neighborhood of the non-relevant documents. Documents identified as relevant (to a

given query) have similarities among themselves and non-relevant docs have term-
weight vectors, which are dissimilar from the relevant documents.

Twenty queries have been selected to verify the Rocchio algorithm. Following was the
approach used to arrive to a set of weights that fetches a more relevant modified query:
1. Get the query and the results from Solr for the results for the query
2. Fetch the the results from Google
3. Label each result from our search query as either relevant for non-relevant
based upon the results of Google.
4. Increment beta, decrement gamma keeping alpha constant to get higher ratio.
5. Repeat step 4 for a fixed number of iterations or until weights stabilize
6. Output the modified query from the query vector


Pseudo Relevance feedback:
Another query expansion technique is implemented which is query expansion through
local clustering. It operates solely on the documents retrieved but this is not suitable for
web searches because it is time consuming. There are three strategies of building local
clusters:
•Association clusters
– Consider the co-occurrence of stems (terms) inside documents
• Metric Clusters
– Consider the distance between two terms in a document
• Scalar Clusters
– Consider the neighborhoods of two terms
We implemented metric clustering algorithm. The queries tested with each of the
clustering techniques were determined by:
• Queries with only one keyword that is in the relevant (i.e.) animal domain
• Queries with two or three keywords in animal domain
• Queries with stop words, ambiguous or irrelevant words
