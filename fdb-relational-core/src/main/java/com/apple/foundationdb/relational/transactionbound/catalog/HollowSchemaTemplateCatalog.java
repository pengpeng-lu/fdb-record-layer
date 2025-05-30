/*
 * HollowSchemaTemplateCatalog.java
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

package com.apple.foundationdb.relational.transactionbound.catalog;

import com.apple.foundationdb.annotation.API;
import com.apple.foundationdb.relational.api.RelationalResultSet;
import com.apple.foundationdb.relational.api.Transaction;
import com.apple.foundationdb.relational.api.catalog.SchemaTemplateCatalog;
import com.apple.foundationdb.relational.api.exceptions.OperationUnsupportedException;
import com.apple.foundationdb.relational.api.exceptions.RelationalException;
import com.apple.foundationdb.relational.api.metadata.SchemaTemplate;

import javax.annotation.Nonnull;

@API(API.Status.EXPERIMENTAL)
public class HollowSchemaTemplateCatalog implements SchemaTemplateCatalog {

    public static final HollowSchemaTemplateCatalog INSTANCE = new HollowSchemaTemplateCatalog();

    @Override
    public boolean doesSchemaTemplateExist(@Nonnull Transaction txn, @Nonnull String templateName) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }

    @Override
    public boolean doesSchemaTemplateExist(@Nonnull Transaction txn, @Nonnull String templateName, int version) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }

    @Nonnull
    @Override
    public SchemaTemplate loadSchemaTemplate(@Nonnull Transaction txn, @Nonnull String templateName) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }

    @Nonnull
    @Override
    public SchemaTemplate loadSchemaTemplate(@Nonnull Transaction txn, @Nonnull String templateId, int version) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }

    @Override
    public void createTemplate(@Nonnull Transaction txn, @Nonnull SchemaTemplate newTemplate) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }

    @Override
    public RelationalResultSet listTemplates(@Nonnull Transaction txn) {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.").toUncheckedWrappedException();
    }

    @Override
    public void deleteTemplate(@Nonnull Transaction txn, @Nonnull String templateId, boolean throwIfDoesNotExist) {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.").toUncheckedWrappedException();
    }

    @Override
    public void deleteTemplate(@Nonnull Transaction txn, @Nonnull String templateId, int version, boolean throwIfDoesNotExist) throws RelationalException {
        throw new OperationUnsupportedException("This Schema Template Catalog is hollow and does not support calls.");
    }
}
