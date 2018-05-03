/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package me.sunlan.sparklabs.wordcount

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext

class WordCount {
    static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount")
        JavaSparkContext sc = new JavaSparkContext(conf)
        def data =
                new File(args[0])
                        .listFiles()
                        .inject(null) { r, e ->
                    def data = sc.textFile(e.absolutePath)
                    return null == r ? data : r.union(data)
                }

        data.flatMap { line -> line.split(/\s+/).grep { it.matches(/\w+/) }.iterator() }
                .mapToPair { word -> new scala.Tuple2(word, 1) }
                .reduceByKey { c1, c2 -> c1 + c2 }
                .coalesce(1, true)
                .saveAsTextFile(args[1])

        sc.stop()
    }
}
