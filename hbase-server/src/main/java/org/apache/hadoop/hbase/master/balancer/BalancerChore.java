/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hbase.master.balancer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.classification.InterfaceAudience;
import org.apache.hadoop.hbase.Chore;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.master.HMaster;

/**
 * Chore that will call HMaster.balance{@link org.apache.hadoop.hbase.master.HMaster#balance()} when
 * needed.
 */
@InterfaceAudience.Private
public class BalancerChore extends Chore {
  private static final Log LOG = LogFactory.getLog(BalancerChore.class);

  private final HMaster master;

  public BalancerChore(HMaster master) {
    super(master.getServerName() + "-BalancerChore",
        master.getConfiguration().getInt("hbase.balancer.period", 300000),
        master);
    this.master = master;
  }

  @Override
  protected void chore() {
    try {
      master.balance();
    } catch (HBaseIOException e) {
      LOG.error("Failed to balance.", e);
    }
  }
}
