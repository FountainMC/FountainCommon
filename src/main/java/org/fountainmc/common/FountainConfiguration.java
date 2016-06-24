package org.fountainmc.common;

import java.util.Map;

import org.fountainmc.api.NonnullByDefault;

@NonnullByDefault
public interface FountainConfiguration {
    Map<String, Object> getMetricsConfiguration();
}
