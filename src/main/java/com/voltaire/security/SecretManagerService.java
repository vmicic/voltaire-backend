package com.voltaire.security;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1beta1.*;
import com.google.cloud.secretmanager.v1beta1.SecretManagerServiceClient.ListSecretsPagedResponse;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SecretManagerService {

    public static final String LATEST_VERSION = "latest";

    private final SecretManagerServiceClient secretManagerServiceClient;

    private final GcpProjectIdProvider gcpProjectIdProvider;

    public void createSecret(String secretId, String payload) {
        createNewSecretVersion(secretId, ByteString.copyFromUtf8(payload), gcpProjectIdProvider.getProjectId());
    }

    private void createNewSecretVersion(String secretId, ByteString payload, String projectId) {
        if (secretNotExists(secretId, projectId)) {
            createSecretInternal(secretId, projectId);
        }

        SecretName name = SecretName.of(projectId, secretId);
        AddSecretVersionRequest payloadRequest = AddSecretVersionRequest.newBuilder()
                .setParent(name.toString())
                .setPayload(SecretPayload.newBuilder().setData(payload))
                .build();

        secretManagerServiceClient.addSecretVersion(payloadRequest);
    }

    private boolean secretNotExists(String secretId, String projectId) {
        SecretName secretName = SecretName.of(projectId, secretId);
        try {
            this.secretManagerServiceClient.getSecret(secretName);
        } catch (NotFoundException ex) {
            return true;
        }

        return false;
    }

    private void createSecretInternal(String secretId, String projectId) {
        ProjectName projectName = ProjectName.of(projectId);

        Secret secret = Secret.newBuilder()
                .setReplication(
                        Replication.newBuilder().setAutomatic(
                                Replication.Automatic.getDefaultInstance()))
                .build();
        CreateSecretRequest request = CreateSecretRequest.newBuilder()
                .setParent(projectName.toString())
                .setSecretId(secretId)
                .setSecret(secret)
                .build();

        this.secretManagerServiceClient.createSecret(request);
    }

    public boolean invalidApiKey(String uuid) {
        ProjectName projectName = ProjectName.of(gcpProjectIdProvider.getProjectId());
        ListSecretsPagedResponse secretsPagedResponse = secretManagerServiceClient.listSecrets(projectName);

        Predicate<Secret> apiKeyExistsInSecret = secret -> {
            var secretShortName = secret.getName().substring(secret.getName().lastIndexOf("/") + 1);

            SecretVersionName secretVersionName = SecretVersionName.of(gcpProjectIdProvider.getProjectId(), secretShortName, LATEST_VERSION);
            AccessSecretVersionResponse response = secretManagerServiceClient.accessSecretVersion(secretVersionName);
            String payload = response.getPayload().getData().toStringUtf8();

            return payload.equals(uuid);
        };

        return StreamSupport.stream(secretsPagedResponse.iterateAll().spliterator(), false).noneMatch(apiKeyExistsInSecret);
    }
}
