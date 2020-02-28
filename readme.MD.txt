
Format for execution of jar file:

* Traverse to - "/home/ubuntu/George_IR_19305272" path using 'cd' command
* java -jar george_19305272_LuceneSearchEngine.jar

   ****OR****

Results - 
Available in the folder - "/home/ubuntu/George_IR_19305272" as results.txt



Please Note: my jar file has a total of 10 hits only

Path to where the index files are generated and stored = "/home/ubuntu/George_IR_19305272/Data/index"
Path to source data - "/home/ubuntu/George_IR_19305272/Data" (Cranfield collection)

Format for execution of Trec_Eval:

* Traverse to - "/home/ubuntu/George_IR_19305272/trec_eval-9.0.7" path using 'cd' command
* trec_eval <QRelsCorrectedforTRECeval> <resultsfile>
  E.g., trec_eval test/QRelsCorrectedforTRECeval.txt test/results.txt