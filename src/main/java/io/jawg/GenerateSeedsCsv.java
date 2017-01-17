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
package io.jawg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Random;

// sbt "run-main io.jawg.GenerateSeedsCsv"

public class GenerateSeedsCsv {

    public static void main(String[] args) throws IOException {

        Properties prop = new Properties();
        String propFileName = "main.parameters.properties";

        InputStream inputStream = GenerateSeedsCsv.class.getClassLoader().getResourceAsStream(propFileName);

        prop.load(inputStream);
        int targetSeedCount = Integer.parseInt(prop.getProperty("simulation.users.count"));

        File file = new File("src/test/resources/seeds.csv");
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file);

        Random rand = new Random();

        writer.append("seed");
        writer.append("\n");
        writer.flush();
        for (int i = 0; i < targetSeedCount; i++) {
            int randInt = Math.abs(rand.nextInt());
            writer.append(String.valueOf(randInt));
            writer.append("\n");
            writer.flush();
        }
    }
}
