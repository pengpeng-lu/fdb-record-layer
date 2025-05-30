/*
 * AbstractQueryFactory.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2025 Apple Inc. and the FoundationDB project authors
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

package com.apple.foundationdb.relational.api.ddl;

import javax.annotation.Nonnull;
import java.net.URI;

public abstract class AbstractQueryFactory implements DdlQueryFactory {
    @Override
    public DdlQuery getListDatabasesQueryAction(@Nonnull URI prefixPath) {
        return NoOpQueryFactory.INSTANCE.getListDatabasesQueryAction(prefixPath);
    }

    @Override
    public DdlQuery getListSchemaTemplatesQueryAction() {
        return NoOpQueryFactory.INSTANCE.getListSchemaTemplatesQueryAction();
    }

    @Override
    public DdlQuery getDescribeSchemaTemplateQueryAction(@Nonnull String schemaId) {
        return NoOpQueryFactory.INSTANCE.getDescribeSchemaTemplateQueryAction(schemaId);
    }

    @Override
    public DdlQuery getDescribeSchemaQueryAction(@Nonnull URI dbId, @Nonnull String schemaId) {
        return NoOpQueryFactory.INSTANCE.getDescribeSchemaQueryAction(dbId, schemaId);
    }
}
