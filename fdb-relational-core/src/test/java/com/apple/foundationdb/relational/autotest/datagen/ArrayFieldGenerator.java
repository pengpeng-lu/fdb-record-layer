/*
 * ArrayFieldGenerator.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2021-2024 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb.relational.autotest.datagen;

import com.apple.foundationdb.relational.api.EmbeddedRelationalArray;
import com.apple.foundationdb.relational.api.EmbeddedRelationalStruct;
import com.apple.foundationdb.relational.api.RelationalStructBuilder;

import javax.annotation.Nonnull;
import java.sql.SQLException;

public class ArrayFieldGenerator implements FieldGenerator {
    private final String fieldName;
    private final FieldGenerator arrayGenerator;
    private final RandomDataSource randomSource;
    private final int maxSize;

    public ArrayFieldGenerator(@Nonnull String fieldName,
                               FieldGenerator arrayGenerator,
                               RandomDataSource randomSource,
                               int maxSize) {
        this.fieldName = fieldName;
        this.arrayGenerator = arrayGenerator;
        this.randomSource = randomSource;
        this.maxSize = maxSize;
    }

    @Override
    public void generateValue(@Nonnull RelationalStructBuilder builder) throws SQLException {
        int numFields = randomSource.nextInt(0, maxSize);
        if (numFields > 0) {
            final var arrayBuilder = EmbeddedRelationalArray.newBuilder();
            for (int i = 0; i < numFields; i++) {
                final var tempStructBuilder = EmbeddedRelationalStruct.newBuilder();
                arrayGenerator.generateValue(tempStructBuilder);
                arrayBuilder.addAll(tempStructBuilder.build().getObject(1));
            }
            builder.addArray(fieldName, arrayBuilder.build());
        }
    }
}