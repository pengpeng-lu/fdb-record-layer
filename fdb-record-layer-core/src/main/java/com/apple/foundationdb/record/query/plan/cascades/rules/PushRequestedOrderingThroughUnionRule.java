/*
 * PushRequestedOrderingThroughUnionRule.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2019 Apple Inc. and the FoundationDB project authors
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

package com.apple.foundationdb.record.query.plan.cascades.rules;

import com.apple.foundationdb.annotation.API;
import com.apple.foundationdb.record.query.plan.cascades.CascadesRule;
import com.apple.foundationdb.record.query.plan.cascades.CascadesRuleCall;
import com.apple.foundationdb.record.query.plan.cascades.Reference;
import com.apple.foundationdb.record.query.plan.cascades.PlannerRule.PreOrderRule;
import com.apple.foundationdb.record.query.plan.cascades.Quantifier;
import com.apple.foundationdb.record.query.plan.cascades.RequestedOrdering;
import com.apple.foundationdb.record.query.plan.cascades.RequestedOrderingConstraint;
import com.apple.foundationdb.record.query.plan.cascades.expressions.LogicalUnionExpression;
import com.apple.foundationdb.record.query.plan.cascades.matching.structure.BindingMatcher;
import com.apple.foundationdb.record.query.plan.cascades.matching.structure.PlannerBindings;
import com.apple.foundationdb.record.query.plan.cascades.matching.structure.ReferenceMatchers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.apple.foundationdb.record.query.plan.cascades.matching.structure.MultiMatcher.all;
import static com.apple.foundationdb.record.query.plan.cascades.matching.structure.QuantifierMatchers.forEachQuantifierOverRef;
import static com.apple.foundationdb.record.query.plan.cascades.matching.structure.RelationalExpressionMatchers.logicalUnionExpression;

/**
 * A rule that pushes an ordering {@link RequestedOrderingConstraint} through a {@link LogicalUnionExpression}.
 */
@API(API.Status.EXPERIMENTAL)
@SuppressWarnings("PMD.TooManyStaticImports")
public class PushRequestedOrderingThroughUnionRule extends CascadesRule<LogicalUnionExpression> implements PreOrderRule {
    private static final BindingMatcher<Reference> lowerRefMatcher = ReferenceMatchers.anyRef();
    private static final BindingMatcher<Quantifier.ForEach> innerQuantifierMatcher = forEachQuantifierOverRef(lowerRefMatcher);
    private static final BindingMatcher<LogicalUnionExpression> root =
            logicalUnionExpression(all(innerQuantifierMatcher));

    public PushRequestedOrderingThroughUnionRule() {
        super(root, ImmutableSet.of(RequestedOrderingConstraint.REQUESTED_ORDERING));
    }

    @Override
    public void onMatch(@Nonnull final CascadesRuleCall call) {
        final Optional<Set<RequestedOrdering>> requestedOrderingsOptional =
                call.getPlannerConstraintMaybe(RequestedOrderingConstraint.REQUESTED_ORDERING);
        if (requestedOrderingsOptional.isEmpty()) {
            return;
        }

        // push only exhaustive requested orderings
        final var requestedOrderings = requestedOrderingsOptional.get();
        final var exhaustiveRequestedOrderings =
                requestedOrderings
                        .stream()
                        .map(RequestedOrdering::exhaustive)
                        .collect(ImmutableSet.toImmutableSet());

        final PlannerBindings bindings = call.getBindings();
        final List<? extends Quantifier.ForEach> rangesOverQuantifiers =
                bindings.getAll(innerQuantifierMatcher);

        for (int i = 0; i < rangesOverQuantifiers.size(); i++) {
            final var rangesOverQuantifier = rangesOverQuantifiers.get(i);
            //
            // The first quantifier needs to produce all possible orderings, the other ones get specifically requested
            // in the union implementation rule.
            //
            call.pushConstraint(rangesOverQuantifier.getRangesOver(),
                    RequestedOrderingConstraint.REQUESTED_ORDERING,
                    i == 0 ? exhaustiveRequestedOrderings : requestedOrderings);
        }

        final var firstQuantifier =
                Objects.requireNonNull(Iterables.getFirst(rangesOverQuantifiers, null));
        call.pushConstraint(firstQuantifier.getRangesOver(),
                RequestedOrderingConstraint.REQUESTED_ORDERING,
                exhaustiveRequestedOrderings);
    }
}
