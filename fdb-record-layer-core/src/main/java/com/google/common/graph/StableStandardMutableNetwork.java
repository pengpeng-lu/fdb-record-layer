/*
 * Copyright (C) 2016 The Guava Authors
 * Copyright 2025 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.graph;

import com.google.common.base.Verify;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.graph.GraphConstants.PARALLEL_EDGES_NOT_ALLOWED;
import static com.google.common.graph.GraphConstants.REUSING_EDGE;
import static com.google.common.graph.GraphConstants.SELF_LOOPS_NOT_ALLOWED;

/**
 * This is mostly a copy of {@link StandardMutableNetwork} that delegates the construction of its underlying
 * connections to a class providing stable iteration order over them.
 *
 * @param <N> Node parameter type
 * @param <E> Edge parameter type
 */
@SuppressWarnings({"UnstableApiUsage", "PMD.TooManyStaticImports"})
@ElementTypesAreNonnullByDefault
public final class StableStandardMutableNetwork<N, E> implements MutableNetwork<N, E> {
    private final StandardMutableNetwork<N, E> standardMutableNetwork;

    public StableStandardMutableNetwork(NetworkBuilder<? super N, ? super E> builder) {
        standardMutableNetwork = new StandardMutableNetwork<>(builder);
    }

    @Override
    public boolean addNode(N node) {
        checkNotNull(node, "node");
        if (standardMutableNetwork.containsNode(node)) {
            return false;
        }
        addNodeInternal(node);
        return true;
    }

    @Override
    public boolean addEdge(N nodeU, N nodeV, E edge) {
        checkNotNull(nodeU, "nodeU");
        checkNotNull(nodeV, "nodeV");
        checkNotNull(edge, "edge");

        if (standardMutableNetwork.containsEdge(edge)) {
            EndpointPair<N> existingIncidentNodes = incidentNodes(edge);
            EndpointPair<N> newIncidentNodes = EndpointPair.of(this, nodeU, nodeV);
            checkArgument(
                    existingIncidentNodes.equals(newIncidentNodes),
                    REUSING_EDGE,
                    edge,
                    existingIncidentNodes,
                    newIncidentNodes);
            return false;
        }
        NetworkConnections<N, E> connectionsU = standardMutableNetwork.nodeConnections.get(nodeU);
        if (!allowsParallelEdges()) {
            checkArgument(
                    !(connectionsU != null && connectionsU.successors().contains(nodeV)),
                    PARALLEL_EDGES_NOT_ALLOWED,
                    nodeU,
                    nodeV);
        }
        boolean isSelfLoop = nodeU.equals(nodeV);
        if (!allowsSelfLoops()) {
            checkArgument(!isSelfLoop, SELF_LOOPS_NOT_ALLOWED, nodeU);
        }

        if (connectionsU == null) {
            connectionsU = addNodeInternal(nodeU);
        }
        connectionsU.addOutEdge(edge, nodeV);
        NetworkConnections<N, E> connectionsV = standardMutableNetwork.nodeConnections.get(nodeV);
        if (connectionsV == null) {
            connectionsV = addNodeInternal(nodeV);
        }
        connectionsV.addInEdge(edge, nodeU, isSelfLoop);
        standardMutableNetwork.edgeToReferenceNode.put(edge, nodeU);
        return true;
    }

    @CanIgnoreReturnValue
    private NetworkConnections<N, E> addNodeInternal(N node) {
        NetworkConnections<N, E> connections = newConnections();
        checkState(standardMutableNetwork.nodeConnections.put(node, connections) == null);
        return connections;
    }

    @Nonnull
    private NetworkConnections<N, E> newConnections() {
        Verify.verify(isDirected() && allowsParallelEdges(),
                "Only directed with parallel edges network is supported");
        return StableDirectedMultiNetworkConnections.of();
    }

    @Override
    public Graph<N> asGraph() {
        return standardMutableNetwork.asGraph();
    }

    @Override
    public int degree(N node) {
        return standardMutableNetwork.degree(node);
    }

    @Override
    public int inDegree(N node) {
        return standardMutableNetwork.inDegree(node);
    }

    @Override
    public int outDegree(N node) {
        return standardMutableNetwork.outDegree(node);
    }

    @Override
    public Set<E> adjacentEdges(E edge) {
        return standardMutableNetwork.adjacentEdges(edge);
    }

    @Override
    public Set<E> edgesConnecting(EndpointPair<N> endpoints) {
        return standardMutableNetwork.edgesConnecting(endpoints);
    }

    @Override
    public Optional<E> edgeConnecting(N nodeU, N nodeV) {
        return standardMutableNetwork.edgeConnecting(nodeU, nodeV);
    }

    @Override
    public Optional<E> edgeConnecting(EndpointPair<N> endpoints) {
        return standardMutableNetwork.edgeConnecting(endpoints);
    }

    @Override
    @CheckForNull
    public E edgeConnectingOrNull(N nodeU, N nodeV) {
        return standardMutableNetwork.edgeConnectingOrNull(nodeU, nodeV);
    }

    @Override
    @CheckForNull
    public E edgeConnectingOrNull(EndpointPair<N> endpoints) {
        return standardMutableNetwork.edgeConnectingOrNull(endpoints);
    }

    @Override
    public boolean hasEdgeConnecting(N nodeU, N nodeV) {
        return standardMutableNetwork.hasEdgeConnecting(nodeU, nodeV);
    }

    @Override
    public boolean hasEdgeConnecting(EndpointPair<N> endpoints) {
        return standardMutableNetwork.hasEdgeConnecting(endpoints);
    }

    @Override
    public Set<N> nodes() {
        return standardMutableNetwork.nodes();
    }

    @Override
    public Set<E> edges() {
        return standardMutableNetwork.edges();
    }

    @Override
    public boolean isDirected() {
        return standardMutableNetwork.isDirected();
    }

    @Override
    public boolean allowsParallelEdges() {
        return standardMutableNetwork.allowsParallelEdges();
    }

    @Override
    public boolean allowsSelfLoops() {
        return standardMutableNetwork.allowsSelfLoops();
    }

    @Override
    public ElementOrder<N> nodeOrder() {
        return standardMutableNetwork.nodeOrder();
    }

    @Override
    public ElementOrder<E> edgeOrder() {
        return standardMutableNetwork.edgeOrder();
    }

    @Override
    public Set<E> incidentEdges(N node) {
        return standardMutableNetwork.incidentEdges(node);
    }

    @Override
    public EndpointPair<N> incidentNodes(E edge) {
        return standardMutableNetwork.incidentNodes(edge);
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        return standardMutableNetwork.adjacentNodes(node);
    }

    @Override
    public Set<E> edgesConnecting(N nodeU, N nodeV) {
        return standardMutableNetwork.edgesConnecting(nodeU, nodeV);
    }

    @Override
    public Set<E> inEdges(N node) {
        return standardMutableNetwork.inEdges(node);
    }

    @Override
    public Set<E> outEdges(N node) {
        return standardMutableNetwork.outEdges(node);
    }

    @Override
    public Set<N> predecessors(N node) {
        return standardMutableNetwork.predecessors(node);
    }

    @Override
    public Set<N> successors(N node) {
        return standardMutableNetwork.successors(node);
    }

    @Override
    public boolean addEdge(EndpointPair<N> endpoints, E edge) {
        return standardMutableNetwork.addEdge(endpoints, edge);
    }

    @Override
    public boolean removeNode(N node) {
        return standardMutableNetwork.removeNode(node);
    }

    @Override
    public boolean removeEdge(E edge) {
        return standardMutableNetwork.removeEdge(edge);
    }
}
