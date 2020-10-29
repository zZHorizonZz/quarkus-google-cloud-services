# Quarkiverse - Google Cloud Services - Bigtable

This extension allows using Google Cloud Bigtable inside your Quarkus application.

## Bootstrapping the project

First, we need a new project. Create a new project with the following command:

```shell script
mvn io.quarkus:quarkus-maven-plugin:<quarkusVersion>:create \
    -DprojectGroupId=org.acme \
    -DprojectArtifactId=pubsub-quickstart \
    -Dextensions="quarkus-google-cloud-bigtable"
cd pubsub-quickstart
```

This command generates a Maven project, importing the Google Cloud Bigtable extension.

If you already have your Quarkus project configured, you can add the `quarkus-google-cloud-bigtable` extension to your project by running the following command in your project base directory:
```shell script
./mvnw quarkus:add-extension -Dextensions="quarkus-google-cloud-bigtable"
```

This will add the following to your pom.xml:

```xml
<dependency>
    <groupId>io.quarkiverse.googlecloudservices</groupId>
    <artifactId>quarkus-google-cloud-bigtable</artifactId>
</dependency>
```

## Preparatory steps

To test Bigtable you first need to create a Bigtable instance named `test-instane`

You can create one with `gcloud`:

```
gcloud bigtable instances create test-instance \
    --cluster=test-cluster \
    --cluster-zone=europe-west1-b \
    --display-name=Test
```

As Bigtable mandates the usage of the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to define its credentials, 
you need to set this one instead of relying on the `quarkus.google.cloud.service-account-location` property. 

```
export GOOGLE_APPLICATION_CREDENTIALS=<your-service-account-file>
```

## Some example

This is an example usage of the extension: we create a REST resource with a single endpoint that write a row inside the Bigtable `test-column-family` column family of the `test-table` table then retrieve it.

We also init the Bigtable table at `@PostConstruct` time if it didn't exist.

```java
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;

@Path("/bigtable")
public class BigtableResource {
   private static final String INSTANCE_ID = "test-instance";
       private static final String TABLE_ID = "test-table";
       private static final String COLUMN_FAMILY_ID = "test-column-family";
       
       @ConfigProperty(name = "quarkus.google.cloud.project-id")
       String projectId;
       
       @PostConstruct
       void initBigtable() throws IOException {
           BigtableTableAdminClient adminClient = BigtableTableAdminClient.create(projectId, INSTANCE_ID);
           if (!adminClient.exists(TABLE_ID)) {
               System.out.println("Creating table: " + TABLE_ID);
               CreateTableRequest createTableRequest = CreateTableRequest.of(TABLE_ID).addFamily(COLUMN_FAMILY_ID);
               adminClient.createTable(createTableRequest);
               System.out.printf("Table %s created successfully%n", TABLE_ID);
           }        
       }
   
       @GET
       public String bigtable() throws IOException {
        BigtableDataClient dataClient = BigtableDataClient.create(projectId, instanceId);
        try {
            // create a row
            RowMutation rowMutation = RowMutation.create(tableId, "key1").setCell(columnFamily, "test", "value1");
            dataClient.mutateRow(rowMutation);

            Row row = dataClient.readRow(tableId, "key1");
            System.out.println("Row: " + row.getKey().toStringUtf8());
            StringBuilder cells = new StringBuilder();
            for (RowCell cell : row.getCells()) {
                cells.append(String.format(
                        "Family: %s    Qualifier: %s    Value: %s%n",
                        cell.getFamily(), cell.getQualifier().toStringUtf8(), cell.getValue().toStringUtf8()));
            }
            return cells.toString();
        } finally {
            dataClient.close();
        }
    }
}
```