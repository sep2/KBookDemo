# Description

KBookDemo aims to provide a simple way to define real-time streaming data processing
from a DAG (directed acyclic graph, see *sample1.json*). End-users submits DAG definition
(called KBook, which will be run as Kafka Streams Tasks) to the server and
are possible to visualize, manage and inspect these jobs.

# Compile
`mill server.compile`

# Assembly jar
`mill server.assembly`

# Run
`java -jar KBookDemo.jar`

# License
GNU GPLv3
