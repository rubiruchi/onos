/*
 * Copyright 2015-present Open Networking Foundation
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
package org.onosproject.net.host.impl;

import org.onlab.packet.IpAddress;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Host;
import org.onosproject.net.HostLocation;
import org.onosproject.net.SparseAnnotations;
import org.onosproject.net.config.basics.BasicHostConfig;
import org.onosproject.net.device.impl.BasicElementOperator;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.host.HostDescription;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementations of merge policies for various sources of host configuration
 * information. This includes applications, providers, and network configurations.
 */
public final class BasicHostOperator extends BasicElementOperator {

    private BasicHostOperator() {
    }

    /**
     * Generates a HostDescription containing fields from a HostDescription and
     * a HostConfig.
     *
     * @param cfg   the host config entity from network config
     * @param descr a HostDescription
     * @return HostDescription based on both sources
     */
    public static HostDescription combine(BasicHostConfig cfg,
                                          HostDescription descr) {
        if (cfg == null || descr == null) {
            return descr;
        }

        HostLocation location = descr.location();
        ConnectPoint cfgLocation = cfg.location();
        if (cfgLocation != null) {
            location = new HostLocation(cfgLocation, System.currentTimeMillis());
        }

        Set<IpAddress> ipAddresses = descr.ipAddress();
        Set<IpAddress> cfgIpAddresses = cfg.ipAddresses();
        if (cfgIpAddresses != null) {
            ipAddresses = cfgIpAddresses;
        }

        SparseAnnotations sa = combine(cfg, descr.annotations());
        return new DefaultHostDescription(descr.hwAddress(), descr.vlan(),
                                          location, ipAddresses,
                                          descr.configured(), sa);
    }

    /**
     * Generates an annotation from an existing annotation and HostConfig.
     *
     * @param cfg the host config entity from network config
     * @param an  the annotation
     * @return annotation combining both sources
     */
    public static SparseAnnotations combine(BasicHostConfig cfg, SparseAnnotations an) {
        DefaultAnnotations.Builder builder = DefaultAnnotations.builder();

        combineElementAnnotations(cfg, builder);

        return DefaultAnnotations.union(an, builder.build());
    }

    /**
     * Returns a description of the given host.
     *
     * @param host the host
     * @return a description of the host
     */
    public static HostDescription descriptionOf(Host host) {
        checkNotNull(host, "Must supply a non-null Host");
        return new DefaultHostDescription(host.mac(), host.vlan(), host.location(),
                                          host.ipAddresses(), host.configured(),
                                          (SparseAnnotations) host.annotations());
    }
}
