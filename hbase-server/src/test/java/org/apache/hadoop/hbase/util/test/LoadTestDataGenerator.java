/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hbase.util.test;

import java.io.IOException;
import java.util.Set;

import org.apache.hadoop.hbase.classification.InterfaceAudience;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;

/**
 * A generator of random data (keys/cfs/columns/values) for load testing.
 * Contains LoadTestKVGenerator as a matter of convenience...
 */
@InterfaceAudience.Private
public abstract class LoadTestDataGenerator {
  protected LoadTestKVGenerator kvGenerator;

  // The mutate info column stores information
  // about update done to this column family this row.
  public final static byte[] MUTATE_INFO = "mutate_info".getBytes();

  // The increment column always has a long value,
  // which can be incremented later on during updates.
  public final static byte[] INCREMENT = "increment".getBytes();

  protected String[] args;

  public LoadTestDataGenerator() {

  }

  /**
   * Initializes the object.
   * @param minValueSize minimum size of the value generated by
   * {@link #generateValue(byte[], byte[], byte[])}.
   * @param maxValueSize maximum size of the value generated by
   * {@link #generateValue(byte[], byte[], byte[])}.
   */
  public LoadTestDataGenerator(int minValueSize, int maxValueSize) {
    this.kvGenerator = new LoadTestKVGenerator(minValueSize, maxValueSize);
  }

  /**
   * initialize the LoadTestDataGenerator
   *
   * @param args
   *          init args
   */
  public void initialize(String[] args) {
    this.args = args;
  }

  /**
   * Generates a deterministic, unique hashed row key from a number. That way, the user can
   * keep track of numbers, without messing with byte array and ensuring key distribution.
   * @param keyBase Base number for a key, such as a loop counter.
   */
  public abstract byte[] getDeterministicUniqueKey(long keyBase);

  /**
   * Gets column families for the load test table.
   * @return The array of byte[]s representing column family names.
   */
  public abstract byte[][] getColumnFamilies();

  /**
   * Generates an applicable set of columns to be used for a particular key and family.
   * @param rowKey The row key to generate for.
   * @param cf The column family name to generate for.
   * @return The array of byte[]s representing column names.
   */
  public abstract byte[][] generateColumnsForCf(byte[] rowKey, byte[] cf);

  /**
   * Generates a value to be used for a particular row/cf/column.
   * @param rowKey The row key to generate for.
   * @param cf The column family name to generate for.
   * @param column The column name to generate for.
   * @return The value to use.
   */
  public abstract byte[] generateValue(byte[] rowKey, byte[] cf, byte[] column);

  /**
   * Checks that columns for a rowKey and cf are valid if generated via
   * {@link #generateColumnsForCf(byte[], byte[])}
   * @param rowKey The row key to verify for.
   * @param cf The column family name to verify for.
   * @param columnSet The column set (for example, encountered by read).
   * @return True iff valid.
   */
  public abstract boolean verify(byte[] rowKey, byte[] cf, Set<byte[]> columnSet);

  /**
   * Checks that value for a rowKey/cf/column is valid if generated via
   * {@link #generateValue(byte[], byte[], byte[])}
   * @param rowKey The row key to verify for.
   * @param cf The column family name to verify for.
   * @param column The column name to verify for.
   * @param value The value (for example, encountered by read).
   * @return True iff valid.
   */
  public abstract boolean verify(byte[] rowKey, byte[] cf, byte[] column, byte[] value);

  /**
   * Giving a chance for the LoadTestDataGenerator to change the Mutation load.
   * @param rowkeyBase
   * @param m
   * @return updated Mutation
   * @throws IOException
   */
  public Mutation beforeMutate(long rowkeyBase, Mutation m) throws IOException {
    return m;
  }

  /**
   * Giving a chance for the LoadTestDataGenerator to change the Get load.
   * @param rowkeyBase
   * @param get
   * @return updated Get
   * @throws IOException
   */
  public Get beforeGet(long rowkeyBase, Get get) throws IOException {
    return get;
  }

  /**
   * Return the arguments passed to the generator as list of object
   * @return
   */
  public String[] getArgs() {
    return this.args;
  }
}
