/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.example;

public class Orange {

    private final Integer wrap;
    private final int max;
    private int count = 1;

    public Orange(final Integer wrap, final Integer max) {
        this.wrap = wrap;
        this.max = max == null ? Integer.MAX_VALUE : max;
    }

    public Object method(final int i) {
        if (i == 0) throw new OrangeException("Bad news");
        if (count >= max) return method(i - 1);
        if (wrap == null || (i % wrap) != 0) return method(i - 1);

        count++;

        try {
            return method(i - 1);
        } catch (Exception e) {
            throw new OrangeException(e);
        }
    }
}
