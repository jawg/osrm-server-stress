/*
 * Copyright 2015 eBusiness Information
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
enablePlugins(GatlingPlugin)

scalaVersion := "2.11.8"

scalacOptions := Seq(
    "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
    "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

resolvers += Resolver.mavenLocal

libraryDependencies += "io.gatling"           % "gatling-test-framework" % "2.2.3" % "test" changing()
libraryDependencies += "io.gatling.highcharts"           % "gatling-charts-highcharts" % "2.2.3" % "test" changing()

Project.inConfig(Test)(baseAssemblySettings)
assemblyMergeStrategy in (Test, assembly) := {
  case "META-INF/io.netty.versions.properties" => MergeStrategy.discard
  case x => (assemblyMergeStrategy in assembly).value(x)
}
assemblyJarName in (Test, assembly) := s"${name.value}-test-${version.value}.jar"
