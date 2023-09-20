package io.quarkiverse.googlecloudservices.bigtable.deployment;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;

import io.quarkiverse.googlecloudservice.bigtable.api.BigtableClient;
import io.quarkiverse.googlecloudservices.bigtable.runtime.BigtableClients;
import io.quarkus.gizmo.MethodDescriptor;

public class BigTableDotNames {

    public static final DotName BIGTABLE_CLIENT = DotName.createSimple(BigtableClient.class.getName());
    public static final DotName DATA_CLIENT = DotName.createSimple(BigtableDataClient.class.getName());
    public static final MethodDescriptor CREATE_CLIENT_METHOD = MethodDescriptor.ofMethod(BigtableClients.class, "createClient",
            BigtableDataClient.class, String.class);

    static boolean isBigtableClient(AnnotationInstance instance) {
        return instance.name().equals(BIGTABLE_CLIENT);
    }
}