/*
 * JDBCExternalYamlIntegrationTests.java
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

import com.apple.foundationdb.relational.api.RelationalConnection;
import com.apple.foundationdb.relational.yamltests.YamlRunner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;

import java.sql.DriverManager;

/**
 * A version of {@link YamlIntegrationTests} that points to an external running server.
 */
@Disabled("Depends on an external running service, don't run anywhere automatically")
public class JDBCExternalYamlIntegrationTests extends JDBCYamlIntegrationTests {
    private static final Logger LOG = LogManager.getLogger(JDBCExternalYamlIntegrationTests.class);

    @Override
    YamlRunner.YamlConnectionFactory createConnectionFactory() {
        return connectPath -> {
            final String remoteUrl = connectPath.toString()
                    .replaceFirst("embed:", "relational://localhost:1111");
            LOG.info("Connecting to " + connectPath);
            return DriverManager.getConnection(remoteUrl).unwrap(RelationalConnection.class);
        };
    }

}