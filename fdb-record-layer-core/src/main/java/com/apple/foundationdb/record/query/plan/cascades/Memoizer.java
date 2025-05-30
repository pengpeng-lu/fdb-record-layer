/*
 * Memoizer.java
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

package com.apple.foundationdb.record.query.plan.cascades;

import com.apple.foundationdb.annotation.API;
import com.apple.foundationdb.record.query.plan.cascades.expressions.RelationalExpression;
import com.apple.foundationdb.record.query.plan.plans.RecordQueryPlan;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An interface for memoizing {@link Reference}s and their member {@link RelationalExpression}s. The methods declared in
 * this interface mostly have one thing in common. They expect among their parameters an expression or a collection of
 * expressions which are to be memoized and return a {@link Reference} which may be a new reference that was just
 * created or an already existing reference that was previously memoized by this {@code Memoizer} and that was deemed
 * to be compatible to be reused.
 * <br>
 * There are numerous considerations that determine if a reference can be safely reused. Most of these considerations
 * can be derived from the individual use case and the context the method is called from. Each individual method
 * declaration in this interface will also indicate (via java doc) if the method can return a reused expression or
 * if the caller can always expect a fresh reference to be returned. Note that the terminology used here is that
 * a <em>memoized expression</em> indicates that the memoization structures of the planner are aware of this expression.
 * A reference (not an expression) can be reused as an effect of memoization of the given expressions (depending on
 * use case and context).
 */
@API(API.Status.EXPERIMENTAL)
public interface Memoizer extends ExploratoryMemoizer, FinalMemoizer {
    /**
     * Builder for references.
     */
    interface ReferenceBuilder {
        @Nonnull
        Reference reference();

        @Nonnull
        Set<? extends RelationalExpression> members();
    }

    /**
     * Builder for references.
     */
    interface ReferenceOfPlansBuilder extends ReferenceBuilder {
        @Nonnull
        @Override
        Set<? extends RecordQueryPlan> members();
    }
}
