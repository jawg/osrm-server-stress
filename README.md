# osrm-server-stress

This gatling script allow you to qualify and stress an **OSRM Server**

The Testing scenario is the following:
 * Each user will randomly select a region in the regions.csv file (round-robin strategy)
 * In this region, the user will randomly choose two positions (lat/lng) in the same contient
 * Then, the user will request and get the route between the two positions

## Requirements
 * SBT : http://www.scala-sbt.org/download.html
 * Scala : http://www.scala-lang.org/download/

Scala plugin for IntelliJ platform also helps.

## How to use

This script has been tested and approved by both Gatling and Gatling Frontline solutions.

 * Clone the project
 ```git clone https://github.com/jawg/osrm-server-stress.git```sh  
 * Set your environment properties in {projectRoot}/src/test/resources
 Properties are server.url, simulation.users.count...  
 * Browse the project root and execute the following commands
 ```sbt "run-main io.jawg.GenerateSeedsCsv"``` to generate seeds for all simulated users. Skip this step if you want to use a custom seeds file or keep the previous one and produce the exact same test.
 ```sbt run "gatling:testOnly io.jawg.OSRMSimulation"``` to run the stress test.


## License

Copyright 2017 eBusiness Information

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


