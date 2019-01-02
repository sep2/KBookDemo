# Description

KBookDemo aims to provide a simple way to define real-time streaming data processing
from a DAG (directed acyclic graph, see *sample1.json*). End-users submits DAG definition
(called KBook, which will be run as Kafka Streams Tasks) to the server and
are possible to visualize, manage and inspect these jobs.

Currently only sample APIs are provided, please see *KBookDemo.scala* for details. With
these APIs, you may submit, start, stop, delete and query a KBook instance. 

The next step in the roadmap is to leverage Zookeeper or other cluster negotiators to
turn on High Availability.

# Usage
[Mill](http://www.lihaoyi.com/mill/) is the build tool used by KBookDemo.

## compile
`mill server.compile`

## Assembly Jar
`mill server.assembly`

## Run
`java -jar KBookDemo.jar`

# License
GNU GPLv3
