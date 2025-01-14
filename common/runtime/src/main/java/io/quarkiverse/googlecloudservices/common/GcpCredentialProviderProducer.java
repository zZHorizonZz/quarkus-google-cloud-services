package io.quarkiverse.googlecloudservices.common;

import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

@Singleton
public class GcpCredentialProviderProducer {

    @Inject
    GoogleCredentials googleCredentials;

    @Produces
    @Singleton
    @Default
    public CredentialsProvider credentialsProvider() {
        return FixedCredentialsProvider.create(googleCredentials);
    }
}
